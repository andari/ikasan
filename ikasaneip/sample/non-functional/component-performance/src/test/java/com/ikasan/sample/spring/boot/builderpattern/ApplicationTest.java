/*
 * $Id: SchedulerFactoryTest.java 3629 2011-04-18 10:00:52Z mitcje $
 * $URL: http://open.jira.com/svn/IKASAN/branches/ikasaneip-0.9.x/scheduler/src/test/java/org/ikasan/scheduler/SchedulerFactoryTest.java $
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package com.ikasan.sample.spring.boot.builderpattern;

import org.apache.activemq.broker.BrokerService;
import org.h2.tools.Server;
import org.ikasan.nonfunctional.test.util.SimpleTimer;
import org.ikasan.nonfunctional.test.util.WiretapTestUtil;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.spec.wiretap.WiretapService;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.ikasan.spec.trigger.TriggerRelationship;
import org.ikasan.wiretap.listener.JobAwareFlowEventListener;
import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

/**
 * Stress testing of Ikasan persistence.
 *
 * Publish 10,000 events across the following flows.
 * - eventGeneratingProducer flow to create and publish 10,000 events to JMS with a single wiretap
 * - eventGeneratingDynamicConfiguration flow to create and publish 10,000 events to devNull with a dynamic configuration to DB
 * - Two JMS consumers with single wiretap each
 *
 * THe above results in all flows concurrently updating H2 transactionally.
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { com.ikasan.sample.spring.boot.builderpattern.Application.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest
{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private Module<Flow> moduleUnderTest;

    @Resource
    private WiretapService<WiretapEvent,PagedSearchResult> wiretapService;

    @Resource
    private JobAwareFlowEventListener jobAwareFlowEventListener;

    @Value("${jms.provider.url}")
    private String brokerUrl;

    public IkasanFlowTestRule splitterFlowTestRule = new IkasanFlowTestRule();

    // h2 server instance
    static Server server;

    // test utils
    WiretapTestUtil wiretapTestUtil;
    SimpleTimer stopWatch;

    // AMQ Broker
    BrokerService broker;

    // Arjuna transaction logs
    String objectStoreDir = "./ObjectStore";

    // AMQ persistence
    String amqPersistenceBaseDir = "./activemq-data";
    String amqPersistenceDir = amqPersistenceBaseDir + "/localhost/KahaDB";

    @BeforeClass
    public static void setup() throws SQLException
    {
        // TODO can we use a random port and tie back to the application.properties url?
        server = Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers","-ifNotExists").start();
    }

    @Before
    public void start() throws Exception
    {
        stopWatch = SimpleTimer.getInstance();
        wiretapTestUtil = new WiretapTestUtil(wiretapService, jobAwareFlowEventListener);
    }

    @AfterClass
    public static void teardown()
    {
        server.shutdown();
    }

    @Test
    @DirtiesContext
    public void test_splitter_stress() throws Exception
    {
        // event generator publishing to JMS topic
        splitterFlowTestRule.withFlow(moduleUnderTest.getFlow("splitter stress flow"));

        // Setup component expectations
        for (int i = 0; i < ModuleConfig.EVENT_GENERATOR_COUNT; i++)
        {
            splitterFlowTestRule.consumer("Event Generating Consumer")
                    .producer("Logging Producer");
        }

        wiretapTestUtil.addWiretapTrigger("Component Stress Test Module", "splitter stress flow",
                TriggerRelationship.BEFORE, "Logging Producer");

        // start flows right to left
        splitterFlowTestRule.startFlow();

        // wait for event generating flows to complete
        stopWatch.start();
        logger.info("Waiting for 'splitter stress flow' flow to complete (circa  seconds).");
        while (splitterFlowTestRule.getFlowState().equals(Flow.RUNNING))
        {
            // log if it takes longer than 70 seconds
            if(stopWatch.elapsedInSeconds() > 70)
            {
                logger.info("Still waiting for 'configurationUpdaterFlow' flow to complete. Waiting for " + stopWatch.elapsedInSeconds() + " seconds");
            }
            Thread.sleep(2000);
        }

        // wait for things to catch up
        int waitCounter = 0;
        PagedSearchResult<WiretapEvent> wiretaps = null;
        while( waitCounter < 10 &&
            (wiretaps = wiretapTestUtil.getWiretaps("Component Stress Test Module",
                "splitter stress flow",
                TriggerRelationship.BEFORE,
                "Logging Producer",
                ModuleConfig.EVENT_GENERATOR_COUNT)).getResultSize() != ModuleConfig.EVENT_GENERATOR_COUNT)
        {
            waitCounter++;
            logger.info("Waiting for splitter stress flow flow to complete (circa 10 seconds). Waiting for " + waitCounter + " seconds");
            Thread.sleep(2000);
        }

        splitterFlowTestRule.stopFlow();

        assertTrue("Expected " + "splitter stress flow" + " flow wiretap count "
                + ModuleConfig.EVENT_GENERATOR_COUNT + " but found " + wiretaps.getResultSize(), wiretaps.getResultSize() == ModuleConfig.EVENT_GENERATOR_COUNT );

    }
}


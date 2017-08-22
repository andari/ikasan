/*
 * $Id$
 * $URL$
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
package org.ikasan.sample.spring.boot.builderpattern;

import org.ikasan.builder.FlowBuilder;
import org.ikasan.builder.IkasanApplication;
import org.ikasan.builder.IkasanApplicationFactory;
import org.ikasan.builder.ModuleBuilder;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;

import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Sample standalone bootstrap application using the builder pattern.
 *
 * @author Ikasan Development Team
 */
public class MyApplication
{
    public static void main(String[] args) throws Exception
    {
        MyApplication myApplication = new MyApplication();
        IkasanApplication ikasanApplication = IkasanApplicationFactory.getIkasanApplication(args);

        ModuleBuilder moduleBuilder = ikasanApplication.getModuleBuilder("moduleName");
        Flow flow = myApplication.getFlow(moduleBuilder);
        moduleBuilder.addFlow(flow);
        Module module = moduleBuilder.build();

        ikasanApplication.run(module);

       System.out.println("Let's inspect the beans provided by Spring Boot:");

    }

    public Flow getFlow(ModuleBuilder moduleBuilder)
    {
        FlowBuilder flowBuilder = moduleBuilder.getFlowBuilder("flowName");
         return flowBuilder.withDescription("flowDescription")
                .consumer("consumer", new MyConsumer())
                .producer("producer", new MyProducer()).build();

    }

    private class MyConsumer implements Consumer,MessageListener
    {

        private boolean isRunning;
        @Override
        public void setListener(Object o) {

        }

        @Override
        public void setEventFactory(Object o) {

        }

        @Override
        public Object getEventFactory() {
            return null;
        }

        @Override
        public void start()
        {
            this.isRunning = true;
        }

        @Override
        public boolean isRunning() {
            return isRunning;
        }

        @Override
        public void stop() {
            this.isRunning = false;
        }

        @Override public void onMessage(Message message)
        {
            System.out.print("Message");
        }
    }

    private class MyProducer implements Producer
    {

        @Override
        public void invoke(Object payload) throws EndpointException {

        }
    }
}
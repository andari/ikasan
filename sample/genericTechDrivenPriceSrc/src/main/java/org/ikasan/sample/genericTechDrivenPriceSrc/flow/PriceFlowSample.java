/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 *
 * Copyright (c) 2000-20010 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.sample.genericTechDrivenPriceSrc.flow;

import org.ikasan.flow.configuration.dao.ConfigurationDao;
import org.ikasan.flow.configuration.dao.ConfigurationHibernateImpl;
import org.ikasan.flow.configuration.service.ConfigurationService;
import org.ikasan.flow.configuration.service.ConfiguredResourceConfigurationService;
import org.ikasan.flow.event.FlowEventFactory;
import org.ikasan.flow.visitorPattern.DefaultFlowConfiguration;
import org.ikasan.flow.visitorPattern.FlowConfiguration;
import org.ikasan.flow.visitorPattern.FlowElementImpl;
import org.ikasan.flow.visitorPattern.VisitingFlowElementInvoker;
import org.ikasan.flow.visitorPattern.VisitingInvokerFlow;
import org.ikasan.recovery.ScheduledRecoveryManagerFactory;
import org.ikasan.sample.genericTechDrivenPriceSrc.component.converter.PriceConverter;
import org.ikasan.sample.genericTechDrivenPriceSrc.component.endpoint.PriceConsumer;
import org.ikasan.sample.genericTechDrivenPriceSrc.component.endpoint.PriceProducer;
import org.ikasan.sample.genericTechDrivenPriceSrc.tech.PriceTechImpl;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvoker;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.recovery.RecoveryManager;
import org.junit.Before;
import org.junit.Test;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Pure Java based sample of Ikasan EIP for sourcing prices from a tech endpoint.
 * 
 * @author Ikasan Development Team
 */
public class PriceFlowFactory
{
    FlowEventFactory flowEventFactory = new FlowEventFactory();
    ScheduledRecoveryManagerFactory scheduledRecoveryManagerFactory;
    
    protected PriceTechImpl getTechImpl()
    {
        return new PriceTechImpl();
    }
    
    protected EventFactory<FlowEvent<?,?>> getEventFactory()
    {
        return new FlowEventFactory();
    }
    
    protected ConfigurationService getConfigurationService()
    {
        ConfigurationDao configurationDao = new ConfigurationHibernateImpl();
        return new ConfiguredResourceConfigurationService(configurationDao, configurationDao);
    }
    
    @Before
    public void setup() throws SchedulerException
    {
        this.scheduledRecoveryManagerFactory  = 
            new ScheduledRecoveryManagerFactory(StdSchedulerFactory.getDefaultScheduler());
    }

    @Test
    public void test_flow_consumer_translator_producer() throws SchedulerException
    {
        Producer producer = new PriceProducer();
        FlowElement producerFlowElement = new FlowElementImpl("priceProducer", producer);

        Converter priceToStringBuilderConverter = new PriceConverter();
        FlowElement<Converter> converterFlowElement = new FlowElementImpl("priceToStringBuilder", priceToStringBuilderConverter, producerFlowElement);

        Consumer consumer = new PriceConsumer(getTechImpl(), getEventFactory());
        FlowElement<Consumer> consumerFlowElement = new FlowElementImpl("priceConsumer", consumer, converterFlowElement);

        // flow configuration wiring
        FlowConfiguration flowConfiguration = new DefaultFlowConfiguration(consumerFlowElement, getConfigurationService());

        // iterator over each flow element
        FlowElementInvoker flowElementInvoker = new VisitingFlowElementInvoker();

        RecoveryManager recoveryManager = scheduledRecoveryManagerFactory.getRecoveryManager("flowName", "moduleName", consumer);
        
        // container for the complete flow
        Flow flow = new VisitingInvokerFlow("flowName", "moduleName", 
            flowConfiguration, flowElementInvoker, recoveryManager);
        
        flow.start();

        flow.stop();
    }

}

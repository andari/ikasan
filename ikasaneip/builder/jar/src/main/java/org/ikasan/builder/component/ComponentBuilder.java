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
package org.ikasan.builder.component;

import org.ikasan.builder.component.endpoint.*;
import org.ikasan.builder.component.filter.MessageFilterBuilder;
import org.ikasan.builder.component.filter.MessageFilterBuilderImpl;
import org.ikasan.builder.component.splitting.ListSplitterBuilderImpl;
import org.ikasan.builder.AopProxyProvider;
import org.ikasan.component.endpoint.filesystem.messageprovider.FileMessageProvider;
import org.ikasan.component.endpoint.filesystem.producer.FileProducerConfiguration;
import org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer;
import org.ikasan.component.endpoint.jms.spring.producer.JmsTemplateProducer;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.component.splitter.DefaultListSplitter;
import org.ikasan.connector.base.command.TransactionalResourceCommandDAO;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;
import org.ikasan.connector.util.chunking.model.dao.FileChunkDao;
import org.ikasan.filter.duplicate.service.DuplicateFilterService;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.quartz.Scheduler;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.IkasanJmsTemplate;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.transaction.TransactionManager;

/**
 * A simple Component builder.
 * 
 * @author Ikasan Development Team
 */
public class ComponentBuilder
{
    /** handle to spring context */
    ApplicationContext applicationContext;

    public ComponentBuilder(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
        if(applicationContext == null)
        {
            throw new IllegalArgumentException("applicationContext cannot be 'null'");
        }
    }

    /**
     * Get an instance of an Ikasan default scheduledConsumer
     * @return scheduledConsumerBuilder
     */
    public ScheduledConsumerBuilder scheduledConsumer()
    {
        ScheduledConsumer scheduledConsumer = new org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer( this.applicationContext.getBean(Scheduler.class) );
        ScheduledConsumerBuilder scheduledConsumerBuilder = new ScheduledConsumerBuilderImpl(scheduledConsumer,
                this.applicationContext.getBean(ScheduledJobFactory.class), this.applicationContext.getBean(AopProxyProvider.class));
        return scheduledConsumerBuilder;
    }

    /**
     * Get an instance of an Ikasan default scheduledConsumer with SFTP message Provider
     * @return sftpConsumerBuilder
     */
    public SftpConsumerBuilder sftpConsumer()
    {
        ScheduledConsumer scheduledConsumer = new org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer( this.applicationContext.getBean(Scheduler.class) );
        SftpConsumerBuilder sftpConsumerBuilder = new SftpConsumerBuilderImpl(scheduledConsumer,
                this.applicationContext.getBean(ScheduledJobFactory.class), this.applicationContext.getBean(AopProxyProvider.class)
                ,this.applicationContext.getBean(JtaTransactionManager.class)
                ,this.applicationContext.getBean(BaseFileTransferDao.class)
                ,this.applicationContext.getBean(FileChunkDao.class)
                ,this.applicationContext.getBean(TransactionalResourceCommandDAO.class)

        );
        return sftpConsumerBuilder;
    }

    /**
     * Get an instance of an Ikasan default SFTP Producer
     * @return sftpProducerBuilder
     */
    public SftpProducerBuilder sftpProducer()
    {

        SftpProducerBuilder sftpProducerBuilder = new SftpProducerBuilderImpl(
                this.applicationContext.getBean(JtaTransactionManager.class)
                ,this.applicationContext.getBean(BaseFileTransferDao.class)
                ,this.applicationContext.getBean(FileChunkDao.class)
                ,this.applicationContext.getBean(TransactionalResourceCommandDAO.class)

        );
        return sftpProducerBuilder;
    }

    /**
     * Get an instance of an Ikasan default scheduledConsumer with FTP message Provider
     * @return FtpConsumerBuilder
     */
    public FtpConsumerBuilder ftpConsumer()
    {
        ScheduledConsumer scheduledConsumer = new org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer( this.applicationContext.getBean(Scheduler.class) );
        FtpConsumerBuilder ftpConsumerBuilder = new FtpConsumerBuilderImpl(scheduledConsumer,
                this.applicationContext.getBean(ScheduledJobFactory.class), this.applicationContext.getBean(AopProxyProvider.class)
                ,this.applicationContext.getBean(JtaTransactionManager.class)
                ,this.applicationContext.getBean(BaseFileTransferDao.class)
                ,this.applicationContext.getBean(FileChunkDao.class)
                ,this.applicationContext.getBean(TransactionalResourceCommandDAO.class)

        );
        return ftpConsumerBuilder;
    }

    /**
     * Get an instance of an Ikasan default FTP Producer
     * @return ftpProducerBuilder
     */
    public FtpProducerBuilder ftpProducer()
    {

        FtpProducerBuilder ftpProducerBuilder = new FtpProducerBuilderImpl(
                this.applicationContext.getBean(JtaTransactionManager.class)
                ,this.applicationContext.getBean(BaseFileTransferDao.class)
                ,this.applicationContext.getBean(FileChunkDao.class)
                ,this.applicationContext.getBean(TransactionalResourceCommandDAO.class)

        );
        return ftpProducerBuilder;
    }

    /**
     * Get an instance of an Ikasan default fileConsumer
     * @return scheduledConsumerBuilder
     */
    public FileConsumerBuilder fileConsumer()
    {
        ScheduledConsumer scheduledConsumer = new org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer( this.applicationContext.getBean(Scheduler.class) );
        FileConsumerBuilder fileConsumerBuilder = new FileConsumerBuilderImpl(scheduledConsumer,
                this.applicationContext.getBean(ScheduledJobFactory.class), this.applicationContext.getBean(AopProxyProvider.class), new FileMessageProvider() );
        return fileConsumerBuilder;
    }

    /**
     * Get an instance of an Ikasan default fileProducer
     * @return FileProducerBuilder
     */
    public FileProducerBuilder fileProducer()
    {
        return new FileProducerBuilderImpl();
    }

    /**
     * Get an instance of an Ikasan default jmsConsumer
     * @return jmsConsumerBuilder
     */
    public JmsConsumerBuilder jmsConsumer() {
        JmsContainerConsumer jmsConsumer = new JmsContainerConsumer();
        JmsConsumerBuilder jmsConsumerBuilder = new JmsConsumerBuilderImpl(jmsConsumer,
                this.applicationContext.getBean(JtaTransactionManager.class), this.applicationContext.getBean(TransactionManager.class),this.applicationContext.getBean(AopProxyProvider.class));
        return jmsConsumerBuilder;
    }

    /**
     * Get an instance of an Ikasan default jmsProducer
     * @return jmsProducerBuilder
     */
    public JmsProducerBuilder jmsProducer() {
        JmsTemplateProducer jmsTemplateProducer = new JmsTemplateProducer(new IkasanJmsTemplate());
        JmsProducerBuilder jmsProducerBuilder = new JmsProducerBuilderImpl(jmsTemplateProducer);
        return jmsProducerBuilder;
    }



    public Builder<Splitter> listSplitter()
    {
        return new ListSplitterBuilderImpl( new DefaultListSplitter() );
    }

    public MessageFilterBuilder messageFilter()
    {
       return new MessageFilterBuilderImpl(this.applicationContext.getBean(DuplicateFilterService.class));
    }

}




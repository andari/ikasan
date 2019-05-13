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
package org.ikasan.builder.component.endpoint;

import org.ikasan.builder.AopProxyProvider;
import org.ikasan.builder.component.RequiresAopProxy;
import org.ikasan.component.endpoint.db.messageprovider.DbConsumerConfiguration;
import org.ikasan.component.endpoint.filesystem.messageprovider.FileConsumerConfiguration;
import org.ikasan.component.endpoint.quartz.consumer.MessageProvider;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.ManagedEventIdentifierService;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;

/**
 * Ikasan provided local file consumer default implementation.
 *
 * This implementation allows for proxying of the object to facilitate transaction pointing.
 *
 * @author Ikasan Development Team
 */
public class DbConsumerBuilderImpl extends ScheduledConsumerBuilderImpl
        implements DbConsumerBuilder, RequiresAopProxy
{
    MessageProvider<?> messageProvider;

    /**
     * Constructor
     * @param scheduledConsumer
     */
    public DbConsumerBuilderImpl(ScheduledConsumer scheduledConsumer, ScheduledJobFactory scheduledJobFactory,
                                 AopProxyProvider aopProxyProvider, MessageProvider messageProvider)
    {
        super(scheduledConsumer, scheduledJobFactory, aopProxyProvider);
        this.messageProvider = messageProvider;
    }

    /**
     * Is this successful start of this component critical on flow start.
     * If it can recover post flow start up then its not critical.
     * @param criticalOnStartup
     * @return
     */
    public DbConsumerBuilder setCriticalOnStartup(boolean criticalOnStartup)
    {
        this.scheduledConsumer.setCriticalOnStartup(criticalOnStartup);
        return this;
    }

    /**
     * ConfigurationService identifier for this component configuration.
     * @param configuredResourceId
     * @return
     */
    public DbConsumerBuilder setConfiguredResourceId(String configuredResourceId)
    {
        this.scheduledConsumer.setConfiguredResourceId(configuredResourceId);
        return this;
    }

    @Override
    public DbConsumerBuilder setConfiguration(FileConsumerConfiguration fileConsumerConfiguration)
    {
        this.scheduledConsumer.setConfiguration(fileConsumerConfiguration);
        return this;
    }

    /**
     * Underlying tech providing the message event
     * @param messageProvider
     * @return
     */
    public DbConsumerBuilder setMessageProvider(MessageProvider messageProvider)
    {
        this.scheduledConsumer.setMessageProvider(messageProvider);
        return this;
    }

    /**
     * Implementation of the managed event identifier service - sets the life identifier based on the incoming event.
     * @param managedEventIdentifierService
     * @return
     */
    public DbConsumerBuilder setManagedEventIdentifierService(ManagedEventIdentifierService managedEventIdentifierService)
    {
        this.scheduledConsumer.setManagedEventIdentifierService(managedEventIdentifierService);
        return this;
    }

    /**
     * Give the component a handle directly to the recovery manager
     * @param managedResourceRecoveryManager
     * @return
     */
    public DbConsumerBuilder setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager)
    {
        this.scheduledConsumer.setManagedResourceRecoveryManager(managedResourceRecoveryManager);
        return this;
    }

    /**
     * Override default event factory
     * @param eventFactory
     * @return
     */
    public DbConsumerBuilder setEventFactory(EventFactory eventFactory) {
        this.scheduledConsumer.setEventFactory(eventFactory);
        return this;
    }

    /**
     * Scheduled consumer cron expression
     * @param cronExpression
     * @return
     */
    public DbConsumerBuilder setCronExpression(String cronExpression)
    {
        getConfiguration().setCronExpression(cronExpression);
        return this;
    }

    /**
     * When true the scheduled consumer is immediately called back on completion of flow execution.
     * If false the scheduled consumers cron expression determines the callback.
     * @param eager
     * @return
     */
    public DbConsumerBuilder setEager(boolean eager)
    {
        getConfiguration().setEager(eager);
        return this;
    }

    /**
     * Whether to ignore call back failures.
     * @param ignoreMisfire
     * @return
     */
    public DbConsumerBuilder setIgnoreMisfire(boolean ignoreMisfire) {
        getConfiguration().setIgnoreMisfire(ignoreMisfire);
        return this;
    }

    /**
     * Specifically set the timezone of the scheduled callback.
     * @param timezone
     * @return
     */
    public DbConsumerBuilder setTimezone(String timezone) {
        getConfiguration().setTimezone(timezone);
        return this;
    }

    @Override
    public DbConsumerBuilder setScheduledJobGroupName(String scheduledJobGroupName) {
        this.scheduledJobGroupName = scheduledJobGroupName;
        return this;
    }

    @Override
    public DbConsumerBuilder setScheduledJobName(String scheduledJobName) {
        this.scheduledJobName = scheduledJobName;
        return this;
    }

    @Override
    public DbConsumerBuilder setDriver(String driver)
    {
        getConfiguration().setDriver(driver);
        return this;
    }

    @Override
    public DbConsumerBuilder setUrl(String url)
    {
        getConfiguration().setUrl(url);
        return this;
    }

    @Override
    public DbConsumerBuilder setUsername(String username)
    {
        getConfiguration().setUsername(username);
        return this;
    }

    @Override
    public DbConsumerBuilder setPassword(String password)
    {
        getConfiguration().setPassword(password);
        return this;
    }

    @Override
    public DbConsumerBuilder setSqlStatement(String sqlStatement)
    {
        getConfiguration().setSqlStatement(sqlStatement);
        return this;
    }

    private DbConsumerConfiguration getConfiguration()
    {
        DbConsumerConfiguration scheduledDbConsumerConfiguration = (DbConsumerConfiguration)this.scheduledConsumer.getConfiguration();
        if(scheduledDbConsumerConfiguration == null)
        {
            scheduledDbConsumerConfiguration = new DbConsumerConfiguration();
            this.scheduledConsumer.setConfiguration(scheduledDbConsumerConfiguration);
        }

        return scheduledDbConsumerConfiguration;
    }

    /**
     * Configure the raw component based on the properties passed to the builder, configure it
     * ready for use and return the instance.
     * @return
     */
    public ScheduledConsumer build()
    {
        this.scheduledConsumer.setMessageProvider(this.messageProvider);

        // it maybe no configuration properties are set by the developer, so call getConfiguration to ensure one exists
        getConfiguration();

        return super.build();
    }

}

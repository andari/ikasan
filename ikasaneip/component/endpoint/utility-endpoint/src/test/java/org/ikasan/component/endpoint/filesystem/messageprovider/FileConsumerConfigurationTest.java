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
package org.ikasan.component.endpoint.filesystem.messageprovider;

import org.ikasan.spec.configuration.Configured;
import org.ikasan.spec.configuration.InvalidConfigurationException;
import org.ikasan.spec.configuration.IsValidationAware;
import org.jmock.Expectations;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Functional unit test cases for <code>FileConsumerConfiguration</code>.
 * 
 * @author Ikasan Development Team
 */
public class FileConsumerConfigurationTest
{
    /**
     * Test to ensure the configuration is validation aware.
     *
     **/
    @Test
    public void test_ftpConfiguration_isValidationAware() throws InvalidConfigurationException
    {
        Assert.assertTrue("Configuration doesnt implement IsValidationAware", new FileConsumerConfiguration() instanceof IsValidationAware);
    }

    /**
     * Test.
     */
    @Test
    public void test_valid_configuration()
    {
        FileConsumerConfiguration fileConsumerConfiguration = new FileConsumerConfiguration();
        fileConsumerConfiguration.setCronExpression("0/5 * * * * ?");

        List<String> filenames = new ArrayList<String>();
        filenames.add("one.txt");
        fileConsumerConfiguration.setFilenames(filenames);

        fileConsumerConfiguration.validate();
    }

    /**
     * Test.
     */
    @Test
    public void test_invalid_configuration_invalid_cronExpression()
    {
        FileConsumerConfiguration fileConsumerConfiguration = new FileConsumerConfiguration();

        try
        {
            fileConsumerConfiguration.validate();
            Assert.fail("configuration is not valid");
        }
        catch(InvalidConfigurationException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Test.
     */
    @Test
    public void test_invalid_configuration_invalid_filenames()
    {
        FileConsumerConfiguration fileConsumerConfiguration = new FileConsumerConfiguration();
        fileConsumerConfiguration.setCronExpression("0/5 * * * * ?");

        try
        {
            fileConsumerConfiguration.validate();
            Assert.fail("configuration is not valid");
        }
        catch(InvalidConfigurationException e)
        {
            e.printStackTrace();
        }
    }
}
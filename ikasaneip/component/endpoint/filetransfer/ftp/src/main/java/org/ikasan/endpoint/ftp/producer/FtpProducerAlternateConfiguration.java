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
package org.ikasan.endpoint.ftp.producer;

import org.ikasan.spec.configuration.Masked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FTP Producer Configuration window providing alternate connection details
 * to be used in case primary connection fails
 * 
 * @author Ikasan Development Team
 */
public class FtpProducerAlternateConfiguration extends FtpProducerConfiguration
{
    /** Whether it is active transfer mode - default False */
    private Boolean alternateActive = Boolean.FALSE;

    /** FTP default Remote host */
    private String alternateRemoteHost = String.valueOf("localhost");

    /** FTP max retry attempts */
    private Integer alternateMaxRetryAttempts = Integer.valueOf(3);

    /** FTP default remote port */
    private Integer alternateRemotePort = Integer.valueOf(21);

    /** FTP user */
    private String alternateUsername;

    /** FTP password/passphrase */
    @Masked
    private String alternatePassword;

    /** Connection Timeout */
    private Integer alternateConnectionTimeout = Integer.valueOf(60000);

    /** Data connection timeout, default is 0 (infinite) */
    private Integer alternateDataTimeout = Integer.valueOf(300000);
    
    /** Socket connection timeout, default is 0 (infinite) */
    private Integer alternateSocketTimeout = Integer.valueOf(300000);

    /** System key */
    private String alternateSystemKey = "";

    /** Logger instance */
    private static final Logger logger = LoggerFactory.getLogger(FtpProducerAlternateConfiguration.class);

    /**
     * @return the alternateActive
     */
    public Boolean getAlternateActive()
    {
        return this.alternateActive;
    }

    /**
     * @param alternateActive the alternateActive to set
     */
    public void setAlternateActive(Boolean alternateActive)
    {
        this.alternateActive = alternateActive;
    }

    /**
     * @return the alternateRemoteHost
     */
    public String getAlternateRemoteHost()
    {
        return this.alternateRemoteHost;
    }

    /**
     * @param alternateRemoteHost the alternateRemoteHost to set
     */
    public void setAlternateRemoteHost(String alternateRemoteHost)
    {
        this.alternateRemoteHost = alternateRemoteHost;
    }

    /**
     * @return the alternateMaxRetryAttempts
     */
    public Integer getAlternateMaxRetryAttempts()
    {
        return this.alternateMaxRetryAttempts;
    }

    /**
     * @param alternateMaxRetryAttempts the alternateMaxRetryAttempts to set
     */
    public void setAlternateMaxRetryAttempts(Integer alternateMaxRetryAttempts)
    {
        this.alternateMaxRetryAttempts = alternateMaxRetryAttempts;
    }

    /**
     * @return the alternateRemotePort
     */
    public Integer getAlternateRemotePort()
    {
        return this.alternateRemotePort;
    }

    /**
     * @param alternateRemotePort the alternateRemotePort to set
     */
    public void setAlternateRemotePort(Integer alternateRemotePort)
    {
        this.alternateRemotePort = alternateRemotePort;
    }

    /**
     * @return the alternateUsername
     */
    public String getAlternateUsername()
    {
        return this.alternateUsername;
    }

    /**
     * @param alternateUsername the alternateUsername to set
     */
    public void setAlternateUsername(String alternateUsername)
    {
        this.alternateUsername = alternateUsername;
    }

    /**
     * @return the alternatePassword
     */
    public String getAlternatePassword()
    {
        return this.alternatePassword;
    }

    /**
     * @param alternatePassword the alternatePassword to set
     */
    public void setAlternatePassword(String alternatePassword)
    {
        this.alternatePassword = alternatePassword;
    }

    /**
     * @return the alternateConnectionTimeout
     */
    public Integer getAlternateConnectionTimeout()
    {
        return this.alternateConnectionTimeout;
    }

    /**
     * @param alternateConnectionTimeout the alternateConnectionTimeout to set
     */
    public void setAlternateConnectionTimeout(Integer alternateConnectionTimeout)
    {
        this.alternateConnectionTimeout = alternateConnectionTimeout;
    }

    /**
     * @return the alternateDataTimeout
     */
    public Integer getAlternateDataTimeout()
    {
        return this.alternateDataTimeout;
    }

    /**
     * @param alternateDataTimeout the alternateDataTimeout to set
     */
    public void setAlternateDataTimeout(Integer alternateDataTimeout)
    {
        this.alternateDataTimeout = alternateDataTimeout;
    }

    /**
     * @return the alternateSocketTimeout
     */
    public Integer getAlternateSocketTimeout()
    {
        return this.alternateSocketTimeout;
    }

    /**
     * @param alternateSocketTimeout the alternateSocketTimeout to set
     */
    public void setAlternateSocketTimeout(Integer alternateSocketTimeout)
    {
        this.alternateSocketTimeout = alternateSocketTimeout;
    }

    /**
     * @return the alternateSystemKey
     */
    public String getAlternateSystemKey()
    {
        return this.alternateSystemKey;
    }

    /**
     * @param alternateSystemKey the alternateSystemKey to set
     */
    public void setAlternateSystemKey(String alternateSystemKey)
    {
        this.alternateSystemKey = alternateSystemKey;
    }

    @Override
    /**
     * Validating configuration parameters
     */
    public void validate()
    {
        super.validate();
        if (this.getAlternateSystemKey() == null || this.getAlternateSystemKey().equals(" "))
        {
            logger.debug("Provided systemKey value [" + this.getAlternateSystemKey() + "] is invalid. Reverting to default empty String.");
            this.setAlternateSystemKey("");
        }
    }
}

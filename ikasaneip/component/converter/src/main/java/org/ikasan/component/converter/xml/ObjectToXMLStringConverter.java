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
package org.ikasan.component.converter.xml;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.oxm.support.SaxResourceUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Marshall the incoming Object into an XML String.
 * 
 * @author Ikasan Development Team
 */
public class ObjectToXMLStringConverter implements Converter<Object, Object>, ConfiguredResource<XmlConfiguration>
{
    /** class logger */
    private static Logger logger = Logger.getLogger(ObjectToXMLStringConverter.class);

    /** JAXB Context is thread-safe so keep handle to the context */
    private JAXBContext context;
 
    /** configuration id */
    private String configuredResourceId;
    
    /** configuration */
    private XmlConfiguration xmlConfiguration;

    /** schema */
    private Schema schema;

    /** whether to route or throw exception on validation failure */
    private boolean routeOnValidationException;

    /** list of potential classes for this context */
    private List<Class> classes;

    /** overriding root class */
    private Class rootClass;

    /** QNAME of the root element */
    private QName rootQName;

    /**
     * Constructor
     * @param classes
     */
    public ObjectToXMLStringConverter(List<Class> classes)
    {
        try
        {
            this.classes = classes;
            if(classes == null)
            {
                throw new IllegalArgumentException("classes cannot be 'null'");
            }

            this.context = JAXBContext.newInstance(classes.toArray(new Class[classes.size()]));
        }
        catch(JAXBException e)
        {
            throw new IllegalArgumentException("Failed to create JAXBContext with classes[" + classes + "]", e);
        }
    }

    /**
     * Constructor
     * @param cls
     */
    public ObjectToXMLStringConverter(Class cls)
    {
        try
        {
            if(cls == null)
            {
                throw new IllegalArgumentException("class cannot be 'null'");
            }

            this.classes = new ArrayList<Class>();
            this.classes.add(cls);
            this.context = JAXBContext.newInstance(cls);
        }
        catch(JAXBException e)
        {
            throw new IllegalArgumentException("Failed to create JAXBContext with class[" + cls + "]", e);
        }
    }

    /* (non-Javadoc)
     * @see org.ikasan.spec.component.transformation.Converter#convert(java.lang.Object)
     */
    @Override
    public Object convert(Object object) throws TransformationException
    {
        try
        {
            Marshaller marshaller = this.context.createMarshaller();
            StringWriter writer = new StringWriter();

            if(rootQName != null)
            {
                object = new JAXBElement(rootQName, rootClass, object);
            }

            if(this.xmlConfiguration.getSchemaLocation() != null)
            {
                if(this.xmlConfiguration.isNoNamespaceSchema())
                {
                    marshaller.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, this.xmlConfiguration.getSchemaLocation());
                }
                else
                {
                    marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, this.xmlConfiguration.getSchemaLocation());
                }
            }

            if(this.xmlConfiguration.isValidate())
            {
                marshaller.setSchema(this.schema);
            }
            else
            {
                marshaller.setSchema(null);
            }

            marshaller.setEventHandler(new XmlValidationEventHandler());
            marshaller.marshal(object, writer);
            String marshalledXml = writer.toString();
            if(logger.isDebugEnabled())
            {
                logger.debug("Marshalled XML [" + marshalledXml + "]");
            }

            return marshalledXml;
        }
        catch (XmlValidationException e)
        {
            String failedXml = getFailedMessageXml(object);
            e.setFailedEvent(failedXml);
            if(this.routeOnValidationException)
            {
                logger.info("Failed XML Validation on[" + failedXml + "]", e);
                return e;
            }

            throw new TransformationException("Failed XML Validation on[" + failedXml + "]", e);
        }
        catch (JAXBException e)
        {
            String failedXml = getFailedMessageXml(object);
            if(e.getLinkedException() instanceof XmlValidationException)
            {
                XmlValidationException validationException = (XmlValidationException) e.getLinkedException();
                validationException.setFailedEvent(failedXml);

                if(this.routeOnValidationException)
                {
                    logger.info("Failed XML Validation on[" + failedXml + "]", e);
                    return validationException;
                }
            }
            
            throw new TransformationException("Failed XML Validation on[" + failedXml + "]", e);
        }
    }

    /**
     * Load the XSD and return the Source. If not specified then return null.
     * @param schema
     * @return
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     */
    private Schema loadSchema(String schema) throws IOException, SAXException
    {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        if(schema == null)
        {
            return schemaFactory.newSchema();
        }

        InputSource inputSource = getResource(new ClassPathResource(schema));
        if(inputSource == null)
        {
            inputSource = getResource(new UrlResource(schema));
            if(inputSource == null)
            {
                inputSource = getResource(new FileSystemResource(schema));
                if(inputSource == null)
                {
                    throw new IOException("Unable to load " + schema);
                }
            }
        }

        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", this.xmlConfiguration.isUseNamespacePrefix());
        Source schemaSource = new SAXSource(xmlReader, inputSource);
        return schemaFactory.newSchema(schemaSource);
    }

    protected InputSource getResource(Resource resource)
    {
        try
        {
            return SaxResourceUtils.createInputSource(resource);
        }
        catch(IOException e)
        {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.ikasan.spec.configuration.ConfiguredResource#getConfiguration()
     */
    @Override
    public XmlConfiguration getConfiguration()
    {
        return this.xmlConfiguration;
    }

    /* (non-Javadoc)
     * @see org.ikasan.spec.configuration.ConfiguredResource#getConfiguredResourceId()
     */
    @Override
    public String getConfiguredResourceId()
    {
        return this.configuredResourceId;
    }

    /* (non-Javadoc)
     * @see org.ikasan.spec.configuration.ConfiguredResource#setConfiguration(java.lang.Object)
     */
    @Override
    public void setConfiguration(XmlConfiguration xmlConfiguration)
    {
        this.rootClass = null;
        this.rootQName = null;
        this.xmlConfiguration = xmlConfiguration;
        this.routeOnValidationException = this.xmlConfiguration.isRouteOnValidationException();

        // do we need to override the root name
        if(this.xmlConfiguration.getRootName() != null)
        {
            rootQName = new QName(this.xmlConfiguration.getRootName());

            // do we have a rootname class
            if(this.xmlConfiguration.getRootClassName() != null)
            {
                try
                {
                    rootClass = Class.forName(this.xmlConfiguration.getRootClassName());
                }
                catch (ClassNotFoundException e)
                {
                    throw new RuntimeException(e);
                }
            }
            else
            {
                // try guessing
                for(Class cls:this.classes)
                {
                    if( cls.getName().endsWith(this.xmlConfiguration.getRootName()) )
                    {
                        if(rootClass != null)
                        {
                            throw new RuntimeException("Too many matches for root class. Specifically override the rootClassName on the configuration.");
                        }

                        rootClass = cls;
                    }
                }

                if(rootClass == null)
                {
                    throw new RuntimeException("rootClass is 'null'. Specifically override the rootClassName on the configuration.");
                }
            }
        }
        else
        {
            // no root name, but if we have root class name then try to guess root name
            if(this.xmlConfiguration.getRootClassName() != null)
            {
                try
                {
                    rootClass = Class.forName(this.xmlConfiguration.getRootClassName());
                }
                catch (ClassNotFoundException e)
                {
                    throw new RuntimeException(e);
                }

                int period = this.xmlConfiguration.getRootClassName().lastIndexOf(".");
                if(period <= 0)
                {
                    rootQName = new QName( this.xmlConfiguration.getRootClassName() );
                }
                else
                {
                    rootQName = new QName( this.xmlConfiguration.getRootClassName().substring(period + 1) );
                }
            }
        }

        // try to load the schema
        try
        {
            this.schema = this.loadSchema(this.xmlConfiguration.getSchema());
        }
        catch(SAXException e)
        {
            throw new RuntimeException(e);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.ikasan.spec.configuration.ConfiguredResource#setConfiguredResourceId(java.lang.String)
     */
    @Override
    public void setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
    }

    private String getFailedMessageXml(Object object)
    {
        try
        {
            Marshaller marshaller = this.context.createMarshaller();
            StringWriter writer = new StringWriter();
            marshaller.setEventHandler(new XmlValidationEventHandler());
            marshaller.marshal(object, writer);
            return writer.toString();
        }
        catch(XmlValidationException e)
        {
            return null;
        }
        catch(JAXBException e)
        {
            return null;
        }
    }
    


}

/*
 * Copyright 2009 Aleksandar Seovic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seovic.core.io;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;


/**
 * JAXB-based marshaller that marshals object to/from XML.
 *
 * @author Aleksandar Seovic  2012.04.09
 */
public class JAXBMarshaller {
    // ---- constructors ----------------------------------------------------

    /**
     * Construct an JAXBMarshaller instance.
     *
     * @param clzRoot class of the root object this marshaller is for
     */
    public JAXBMarshaller(Class clzRoot) {
        this(clzRoot, false);
    }

    /**
     * Construct an JAXBMarshaller instance.
     *
     * @param clzRoot       class of the root object this marshaller is for
     * @param formatOutput  whether to format output for human readability
     */
    public JAXBMarshaller(Class clzRoot, boolean formatOutput) {
        m_clzRoot      = clzRoot;
        m_formatOutput = formatOutput;
        try {
            m_ctx = JAXBContext.newInstance(clzRoot);
        }
        catch (JAXBException e) {
            throw new IllegalArgumentException(
                    "error creating JAXB context for class \""
                    + clzRoot.getName() + "\"", e);
        }
    }

    // ---- Marshaller implementation ---------------------------------------

    /**
     * {@inheritDoc}
     */
    public void marshal(Object oValue, OutputStream out)
            throws IOException {
        try {
            javax.xml.bind.Marshaller marshaller = m_ctx.createMarshaller();
            configureJaxbMarshaller(marshaller);
            marshaller.marshal(oValue, out);
        }
        catch (JAXBException e) {
            throw new IOException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void marshalAsFragment(Object oValue, OutputStream out)
            throws IOException {
        try {
            javax.xml.bind.Marshaller marshaller = m_ctx.createMarshaller();
            configureJaxbMarshaller(marshaller);
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FRAGMENT, true);
            marshaller.marshal(oValue, out);
        }
        catch (JAXBException e) {
            throw new IOException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal(InputStream in)
            throws IOException {
        try {
            Unmarshaller unmarshaller = m_ctx.createUnmarshaller();
            configureJaxbUnmarshaller(unmarshaller);
            return unmarshaller.unmarshal(new StreamSource(in), m_clzRoot).getValue();
        }
        catch (JAXBException e) {
            throw new IOException(e);
        }
    }

    // ---- helpers ---------------------------------------------------------

    /**
     * Configure a JAXB marshaller.
     *
     * @param marshaller marshaller to configure
     *
     * @throws PropertyException when there is an error processing a property or
     *                           value
     */
    protected void configureJaxbMarshaller(javax.xml.bind.Marshaller marshaller)
            throws PropertyException {
        marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT,
                               m_formatOutput);
    }

    /**
     * Configure a JAXB unmarshaller.
     *
     * @param unmarshaller unmarshaller to configure
     *
     * @throws PropertyException when there is an error processing a property or
     *                           value
     */
    protected void configureJaxbUnmarshaller(Unmarshaller unmarshaller)
            throws PropertyException {
    }

    // ---- data members ----------------------------------------------------

    /**
     * Class of the root object this marshaller is for.
     */
    private final Class m_clzRoot;

    /**
     * Flag specifying whether to format XML output for human readability.
     */
    private final boolean m_formatOutput;

    /**
     * JAXB context to use for marshalling.
     */
    private final JAXBContext m_ctx;
}

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

package com.seovic.loader.target;


import com.seovic.core.Factory;
import com.seovic.core.Updater;
import com.seovic.core.updater.MapUpdater;
import com.seovic.loader.Source;
import com.seovic.loader.Target;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;


/**
 * A {@link Target} implementation that writes items into a CSV file.
 *
 * @author Aleksandar Seovic/Ivan Cikic  2009.06.15
 */
public class XmlTarget
        extends AbstractBaseTarget
    {
    // ---- constructors ----------------------------------------------------

    /**
     * Construct XmlTarget instance.
     *
     * @param writerFactory    writer factory that should be used to create Writer
     * @param rootElementName  root element name
     * @param itemElementName  item element name
     * @param propertyNames    property names (any property prefixed with '@'
     *                         will be written out as attribute)
     */
    public XmlTarget(Factory<Writer> writerFactory,
                     String rootElementName,
                     String itemElementName,
                     String propertyNames)
        {
        this(writerFactory, null, rootElementName, itemElementName, propertyNames);
        }

    /**
     * Construct XmlTarget instance.
     *
     * @param writerFactory    writer factory that should be used to create Writer
     * @param rootElementName  root element name
     * @param itemElementName  item element name
     * @param propertyNames    property names (any property prefixed with '@'
     *                         will be written out as attribute)
     */
    public XmlTarget(Factory<Writer> writerFactory,
                     String rootElementName,
                     String itemElementName,
                     String... propertyNames)
        {
        this(writerFactory, null, rootElementName, itemElementName, propertyNames);
        }

    /**
     * Construct XmlTarget instance.
     *
     * @param writerFactory    writer factory that should be used to create Writer
     * @param namespaces       namespace map
     * @param rootElementName  root element name
     * @param itemElementName  item element name
     * @param propertyNames    property names (any property prefixed with '@'
     *                         will be written out as attribute)
     */
    public XmlTarget(Factory<Writer> writerFactory,
                     Map<String, String> namespaces,
                     String rootElementName,
                     String itemElementName,
                     String propertyNames)
        {
        this(writerFactory, namespaces, rootElementName, itemElementName,
             propertyNames.split(","));
        }

    /**
     * Construct XmlTarget instance.
     *
     * @param writerFactory    writer factory that should be used to create Writer
     * @param namespaces       namespace map
     * @param rootElementName  root element name
     * @param itemElementName  item element name
     * @param propertyNames    property names (any property prefixed with '@'
     *                         will be written out as attribute)
     */
    public XmlTarget(Factory<Writer> writerFactory,
                     Map<String, String> namespaces,
                     String rootElementName,
                     String itemElementName,
                     String... propertyNames)
        {
        this((Writer) null, namespaces, rootElementName, itemElementName, propertyNames);
        m_writerFactory = writerFactory;
        }

    /**
     * Construct XmlTarget instance.
     * <p/>
     * This constructor should only be used when using XmlTarget in process.
     * In situations where this object might be serialized and used in a
     * remote process (as part of remote batch load job, for example), you
     * should use the constructor that accepts {@link Factory<Writer>} as an
     * argument instead.
     *
     * @param writer           writer to use
     * @param rootElementName  root element name
     * @param itemElementName  item element name
     * @param propertyNames    property names (any property prefixed with '@'
     *                         will be written out as attribute)
     */
    public XmlTarget(Writer writer,
                     String rootElementName,
                     String itemElementName,
                     String propertyNames)
        {
        this(writer, null, rootElementName, itemElementName, propertyNames);
        }

    /**
     * Construct XmlTarget instance.
     * <p/>
     * This constructor should only be used when using XmlTarget in process.
     * In situations where this object might be serialized and used in a
     * remote process (as part of remote batch load job, for example), you
     * should use the constructor that accepts {@link Factory<Writer>} as an
     * argument instead.
     *
     * @param writer           writer to use
     * @param rootElementName  root element name
     * @param itemElementName  item element name
     * @param propertyNames    property names (any property prefixed with '@'
     *                         will be written out as attribute)
     */
    public XmlTarget(Writer writer,
                     String rootElementName,
                     String itemElementName,
                     String... propertyNames)
        {
        this(writer, null, rootElementName, itemElementName, propertyNames);
        }

    /**
     * Construct XmlTarget instance.
     * <p/>
     * This constructor should only be used when using XmlTarget in process.
     * In situations where this object might be serialized and used in a
     * remote process (as part of remote batch load job, for example), you
     * should use the constructor that accepts {@link Factory<Writer>} as an
     * argument instead.
     *
     * @param writer           writer to use
     * @param namespaces       namespace map
     * @param rootElementName  root element name
     * @param itemElementName  item element name
     * @param propertyNames    property names (any property prefixed with '@'
     *                         will be written out as attribute)
     */
    public XmlTarget(Writer writer,
                     Map<String, String> namespaces,
                     String rootElementName,
                     String itemElementName,
                     String propertyNames)
        {
        this(writer, namespaces, rootElementName, itemElementName,
             propertyNames.split(","));
        }

    /**
     * Construct XmlTarget instance.
     * <p/>
     * This constructor should only be used when using XmlTarget in process.
     * In situations where this object might be serialized and used in a
     * remote process (as part of remote batch load job, for example), you
     * should use the constructor that accepts {@link Factory<Writer>} as an
     * argument instead.
     *
     * @param writer           writer to use
     * @param namespaces       namespace map
     * @param rootElementName  root element name
     * @param itemElementName  item element name
     * @param propertyNames    property names (any property prefixed with '@'
     *                         will be written out as attribute)
     */
    public XmlTarget(Writer writer,
                     Map<String, String> namespaces,
                     String rootElementName,
                     String itemElementName,
                     String... propertyNames)
        {
        m_writer          = writer;
        m_namespaceMap = namespaces == null
                            ? new HashMap<String, String>()
                            : namespaces;
        m_rootElementName = rootElementName;
        m_itemElementName = itemElementName;
        initAttributesAndElements(propertyNames);
        }


    // ---- AbstractBaseTarget implementation -------------------------------

    /**
     * {@inheritDoc}
     */
    protected Updater createDefaultUpdater(String propertyName)
        {
        return new MapUpdater(propertyName);
        }


    // ---- Source implementation -------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public void beginImport()
        {
        try
            {
            if (m_writer == null)
                {
                m_writer = m_writerFactory.create();
                }
            m_xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(m_writer);

            m_xmlWriter.writeStartDocument();
            m_xmlWriter.writeStartElement(m_rootElementName);
            for (Map.Entry<String, String> entry : m_namespaceMap.entrySet())
                {
                m_xmlWriter.writeNamespace(entry.getKey(), entry.getValue());
                }
            }
        catch (Exception e)
            {
            throw new RuntimeException(e);
            }
        }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({"unchecked"})
    public void importItem(Object item)
        {
        try
            {
            XMLStreamWriter     writer = m_xmlWriter;
            Map<String, String> nsMap  = m_namespaceMap;

            boolean hasDecendants = !m_elements.isEmpty();
            if (hasDecendants)
                {
                writer.writeStartElement(m_itemElementName);
                }
            else
                {
                writer.writeEmptyElement(m_itemElementName);
                }
            Map<String, Object> convertedItem = (Map<String, Object>) item;
            for (Property property : m_attributes)
                {
                String prefix = property.getNamespacePrefix();
                if (prefix == null)
                    {
                    prefix = "";
                    }
                String localName = property.getLocalName();
                String namespace = nsMap.get(prefix);
                if (namespace == null)
                    {
                    namespace = "";
                    }
                writer.writeAttribute(prefix,
                                      namespace,
                                      localName,
                                      convertedItem.get(localName).toString());
                }
            for (Property property : m_elements)
                {
                String prefix = property.getNamespacePrefix() == null
                                ? ""
                                : property.getNamespacePrefix();
                String localName = property.getLocalName();
                String namespace = nsMap.get(prefix) == null
                                   ? ""
                                   : nsMap.get(prefix);
                writer.writeStartElement(prefix, localName, namespace);
                writer.writeCharacters(convertedItem.get(localName).toString());
                writer.writeEndElement();
                }
            if (hasDecendants)
                {
                writer.writeEndElement();
                }
            }
        catch (XMLStreamException e)
            {
            throw new RuntimeException(e);
            }
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endImport()
        {
        try
            {
            m_xmlWriter.writeEndElement();
            m_xmlWriter.writeEndDocument();
            m_xmlWriter.close();
            }
        catch (XMLStreamException e)
            {
            throw new RuntimeException(e);
            }
        }

    /**
     * {@inheritDoc}
     */
    public Set<String> getPropertyNames()
        {
        Set<String> propertyNames =
                new HashSet<String>(m_attributes.size() + m_elements.size());
        for (Property property : m_attributes)
            {
            propertyNames.add(property.getLocalName());
            }
        for (Property property : m_elements)
            {
            propertyNames.add(property.getLocalName());
            }
        return propertyNames;
        }

    /**
     * {@inheritDoc}
     */
    public Object createTargetInstance(Source source, Object sourceItem)
        {
        return new HashMap<String, Object>();
        }


    // ---- helper methods --------------------------------------------------

    /**
     * Parses user-specified property names and determines which properties
     * should be written out as attributes and which as child elements.
     *
     * @param propertyNames  property names
     */
    private void initAttributesAndElements(String... propertyNames)
        {
        for (String propertyName : propertyNames)
            {
            Property property = new Property(propertyName);
            if (property.isAttribute())
                {
                m_attributes.add(property);
                }
            else
                {
                m_elements.add(property);
                }
            }
        }

    // ---- inner class: Property -------------------------------------------

    /**
     * Represents a single property.
     */
    private static class Property
            implements Serializable
        {
        // ---- constructors --------------------------------------------

        /**
         * Create property instance.
         *
         * @param propertyExpr  property expression to parse
         */
        public Property(String propertyExpr)
            {
            Matcher matcher = PATTERN.matcher(propertyExpr);
            if (matcher.matches())
                {
                m_nsPrefix = matcher.group(2);
                m_fAttribute = matcher.group(3) != null;
                m_localName = matcher.group(4);
                }
            else
                {
                throw new IllegalArgumentException(
                        "Property name must be in the [ns:][@]name format.");
                }
            }

        /**
         * Return namespace prefix.
         *
         * @return namespace prefix
         */
        public String getNamespacePrefix()
            {
            return m_nsPrefix;
            }

        /**
         * Return true if this property should be written out as attribute.
         *
         * @return true if this property should be written out as attribute
         */
        public boolean isAttribute()
            {
            return m_fAttribute;
            }

        /**
         * Return local name
         *
         * @return local name
         */
        public String getLocalName()
            {
            return m_localName;
            }

        // ---- Object methods ----------------------------------------------

        /**
         * Test objects for equality.
         *
         * @param o  object to compare this object with
         *
         * @return <tt>true</tt> if the specified object is equal to this object
         *         <tt>false</tt> otherwise
         */
        @Override
        public boolean equals(Object o)
            {
            if (this == o)
                {
                return true;
                }
            if (o == null || getClass() != o.getClass())
                {
                return false;
                }

            Property property = (Property) o;

            return m_fAttribute == property.m_fAttribute
                   && m_localName.equals(property.m_localName)
                   && m_nsPrefix == null
                       ? property.m_nsPrefix == null
                       : m_nsPrefix.equals(property.m_nsPrefix);
            }

        /**
         * Return hash code for this object.
         *
         * @return this object's hash code
         */
        @Override
        public int hashCode()
            {
            int result = m_nsPrefix != null ? m_nsPrefix.hashCode() : 0;
            result = 31 * result + (m_fAttribute ? 1 : 0);
            result = 31 * result + m_localName.hashCode();
            return result;
            }

        // ---- data members --------------------------------------------

        /**
         * Regex expression to use when parsing property names.
         */
        private static final Pattern PATTERN =
                Pattern.compile("((.*):)?(@)?(.+)");

        /**
         * Namespace prefix.
         */
        private String m_nsPrefix;

        /**
         * True if this property shold be writtin out as attribute.
         */
        private boolean m_fAttribute;

        /**
         * Property name.
         */
        private String m_localName;
        }


    // ---- data members ----------------------------------------------------

    /**
     * A factory that should be used to create writer.
     */
    private Factory<Writer> m_writerFactory;

    /**
     * A writer to use.
     */
    private transient Writer m_writer;

    /**
     * XML stream writer to use.
     */
    private transient XMLStreamWriter m_xmlWriter;

    /**
     * Root element name.
     */
    private String m_rootElementName;

    /**
     * Item element name.
     */
    private String m_itemElementName;

    /**
     * A list of property names that should be written as attributes.
     */
    private List<Property> m_attributes = new ArrayList<Property>();

    /**
     * A list of property names that should be written as child elements.
     */
    private List<Property> m_elements = new ArrayList<Property>();

    /**
     * Namespace map.
     */
    private Map<String, String> m_namespaceMap;
    }

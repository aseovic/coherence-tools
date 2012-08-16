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

package com.seovic.loader.source;


import com.seovic.core.Extractor;
import com.seovic.core.extractor.MapExtractor;
import com.seovic.loader.Source;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;


/**
 * A {@link Source} implementation that reads items to load from a CSV file.
 *
 * @author Aleksandar Seovic/Ivan Cikic  2009.06.15
 */
public class CsvSource
        extends AbstractBaseSource
    {
    // ---- constructors ----------------------------------------------------

    /**
     * Construct CsvSource instance.
     *
     * @param resourceName  the name of the CSV resource to read items from
     */
    public CsvSource(String resourceName)
        {
        m_resourceName = resourceName;
        }

    /**
     * Construct CsvSource instance.
     * <p/>
     * This constructor should only be used when using CsvSource in process.
     * In situations where this object might be serialized and used in a
     * remote process (as part of remote batch load job, for example), you
     * should use the constructor that accepts resource name as an argument.
     *
     * @param reader  the reader to use
     */
    public CsvSource(Reader reader)
        {
        m_reader = reader;
        }

    // ---- Source implementation -------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void beginExport()
        {
        if (m_reader == null)
            {
            m_reader = createResourceReader(getResource(m_resourceName));
            }
        }

    /**
     * {@inheritDoc}
     */
    public void endExport()
        {
        try
            {
            m_reader.close();
            }
        catch (IOException e)
            {
            throw new RuntimeException(e);
            }
        }


    // ---- Iterable implementation -----------------------------------------

    /**
     * Return an iterator over this source.
     *
     * @return a source iterator
     */
    public Iterator iterator()
        {
        CsvPreference preferences =
                new CsvPreference(m_quoteChar, m_delimiterChar, m_endOfLineSymbols);
        return new CsvIterator(new CsvListReader(m_reader, preferences), m_header);
        }


    // ---- public API ------------------------------------------------------

    /**
     * Set attribute names to use as header when one is not present in the file.
     *
     * @param header  an array of attribute names
     */
    public void setHeader(String... header)
        {
        m_header = header;
        }

    /**
     * Set the delimiter character for CSV fields (default is comma).
     *
     * @param delimiterChar  delimiter character
     */
    public void setDelimiterChar(char delimiterChar)
        {
        m_delimiterChar = delimiterChar;
        }

    /**
     * Set the quote character (default is double quote).
     *
     * @param quoteChar  quote character
     */
    public void setQuoteChar(char quoteChar)
        {
        m_quoteChar = quoteChar;
        }

    /**
     * Set the end-of-line characters.
     *
     * @param endOfLineSymbols  end-of-line characters
     */
    public void setEndOfLineSymbols(String endOfLineSymbols)
        {
        m_endOfLineSymbols = endOfLineSymbols;
        }


    // ---- AbstractBaseSource implementation -------------------------------

    /**
     * {@inheritDoc}
     */
    protected Extractor createDefaultExtractor(String propertyName)
        {
        return new MapExtractor(propertyName);
        }


    // ---- inner class: CsvIterator ----------------------------------------

    /**
     * Iterator implementation for CsvSource.
     */
    public class CsvIterator
            implements Iterator
        {
        // ---- constructors --------------------------------------------

        /**
         * Construct CsvIterator instance.
         *
         * @param reader  reader to use
         * @param header  an array of attribute names, if there is no header row
         *                in the file, or null to read attribute names from the
         *                header row
         */
        public CsvIterator(ICsvListReader reader, String[] header)
            {
            try
                {
                m_reader = reader;
                m_header = header != null ? header : reader.getCSVHeader(false);
                }
            catch (IOException e)
                {
                throw new RuntimeException(e);
                }
            }

        // ---- Iterator implementation ---------------------------------

        /**
         * Returns true if there are more items to read, false otherwise.
         *
         * @return true if there are more items to read, false otherwise
         */
        public boolean hasNext()
            {
            try
                {
                m_currentLine = m_reader.read();
                }
            catch (IOException e)
                {
                throw new RuntimeException(e);
                }
            return m_currentLine != null;
            }

        /**
         * Reads the next item from the file and converts it into a map of
         * attribute names to string values.
         *
         * @return a map of attribute names to values
         */
        public Object next()
            {
            return createMap(m_header, m_currentLine);
            }

        /**
         * Not supported.
         */
        public void remove()
            {
            throw new UnsupportedOperationException(
                    "CsvIterator does not support remove operation");
            }

        // ---- helper methods ------------------------------------------

        /**
         * Creates a name-value map for a single item.
         *
         * @param keys    attribute names
         * @param values  attribute values
         *
         * @return a map of attribute names to values
         */
        private Map<String, String> createMap(String[] keys,
                                              List<String> values)
            {
            Map<String, String> mapValues = new HashMap<String, String>();
            for (int i = 0, count = values.size(); i < count; i++)
                {
                String value = values.get(i);
                mapValues.put(keys[i], value.length() > 0 ? value : null);
                }
            return mapValues;
            }

        // ---- data members --------------------------------------------

        /**
         * A list of attribute values for the last line read.
         */
        private List<String> m_currentLine;

        /**
         * Reader to use.
         */
        private ICsvListReader m_reader;

        /**
         * An array of attribute names (parsed from the header row).
         */
        private String[] m_header;
        }


    // ---- data members ----------------------------------------------------

    /**
     * The name of the CSV resource to read items from.
     */
    private String m_resourceName;

    /**
     * An array of attribute names to use as the file header.
     */
    private String[] m_header;

    /**
     * The delimiter character for CSV fields.
     */
    private char m_delimiterChar =
            (char) CsvPreference.STANDARD_PREFERENCE.getDelimiterChar();

    /**
     * The quote character.
     */
    private char m_quoteChar =
            (char) CsvPreference.STANDARD_PREFERENCE.getQuoteChar();

    /**
     * The end-of-line characters.
     */
    private String m_endOfLineSymbols =
            CsvPreference.STANDARD_PREFERENCE.getEndOfLineSymbols();

    /**
     * Reader to use.
     */
    private transient Reader m_reader;
    }

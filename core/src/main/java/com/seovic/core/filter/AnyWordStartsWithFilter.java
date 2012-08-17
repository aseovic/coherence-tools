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

package com.seovic.core.filter;


import com.seovic.core.Condition;
import com.seovic.core.extractor.PropertyExtractor;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.util.ValueExtractor;
import com.tangosol.util.filter.ComparisonFilter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;


/**
 * @author Ivan Cikic  2011.04.19
 */
public class AnyWordStartsWithFilter
                extends ComparisonFilter
                implements Condition, Serializable {

    // ---- data members ----------------------------------------------------

    /**
     * Flag specifying if case should be ignored when comparing strings.
     */
    private boolean ignoreCase;


    // ---- constructors ----------------------------------------------------
    /**
     * Deserialization constructor. For internal use only.
     */
    public AnyWordStartsWithFilter()
        {
        }

    /**
     * Construct <tt>AnyWordStartsWithFilter</tt> instance.
     *
     * @param propertyName  the name of the propery to evaluate
     * @param filter        the search string
     * @param ignoreCase    the flag specifying whether case should be ignored
     *                      when comparing strings
     */
    public AnyWordStartsWithFilter(String propertyName, String filter, boolean ignoreCase)
        {
        this(new PropertyExtractor(propertyName), filter, ignoreCase);
        }

    /**
     * Construct <tt>AnyWordStartsWithFilter</tt> instance.
     *
     * @param extractor   the property extractor to use
     * @param filter      the search string
     * @param ignoreCase  the flag specifying whether case should be ignored
     *                    when comparing strings
     */
    public AnyWordStartsWithFilter(ValueExtractor extractor, String filter, boolean ignoreCase)
        {
        super(extractor, filter);
        this.ignoreCase = ignoreCase;
        }


   // ---- ExtractorFilter implementation ----------------------------------

    /**
     * {@inheritDoc}
     */
    protected boolean evaluateExtracted(Object o)
        {
        String sWords   = (String) o;
        String sFilter  = getFilterString();
        boolean matches = false;
        if (sWords != null && !sWords.isEmpty() && sFilter != null && !sFilter.isEmpty())
            {
            String[] arrWords = sWords.split("[\\s\\-/]");
            matches = true;
            for (String sPart : sFilter.split("\\s"))
                {
                if (!isMatch(arrWords, sPart))
                    {
                        matches = false;
                        break;
                    }
                }
            }
        return matches;
        }



    // ---- helper methods --------------------------------------------------

    /**
     * Return filter string.
     *
     * @return filter string
     */
    protected String getFilterString()
        {
        return (String) getValue();
        }

    /**
     * Return <tt>true</tt> if the specified value matches this filter.
     *
     * TODO: javadoc
     */
    protected boolean isMatch(String[] words, String filter)
        {
        for (String word : words)
            {
            int len = filter.length();
            if (word.regionMatches(ignoreCase, 0, filter, 0, len))
                {
                return true;
                }
            }
        return false;
        }

    // ---- ExternalizableLite implementation -------------------------------

    /**
     * {@inheritDoc}
     */
    public void readExternal(DataInput in)
            throws IOException
        {
        super.readExternal(in);

        ignoreCase = in.readBoolean();
        }

    /**
     * {@inheritDoc}
     */
    public void writeExternal(DataOutput out)
            throws IOException
        {
        super.writeExternal(out);

        out.writeBoolean(ignoreCase);
        }

    // ---- PortableObject implementation -----------------------------------

    /**
     * {@inheritDoc}
     */
    public void readExternal(PofReader reader)
            throws IOException
        {
        super.readExternal(reader);

        ignoreCase = reader.readBoolean(2);
        }

    /**
     * {@inheritDoc}
     */
    public void writeExternal(PofWriter writer)
            throws IOException
        {
        super.writeExternal(writer);

        writer.writeBoolean(2, ignoreCase);
        }


    // ---- Object methods implementation -----------------------------------

    /**
     * {@inheritDoc}
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

        AnyWordStartsWithFilter that = (AnyWordStartsWithFilter) o;

        return ignoreCase == that.ignoreCase
                && super.equals(o);
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
        {
        int result = super.hashCode();
        result = 31 * result + (ignoreCase ? 1 : 0);
        return result;
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
        {
        return "AnyWordStartsWithFilter{" +
               "extractor=" + getValueExtractor() +
               ", filterString=" + getFilterString() +
               ", ignoreCase=" + ignoreCase +
               '}';
        }
    }

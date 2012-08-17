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
import com.tangosol.util.Filter;
import com.tangosol.util.MapIndex;
import com.tangosol.util.ValueExtractor;
import com.tangosol.util.filter.ComparisonFilter;
import com.tangosol.util.filter.IndexAwareFilter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;


/**
 * A {@link Filter} implementation that evaluates to <tt>true</tt> if the
 * extracted property value starts with a specified string.
 * <p/>
 * This filter provides a subset of the functionality provided by the built-in
 * <tt>LikeFilter</tt>, but is slightly lighter and faster as it only has to
 * evaluate one special, although very common case supported by the
 * <tt>LikeFilter</tt>.
 * 
 * @author Aleksandar Seovic  2009.06.07
 */
@SuppressWarnings("unchecked")
public class StartsWithFilter
        extends    ComparisonFilter
        implements Condition, IndexAwareFilter, Serializable
    {
    // ---- data members ----------------------------------------------------

    /**
     * Flag specifying if case should be ignored when comparing strings.
     */
    private boolean m_ignoreCase;


    // ---- constructors ----------------------------------------------------

    /**
     * Deserialization constructor. For internal use only.
     */
    public StartsWithFilter()
        {
        }

    /**
     * Construct <tt>StartsWithFilter</tt> instance.
     *
     * @param propertyName  the name of the propery to evaluate
     * @param filter        the search string
     * @param ignoreCase    the flag specifying whether case should be ignored
     *                      when comparing strings
     */
    public StartsWithFilter(String propertyName, String filter, boolean ignoreCase)
        {
        this(new PropertyExtractor(propertyName), filter, ignoreCase);
        }

    /**
     * Construct <tt>StartsWithFilter</tt> instance.
     *
     * @param extractor   the property extractor to use
     * @param filter      the search string
     * @param ignoreCase  the flag specifying whether case should be ignored
     *                    when comparing strings
     */
    public StartsWithFilter(ValueExtractor extractor, String filter, boolean ignoreCase)
        {
        super(extractor, filter);
        m_ignoreCase = ignoreCase;
        }


    // ---- ExtractorFilter implementation ----------------------------------

    /**
     * {@inheritDoc}
     */
    protected boolean evaluateExtracted(Object o)
        {
        return isMatch((String) o);
        }


    // ---- IndexAwareFilter implementation ---------------------------------

    /**
     * {@inheritDoc}
     */
    public int calculateEffectiveness(Map mapIndexes, Set setKeys)
        {
        return calculateRangeEffectiveness(mapIndexes, setKeys);
        }

    /**
     * {@inheritDoc}
     */
    public Filter applyIndex(Map mapIndexes, Set setKeys)
        {
        MapIndex index = (MapIndex) mapIndexes.get(getValueExtractor());
        if (index == null)
            {
            // there is no relevant index
            return this;
            }

        Map     candidates     = index.getIndexContents();
        Set     matches        = new HashSet();
        boolean abortIfNoMatch = false;

        if (!m_ignoreCase && index.isOrdered())
            {
            candidates     = ((SortedMap) candidates).tailMap(getFilterString());
            abortIfNoMatch = true;
            }

        for (Object entry : candidates.entrySet())
            {
            Map.Entry indexEntry    = (Map.Entry) entry;
            String    propertyValue = (String) indexEntry.getKey();
            if (isMatch(propertyValue))
                {
                matches.addAll((Set) indexEntry.getValue());
                }
            else if (abortIfNoMatch)
                {
                break;
                }
            }

        setKeys.retainAll(matches);
        return null;
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
     * @param value  value to check for a match
     *
     * @return <tt>true</tt> if the specified value matches this filter,
     *         <tt>false</tt> otherwise
     */
    protected boolean isMatch(String value)
        {
        String filter = getFilterString();
        int    len    = filter.length();

        return value.regionMatches(m_ignoreCase, 0, filter, 0, len);
        }


    // ---- ExternalizableLite implementation -------------------------------

    /**
     * {@inheritDoc}
     */
    public void readExternal(DataInput in)
            throws IOException
        {
        super.readExternal(in);

        m_ignoreCase = in.readBoolean();
        }

    /**
     * {@inheritDoc}
     */
    public void writeExternal(DataOutput out)
            throws IOException
        {
        super.writeExternal(out);

        out.writeBoolean(m_ignoreCase);
        }

    // ---- PortableObject implementation -----------------------------------

    /**
     * {@inheritDoc}
     */
    public void readExternal(PofReader reader)
            throws IOException
        {
        super.readExternal(reader);

        m_ignoreCase = reader.readBoolean(2);
        }

    /**
     * {@inheritDoc}
     */
    public void writeExternal(PofWriter writer)
            throws IOException
        {
        super.writeExternal(writer);
        
        writer.writeBoolean(2, m_ignoreCase);
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

        StartsWithFilter that = (StartsWithFilter) o;

        return m_ignoreCase == that.m_ignoreCase
                && super.equals(o);
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
        {
        int result = super.hashCode();
        result = 31 * result + (m_ignoreCase ? 1 : 0);
        return result;
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
        {
        return "StartsWithFilter{" +
               "extractor=" + getValueExtractor() +
               ", filterString=" + getFilterString() +
               ", ignoreCase=" + m_ignoreCase +
               '}';
        }
    }

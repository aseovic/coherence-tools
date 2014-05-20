package com.seovic.core.index;


import com.tangosol.util.*;
import com.tangosol.util.extractor.IdentityExtractor;
import com.tangosol.util.filter.NotEqualsFilter;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;


/**
 */
@SuppressWarnings("unchecked")
public class BaseIndex
        implements MapIndex
    {
    protected static Filter NOT_NULL = new NotEqualsFilter(IdentityExtractor.INSTANCE, null);

    protected ValueExtractor extractor;
    protected boolean fOrdered;
    protected boolean fInverse;
    protected boolean fForward;
    protected Filter partialFilter;
    protected Comparator comparator;
    protected Map inverseIndex;
    protected Map forwardIndex;

    protected BaseIndex(ValueExtractor extractor, boolean fInverse, boolean fForward, Filter partialFilter, boolean fOrdered, Comparator comparator)
        {
        this.extractor = extractor;
        this.fInverse = fInverse;
        this.fForward = fForward;
        this.partialFilter = partialFilter;
        this.fOrdered = fOrdered;
        this.comparator = comparator;
        if (fInverse)
            {
            this.inverseIndex = fOrdered
                                ? new SafeSortedMap(comparator)
                                : new SegmentedHashMap();
            }
        if (fForward)
            {
            forwardIndex = new SegmentedHashMap();
            }
        }

    @Override
    public ValueExtractor getValueExtractor()
        {
        return extractor;
        }

    @Override
    public boolean isOrdered()
        {
        return fOrdered;
        }

    @Override
    public boolean isPartial()
        {
        return partialFilter != null;
        }

    @Override
    public Map getIndexContents()
        {
        return fInverse ? inverseIndex : Collections.emptyMap();
        }

    @Override
    public Object get(Object o)
        {
        return fForward ? forwardIndex.get(o) : NO_VALUE;
        }

    @Override
    public Comparator getComparator()
        {
        return comparator;
        }

    @Override
    public void insert(Map.Entry entry)
        {
        Object key = extractKey(entry);
        Object value = extractNewValue(entry);
        if (partialFilter == null || partialFilter.evaluate(value))
            {
            addToIndex(value, key);
            }
        }

    @Override
    public void update(Map.Entry entry)
        {
        Object key = extractKey(entry);
        Object newValue = extractNewValue(entry);
        Object oldValue = extractOldValue(entry, key);

        synchronized (this)
            {
            if (oldValue != null && !Base.equalsDeep(oldValue, newValue))
                {
                removeFromIndex(oldValue, key);
                }
            if (partialFilter == null || partialFilter.evaluate(newValue))
                {
                addToIndex(newValue, key);
                }
            }
        }

    @Override
    public void delete(Map.Entry entry)
        {
        Object key = extractKey(entry);
        Object value = extractOldValue(entry, key);
        removeFromIndex(value, key);
        }

    protected synchronized void addToIndex(Object value, Object key)
        {
        if (fInverse)
            {
            Set keys = (Set) inverseIndex.get(value);
            if (keys == null)
                {
                keys = new InflatableSet();
                inverseIndex.put(value, keys);
                }
            keys.add(key);
            }
        if (fForward)
            {
            forwardIndex.put(key, value);
            }
        }

    protected synchronized void removeFromIndex(Object value, Object key)
        {
        if (fInverse)
            {
            Set keys = (Set) inverseIndex.get(value);
            if (keys != null)
                {
                keys.remove(key);
                }
            }
        if (fForward)
            {
            forwardIndex.remove(key);
            }
        }

    protected Object extractKey(Map.Entry entry)
        {
        return entry instanceof BinaryEntry
               ? ((BinaryEntry) entry).getBinaryKey()
               : entry.getKey();
        }

    protected Object extractNewValue(Map.Entry entry)
        {
        return InvocableMapHelper.extractFromEntry(extractor, entry);
        }

    protected Object extractOldValue(Map.Entry entry, Object key)
        {
        Object oldValue = get(key);
        if (oldValue == NO_VALUE)
            {
            if (entry instanceof MapTrigger.Entry)
                {
                try
                    {
                    oldValue = InvocableMapHelper.extractOriginalFromEntry(extractor, (MapTrigger.Entry) entry);
                    }
                catch (RuntimeException e)
                    {
                    oldValue = null;
                    }
                }
            else if (entry instanceof BinaryEntry)
                {
                try
                    {
                    oldValue = ((BinaryEntry) entry).getOriginalBinaryValue();
                    }
                catch (RuntimeException e)
                    {
                    oldValue = null;
                    }
                }
            else
                {
                // should never get here
                throw new IllegalStateException("Cannot extract the old value");
                }
            }
        return oldValue;
        }

    @Override
    public String toString()
        {
        return getClass().getSimpleName() + "{" +
               "extractor=" + extractor +
               ", ordered=" + fOrdered +
               ", comparator=" + comparator +
               ", partial=" + isPartial() +
               ", inverseIndex[enabled=" + fInverse + ", size=" + (fInverse
                                                                   ? inverseIndex.size()
                                                                   : "n/a") + "]" +
               ", forwardIndex[enabled=" + fForward + ", size=" + (fForward
                                                                   ? forwardIndex.size()
                                                                   : "n/a") + "]" +
               '}';
        }
    }

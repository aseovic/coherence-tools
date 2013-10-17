package com.seovic.core.processor;


import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;

import com.tangosol.net.BackingMapManagerContext;

import com.tangosol.util.Binary;
import com.tangosol.util.BinaryEntry;
import com.tangosol.util.InvocableMap;
import com.tangosol.util.processor.AbstractProcessor;

import java.io.IOException;
import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * An entry processor that copies entry from the cache it is executed on to a
 * target cache on the same cache service.
 *
 * @author Aleksandar Seovic  2013.09.05
 */
@SuppressWarnings("unchecked")
public class LocalCopyProcessor
        extends AbstractProcessor
        implements Serializable, PortableObject
    {
    private String targetCacheName;

    public LocalCopyProcessor()
        {
        }

    public LocalCopyProcessor(String targetCacheName)
        {
        this.targetCacheName = targetCacheName;
        }

    public Object process(InvocableMap.Entry entry)
        {
        BinaryEntry binEntry = (BinaryEntry) entry;
        if (entry.isPresent())
            {
            getTargetBackingMap(binEntry).put(binEntry.getBinaryKey(), binEntry.getBinaryValue());
            }
        return null;
        }

    public Map processAll(Set entries)
        {
        Map<Binary, Binary> binEntries = new HashMap<Binary, Binary>(entries.size());
        BinaryEntry binEntry = null;
        for (Object entry : entries)
            {
            binEntry = (BinaryEntry) entry;
            if (binEntry.isPresent())
                {
                binEntries.put(binEntry.getBinaryKey(), binEntry.getBinaryValue());
                }
            }

        if (binEntry != null)
            {
            getTargetBackingMap(binEntry).putAll(binEntries);
            }

        return null;
        }

    protected Map getTargetBackingMap(BinaryEntry binEntry)
        {
        BackingMapManagerContext ctx = binEntry.getBackingMapContext().getManagerContext();
        return ctx.getBackingMapContext(targetCacheName).getBackingMap();
        }

    public void readExternal(PofReader in)
            throws IOException
        {
        targetCacheName = in.readString(0);
        }

    public void writeExternal(PofWriter out)
            throws IOException
        {
        out.writeString(0, targetCacheName);
        }
    }

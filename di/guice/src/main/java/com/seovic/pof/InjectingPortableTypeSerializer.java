package com.seovic.pof;


import com.google.inject.Injector;

import com.tangosol.io.pof.PofReader;
import com.tangosol.net.CacheFactory;

import java.io.IOException;


/**
 * An extension of the {@link PortableTypeSerializer} that uses Guice injector
 * configured in the Coherence cache config to inject dependencies into
 * deserialized object.
 *
 * @author Aleksandar Seovic  2013.10.07
 */
public class InjectingPortableTypeSerializer
        extends PortableTypeSerializer
    {
    private static final Injector INJECTOR =
            CacheFactory.getConfigurableCacheFactory()
                    .getResourceRegistry()
                    .getResource(Injector.class);

    /**
     * Construct InjectingPortableTypeSerializer instance.
     *
     * @param typeId  POF type identifier
     * @param clz     Java class of the type
     */
    public InjectingPortableTypeSerializer(int typeId, Class<?> clz)
        {
        super(typeId, clz);
        }

    /**
     * {@inheritDoc}
     */
    public Object deserialize(PofReader in)
            throws IOException
        {
        Object o = super.deserialize(in);
        INJECTOR.injectMembers(o);
        return o;
        }
    }

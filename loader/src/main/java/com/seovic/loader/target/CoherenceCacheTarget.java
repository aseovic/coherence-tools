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


import com.seovic.core.Entity;
import com.seovic.identity.EntityIdExtractor;
import com.seovic.identity.IdExtractor;
import com.seovic.identity.IdGenerator;
import com.seovic.loader.Target;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;

import java.util.HashMap;
import java.util.Map;


/**
 * A {@link Target} implementation that loads objects into Coherence cache.
 *
 * @author Aleksandar Seovic/Ivan Cikic  2009.06.15
 */
@SuppressWarnings("unchecked")
public class CoherenceCacheTarget
        extends AbstractObjectTarget {
    // ---- constructors ----------------------------------------------------

    /**
     * Construct CoherenceCacheTarget instance.
     * <p/>
     * This constructor assumes that the target class implements {@link Entity}
     * interface and will use {@link EntityIdExtractor} to determine cache
     * key.
     *
     * @param cacheName the name of the cache to load objects into
     * @param itemClass target item class
     */
    public CoherenceCacheTarget(String cacheName, Class itemClass) {
        super(itemClass);
        init(cacheName, null, null, new EntityIdExtractor());
    }

    /**
     * Construct CoherenceCacheTarget instance.
     *
     * @param cacheName   the name of the cache to load objects into
     * @param itemClass   target item class
     * @param idGenerator identity generator to use to determine key
     */
    public CoherenceCacheTarget(String cacheName, Class itemClass,
                                IdGenerator idGenerator) {
        super(itemClass);
        init(cacheName, null, idGenerator, null);
    }

    /**
     * Construct CoherenceCacheTarget instance.
     *
     * @param cacheName   the name of the cache to load objects into
     * @param itemClass   target item class
     * @param idExtractor identity extractor to use to determine key
     */
    public CoherenceCacheTarget(String cacheName, Class itemClass,
                                IdExtractor idExtractor) {
        super(itemClass);
        init(cacheName, null, null, idExtractor);
    }

    /**
     * Construct CoherenceCacheTarget instance.
     * <p/>
     * This constructor assumes that target class implements {@link Entity}
     * interface and will use {@link EntityIdExtractor} to determine cache
     * key.
     * <p/>
     * This constructor should only be used when using this target in process.
     * In situations where this object might be serialized and used in a remote
     * process (as part of remote batch load job, for example), you should use
     * the constructor that accepts cache name as an argument instead.
     *
     * @param cache     the cache to load objects into
     * @param itemClass target item class
     */
    public CoherenceCacheTarget(NamedCache cache, Class itemClass) {
        super(itemClass);
        init(null, cache, null, new EntityIdExtractor());
    }

    /**
     * Construct CoherenceCacheTarget instance.
     * <p/>
     * This constructor should only be used when using this target in process.
     * In situations where this object might be serialized and used in a remote
     * process (as part of remote batch load job, for example), you should use
     * the constructor that accepts cache name as an argument instead.
     *
     * @param cache       the cache to load objects into
     * @param itemClass   target item class
     * @param idGenerator identity generator to use to determine key
     */
    public CoherenceCacheTarget(NamedCache cache, Class itemClass,
                                IdGenerator idGenerator) {
        super(itemClass);
        init(null, cache, idGenerator, null);
    }

    /**
     * Construct CoherenceCacheTarget instance.
     * <p/>
     * This constructor should only be used when using this target in process.
     * In situations where this object might be serialized and used in a remote
     * process (as part of remote batch load job, for example), you should use
     * the constructor that accepts cache name as an argument instead.
     *
     * @param cache       the cache to load objects into
     * @param itemClass   target item class
     * @param idExtractor identity extractor to use to determine key
     */
    public CoherenceCacheTarget(NamedCache cache, Class itemClass,
                                IdExtractor idExtractor) {
        super(itemClass);
        init(null, cache, null, idExtractor);
    }

    /**
     * Initializes CoherenceCacheTarget instance.
     *
     * @param cacheName   the name of the cache to load objects into
     * @param cache       the cache to load objects into
     * @param idGenerator identity generator to use to determine key
     * @param idExtractor identity extractor to use to determine key
     */
    private void init(String cacheName, NamedCache cache,
                      IdGenerator idGenerator,
                      IdExtractor idExtractor) {
        m_cacheName   = cacheName;
        m_cache       = cache;
        m_idGenerator = idGenerator;
        m_idExtractor = idExtractor;
    }

    // ---- public API ------------------------------------------------------

    /**
     * Set batch size.
     *
     * @param batchSize batch size
     */
    public void setBatchSize(int batchSize) {
        m_batchSize = batchSize;
    }

    // ---- Target implementation -------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void beginImport() {
        if (m_cache == null) {
            m_cache = CacheFactory.getCache(m_cacheName);
        }
        m_batch = new HashMap(m_batchSize);
    }

    @SuppressWarnings("unchecked")
    public void importItem(Object item) {
        Object id = m_idGenerator != null
                    ? m_idGenerator.generateId()
                    : m_idExtractor.extractId(item);

        m_batch.put(id, item);
        if (m_batch.size() % m_batchSize == 0) {
            m_cache.putAll(m_batch);
            m_batch.clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void endImport() {
        if (!m_batch.isEmpty()) {
            m_cache.putAll(m_batch);
        }
    }


    // ---- data members ----------------------------------------------------

    /**
     * Default batch size.
     */
    public static final int DEFAULT_BATCH_SIZE = 1000;

    /**
     * The name of the cache to load objects into.
     */
    private String m_cacheName;

    /**
     * Cache to load objects into.
     */
    private transient NamedCache m_cache;

    /**
     * Identity generator.
     */
    private IdGenerator m_idGenerator;

    /**
     * Identity extractor.
     */
    private IdExtractor m_idExtractor;

    /**
     * Batch of items.
     */
    private transient Map m_batch;

    /**
     * Batch size.
     */
    private int m_batchSize = DEFAULT_BATCH_SIZE;
}

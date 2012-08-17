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

package com.seovic.core.persistence;


import com.tangosol.net.cache.AbstractCacheStore;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Abstract base class for cache store implementations that breaks the map of
 * entries passed to {@link #storeAll(Map)} into batches of configurable size.
 * <p/>
 * By default batch size iz zero, which causes all entries to be persisted in a
 * single call to the underlying data store.
 *
 * @author Patrick Peralta/Aleksandar Seovic  2010.01.30
 */
@SuppressWarnings({"unchecked"})
public abstract class AbstractBatchingCacheStore
        extends AbstractCacheStore {

    /**
     * The number of items that will be persisted in a single database call
     * during a {@link #storeAll(Map)} invocation.
     */
    private int batchSize;

    /**
     * Persist all entries from the specified map into the data store.
     *
     * @param mapEntries entries to persist
     */
    public abstract void storeBatch(Map mapEntries);

    /**
     * Persist all entries from the specified map into the data store.
     *
     * @param mapEntries entries to persist
     */
    @Override
    public void storeAll(Map mapEntries) {
        int batchSize = getBatchSize();
        if (batchSize == 0 || mapEntries.size() < batchSize) {
            storeBatch(mapEntries);
        }
        else {
            Map batch = new HashMap(batchSize);

            while (!mapEntries.isEmpty()) {
                Iterator iter = mapEntries.entrySet().iterator();
                while (iter.hasNext() && batch.size() < batchSize) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    batch.put(entry.getKey(), entry.getValue());
                }

                storeBatch(batch);

                mapEntries.keySet().removeAll(batch.keySet());
                batch.clear();
            }
        }
    }

    /**
     * Return batch size.
     *
     * @return number of items that will be persisted in a single batch during a
     *         {@link #storeAll(Map)} invocation.
     */
    public int getBatchSize() {
        return batchSize;
    }

    /**
     * Set batch size.
     *
     * @param batchSize number of items that will be persisted in a single
     *                  database call during a {@link #storeAll(Map)}
     *                  invocation.
     */
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }
}

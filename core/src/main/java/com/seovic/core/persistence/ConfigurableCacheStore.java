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


import com.tangosol.net.cache.CacheLoader;
import com.tangosol.net.cache.CacheStore;
import java.util.Collection;
import java.util.Map;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * @author Aleksandar Seovic  2010.01.22
 */
public class ConfigurableCacheStore
        implements CacheStore {
    // ---- configuration context -------------------------------------------

    private static final ApplicationContext CONTEXT =
            new ClassPathXmlApplicationContext(
                    System.getProperty("cache-store.context",
                                       "cache-store-context.xml"));


    // ---- data members ----------------------------------------------------

    private CacheLoader loader;
    private CacheStore store;

    // ---- constructors ----------------------------------------------------

    /**
     * Construct <tt>ConfigurableBackingMapListener</tt> instance.
     *
     * @param cacheName name of the cache to set up listener for
     */
    public ConfigurableCacheStore(String cacheName) {
        loader = (CacheLoader) CONTEXT.getBean(cacheName);
        store = loader instanceof CacheStore
                  ? (CacheStore) loader
                  : new UnsupportedCacheStore();
    }


    // ---- CacheLoader implementation ---------------------------------------

    public Object load(Object key) {
        return loader.load(key);
    }

    public Map loadAll(Collection collection) {
        return loader.loadAll(collection);
    }


    // ---- CacheStore implementation ---------------------------------------

    public void store(Object key, Object value) {
        store.store(key, value);
    }

    public void storeAll(Map map) {
        store.storeAll(map);
    }

    public void erase(Object key) {
        store.erase(key);
    }

    public void eraseAll(Collection collection) {
        store.eraseAll(collection);
    }


    // ---- inner class: UnsupportedCacheStore ------------------------------

    private static class UnsupportedCacheStore
            implements CacheStore {
        public void store(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        public void storeAll(Map map) {
            throw new UnsupportedOperationException();
        }

        public void erase(Object key) {
            throw new UnsupportedOperationException();
        }

        public void eraseAll(Collection collection) {
            throw new UnsupportedOperationException();
        }

        public Object load(Object key) {
            throw new UnsupportedOperationException();
        }

        public Map loadAll(Collection collection) {
            throw new UnsupportedOperationException();
        }
    }
}

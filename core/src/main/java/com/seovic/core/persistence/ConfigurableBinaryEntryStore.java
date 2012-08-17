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


import com.tangosol.net.cache.BinaryEntryStore;
import com.tangosol.util.BinaryEntry;
import java.util.Set;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * @author Aleksandar Seovic  2010.06.24
 */
public class ConfigurableBinaryEntryStore
        implements BinaryEntryStore {
    // ---- configuration context -------------------------------------------

    private static final ApplicationContext CONTEXT =
            new ClassPathXmlApplicationContext(
                    System.getProperty("binary.entry.store.context",
                                       "binary-entry-store-context.xml"));

    // ---- data members ----------------------------------------------------

    private BinaryEntryStore store;

    // ---- constructors ----------------------------------------------------

    /**
     * Construct <tt>ConfigurableBinaryEntryStore</tt> instance.
     *
     * @param cacheName the name of the cache to construct the store for
     */
    public ConfigurableBinaryEntryStore(String cacheName) {
        store = (BinaryEntryStore) CONTEXT.getBean(cacheName);
    }


    // ---- BinaryEntryStore implementation ---------------------------------

    public void load(BinaryEntry entry) { store.load(entry); }

    public void loadAll(Set set) { store.loadAll(set); }

    public void store(BinaryEntry entry) { store.store(entry); }

    public void storeAll(Set set) { store.storeAll(set); }

    public void erase(BinaryEntry entry) { store.erase(entry); }

    public void eraseAll(Set set) { store.eraseAll(set); }
}
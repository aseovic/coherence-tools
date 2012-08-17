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


/**
 * @author Aleksandar Seovic  2010.06.29
 */
@SuppressWarnings({"unchecked"})
public abstract class AbstractBinaryEntryStore implements BinaryEntryStore {
    @Override
    public void load(BinaryEntry entry) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void loadAll(Set set) {
        for (BinaryEntry entry : (Set<BinaryEntry>) set) {
            load(entry);
        }
    }

    @Override
    public void store(BinaryEntry entry) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void storeAll(Set set) {
        for (BinaryEntry entry : (Set<BinaryEntry>) set) {
            store(entry);
        }
    }

    @Override
    public void erase(BinaryEntry entry) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void eraseAll(Set set) {
        for (BinaryEntry entry : (Set<BinaryEntry>) set) {
            erase(entry);
        }
    }
}

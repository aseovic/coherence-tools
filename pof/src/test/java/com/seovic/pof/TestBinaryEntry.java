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

package com.seovic.pof;


import com.tangosol.io.Serializer;
import com.tangosol.io.pof.PofContext;
import com.tangosol.net.BackingMapContext;
import com.tangosol.net.BackingMapManagerContext;
import com.tangosol.util.Binary;
import com.tangosol.util.BinaryEntry;
import com.tangosol.util.ObservableMap;
import com.tangosol.util.ValueExtractor;
import com.tangosol.util.ValueUpdater;


/**
* Created with IntelliJ IDEA.
* User: aseovi
* Date: 6/25/12
* Time: 12:55 PM
* To change this template use File | Settings | File Templates.
*/
class TestBinaryEntry
        implements BinaryEntry {
    // ----- constructor ----------------------------------------------

    TestBinaryEntry(Binary binKey, Binary binValue, PofContext pofContext) {
        m_binKey = binKey;
        m_binValue = binValue;
        m_pofContext = pofContext;
    }

    // ----- BinaryEntry implementation -------------------------------

    public Binary getBinaryKey() {
        return m_binKey;
    }

    public Binary getBinaryValue() {
        return m_binValue;
    }

    public Serializer getSerializer() {
        return m_pofContext;
    }

    public BackingMapManagerContext getContext() {
        return null;
    }

    public void updateBinaryValue(Binary binValue) {
        m_binValue = binValue;
    }

    public void updateBinaryValue(Binary binary, boolean b) {
    }

    public Object getKey() {
        return null;
    }

    public Object getValue() {
        return null;
    }

    public Object setValue(Object oValue) {
        return null;
    }

    public void setValue(Object oValue, boolean fSynthetic) {
    }

    public void update(ValueUpdater updater, Object oValue) {
    }

    public boolean isPresent() {
        return false;
    }

    public void remove(boolean fSynthetic) {
    }

    public Object extract(ValueExtractor extractor) {
        return null;
    }

    public Object getOriginalValue() {
        return null;
    }

    public Binary getOriginalBinaryValue() {
        return null;
    }

    public ObservableMap getBackingMap() {
        return null;
    }

    public BackingMapContext getBackingMapContext() {
        return null;
    }

    public void expire(long cMillis) {
    }

    public boolean isReadOnly() {
        return true;
    }

    private Binary m_binKey;
    private Binary m_binValue;
    private PofContext m_pofContext;
}

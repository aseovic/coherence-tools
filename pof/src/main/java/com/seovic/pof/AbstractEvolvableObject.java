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


import com.tangosol.io.pof.reflect.PofNavigator;
import com.tangosol.util.Binary;
import com.tangosol.util.extractor.PofExtractor;
import com.tangosol.util.extractor.PofUpdater;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


@SuppressWarnings("unchecked")
public abstract class AbstractEvolvableObject
        implements EvolvableObject {

    private SortedMap<Integer, Integer> versionMap = new TreeMap<Integer, Integer>();
    private Map<Integer, Binary> futureDataMap = new HashMap<Integer, Binary>();

    public int getDataVersion(int typeId) {
        Integer version = versionMap.get(typeId);
        return version == null ? 0 : version;
    }

    public void setDataVersion(int typeId, int dataVersion) {
        versionMap.put(typeId, dataVersion);
    }

    public Binary getFutureData(int typeId) {
        return futureDataMap.get(typeId);
    }

    public void setFutureData(int typeId, Binary futureData) {
        futureDataMap.put(typeId, futureData);
    }

    public SortedMap<Integer, Integer> getVersions() {
        return versionMap;
    }

    @Override
    public PofNavigator getPofNavigator(String fieldName) {
        return PofUtil.getPofNavigator(getClass(), fieldName);
    }

    @Override
    public PofExtractor getPofExtractor(String fieldName) {
        return PofUtil.getPofExtractor(getClass(), fieldName);
    }

    @Override
    public PofUpdater getPofUpdater(String fieldName) {
        return PofUtil.getPofUpdater(getClass(), fieldName);
    }
}

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
import java.util.SortedMap;


public interface EvolvableObject {
    int getDataVersion(int typeId);
    void setDataVersion(int typeId, int dataVersion);
    Binary getFutureData(int typeId);
    void setFutureData(int typeId, Binary futureData);
    SortedMap<Integer, Integer> getVersions();

    PofNavigator getPofNavigator(String fieldName);
    PofExtractor getPofExtractor(String fieldName);
    PofUpdater   getPofUpdater(String fieldName);
}

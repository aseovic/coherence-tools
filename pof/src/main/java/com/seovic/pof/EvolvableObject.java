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

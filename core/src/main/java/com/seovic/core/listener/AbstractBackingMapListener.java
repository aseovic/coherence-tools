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

package com.seovic.core.listener;


import com.tangosol.net.BackingMapManagerContext;
import com.tangosol.net.cache.CacheEvent;
import com.tangosol.util.AbstractMapListener;
import com.tangosol.util.Binary;
import com.tangosol.util.ConverterCollections;
import com.tangosol.util.ExternalizableHelper;
import com.tangosol.util.MapEvent;


/**
 * Abstract base class for backing map listeners.
 *
 * @author Aleksandar Seovic  2009.02.27
 */
@SuppressWarnings({"unchecked"})
public abstract class AbstractBackingMapListener<K, V>
        extends AbstractMapListener {

    private BackingMapManagerContext context;

    protected AbstractBackingMapListener(BackingMapManagerContext context) {
        this.context = context;
    }

    protected BackingMapManagerContext getContext() {
        return context;
    }

    protected void setContext(BackingMapManagerContext context) {
        this.context = context;
    }

    protected MapEvent convertFromInternal(MapEvent event) {
        return ConverterCollections.getMapEvent(
                event.getMap(),
                event,
                context.getKeyFromInternalConverter(),
                context.getValueFromInternalConverter());
    }

    protected K getKey(MapEvent event) {
        return (K) context.getKeyFromInternalConverter().convert(event.getKey());
    }

    protected V getOldValue(MapEvent event) {
        return (V) context.getValueFromInternalConverter().convert(event.getOldValue());
    }

    protected V getNewValue(MapEvent event) {
        return (V) context.getValueFromInternalConverter().convert(event.getNewValue());
    }

    protected boolean isEviction(MapEvent event) {
        return context.isKeyOwned(event.getKey())
                && event instanceof CacheEvent
                && ((CacheEvent) event).isSynthetic();
    }

    protected boolean isEntryWritePending(MapEvent event) {
    	if (event.getId() == MapEvent.ENTRY_DELETED || !context.isKeyOwned(event.getKey())) {
    		return false;
    	}
    	Object newValue = event.getNewValue();
    	return newValue instanceof Binary
               && context.isInternalValueDecorated(newValue, ExternalizableHelper.DECO_STORE);
    }

    protected boolean isDistribution(MapEvent event) {
        return !context.isKeyOwned(event.getKey());
    }
}

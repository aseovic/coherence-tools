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
import com.tangosol.net.CacheFactory;
import com.tangosol.util.MapEvent;


/**
 * Simple backing map listener that logs all inserts, updates and deletes.
 *
 * @author Aleksandar Seovic  2012.09.19
 */
public class LoggingBackingMapListener extends AbstractBackingMapListener {
    private final int logLevel;

    public LoggingBackingMapListener(BackingMapManagerContext context) {
        this(context, CacheFactory.LOG_INFO);
    }
    public LoggingBackingMapListener(BackingMapManagerContext context, int logLevel) {
        super(context);
        this.logLevel = logLevel;
    }

    @Override
    public void entryInserted(MapEvent evt) {
        CacheFactory.log("INSERTED: " + getNewValue(evt), logLevel);
    }

    @Override
    public void entryUpdated(MapEvent evt) {
        CacheFactory.log("UPDATED: " + getOldValue(evt) + " to " + getNewValue(evt), logLevel);
    }

    @Override
    public void entryDeleted(MapEvent evt) {
        CacheFactory.log("DELETED: " + getOldValue(evt), logLevel);
    }
}

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

package com.seovic.batch;


import com.seovic.core.objects.DynamicObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author Aleksandar Seovic  2009.11.05
 */
public class ExecutionContext
        extends DynamicObject {

    private transient ExecutorService localExecutor;
    private transient ExecutorService remoteExecutor;

    /**
     * {@inheritDoc}
     */
    protected Map<String, Object> createPropertyMap() {
        return new ConcurrentHashMap<String, Object>();
    }

    public ExecutorService getLocalExecutor() {
        if (localExecutor == null) {
            localExecutor = Executors.newCachedThreadPool();
        }
        return localExecutor;
    }

    public void setLocalExecutor(ExecutorService localExecutor) {
        this.localExecutor = localExecutor;
    }

    public ExecutorService getRemoteExecutor() {
        return remoteExecutor;
    }

    public void setRemoteExecutor(ExecutorService remoteExecutor) {
        this.remoteExecutor = remoteExecutor;
    }
}

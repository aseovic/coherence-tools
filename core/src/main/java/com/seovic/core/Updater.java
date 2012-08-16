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

package com.seovic.core;


import com.tangosol.util.ValueUpdater;


/**
 * Updater is used to update the target object.
 *
 * @author Aleksandar Seovic  2009.06.17
 */
public interface Updater
        extends ValueUpdater {
    /**
     * Update one or more of target object's properties using specified value.
     *
     * @param target object to update
     * @param value  value to update object with
     */
    void update(Object target, Object value);
}
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


/**
 * An interface that should be implemented by objects that can be updated.
 *
 * @author Aleksandar Seovic  2010.10.13
 */
public interface Updatable {
    /**
     * Update this object using specified updater and value.
     *
     * @param updater  an updater to use
     * @param value    a value to use for update
     */
    void update(Updater updater, Object value);
}

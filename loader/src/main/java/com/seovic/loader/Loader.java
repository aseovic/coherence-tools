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

package com.seovic.loader;


import java.io.Serializable;


/**
 * An interface that all loaders have to implement.
 *
 * @author Aleksandar Seovic  2009.09.29
 */
public interface Loader
        extends Serializable {
    /**
     * Load items from the Source into the Target.
     */
    void load();

    /**
     * Return mapping mode for the loader.
     *
     * @return mapping mode
     */
    MappingMode getMappingMode();

    /**
     * Set mapping mode for the loader.
     *
     * @param mode  mapping mode
     */
    void setMappingMode(MappingMode mode);
}

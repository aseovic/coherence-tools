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


import com.seovic.core.Extractor;
import com.seovic.core.Updater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;


/**
 * Asbtract base class for {@link Loader} implementations.
 *
 * @author Aleksandar Seovic  2012.04.06
 */
public abstract class AbstractLoader
        implements Loader {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractLoader.class);

    private MappingMode mode = MappingMode.AUTO;

    // ---- Loader implementation -------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void load() {
        Source source = getSource();
        Target target = getTarget();
        source.beginExport();
        target.beginImport();
        Set<String> propertyNames = mode.equals(MappingMode.AUTO)
                                 ? target.getPropertyNames()
                                 : source.getPropertyNames();

        for (Object sourceItem : source) {
            Object targetItem = target.createTargetInstance(source, sourceItem);
            for (String property : propertyNames) {
                try {
                    Extractor extractor = source.getExtractor(property);
                    Updater updater = target.getUpdater(property);
                    updater.update(targetItem, extractor.extract(sourceItem));
                }
                catch (RuntimeException e) {
                    LOG.error("Mapping error for property '" + property + "': " + e.getMessage(), e);
                    throw e;
                }
            }
            target.importItem(targetItem);
        }
        source.endExport();
        target.endImport();
    }

    @Override
    public MappingMode getMappingMode() {
        return mode;
    }

    @Override
    public void setMappingMode(MappingMode mode) {
        this.mode = mode;
    }

    // ---- abstract methods ------------------------------------------------

    /**
     * Return fully configured {@link Source} instance.
     *
     * @return Source instance
     */
    protected abstract Source getSource();

    /**
     * Return fully configured {@link Target} instance.
     *
     * @return Target instance
     */
    protected abstract Target getTarget();
}

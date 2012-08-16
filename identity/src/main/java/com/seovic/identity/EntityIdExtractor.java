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

package com.seovic.identity;


import com.seovic.core.Entity;
import java.io.Serializable;


/**
 * An {@link IdExtractor} that can be used with the classes implementing
 * {@link Entity} interface.
 *
 * @author ic  2009.06.09
 */
public class EntityIdExtractor<T>
        implements IdExtractor<T, Entity<T>>, Serializable {

    private static final long serialVersionUID = -1922209545583231451L;

    /**
     * {@inheritDoc}
     */
    public T extractId(Entity<T> entity) {
        return entity.getId();
    }
}

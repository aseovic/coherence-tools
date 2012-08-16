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


import com.tangosol.util.UUID;
import java.io.Serializable;


/**
 * UUID-based {@link IdGenerator} implementation.
 *
 * @author Aleksandar Seovic  2009.05.27
 */
public class UuidGenerator
        implements IdGenerator<UUID>, Serializable {

    private static final long serialVersionUID = -8960735400154938606L;

    /**
     * Generates a UUID-based identity.
     *
     * @return a new UUID value
     */
    public UUID generateId() {
        return new UUID();
    }
}

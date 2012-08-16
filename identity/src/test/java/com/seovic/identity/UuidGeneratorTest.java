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


import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Tests for {@link UuidGenerator}.
 *
 * @author Aleksandar Seovic  2008.11.24
 */
public class UuidGeneratorTest {
    @Test
    public void testIdGeneration() {
        IdGeneratorClient igc = new IdGeneratorClient(
                new UuidGenerator());

        assertEquals(100, igc.generateIdentities(1, 100).size());
        assertEquals(100, igc.generateIdentities(5, 20).size());
        assertEquals(100, igc.generateIdentities(10, 10).size());
    }
}

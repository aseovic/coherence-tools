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


import com.tangosol.net.NamedCache;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Tests for {@link CoherenceSequenceGenerator}.
 *
 * @author Aleksandar Seovic  2008.11.24
 */
public class CoherenceSequenceGeneratorTest {
    private static NamedCache SEQUENCES = CoherenceSequenceGenerator.SEQUENCES;

    @Before
    public void before() throws Exception {
        SEQUENCES.clear();
    }

    @Test
    public void testIdGeneration() {
        IdGeneratorClient igc = new IdGeneratorClient(
                new CoherenceSequenceGenerator("test", 20));

        assertEquals(100, igc.generateIdentities(1, 100).size());
        assertEquals(100, igc.generateIdentities(5, 20).size());
        assertEquals(100, igc.generateIdentities(10, 10).size());

        Sequence seq = (Sequence) SEQUENCES.get("test");
        assertEquals("test", seq.getName());
        assertEquals(300, seq.getLast());
    }

    @Test
    public void testIdGenerationWithoutBlockCaching() {
        IdGeneratorClient igc = new IdGeneratorClient(
                new CoherenceSequenceGenerator("test", 1));

        assertEquals(100, igc.generateIdentities(5, 20).size());

        Sequence seq = (Sequence) SEQUENCES.get("test");
        assertEquals("test", seq.getName());
        assertEquals(100, seq.getLast());
    }

    @Test
    public void testIdGenerationWithMultipleClients() {
        IdGeneratorClient igc1 = new IdGeneratorClient(
                new CoherenceSequenceGenerator("test", 10));
        IdGeneratorClient igc2 = new IdGeneratorClient(
                new CoherenceSequenceGenerator("test", 10));

        assertEquals(25, igc1.generateIdentities(5, 5).size());
        assertEquals(25, igc2.generateIdentities(5, 5).size());

        Sequence seq = (Sequence) SEQUENCES.get("test");
        assertEquals("test", seq.getName());
        assertEquals(60, seq.getLast());
    }
}

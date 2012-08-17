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

package com.seovic.core.aggregator;


import com.seovic.core.extractor.ExpressionExtractor;
import com.tangosol.net.NamedCache;
import com.tangosol.net.cache.WrapperNamedCache;
import com.tangosol.util.comparator.InverseComparator;
import com.tangosol.util.filter.AlwaysFilter;
import java.util.HashMap;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


@SuppressWarnings("unchecked")
public class TopAggregatorTest {
    private static final NamedCache test =
            new WrapperNamedCache(new HashMap(), "test");

    @Before
    public void clearCache()
            throws Exception {
        test.clear();
        for (int i = 1; i <= 100; i++) {
            test.put(i, new Score(i));
        }
    }

    @Test
    public void testTop10() {
        TopAggregator ta = new TopAggregator(new ExpressionExtractor("score"), null, 10);
        List<Integer> results = (List<Integer>) test.aggregate(AlwaysFilter.INSTANCE, ta);

        assertEquals(10, results.size());
        for (int i = 0; i < 10; i++) {
            assertEquals(i+1, results.get(i).intValue());
        }
    }

    @Test
    public void testBottom10() {
        TopAggregator ta = new TopAggregator(new ExpressionExtractor("score"), new InverseComparator(), 10);
        List<Integer> results = (List<Integer>) test.aggregate(AlwaysFilter.INSTANCE, ta);

        assertEquals(10, results.size());
        for (int i = 0, s = 100; i < 10; i++, s--) {
            assertEquals(s, results.get(i).intValue());
        }
    }

    public static class Score {
        private int score;

        public Score(int score) {
            this.score = score;
        }

        public int getScore() {
            return score;
        }
    }
}
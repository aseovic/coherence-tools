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


import com.tangosol.util.comparator.InverseComparator;
import java.util.Collection;
import org.junit.Test;

import static com.seovic.core.aggregator.TopAggregator.*;
import static org.junit.Assert.*;


/**
 * Tests for TopAggregator.SortedSizeLimitedList.
 */
public class SortedSizeLimitedListTest {
    @Test
    public void testWithoutComparator() {
        SortedSizeLimitedList l = new SortedSizeLimitedList(5, null);

        l.add(new KeyValuePair(6, "f"));
        l.add(new KeyValuePair(1, "a"));
        l.add(new KeyValuePair(8, "h"));
        l.add(new KeyValuePair(2, "b"));
        l.add(new KeyValuePair(4, "d"));
        l.add(new KeyValuePair(3, "c"));
        l.add(new KeyValuePair(5, "e"));
        l.add(new KeyValuePair(7, "g"));
        System.out.println(l);

        Collection res = l.toKeyList();
        assertEquals(5, res.size());
    }

    @Test
    public void testWithComparator() {
        TopAggregator.SortedSizeLimitedList l = new TopAggregator.SortedSizeLimitedList(5, new InverseComparator());

        l.add(new KeyValuePair(6, "f"));
        l.add(new KeyValuePair(1, "a"));
        l.add(new KeyValuePair(8, "h"));
        l.add(new KeyValuePair(2, "b"));
        l.add(new KeyValuePair(4, "d"));
        l.add(new KeyValuePair(3, "c"));
        l.add(new KeyValuePair(5, "e"));
        l.add(new KeyValuePair(7, "g"));
        System.out.println(l);

        Collection res = l.toKeyList();
        assertEquals(5, res.size());
    }
}

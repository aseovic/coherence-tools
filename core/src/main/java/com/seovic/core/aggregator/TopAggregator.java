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


import com.tangosol.io.ExternalizableLite;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import com.tangosol.util.ValueExtractor;
import com.tangosol.util.aggregator.AbstractAggregator;
import com.tangosol.util.extractor.AbstractExtractor;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


/**
 * Sample aggregator that retrieves top N keys from each node
 * and merges them into a final result.
 */
@SuppressWarnings("unchecked")
public class TopAggregator
        extends AbstractAggregator {

    private transient SortedSizeLimitedList results;

    private Comparator comparator;
    private int maxItems;

    public TopAggregator() {
    }

    public TopAggregator(ValueExtractor extractor, Comparator comparator, int maxItems) {
        super(new KeyValuePairExtractor(extractor));
        this.comparator = comparator;
        this.maxItems = maxItems;
    }

    protected void init(boolean isFinal) {
        results = new SortedSizeLimitedList(maxItems, comparator);
    }

    protected void process(Object o, boolean isFinal) {
        if (isFinal) {
            results.addAll((List) o);
        }
        else {
            results.add((KeyValuePair) o);
        }
    }

    protected Object finalizeResult(boolean isFinal) {
        if (isFinal) {
            // get a list of keys
            return results.toKeyList();
        }
        else {
            // get a list of KeyValuePair objects
            return results.toList();
        }
    }

    @Override
    public void readExternal(DataInput in) throws IOException {
        super.readExternal(in);
        comparator = (Comparator) readObject(in);
        maxItems   = in.readInt();
    }

    @Override
    public void writeExternal(DataOutput out) throws IOException {
        super.writeExternal(out);
        writeObject(out, comparator);
        out.writeInt(maxItems);
    }

    @Override
    public void readExternal(PofReader in) throws IOException {
        super.readExternal(in);
        comparator = (Comparator) in.readObject(10);
        maxItems   = in.readInt(11);
    }

    @Override
    public void writeExternal(PofWriter out) throws IOException {
        super.writeExternal(out);
        out.writeObject(10, comparator);
        out.writeInt(11, maxItems);
    }

    public static class KeyValuePair implements ExternalizableLite, PortableObject {
        private Object key;
        private Object value;

        private transient KeyValuePair previous;
        private transient KeyValuePair next;

        public KeyValuePair() {
        }

        public KeyValuePair(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        public Object getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public void insertBefore(KeyValuePair previous) {
            this.previous.next = previous;
            previous.previous = this.previous;
            previous.next = this;
            this.previous = previous;
        }

        public void remove() {
           this.previous.next = this.next;
           this.next.previous = this.previous;
        }

        public void readExternal(DataInput in) throws IOException {
            key = readObject(in);
            value = readObject(in);
        }

        public void writeExternal(DataOutput out) throws IOException {
            writeObject(out, key);
            writeObject(out, value);
        }

        public void readExternal(PofReader in) throws IOException {
            key = in.readObject(0);
            value = in.readObject(1);
        }

        public void writeExternal(PofWriter out) throws IOException {
            out.writeObject(0, key);
            out.writeObject(1, value);
        }

        @Override
        public String toString() {
            return "[" + key + ", " + value + "]";
        }
    }

    public static class KeyValuePairExtractor
            extends AbstractExtractor
            implements ExternalizableLite, PortableObject {

        private ValueExtractor valueExtractor;

        public KeyValuePairExtractor() {
        }

        public KeyValuePairExtractor(ValueExtractor valueExtractor) {
            this.valueExtractor = valueExtractor;
        }

        public Object extractFromEntry(Map.Entry entry) {
            return new KeyValuePair(entry.getKey(), valueExtractor.extract(entry.getValue()));
        }

        public void readExternal(DataInput in) throws IOException {
            valueExtractor = (ValueExtractor) readObject(in);
        }

        public void writeExternal(DataOutput out) throws IOException {
            writeObject(out, valueExtractor);
        }

        public void readExternal(PofReader in) throws IOException {
            valueExtractor = (ValueExtractor) in.readObject(0);
        }

        public void writeExternal(PofWriter out) throws IOException {
            out.writeObject(0, valueExtractor);
        }
    }

    public static class KeyValuePairComparator implements Comparator<KeyValuePair> {

        private Comparator comparator;

        public KeyValuePairComparator(Comparator comparator) {
            this.comparator = comparator;
        }

        public int compare(KeyValuePair o1, KeyValuePair o2) {
            Object v1 = o1.getValue();
            Object v2 = o2.getValue();

            if (comparator != null) {
                return comparator.compare(v1, v2);
            }
            else {
                try {
                    Comparable c1 = (Comparable) v1;
                    Comparable c2 = (Comparable) v2;

                    return c1.compareTo(c2);
                }
                catch (ClassCastException e) {
                    throw new IllegalArgumentException("Extracted values are not Comparable and explicit Comparator was not specified/");
                }
            }
        }
    }

    public static class SortedSizeLimitedList {

        private KeyValuePairComparator comparator;

        private int maxLength;
        private int length;

        private KeyValuePair head;

        public SortedSizeLimitedList(int maxLength, Comparator comparator) {
            this.maxLength = maxLength;
            this.comparator = new KeyValuePairComparator(comparator);
        }

        public void add(KeyValuePair el) {
            if (length < maxLength || comparator.compare(head.previous, el) > 0) {
                insert(el);
            }
        }

        public void addAll(List<KeyValuePair> elements) {
            if (head == null || comparator.compare(head.previous, elements.get(0)) > 0) {
                for (KeyValuePair element : elements) {
                    add(element);
                }
            }
        }

        public List<KeyValuePair> toList() {
            ArrayList<KeyValuePair> list = new ArrayList<KeyValuePair>(length);
            KeyValuePair cur = head;
            do {
                list.add(cur);
                cur = cur.next;
            } while (cur != head);

            return list;
        }

        public List toKeyList() {
            ArrayList list = new ArrayList(length);
            KeyValuePair cur = head;
            do {
                list.add(cur.getKey());
                cur = cur.next;
            } while (cur != head);

            return list;
        }

        private void insert(KeyValuePair el) {
            if (length == 0) {       // empty
                head = el;
                head.previous = head;
                head.next = head;
            }
            else {                   // general case
                KeyValuePair cur = head;
                boolean inserted = false;
                do {
                    if (comparator.compare(cur, el) > 0) {
                        cur.insertBefore(el);
                        if (cur == head) {
                            head = el;
                        }
                        inserted = true;
                        break;
                    }
                    cur = cur.next;
                } while (cur != head);

                if (!inserted) {
                    head.insertBefore(el);
                }
            }

            if (++length > maxLength) {
                head.previous.remove();
                length--;
            }
        }

        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer("{ length: " + length + "  ");

            KeyValuePair cur = head;
            do {
                sb.append(cur).append(" ");
                cur = cur.next;
            } while (cur != head);

            return sb.append(" }").toString();
        }
    }
}

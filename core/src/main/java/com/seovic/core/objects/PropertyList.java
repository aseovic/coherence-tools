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

package com.seovic.core.objects;


import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * @author Aleksandar Seovic  2010.10.11
 */
public class PropertyList implements Iterable<PropertySpec>, Serializable, PortableObject {
    private List<PropertySpec> properties;

    public PropertyList() {
        properties = new LinkedList<PropertySpec>();
    }

    public PropertyList(PropertySpec... properties) {
        this(Arrays.asList(properties));
    }

    public PropertyList(List<PropertySpec> properties) {
        this.properties = properties;
    }

    public static PropertyList fromString(String sPropertyList) {
        PropertyList properties = new PropertyList();
        sPropertyList = sPropertyList.trim();

        StringBuilder name = new StringBuilder();
        PropertyList list = null;

        int pos = 0;
        while (pos < sPropertyList.length()) {
            char c = sPropertyList.charAt(pos++);
            if (c == ',') {
                // terminate property
                properties.add(new PropertySpec(name.toString(), list));
                name = new StringBuilder();
                list = null;
            }
            else if (c == ':') {
                // property has inner property list
                pos++; // skip first open parenthesis
                int parenCount = 1;
                StringBuilder innerList = new StringBuilder();
                while (parenCount > 0 && pos < sPropertyList.length()) {
                    char s = sPropertyList.charAt(pos++);
                    if (s == ')') {
                        parenCount--;
                    }
                    else if (s == '(') {
                        parenCount++;
                    }

                    if (parenCount > 0) {
                        innerList.append(s);
                    }
                }

                list = fromString(innerList.toString());
            }
            else if (c != ' ') {
                name.append(c);
            }
        }

        if (name.length() > 0) {
            properties.add(new PropertySpec(name.toString(), list));
        }

        return properties;
    }

    public void add(PropertySpec property) {
        properties.add(property);
    }

    List<PropertySpec> getProperties() {
        return properties;
    }

    PropertySpec first() {
        if (properties.size() > 0) {
            return properties.get(0);
        }
        return null;
    }

    @Override
    public Iterator<PropertySpec> iterator() {
        return properties.iterator();
    }

    @Override
    public void readExternal(PofReader reader) throws IOException {
        reader.readCollection(0, properties);
    }

    @Override
    public void writeExternal(PofWriter writer) throws IOException {
        writer.writeCollection(0, properties, PropertySpec.class);
    }
}

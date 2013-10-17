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


import com.seovic.core.Extractor;
import com.seovic.core.extractor.MvelExtractor;
import org.mvel2.PropertyAccessException;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import java.io.IOException;
import java.io.Serializable;


/**
 * @author Aleksandar Seovic  2010.10.11
 */
public class PropertySpec
        implements Serializable, PortableObject {

    private static final long serialVersionUID = 5440831276720491115L;

    private String name;
    private PropertyList propertyList;

    private transient Extractor extractor;

    public PropertySpec() {
    }

    public PropertySpec(String name) {
        this(name, null);
    }

    public PropertySpec(String name, PropertyList propertyList) {
        this.name = name;
        this.propertyList = propertyList;
    }

    public static PropertySpec fromString(String propertySpec) {
        return PropertyList.fromString(propertySpec).first();    
    }

    public String getName() {
        return name;
    }

    public PropertyList getPropertyList() {
        return propertyList;
    }

    public Object getValue(Object target) {
        try {
            return getExtractor().extract(target);
        }
        catch (PropertyAccessException e) {
            return null;
        }
    }

    protected Extractor getExtractor() {
        if (extractor == null) {
            extractor = new MvelExtractor(name);
        }
        return extractor;
    }

    @Override
    public void readExternal(PofReader reader) throws IOException {
        name = reader.readString(0);
        propertyList = (PropertyList) reader.readObject(1);
    }

    @Override
    public void writeExternal(PofWriter writer) throws IOException {
        writer.writeString(0, name);
        writer.writeObject(1, propertyList);
    }
}

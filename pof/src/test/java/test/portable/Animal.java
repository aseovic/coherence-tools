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

package test.portable;


import com.seovic.core.Equatable;
import com.tangosol.io.AbstractEvolvable;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import java.io.IOException;
import java.io.Serializable;


/**
 * @author Aleksandar Seovic  2012.05.27
 */
public class Animal
        extends AbstractEvolvable
        implements PortableObject, Serializable, Equatable {
    protected String species;

    public Animal() {
    }

    @Override
    public int getImplVersion() {
        return 1;
    }

    public Animal(String species) {
        this.species = species;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public void readExternal(PofReader in) throws IOException {
        species = in.readString(0);
    }

    public void writeExternal(PofWriter out) throws IOException {
        out.writeString(0, species);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || getClass().equals(o.getClass()) && matches(o);
    }

    @Override
    public boolean matches(Object o) {
        if (o instanceof Animal) {
            Animal animal = (Animal) o;
            return species.equals(animal.species);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return species.hashCode();
    }

    @Override
    public String toString() {
        return "Animal{" +
                "species='" + species + '\'' +
                '}';
    }
}

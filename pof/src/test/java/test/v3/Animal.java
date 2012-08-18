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

package test.v3;


import com.seovic.core.Equatable;
import com.seovic.pof.AbstractEvolvableObject;
import com.seovic.pof.annotations.Portable;
import com.seovic.pof.annotations.PortableType;


/**
 * @author Aleksandar Seovic  2012.06.03
 */
@PortableType(id = 3, version = 1)
public class Animal
        extends AbstractEvolvableObject
        implements Equatable {
    @Portable(since = 1)
    protected String species;

    public Animal(String species) {
        this.species = species;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
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
        return "Animal.v3{" +
                "species='" + species + '\'' +
                '}';
    }
}

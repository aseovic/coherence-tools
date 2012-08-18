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


import com.seovic.pof.AbstractEvolvableObject;
import com.seovic.pof.annotations.Portable;
import com.seovic.pof.annotations.PortableType;

import java.util.HashSet;
import java.util.Set;


@SuppressWarnings("unchecked")
@PortableType(id = 4, version = 1)
public class Zoo extends AbstractEvolvableObject {
    @Portable(since = 1) private Address address;
    @Portable(since = 1) private Set<test.v2.Animal> animals = new HashSet<test.v2.Animal>();

    public Zoo() {
    }

    public Zoo(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

    public Set<test.v2.Animal> getAnimals() {
        return animals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Zoo zoo = (Zoo) o;

        if (address != null ? !address.equals(zoo.address) : zoo.address != null) return false;
        if (animals != null ? !animals.equals(zoo.animals) : zoo.animals != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + (animals != null ? animals.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Zoo{" +
                "address=" + address +
                ", animals=" + animals +
                '}';
    }
}

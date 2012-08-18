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

package test.v2;


import com.seovic.pof.annotations.PortableType;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;

import java.io.IOException;


@PortableType(id = 1, version = 2)
public class Pet
        extends Animal {
    protected String name;
    protected int age;

    public Pet() {}

    public Pet(String species, String name, int age) {
        super(species);
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    private void readExternal(PofReader in) throws IOException {
        name = in.readString(0);
        if (in.getVersionId() > 1) {
            age = in.readInt(1);
        }
    }

    private void writeExternal(PofWriter out) throws IOException {
        out.writeString(0, name);
        out.writeInt(1, age);
    }

    @Override
    public boolean matches(Object o) {
        if (o instanceof Pet) {
            Pet pet = (Pet) o;
            return super.matches(pet) && age == pet.age && name.equals(pet.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + age;
        return result;
    }

    @Override
    public String toString() {
        return "Pet.v2{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", species=" + species +
                '}';
    }
}

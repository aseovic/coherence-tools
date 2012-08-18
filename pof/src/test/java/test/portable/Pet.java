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


import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import java.io.IOException;


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

    public void readExternal(PofReader in) throws IOException {
        super.readExternal(in.createNestedPofReader(0));
        name = in.readString(1);
        age = in.readInt(2);
    }

    public void writeExternal(PofWriter out) throws IOException {
        super.writeExternal(out.createNestedPofWriter(0));
        out.writeString(1, name);
        out.writeInt(2, age);
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
        return "Pet{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", species=" + species +
                '}';
    }
}

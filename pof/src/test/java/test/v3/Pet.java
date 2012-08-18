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


import com.seovic.pof.annotations.Portable;
import com.seovic.pof.annotations.PortableType;


@PortableType(id = 1, version = 2)
public class Pet
        extends Animal {
    @Portable(since = 1)
    protected String name;
    @Portable(since = 2)
    protected int age;

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
        return "Pet.v3{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", species=" + species +
                '}';
    }
}

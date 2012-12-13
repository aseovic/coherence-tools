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
import com.tangosol.net.CacheFactory;

import java.io.IOException;

import test.Color;


@PortableType(id = 2, version = 2)
public class Dog
        extends Pet {
    private String breed;
    private Color color;

    public Dog() {}

    public Dog(String name, int age, String breed, Color color) {
        super("Canis lupus familiaris", name, age);
        this.breed = breed;
        this.color = color;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    private void readExternal(PofReader in) throws IOException {
        if (in.getVersionId() >= 1) {
            breed = in.readString(0);
        }
        if (in.getVersionId() >= 2) {
            color = (Color) in.readObject(1);
        }
    }

    private void writeExternal(PofWriter out) throws IOException {
        out.writeString(0, breed);
        out.writeObject(1, color);
    }

    @Override
    public boolean matches(Object o) {
        if (o instanceof Dog) {
            Dog dog = (Dog) o;
            return super.matches(dog) && breed.equals(dog.breed) && color.equals(dog.color);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + breed.hashCode();
        result = 31 * result + color.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Dog.v2{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", species='" + species + '\'' +
                ", breed='" + breed + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}

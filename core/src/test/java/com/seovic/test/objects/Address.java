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

package com.seovic.test.objects;


import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import java.io.IOException;
import java.io.Serializable;


/**
 * Simple Address class that can be used within tests.
 *
 * @author Aleksandar Seovic  2009.09.20
 */
public class Address
        implements Serializable, PortableObject {

    private static final long serialVersionUID = -9059893618926097279L;

    private String street;
    private String city;
    private String country;

    public Address() {}

    public Address(String street, String city, String country) {
        this.street = street;
        this.city = city;
        this.country = country;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


    public void readExternal(PofReader reader)
            throws IOException {
        street = reader.readString(0);
        city = reader.readString(1);
        country = reader.readString(2);
    }

    public void writeExternal(PofWriter writer)
            throws IOException {
        writer.writeString(0, street);
        writer.writeString(1, city);
        writer.writeString(2, country);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Address address = (Address) o;

        return !(city != null
                 ? !city.equals(address.city)
                 : address.city != null)
               && !(country != null
                    ? !country.equals(address.country)
                    : address.country != null)
               && !(street != null
                    ? !street.equals(address.street)
                    : address.street != null);
    }

    @Override
    public int hashCode() {
        int result = street != null ? street.hashCode() : 0;
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Address{" +
               "street='" + street + '\'' +
               ", city='" + city + '\'' +
               ", country='" + country + '\'' +
               '}';
    }
}

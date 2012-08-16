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
 * Simple data object representing a city that can be used for testing.
 *
 * @author Aleksandar Seovic  2009.06.07
 */
public class City
        implements PortableObject, Serializable, Comparable
    {
    // ---- data members ----------------------------------------------------

    private long   m_id;
    private String m_countryCode;
    private String m_name;


    // ---- constructors ----------------------------------------------------

    /**
     * Deserialization constructor (for internal use only).
     */
    public City()
        {
        }

    /**
     * Construct a <tt>City</tt> intance.
     *
     * @param id           city id
     * @param countryCode  country code
     * @param name         city name
     */
    public City(long id, String countryCode, String name)
        {
        m_id          = id;
        m_countryCode = countryCode;
        m_name        = name;
        }


    // ---- getters and setters ---------------------------------------------

    public long getId()
        {
        return m_id;
        }

    public String getCountryCode()
        {
        return m_countryCode;
        }

    public String getName()
        {
        return m_name;
        }

    public void setName(String name)
        {
        m_name = name;
        }


    // ---- PortableObject implementation -----------------------------------

    public void readExternal(PofReader reader)
            throws IOException
        {
        m_id          = reader.readLong(0);
        m_countryCode = reader.readString(1);
        m_name        = reader.readString(2);
        }

    public void writeExternal(PofWriter writer)
            throws IOException
        {
        writer.writeLong(  0, m_id);
        writer.writeString(1, m_countryCode);
        writer.writeString(2, m_name);
        }


    // ---- Object methods implementation -----------------------------------

    @Override
    public boolean equals(Object o)
        {
        if (this == o)
            {
            return true;
            }
        if (o == null || getClass() != o.getClass())
            {
            return false;
            }

        City city = (City) o;

        return m_id == city.m_id
               && m_countryCode.equals(city.m_countryCode)
               && m_name.equals(city.m_name);
        }

    @Override
    public int hashCode()
        {
        int result = (int) (m_id ^ (m_id >>> 32));
        result = 31 * result + m_countryCode.hashCode();
        result = 31 * result + m_name.hashCode();
        return result;
        }

    @Override
    public String toString()
        {
        return "City{" +
               "id=" + m_id +
               ", countryCode='" + m_countryCode + '\'' +
               ", name='" + m_name + '\'' +
               '}';
        }


    // ---- Comparable implementation ---------------------------------------

    public int compareTo(Object o)
        {
        City other = (City) o;
        return m_name.compareTo(other.m_name);
        }
    }

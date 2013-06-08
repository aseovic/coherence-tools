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


import com.seovic.core.Entity;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;

import java.io.IOException;
import java.io.Serializable;


public class Country
        implements Entity<String>, PortableObject, Serializable, Comparable {

    private static final long serialVersionUID = -8366938900371621512L;

    // ---- data members ----------------------------------------------------

    private String code;
    private String name;
    private String formalName;
    private String capital;
    private String currencySymbol;
    private String currencyName;
    private String telephonePrefix;
    private String domain;


    // ---- constructors ----------------------------------------------------

    public Country() {
    }

    public Country(String code, String name) {
        this.code = code;
        this.name = name;
    }


    // ---- Entity implementation -------------------------------------------

    public String getId() {
        return code;
    }


    // ---- getters and setters ---------------------------------------------


    public void setId(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormalName() {
        return formalName;
    }

    public void setFormalName(String formalName) {
        this.formalName = formalName;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getTelephonePrefix() {
        return telephonePrefix;
    }

    public void setTelephonePrefix(String telephonePrefix) {
        this.telephonePrefix = telephonePrefix;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }


    // ---- PortableObject implementation -----------------------------------

    public void readExternal(PofReader pofReader)
            throws IOException {
        code = pofReader.readString(0);
        name = pofReader.readString(1);
        formalName = pofReader.readString(2);
        capital = pofReader.readString(3);
        currencySymbol = pofReader.readString(4);
        currencyName = pofReader.readString(5);
        telephonePrefix = pofReader.readString(6);
        domain = pofReader.readString(7);
    }

    public void writeExternal(PofWriter pofWriter)
            throws IOException {
        pofWriter.writeString(0, code);
        pofWriter.writeString(1, name);
        pofWriter.writeString(2, formalName);
        pofWriter.writeString(3, capital);
        pofWriter.writeString(4, currencySymbol);
        pofWriter.writeString(5, currencyName);
        pofWriter.writeString(6, telephonePrefix);
        pofWriter.writeString(7, domain);
    }


    // ---- Object methods implementation -----------------------------------

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Country country = (Country) o;

        return !(capital != null
                 ? !capital.equals(country.capital)
                 : country.capital != null)
               && code.equals(country.code)
               && !(currencyName != null
                    ? !currencyName.equals(country.currencyName)
                    : country.currencyName != null)
               && !(currencySymbol != null
                    ? !currencySymbol.equals(country.currencySymbol)
                    : country.currencySymbol != null)
               && !(domain != null
                    ? !domain.equals(country.domain)
                    : country.domain != null)
               && !(formalName != null
                    ? !formalName.equals(country.formalName)
                    : country.formalName != null)
               && name.equals(country.name)
               && !(telephonePrefix != null
                    ? !telephonePrefix.equals(country.telephonePrefix)
                    : country.telephonePrefix != null);
    }

    public int hashCode() {
        return code.hashCode();
    }

    public String toString() {
        return "Country(" +
               "Code = " + code + ", " +
               "Name = " + name + ", " +
               "FormalName = " + formalName + ", " +
               "Capital = " + capital + ", " +
               "CurrencySymbol = " + currencySymbol + ", " +
               "CurrencyName = " + currencyName + ", " +
               "TelephonePrefix = " + telephonePrefix + ", " +
               "Domain = " + domain + ")";
    }


    // ---- Comparable implementation ---------------------------------------

    public int compareTo(Object o) {
        Country other = (Country) o;
        return this.name.compareTo(other.name);
    }
}

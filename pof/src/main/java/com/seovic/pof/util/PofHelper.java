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

package com.seovic.pof.util;


import com.tangosol.io.pof.RawDate;
import com.tangosol.io.pof.RawDateTime;
import com.tangosol.io.pof.RawTime;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * @author Aleksandar Seovic  2011.12.16
 */
public class PofHelper {
    public static RawDateTime toRawDateTime(XMLGregorianCalendar dateTime) {
        return dateTime == null ? null : new RawDateTime(toRawDate(dateTime), toRawTime(dateTime));
    }

    public static RawDate toRawDate(XMLGregorianCalendar date) {
        return date == null ? null : new RawDate(date.getYear(), date.getMonth(), date.getDay());
    }

    public static RawTime toRawTime(XMLGregorianCalendar time) {
        if (time == null) return null;

        int hourOffset   = 0;
        int minuteOffset = 0;

        int tz = time.getTimezone();
        if (tz != DatatypeConstants.FIELD_UNDEFINED) {
            hourOffset   = tz / 60;
            minuteOffset = tz % 60;
        }

        return new RawTime(time.getHour(),
                           time.getMinute(),
                           time.getSecond(),
                           time.getMillisecond() * 1000000,
                           hourOffset, minuteOffset);
    }

    public static XMLGregorianCalendar fromRawDateTime(RawDateTime dt) {
        if (dt == null) return null;

        try {
            DatatypeFactory factory = DatatypeFactory.newInstance();
            RawDate d = dt.getRawDate();
            RawTime t = dt.getRawTime();
            return factory.newXMLGregorianCalendar(d.getYear(), d.getMonth(), d.getDay(),
                                                   t.getHour(), t.getMinute(), t.getSecond(), t.getNano() / 1000000,
                                                   t.getHourOffset() * 60 + t.getMinuteOffset());
        }
        catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static XMLGregorianCalendar fromRawDate(RawDate d) {
        if (d == null) return null;

        try {
            DatatypeFactory factory = DatatypeFactory.newInstance();
            return factory.newXMLGregorianCalendar(d.getYear(), d.getMonth(), d.getDay(), 0, 0, 0, 0, 0);
        }
        catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static XMLGregorianCalendar fromRawTime(RawTime t) {
        if (t == null) return null;
        
        try {
            DatatypeFactory factory = DatatypeFactory.newInstance();
            return factory.newXMLGregorianCalendar(0, 0, 0,
                                                   t.getHour(), t.getMinute(), t.getSecond(), t.getNano() / 1000000,
                                                   t.getHourOffset() * 60 + t.getMinuteOffset());
        }
        catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}

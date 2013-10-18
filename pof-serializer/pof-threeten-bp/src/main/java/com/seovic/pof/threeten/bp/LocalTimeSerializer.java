package com.seovic.pof.threeten.bp;


import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofSerializer;
import com.tangosol.io.pof.PofWriter;

import java.io.IOException;

import org.threeten.bp.LocalTime;


/**
 * PofSerializer implementation for LocalTime class.
 *
 * @author Aleksandar Seovic  2013.10.01
 */
public class LocalTimeSerializer
        implements PofSerializer
    {
    public void serialize(PofWriter out, Object o)
            throws IOException
        {
        LocalTime lt = (LocalTime) o;
        out.writeInt(0, lt.getHour());
        out.writeInt(1, lt.getMinute());
        out.writeInt(2, lt.getSecond());
        out.writeInt(3, lt.getNano());
        out.writeRemainder(null);
        }

    public Object deserialize(PofReader in)
            throws IOException
        {
        LocalTime lt = LocalTime.of(in.readInt(0), in.readInt(1), in.readInt(2), in.readInt(3));
        in.readRemainder();
        return lt;
        }
    }

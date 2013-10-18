package com.seovic.pof.threeten.bp;


import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofSerializer;
import com.tangosol.io.pof.PofWriter;

import java.io.IOException;

import org.threeten.bp.LocalDateTime;


/**
 * PofSerializer implementation for LocalDateTime class.
 *
 * @author Aleksandar Seovic  2013.10.01
 */
public class LocalDateTimeSerializer
        implements PofSerializer
    {
    public void serialize(PofWriter out, Object o)
            throws IOException
        {
        LocalDateTime ldt = (LocalDateTime) o;
        out.writeInt(0, ldt.getYear());
        out.writeInt(1, ldt.getMonthValue());
        out.writeInt(2, ldt.getDayOfMonth());
        out.writeInt(3, ldt.getHour());
        out.writeInt(4, ldt.getMinute());
        out.writeInt(5, ldt.getSecond());
        out.writeInt(6, ldt.getNano());
        out.writeRemainder(null);
        }

    public Object deserialize(PofReader in)
            throws IOException
        {
        LocalDateTime ldt = LocalDateTime.of(
                in.readInt(0), in.readInt(1), in.readInt(2),
                in.readInt(3), in.readInt(4), in.readInt(5), in.readInt(6));
        in.readRemainder();
        return ldt;
        }
    }

package com.seovic.pof.threeten.bp;


import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofSerializer;
import com.tangosol.io.pof.PofWriter;

import java.io.IOException;

import org.threeten.bp.LocalDate;


/**
 * PofSerializer implementation for LocalDate class.
 *
 * @author Aleksandar Seovic  2013.10.01
 */
public class LocalDateSerializer
        implements PofSerializer
    {
    public void serialize(PofWriter out, Object o)
            throws IOException
        {
        LocalDate ld = (LocalDate) o;
        out.writeInt(0, ld.getYear());
        out.writeInt(1, ld.getMonthValue());
        out.writeInt(2, ld.getDayOfMonth());
        out.writeRemainder(null);
        }

    public Object deserialize(PofReader in)
            throws IOException
        {
        LocalDate ld = LocalDate.of(in.readInt(0), in.readInt(1), in.readInt(2));
        in.readRemainder();
        return ld;
        }
    }

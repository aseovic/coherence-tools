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

package com.seovic.core.io;


import com.tangosol.io.WrapperInputStream;
import com.tangosol.util.Binary;
import com.tangosol.util.BinaryWriteBuffer;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


/**
 * @author Aleksandar Seovic  2012.06.20
 */
public abstract class StreamUtils {
    public static Binary readAsBinary(String fileName) {
        try {
            return readAsBinary(new FileInputStream(fileName));
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Binary readAsBinary(InputStream in) {
        try {
            BinaryWriteBuffer buf = new BinaryWriteBuffer(4096);
            buf.getBufferOutput().writeStream(new WrapperInputStream(in));
            return buf.toBinary();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

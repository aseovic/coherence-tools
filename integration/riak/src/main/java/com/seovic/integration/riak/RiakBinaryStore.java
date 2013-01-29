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

package com.seovic.integration.riak;


import com.basho.riak.pbc.KeySource;
import com.basho.riak.pbc.RiakClient;
import com.basho.riak.pbc.RiakObject;

import com.google.protobuf.ByteString;

import com.tangosol.io.BinaryStore;
import com.tangosol.util.Binary;
import com.tangosol.util.Disposable;

import java.io.IOException;
import java.util.Iterator;


/**
 * @author Aleksandar Seovic  2012.09.19
 */
public class RiakBinaryStore
        implements BinaryStore, Disposable {

    private final RiakClient client;
    private final ByteString bucket;

    public RiakBinaryStore(RiakClient client, String bucket) {
        this.client = client;
        this.bucket = ByteString.copyFromUtf8(bucket);
    }

    @Override
    public Binary load(Binary binKey) {
        try {
            ByteString key = ByteString.copyFrom(binKey.toByteArray());
            RiakObject[] response = client.fetch(bucket, key);
            if (response.length >= 1) {
                return new Binary(response[0].getValue().toByteArray());
            }
            return null;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void store(Binary binKey, Binary binValue) {
        try {
            ByteString key   = ByteString.copyFrom(binKey.toByteArray());
            ByteString value = ByteString.copyFrom(binValue.toByteArray());

            client.store(new RiakObject(bucket, key, value));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void erase(Binary binKey) {
        try {
            ByteString key = ByteString.copyFrom(binKey.toByteArray());
            client.delete(bucket, key);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void eraseAll() {
        try {
            KeySource keys = client.listKeys(bucket);
            Iterator<ByteString> it = keys.iterator();
            while (it.hasNext()) {
                client.delete(bucket, it.next());
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterator keys() {
        try {
            final KeySource keys = client.listKeys(bucket);
            final Iterator<ByteString> it = keys.iterator();
            return new Iterator<Binary>() {
                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public Binary next() {
                    return new Binary(it.next().toByteArray());
                }

                @Override
                public void remove() {
                    it.remove();
                }
            };
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void dispose() {
        client.shutdown();
    }
}

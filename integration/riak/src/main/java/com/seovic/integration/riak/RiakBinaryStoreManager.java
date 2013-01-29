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


import com.basho.riak.pbc.RiakClient;

import com.google.protobuf.ByteString;

import com.tangosol.io.BinaryStore;
import com.tangosol.io.BinaryStoreManager;

import com.tangosol.run.xml.XmlConfigurable;
import com.tangosol.run.xml.XmlElement;
import java.io.IOException;


/**
 * A store manager implementation that allows Riak to be used as a backing map.
 *
 * @author Aleksandar Seovic  2012.09.19
 */
public class RiakBinaryStoreManager
        implements BinaryStoreManager, XmlConfigurable {
    public static final String DEFAULT_HOST = "localhost";
    public static final int    DEFAULT_PORT = 8087;

    private XmlElement xmlConfig;
    private String host;
    private int port;
    private String bucket;

    @Override
    public BinaryStore createBinaryStore() {
        try {
            return new RiakBinaryStore(new RiakClient(host, port), bucket);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroyBinaryStore(BinaryStore store) {
    }

    @Override
    public XmlElement getConfig() {
        return xmlConfig;
    }

    @Override
    public void setConfig(XmlElement xml) {
        xmlConfig = xml;
        this.host = xml.getSafeElement("host").getString(DEFAULT_HOST);
        this.port = xml.getSafeElement("port").getInt(DEFAULT_PORT);
        this.bucket = xml.getElement("bucket").getString();
    }
}

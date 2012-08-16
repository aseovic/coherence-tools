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

package com.seovic.loader;


import com.seovic.loader.source.XmlSource;
import com.seovic.loader.target.CoherenceCacheTarget;
import com.tangosol.net.NamedCache;
import java.io.Reader;


/**
 * Convenience class that loads data from the XML file into Coherence cache
 * using default settings.
 *
 * @author Aleksandar Seovic  2009.09.29
 */
public class XmlToCoherence
        extends AbstractDelegatingLoader
    {
    // ---- constructors ----------------------------------------------------

    /**
     * Construct XmlToCoherence loader instance.
     *
     * @param xmlResource  XML resource to read items from
     * @param cacheName    the name of the Coherence cache to import objects into
     * @param itemClass    target item class
     */
    public XmlToCoherence(String xmlResource, String cacheName, Class itemClass)
        {
        Source source = new XmlSource(xmlResource);
        Target target = new CoherenceCacheTarget(cacheName, itemClass);
        setLoader(new DefaultLoader(source, target));
        }

    /**
     * Construct XmlToCoherence loader instance.
     *
     * @param xmlResource  XML resource to read items from
     * @param cache        Coherence cache to import objects into
     * @param itemClass    target item class
     */
    public XmlToCoherence(String xmlResource, NamedCache cache, Class itemClass)
        {
        Source source = new XmlSource(xmlResource);
        Target target = new CoherenceCacheTarget(cache, itemClass);
        setLoader(new DefaultLoader(source, target));
        }

    /**
     * Construct XmlToCoherence loader instance.
     *
     * @param xmlReader  XML file reader
     * @param cache      Coherence cache to import objects into
     * @param itemClass  target item class
     */
    public XmlToCoherence(Reader xmlReader, NamedCache cache, Class itemClass)
        {
        Source source = new XmlSource(xmlReader);
        Target target = new CoherenceCacheTarget(cache, itemClass);
        setLoader(new DefaultLoader(source, target));
        }


    // ---- main method -----------------------------------------------------

    public static void main(String[] args)
            throws Exception
        {
        if (args.length < 3)
            {
            System.out.println("Usage: java com.seovic.coherence.loader.XmlToCoherence <xmlFile> <cacheName> <itemClass>");
            System.exit(0);
            }

        new XmlToCoherence(args[0], args[1], Class.forName(args[2])).load();
        }
    }
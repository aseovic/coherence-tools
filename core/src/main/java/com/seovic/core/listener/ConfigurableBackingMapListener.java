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

package com.seovic.core.listener;


import com.tangosol.net.BackingMapManagerContext;
import com.tangosol.util.MapEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Configurable backing map listener that uses Spring to configure
 * cache-specific listeners that should handle map events.
 * <p/>
 * By default, listener definitions are loaded from the
 * <tt>backing-map-listener-context.xml</tt> configuration file, which should be
 * located in the classpath. Each cache-specific listener defined within that
 * file should extend {@link ManagedBackingMapListener} class.
 * 
 * @author Aleksandar Seovic  2009.06.30
 */
public class ConfigurableBackingMapListener
        extends AbstractBackingMapListener {
    // ---- configuration context -------------------------------------------

    private static final ApplicationContext s_ctx =
            new ClassPathXmlApplicationContext("backing-map-listener-context.xml");


    // ---- data members ----------------------------------------------------

    private ManagedBackingMapListener listener;


    // ---- constructors ----------------------------------------------------

    /**
     * Construct <tt>ConfigurableBackingMapListener</tt> instance.
     *
     * @param context    backing map manager context
     * @param cacheName  name of the cache to set up listener for
     */
    public ConfigurableBackingMapListener(BackingMapManagerContext context, String cacheName) {
        super(context);
        if (s_ctx.containsBean(cacheName)) {
            listener = (ManagedBackingMapListener) s_ctx.getBean(cacheName);
            listener.setContext(getContext());
        }
        else {
            listener = new ManagedBackingMapListener(context);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void entryInserted(MapEvent event) {
        listener.entryInserted(event);
    }

    /**
     * {@inheritDoc}
     */
    public void entryUpdated(MapEvent event) {
        listener.entryUpdated(event);
    }

    /**
     * {@inheritDoc}
     */
    public void entryDeleted(MapEvent event) {
        listener.entryDeleted(event);
    }
}

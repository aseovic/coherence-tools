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

package com.seovic.loader.target;


import com.seovic.core.Updater;
import com.seovic.loader.Target;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;


/**
 * Abstract base class for {@link Target} implementations.
 *
 * @author Aleksandar Seovic/Ivan Cikic  2009.06.15
 */
public abstract class AbstractBaseTarget
        implements Target
    {
    // ---- constructors ----------------------------------------------------

    /**
     * Default constructor.
     */
    protected AbstractBaseTarget()
        {
        m_updaters = new HashMap<String, Updater>();
        }


    // ---- abstract methods ------------------------------------------------

    /**
     * Create default updater for the specified property.
     *
     * @param propertyName  property to create an updater for
     *
     * @return property updater instance
     */
    protected abstract Updater createDefaultUpdater(String propertyName);


    // ---- Source implementation -------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void beginImport()
        {
        // default implementation
        }

    /**
     * {@inheritDoc}
     */
    public void endImport()
        {
        // default implementation
        }

    /**
     * {@inheritDoc}
     */
    public Updater getUpdater(String propertyName)
        {
        Updater updater = m_updaters.get(propertyName);
        if (updater == null)
            {
            updater = createDefaultUpdater(propertyName);
            m_updaters.put(propertyName, updater);
            }
        return updater;
        }

    /**
     * {@inheritDoc}
     */
    public void setUpdater(String targetProperty, Updater updater)
        {
        m_updaters.put(targetProperty, updater);
        }


    // ---- helper methods --------------------------------------------------

    /**
     * Returns a list of all writeable properties of a class.
     *
     * @param cls  class to obtain a list of writeable properties for
     *
     * @return a list of writeable properties of the specified class
     */
    protected static List<PropertyDescriptor> getWriteableProperties(Class cls)
        {
        BeanWrapper beanWrapper = new BeanWrapperImpl(cls);
        List<PropertyDescriptor> writeableProperties =
                new ArrayList<PropertyDescriptor>();
        PropertyDescriptor[] props = beanWrapper.getPropertyDescriptors();
        for (PropertyDescriptor prop : props)
            {
            if (isWriteable(prop))
                {
                writeableProperties.add(prop);
                }
            }
        return writeableProperties;
        }

    /**
     * Return true if the specified property is writeable, false otherwise.
     *
     * @param property  property to check
     *
     * @return true if the specified property is writeable, false otherwise
     */
    private static boolean isWriteable(PropertyDescriptor property)
        {
        return property.getWriteMethod() != null;
        }


    // ---- data members ----------------------------------------------------

    /**
     * A map of registered property updaters for this target.
     */
    private Map<String, Updater> m_updaters;
    }
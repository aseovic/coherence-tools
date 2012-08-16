package com.seovic.loader;


import com.seovic.core.Updater;
import java.io.Serializable;
import java.util.Set;


/**
 * A target that the items can be loaded into.
 *
 * @author Aleksandar Seovic/Ivan Cikic  2009.06.15
 */
public interface Target
        extends Serializable
    {
    /**
     * Called by the loader to inform target that the loading process is
     * about to start.
     * <p/>
     * This is a lifecycle method that allows implementations to perform any
     * preliminary one-time set up before the load starts.
     */
    void beginImport();

    /**
     * Import a single item.
     *
     * @param item  item to import
     */
    void importItem(Object item);

    /**
     * Called by the loader to inform target that the loading process is
     * finished.
     * <p/>
     * This is a lifecycle method that allows implementations to perform any
     * necessary cleanup after the load is finished.
     */
    void endImport();

    /**
     * Return target property names.
     * <p/>
     * This method will be called if the {@link Loader} mapping mode is set to
     * {@link MappingMode#AUTO} to determine a list of properties that should
     * be copied from source to target.
     *
     * @return the names of the properties that should be mapped to target
     */
    Set<String> getPropertyNames();

    /**
     * Create an instance of a target object.
     *
     * @param source      source object is loaded from
     * @param sourceItem  source object, in a format determined by its source
     *
     * @return a target object instance
     */
    Object createTargetInstance(Source source, Object sourceItem);

    /**
     * Return updater for the specified property.
     *
     * @param propertyName  property name
     *
     * @return updater that should be used for the specified property
     */
    Updater getUpdater(String propertyName);

    /**
     * Set updater for the specified property.
     *
     * @param propertyName  property name
     * @param updater       updater that should be used for the specified
     *                      property
     */
    void setUpdater(String propertyName, Updater updater);
    }

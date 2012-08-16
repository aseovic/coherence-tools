package com.seovic.loader;


/**
 * Defines how loader should determine property mappings.
 *
 * @author Aleksandar Seovic  2012.04.06
 */
public enum MappingMode {
    /**
     * Loader will attampt to map all target fields to corresponding source
     * fields, whether or not they are explicitly configured.
     * <p/>
     * This is the default setting.
     */
    AUTO,

    /**
     * Loader will only map explicitly configured fields.
     */
    EXPLICIT
}

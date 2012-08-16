package com.seovic.loader;


/**
 * Default loader implementation.
 *
 * @author Aleksandar Seovic/Ivan Cikic  2009.06.15
 */
public class DefaultLoader
        extends AbstractLoader {
    // ---- constructors ----------------------------------------------------

    /**
     * Construct DefaultLoader instance.
     *
     * @param source source to load items from
     * @param target target to load items into
     */
    public DefaultLoader(Source source, Target target) {
        this.source = source;
        this.target = target;
    }

    // ---- AbstractLoader implementation -----------------------------------

    @Override
    protected Source getSource() {
        return source;
    }

    @Override
    protected Target getTarget() {
        return target;
    }

    // ---- data members ----------------------------------------------------

    /**
     * Source to load items from.
     */
    private Source source;

    /**
     * Target to load items into.
     */
    private Target target;
}


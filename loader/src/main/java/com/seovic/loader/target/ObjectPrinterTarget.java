package com.seovic.loader.target;


import java.io.PrintStream;


/**
 * @author Aleksandar Seovic  2012.04.06
 */
public class ObjectPrinterTarget
        extends AbstractObjectTarget {

    private PrintStream out;

    /**
     * Construct AbstractObjectTarget instance.
     *
     * @param out        print stream to print objects to
     * @param itemClass  class of the items to load
     */
    public ObjectPrinterTarget(PrintStream out, Class itemClass) {
        super(itemClass);
        this.out = out;
    }

    @Override
    public void importItem(Object item) {
        out.println(item);
    }
}

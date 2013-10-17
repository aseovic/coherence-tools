package com.seovic.core.objects;


import com.seovic.core.Entity;


/**
 * @author Aleksandar Seovic  2013.10.09
 */
public abstract class LiveObject<T> implements Entity<T>
    {
    public abstract void start();
    public abstract void stop();

    public void restart()
        {
        stop();
        start();
        }
    }

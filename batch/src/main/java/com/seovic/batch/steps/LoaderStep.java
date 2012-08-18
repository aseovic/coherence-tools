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

package com.seovic.batch.steps;


import com.seovic.batch.AbstractStep;
import com.seovic.batch.ExecutionContext;
import com.seovic.loader.DefaultLoader;
import com.seovic.loader.Loader;
import com.seovic.loader.Source;
import com.seovic.loader.Target;


/**
 * Step implementation that uses {@link Loader} to import data from one data
 * source into another.
 *
 * @author Aleksandar Seovic  2009.11.06
 */
public class LoaderStep
        extends AbstractStep {

    private static final long serialVersionUID = -5347903443099790511L;

    // ---- data members ----------------------------------------------------

    /**
     * Loader to use.
     */
    private Loader loader;

    // ---- constructors ----------------------------------------------------

    /**
     * Construct LoaderStep instance.
     *
     * @param name   step name
     * @param loader loader to use
     */
    public LoaderStep(String name, Loader loader) {
        super(name);
        this.loader = loader;
    }

    /**
     * Construct LoaderStep instance.
     *
     * @param name   step name
     * @param source loader source to use
     * @param target loader target to use
     */
    public LoaderStep(String name, Source source, Target target) {
        this(name, new DefaultLoader(source, target));
    }

    // ---- Step implementation ---------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void execute(ExecutionContext context) {
        loader.load();
    }
}

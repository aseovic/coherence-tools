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

package com.seovic.core;


import java.lang.reflect.Constructor;

import org.slf4j.LoggerFactory;


/**
 * Factory class for default implementations.
 *
 * @author Aleksandar Seovic  2009.09.28
 */
@SuppressWarnings("unchecked")
public final class Defaults {
    private static final Defaults INSTANCE = new Defaults();

    // ---- data members ----------------------------------------------------

    private Constructor<Expression> ctorExpression;
    private Constructor<Extractor> ctorExtractor;
    private Constructor<Updater> ctorUpdater;
    private Constructor<Condition> ctorCondition;

    // ---- constructors ----------------------------------------------------

    /**
     * Singleton constructor.
     */
    private Defaults() {
        try {
            ctorExpression = getConstructor(
                    Configuration.getDefaultExpressionType());
            ctorExtractor = getConstructor(
                    Configuration.getDefaultExtractorType());
            ctorUpdater = getConstructor(
                    Configuration.getDefaultUpdaterType());
            ctorCondition = getConstructor(
                    Configuration.getDefaultConditionType());
        }
        catch (ClassNotFoundException e) {
            LoggerFactory.getLogger(Defaults.class).error("Unable to initialize Defaults.", e);
            throw new RuntimeException(e);
        }
    }

    // ---- public methods --------------------------------------------------

    /**
     * Creates a default expression instance.
     *
     * @param expression string expression
     *
     * @return expression instance
     */
    public static Expression createExpression(String expression) {
        try {
            return INSTANCE.ctorExpression.newInstance(expression);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a default extractor instance.
     *
     * @param expression string expression
     *
     * @return extractor instance
     */
    public static Extractor createExtractor(String expression) {
        try {
            return INSTANCE.ctorExtractor.newInstance(expression);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a default updater instance.
     *
     * @param expression string expression
     *
     * @return updater instance
     */
    public static Updater createUpdater(String expression) {
        try {
            return INSTANCE.ctorUpdater.newInstance(expression);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a default condition instance.
     *
     * @param expression conditional expression
     *
     * @return condition instance
     */
    public static Condition createCondition(String expression) {
        try {
            return INSTANCE.ctorCondition.newInstance(expression);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    // ---- helper methods --------------------------------------------------

    /**
     * Gets a constructor for the specified class that accepts a single String
     * argument.
     *
     * @param type the class to find the constructor for
     *
     * @return a constructor for the specified class that accepts a single
     *         String argument
     */
    protected Constructor getConstructor(Class type) {
        try {
            return type.getConstructor(String.class);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(
                    "Unable to find a constructor that accepts"
                    + " a single String argument in the "
                    + type.getName() + " class.", e);
        }
    }
}

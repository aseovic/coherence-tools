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


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Provides centralized access to configuration info.
 * <p/>
 * You can modify configuration settings by editing <tt>seovic-core.properties</tt>
 * configuration file, which should be located in the classpath root.
 *
 * @author Aleksandar Seovic  2010.02.05
 */
public final class Configuration {
    // ---- constants -------------------------------------------------------

    // configuration property keys
    private static final String EXPRESSION_TYPE = "expression.type";
    private static final String EXTRACTOR_TYPE = "extractor.type";
    private static final String UPDATER_TYPE = "updater.type";
    private static final String CONDITION_TYPE = "condition.type";
    private static final String SCRIPT_LANGUAGE = "script.language";

    // default values
    private static final String DEFAULT_EXPRESSION_TYPE =
            "com.seovic.core.expression.MvelExpression";
    private static final String DEFAULT_EXTRACTOR_TYPE =
            "com.seovic.core.extractor.ExpressionExtractor";
    private static final String DEFAULT_UPDATER_TYPE =
            "com.seovic.core.updater.ExpressionUpdater";
    private static final String DEFAULT_CONDITION_TYPE =
            "com.seovic.core.condition.ExpressionCondition";
    private static final String DEFAULT_SCRIPT_LANGUAGE = "javascript";

    // ---- data members ----------------------------------------------------

    /**
     * Logger for this class.
     */
    private static final Log LOG = LogFactory.getLog(Configuration.class);

    /**
     * Singleton instance.
     */
    private static final Configuration INSTANCE = new Configuration();

    /**
     * Configuration settings.
     */
    private final Map<String, String> configuration;

    // ---- constructors ----------------------------------------------------

    /// <summary>
    /// Singleton constructor.
    /// </summary>
    private Configuration() {
        Map<String, String> props = new HashMap<String, String>();
        props.put(EXPRESSION_TYPE, DEFAULT_EXPRESSION_TYPE);
        props.put(EXTRACTOR_TYPE, DEFAULT_EXTRACTOR_TYPE);
        props.put(UPDATER_TYPE, DEFAULT_UPDATER_TYPE);
        props.put(CONDITION_TYPE, DEFAULT_CONDITION_TYPE);
        props.put(SCRIPT_LANGUAGE, DEFAULT_SCRIPT_LANGUAGE);

        try {
            Properties config = new Properties();
            config.load(Configuration.class.getClassLoader()
                                .getResourceAsStream("com.seovic.core.properties"));
            for (String propertyName : config.stringPropertyNames()) {
                props.put(propertyName, config.getProperty(propertyName));
            }
        }
        catch (IOException e) {
            // should never happen, as default file is embedded within JAR
            LOG.warn("Configuration file com.seovic.core.properties"
                     + " is missing. Using hardcoded defaults: \n" + props);
        }

        configuration = props;
    }

    // ---- public methods --------------------------------------------------

    /**
     * Get a default expression type.
     *
     * @return default expression type
     *
     * @throws ClassNotFoundException if specified class cannot be found
     */
    public static Class getDefaultExpressionType()
            throws ClassNotFoundException {
        return Class.forName(INSTANCE.configuration.get(EXPRESSION_TYPE));
    }

    /**
     * Get a default extractor type.
     *
     * @return default extractor type
     *
     * @throws ClassNotFoundException if specified class cannot be found
     */
    public static Class getDefaultExtractorType()
            throws ClassNotFoundException {
        return Class.forName(INSTANCE.configuration.get(EXTRACTOR_TYPE));
    }

    /**
     * Get a default updater type.
     *
     * @return default updater type
     *
     * @throws ClassNotFoundException if specified class cannot be found
     */
    public static Class getDefaultUpdaterType()
            throws ClassNotFoundException {
        return Class.forName(INSTANCE.configuration.get(UPDATER_TYPE));
    }

    /**
     * Get a default condition type.
     *
     * @return default condition type
     *
     * @throws ClassNotFoundException if specified class cannot be found
     */
    public static Class getDefaultConditionType()
            throws ClassNotFoundException {
        return Class.forName(INSTANCE.configuration.get(CONDITION_TYPE));
    }

    /**
     * Get the name of the default script language.
     *
     * @return the name of the default script language
     */
    public static String getDefaultScriptLanguage() {
        return INSTANCE.configuration.get(SCRIPT_LANGUAGE);
    }
}

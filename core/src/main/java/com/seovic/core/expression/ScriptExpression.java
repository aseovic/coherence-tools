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

package com.seovic.core.expression;


import com.seovic.core.Configuration;
import com.seovic.core.Expression;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;


/**
 * An imlementation of {@link Expression} that evaluates specified expression
 * using any supported scripting language.
 * <p/>
 * Unlike the expression languages such as OGNL and MVEL, scripting languages do
 * not have a notion of a "root object" for expression evaluation. Because of
 * this, the target object is bound to a variable called <tt>target</tt> and
 * must be referenced explicitly within the expression.
 *
 * @author Aleksandar Seovic  2009.09.27
 */
@SuppressWarnings({"unchecked"})
public class ScriptExpression<T>
        extends AbstractExpression<T> {

    private static final long serialVersionUID = -9173496441102391996L;

    private static final ScriptEngineManager MANAGER = new ScriptEngineManager();

    // ---- data members ----------------------------------------------------

    /**
     * Script language.
     */
    private String language;

    /**
     * Scripting engine to use.
     */
    private transient ScriptEngine scriptEngine;

    /**
     * Compiled script
     */
    private transient CompiledScript script;

    // ---- constructors ----------------------------------------------------

    /**
     * Deserialization constructor (for internal use only).
     */
    public ScriptExpression() {
    }

    /**
     * Construct a <tt>ScriptExpression</tt> instance.
     *
     * @param expression the script to evaluate
     */
    public ScriptExpression(String expression) {
        this(expression, Configuration.getDefaultScriptLanguage());
    }

    /**
     * Construct a <tt>ScriptExpression</tt> instance.
     *
     * @param script the script to evaluate
     */
    public ScriptExpression(InputStream script) {
        this(script, Configuration.getDefaultScriptLanguage());
    }

    /**
     * Construct a <tt>ScriptExpression</tt> instance.
     *
     * @param expression the script to evaluate
     * @param language   scripting language to use
     */
    public ScriptExpression(String expression, String language) {
        super(expression);
        this.language = language;
        init();
    }

    /**
     * Construct a <tt>ScriptExpression</tt> instance.
     *
     * @param script   the script to evaluate
     * @param language scripting language to use
     */
    public ScriptExpression(InputStream script, String language) {
        super(script);
        this.language = language;
        init();
    }

    private void init() {
        try {
            scriptEngine = MANAGER.getEngineByName(language);
            if (scriptEngine instanceof Compilable) {
                script = ((Compilable) scriptEngine).compile(getExpression());
            }
        }
        catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    // ---- Expression implementation ----------------------------------------

    /**
     * {@inheritDoc}
     */
    public T evaluate(Object target, Map<String, Object> variables) {
        SimpleBindings bindings = variables == null
                                  ? new SimpleBindings()
                                  : new SimpleBindings(variables);
        bindings.put("target", target);

        try {
            return (T) (script == null
                        ? scriptEngine.eval(getExpression(), bindings)
                        : script.eval(bindings));
        }
        catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void evaluateAndSet(Object target, Object value) {
        throw new UnsupportedOperationException(
                "ScriptExpression cannot be used to update target object value.");
    }


    // ---- PortableObject implementation -----------------------------------

    /**
     * Deserialize this object from a POF stream.
     *
     * @param reader POF reader to use
     *
     * @throws IOException if an error occurs during deserialization
     */
    public void readExternal(PofReader reader) throws IOException {
        super.readExternal(reader);
        language = reader.readString(10);
        init();
    }

    /**
     * Serialize this object into a POF stream.
     *
     * @param writer POF writer to use
     *
     * @throws IOException if an error occurs during serialization
     */
    public void writeExternal(PofWriter writer) throws IOException {
        super.writeExternal(writer);
        writer.writeString(10, language);
    }
}
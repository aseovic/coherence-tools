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


import com.seovic.core.Expression;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import java.io.InputStream;
import java.util.Map;


/**
 * An imlementation of {@link Expression} that evaluates specified expression
 * using <a href="http://groovy.codehaus.org/" target="_new">Groovy</a>.
 * <p/>
 * Unlike the expression languages such as OGNL and MVEL, Groovy does not have a
 * notion of a "root object" for expression evaluation. Because of this, the
 * target object is bound to a variable called <tt>target</tt> and must be
 * referenced explicitly within the expression.
 *
 * @author Aleksandar Seovic  2009.09.20
 */
@SuppressWarnings({"unchecked"})
public class GroovyExpression<T>
        extends AbstractExpression<T> {

    private static final long serialVersionUID = 8565991664974395148L;

    // ---- data members ----------------------------------------------------

    /**
     * Monitor object.
     */
    private final transient Object monitor = new Object();

    /**
     * Compiled Groovy script
     */
    private transient Script getScript;

    /**
     * Compiled Groovy set script
     */
    private transient Script setScript;


    // ---- constructors ----------------------------------------------------

    /**
     * Deserialization constructor (for internal use only).
     */
    public GroovyExpression() {
    }

    /**
     * Construct a <tt>GroovyExpression</tt> instance.
     *
     * @param expression the script to evaluate
     */
    public GroovyExpression(String expression) {
        super(expression);
    }

    /**
     * Construct a <tt>GroovyExpression</tt> instance.
     *
     * @param script the script to evaluate
     */
    public GroovyExpression(InputStream script) {
        super(script);
    }


    // ---- Expression implementation ----------------------------------------

    /**
     * {@inheritDoc}
     */
    public T evaluate(Object target, Map<String, Object> variables) {
        Binding binding = new Binding(variables);
        binding.setVariable("target", target);

        synchronized (monitor) {
            Script script = getCompiledScript();
            script.setBinding(binding);
            return (T) script.run();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void evaluateAndSet(Object target, Object value) {
        Binding binding = new Binding();
        binding.setVariable("target", target);
        binding.setVariable("value", value);

        synchronized (monitor) {
            Script script = getCompiledSetScript();
            script.setBinding(binding);
            script.run();
        }
    }

    // ---- helper methods --------------------------------------------------

    /**
     * Return a compiled Groovy script for this expression.
     *
     * @return compiled Groovy script
     */
    protected Script getCompiledScript() {
        if (getScript == null) {
            GroovyShell shell = new GroovyShell();
            getScript = shell.parse(getExpression());
        }
        return getScript;
    }

    /**
     * Return a compiled Groovy script for this expression.
     *
     * @return compiled Groovy script
     */
    protected Script getCompiledSetScript() {
        if (setScript == null) {
            GroovyShell shell = new GroovyShell();
            setScript = shell.parse(getExpression() + " = value");
        }
        return setScript;
    }
}
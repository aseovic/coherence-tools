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

import com.tangosol.coherence.mvel.MVEL;
import com.tangosol.coherence.mvel.ParserContext;
import java.io.Serializable;
import java.util.Map;



/**
 * An imlementation of {@link Expression} that evaluates specified expression
 * using <a href="http://mvel.codehaus.org/" target="_new">MVEL</a>.
 *
 * @author Aleksandar Seovic  2009.09.20
 */
@SuppressWarnings({"unchecked"})
public class MvelExpression<T>
        extends AbstractExpression<T> {
    // ---- static members --------------------------------------------------

    private static final ParserContext PARSER_CONTEXT;
    private static final long serialVersionUID = 318851246249192574L;

    // ---- static initializer ----------------------------------------------

    static {
        ParserContext parserContext = new ParserContext();
        parserContext.addPackageImport("java.util");
        PARSER_CONTEXT = parserContext;
    }

    // ---- data members ----------------------------------------------------

    /**
     * Compiled expression.
     */
    private transient Serializable compiledExpression;

    /**
     * Compiled set expression.
     */
    private transient Serializable compiledSetExpression;

    // ---- constructors ----------------------------------------------------

    /**
     * Deserialization constructor (for internal use only).
     */
    public MvelExpression() {
    }

    /**
     * Construct a <tt>MvelExpression</tt> instance.
     *
     * @param expression the expression to evaluate
     */
    public MvelExpression(String expression) {
        super(expression);
    }


    // ---- Expression implementation ----------------------------------------

    /**
     * {@inheritDoc}
     */
    public T evaluate(Object target, Map<String, Object> variables) {
        return (T) MVEL.executeExpression(getCompiledExpression(), target, variables);
    }

    /**
     * {@inheritDoc}
     */
    public void evaluateAndSet(Object target, Object value) {
        MVEL.executeSetExpression(getCompiledSetExpression(), target, value);
    }

    // ---- helper methods --------------------------------------------------

    /**
     * Return a compiled MVEL expression.
     *
     * @return compiled MVEL expression
     */
    protected Serializable getCompiledExpression() {
        if (compiledExpression == null) {
            compiledExpression = MVEL.compileExpression(getExpression(), PARSER_CONTEXT);
        }
        return compiledExpression;
    }

    /**
     * Return a compiled MVEL set expression.
     *
     * @return compiled MVEL set expression
     */
    protected Serializable getCompiledSetExpression() {
        if (compiledSetExpression == null) {
            compiledSetExpression = MVEL.compileSetExpression(getExpression(), PARSER_CONTEXT);
        }
        return compiledSetExpression;
    }
}
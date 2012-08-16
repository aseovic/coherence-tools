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


import java.util.Map;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;


/**
 * An imlementation of {@link Expression} that evaluates specified expression
 * using <a href="http://static.springsource.org/spring/docs/3.0.x/reference/html/expressions.html"
 * target="_new"> Spring Expression Language (SpEL)</a>.
 *
 * @author Aleksandar Seovic  2009.09.30
 * @author Ivan Cikic  2009.09.30
 */
@SuppressWarnings("unchecked")
public class SpelExpression<T>
        extends AbstractExpression<T> {
    // ---- static members --------------------------------------------------

    private static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();
    private static final long serialVersionUID = 400321438177955162L;

    // ---- data members ----------------------------------------------------

    private transient StandardEvaluationContext context = new StandardEvaluationContext();
    private transient Expression parsedExpression;

    // ---- constructors ----------------------------------------------------

    /**
     * Deserialization constructor (for internal use only).
     */
    public SpelExpression() {
    }

    /**
     * Construct a <tt>SpelExpression</tt> instance.
     *
     * @param expression the expression to evaluate
     */
    public SpelExpression(String expression) {
        super(expression);
    }


    // ---- Expression implementation ---------------------------------------

    /**
     * {@inheritDoc}
     */
    public T evaluate(Object target, Map<String, Object> variables) {
        Expression expression = getParsedExpression();

        context.setRootObject(target);
        if (variables != null) {
            context.setVariables(variables);
        }
        try {
            return (T) expression.getValue(context);
        }
        catch (EvaluationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void evaluateAndSet(Object target, Object value) {
        Expression expression = getParsedExpression();

        context.setRootObject(target);
        try {
            expression.setValue(context, value);
        }
        catch (EvaluationException e) {
            throw new RuntimeException(e);
        }
    }


    // ---- helper methods --------------------------------------------------

    /**
     * Return a parsed SpEL expression.
     *
     * @return parsed SpEL expression
     */
    protected synchronized Expression getParsedExpression() {
        if (parsedExpression == null) {
            try {
                parsedExpression = EXPRESSION_PARSER.parseExpression(getExpression());
            }
            catch (ParseException e) {
                throw new IllegalArgumentException("[" + getExpression() + "] is not a valid SpEL expression", e);
            }
        }
        return parsedExpression;
    }
}

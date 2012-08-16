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

package com.seovic.core.extractor;


import com.seovic.core.Defaults;
import com.seovic.core.Expression;
import com.seovic.core.Extractor;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * An implementation of {@link Extractor} that extracts value from a target
 * object using one of the {@link Expression} implementations.
 *
 * @author Aleksandar Seovic  2009.06.17
 */
@SuppressWarnings({"unchecked"})
public class ExpressionExtractor<T>
        implements Extractor<T>, Serializable, PortableObject {

    private static final long serialVersionUID = -4304647085789449767L;

    // ---- data members ----------------------------------------------------

    /**
     * The expression to use.
     */
    private Expression<T> expression;

    /**
     * The map containing variables to use during evaluation.
     */
    private Map<String, Object> variables = new HashMap<String, Object>();


    // ---- constructors ----------------------------------------------------

    /**
     * Deserialization constructor (for internal use only).
     */
    public ExpressionExtractor() {
    }

    /**
     * Construct an <tt>ExpressionExtractor</tt> instance.
     *
     * @param expression the expression to use
     */
    public ExpressionExtractor(String expression) {
        this(Defaults.createExpression(expression));
    }

    /**
     * Construct an <tt>ExpressionExtractor</tt> instance.
     *
     * @param expression the expression to use
     */
    public ExpressionExtractor(Expression<T> expression) {
        this.expression = expression;
    }

    /**
     * Construct an <tt>ExpressionExtractor</tt> instance.
     *
     * @param expression the expression to use
     * @param variables  the map containing variables to be used during
     *                   evaluation
     */
    public ExpressionExtractor(Expression<T> expression,
                               Map<String, Object> variables) {
        this.expression = expression;
        this.variables = variables;
    }


    // ---- Extractor implementation ----------------------------------------

    /**
     * {@inheritDoc}
     */
    public T extract(Object target) {
        if (target == null) {
            return null;
        }
        return expression.evaluate(target, variables);
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
        expression = (Expression<T>) reader.readObject(0);
        reader.readMap(1, variables);
    }

    /**
     * Serialize this object into a POF stream.
     *
     * @param writer POF writer to use
     *
     * @throws IOException if an error occurs during serialization
     */
    public void writeExternal(PofWriter writer) throws IOException {
        writer.writeObject(0, expression);
        writer.writeMap(1, variables, String.class);
    }


    // ---- Object methods --------------------------------------------------

    /**
     * Test objects for equality.
     *
     * @param o object to compare this object with
     *
     * @return <tt>true</tt> if the specified object is equal to this object
     *         <tt>false</tt> otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ExpressionExtractor that = (ExpressionExtractor) o;

        return expression.equals(that.expression)
               && (variables == null
                   ? that.variables == null
                   : variables.equals(that.variables));
    }

    /**
     * Return hash code for this object.
     *
     * @return this object's hash code
     */
    @Override
    public int hashCode() {
        int result = expression.hashCode();
        result = 31 * result + (variables != null
                                ? variables.hashCode()
                                : 0);
        return result;
    }

    /**
     * Return string representation of this object.
     *
     * @return string representation of this object
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
               "expression=" + expression +
               ", variables=" + variables +
               '}';
    }
}
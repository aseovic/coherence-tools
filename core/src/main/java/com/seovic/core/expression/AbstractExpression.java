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
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import org.apache.commons.io.IOUtils;


/**
 * Abstract base class for various expression implementations.
 *
 * @author Aleksandar Seovic  2009.09.20
 */
public abstract class AbstractExpression<T>
        implements Expression<T>, Serializable, PortableObject {

    private static final long serialVersionUID = 4151713196686835487L;

    // ---- data members ----------------------------------------------------

    /**
     * Expression source.
     */
    private String expression;

    // ---- constructors ----------------------------------------------------

    /**
     * Deserialization constructor (for internal use only).
     */
    protected AbstractExpression() {
    }

    /**
     * Construct an expression instance.
     *
     * @param expression the expression to evaluate
     */
    protected AbstractExpression(String expression) {
        this.expression = expression;
    }

    /**
     * Construct an expression instance.
     *
     * @param expression the expression to evaluate
     */
    protected AbstractExpression(InputStream expression) {
        try {
            this.expression = IOUtils.toString(expression);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // ---- Expression implementation ---------------------------------------

    protected String getExpression() {
        return expression;
    }

    /**
     * {@inheritDoc}
     */
    public T evaluate(Object target) {
        return evaluate(target, null);
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
        expression = reader.readString(0);
    }

    /**
     * Serialize this object into a POF stream.
     *
     * @param writer POF writer to use
     *
     * @throws IOException if an error occurs during serialization
     */
    public void writeExternal(PofWriter writer) throws IOException {
        writer.writeString(0, expression);
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

        AbstractExpression that = (AbstractExpression) o;
        return expression.equals(that.expression);
    }

    /**
     * Return hash code for this object.
     *
     * @return this object's hash code
     */
    @Override
    public int hashCode() {
        return expression.hashCode();
    }

    /**
     * Return string representation of this object.
     *
     * @return string representation of this object
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
               "expression='" + expression + '\'' +
               '}';
    }
}

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

package com.seovic.core.updater;


import com.seovic.core.Defaults;
import com.seovic.core.Expression;
import com.seovic.core.Updater;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import java.io.IOException;
import java.io.Serializable;


/**
 * An imlementation of {@link Updater} that updates the last node of the
 * specified {@link Expression}.
 *
 * @author Aleksandar Seovic  2009.06.17
 */
public class ExpressionUpdater
        implements Updater, Serializable, PortableObject {

    private static final long serialVersionUID = -7948857569953908668L;

    // ---- data members ----------------------------------------------------

    /**
     * The expression to use.
     */
    private Expression expression;


    // ---- constructors ----------------------------------------------------

    /**
     * Deserialization constructor (for internal use only).
     */
    public ExpressionUpdater() {
    }

    /**
     * Construct an <tt>ExpressionUpdater</tt> instance.
     *
     * @param expression the expression to use
     */
    public ExpressionUpdater(String expression) {
        this(Defaults.createExpression(expression));
    }

    /**
     * Construct an <tt>ExpressionUpdater</tt> instance.
     *
     * @param expression the expression to use
     */
    public ExpressionUpdater(Expression expression) {
        this.expression = expression;
    }


    // ---- Updater implementation ------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void update(Object target, Object value) {
        if (target == null) {
            throw new IllegalArgumentException("Updater target cannot be null");
        }
        expression.evaluateAndSet(target, value);
    }


    // ---- PortableObject implementation -----------------------------------

    /**
     * Deserialize this object from a POF stream.
     *
     * @param reader POF reader to use
     *
     * @throws IOException if an error occurs during deserialization
     */
    public void readExternal(PofReader reader)
            throws IOException {
        expression = (Expression) reader.readObject(0);
    }

    /**
     * Serialize this object into a POF stream.
     *
     * @param writer POF writer to use
     *
     * @throws IOException if an error occurs during serialization
     */
    public void writeExternal(PofWriter writer)
            throws IOException {
        writer.writeObject(0, expression);
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

        ExpressionUpdater extractor = (ExpressionUpdater) o;
        return expression.equals(extractor.expression);
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
        return "ExpressionUpdater{" +
               "expression=" + expression +
               '}';
    }
}
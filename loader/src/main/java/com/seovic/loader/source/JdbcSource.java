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

package com.seovic.loader.source;


import com.seovic.core.Extractor;
import com.seovic.core.Factory;
import com.seovic.core.extractor.MvelExtractor;
import com.seovic.loader.Source;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import javax.sql.DataSource;


/**
 * A {@link Source} implementation that reads items to load using plain old JDBC
 * API.
 *
 * @author Ivan Cikic  2009.12.16
 */
public class JdbcSource
        extends AbstractBaseSource {

    private static final long serialVersionUID = 4644313215099227563L;

    // ---- data members ----------------------------------------------------

    private Factory<DataSource> m_dataSourceFactory;
    private String m_sql;

    private transient DataSource m_dataSource;
    private transient Connection m_connection;
    private transient Statement m_statement;
    private transient ResultSet  m_resultSet;

    // --- constructors -----------------------------------------------------

    /**
     * Construct JdbcSource instance.
     *
     * @param dataSourceFactory DataSource factory
     * @param sql               SQL query to execute
     */
    public JdbcSource(Factory<DataSource> dataSourceFactory, String sql) {
        m_dataSourceFactory = dataSourceFactory;
        m_sql = sql;
    }

    /**
     * Construct JdbcSource instance.
     * <p/>
     * This constructor should only be used when using JdbcSource in process. In
     * situations where this object might be serialized and used in a remote
     * process (as part of remote batch load job, for example), you should use
     * the constructor that accepts {@link Factory<DataSource>} as an argument.
     *
     * @param dataSource data source to use
     * @param sql        SQL query to execute
     */
    public JdbcSource(DataSource dataSource, String sql) {
        m_dataSource = dataSource;
        m_sql = sql;
    }


    // ---- Source implementation -------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void beginExport() {
        if (m_dataSource == null) {
            m_dataSource = m_dataSourceFactory.create();
        }
        try {
            m_connection = m_dataSource.getConnection();
            m_statement = m_connection.createStatement();
            m_statement.execute(m_sql);
            m_resultSet = m_statement.getResultSet();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void endExport() {
        try {
            m_resultSet.close();
            m_statement.close();
            m_connection.close();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // ---- Iterator implementation -----------------------------------------

    /**
     * {@inheritDoc}
     */
    public Iterator iterator() {
        return new JdbcIterator(m_resultSet);
    }


    // ---- AbstractBaseSource implementation -------------------------------

    @Override
    protected Extractor createDefaultExtractor(String propertyName) {
        return new ResultSetExtractor(propertyName);
    }


    // ---- inner class: JdbcIterator ---------------------------------------

    /**
     * Iterator implementation for {@link JdbcSource}.
     * <p/>
     * Allows iterator-like behavior over supplied ResultSet.
     */
    public static class JdbcIterator
            implements Iterator {

        // ---- constructors --------------------------------------------

        /**
         * Construct JdbcIterator instance.
         *
         * @param resultSet result set to iterate over
         */
        public JdbcIterator(ResultSet resultSet) {
            m_resultSet = resultSet;
        }


        // ---- Iterator implementation ---------------------------------

        /**
         * {@inheritDoc}
         */
        public boolean hasNext() {
            try {
                return m_resultSet.next();
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * {@inheritDoc}
         */
        public Object next() {
            return m_resultSet;
        }

        /**
         * Not supported.
         *
         * @throws UnsupportedOperationException always
         */
        public void remove() {
            throw new UnsupportedOperationException(
                    "Not supported for JdbcIterator");
        }

        // ---- data members --------------------------------------------

        private ResultSet m_resultSet;
    }


    // ---- inner class: ResultSetExtractor ---------------------------------

    /**
     * {@link Extractor} implementation used to extract values from
     * java.sql.ResultSet.
     */
    public static class ResultSetExtractor
            extends MvelExtractor {

        /**
         * Construct ResultSetExtractor instance.
         *
         * @param propertyName property name to extract value
         */
        public ResultSetExtractor(String propertyName) {
            super("getObject('" + propertyName + "')");
        }
    }
}

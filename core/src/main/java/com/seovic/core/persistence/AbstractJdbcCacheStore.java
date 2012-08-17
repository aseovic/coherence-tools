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

package com.seovic.core.persistence;


import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;


/**
 * Abstract cache store implementation that uses Spring JDBC support for data
 * store operations.
 *
 * @author Patrick Peralta/Aleksandar Seovic  2010.01.30
 */
@Transactional
public abstract class AbstractJdbcCacheStore<T>
        extends AbstractBatchingCacheStore {
    // ---- data members ----------------------------------------------------

    /**
     * Spring JDBC template that should be used for data store access.
     */
    private final SimpleJdbcTemplate jdbcTemplate;

    // ---- constructors ----------------------------------------------------

    /**
     * Constructor.
     *
     * @param dataSource JDBC datasource for underlying database
     */
    public AbstractJdbcCacheStore(DataSource dataSource) {
        jdbcTemplate = new SimpleJdbcTemplate(dataSource);
    }


    // ---- abstract methods ------------------------------------------------

    /**
     * Return SQL statement that should be executed in order to insert or update
     * a single object.
     *
     * @return SQL statement that should be executed in order to insert or
     *         update a single object
     */
    protected abstract String getMergeSql();

    /**
     * Return SQL statement that should be executed in order to load a single
     * object.
     *
     * @return SQL statement that should be executed in order to load a single
     *         object
     */
    protected abstract String getSelectSql();

    /**
     * Return an object that can be used to map a single row from a {@link
     * ResultSet} into an object.
     *
     * @return helper object used to map a single row from a {@link ResultSet}
     *         into an object
     */
    protected abstract RowMapper<T> getRowMapper();


    // ---- CacheLoader implementation --------------------------------------

    /**
     * Load and return a single object from the data store.
     *
     * @param key entry key that can be used to find the object in the data
     *            store
     *
     * @return loaded object or null if the object for the specified key was not
     *         found
     */
    @Transactional(readOnly = true)
    public Object load(Object key) {
        List<T> results = getJdbcTemplate().query(
                getSelectSql(), getRowMapper(), getPrimaryKeyComponents(key));
        return results.size() == 0 ? null : results.get(0);
    }


    // ---- CacheStore implementation ---------------------------------------

    /**
     * Persist a single object into the data store.
     *
     * @param key   entry key of the object that should be persisted
     * @param value object to persist
     */
    public void store(Object key, Object value) {
        getJdbcTemplate().update(getMergeSql(),
                                 new BeanPropertySqlParameterSource(value));
    }


    // ---- AbstractBatchingCacheStore implementation -----------------------

    /**
     * {@inheritDoc}
     */
    public void storeBatch(Map mapBatch) {
        SqlParameterSource[] batch =
                SqlParameterSourceUtils.createBatch(
                        mapBatch.values().toArray());

        getJdbcTemplate().batchUpdate(getMergeSql(), batch);
    }


    // ---- helper methods --------------------------------------------------

    /**
     * Return components of the primary key that should be used when searching
     * for an object to load in the data store.
     * <p/>
     * This method should be overriden if the underlying entity has a composite
     * primary key. It should return an array of primary key components in the
     * same order in which those components are used in the WHERE clause of the
     * SQL returned by the {@link #getSelectSql()} method.
     * <p/>
     * For example, given the following select SQL
     * <pre>
     *     SELECT line_id, product_id, quantity, price FROM order_lines
     *     WHERE order_id = ? AND line_id = ?
     * </pre>
     * and assuming that the key for <tt>LineItem</tt> objects is a custom class
     * <tt>LineItemId</tt> containing both the order id and the line item id,
     * this method should be implemented like this:
     * <pre>
     *     protected Object[] getPrimaryKeyComponents(Object key)
     *         {
     *         LineItemId id = (LineItemId) key;
     *         return new Object[] { id.getOrderId(), id.getLineId };
     *         }
     * </pre>
     * Notice that the elements <b>must</b> be in the same order as the WHERE
     * clause arguments above.
     *
     * @param key entry key that the primary key components should be obtained
     *            from
     *
     * @return an array of primary key components in the order required by the
     *         SQL statement returned by {@link #getSelectSql()} method
     */
    protected Object[] getPrimaryKeyComponents(Object key) {
        return new Object[] {key};
    }

    /**
     * Return Spring JDBC template that should be used for data store access.
     *
     * @return Spring JDBC template that should be used for data store access
     */
    protected SimpleJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}

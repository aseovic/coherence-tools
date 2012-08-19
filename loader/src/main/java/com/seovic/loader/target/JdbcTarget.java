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

package com.seovic.loader.target;


import com.seovic.core.Factory;
import com.seovic.core.Updater;
import com.seovic.core.updater.MapUpdater;
import com.seovic.loader.Source;
import com.seovic.loader.Target;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;


/**
 * A {@link Target} implementation that loads objects into database using plain
 * old JDBC API.
 *
 * @author Ivan Cikic  2009.12.16
 */
public class JdbcTarget
        extends AbstractBaseTarget {

    // ---- constructors ----------------------------------------------------

    /**
     * Construct JdbcTarget instance.
     *
     * @param dataSourceFactory data source factory that should be used to
     *                          create DataSource instance
     * @param tableName         name of the database table to insert data
     * @param propertyNames     comma-separated property names
     */
    public JdbcTarget(Factory<DataSource> dataSourceFactory,
                      String tableName,
                      String propertyNames) {
        this(dataSourceFactory, tableName, propertyNames.split(","));
    }

    /**
     * Construct JdbcTarget instance.
     *
     * @param dataSourceFactory data source factory that should be used to
     *                          create DataSource instance
     * @param tableName         name of the database table to insert data
     * @param propertyNames     property names
     */
    public JdbcTarget(Factory<DataSource> dataSourceFactory,
                      String tableName,
                      String... propertyNames) {
        m_dataSourceFactory = dataSourceFactory;
        m_tableName = tableName;
        m_propertyNames = propertyNames;
    }

    /**
     * Construct JdbcTarget instance.
     * <p/>
     * This constructor should only be used when using JdbcSource in process. In
     * situations where this object might be serialized and used in a remote
     * process (as part of remote batch load job, for example), you should use
     * the constructor that accepts {@link Factory<DataSource>} as an argument.
     *
     * @param dataSource    data source to use to connect to database
     * @param tableName     name of the database table to insert data
     * @param propertyNames comma-separated property names
     */
    public JdbcTarget(DataSource dataSource,
                      String tableName,
                      String propertyNames) {
        this(dataSource, tableName, propertyNames.split(","));
    }

    /**
     * Construct JdbcTarget instance.
     * <p/>
     * This constructor should only be used when using JdbcSource in process. In
     * situations where this object might be serialized and used in a remote
     * process (as part of remote batch load job, for example), you should use
     * the constructor that accepts {@link Factory<DataSource>} as an argument.
     *
     * @param dataSource    data source to use to connect to database
     * @param tableName     name of the database table to insert data
     * @param propertyNames property names
     */
    public JdbcTarget(DataSource dataSource,
                      String tableName,
                      String... propertyNames) {
        m_dataSource = dataSource;
        m_tableName = tableName;
        m_propertyNames = propertyNames;
    }


    // ---- Target implementation -------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void beginImport() {
        if (m_dataSource == null) {
            m_dataSource = m_dataSourceFactory.create();
        }
        m_jdbcTemplate = new JdbcTemplate(m_dataSource);
        m_batch = new ArrayList(m_batchSize);
    }

    public void endImport() {
        if (!m_batch.isEmpty()) {
            batchImport();
        }
    }

    @SuppressWarnings({"unchecked"})
    public void importItem(Object item) {
        m_batch.add(item);
        if (m_batch.size() % m_batchSize == 0) {
            batchImport();
            m_batch.clear();
        }
    }

    public Set<String> getPropertyNames() {
        return new HashSet<String>(Arrays.asList(m_propertyNames));
    }

    public Object createTargetInstance(Source source, Object sourceItem) {
        return new HashMap<String, Object>();
    }


    // ---- AbstractBaseTarget implementation -------------------------------

    @Override
    protected Updater createDefaultUpdater(String propertyName) {
        return new MapUpdater(propertyName);
    }


    // ---- getters and setters ---------------------------------------------

    public int getBatchSize() {
        return m_batchSize;
    }

    public void setBatchSize(int batchSize) {
        m_batchSize = batchSize;
    }

    // ---- helper methods --------------------------------------------------

    /**
     * Perform batch import into database.
     */
    private void batchImport() {
        m_jdbcTemplate.batchUpdate(createInsertQuery(),
                                   new BatchPreparedStatementSetter() {
                                       public void setValues(
                                               PreparedStatement ps, int i)
                                               throws SQLException {
                                           int j = 1;
                                           for (String property : m_propertyNames) {
                                               Map params = (Map) m_batch.get(
                                                       i);
                                               ps.setObject(j++, params.get(
                                                       property));
                                           }
                                       }

                                       public int getBatchSize() {
                                           return m_batch.size();
                                       }
                                   });
    }

    /**
     * Construct insert query using property names and database table name.
     *
     * @return insert query
     */
    private String createInsertQuery() {
        StringBuilder query = new StringBuilder();
        query.append("insert into ").append(m_tableName)
                .append("(").append(m_propertyNames[0]);
        for (int i = 1; i < m_propertyNames.length; i++) {
            query.append(",").append(m_propertyNames[i]);
        }
        query.append(") values (?");
        for (int i = 1; i < m_propertyNames.length; i++) {
            query.append(",?");
        }
        query.append(")");
        return query.toString();
    }


    // ---- data members ----------------------------------------------------

    /**
     * Default batch size.
     */
    private static final int DEFAULT_BATCH_SIZE = 1000;

    /**
     * The batch size.
     */
    private int m_batchSize = DEFAULT_BATCH_SIZE;

    /**
     * Batch of items.
     */
    private transient List m_batch;

    /**
     * Data source.
     */
    private transient DataSource m_dataSource;

    /**
     * Jdbc template to use for easier utilization of JDBC API.
     */
    private transient JdbcTemplate m_jdbcTemplate;

    /**
     * Name of database table to insert data into.
     */
    private String m_tableName;

    /**
     * Property names.
     */
    private String[] m_propertyNames;

    /**
     * Data source factory that should be used to create DataSource instance.
     */
    private Factory<DataSource> m_dataSourceFactory;
}

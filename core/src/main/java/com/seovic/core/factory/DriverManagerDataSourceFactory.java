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

package com.seovic.core.factory;


import com.seovic.core.Factory;
import java.io.Serializable;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;


/**
 * An {@link Factory} implementation that uses Spring's
 * <b>DriverManagerDataSource</b>.
 *
 * @author Ivan Cikic  2009.11.27
 */
public class DriverManagerDataSourceFactory
        extends AbstractFactory<DataSource> {

    private static final long serialVersionUID = -1500371491794738864L;

    // ---- data members ----------------------------------------------------

    private String url;
    private String username;
    private String password;

    // ---- constructors ----------------------------------------------------

    /**
     * Construct DriverManagerDataSourceFactory instance.
     *
     * @param url       driver URL
     * @param username  user name
     * @param password  password
     */
    public DriverManagerDataSourceFactory(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    // ---- Factory implementation ------------------------------------------

    /**
     * {@inheritDoc}
     */
    public DataSource create() {
        return new DriverManagerDataSource(url, username, password);
    }
}


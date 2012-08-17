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

package com.seovic.core.concurrent;


import com.tangosol.util.Base;
import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Tests for ClusteredExecutorService.
 * 
 * @author Aleksandar Seovic  2009.11.02
 */
public class ClusteredExecutorServiceTest
    {
    @Test
    public void testTaskDistribution() throws Exception
        {
        ExecutorService exec = new ClusteredExecutorService();

        for (int i = 0; i < 10; i++)
            {
            exec.execute(new Logger());
            }
        }

    @Test
    public void testClusteredCallableExecution() throws Exception
        {
        ExecutorService exec = new ClusteredExecutorService();

        Future[] results = new Future[10];
        for (int i = 0; i < 10; i++)
            {
            results[i] = exec.submit(new Echo("hello " + i));
            }

        for (int i = 0; i < 10; i++)
            {
            assertEquals("hello " + i, results[i].get());
            }
        }

    public static class Logger
            implements Runnable, Serializable
        {
        public void run()
            {
            Base.log("*** Logger Invocation ***");
            }
        }

    public static class Echo
            implements Callable<String>, Serializable
        {
        public Echo(String message)
            {
            m_message = message;
            }

        public String call() throws Exception
            {
            Base.log("*** Echo Invocation: message = '" + m_message + "' ***");
            return m_message;
            }

        private String m_message;
        }
    }

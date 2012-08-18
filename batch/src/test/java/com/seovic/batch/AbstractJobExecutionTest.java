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

package com.seovic.batch;


import com.seovic.batch.steps.ClearCacheStep;
import com.seovic.batch.steps.LoaderStep;
import com.seovic.loader.CsvToCoherence;
import com.seovic.loader.XmlToCoherence;
import com.seovic.test.objects.Country;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.util.Base;
import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Abstract class containing job execution tests.
 *
 * @author Aleksandar Seovic  2009.11.04
 */
public abstract class AbstractJobExecutionTest
    {
    protected abstract ExecutorService getExecutor();

    @Test
    public void testEchoExecution()
            throws Exception
        {
        Job job = (Job) new SimpleJob("Echo Job")
            .addStep(new Echo("step 1"))
            .addStep(new Echo("step 2"))
            .addStep(new ParallelStep("step 3")
                .addStep(new Echo("step 3.1"))
                .addStep(new Echo("step 3.2"))
                .addStep(new SequentialStep("step 3.3")
                    .addStep(new Echo("step 3.3.1"))
                    .addStep(new Echo("step 3.3.2")))
                .addStep(new Echo("step 3.4"))
                .addStep(new Echo("step 3.5")))
            .addStep(new Echo("step 4"))
            .addStep(new Echo("step 5"));

        ExecutionContext ctx = new ExecutionContext();
        ctx.setRemoteExecutor(getExecutor());

        job.execute(ctx);

        printSteps(job, 0);
        }

    @Test
    public void testLoaderExecution()
            throws Exception
        {
        Job job = (Job) new SimpleJob("Loader Job")
            .addStep(new ParallelStep("Load Countries")
                .addStep(new LoaderStep("CSV 1", new CsvToCoherence("countries.csv", "countries-1", Country.class)))
                .addStep(new LoaderStep("CSV 2", new CsvToCoherence("countries.csv", "countries-2", Country.class)))
                .addStep(new LoaderStep("CSV 3", new CsvToCoherence("countries.csv", "countries-3", Country.class)))
                .addStep(new LoaderStep("XML 1", new XmlToCoherence("countries.xml", "countries-4", Country.class)))
                .addStep(new LoaderStep("XML 2", new XmlToCoherence("countries.xml", "countries-5", Country.class)))
                .addStep(new LoaderStep("XML 3", new XmlToCoherence("countries.xml", "countries-6", Country.class))));

        ExecutionContext ctx = new ExecutionContext();
        ctx.setRemoteExecutor(getExecutor());

        job.execute(ctx);
        printSteps(job, 0);

        for (int i = 1; i <= 6; i++)
            {
            NamedCache cache = CacheFactory.getCache("countries-" + i);
            assertEquals(244, cache.size());
            }

        job = (Job) new SimpleJob("Clear Cache Job")
                .addStep(new ClearCacheStep("Clear 1", "countries-1"))
                .addStep(new ClearCacheStep("Clear 2", "countries-2"))
                .addStep(new ClearCacheStep("Clear 3", "countries-3"))
                .addStep(new ClearCacheStep("Clear 4", "countries-4"))
                .addStep(new ClearCacheStep("Clear 5", "countries-5"))
                .addStep(new ClearCacheStep("Clear 6", "countries-6"));

        job.execute(ctx);

        for (int i = 1; i <= 6; i++)
            {
            NamedCache cache = CacheFactory.getCache("countries-" + i);
            assertEquals(0, cache.size());
            }
        }


    // ---- helper methods --------------------------------------------------

    protected void printSteps(CompositeStep compositeStep, int indent)
        {
        for (Step step : compositeStep.getSteps())
            {
                StringBuilder sb = indentString(indent)
                        .append(step.getName())
                        .append(": ")
                        .append(step.getResult().getStatus());
                if (step.getResult().getStatus() == ExecutionStatus.FAILED) {
                    sb.append(" (").append(step.getResult().getException().getMessage()).append(')');
                }
                System.out.println(sb);
            if (step instanceof CompositeStep)
                {
                printSteps((CompositeStep) step, indent + 4);
                }
            }
        }

    private StringBuilder indentString(int indent)
        {
        StringBuilder sb = new StringBuilder(indent);
        for (int i = 0; i < indent; i++)
            {
            sb.append(" ");
            }
        return sb;
        }


    public static class Echo
            extends AbstractStep
            implements Serializable
        {
        public Echo(String name)
            {
            super(name);
            }

        public void execute(ExecutionContext context)
            {
            Base.log(getName());
            }
        }
    }

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

package com.seovic.scheduler;


import com.seovic.core.processor.MethodInvocationProcessor;

import com.tangosol.net.ConfigurableCacheFactory;
import com.tangosol.net.DefaultConfigurableCacheFactory;
import com.tangosol.net.NamedCache;

import java.util.Collection;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Trigger;


/**
 * @author Aleksandar Seovic  2011.04.19
 */
@SuppressWarnings({"unchecked"})
public abstract class JobScheduler {
    static final ConfigurableCacheFactory CACHE_FACTORY =
            new DefaultConfigurableCacheFactory("scheduler-cache-config.xml");

    static final NamedCache JOBS =
            CACHE_FACTORY.ensureCache("scheduled-jobs", JobScheduler.class.getClassLoader());

    public static void schedule(JobDetail job, Trigger... schedule) {
        JOBS.put(job.getKey(), new ScheduledJob(job, schedule));
    }
    
    public static void unschedule(JobKey key) {
        JOBS.remove(key);
    }

    public static void pause(JobKey key) {
        JOBS.invoke(key, new MethodInvocationProcessor("setPaused", true, true));
    }

    public static void resume(JobKey key) {
        JOBS.invoke(key, new MethodInvocationProcessor("setPaused", true, false));
    }

    public static Collection<ScheduledJob> getJobs() {
        return JOBS.values();
    }
}

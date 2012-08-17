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


import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import org.quartz.JobDetail;
import org.quartz.Trigger;


/**
 * @author Aleksandar Seovic  2011.04.19
*/
public class ScheduledJob implements Serializable {
    private JobDetail     job;
    private List<Trigger> triggers;
    private boolean       paused;

    public ScheduledJob(JobDetail job, Trigger... schedule) {
        this(job, Arrays.asList(schedule));
    }

    public ScheduledJob(JobDetail job, List<Trigger> triggers) {
        this.job      = job;
        this.triggers = triggers;
    }

    public JobDetail getJob() {
        return job;
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}

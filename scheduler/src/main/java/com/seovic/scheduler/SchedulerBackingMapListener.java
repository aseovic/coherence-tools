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


import com.seovic.core.listener.AbstractBackingMapListener;

import com.tangosol.net.BackingMapManagerContext;
import com.tangosol.util.MapEvent;

import java.util.Collections;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Aleksandar Seovic  2011.04.19
 */
public class SchedulerBackingMapListener
        extends AbstractBackingMapListener<JobKey, ScheduledJob> {

    private static Logger LOG = LoggerFactory.getLogger(
            SchedulerBackingMapListener.class);

    private Scheduler scheduler;

    public SchedulerBackingMapListener(BackingMapManagerContext context) {
        super(context);

        SchedulerFactory factory = new StdSchedulerFactory();
        try {
            scheduler = factory.getScheduler();
            scheduler.start();
        }
        catch (SchedulerException e) {
            LOG.error("Failed to initialize Quartz scheduler", e);
        }
    }

    @Override
    public void entryInserted(MapEvent event) {
        ScheduledJob sj = getNewValue(event);
        JobDetail job = sj.getJob();

        try {
            scheduler.scheduleJobs(Collections.singletonMap(job, sj.getTriggers()), false);
            LOG.info("Scheduled job " + job);
        }
        catch (SchedulerException e) {
            LOG.error("Failed to schedule job " + job, e);
        }
    }

    @Override
    public void entryUpdated(MapEvent event) {
        ScheduledJob sj  = getNewValue(event);
        JobDetail    job = sj.getJob();

        try {
            if (sj.isPaused()) {
                scheduler.pauseJob(job.getKey());
                LOG.info("Paused job " + job);
            }
            else {
                scheduler.scheduleJobs(Collections.singletonMap(job, sj.getTriggers()), true);
                LOG.info("Rescheduled job " + job);
            }
        }
        catch (SchedulerException e) {
            LOG.error("Failed to reschedule job " + job, e);
        }
    }

    @Override
    public void entryDeleted(MapEvent event) {
        ScheduledJob sj = getOldValue(event);
        JobDetail job = sj.getJob();

        try {
            scheduler.deleteJob(job.getKey());
            LOG.info("Unscheduled job " + job);
        }
        catch (SchedulerException e) {
            LOG.error("Failed to unschedule job " + job, e);
        }
    }
}

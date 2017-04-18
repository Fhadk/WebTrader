/*
 * Copyright (C) 2017
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.charlieparkerstraat;

import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.charlieparkerstraat.betfair.client.BetfairClient;
import org.charlieparkerstraat.betfair.client.BetfairClientParameters;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;

public class ActionContext {

    private static final Logger LOG = Logger.getLogger(ActionContext.class.getName());
    private final BetfairClient<BetfairClientParameters> client;
    private final JobExecutionContext context;
    private final Scheduler scheduler;
    private final SchedulerContext schedulerContext;
    private final ServletContext servletContext;
    private final long startTimeMillis;
    private final long startTimeNanos;

    public ActionContext(final JobExecutionContext context, final Scheduler scheduler, final SchedulerContext schedulerContext, final ServletContext servletContext, final BetfairClient<BetfairClientParameters> client, final long startTimeNanos, final long startTimeMillis) {
        this.context = context;
        this.scheduler = scheduler;
        this.schedulerContext = schedulerContext;
        this.servletContext = servletContext;
        this.client = client;
        this.startTimeNanos = startTimeNanos;
        this.startTimeMillis = startTimeMillis;
    }

    public BetfairClient<BetfairClientParameters> getClient() {
        return client;
    }

    public JobExecutionContext getContext() {
        return context;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public SchedulerContext getSchedulerContext() {
        return schedulerContext;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public long getStartTimeNanos() {
        return startTimeNanos;
    }
}

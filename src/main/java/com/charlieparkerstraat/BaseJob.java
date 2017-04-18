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

import static com.charlieparkerstraat.ApplicationConstants.MSG_ACTION_IS_NULL;
import static com.charlieparkerstraat.ApplicationConstants.MSG_EXECUTE_COMPLETED;
import static com.charlieparkerstraat.ApplicationConstants.MSG_EXECUTE_ENTERED;
import static com.charlieparkerstraat.ContextFunction.getScheduler;
import static com.charlieparkerstraat.ContextFunction.getSchedulerContext;
import static com.charlieparkerstraat.ContextFunction.getServletContext;
import static com.charlieparkerstraat.ServerEndPointUtil.getBetfairClient;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.charlieparkerstraat.betfair.client.BetfairClient;
import org.charlieparkerstraat.betfair.client.BetfairClientParameters;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;

public abstract class BaseJob implements Job {

    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        Logger.getLogger(BaseJob.class.getName()).log(Level.FINE, MSG_EXECUTE_ENTERED);
        final long startTimeNanos = System.nanoTime();
        final long startTimeMillis = System.currentTimeMillis();
        final Scheduler scheduler = getScheduler(context);
        final SchedulerContext schedulerContext = getSchedulerContext(scheduler);
        final ServletContext servletContext = getServletContext(schedulerContext);
        final BetfairClient<BetfairClientParameters> client = getBetfairClient(servletContext);
        final Action action = getAction();
        if (action != null) {
            final ActionContext actionContext = new ActionContext(context, scheduler, schedulerContext, servletContext, client, startTimeMillis, startTimeNanos);
            action.perform(actionContext);
        } else {
            Logger.getLogger(BaseJob.class.getName()).log(Level.FINE, MSG_ACTION_IS_NULL);
        }
        Logger.getLogger(BaseJob.class.getName()).log(Level.FINE, MSG_EXECUTE_COMPLETED);
    }

    public abstract Action getAction();
}

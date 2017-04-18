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

import static com.charlieparkerstraat.ApplicationConstants.MSG_FAILED_TO_REMOVE_CLIENT_FROM_CONTEXT;
import static com.charlieparkerstraat.ContextFunction.getSchedulerContext;
import static com.charlieparkerstraat.ContextFunction.getServletContext;
import static com.charlieparkerstraat.ContextFunction.setBetfairClient;
import static com.charlieparkerstraat.ServerEndPointUtil.getBetfairClient;
import java.lang.ref.WeakReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.charlieparkerstraat.betfair.client.BetfairClient;
import org.charlieparkerstraat.betfair.client.BetfairClientParameters;
import org.quartz.JobDetail;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

public class BetfairReconnectSchedulerListener implements SchedulerListener {

    private static final Logger LOG = Logger.getLogger(BetfairReconnectSchedulerListener.class.getName());
    private WeakReference<Scheduler> schedulerReference;

    public BetfairReconnectSchedulerListener() {
    }

    public WeakReference<Scheduler> getSchedulerReference() {
        return schedulerReference;
    }

    public void setSchedulerReference(WeakReference<Scheduler> schedulerReference) {
        this.schedulerReference = schedulerReference;
    }

    @Override
    public void jobAdded(JobDetail jobDetail) {
    }

    @Override
    public void jobDeleted(JobKey jobKey) {
    }

    @Override
    public void jobPaused(JobKey jobKey) {
    }

    @Override
    public void jobResumed(JobKey jobKey) {
    }

    @Override
    public void jobScheduled(Trigger trigger) {
    }

    @Override
    public void jobUnscheduled(TriggerKey triggerKey) {
    }

    @Override
    public void jobsPaused(String jobGroup) {
    }

    @Override
    public void jobsResumed(String jobGroup) {
    }

    @Override
    public void schedulerError(String msg, SchedulerException cause) {
    }

    @Override
    public void schedulerInStandbyMode() {
    }

    /**
     * This method is used to close the connection to betfair and remove the reference from servlet context.<br/>
     * Servlet context is used in this project to store variables in a loosely coupled manner. At this point the scheduler has shutdown. There will be no new connection created by the scheduler.
     */
    @Override
    public void schedulerShutdown() {
        LOG.log(Level.FINE, "::: schedulerShutdown");
        ServletContext servletContext = null;
        try {
            final Scheduler scheduler = schedulerReference.get();
            final SchedulerContext schedulerContext = getSchedulerContext(scheduler);
            servletContext = getServletContext(schedulerContext);
            BetfairClient<BetfairClientParameters> client = getBetfairClient(servletContext);
            if (client != null) {
                client.disconnect();
            }
        }
        catch (JobExecutionException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        catch (Throwable ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                setBetfairClient(null, servletContext);
            }
            catch (JobExecutionException ex) {
                LOG.log(Level.WARNING, MSG_FAILED_TO_REMOVE_CLIENT_FROM_CONTEXT, ex);
            }
        }
    }

    @Override
    public void schedulerShuttingdown() {
        LOG.log(Level.INFO, "::: schedulerShuttingdown");
    }

    @Override
    public void schedulerStarted() {
    }

    @Override
    public void schedulerStarting() {
    }

    @Override
    public void schedulingDataCleared() {
    }

    @Override
    public void triggerFinalized(Trigger trigger) {
    }

    @Override
    public void triggerPaused(TriggerKey triggerKey) {
    }

    @Override
    public void triggerResumed(TriggerKey triggerKey) {
    }

    @Override
    public void triggersPaused(String triggerGroup) {
    }

    @Override
    public void triggersResumed(String triggerGroup) {
    }
}

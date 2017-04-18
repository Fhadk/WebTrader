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

import static com.charlieparkerstraat.ApplicationConstants.KEY_BETFAIR_CLIENT;
import static com.charlieparkerstraat.ApplicationConstants.KEY_BETFAIR_CLIENT_RECONNECTION_START_TIME;
import static com.charlieparkerstraat.ApplicationConstants.KEY_BETFAIR_CLIENT_RECONNECTION_TIMEOUT;
import static com.charlieparkerstraat.ApplicationConstants.KEY_SCHEDULER_CONTEXT_SERVLET_CONTEXT;
import static com.charlieparkerstraat.ApplicationConstants.MAXIMUM_TIMEOUT;
import static com.charlieparkerstraat.ApplicationConstants.MINIMUM_TIMEOUT;
import static com.charlieparkerstraat.ApplicationConstants.MSG_BETFAIR_CLIENT_RECONNECTION_IN_PROGRESS;
import static com.charlieparkerstraat.ApplicationConstants.MSG_RECONNECTION_TIMEOUT_TOO_HIGHER;
import static com.charlieparkerstraat.ApplicationConstants.MSG_RECONNECTION_TIMEOUT_TOO_LOWER;
import static com.charlieparkerstraat.ApplicationConstants.MSG_SCHEDULER_CONTEXT_IS_NULL;
import static com.charlieparkerstraat.ApplicationConstants.MSG_SCHEDULER_IS_NULL;
import static com.charlieparkerstraat.ApplicationConstants.MSG_SERVLET_CONTEXT_IS_NULL;
import static com.charlieparkerstraat.ApplicationConstants.MSG_SERVLET_CONTEXT_REFERENCE_IS_NULL;
import java.lang.ref.WeakReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.charlieparkerstraat.betfair.client.BetfairClient;
import org.charlieparkerstraat.betfair.client.BetfairClientParameters;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;

public interface ContextFunction {

    @SuppressWarnings("unchecked")
    public static BetfairClient<BetfairClientParameters> getBetfairClient(final JobExecutionContext context) throws JobExecutionException {
        final Scheduler scheduler = getScheduler(context);
        final SchedulerContext schedulerContext = getSchedulerContext(scheduler);
        final ServletContext servletContext = getServletContext(schedulerContext);
        final WeakReference<BetfairClient> weakreference = (WeakReference<BetfairClient>) ServletContextFunction.<WeakReference>getServletContextAttribute(KEY_BETFAIR_CLIENT, servletContext, WeakReference.class);
        return weakreference.get();
    }

    public static void setBetfairClient(final BetfairClient<BetfairClientParameters> client, final ServletContext servletContext) throws JobExecutionException {
        if (servletContext == null) {
            throw new JobExecutionException(MSG_SERVLET_CONTEXT_IS_NULL);
        }
        servletContext.setAttribute(KEY_BETFAIR_CLIENT, new WeakReference<>(client));
    }

    public static long getReconnectionTimeout(final JobExecutionContext context) throws JobExecutionException {
        long reconnectionTimeout = -1;
        try {
            reconnectionTimeout = context.getJobDetail().getJobDataMap().getLongValue(KEY_BETFAIR_CLIENT_RECONNECTION_TIMEOUT);
        }
        catch (ClassCastException ex) {
            throw new JobExecutionException(ex.getMessage(), ex);
        }
        if (reconnectionTimeout < MINIMUM_TIMEOUT) {
            reconnectionTimeout = MINIMUM_TIMEOUT;
            Logger.getLogger(BaseJob.class.getName()).log(Level.WARNING, MSG_RECONNECTION_TIMEOUT_TOO_LOWER);
        }
        if (reconnectionTimeout > MAXIMUM_TIMEOUT) {
            reconnectionTimeout = MAXIMUM_TIMEOUT;
            Logger.getLogger(BaseJob.class.getName()).log(Level.WARNING, MSG_RECONNECTION_TIMEOUT_TOO_HIGHER);
        }
        return reconnectionTimeout;
    }
//
//    public static Scheduler getScheduler(final WeakReference<ServletContext> weakReference) {
//        return getScheduler(getServletContext(weakReference));
//    }
//
//    public static Scheduler getScheduler(final ServletContext servletContext) {
//        if (servletContext == null) {
//            Logger.getLogger(BaseJob.class.getName()).log(Level.WARNING, MSG_SERVLET_CONTEXT_IS_NULL);
//            return null;
//        }
//        final Object object = servletContext.getAttribute(KEY_QUARTZ_SCHEDULER);
//        if (object == null) {
//            Logger.getLogger(BaseJob.class.getName()).log(Level.WARNING, MSG_SCHEDULER_IS_NULL);
//            return null;
//        }
//        return (Scheduler) ((Reference) object).get();
//    }

    public static ServletContext getServletContext(final WeakReference<ServletContext> weakReference) {
        if (weakReference == null) {
            Logger.getLogger(BaseJob.class.getName()).log(Level.WARNING, MSG_SERVLET_CONTEXT_REFERENCE_IS_NULL);
            return null;
        }
        return weakReference.get();
    }

    public static Scheduler getScheduler(final JobExecutionContext context) throws JobExecutionException {
        final Scheduler scheduler = context.getScheduler();
        if (scheduler == null) {
            throw new JobExecutionException(MSG_SCHEDULER_IS_NULL);
        }
        return scheduler;
    }

    public static SchedulerContext getSchedulerContext(final Scheduler scheduler) throws JobExecutionException {
        if (scheduler == null) {
            throw new JobExecutionException(MSG_SCHEDULER_IS_NULL);
        }
        SchedulerContext schedulerContext = null;
        try {
            schedulerContext = scheduler.getContext();
        }
        catch (SchedulerException ex) {
            Logger.getLogger(BaseJob.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (schedulerContext == null) {
            throw new JobExecutionException(MSG_SCHEDULER_CONTEXT_IS_NULL);
        }
        return schedulerContext;
    }

    public static <T> T getSchedulerContextAttribute(final String key, final SchedulerContext schedulerContext, final Class<T> clazz) {
        T value = null;
        try {
            final Object object = schedulerContext.get(key);
            value = clazz.cast(object);
        }
        catch (ClassCastException ex) {
            Logger.getLogger(BaseJob.class.getName()).warning(ex.getMessage());
        }
        return value;
    }

    public static ServletContext getServletContext(final SchedulerContext schedulerContext) {
        return getSchedulerContextAttribute(KEY_SCHEDULER_CONTEXT_SERVLET_CONTEXT, schedulerContext, ServletContext.class);
    }

    public static boolean isValid(BetfairClient<BetfairClientParameters> client) {
        return client != null && client.getParameters().isLoggedIn() && client.getParameters().getSessionToken() != null && !client.getParameters().getSessionToken().trim().isEmpty();
    }

    public static void throwExceptionWhenReconnectionInProgress(final SchedulerContext schedulerContext, final JobExecutionContext context, final boolean reconnectionInProgress, final long startTime) throws JobExecutionException {
        if (reconnectionInProgress && schedulerContext.containsKey(KEY_BETFAIR_CLIENT_RECONNECTION_START_TIME)) {
            final Long previousStartTime = schedulerContext.getLong(KEY_BETFAIR_CLIENT_RECONNECTION_START_TIME);
            if (startTime - previousStartTime < getReconnectionTimeout(context)) {
                throw new JobExecutionException(MSG_BETFAIR_CLIENT_RECONNECTION_IN_PROGRESS);
            }
        }
    }
}

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
package com.charlieparkerstraat.betfair;

import com.beust.jcommander.JCommander;
import com.charlieparkerstraat.Action;
import com.charlieparkerstraat.ActionContext;
import com.charlieparkerstraat.BaseJob;
import static com.charlieparkerstraat.ApplicationConstants.KEY_BETFAIR_CLIENT_APPLICATION_KEY_DELAYED_ENCRYPTED;
import static com.charlieparkerstraat.ApplicationConstants.KEY_BETFAIR_CLIENT_APPLICATION_KEY_ENCRYPTED;
import static com.charlieparkerstraat.ApplicationConstants.KEY_BETFAIR_CLIENT_DELAYED_MODE;
import static com.charlieparkerstraat.ApplicationConstants.KEY_BETFAIR_CLIENT_KEYSTORE_FILEPATH;
import static com.charlieparkerstraat.ApplicationConstants.KEY_BETFAIR_CLIENT_KEYSTORE_PASSWORD_ENCRYPTED;
import static com.charlieparkerstraat.ApplicationConstants.KEY_BETFAIR_CLIENT_PASSWORD_ENCRYPTED;
import static com.charlieparkerstraat.ApplicationConstants.KEY_BETFAIR_CLIENT_RECONNECTION_IN_PROGRESS;
import static com.charlieparkerstraat.ApplicationConstants.KEY_BETFAIR_CLIENT_RECONNECTION_START_TIME;
import static com.charlieparkerstraat.ApplicationConstants.KEY_BETFAIR_CLIENT_USERNAME;
import static com.charlieparkerstraat.ApplicationConstants.MSG_BETFAIR_CLIENT_ACTIVE;
import static com.charlieparkerstraat.ApplicationConstants.MSG_BETFAIR_CLIENT_CONNECTED;
import static com.charlieparkerstraat.ContextFunction.isValid;
import static com.charlieparkerstraat.ContextFunction.setBetfairClient;
import static com.charlieparkerstraat.ContextFunction.throwExceptionWhenReconnectionInProgress;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.charlieparkerstraat.betfair.client.BetfairClient;
import org.charlieparkerstraat.betfair.client.BetfairClientParameters;
import org.charlieparkerstraat.betfair.client.jsonrpc.BetfairJsonRpcClient;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class BetfairReconnectJob extends BaseJob {

    private static final Logger LOG = Logger.getLogger(BetfairReconnectJob.class.getName());

    private BetfairClient<BetfairClientParameters> createAndOpenConnection(final JobExecutionContext context) throws Throwable {
        final JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        BetfairClient<BetfairClientParameters> client;
        String[] argz = {
            "-betfair-username", jobDataMap.getString(KEY_BETFAIR_CLIENT_USERNAME),
            "-betfair-password-encrypted", jobDataMap.getString(KEY_BETFAIR_CLIENT_PASSWORD_ENCRYPTED),
            "-keystore-password-encrypted", jobDataMap.getString(KEY_BETFAIR_CLIENT_KEYSTORE_PASSWORD_ENCRYPTED),
            "-application-key-encrypted", jobDataMap.getString(KEY_BETFAIR_CLIENT_APPLICATION_KEY_ENCRYPTED),
            "-application-key-delayed-encrypted", jobDataMap.getString(KEY_BETFAIR_CLIENT_APPLICATION_KEY_DELAYED_ENCRYPTED),
            "-keystore-filepath", jobDataMap.getString(KEY_BETFAIR_CLIENT_KEYSTORE_FILEPATH),
            jobDataMap.getBoolean(KEY_BETFAIR_CLIENT_DELAYED_MODE) ? "" : "-delayed-mode"
        };
        BetfairClientParameters params = new BetfairClientParameters();
        new JCommander(params, argz);
        client = new BetfairJsonRpcClient<>(params);
        client.connect();
        return client;
    }

    @Override
    public Action getAction() {
        return (final ActionContext actionContext) -> {
//            addServletContextAttributeIfAbsent(KEY_QUARTZ_SCHEDULER, actionContext.getScheduler(), actionContext.getServletContext());
//            addSchedulerListenerIfAbsent(INSTANCE_SCHEDULER_LISTENER, actionContext.getScheduler());
//            createCacheManagerIfAbsent(actionContext.getServletContext());
            if (!isValid(actionContext.getClient())) {
                final boolean reconnectionInProgress = actionContext.getSchedulerContext().putIfAbsent(KEY_BETFAIR_CLIENT_RECONNECTION_IN_PROGRESS, true) == null;
                throwExceptionWhenReconnectionInProgress(actionContext.getSchedulerContext(), actionContext.getContext(), reconnectionInProgress, actionContext.getStartTimeMillis());
                actionContext.getSchedulerContext().put(KEY_BETFAIR_CLIENT_RECONNECTION_START_TIME, actionContext.getStartTimeMillis());
                BetfairClient<BetfairClientParameters> newClient = null;
                try {
                    newClient = createAndOpenConnection(actionContext.getContext());
                }
                catch (Throwable th) {
                    LOG.log(Level.SEVERE, null, th);
                    onConnectError();
                }
                finally {
                    actionContext.getSchedulerContext().remove(KEY_BETFAIR_CLIENT_RECONNECTION_IN_PROGRESS);
                    actionContext.getSchedulerContext().remove(KEY_BETFAIR_CLIENT_RECONNECTION_START_TIME);
                }
                try {
                    if (newClient != null) {
                        setBetfairClient(newClient, actionContext.getServletContext());
                    }
                }
                catch (JobExecutionException ex) {
                    LOG.log(Level.WARNING, "Error occurred saving reference to the new connection", ex);
                }
                onConnectSuccess();
                LOG.log(Level.INFO, MSG_BETFAIR_CLIENT_CONNECTED + " in {0} nanoseconds", System.nanoTime() - actionContext.getStartTimeNanos());
            } else {
                LOG.log(Level.FINE, MSG_BETFAIR_CLIENT_ACTIVE);
            }
        };
    }

    private void onConnectError() {
    }

    private void onConnectSuccess() {
    }
}

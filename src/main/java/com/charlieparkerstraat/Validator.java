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

import static com.charlieparkerstraat.ApplicationConstants.BETFAIR_CLIENT_IS_NULL;
import static com.charlieparkerstraat.ApplicationConstants.BETFAIR_CLIENT_IS_SET;
import static com.charlieparkerstraat.ApplicationConstants.DISPATCHER_IS_NULL;
import static com.charlieparkerstraat.ApplicationConstants.DISPATCHER_IS_SET;
import static com.charlieparkerstraat.ApplicationConstants.HTTP_SESSION_IS_NULL;
import static com.charlieparkerstraat.ApplicationConstants.HTTP_SESSION_IS_SET;
import static com.charlieparkerstraat.ApplicationConstants.SERVLET_CONTEXT_IS_NULL;
import static com.charlieparkerstraat.ApplicationConstants.SERVLET_CONTEXT_IS_SET;
import static com.charlieparkerstraat.ApplicationConstants.THREAD_LOCAL_HANDSHAKE_REQUEST_IS_NULL;
import static com.charlieparkerstraat.ApplicationConstants.THREAD_LOCAL_HANDSHAKE_REQUEST_IS_SET;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.websocket.server.HandshakeRequest;
import org.charlieparkerstraat.betfair.client.BetfairClient;
import org.charlieparkerstraat.betfair.client.BetfairClientParameters;

public class Validator {

    private static final Logger LOG = Logger.getLogger(Validator.class.getName());

    public static void validateBetfairClient(final BetfairClient<BetfairClientParameters> betfairClient) {
        if (betfairClient != null) {
            LOG.log(Level.INFO, BETFAIR_CLIENT_IS_SET);
        } else {
            LOG.log(Level.WARNING, BETFAIR_CLIENT_IS_NULL);
        }
    }

    public static void validateDispatcher(Dispatcher dispatcher) {
        if (dispatcher != null) {
            LOG.log(Level.INFO, DISPATCHER_IS_SET);
        } else {
            LOG.log(Level.WARNING, DISPATCHER_IS_NULL);
        }
    }

    public static void validateHttpSession(final HttpSession httpSession) {
        if (httpSession != null) {
            LOG.log(Level.INFO, HTTP_SESSION_IS_SET);
        } else {
            LOG.log(Level.WARNING, HTTP_SESSION_IS_NULL);
        }
    }

    public static void validateServletContext(final ServletContext servletContext) {
        if (servletContext != null) {
            LOG.log(Level.INFO, SERVLET_CONTEXT_IS_SET);
        } else {
            LOG.log(Level.WARNING, SERVLET_CONTEXT_IS_NULL);
        }
    }
//
//    public static void validateThreadLocalContext(final WebSocketRequestDataContext context) {
//        if (context != null) {
//            LOG.log(Level.INFO, CONTEXT_IS_SET);
//        } else {
//            LOG.log(Level.WARNING, CONTEXT_IS_NULL);
//        }
//    }

    public static void validateThreadLocalRequest(final HandshakeRequest handshakeRequest) {
        if (handshakeRequest != null) {
            LOG.log(Level.INFO, THREAD_LOCAL_HANDSHAKE_REQUEST_IS_SET);
        } else {
            LOG.log(Level.WARNING, THREAD_LOCAL_HANDSHAKE_REQUEST_IS_NULL);
        }
    }

    private Validator() {
    }
}

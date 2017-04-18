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
import static com.charlieparkerstraat.ApplicationConstants.MSG_SERVLET_CONTEXT_IS_NULL;
import static com.charlieparkerstraat.ApplicationConstants.THREAD_LOCAL_HANDSHAKE_REQUEST;
import static com.charlieparkerstraat.ServletContextFunction.getServletContextAttribute;
import static com.charlieparkerstraat.Validator.validateBetfairClient;
import static com.charlieparkerstraat.Validator.validateHttpSession;
import static com.charlieparkerstraat.Validator.validateServletContext;
import static com.charlieparkerstraat.Validator.validateThreadLocalRequest;
import java.lang.ref.WeakReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.websocket.server.HandshakeRequest;
import org.charlieparkerstraat.betfair.client.BetfairClient;
import org.charlieparkerstraat.betfair.client.BetfairClientParameters;

public class ServerEndPointUtil {

    private static final Logger LOG = Logger.getLogger(ServerEndPointUtil.class.getName());

    @SuppressWarnings("unchecked")
    public static BetfairClient<BetfairClientParameters> getBetfairClient(final ServletContext servletContext) throws NullPointerException {
        if (servletContext == null) {
            throw new NullPointerException(MSG_SERVLET_CONTEXT_IS_NULL);
        }
        final Object object = getServletContextAttribute(KEY_BETFAIR_CLIENT, servletContext, WeakReference.class);
        if (object == null) {
            return null;
        }
        return ((WeakReference<BetfairClient>) object).get();
    }

    public static BetfairClient<BetfairClientParameters> getBetfairConnection(final ServletContext servletContext) {
        BetfairClient<BetfairClientParameters> betfairClient = null;
        try {
            betfairClient = getBetfairClient(servletContext);
        }
        catch (NullPointerException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        validateBetfairClient(betfairClient);
        return betfairClient;
    }

    public static HandshakeRequest getAndRemoveHandshakeRequest() {
        final HandshakeRequest value = THREAD_LOCAL_HANDSHAKE_REQUEST.get();
        // Remove to prevent classloader leak
        THREAD_LOCAL_HANDSHAKE_REQUEST.remove();
        validateThreadLocalRequest(value);
        return value;
    }

    public static HttpSession getHttpSession(final HandshakeRequest threadLocalRequest) {
        validateThreadLocalRequest(threadLocalRequest);
        HttpSession httpSession = null;
        if (threadLocalRequest != null) {
            httpSession = (HttpSession) threadLocalRequest.getHttpSession();
        }
        validateHttpSession(httpSession);
        return httpSession;
    }

    public static ServletContext getServletContext(final HttpSession httpSession) {
        validateHttpSession(httpSession);
        ServletContext servletContext = null;
        if (httpSession != null) {
            servletContext = httpSession.getServletContext();
        }
        validateServletContext(servletContext);
        return servletContext;
    }

    private ServerEndPointUtil() {
    }
}

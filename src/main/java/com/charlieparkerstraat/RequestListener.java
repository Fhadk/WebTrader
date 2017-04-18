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
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

/**
 * Web application life cycle listener.
 *
 * <pre>
 * This class forces the creation of httpSession to solve the problem of accessing the http session from websocket handshake.
 * The problem is that httpSession returns null during handshaking making it impossible to access the httpsession from websocket.
 *
 * From the HandshakeRequest.getHttpSession javadoc:
 *
 * \/**
 * The method getHttpSession returns a reference to the HttpSession that the web socket handshake that
 * started this conversation was part of, if the implementation
 * is part of a Java EE web container.
 *
 * return the http session or {@code null} if either the websocket implementation is not part of a Java EE web
 * container, or there is no HttpSession associated with the opening handshake request. \/ Problem is, that HttpSession
 * was not yet created for your client connection and WebSocket API implementation just asks whether there is something
 * created and if not, it does not create it. What you need to do is call httpServletRequest.getSession() sometime
 * before WebSocket impl filter is invoked (doFilter(...) is called).
 *
 * This can be achieved for example by calling mentioned method in ServletRequestListener#requestInitalized or in
 * different filter, etc..
 *
 * </pre>
 *
 * After adding the implementation below, the httpsession was available by calling method getHttpSession in the web socket handshake
 *
 */
public class RequestListener implements ServletRequestListener {

    private static final Logger LOG = Logger.getLogger(RequestListener.class.getName());

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        // Force initialize session and servlet context
        ((HttpServletRequest) sre.getServletRequest()).getSession();
    }

}

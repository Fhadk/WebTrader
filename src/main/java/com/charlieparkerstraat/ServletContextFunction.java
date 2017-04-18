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

import static com.charlieparkerstraat.ApplicationConstants.MSG_SERVLET_ATTRIBUTE_IS_NULL;
import static com.charlieparkerstraat.ApplicationConstants.MSG_SERVLET_CONTEXT_IS_NULL;
import java.lang.ref.WeakReference;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.quartz.Scheduler;

public interface ServletContextFunction {

    public static void addServletContextAttributeIfAbsent(final String key, final Scheduler scheduler, final ServletContext servletContext) {
        if (servletContext.getAttribute(key) == null) {
            servletContext.setAttribute(key, new WeakReference<>(scheduler));
        }
    }

    @SuppressWarnings("unchecked")
    public static <T, R> R getOrCreateIfAbsent(String key, final Object MONITOR, final ServletContext servletContext, Function<T, R> creationFunction) {
        WeakReference<R> weakReference;
        synchronized (MONITOR) {
            weakReference = (WeakReference<R>) servletContext.getAttribute(key);
            if (weakReference == null) {
                R value = creationFunction.apply(null);
                weakReference = new WeakReference<>(value);
                servletContext.setAttribute(key, weakReference);
            }
        }
        return weakReference.get();
    }

    public static <T> T getServletContextAttribute(final String key, final ServletContext servletContext, final Class<T> clazz) {
        if (servletContext == null) {
            Logger.getLogger(BaseJob.class.getName()).log(Level.WARNING, MSG_SERVLET_CONTEXT_IS_NULL);
            return null;
        }
        T value = null;
        final Object object = servletContext.getAttribute(key);
        if (object == null) {
            Logger.getLogger(BaseJob.class.getName()).log(Level.WARNING, MSG_SERVLET_ATTRIBUTE_IS_NULL, key);
            return null;
        }
        try {
            value = clazz.cast(object);
        }
        catch (ClassCastException ex) {
            Logger.getLogger(BaseJob.class.getName()).warning(ex.getMessage());
        }
        return value;
    }
}

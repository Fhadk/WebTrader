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

import static com.charlieparkerstraat.ApplicationConstants.AFTER_QUARTZ_JOB_TRIGGERED;
import static com.charlieparkerstraat.ApplicationConstants.BEFORE_QUARTZ_JOB_TRIGGERED;
import static com.charlieparkerstraat.ApplicationConstants.KEY_SUPPORTED_RESPONSE_MESSAGE_TYPE;
import static com.charlieparkerstraat.ApplicationConstants.MSG_BETFAIR_RESPONSE_MESSAGE_TYPE_IS_NULL;
import static com.charlieparkerstraat.ApplicationConstants.MSG_MESSAGE_IS_NULL;
import static com.charlieparkerstraat.ApplicationConstants.MSG_SCHEDULER_IS_NULL;
import static com.charlieparkerstraat.ApplicationConstants.MSG_SUBSCRIBED_SUCCESSFULLY_BUT_ACK_ERROR;
import static com.charlieparkerstraat.ApplicationConstants.MSG_SUBSCRIBER_IS_NULL;
import static com.charlieparkerstraat.ApplicationConstants.MSG_UNSUBSCRIBED_SUCCESSFULLY_BUT_ACK_ERROR;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;

public class Dispatcher {

    private static final Logger LOG = Logger.getLogger(Dispatcher.class.getName());

    /**
     * The Singleton Instance.
     */
    private static volatile Dispatcher singleton;

    public static Dispatcher getInstance() {
        return singleton;
    }

    /**
     * A factory method to create a singleton CacheManager with default config, or return it if it exists.
     * <p/>
     * The configuration will be read, {@link Ehcache}s created and required stores initialized. When the {@link CacheManager} is no longer required, call shutdown to free resources.
     *
     * @param servletContext
     * @param scheduler
     * @return the singleton CacheManager
     * @throws CacheException if the CacheManager cannot be created
     */
    public static Dispatcher getInstance(final ServletContext servletContext, final Scheduler scheduler) {
        synchronized (Dispatcher.class) {
            if (singleton == null) {
                singleton = new Dispatcher(servletContext, scheduler);
            }
            return singleton;
        }
    }

    private final Map MESSAGE_TYPES;
    private final WeakReference<Scheduler> QUARTZ_SCHEDULER_WEAK_REFERENCE;
    private final Map RESPONSE_MESSAGES;
    private final WeakReference<ServletContext> SERVLET_CONTEXT_WEAK_REFERENCE_;

    private Dispatcher(final ServletContext servletContext, final Scheduler scheduler) {
        this.SERVLET_CONTEXT_WEAK_REFERENCE_ = new WeakReference<>(servletContext);
        this.QUARTZ_SCHEDULER_WEAK_REFERENCE = new WeakReference<>(scheduler);
        this.MESSAGE_TYPES = new ConcurrentHashMap<>(0);
        this.RESPONSE_MESSAGES = new ConcurrentHashMap<>(0);
    }

    private <T> void acknowledgeSubscription(Subscriber<T> subscriber) {
        try {
            subscriber.subscribed();
        }
        catch (Exception ex) {
            LOG.log(Level.WARNING, subscriber.toString() + MSG_SUBSCRIBED_SUCCESSFULLY_BUT_ACK_ERROR, ex);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> void dispatch(T responseMessage, final Class<T> responseMessageType) {
        this.RESPONSE_MESSAGES.put(responseMessageType, responseMessage);
        final Set<Subscriber<T>> subscribers = this.<T>getSubscribers(responseMessageType);
        subscribers.parallelStream().forEach((subscriber) -> dispatch(responseMessage, subscriber));
    }

    @SuppressWarnings("unchecked")
    public <T> void dispatch(List<T> responseMessage, final Class<T> responseMessageType) {
        this.RESPONSE_MESSAGES.put(responseMessageType, responseMessage);
        final Set<Subscriber<T>> subscribers = this.<T>getSubscribers(responseMessageType);
        subscribers.parallelStream().forEach((subscriber) -> dispatch(responseMessage, subscriber));
    }

    private <T> void dispatch(T responseMessage, Subscriber<T> subscriber) {
        if (subscriber != null) {
            subscriber.dispatch(responseMessage);
        } else {
            LOG.log(Level.WARNING, MSG_SUBSCRIBER_IS_NULL);
        }
    }

    private <T> void dispatch(List<T> responseMessages, Subscriber<T> subscriber) {
        if (subscriber != null) {
            subscriber.dispatch(responseMessages);
        } else {
            LOG.log(Level.WARNING, MSG_SUBSCRIBER_IS_NULL);
        }
    }

    private <T> void dispatchLastReceivedMessage(Class<T> responseMessageType, Subscriber<T> subscriber) {
        @SuppressWarnings("unchecked")
        final T message = (T) RESPONSE_MESSAGES.get(responseMessageType);
        if (message != null) {
            dispatch(message, subscriber);
        } else {
            LOG.log(Level.WARNING, MSG_MESSAGE_IS_NULL);
            request(responseMessageType);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Set<Subscriber<T>> getSubscribers(Class<T> messageType) {
        Set<Subscriber<T>> subscribers = (Set<Subscriber<T>>) MESSAGE_TYPES.get(messageType);
        if (subscribers == null) {
            subscribers = new HashSet<>(0);
            MESSAGE_TYPES.put(messageType, subscribers);
        }
        return subscribers;
    }

    public <T> void request(Class<T> responseMessageType) {
        this.<T>triggerJobWithMatchingResponseMessageType(responseMessageType, QUARTZ_SCHEDULER_WEAK_REFERENCE.get());
    }

    public void shutdown() {
        // stop any threads that may exist in this class
    }

    public <T> void subscribe(Class<T> responseMessageType, Subscriber<T> subscriber) {
        final Set<Subscriber<T>> sunscribers = getSubscribers(responseMessageType);
        if (sunscribers.add(subscriber)) {
            this.<T>acknowledgeSubscription(subscriber);
            this.<T>dispatchLastReceivedMessage(responseMessageType, subscriber);
        }
    }

    private <T> void triggerJobWithMatchingResponseMessageType(final Class<T> responseMessageType, final Scheduler scheduler) {
        if (scheduler == null) {
            LOG.log(Level.WARNING, MSG_SCHEDULER_IS_NULL);
            return;
        }
        if (responseMessageType == null) {
            LOG.log(Level.WARNING, MSG_BETFAIR_RESPONSE_MESSAGE_TYPE_IS_NULL);
            return;
        }
        try {
            for (final String jobGroupName : scheduler.getJobGroupNames()) {
                for (final JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(jobGroupName))) {
                    final JobDetail jobDetails = scheduler.getJobDetail(jobKey);
                    final String supportedResponse = jobDetails.getJobDataMap().getString(KEY_SUPPORTED_RESPONSE_MESSAGE_TYPE);
                    if (supportedResponse != null && supportedResponse.equalsIgnoreCase(responseMessageType.getName())) {
                        LOG.log(Level.WARNING, BEFORE_QUARTZ_JOB_TRIGGERED, new Object[]{jobKey.getName(), jobKey.getGroup()});
                        scheduler.triggerJob(jobKey);
                        LOG.log(Level.WARNING, AFTER_QUARTZ_JOB_TRIGGERED, new Object[]{jobKey.getName(), jobKey.getGroup()});
                    }
                }
            }
        }
        catch (SchedulerException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    public <T> void unsubscribe(Subscriber<T> subscriber, Class<T> messageType) {
        final Set<Subscriber<T>> sunscribers = getSubscribers(messageType);
        if (sunscribers.<T>remove(subscriber)) {
            try {
                subscriber.unsubscribed();
            }
            catch (Exception ex) {
                LOG.log(Level.WARNING, subscriber.toString() + MSG_UNSUBSCRIBED_SUCCESSFULLY_BUT_ACK_ERROR, ex);
            }
        }
    }

}

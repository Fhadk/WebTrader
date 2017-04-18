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

import com.betfair.sports.api.CompetitionResult;
import com.betfair.sports.api.CountryCodeResult;
import com.betfair.sports.api.EventResult;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.websocket.server.HandshakeRequest;

public interface ApplicationConstants {

    static final String AFTER_QUARTZ_JOB_TRIGGERED = "After Quartz job triggered job key name: {0} job key group: {1}";
    static final String BEFORE_QUARTZ_JOB_TRIGGERED = "Before Quartz job triggered job key name: {0} job key group: {1}";
    static final String KEY_BETFAIR_CLIENT_DELAYED_MODE = "betfair-client.delayed-mode";
    static final String KEY_BETFAIR_CLIENT_KEYSTORE_FILEPATH = "betfair-client.keystore-filepath";
    static final String KEY_BETFAIR_CLIENT_APPLICATION_KEY_DELAYED_ENCRYPTED = "betfair-client.application-key-delayed-encrypted";
    static final String KEY_BETFAIR_CLIENT_APPLICATION_KEY_ENCRYPTED = "betfair-client.application-key-encrypted";
    static final String KEY_BETFAIR_CLIENT_KEYSTORE_PASSWORD_ENCRYPTED = "betfair-client.keystore-password-encrypted";
    static final String KEY_BETFAIR_CLIENT_PASSWORD_ENCRYPTED = "betfair-client.password-encrypted";
    static final String KEY_BETFAIR_CLIENT_USERNAME = "betfair-client.username";
    static final String KEY_BETFAIR_STATEMENT_INCLUDE_ITEM = "betfair-statement-include-item";
    static final String KEY_BETFAIR_STATEMENT_WALLET = "betfair-statement-wallet";
    static final String KEY_BETFAIR_STATEMENT_RANGE_IN_HOURS = "betfair-statement-range-in-hours";
    static final String KEY_BETFAIR_LIST_CURRENT_ORDER_PROJECTION = "betfair-list-current-order-projection";
    static final String KEY_BETFAIR_LIST_CURRENT_ORDER_BY = "betfair-list-current-order-by";
    static final String KEY_BETFAIR_LIST_CURRENT_ORDER_SORT_DIRECTION = "betfair-list-current-order-sort-direction";
    static final String KEY_BETFAIR_TURNING_IN_PLAY = "betfair-turning-in-play";
    static final String KEY_BETFAIR_EVENT_TYPE_IDS = "betfair-event-type-ids";
    static final String KEY_BETFAIR_EXCHANGE_IDS = "betfair-exchange-ids";
    static final String KEY_BETFAIR_MARKET_BETTING_TYPES = "betfair-market-betting-types";
    static final String KEY_BETFAIR_MARKET_PROJECTIONS = "betfair-market-projections";
    static final String KEY_BETFAIR_MARKET_SORT = "betfair-market-sort";
    static final String KEY_BETFAIR_MAXIMUM_RESULTS = "betfair-maximum-results";
    static final String KEY_BETFAIR_MARKET_TYPE_CODES = "betfair-market-type-codes";
    static final String KEY_BETFAIR_LOCALE = "betfair-locale";
    static final String KEY_BETFAIR_FROM_HOURS = "betfair-from-hours";
    static final String KEY_BETFAIR_TO_HOURS = "betfair-to-hours";
    static final String KEY_BETFAIR_LIST_CLEARED_ORDERS_TIME_RANGE_IN_HOURS = "betfair-list-cleared-orders-time-range-in-hours";
    static final String KEY_SUPPORTED_RESPONSE_MESSAGE_TYPE = "supported-betfair-response-message-type";
    static final String KEY_QUARTZ_SCHEDULER = "quartz-scheduler";
    static final String KEY_HANDSHAKE_REQUEST = "HANDSHAKE_REQUEST";
    static final String MSG_DISPATCHER_ALREADY_EXISTS = "Dispatcher already exists. Another one will not be created";
    static final String MSG_BETFAIR_CLIENT_IS_NULL = "betfair client is null (not set)";
    static final String MSG_BETFAIR_MESSAGE_TYPE_NOT_SUPPORTED = "betfair response message type {0} not supported";
    static final String MSG_USER_SESSION_IS_NULL = "userSession context is null (not set)";
    static final String MSG_DISPATCHER_IS_NULL = "dispatcher context is null (not set)";
    static final String KEY_BETFAIR_CLIENT_RECONNECTION_START_TIME = "betfair-client.reconnection-start-time";
    static final String KEY_BETFAIR_CLIENT_RECONNECTION_IN_PROGRESS = "betfair-client.reconnection-in-progress";
    static final String KEY_BETFAIR_CLIENT_RECONNECTION_TIMEOUT = "betfair-client.reconnection-timeout";
    static final String KEY_BETFAIR_CLIENT = "betfair-client";
    static final String KEY_BETFAIR_RESPONSE_MESSAGE_DISPATCHER = "betfair-response-message-dispatcher";
    static final String KEY_SCHEDULER_CONTEXT_SERVLET_CONTEXT = "scheduler-context-servlet-context-key";
    static final String MSG_BETFAIR_CLIENT_ACTIVE = "betfair client reconnection not required. client is already active";
    static final String MSG_BETFAIR_CLIENT_CONNECTED = "betfair client reconnection successful.";
    static final String MSG_BETFAIR_CLIENT_RECONNECTION_IN_PROGRESS = "betfair client reconnection is already in progress";
    static final String MSG_FAILED_TO_REGISTER_SCHEDULER_LISTENER = "Failed to register scheduler listener";
    static final String MSG_JOB_EXECUTION_CONTEXT_IS_NULL = "job execution context is null (not set)";
    static final String MSG_RECONNECTION_TIMEOUT_TOO_HIGHER = "configured reconnection timeout is higher than maximum allowed. default value will be used instead.";
    static final String MSG_RECONNECTION_TIMEOUT_TOO_LOWER = "configured reconnection timeout is lower than minimum allowed. default value will be used instead.";
    static final String MSG_SCHEDULER_CONTEXT_IS_NULL = "scheduler context is null (not set)";
    static final String MSG_SERVLET_CONTEXT_IS_NULL = "servlet context is null (not set)";
    static final String MSG_SERVLET_ATTRIBUTE_IS_NULL = "servlet atribute {0} is null (not set)";
    static final String MSG_SERVLET_CONTEXT_REFERENCE_IS_NULL = "servlet context reference is null (not set)";
    static final String MSG_SCHEDULER_IS_NULL = "scheduler is null (not set)";
    static final String MSG_BETFAIR_RESPONSE_MESSAGE_TYPE_IS_NULL = "betfair response message type is null (not set)";
    static final String MSG_ACTION_IS_NULL = "action is null (not set)";
    static final String MSG_MESSAGE_IS_NULL = "message is null (not set)";
    static final String MSG_SUBSCRIBER_IS_NULL = "subscriber is null (not set)";
    static final String MSG_EXECUTE_COMPLETED = "execute completed";
    static final String MSG_EXECUTE_ENTERED = "execute entered";
    static final String MSG_ALREADY_SUBSCRIBED = "{0} is already subscribed. It is not going to be subscribed again.";
    static final String MSG_SUBSCRIBED_SUCCESSFULLY_BUT_ACK_ERROR = " subscribed successfully but an error occured while sending acknowledgement.";
    static final String MSG_UNSUBSCRIBED_SUCCESSFULLY_BUT_ACK_ERROR = " unsubscribed successfully but an error occured while sending acknowledgement.";
    static final String MSG_FAILED_TO_REMOVE_CLIENT_FROM_CONTEXT = "Failed to remove client from the context after closing connection";
    static final long MAXIMUM_TIMEOUT = 1000L * 100L;
    static final long MINIMUM_TIMEOUT = 1000L * 10L;
    static final String BETFAIR_JSON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static final String BETFAIR_CLIENT_IS_NULL = "betfairClient is null";
    static final String BETFAIR_CLIENT_IS_SET = "betfairClient is set";
    static final String CONTEXT_IS_NULL = "context is null";
    static final String CONTEXT_IS_SET = "context is set";
    static final String DISPATCHER_IS_NULL = "dispatcher is null";
    static final String DISPATCHER_IS_SET = "dispatcher is set";
    static final String HTTP_SESSION_IS_NULL = "httpSession is null";
    static final String HTTP_SESSION_IS_SET = "httpSession is set";
    static final String SERVLET_CONTEXT_IS_NULL = "servletContext is null";
    static final String SERVLET_CONTEXT_IS_SET = "servletContext is set";
    static final String THREAD_LOCAL_HANDSHAKE_REQUEST_IS_NULL = "threadlocal.handshakeRequest is null";
    static final String THREAD_LOCAL_HANDSHAKE_REQUEST_IS_SET = "threadlocal.handshakeRequest is set";
    static final Type TYPE_LIST_COMPETITION_RESULT = new TypeToken<List<CompetitionResult>>() {
    }.getType();
    static final Type TYPE_LIST_COUNTRY_CODE_RESULT = new TypeToken<List<CountryCodeResult>>() {
    }.getType();
    static final Type TYPE_LIST_EVENT_RESULT = new TypeToken<List<EventResult>>() {
    }.getType();
    static final ThreadLocal<HandshakeRequest> THREAD_LOCAL_HANDSHAKE_REQUEST = new ThreadLocal<HandshakeRequest>() {

        @Override
        protected HandshakeRequest initialValue() {
            return null;
        }
    };
}
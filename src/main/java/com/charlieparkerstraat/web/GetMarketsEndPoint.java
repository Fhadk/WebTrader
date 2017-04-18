package com.charlieparkerstraat.web;

import com.betfair.sports.api.MarketCatalogue;
import static com.charlieparkerstraat.ApplicationConstants.BETFAIR_JSON_DATE_FORMAT;
import static com.charlieparkerstraat.ApplicationConstants.MSG_BETFAIR_CLIENT_IS_NULL;
import static com.charlieparkerstraat.ApplicationConstants.MSG_BETFAIR_MESSAGE_TYPE_NOT_SUPPORTED;
import static com.charlieparkerstraat.ApplicationConstants.MSG_DISPATCHER_IS_NULL;
import static com.charlieparkerstraat.ApplicationConstants.MSG_USER_SESSION_IS_NULL;
import com.charlieparkerstraat.Dispatcher;
import com.charlieparkerstraat.ServerEndPointConfigurator;
import static com.charlieparkerstraat.ServerEndPointUtil.getAndRemoveHandshakeRequest;
import static com.charlieparkerstraat.ServerEndPointUtil.getBetfairConnection;
import static com.charlieparkerstraat.ServerEndPointUtil.getHttpSession;
import static com.charlieparkerstraat.ServerEndPointUtil.getServletContext;
import com.charlieparkerstraat.Subscriber;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpoint;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.charlieparkerstraat.betfair.client.BetfairClient;
import org.charlieparkerstraat.betfair.client.BetfairClientParameters;
import org.charlieparkerstraat.betfair.converters.JsonConverter;

/**
 * This method combines the following data to produce a model that can be displayed on the ui.
 * <pre>
 * == GetMarkets ==
 * ListCountry
 * ListCompetitions
 * ListEventTypes
 * ListEvents
 * ListMarketTypes
 * ListMarketCatalogue
 * ListMarketBook
 * </pre>
 *
 * @author R584285
 */
@ServerEndpoint(value = "/getmarkets", configurator = ServerEndPointConfigurator.class)
public class GetMarketsEndPoint implements Subscriber<MarketCatalogue> {

    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(GetMarketsEndPoint.class.getName());
    private final JsonConverter JSON_CONVERTER = new JsonConverter(BETFAIR_JSON_DATE_FORMAT);
    private WeakReference<ServletContext> servletContextWeakReference;
    private WeakReference<HandshakeRequest> threadLocalRequestWeakReference;
    private WeakReference<Session> userSessionWeakReference;

    public GetMarketsEndPoint() {
    }

    /**
     * Dispatch is the method called by the dispatcher to send messages to the subscribers.
     *
     * @param <T>
     * @param message
     */
    @Override
    public <T> void dispatch(T message) {
        final BetfairClient<BetfairClientParameters> connection = getBetfairConnection(servletContextWeakReference.get());
        if (connection == null) {
            LOG.log(Level.WARNING, MSG_BETFAIR_CLIENT_IS_NULL);
            return;
        }
        if (!(message instanceof List)) {
            LOG.log(Level.WARNING, MSG_BETFAIR_MESSAGE_TYPE_NOT_SUPPORTED, new Object[]{message.getClass().getName()});
            return;
        }
        try {
            @SuppressWarnings("unchecked")
            final List<MarketCatalogue> marketCatalogues = (List<MarketCatalogue>) message;
            LOG.log(Level.INFO, ToStringBuilder.reflectionToString(marketCatalogues));
            final String json = JSON_CONVERTER.convertToJson(marketCatalogues);
            LOG.log(Level.INFO, "to frontend: json: {0}", json);
            final Session userSession = this.userSessionWeakReference.get();
            if (userSession != null) {
                userSession.getAsyncRemote().sendText(json);
            } else {
                LOG.log(Level.WARNING, MSG_USER_SESSION_IS_NULL);
            }
        }
        catch (Throwable ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Remove any variables and references created to allow garbage collection.
     *
     * @param userSession
     */
    @OnClose
    public void onClose(Session userSession) {
        LOG.log(Level.INFO, "onClose - userSession.getId: {0}", this.getClass());
        final Dispatcher dispatcher = Dispatcher.getInstance();
        if (dispatcher != null) {
            dispatcher.unsubscribe(this, MarketCatalogue.class);
        } else {
            LOG.log(Level.WARNING, MSG_DISPATCHER_IS_NULL);
        }
        this.servletContextWeakReference = null;
        this.threadLocalRequestWeakReference = null;
        this.userSessionWeakReference = null;
    }

    @OnMessage
    public void onMessage(String message, Session userSession) {
        LOG.log(Level.INFO, "onMessage - userSession.getId: {0}, message: {1}", new Object[]{this.getClass(), message});
//        final HttpSession httpSession = getHttpSession(this.threadLocalRequest);
//        final ServletContext servletContext = getServletContext(httpSession);
//        try {
//            Map<String, Object> parameter = JSON_CONVERTER.convertFromJson(message, TYPE_MAP_STRING_OBJECT);
//
//            if ("read".equals(parameter.get("event"))) {
//                onOpen(userSession, this.config);
//            }
//        } catch (Error ex) {
//            LOG.log(Level.SEVERE, null, ex);
//        }
    }

    /**
     *
     * <pre>
     * == GetMarkets ==
     * ListCountry
     * ListCompetitions
     * ListEventTypes
     * ListEvents
     * ListMarketTypes
     * ListMarketCatalogue
     * ListMarketBook
     * </pre>
     *
     * @param userSession
     * @param config
     */
    @OnOpen
    public void onOpen(final Session userSession, final EndpointConfig config) {
        LOG.log(Level.INFO, "onOpen - userSession.getId: {0}", this.getClass());
        this.userSessionWeakReference = new WeakReference<>(userSession);
        this.threadLocalRequestWeakReference = new WeakReference<>(getAndRemoveHandshakeRequest());
        final HttpSession httpSession = getHttpSession(this.threadLocalRequestWeakReference.get());
        this.servletContextWeakReference = new WeakReference<>(getServletContext(httpSession));
        final Dispatcher dispatcher = Dispatcher.getInstance();
        if (dispatcher != null) {
            dispatcher.subscribe(MarketCatalogue.class, this);
        } else {
            LOG.log(Level.WARNING, MSG_DISPATCHER_IS_NULL);
        }
    }

    @Override
    public void subscribed() {
        LOG.log(Level.INFO, "subscribed");
    }

    @Override
    public void unsubscribed() {
        LOG.log(Level.INFO, "unsubscribed");
    }
}

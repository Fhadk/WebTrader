package com.charlieparkerstraat.web.sample;

import com.betfair.account.api.AccountFundsResponse;
import static com.charlieparkerstraat.ApplicationConstants.BETFAIR_JSON_DATE_FORMAT;
import static com.charlieparkerstraat.ApplicationConstants.MSG_BETFAIR_CLIENT_IS_NULL;
import static com.charlieparkerstraat.ApplicationConstants.MSG_DISPATCHER_IS_NULL;
import static com.charlieparkerstraat.ApplicationConstants.MSG_USER_SESSION_IS_NULL;
import com.charlieparkerstraat.Subscriber;
import com.charlieparkerstraat.Dispatcher;
import com.charlieparkerstraat.ServerEndPointConfigurator;
import static com.charlieparkerstraat.ServerEndPointUtil.getBetfairConnection;
import static com.charlieparkerstraat.ServerEndPointUtil.getHttpSession;
import static com.charlieparkerstraat.ServerEndPointUtil.getServletContext;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import static com.charlieparkerstraat.ServerEndPointUtil.getAndRemoveHandshakeRequest;

@ServerEndpoint(value = "/getaccountfunds", configurator = ServerEndPointConfigurator.class)
public class GetAccountFundsEndPoint implements Subscriber<AccountFundsResponse> {

    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(GetAccountFundsEndPoint.class.getName());
    private final JsonConverter JSON_CONVERTER = new JsonConverter(BETFAIR_JSON_DATE_FORMAT);
    private WeakReference<ServletContext> servletContextWeakReference;
    private WeakReference<HandshakeRequest> threadLocalRequestWeakReference;
    private WeakReference<Session> userSessionWeakReference;

    public GetAccountFundsEndPoint() {
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
        try {
            final AccountFundsResponse accountFunds = (AccountFundsResponse) message;
            LOG.log(Level.INFO, ToStringBuilder.reflectionToString(accountFunds));
            Map<String, Object> resultMap = encode(accountFunds);
            final String json = JSON_CONVERTER.convertToJson(resultMap);
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

    private Map<String, Object> encode(AccountFundsResponse accountFunds) {
        final Map<String, Object> resultMap = new HashMap<>(0);
        final List<Map<String, Object>> data = new ArrayList<>(0);
        final String wallet = accountFunds.getWallet();
        encode(wallet + " AvailableToBetBalance", String.valueOf(accountFunds.getAvailableToBetBalance()), data);
        encode(wallet + " DiscountRate", String.valueOf(accountFunds.getDiscountRate()), data);
        encode(wallet + " Exposure", String.valueOf(accountFunds.getExposure()), data);
        encode(wallet + " ExposureLimit", String.valueOf(accountFunds.getExposureLimit()), data);
        encode(wallet + " PointsBalance", String.valueOf(accountFunds.getPointsBalance()), data);
        encode(wallet + " RetainedCommission", String.valueOf(accountFunds.getRetainedCommission()), data);
        encode(wallet + " Wallet", accountFunds.getWallet(), data);
        resultMap.put("event", "read");
        resultMap.put("data", data);
        return resultMap;
    }

    private void encode(String item, String value, final List<Map<String, Object>> data) {
        final Map<String, Object> itemValue = new HashMap<>(0);
        itemValue.put("item", item);
        itemValue.put("value", value);
        data.add(itemValue);
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
            dispatcher.unsubscribe(this, AccountFundsResponse.class);
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

    @OnOpen
    public void onOpen(final Session userSession, final EndpointConfig config) {
        LOG.log(Level.INFO, "onOpen - userSession.getId: {0}", this.getClass());
        this.userSessionWeakReference = new WeakReference<>(userSession);
        this.threadLocalRequestWeakReference = new WeakReference<>(getAndRemoveHandshakeRequest());
        final HttpSession httpSession = getHttpSession(this.threadLocalRequestWeakReference.get());
        this.servletContextWeakReference = new WeakReference<>(getServletContext(httpSession));
        final Dispatcher dispatcher = Dispatcher.getInstance();
        if (dispatcher != null) {
            dispatcher.subscribe(AccountFundsResponse.class, this);
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

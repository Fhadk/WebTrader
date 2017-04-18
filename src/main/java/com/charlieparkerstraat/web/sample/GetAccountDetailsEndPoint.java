package com.charlieparkerstraat.web.sample;

import com.betfair.account.api.AccountDetailsResponse;
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

@ServerEndpoint(value = "/getaccountdetails", configurator = ServerEndPointConfigurator.class)
public class GetAccountDetailsEndPoint implements Subscriber<AccountDetailsResponse> {

    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(GetAccountDetailsEndPoint.class.getName());
    private final JsonConverter JSON_CONVERTER = new JsonConverter(BETFAIR_JSON_DATE_FORMAT);
    private WeakReference<ServletContext> servletContextWeakReference;
    private WeakReference<HandshakeRequest> threadLocalRequestWeakReference;
    private WeakReference<Session> userSessionWeakReference;

    public GetAccountDetailsEndPoint() {
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
            AccountDetailsResponse accountDetails = (AccountDetailsResponse) message;
            LOG.log(Level.INFO, ToStringBuilder.reflectionToString(accountDetails));
            Map<String, Object> resultMap = encode(accountDetails);
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

    private Map<String, Object> encode(AccountDetailsResponse accountDetails) {
        final Map<String, Object> resultMap = new HashMap<>(0);
        final List<Map<String, Object>> data = new ArrayList<>(0);
        encode("CountryCode", accountDetails.getCountryCode(), data);
        encode("CurrencyCode", accountDetails.getCurrencyCode(), data);
        encode("DiscountRate", String.valueOf(accountDetails.getDiscountRate()), data);
        encode("FirstName", accountDetails.getFirstName(), data);
        encode("LastName", accountDetails.getLastName(), data);
        encode("LocaleCode", accountDetails.getLocaleCode(), data);
        encode("PointsBalance", String.valueOf(accountDetails.getPointsBalance()), data);
        encode("Region", accountDetails.getRegion(), data);
        encode("Timezone", accountDetails.getTimezone(), data);
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
            dispatcher.unsubscribe(this, AccountDetailsResponse.class);
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
            dispatcher.subscribe(AccountDetailsResponse.class, this);
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

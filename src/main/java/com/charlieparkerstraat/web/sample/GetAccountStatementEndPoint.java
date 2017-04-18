package com.charlieparkerstraat.web.sample;

import com.betfair.account.api.AccountStatementReport;
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

@ServerEndpoint(value = "/getaccountstatement", configurator = ServerEndPointConfigurator.class)
public class GetAccountStatementEndPoint implements Subscriber<AccountStatementReport> {

    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(GetAccountStatementEndPoint.class.getName());
    private final JsonConverter JSON_CONVERTER = new JsonConverter(BETFAIR_JSON_DATE_FORMAT);
    private WeakReference<ServletContext> servletContextWeakReference;
    private WeakReference<HandshakeRequest> threadLocalRequestWeakReference;
    private WeakReference<Session> userSessionWeakReference;

    public GetAccountStatementEndPoint() {
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
            final AccountStatementReport statementReport = (AccountStatementReport) message;
            LOG.log(Level.INFO, ToStringBuilder.reflectionToString(statementReport));
            Map<String, Object> resultMap = encode(statementReport);
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

    private Map<String, Object> encode(AccountStatementReport accountFunds) {
        final Map<String, Object> resultMap = new HashMap<>(0);
        final List<Map<String, Object>> data = new ArrayList<>(0);
        accountFunds.getAccountStatement().stream().map((statementItem) -> {
            final Map<String, Object> row = new HashMap<>(0);
            row.put("refId", statementItem.getRefId());
            row.put("amount", statementItem.getAmount());
            row.put("balance", statementItem.getBalance());
            row.put("itemClass", statementItem.getItemClass());
            row.put("itemDate", statementItem.getItemDate());
            row.put("avgPrice", statementItem.getLegacyData().getAvgPrice());
            row.put("betCategoryType", statementItem.getLegacyData().getBetCategoryType());
            row.put("betSize", statementItem.getLegacyData().getBetSize());
            row.put("betType", statementItem.getLegacyData().getBetType());
            row.put("commissionRate", statementItem.getLegacyData().getCommissionRate());
            row.put("eventId", statementItem.getLegacyData().getEventId());
            row.put("eventTypeId", statementItem.getLegacyData().getEventTypeId());
            row.put("fullMarketName", statementItem.getLegacyData().getFullMarketName());
            row.put("grossBetAmount", statementItem.getLegacyData().getGrossBetAmount());
            row.put("marketName", statementItem.getLegacyData().getMarketName());
            row.put("marketType", statementItem.getLegacyData().getMarketType());
            row.put("placedDate", statementItem.getLegacyData().getPlacedDate());
            row.put("selectionId", statementItem.getLegacyData().getSelectionId());
            row.put("selectionName", statementItem.getLegacyData().getSelectionName());
            row.put("startDate", statementItem.getLegacyData().getStartDate());
            row.put("transactionId", statementItem.getLegacyData().getTransactionId());
            row.put("transactionType", statementItem.getLegacyData().getTransactionType());
            row.put("winLose", statementItem.getLegacyData().getWinLose());
            return row;
        }).forEachOrdered((row) -> {
            data.add(row);
        });
        resultMap.put("event", "read");
        resultMap.put("data", data);
        return resultMap;
    }

    /**
     * Remove any variables and references created to allow garbage collection.
     *
     * @param userSession
     */
    @OnClose
    public void onClose(Session userSession) {
        LOG.log(Level.INFO, "onClose - userSession.getId: {0}", this.getClass());
        if (servletContextWeakReference != null) {
            final Dispatcher dispatcher = Dispatcher.getInstance();
            if (dispatcher != null) {
                dispatcher.unsubscribe(this, AccountStatementReport.class);
            } else {
                LOG.log(Level.WARNING, MSG_DISPATCHER_IS_NULL);
            }
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
            dispatcher.subscribe(AccountStatementReport.class, this);
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

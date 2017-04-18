package com.charlieparkerstraat.web.sample;

import com.betfair.account.api.AccountDetailsResponse;
import com.charlieparkerstraat.ServerEndPointConfigurator;
import static com.charlieparkerstraat.ServerEndPointUtil.getBetfairConnection;
import static com.charlieparkerstraat.ServerEndPointUtil.getHttpSession;
import static com.charlieparkerstraat.ServerEndPointUtil.getServletContext;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
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
import org.charlieparkerstraat.betfair.api.model.jsonrpc.Error;
import org.charlieparkerstraat.betfair.client.BetfairClient;
import org.charlieparkerstraat.betfair.client.BetfairClientParameters;
import org.charlieparkerstraat.betfair.converters.JsonConverter;
import static com.charlieparkerstraat.ServerEndPointUtil.getAndRemoveHandshakeRequest;

@ServerEndpoint(value = "/websocket", configurator = ServerEndPointConfigurator.class)
public class SampleEndPoint {

    private static final String BETFAIR_JSON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(SampleEndPoint.class.getName());
    private static final Map<String, Timer> TIMER_MAP = new HashMap<String, Timer>(0);
    private static final Type TYPE_MAP_STRING_OBJECT = new TypeToken<Map<String, Object>>() {
    }.getType();

    public static Map<String, Object> createNewData() {
        Map<String, Object> data = new HashMap<>(0);
        Date date = new Date();

        data.put("date", DATE_FORMAT.format(date));
        data.put("value", (int) (Math.random() * 1024));

        return data;
    }

    private EndpointConfig config;
    private final JsonConverter jsonConverter;
    private HandshakeRequest threadLocalRequest;

    public SampleEndPoint() {
        this.jsonConverter = new JsonConverter(BETFAIR_JSON_DATE_FORMAT);
    }

    @OnClose
    public void onClose(Session userSession) {
        LOG.log(Level.INFO, "onClose - userSession.getId: {0}", this.getClass());
        String id = userSession.getId();
        if (TIMER_MAP.containsKey(id)) {
            Timer timer = TIMER_MAP.get(id);
            timer.cancel();
            TIMER_MAP.remove(id);
        }
    }

    @OnMessage
    public void onMessage(String message, Session userSession) {
        LOG.log(Level.INFO, "onMessage - userSession.getId: {0}, message: {1}", new Object[]{this.getClass(), message});
        try {
            Map<String, Object> parameter = jsonConverter.convertFromJson(message, TYPE_MAP_STRING_OBJECT);

            if ("read".equals(parameter.get("event"))) {
                onOpen(userSession, this.config);
            }
        }
        catch (Error ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    @OnOpen
    public void onOpen(final Session userSession, final EndpointConfig config) {
        LOG.log(Level.INFO, "onOpen - userSession.getId: {0}", this.getClass());
        this.config = config;
        this.threadLocalRequest = getAndRemoveHandshakeRequest();
        final HttpSession httpSession = getHttpSession(this.threadLocalRequest);
        final ServletContext servletContext = getServletContext(httpSession);
        final BetfairClient<BetfairClientParameters> connection = getBetfairConnection(servletContext);
        if (connection != null) {
            try {
                AccountDetailsResponse accountDetails = connection.getAccountDetails();
                LOG.log(Level.INFO, ToStringBuilder.reflectionToString(accountDetails));
            }
            catch (Throwable ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        Timer timer = new Timer();
        Task task = new Task(userSession);
        TIMER_MAP.put(userSession.getId(), timer);
        timer.scheduleAtFixedRate(task, 200, 2000);
    }

    private class Task extends TimerTask {

        private final Session session;

        Task(Session session) {
            this.session = session;
        }

        @Override
        public void run() {
            Map<String, Object> resultMap = new HashMap<>(0);
            List<Map<String, Object>> data = new ArrayList<>(0);

            data.add(createNewData());

            resultMap.put("event", "read");
            resultMap.put("data", data);

            try {
                session.getAsyncRemote().sendText(jsonConverter.convertToJson(resultMap));
            }
            catch (Exception ex) {
                ex.printStackTrace();
                this.cancel();
            }
        }
    }
}

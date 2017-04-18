package com.charlieparkerstraat;

import static com.charlieparkerstraat.ApplicationConstants.THREAD_LOCAL_HANDSHAKE_REQUEST;
import java.util.logging.Logger;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

public class ServerEndPointConfigurator extends ServerEndpointConfig.Configurator {

    private static final Logger LOG = Logger.getLogger(ServerEndPointConfigurator.class.getName());

    @Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
        // We don't use config.getUserProperties.add because it isn't always one-to-one with a web socket connection; we use ThreadLocal instead
        // config.getUserProperties().put(KEY_HANDSHAKE_REQUEST, request);
        THREAD_LOCAL_HANDSHAKE_REQUEST.set(request);
        super.modifyHandshake(config, request, response);
    }
}

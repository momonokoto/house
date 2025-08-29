package com.zpark.manager;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionManager {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void registerSession(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    public void unregisterSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public void closeSession(String sessionId) throws IOException {
        WebSocketSession session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            session.close(CloseStatus.NORMAL);
        }
    }
}

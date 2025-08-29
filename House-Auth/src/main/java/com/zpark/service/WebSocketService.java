package com.zpark.service;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

public interface WebSocketService {

    void handleDuplicateSubscriptions(String username, String destination, StompHeaderAccessor accessor);

}

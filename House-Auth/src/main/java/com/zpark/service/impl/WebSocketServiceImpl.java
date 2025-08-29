package com.zpark.service.impl;

import com.zpark.manager.WebSocketSessionManager;
import com.zpark.service.WebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.io.IOException;
import java.security.Principal;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class WebSocketServiceImpl implements WebSocketService {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServiceImpl.class);
    @Autowired
    private SimpUserRegistry simpUserRegistry;
    @Autowired
    private WebSocketSessionManager sessionManager;
    private final ConcurrentMap<String, Set<String>> userSubscriptions = new ConcurrentHashMap<>();

    public void handleDuplicateSubscriptions(String username, String destination, StompHeaderAccessor accessor) {
        // 获取当前订阅ID
        String subscriptionId = accessor.getSubscriptionId();

        // 记录用户订阅关系
        userSubscriptions.computeIfAbsent(username, k -> ConcurrentHashMap.newKeySet())
                .add(destination + "|" + subscriptionId);

        // 检查是否有重复订阅
        SimpUser user = simpUserRegistry.getUser(username);
        if (user != null) {
            user.getSessions().forEach(simpSession -> {
                simpSession.getSubscriptions().stream()
                        .filter(sub -> sub.getDestination().equals(destination))
                        .forEach(sub -> {
                            // 排除当前正在创建的订阅
                            if (!sub.getId().equals(subscriptionId)) {
                                logger.warn("发现重复订阅,断开旧连接: 用户={}, 路径={}, 旧订阅ID={}",
                                        username, destination, sub.getId());

                                try {
                                    // 关闭旧会话
                                    sessionManager.closeSession(simpSession.getId());
                                } catch (IOException e) {
                                    logger.error("关闭会话失败: sessionId={}", simpSession.getId(), e);
                                }
                            }
                        });
            });
        }
    }

    // 添加断开连接时的清理逻辑
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = accessor.getUser();
        if (user != null) {
            String username = user.getName().toLowerCase();
            String sessionId = accessor.getSessionId();

            // 清理该会话的所有订阅记录
            userSubscriptions.computeIfPresent(username, (k, v) -> {
                v.removeIf(sub -> sub.endsWith("|" + sessionId));
                return v.isEmpty() ? null : v;
            });
        }
    }

}

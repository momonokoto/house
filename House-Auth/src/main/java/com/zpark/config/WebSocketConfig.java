package com.zpark.config;

import com.zpark.manager.WebSocketSessionManager;
import com.zpark.mapper.UserMapper_Auth;
import com.zpark.service.WebSocketService;
import com.zpark.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

import java.security.Principal;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Autowired
    private JwtUtil  jwtUtil;
    @Autowired
    private UserMapper_Auth userMapper_auth;
    @Autowired
    private  WebSocketSessionManager sessionManager;

    @Autowired
    @Lazy
    private WebSocketService webSocketService;





    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.addDecoratorFactory(new WebSocketHandlerDecoratorFactory() {
            @Override
            public WebSocketHandler decorate(WebSocketHandler handler) {
                return new WebSocketHandlerDecorator(handler) {
                    @Override
                    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                        sessionManager.registerSession(session);
                        super.afterConnectionEstablished(session);
                    }

                    @Override
                    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                        sessionManager.unregisterSession(session.getId());
                        super.afterConnectionClosed(session, closeStatus);
                    }
                };
            }
        });
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

                // 添加订阅路径验证日志
                if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    String destination = accessor.getDestination();
                    Principal user = accessor.getUser();
                    String username = user != null ? user.getName() : "null";
                    logger.info("用户订阅检测: 路径={}, 用户={}", destination, username);
                    System.out.println( "destiination"+destination);
                    // 检查是否尝试订阅管理员频道
                    if ("/topic/admin".equals(destination)) {
                        if(userMapper_auth.selectByUsername( username).getRole().equals("ADMIN")){
                            logger.info("用户 {} 是管理员", username);
                        }else {
                            logger.warn("非管理员用户尝试订阅管理员频道: {}", username);
                            // 拒绝订阅
                            return null;
                        }
                    }
                    // 统一用户名格式为小写
                    String normalizedUsername = username.toLowerCase();
                    
                    // 修改：放宽路径验证规则
                    if (destination != null && destination.startsWith("/user/")) {
                        // 允许两种格式：/user/queue/... 或 /user/{username}/queue/...
                        if (!destination.startsWith("/user/queue/") && 
                            !destination.startsWith("/user/"+normalizedUsername+"/")) {
                            
                            // 修改：使用警告级别日志并记录详细信息
                            logger.warn("订阅路径格式警告: 实际路径={}, 期望格式=/user/queue/... 或 /user/{}/queue/...", 
                                       destination, normalizedUsername);
                        }
                    }
                    if (user != null) {
                        webSocketService.handleDuplicateSubscriptions(normalizedUsername, destination, accessor);
                    }
                }

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // 增强日志输出
                    logger.debug("处理CONNECT命令，当前用户: {}", accessor.getUser());
                    
                    // 双重验证：优先使用handshakeHandler绑定的用户
                    Principal user = accessor.getUser();
                    if (user == null) {
                        // 如果握手阶段未绑定，尝试从STOMP头补救
                        String token = accessor.getFirstNativeHeader("Authorization");
                        if (token != null) {
                            String newtoken = token.substring(7);
                            String username = jwtUtil.getUsernameFromToken(newtoken);
                            
                            // 统一用户名格式为小写
                            String normalizedUsername = username.toLowerCase();

                            // 设置认证并记录
                            accessor.setUser(() -> normalizedUsername); // 使用小写格式
                            logger.info("STOMP连接阶段设置用户: {}", normalizedUsername);
                            // 更新消息头
                            message = MessageBuilder.fromMessage(message)
                                    .setHeader(SimpMessageHeaderAccessor.USER_HEADER, accessor.getUser())
                                    .build();
                        } else {
                            // 修改：使用更具体的异常类型
                            throw new IllegalArgumentException("未提供身份凭证");
                        }
                    } else {
                        logger.debug("使用握手阶段设置的用户: {}", user.getName());
                    }
                }
                return message;
            }
        });
    }



    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost("")  // RabbitMQ服务器地址
                .setRelayPort(61613)           // STOMP端口(默认61613)
                .setClientLogin("")        // 客户端用户名
                .setClientPasscode("")     // 客户端密码
                .setVirtualHost("/");           // RabbitMQ虚拟主机

        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new JwtHandshakeInterceptor(jwtUtil));
//                .withSockJS();
    }
}

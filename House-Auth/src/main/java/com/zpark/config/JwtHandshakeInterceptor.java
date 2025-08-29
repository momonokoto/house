package com.zpark.config;

import com.zpark.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.security.Principal;
import java.util.Map;

public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(JwtHandshakeInterceptor.class);

    public JwtHandshakeInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        // 1. 从请求参数获取token (支持两种方式)
        String token = null;

        // 从 query 参数中获取 token
        String query = request.getURI().getQuery();
        if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("token=")) {
                    token = param.split("=")[1];
                    break;
                }
            }
        }

        if (request.getHeaders().containsKey("Authorization")) {
            token = request.getHeaders().getFirst("Authorization").substring(7);
        }

        if (token == null){
            String protocol = request.getHeaders().getUpgrade(); // 获取子协议
            if (protocol != null && protocol.contains("Bearer=")) {
                token = protocol.substring(protocol.indexOf("Bearer=") + 7);

            }
        }


        System.out.println("这里是JwtHandshakeInterceptor:"+ token);

        // 2. 验证JWT
        if (token != null) {
            try {
                Authentication authentication = jwtUtil.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 创建完整实现的Principal对象
                final String username = authentication.getName();
                // 统一用户名格式为小写
                final String normalizedUsername = username.toLowerCase();
                
                attributes.put("user", new Principal() {
                    @Override
                    public String getName() {
                        return normalizedUsername;
                    }
                    
                    // 添加equals和hashCode实现
                    @Override
                    public boolean equals(Object obj) {
                        if (this == obj) return true;
                        if (!(obj instanceof Principal)) return false;
                        return username.equals(((Principal) obj).getName());
                    }
                    
                    @Override
                    public int hashCode() {
                        return username.hashCode();
                    }
                });
                
                // 替换System.out为logger
                logger.info("用户Principal已设置: {}", normalizedUsername);
                return true;
            } catch (Exception e) {
                logger.error("JWT验证失败", e);
                response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                return false;
            }
        }

        response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 握手成功后清理资源（可选）
    }
}
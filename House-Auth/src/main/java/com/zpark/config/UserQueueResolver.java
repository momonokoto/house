package com.zpark.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserQueueResolver {

    private static final Logger logger = LoggerFactory.getLogger(UserQueueResolver.class);
    private static final String DEFAULT_USER_QUEUE = "default.user.queue";
    private static final String DEFAULT_GROUP_QUEUE = "default.group.queue";

    // 解析当前用户的队列
//    public Queue resolveUserQueue() {
//        try {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            if (authentication == null || !authentication.isAuthenticated()) {
//                logger.warn("未找到认证用户，使用默认用户队列");
//                return new Queue(DEFAULT_USER_QUEUE, true);
//            }
//            String username = authentication.getName();
//            return new Queue(RabbitMQConfig.USER_QUEUE_PREFIX + username, true);
//        } catch (Exception e) {
//            logger.error("解析用户队列时发生异常", e);
//            return new Queue(DEFAULT_USER_QUEUE, true); // 返回默认队列
//        }
//    }

    // 解析群组队列（实际使用中需要根据用户加入的群组动态解析）
    public Queue resolveGroupQueue() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                logger.warn("未找到认证用户，使用默认群组队列");
                return new Queue(DEFAULT_GROUP_QUEUE, true);
            }
            // 实际应根据用户加入的群组动态生成队列名
            return new Queue(RabbitMQConfig.GROUP_QUEUE_PREFIX + "default", true);
        } catch (Exception e) {
            logger.error("解析群组队列时发生异常", e);
            return new Queue(DEFAULT_GROUP_QUEUE, true); // 返回默认队列
        }
    }
}

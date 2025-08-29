package com.zpark.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zpark.config.RabbitMQConfig;
import com.zpark.entity.Message;
import com.zpark.mapper.UserMapper_Auth;
import com.zpark.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Properties;

@Service
public class UserQueueService {
    private static final Logger logger = LoggerFactory.getLogger(UserQueueService.class);

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private DirectExchange userDirectExchange;

    @Autowired  // 新增消息模板注入
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired  // 新增监听器注册中心注入
    private RabbitListenerEndpointRegistry endpointRegistry;
    
    @Autowired  // 新增容器工厂注入
    private RabbitListenerContainerFactory<?> containerFactory;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserMapper_Auth userMapper_auth;
    
    @Autowired
    private RabbitListenerEndpointRegistry registry;

    @Autowired
    private StringRedisTemplate  stringRedisTemplate;

    public void registerUserQueueListener(String username) {
        // 统一用户名格式为小写
        String normalizedUsername = username.toLowerCase();


        if (endpointRegistry.getListenerContainer("listener-" + normalizedUsername) != null){
            logger.debug("已存在用户队列监听器: {}", "listener-" +normalizedUsername);
            return;
        }

        // 1. 创建端点配置
        SimpleRabbitListenerEndpoint endpoint = new SimpleRabbitListenerEndpoint();
        // 设置唯一ID（格式：listener-{username}）
        endpoint.setId("listener-" + normalizedUsername);
        // 绑定用户专属队列（RabbitMQ队列名格式：user.queue.{username}）
        endpoint.setQueueNames("user.queue." + normalizedUsername);

        logger.info("注册用户队列监听器: {}","listener-" + normalizedUsername);

        // 2. 设置消息监听逻辑
        endpoint.setMessageListener(message -> {
            try {
                Message msg = new ObjectMapper().readValue(message.getBody(), Message.class);
                logger.info("动态监听器收到来自用户{}消息: {}", msg.getSender(), msg.getContent());
                long timestamp = Instant.now().getEpochSecond();

                try {
                    // 尝试推送消息
                    messagingTemplate.convertAndSendToUser(
                            msg.getReceiver(),
                            "/queue/private",
                            msg
                    );
                    redisUtil.addAndTrimRecentContact(msg.getReceiver(),msg.getSender(), timestamp,20);
                    Map<String, Double> recentContactsMap = redisUtil.getRecentContactsMap(msg.getReceiver(), 20);
                    messagingTemplate.convertAndSendToUser(
                            msg.getReceiver(),
                            "/queue/contacts",
                            recentContactsMap
                    );

                } catch (MessageDeliveryException e) {
                    logger.warn("用户 {} 无活跃会话，消息暂存", msg.getReceiver());
                    // 此处可添加离线消息存储逻辑
                }
            } catch (Exception e) {
                logger.error("处理消息异常", e);
            }
        });

        // 3. 注册监听容器
        endpointRegistry.registerListenerContainer(
                endpoint,
                containerFactory,
                true // 是否自动启动
        );
    }

    /**
     * 注销指定用户的队列监听器
     * @param username 要注销的用户名
     */
    public void unregisterUserQueueListener(String username) {
        String normalizedUsername = username.toLowerCase(); // 统一格式
        String listenerId = "listener-" + normalizedUsername;

        try {
            // 1. 检查监听器是否存在
            if (endpointRegistry.getListenerContainer(listenerId) != null) {
                // 2. 停止并移除监听器
                endpointRegistry.unregisterListenerContainer(listenerId);
                logger.info("成功注销监听器: {}", listenerId); // 替换System.out
            } else {
                logger.info("监听器不存在: {}", listenerId); // 替换System.out
            }
        } catch (Exception e) {
            logger.error("注销监听器失败: {}", e.getMessage()); // 替换System.err
        }
    }
    public void createUserQueue(String username) {
        // 统一用户名格式为小写
        String normalizedUsername = username.toLowerCase();
        String queueName = RabbitMQConfig.USER_QUEUE_PREFIX + normalizedUsername;
        
        // 检查队列是否已存在
        Properties queueProperties = rabbitAdmin.getQueueProperties(queueName);
        if (queueProperties != null) {
            logger.info("用户队列已存在: {}", queueName);
            return;
        }

        logger.info("创建用户队列: {}", queueName);
        
        try {
            Queue queue = new Queue(queueName, true, false, false);
            rabbitAdmin.declareQueue(queue);

            // 绑定队列到交换器
            Binding binding = BindingBuilder.bind(queue)
                    .to(userDirectExchange)
                    .with(normalizedUsername);  // 使用统一格式的用户名
            rabbitAdmin.declareBinding(binding);
        } catch (AmqpException e) {
            logger.error("用户队列创建失败: {} | 异常原因: {}", queueName, e.getMessage());
            throw new RuntimeException("用户队列创建失败: " + username, e);
        }
    }

//    public void createGroupQueue(String groupId) {
//        String queueName = RabbitMQConfig.GROUP_QUEUE_PREFIX + groupId;
//        try {
//            Queue queue = new Queue(queueName, true, false, false);
//            rabbitAdmin.declareQueue(queue);
//
//            // 绑定到群组交换器
//            Binding binding = BindingBuilder.bind(queue)
//                    .to(new TopicExchange(RabbitMQConfig.GROUP_EXCHANGE))
//                    .with("group.routing." + groupId);
//            rabbitAdmin.declareBinding(binding);
//        } catch (AmqpException e) {
//            logger.error("群组队列创建失败: {} | 异常原因: {}", groupId, e.getMessage());
//            throw new RuntimeException("群组队列创建失败: " + groupId, e);
//        }
//    }
}

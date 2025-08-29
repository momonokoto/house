package com.zpark.service;

import com.zpark.config.RabbitMQConfig;
import com.zpark.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class MessageProducerService {
    private static final Logger logger = LoggerFactory.getLogger(MessageProducerService.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void sendAdmin(String content) {
        Message message = new Message();
        message.setSender("System");
        message.setReceiver("admin");
        message.setContent(content);
        message.setType(Message.MessageType.System);

        try {
            messageService.saveMessage(message);
        } catch (DataAccessException e) {
            logger.error("私信消息保存失败 [{}->{}] | 原因: {}", "System", "Admin", e.getMessage());
            throw new RuntimeException("消息保存失败", e);
        }

        try {
            rabbitTemplate.convertAndSend(
                    "user.direct.exchange",
                    "adminMessage",
                    message
            );
            logger.info("私信发送成功 [{}->{}]", "System", "Admin");
        } catch (AmqpException e) {
            logger.error("私信发送失败 [{}->{}] | 原因: {}", "System", "Admin", e.getMessage());
            throw new RuntimeException("消息发送失败", e);
        }
    }

    public void sendPrivateMessage_System(String receiver, String content) {
        Message message = new Message();
        message.setSender("System");
        message.setReceiver(receiver);
        message.setContent(content);
        message.setType(Message.MessageType.PRIVATE);

        try {
            messageService.saveMessage(message);
        } catch (DataAccessException e) {
            logger.error("私信消息保存失败 [{}->{}] | 原因: {}", "System", receiver, e.getMessage());
            throw new RuntimeException("消息保存失败", e);
        }

        try {
            rabbitTemplate.convertAndSend(
                    "user.direct.exchange",
                    receiver,
                    message
            );
        } catch (AmqpException e) {
            logger.error("私信发送失败 [{}->{}] | 原因: {}", "System", receiver, e.getMessage());
            throw new RuntimeException("消息发送失败", e);
        }
    }


    public void sendPrivateMessage_static(String receiver, String content) {
        String sender = SecurityContextHolder.getContext().getAuthentication().getName();

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setType(Message.MessageType.PRIVATE);

        try {
            messageService.saveMessage(message);
        } catch (DataAccessException e) {
            logger.error("私信消息保存失败 [{}->{}] | 原因: {}", sender, receiver, e.getMessage());
            throw new RuntimeException("消息保存失败", e);
        }

        try {
            rabbitTemplate.convertAndSend(
                    "user.direct.exchange",
                    "all",
                    message
            );
        } catch (AmqpException e) {
            logger.error("私信发送失败 [{}->{}] | 原因: {}", sender, receiver, e.getMessage());
            throw new RuntimeException("消息发送失败", e);
        }
    }
    public void sendPrivateMessage(String receiver, String content) {
        String sender = SecurityContextHolder.getContext().getAuthentication().getName();

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setType(Message.MessageType.PRIVATE);

        try {
            messageService.saveMessage(message);
        } catch (DataAccessException e) {
            logger.error("私信消息保存失败 [{}->{}] | 原因: {}", sender, receiver, e.getMessage());
            throw new RuntimeException("消息保存失败", e);
        }

        try {
            rabbitTemplate.convertAndSend(
                    "user.direct.exchange",
                    receiver,
                    message
            );
        } catch (AmqpException e) {
            logger.error("私信发送失败 [{}->{}] | 原因: {}", sender, receiver, e.getMessage());
            throw new RuntimeException("消息发送失败", e);
        }
    }

    public void sendGroupMessage(String groupId, String content) {
        String sender = SecurityContextHolder.getContext().getAuthentication().getName();

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(groupId);
        message.setContent(content);
        message.setType(Message.MessageType.GROUP);

        try {
            messageService.saveMessage(message);
        } catch (DataAccessException e) {
            logger.error("群组消息保存失败 [群组{}] | 原因: {}", groupId, e.getMessage());
            throw new RuntimeException("消息保存失败", e);
        }

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.GROUP_EXCHANGE,
                    "group.routing." + groupId,
                    message
            );
        } catch (AmqpException e) {
            logger.error("群组消息发送失败 [群组{}] | 原因: {}", groupId, e.getMessage());
            throw new RuntimeException("消息发送失败", e);
        }
    }
}

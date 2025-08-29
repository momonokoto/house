package com.zpark.consumer;

import com.zpark.entity.Message;
import com.zpark.service.MessageService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;



    public MessageConsumer(MessageService messageService,SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }

    // 监听用户点对点队列
//    @RabbitListener(queues = "#{@userQueueResolver.resolveUserQueue()}")
    @RabbitListener(queues = "user.queue.all")
    public void receivePrivateMessage(Message message) {
        // 消息已保存到数据库，这里可以进行其他处理
        // 实时推送给指定用户
        messagingTemplate.convertAndSendToUser(
                message.getReceiver(),
                "/queue/private",
                message
        );
        System.out.println("收到来自"+message.getSender()+ "的信息:" + message.getContent());
    }

    @RabbitListener(queues = "user.queue.adminMessage")
    public void receiveAdminMessage(Message message) {
        // 实时推送给管理员
        messagingTemplate.convertAndSend(
                "/topic/admin",
                message
        );
        System.out.println("收到管理员消息: " + message.getContent());
    }

//    // 监听群组队列
//    @RabbitListener(queues = "#{@userQueueResolver.resolveGroupQueue()}")
//    public void receiveGroupMessage(Message message) {
//        // 实时推送给群组所有成员
//        messagingTemplate.convertAndSend(
//                "/topic/group." + message.getReceiver(),
//                message
//        );
//        System.out.println("收到群消息[" + message.getReceiver() + "]: " + message.getContent());
//    }
}

package com.zpark.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 用户点对点队列前缀
    public static final String USER_QUEUE_PREFIX = "user.queue.";

    // 群组交换器
    public static final String GROUP_EXCHANGE = "group.exchange";

    // 群组队列前缀
    public static final String GROUP_QUEUE_PREFIX = "group.queue.";

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();  // 替换默认的SimpleMessageConverter
    }
    @Bean
    public Queue defaultUserQueue() {
        return new Queue("default.user.queue", true);
    }
//
//    @Bean
//    public Queue defaultGroupQueue() {
//        return new Queue("default.group.queue", true);
//    }

    // 用户点对点交换器
    @Bean
    public DirectExchange userDirectExchange() {
        return new DirectExchange("user.direct.exchange");
    }

    // 群组主题交换器
//    @Bean
//    public TopicExchange groupTopicExchange() {
//        return new TopicExchange(GROUP_EXCHANGE);
//    }


//    // 声明群组队列绑定
//    @Bean
//    public Binding groupBinding(TopicExchange groupTopicExchange) {
//        return BindingBuilder.bind(groupQueue())
//                .to(groupTopicExchange)
//                .with("group.routing.#");
//    }
//
//    // 声明群组队列（实际使用时动态创建）
//    @Bean
//    public Queue groupQueue() {
//        return new AnonymousQueue();
//    }

    @Bean
    public RabbitAdmin rabbitAdmin(RabbitTemplate rabbitTemplate) {
        return new RabbitAdmin(rabbitTemplate);
    }
}

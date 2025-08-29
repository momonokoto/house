package com.zpark.service;

import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class DynamicQueueListenerService {

    @Autowired
    private RabbitListenerEndpointRegistry endpointRegistry;

    @Autowired
    private RabbitListenerContainerFactory<?> containerFactory;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
}

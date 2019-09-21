package com.mgvr.kudos.user.api.config;


import com.mgvr.kudos.user.api.constants.RabbitmqExchangeName;
import com.mgvr.kudos.user.api.constants.RabbitmqQueueNames;
import com.mgvr.kudos.user.api.constants.RabbitmqRoutingKeys;
import com.mgvr.kudos.user.api.messaging.Sender;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mgvr.kudos.user.api.messaging.Receiver;

@Configuration
public class RabbitMqConfig {
	public static final String EXCHANGE_NAME = RabbitmqExchangeName.EXCHANGE_NAME;
    public static final String KUDO_RPC_USER_API_ROUTING_KEY = RabbitmqRoutingKeys.KUDO_RPC_USER_API;
    public static final String KUDO_RPC_USER_API_QUEUE_NAME = RabbitmqQueueNames.KUDO_RPC_USER_API;

    public static final String KUDO_RPC_USER_REQUEST_ROUTING_KEY = RabbitmqRoutingKeys.KUDO_RPC_USER_REQUEST;
    public static final String KUDO_RPC_USER_REQUEST_QUEUE_NAME = RabbitmqQueueNames.KUDO_RPC_USER_REQUEST;

    private static final boolean IS_DURABLE_QUEUE = false;
 
    @Bean
    Queue userApiQueue() {
        return new Queue(KUDO_RPC_USER_API_QUEUE_NAME, IS_DURABLE_QUEUE);
    }
 
    @Bean
    DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    Queue userRequestQueue() {
        return new Queue(KUDO_RPC_USER_REQUEST_QUEUE_NAME, IS_DURABLE_QUEUE);
    }
 
    @Bean
    Binding bindingApi(Queue userApiQueue, DirectExchange exchange) {
        return BindingBuilder.bind(userApiQueue).to(exchange).with(KUDO_RPC_USER_API_ROUTING_KEY);
    }

    @Bean
    Binding bindingUserRequest(Queue userRequestQueue, DirectExchange exchange) {
        return BindingBuilder.bind(userRequestQueue).to(exchange).with(KUDO_RPC_USER_REQUEST_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        rabbitTemplate.setReceiveTimeout(600000);
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
 
    @Bean
    Receiver receiver() {
        return new Receiver();
    }
    @Bean
    Sender sender() {
        return new Sender();
    }
}

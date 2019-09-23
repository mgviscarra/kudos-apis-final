package com.mgvr.stats.api.config;

import com.mgvr.stats.api.constants.RabbitmqExchangeName;
import com.mgvr.stats.api.constants.RabbitmqQueueNames;
import com.mgvr.stats.api.constants.RabbitmqRoutingKeys;
import com.mgvr.stats.api.messaging.Receiver;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {
    public static final String EXCHANGE_NAME = RabbitmqExchangeName.EXCHANGE_NAME;

    public static final String KUDO_RPC_STATS_API_UPDATE_KUDOS_ROUTING_KEY = RabbitmqRoutingKeys.KUDO_RPC_STATS_API_UPDATE_KUDOS_ROUTING_KEY;
    public static final String KUDO_RPC_STATS_API_UPDATE_KUDOS_QUEUE_NAME = RabbitmqQueueNames.KUDO_RPC_STATS_API_UPDATE_KUDOS_QUEUE_NAME;

    public static final String KUDO_RPC_STATS_API_UPDATE_KUDO_ROUTING_KEY = RabbitmqRoutingKeys.KUDO_RPC_STATS_API_UPDATE_KUDO_ROUTING_KEY;
    public static final String KUDO_RPC_STATS_API_UPDATE_KUDO_QUEUE_NAME = RabbitmqQueueNames.KUDO_RPC_STATS_API_UPDATE_KUDO_QUEUE_NAME;

    private static final boolean IS_DURABLE_QUEUE = false;

    @Bean
    Queue kudosStatsQueue() {
        return new Queue(KUDO_RPC_STATS_API_UPDATE_KUDOS_QUEUE_NAME, IS_DURABLE_QUEUE);
    }

    @Bean
    Queue kudoStatsQueue() {
        return new Queue(KUDO_RPC_STATS_API_UPDATE_KUDO_QUEUE_NAME, IS_DURABLE_QUEUE);
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    Binding kudosStatsBinding(Queue kudosStatsQueue, DirectExchange exchange) {
        return BindingBuilder.bind(kudosStatsQueue).to(exchange).with(KUDO_RPC_STATS_API_UPDATE_KUDOS_ROUTING_KEY);
    }

    @Bean
    Binding kudoStatsBinding(Queue kudoStatsQueue, DirectExchange exchange) {
        return BindingBuilder.bind(kudoStatsQueue).to(exchange).with(KUDO_RPC_STATS_API_UPDATE_KUDO_ROUTING_KEY);
    }


    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        rabbitTemplate.setReceiveTimeout(30000);
        rabbitTemplate.setReplyTimeout(30000);
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
}

package com.mgvr.stats.api.messaging;

import com.mgvr.stats.api.constants.RabbitmqQueueNames;
import com.mgvr.stats.api.model.Kudo;
import com.mgvr.stats.api.service.StatsService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class Receiver {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private StatsService statsService;

    @RabbitListener(queues = RabbitmqQueueNames.KUDO_RPC_STATS_API_UPDATE_KUDO_QUEUE_NAME)
    public void receiveStatsRequestByUsersApi(Kudo kudo){
       statsService.updateStatesFromSimpleKudo(kudo);
    }

    @RabbitListener(queues = RabbitmqQueueNames.KUDO_RPC_STATS_API_UPDATE_KUDOS_QUEUE_NAME)
    public void receiveStatsRequestByKudosApi(List<Kudo> message){
        statsService.updateStatsFromKudosList(message);
    }
}

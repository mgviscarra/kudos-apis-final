package com.mgvr.kudos.api.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgvr.kudos.api.com.mgvr.kudos.api.constants.ApiMessages;
import com.mgvr.kudos.api.com.mgvr.kudos.api.constants.RabbitmqQueueNames;
import com.mgvr.kudos.api.model.Kudo;
import com.mgvr.kudos.api.model.User;
import com.mgvr.kudos.api.service.KudoService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;

import java.util.List;

public class Receiver {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private KudoService kudoService;

    @RabbitListener(queues = RabbitmqQueueNames.KUDO_RPC_KUDO_DELETE_REQUEST)
    @SendTo(RabbitmqQueueNames.KUDO_RPC_USER_API)
    public String receiveKudoDeleteRequest(User message){
        System.out.println("Recibiendo mensaje: "+message.getNickName());
        kudoService.deleteKudoByFrom(message.getNickName());
        kudoService.deleteKudoByTo(message.getNickName());
        return ApiMessages.DELETED;
    }

    @RabbitListener(queues = RabbitmqQueueNames.KUDO_RPC_GET_KUDO_FOR_USER_REQUEST)
    @SendTo(RabbitmqQueueNames.KUDO_RPC_USER_API)
    public String receiveKudoForUserRequest(User message) throws JsonProcessingException {
        System.out.println("Recibiendo mensaje: "+message.getNickName());
        //List<Kudo> kudos = dao.getKudosByNickName(message.getRealName());
        List<Kudo> kudos = kudoService.getKudosByNickName(message.getNickName());
        if(kudos.size()==0){
            return null;
        }
        ObjectMapper Obj = new ObjectMapper();
        String jsonStr = Obj.writeValueAsString(kudos);
        return jsonStr;
    }
}

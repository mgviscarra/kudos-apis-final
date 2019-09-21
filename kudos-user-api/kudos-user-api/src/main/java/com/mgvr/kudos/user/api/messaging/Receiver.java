package com.mgvr.kudos.user.api.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgvr.kudos.user.api.constants.DbFields;
import com.mgvr.kudos.user.api.constants.RabbitmqQueueNames;
import com.mgvr.kudos.user.api.model.User;
import com.mgvr.kudos.user.api.service.UserService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;

import java.io.IOException;


public class Receiver {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserService userService;

	@RabbitListener(queues =RabbitmqQueueNames.KUDO_RPC_USER_REQUEST)
    @SendTo(RabbitmqQueueNames.KUDO_RPC_KUDO_API)
	public String receiveUserQueryRequest(User message) throws IOException {
		System.out.println("[Receiver] ha recibido el mensaje \"" + message.getRealName() + '"');
        //User user = dao.getUserByRealName(message.getRealName());
        User user = userService.getUserByField(DbFields.REAL_NAME, message.getRealName());
        if(user!=null){
            ObjectMapper Obj = new ObjectMapper();
            String jsonStr = Obj.writeValueAsString(user);
            return jsonStr;
        }
        return null;
    }
}

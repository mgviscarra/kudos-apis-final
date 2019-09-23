package com.mgvr.kudos.api.service;

import com.mgvr.kudos.api.com.mgvr.kudos.api.constants.ApiMessages;
import com.mgvr.kudos.api.com.mgvr.kudos.api.constants.DbFields;
import com.mgvr.kudos.api.com.mgvr.kudos.api.constants.RabbitmqExchangeName;
import com.mgvr.kudos.api.com.mgvr.kudos.api.constants.RabbitmqRoutingKeys;
import com.mgvr.kudos.api.dao.KudoDao;
import com.mgvr.kudos.api.messaging.Sender;
import com.mgvr.kudos.api.model.Kudo;
import com.mgvr.kudos.api.model.KudoSearch;
import com.mgvr.kudos.api.model.User;
import com.monitorjbl.json.JsonResult;
import com.monitorjbl.json.JsonView;
import com.monitorjbl.json.Match;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class KudoService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    Sender sender;

    @Autowired
    private KudoDao dao;

    public boolean createKudo(Kudo kudo){
        User userFrom = new User();
        userFrom.setNickName(kudo.getFuente());
        User userTo  = new User();
        userTo.setNickName(kudo.getDestino());
        String responseUserFrom =  (String)rabbitTemplate.convertSendAndReceive
                (RabbitmqExchangeName.EXCHANGE_NAME, RabbitmqRoutingKeys.KUDO_RPC_USER_REQUEST, userFrom);
        String responseUserTo =  (String)rabbitTemplate.convertSendAndReceive
                (RabbitmqExchangeName.EXCHANGE_NAME, RabbitmqRoutingKeys.KUDO_RPC_USER_REQUEST, userTo);
        if(responseUserFrom.equalsIgnoreCase(ApiMessages.USERS_DONT_EXIST) || responseUserTo.equalsIgnoreCase(ApiMessages.USERS_DONT_EXIST)){
            return false;
        }
        dao.createKudo(kudo);
        sender.sendMessage(RabbitmqExchangeName.EXCHANGE_NAME, RabbitmqRoutingKeys.KUDO_RPC_STATS_API_UPDATE_KUDO_ROUTING_KEY,kudo);
        return true;
    }

    public void deleteKudo(String id){
        Kudo kudo = getKudoById(id);
        dao.deleteKudo(Long.parseLong(id));
        sender.sendMessage(RabbitmqExchangeName.EXCHANGE_NAME, RabbitmqRoutingKeys.KUDO_RPC_STATS_API_UPDATE_KUDO_ROUTING_KEY,kudo);

    }

    public List<Kudo> getKudos(KudoSearch kudo, Map<String,String> pagination){
        List<Kudo> kudos = new ArrayList<>();
        if(kudo==null){
            kudos = dao.getAllkudos(pagination);
        }
        else{
            kudos = dao.getKudosByFuzzyContent(kudo.getContent(),pagination);
        }
        JsonResult json = JsonResult.instance();
        List<Kudo> listKudos= json.use(JsonView.with(kudos)
                .onClass(User.class, Match.match()
                        .exclude(DbFields.FECHA)
                )).returnValue();
        return listKudos;
    }

    public void deleteKudoByFrom(String from){
        List<Kudo> kudosFrom = dao.getKudosByFrom(from);
        dao.deleteKudoByFrom(from);
        sender.sendMessage(RabbitmqExchangeName.EXCHANGE_NAME, RabbitmqRoutingKeys.KUDO_RPC_STATS_API_UPDATE_KUDOS_ROUTING_KEY,kudosFrom);
    }

    public Kudo getKudoById(String id){
        return dao.getKudoById(Long.parseLong(id));
    }

    public void deleteKudoByTo(String to){
        dao.deleteKudoByTo(to);
    }

    public List<Kudo> getKudosByNickName(String realName){
        List<Kudo> kudos = dao.getKudosByNickName(realName);
        JsonResult json = JsonResult.instance();
        List<Kudo> listKudos= json.use(JsonView.with(kudos)
                .onClass(User.class, Match.match()
                        .exclude(DbFields.FECHA)
                )).returnValue();
        return kudos;
    }
}

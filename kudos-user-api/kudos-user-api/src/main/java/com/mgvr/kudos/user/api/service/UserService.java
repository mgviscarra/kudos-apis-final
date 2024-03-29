package com.mgvr.kudos.user.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgvr.kudos.user.api.constants.ApiMessages;
import com.mgvr.kudos.user.api.constants.DbFields;
import com.mgvr.kudos.user.api.constants.RabbitmqExchangeName;
import com.mgvr.kudos.user.api.constants.RabbitmqRoutingKeys;
import com.mgvr.kudos.user.api.dao.UserDao;
import com.mgvr.kudos.user.api.messaging.Sender;
import com.mgvr.kudos.user.api.model.Kudo;
import com.mgvr.kudos.user.api.model.User;
import com.mgvr.kudos.user.api.model.UserSearch;
import com.monitorjbl.json.JsonResult;
import com.monitorjbl.json.JsonView;
import com.monitorjbl.json.Match;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.soap.SOAPBinding;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    private UserDao dao;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Sender sender;

    public void addUser(User user){
        dao.saveUser(user);
    }

    public List<User> getUsers(UserSearch user, Map<String, String> pagination){
        List<User> users = new ArrayList<>();
        if(user==null){
            users = dao.getAllUsers(pagination);
        }
        else{
            users = dao.getUsersByFuzzyName(user.getUserNames(),pagination);
        }
        JsonResult json = JsonResult.instance();
        List<User> listUsers= json.use(JsonView.with(users)
                .onClass(User.class, Match.match()
                        .exclude(DbFields.EMAIL)
                        .exclude(DbFields.KUDOS)
                )).returnValue();
        return listUsers;
    }

    public User getUserByField(String field, String value) throws IOException {
        User user=null;
        switch (field) {
            case DbFields.ID:
                user = dao.getUserById(Integer.parseInt(value));
                break;
            case DbFields.REAL_NAME:
                user = dao.getUserByRealName(value);
                break;
            case DbFields.NICK_NAME:
                user = dao.getUserByNickName(value);
                break;
        }
        if(user==null){
            return user;
        }
        String responseKudosUser=  (String)rabbitTemplate.convertSendAndReceive
                (RabbitmqExchangeName.EXCHANGE_NAME, RabbitmqRoutingKeys.KUDO_RPC_GET_KUDO_FOR_USER_REQUEST, user);
        ObjectMapper mapper = new ObjectMapper();
        if(responseKudosUser!=null){
            List<Kudo> kudoList = mapper.readValue(responseKudosUser, mapper.getTypeFactory().constructCollectionType(List.class, Kudo.class));
            user.setKudos(kudoList);
        }
        JsonResult json = JsonResult.instance();
        return json.use(JsonView.with(user)
                .onClass(User.class, Match.match()
                        .exclude(DbFields.NRO_KUDOS)
                )).returnValue();
    }

    public String  updateUser(String id, User user){
        user.setId(Integer.parseInt(id));
        return dao.updateUser(user);
    }

    public String updateUserStats(User user){
        return dao.updateUserStats(user);
    }

    public boolean deleteUser(String id){
        User user = dao.getUserById(Integer.parseInt(id));
        String responseDeleteKudos = (String)rabbitTemplate.convertSendAndReceive
                (RabbitmqExchangeName.EXCHANGE_NAME, RabbitmqRoutingKeys.KUDO_RPC_KUDO_DELETE_REQUEST, user);
        if(responseDeleteKudos.equalsIgnoreCase(ApiMessages.KUDOS_DELETED)){
            dao.deleteUser(user);
            return true;
        }
        return false;
    }
}

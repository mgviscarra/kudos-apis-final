package com.mgvr.stats.api.service;

import com.mgvr.stats.api.config.InfluxDb;
import com.mgvr.stats.api.constants.ApiConstants;
import com.mgvr.stats.api.constants.Influx;
import com.mgvr.stats.api.constants.UsersDbFields;
import com.mgvr.stats.api.model.Kudo;
import org.influxdb.dto.Point;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
public class StatsService {

    @Value("${kudos.api.service.url}")
    private String kudosServiceUrl;

    @Value("${users.api.service.url}")
    private String usersServiceUrl;

    @Autowired
    InfluxDb influxDb;

    private static final Logger logger = LoggerFactory.getLogger(StatsService.class);

    public void updateStatesFromSimpleKudo(Kudo kudo){
        int nroKudos = getKudosCountByNickName(kudo.getDestino());
        updateUserStat(nroKudos, kudo.getDestino());
        logger.info("Updated stats for user: "+kudo.getDestino() );
        influxDb.getInstance().write(Point.measurement(Influx.STATS_REQUESTS_POINT_MEASUREMENT).time(System.currentTimeMillis(),
                TimeUnit.MILLISECONDS).addField(Influx.STATS_UPDATE_REQUEST,"UPDATE STATS: "+kudo.getDestino()).build());
    }

    public void updateStatsFromKudosList(List<Kudo> kudos){
        List<String> to = new ArrayList<>();
        for (Kudo kudo: kudos) {
            to.add(kudo.getDestino());
        }
        List<String> newToList = to.stream()
                .distinct()
                .collect(Collectors.toList());
        for (String nickName: newToList) {
            int stat = getKudosCountByNickName(nickName);
            updateUserStat(stat, nickName);
            logger.info("Updated stats for user: "+nickName );
            influxDb.getInstance().write(Point.measurement(Influx.STATS_REQUESTS_POINT_MEASUREMENT).time(System.currentTimeMillis(),
                    TimeUnit.MILLISECONDS).addField(Influx.STATS_UPDATE_REQUEST,"UPDATE STATS: "+nickName).build());
        }
    }


    private int getKudosCountByNickName(String nickName){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<Kudo>> response = restTemplate.exchange
                (kudosServiceUrl+ ApiConstants.GET_KUDOS_COUNT_PATH+nickName,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Kudo>>() {});
        return response.getBody().size();
    }

    private void updateUserStat(int stat, String nickName){

        RestTemplate  restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject personJsonObject = new JSONObject();
        personJsonObject.put(UsersDbFields.NICK_NAME, nickName);
        personJsonObject.put(UsersDbFields.NRO_KUDOS, stat);
        HttpEntity<String> request = new HttpEntity<String>(personJsonObject.toString(), headers);
        restTemplate.put(usersServiceUrl+ApiConstants.PUT_USER_STATS, request, String.class);
    }



}

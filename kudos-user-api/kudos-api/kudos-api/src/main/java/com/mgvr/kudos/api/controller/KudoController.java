package com.mgvr.kudos.api.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.mgvr.kudos.api.com.mgvr.kudos.api.constants.*;
import com.mgvr.kudos.api.config.InfluxDb;
import com.mgvr.kudos.api.model.KudoSearch;
import com.mgvr.kudos.api.service.KudoService;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mgvr.kudos.api.model.Kudo;

@RestController
@RequestMapping(KudosApiRoutes.BASE_ROUTE)
public class KudoController {

	@Autowired
	KudoService service;

	@Autowired
	InfluxDb influxDb;

	private static final Logger logger = LoggerFactory.getLogger(KudoController.class);

	@PostMapping(KudosApiRoutes.POST_KUDO)
	public ResponseEntity<String> saveKudo(@RequestBody Kudo kudo) throws IOException {
		if(!service.createKudo(kudo)){
			return new ResponseEntity<>(ApiMessages.KUDOS_NOT_CREATED, HttpStatus.CONFLICT);
		}
		logger.info("Processing POST KUDO method: " +KudosApiRoutes.POST_KUDO);
		logger.info("CREATED KUDO: "+kudo.getTema()+" FROM: "+kudo.getFuente()+" TO: "+kudo.getDestino());
		influxDb.getInstance().write(Point.measurement(Influx.API_REQUESTS_POINT_MEASUREMENT).time(System.currentTimeMillis(),
				TimeUnit.MILLISECONDS).addField(Influx.API_REQUESTS_CREATE_EVENT,"CREATED KUDO: "+kudo.getTema()+" FROM: "+kudo.getFuente()+" TO: "+kudo.getDestino()).build());
		return new ResponseEntity<>(ApiMessages.CREATED, HttpStatus.OK);
	}
	@DeleteMapping(KudosApiRoutes.DELETE_KUDO)
	public ResponseEntity<String> deleteKudo(@PathVariable String id) {
		service.deleteKudo(id);
		logger.info("Processing DELETE KUDO method: " +KudosApiRoutes.DELETE_KUDO);
		logger.info("DELETED KUDO: "+id);
		influxDb.getInstance().write(Point.measurement(Influx.API_REQUESTS_POINT_MEASUREMENT).time(System.currentTimeMillis(),
				TimeUnit.MILLISECONDS).addField(Influx.API_REQUESTS_DELETE_EVENT,"DELETED KUDO: "+id).build());
		return new ResponseEntity<>(ApiMessages.DELETED, HttpStatus.OK);
	}
	@GetMapping(KudosApiRoutes.GET_KUDOS)
	public ResponseEntity<?> getKudos(@RequestParam Map<String,String> pagination, @RequestBody(required=false) KudoSearch kudo){
		try{
			List<Kudo> listKudos = service.getKudos(kudo, pagination);
			logger.info("Processing GET KUDOS method: " +KudosApiRoutes.GET_KUDOS);
			influxDb.getInstance().write(Point.measurement(Influx.API_REQUESTS_POINT_MEASUREMENT).time(System.currentTimeMillis(),
					TimeUnit.MILLISECONDS).addField(Influx.API_REQUESTS_GET_EVENT,"GET KUDOS").build());
			return new ResponseEntity<>(listKudos, HttpStatus.OK);
		} catch (Exception e){
			return new ResponseEntity<>(ApiMessages.GET_KUDOS_ERROR, HttpStatus.CONFLICT);
		}
	}
	@GetMapping(KudosApiRoutes.GET_KUDO)
	public ResponseEntity<Kudo> getKudos(@PathVariable String id){
		logger.info("Processing GET KUDOS method: " +KudosApiRoutes.GET_KUDO);
		influxDb.getInstance().write(Point.measurement(Influx.API_REQUESTS_POINT_MEASUREMENT).time(System.currentTimeMillis(),
				TimeUnit.MILLISECONDS).addField(Influx.API_REQUESTS_GET_EVENT,"GET KUDO").build());
		return new ResponseEntity<>(service.getKudoById(id), HttpStatus.OK);
	}

	@GetMapping(KudosApiRoutes.GET_KUDO_BY_NICK_NAME)
	public ResponseEntity<List<Kudo>> getKudosbyNickName(@PathVariable String nickName){
		return new ResponseEntity<>(service.getKudosByNickName(nickName), HttpStatus.OK);
	}
}

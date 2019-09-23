package com.mgvr.kudos.user.api.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.mgvr.kudos.user.api.config.InfluxDb;
import com.mgvr.kudos.user.api.constants.*;
import com.mgvr.kudos.user.api.model.UserSearch;
import com.mgvr.kudos.user.api.service.UserService;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mgvr.kudos.user.api.model.User;


@RestController
@RequestMapping(UserApiRoutes.BASE_ROUTE)
public class UserController {

    @Autowired
	UserService service;

    @Autowired
    InfluxDb influxDb;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@PostMapping(UserApiRoutes.POST_USER)
	public ResponseEntity<String> saveUser(@RequestBody User user) {
		service.addUser(user);
        logger.info("Processing POST USER method: " +UserApiRoutes.POST_USER);
        logger.info("CREATED USER: " +user.getId());
        influxDb.getInstance().write(Point.measurement(Influx.API_REQUESTS_POINT_MEASUREMENT).time(System.currentTimeMillis(),
                TimeUnit.MILLISECONDS).addField(Influx.API_REQUESTS_CREATE_EVENT,"CREATED USER: "+user.getId()).build());
        return new ResponseEntity<>(ApiMessages.USER_CREATED, HttpStatus.OK);
	}

	@GetMapping(UserApiRoutes.GET_USERS)
	public ResponseEntity<?> getAllUsers(@RequestParam Map<String,String> pagination,@RequestBody(required=false)  UserSearch user) {
	    try{
            List<User> listUsers = service.getUsers(user, pagination);
            logger.info("Processing GET USERS method: " +UserApiRoutes.GET_USERS);
            influxDb.getInstance().write(Point.measurement(Influx.API_REQUESTS_POINT_MEASUREMENT).time(System.currentTimeMillis(),
                    TimeUnit.MILLISECONDS).addField(Influx.API_REQUESTS_GET_EVENT,"GET USERS").build());
            return new ResponseEntity<>(listUsers, HttpStatus.OK);
        } catch (Exception e){
            logger.error("Error in GET USERS method: " +UserApiRoutes.GET_USERS);
            return new ResponseEntity<>(ApiMessages.GET_USERS_ERROR, HttpStatus.OK);
        }
	}
	
	@GetMapping(UserApiRoutes.GET_USER)
	public ResponseEntity<?> getUser(@RequestParam Map<String,String> allParams) throws IOException {
        if (allParams.size() == 0 || allParams.size() > 1) {
            return ResponseEntity.badRequest().body(ApiMessages.GET_USER_REQUIRED_PARAMETERS);
        }
        String field = allParams.entrySet().iterator().next().getKey().toString();
        String value = allParams.get(field);
        User user= service.getUserByField(field,value);
        if(user==null){
            logger.error("Error in GET USER method: " +UserApiRoutes.GET_USER);
            return ResponseEntity.notFound().build();
        }
        logger.info("Processing GET USERS method: " +UserApiRoutes.GET_USER);
        influxDb.getInstance().write(Point.measurement(Influx.API_REQUESTS_POINT_MEASUREMENT).time(System.currentTimeMillis(),
                TimeUnit.MILLISECONDS).addField(Influx.API_REQUESTS_GET_EVENT,"GET ALL USERS").build());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

	
	@PutMapping(UserApiRoutes.PUT_USER)
	public ResponseEntity<String> updateUser(@PathVariable String id, @RequestBody User user) {
        logger.info("Processing PUT USER method: " +UserApiRoutes.PUT_USER);
        logger.info("UPDATED USER " +user.getId());
        influxDb.getInstance().write(Point.measurement(Influx.API_REQUESTS_POINT_MEASUREMENT).time(System.currentTimeMillis(),
                TimeUnit.MILLISECONDS).addField(Influx.API_REQUESTS_UPDATE_EVENT,"PUT USER: "+user.getId()).build());
        return new ResponseEntity<>(service.updateUser(id, user), HttpStatus.OK);
	}

    @PutMapping(UserApiRoutes.PUT_USER_STATS)
    public ResponseEntity<String> updateUserStats(@RequestBody User user) {
        logger.info("Processing PUT USER STATS method: " +UserApiRoutes.PUT_USER_STATS);
        logger.info("UPDATED USER " +user.getId());
        influxDb.getInstance().write(Point.measurement(Influx.API_REQUESTS_POINT_MEASUREMENT).time(System.currentTimeMillis(),
                TimeUnit.MILLISECONDS).addField(Influx.API_REQUESTS_UPDATE_EVENT,"PUT USER: "+user.getNickName()).build());
        return new ResponseEntity<>(service.updateUserStats(user), HttpStatus.OK);
    }
	
	@DeleteMapping(UserApiRoutes.DELETE_USER)
	public ResponseEntity<String> deleteUser(@PathVariable String id) {
		if(service.deleteUser(id)){
            logger.info("Processing DELETE USER method: " +UserApiRoutes.DELETE_USER);
            logger.info("DELETED USER " +id);
            influxDb.getInstance().write(Point.measurement(Influx.API_REQUESTS_POINT_MEASUREMENT).time(System.currentTimeMillis(),
                    TimeUnit.MILLISECONDS).addField(Influx.API_REQUESTS_DELETE_EVENT,"DELETE USER: "+id).build());
			return new ResponseEntity<>(ApiMessages.USER_DELETED, HttpStatus.OK);
		}
        logger.error("Error in DELETE USER method: " +UserApiRoutes.DELETE_USER);
        return new ResponseEntity<>(ApiMessages.USER_NOT_DELETED, HttpStatus.CONFLICT);
	}
}

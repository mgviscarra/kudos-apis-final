package com.mgvr.kudos.user.api.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.mgvr.kudos.user.api.constants.*;
import com.mgvr.kudos.user.api.service.UserService;
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

	@PostMapping(UserApiRoutes.POST_USER)
	public ResponseEntity<String> saveUser(@RequestBody User user) {
		service.addUser(user);
        return new ResponseEntity<>(ApiMessages.USER_CREATED, HttpStatus.OK);
	}

	@GetMapping(UserApiRoutes.GET_USERS)
	public ResponseEntity<List<User>> getAllUsers() {
		List<User> listUsers = service.getUsers();
        return new ResponseEntity<>(listUsers, HttpStatus.OK);
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
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

	
	@PutMapping(UserApiRoutes.PUT_USER)
	public ResponseEntity<String> updateUser(@PathVariable String id, @RequestBody User user) {
		service.updateUser(id, user);
        return new ResponseEntity<>(ApiMessages.USER_UPDATED, HttpStatus.OK);
	}
	
	@DeleteMapping(UserApiRoutes.DELETE_USER)
	public ResponseEntity<String> deleteUser(@PathVariable String id) {
		if(service.deleteUser(id)){
			return new ResponseEntity<>(ApiMessages.USER_DELETED, HttpStatus.OK);
		}
        return new ResponseEntity<>(ApiMessages.USER_NOT_DELETED, HttpStatus.CONFLICT);
	}
}

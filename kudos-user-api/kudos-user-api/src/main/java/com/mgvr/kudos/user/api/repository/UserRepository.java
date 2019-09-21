package com.mgvr.kudos.user.api.repository;

import com.mgvr.kudos.user.api.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {

}


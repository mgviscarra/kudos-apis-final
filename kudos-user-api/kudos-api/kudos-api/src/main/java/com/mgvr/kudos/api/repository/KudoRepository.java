package com.mgvr.kudos.api.repository;



import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.mgvr.kudos.api.model.Kudo;

@Repository
public interface KudoRepository extends MongoRepository<Kudo, Long> {

}

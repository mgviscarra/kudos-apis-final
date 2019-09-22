package com.mgvr.kudos.user.api.repository;

import com.mgvr.kudos.user.api.model.EsUser;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EsUserRepository extends ElasticsearchRepository<EsUser, String> {

}

package com.mgvr.kudos.api.repository;

import com.mgvr.kudos.api.model.EsKudo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EsKudoRepository extends ElasticsearchRepository<EsKudo, String> {
}

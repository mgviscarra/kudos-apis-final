package com.mgvr.kudos.api.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mgvr.kudos.api.com.mgvr.kudos.api.constants.ApiParameters;
import com.mgvr.kudos.api.com.mgvr.kudos.api.constants.DbFields;
import com.mgvr.kudos.api.model.EsKudo;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mgvr.kudos.api.model.DatabaseSequence;
import com.mgvr.kudos.api.model.Kudo;

import static org.elasticsearch.index.query.Operator.AND;
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

@Repository
public class KudoDao {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Value("${elasticsearch.index.name}")
	private String indexName;

	@Value("${elasticsearch.user.type}")
	private String userTypeName;

	@Autowired
	private ElasticsearchTemplate esTemplate;
	
	public List<Kudo> getAllkudos(Map<String,String> pagination) {
		int size = 100;
		int page = 1;
		if (pagination.size()!=0){
			size = Integer.parseInt(pagination.get(ApiParameters.SIZE));
			page = Integer.parseInt(pagination.get(ApiParameters.PAGE));
		}
		List<EsKudo> esKudos = getAllEsKudos(page,size);
		List<Kudo> kudos = new ArrayList<>();
		for (EsKudo kudo: esKudos) {
			kudos.add(getKudoById(Long.parseLong(kudo.getId())));
		}
		return kudos;
	}

	public List<Kudo> getKudosByFuzzyContent(String name, Map<String, String> pagination){
		int size = 100;
		int page = 1;
		if (pagination.size()!=0){
			size = Integer.parseInt(pagination.get(ApiParameters.SIZE));
			page = Integer.parseInt(pagination.get(ApiParameters.PAGE));
		}
		List<EsKudo> esUsers = getKudoByFuzzyContent(name,page,size);
		List<Kudo> users = new ArrayList<>();
		for (EsKudo user: esUsers) {
			users.add(getKudoById(Long.parseLong(user.getId())));
		}
		return users;
	}
	
	public void deleteKudo(Long id) {
		Kudo kudo = new Kudo();
		kudo.setId(id);
		mongoTemplate.remove(kudo);
		deleteIndex(Long.toString(kudo.getId()));
	}
	
	public void createKudo(Kudo kudo) {
		long seq = getNextSequence();
		kudo.setId(seq);
		mongoTemplate.save(kudo);
		indexKudo(kudo);
	}
	
	public long getNextSequence()
    {
		DatabaseSequence seq = mongoTemplate.findById("1", DatabaseSequence.class);
		if(seq==null) {
			seq = new DatabaseSequence();
			seq.setId("1");
			seq.setSeq(1);
			mongoTemplate.save(seq);
		} else {
			Query query = new Query();
			query.addCriteria(Criteria.where(DbFields.ID).is("1"));
			Update update = new Update();
			update.set(DbFields.SEQ, seq.getSeq()+1);
			mongoTemplate.updateFirst(query, update,DatabaseSequence.class );
		}
		
		return seq.getSeq();
    }

	public void deleteKudoByFrom(String from){
		Query query = new Query();
		query.addCriteria(Criteria.where(DbFields.FUENTE).is(from));
		List<Kudo> kudos = getKudosByFrom(from);
		mongoTemplate.remove(query, Kudo.class);
		for (Kudo kudo:kudos) {
			deleteIndex(Long.toString(kudo.getId()));
		}
	}

	public void deleteKudoByTo(String to){
		Query query = new Query();
		query.addCriteria(Criteria.where(DbFields.DESTINO).is(to));
		List<Kudo> kudos = getKudosByNickName(to);
		mongoTemplate.remove(query, Kudo.class);
		for (Kudo kudo:kudos) {
			deleteIndex(Long.toString(kudo.getId()));
		}
	}

	public List<Kudo> getKudosByNickName(String nickName){
		Query query = new Query();
		query.addCriteria(Criteria.where(DbFields.DESTINO).is(nickName));
		return mongoTemplate.find(query, Kudo.class);

	}

	public List<Kudo> getKudosByFrom(String from){
		Query query = new Query();
		query.addCriteria(Criteria.where(DbFields.FUENTE).is(from));
		List<Kudo> kudos = mongoTemplate.find(query,Kudo.class);
		return kudos;
	}

	public Kudo getKudoById(long id) {
		Query query = new Query();
		query.addCriteria(Criteria.where(DbFields.ID).is(id));
		return mongoTemplate.findOne(query, Kudo.class);
	}

	private void indexKudo(Kudo kudo){
		IndexQuery userQuery = new IndexQuery();
		userQuery.setIndexName(indexName);
		userQuery.setType(userTypeName);
		EsKudo esKudo = new EsKudo();
		esKudo.setId(String.valueOf(kudo.getId()));
		esKudo.setTema(kudo.getTema());
		esKudo.setTexto(kudo.getTema());
		userQuery.setObject(esKudo);
		esTemplate.index(userQuery);
	}

	private void deleteIndex(String id){
		esTemplate.delete(indexName, userTypeName, id);
	}

	private List<EsKudo> getAllEsKudos(int page, int size){
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withPageable(new PageRequest(page-1, size))
				.build();
		return esTemplate.queryForList(searchQuery, EsKudo.class);
	}

	private List<EsKudo> getKudoByFuzzyContent(String content, int page, int size){
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withQuery(multiMatchQuery(content)
						.field(DbFields.TEMA)
						.field(DbFields.TEXTO)
						.type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
						.operator(AND)
						.fuzziness(Fuzziness.TWO)
						.prefixLength(0)).withPageable(new PageRequest(page-1, size))
				.build();
		return esTemplate.queryForList(searchQuery, EsKudo.class);
	}
}

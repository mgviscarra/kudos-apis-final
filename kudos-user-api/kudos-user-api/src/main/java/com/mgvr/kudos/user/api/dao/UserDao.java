package com.mgvr.kudos.user.api.dao;

import java.util.List;

import com.mgvr.kudos.user.api.constants.ApiMessages;
import com.mgvr.kudos.user.api.constants.DbFields;
import com.mgvr.kudos.user.api.model.DatabaseSequence;
import com.mgvr.kudos.user.api.model.EsUser;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mgvr.kudos.user.api.model.User;

@Repository
public class UserDao {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Value("${elasticsearch.index.name}")
	private String indexName;

	@Value("${elasticsearch.user.type}")
	private String userTypeName;

	@Autowired
	private ElasticsearchTemplate esTemplate;

	@Autowired
	ElasticsearchOperations elasticsearchTemplate;
	
	public void saveUser(User user) {
		long sequence = getNextSequence();
		user.setId(sequence);
		mongoTemplate.save(user);
		indexUser(user);

	}
	

	public List<User> getAllUsers() {
		return mongoTemplate.findAll(User.class);
	}
	
	public User getUserById(long id) {
		Query query = new Query();
		query.addCriteria(Criteria.where(DbFields.ID).is(id));
		return mongoTemplate.findOne(query, User.class);
	}

	public User getUserByRealName(String realName){
		Query query = new Query();
		query.addCriteria(Criteria.where(DbFields.REAL_NAME).is(realName));
		User user;
		try{
			 user = mongoTemplate.findOne(query, User.class);
		}
		catch (Exception e){
			return null;
		}
		return user;
	}

	public User getUserByNickName(String nickName){
		Query query = new Query();
		query.addCriteria(Criteria.where(DbFields.NICK_NAME).is(nickName));
		User user;
		try{
			user = mongoTemplate.findOne(query, User.class);
		}
		catch (Exception e){
			return null;
		}
		return user;
	}
	
	public String updateUser(User user) {
		if(getUserById(user.getId())!=null){
			mongoTemplate.save(user);
			return ApiMessages.USER_UPDATED;
		}
		return ApiMessages.USER_NOT_UPDATED;
	}
	
	public void deleteUser(User user) {
		mongoTemplate.remove(user);
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
			org.springframework.data.mongodb.core.query.Query query =
					new org.springframework.data.mongodb.core.query.Query();
			query.addCriteria(Criteria.where(DbFields.ID).is("1"));
			Update update = new Update();
			update.set(DbFields.SEQ, seq.getSeq()+1);
			mongoTemplate.updateFirst(query, update,DatabaseSequence.class );
		}
		return seq.getSeq();
	}

	private void indexUser(User user){
		IndexQuery userQuery = new IndexQuery();
		userQuery.setIndexName(indexName);
		userQuery.setType(userTypeName);
		EsUser esUser = new EsUser();
		esUser.setId(String.valueOf(user.getId()));
		esUser.setNickName(user.getNickName());
		esUser.setRealName(user.getRealName());
		userQuery.setObject(esUser);
		esTemplate.index(userQuery);
	}

	public EsUser getUserByIdElastic(String id){
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withFilter(QueryBuilders.matchQuery("id", id)).build();
		List<EsUser> users = esTemplate.queryForList(searchQuery, EsUser.class);
		if(!users.isEmpty()) {
			return users.get(0);
		}
		return null;
	}

}

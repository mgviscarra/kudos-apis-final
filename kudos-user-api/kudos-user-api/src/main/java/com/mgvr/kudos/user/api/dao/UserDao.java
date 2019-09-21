package com.mgvr.kudos.user.api.dao;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.mgvr.kudos.user.api.constants.DbFields;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mgvr.kudos.user.api.model.User;

@Repository
@Transactional
public class UserDao {
	@Autowired
	private SessionFactory factory;
	
	public void saveUser(User user) {
		user.setId((int)getSession().save(user));
		getSession().update(user);
	}
	

	public List<User> getAllUsers() {
		CriteriaBuilder cb = getSession().getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);
		Root<User> rootEntry = cq.from(User.class);
		CriteriaQuery<User> all = cq.select(rootEntry);
		TypedQuery<User> allQuery = getSession().createQuery(all);
		return allQuery.getResultList();
	}
	
	public User getUser(int id) {
		User user = (User) getSession().get(User.class, id);
		return user;
	}

	public User getUserByRealName(String realName){
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<User> criteria = builder.createQuery(User.class);
		Root<User> root = criteria.from(User.class);
		criteria.select(root).where(builder.equal(root.get(DbFields.REAL_NAME),realName));
		Query<User> userQuery = getSession().createQuery(criteria);
		User user;
		try{
			 user = userQuery.getSingleResult();
		}
		catch (Exception e){
			return null;
		}
		return user;
	}

	public User getUserByNickName(String nickName){
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<User> criteria = builder.createQuery(User.class);
		Root<User> root = criteria.from(User.class);
		criteria.select(root).where(builder.equal(root.get(DbFields.NICK_NAME),nickName));
		Query<User> userQuery = getSession().createQuery(criteria);
		User user;
		try{
			user = userQuery.getSingleResult();
		}
		catch (Exception e){
			return null;
		}
		return user;
	}
	
	public void updateUser(User user) {
		getSession().update(user);
	}
	
	public void deleteUser(User user) {
		getSession().delete(user);
	}
	
	private Session getSession() {
		Session session = factory.getCurrentSession();
		if (session == null) {
			session = factory.openSession();
		}
		return session;
	}


}

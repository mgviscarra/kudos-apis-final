package com.mgvr.kudos.user.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "users")
public class User {
	@Id
	private long id;
	private String nickName;
	private String realName;
	@JsonIgnore
	private String email;
	@Transient
	private List<Kudo> kudos;
	private int nroKudos;

	public int getNroKudos() {
		return nroKudos;
	}

	public void setNroKudos(int nroKudos) {
		this.nroKudos = nroKudos;
	}

	public List<Kudo> getKudos() {
		return kudos;
	}

	public void setKudos(List<Kudo> kudos) {
		this.kudos = kudos;
	}

	public String getNickName() {
		return nickName;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public long getId() {
		return id;
	}
}

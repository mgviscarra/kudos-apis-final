package com.mgvr.kudos.user.api;

import com.monitorjbl.json.JsonViewSupportFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication (exclude = HibernateJpaAutoConfiguration.class)
public class KudosUserApiApplication {
	@Bean
	public JsonViewSupportFactoryBean views() {
		return new JsonViewSupportFactoryBean();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(KudosUserApiApplication.class, args);
	}

}

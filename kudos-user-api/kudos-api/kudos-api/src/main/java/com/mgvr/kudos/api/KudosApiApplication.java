package com.mgvr.kudos.api;

import com.monitorjbl.json.JsonViewSupportFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@EnableElasticsearchRepositories
public class KudosApiApplication {
	@Bean
	public JsonViewSupportFactoryBean views() {
		return new JsonViewSupportFactoryBean();
	}

	public static void main(String[] args) {
		SpringApplication.run(KudosApiApplication.class, args);
	}

}

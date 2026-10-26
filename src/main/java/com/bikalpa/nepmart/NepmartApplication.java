package com.bikalpa.nepmart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/*
 * @EnableJpaAuditing is required by BaseEntity: without it the @CreatedDate /
 * @LastModifiedDate fields stay null and every insert fails the
 * "created_at is not nullable" constraint.
 */
@SpringBootApplication
@EnableJpaAuditing
@ConfigurationPropertiesScan
public class NepmartApplication {

	public static void main(String[] args) {
		SpringApplication.run(NepmartApplication.class, args);
	}

}

package org.eapo.service.esign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@ComponentScan(basePackages = "org.eapo.service.esign.rest")
public class EsignApplication {

	public static void main(String[] args) {
		SpringApplication.run(EsignApplication.class, args);
	}

}

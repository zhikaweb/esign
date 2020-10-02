package org.eapo.service.esign;


import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.security.*;

@SpringBootApplication
@Configuration
@ComponentScan(basePackages = "org.eapo.service.esign.rest")
public class EsignApplication {


	public static void main(String[] args)
	{
		addBouncyCastleAsSecurityProvider();
		SpringApplication.run(EsignApplication.class, args);
	}


	public static void addBouncyCastleAsSecurityProvider() {
		Security.addProvider(new BouncyCastleProvider());
	}

}

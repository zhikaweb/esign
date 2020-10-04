package org.eapo.service.esign;


import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eapo.service.esign.crypto.CertificateCreator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.security.Security;

@SpringBootApplication
@Configuration
@ComponentScan(basePackages = {"org.eapo.service.esign.rest",
        "org.eapo.service.esign.service",
        "org.eapo.service.esign.crypto"})
public class EsignApplication {


    public static void main(String[] args) throws Exception {
        addBouncyCastleAsSecurityProvider();
        ApplicationContext context = SpringApplication.run(EsignApplication.class, args);

        // нижеследущая строка пересоздает сертификат!
        //	context.getBean(CertificateCreator.class).generateSelfSignedX509Certificate();

    }


    private static void addBouncyCastleAsSecurityProvider() {
        Security.addProvider(new BouncyCastleProvider());
    }

}

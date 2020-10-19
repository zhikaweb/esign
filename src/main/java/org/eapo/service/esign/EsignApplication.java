package org.eapo.service.esign;


import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eapo.service.esign.crypto.RootCertificateCreator;
import org.eapo.service.esign.service.phoenix.DocLoadManager;
import org.eapo.service.esign.service.phoenix.PhoenixService;
import org.epo.utils.EPODate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;
import java.security.Security;

@SpringBootApplication
@Configuration
@ComponentScan(basePackages = {"org.eapo.service.esign.rest",
        "org.eapo.service.esign.service",
        "org.eapo.service.esign.service.converter",
        "org.eapo.service.esign.service.phoenix",
        "org.eapo.service.esign.crypto",
        "org.eapo.service.esign.exception"
})
public class EsignApplication {

    private static Logger logger = LoggerFactory.getLogger(EsignApplication.class.getName());

    public static void main(String[] args) throws Exception {
        logger.info("Starting Esign service...");
        addBouncyCastleAsSecurityProvider();
        ApplicationContext context = SpringApplication.run(EsignApplication.class, args);
        logger.info("Esign service is ready");
    }

    public static void addBouncyCastleAsSecurityProvider() {
        logger.debug("Adding BouncyCastleAsSecurityProvider");
        Security.setProperty("crypto.policy", "unlimited");
        Security.addProvider(new BouncyCastleProvider());
        logger.debug("BouncyCastle is ready!");
    }
}

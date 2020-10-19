package org.eapo.service.esign;


import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eapo.service.esign.crypto.RootCertificateCreator;
import org.eapo.service.esign.service.phoenix.DocLoadManager;
import org.epo.utils.EPODate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.security.Security;

//@SpringBootApplication
//@Configuration

/*
@ComponentScan(basePackages = {"org.eapo.service.esign.rest",
        "org.eapo.service.esign.service",
        "org.eapo.service.esign.service.converter",
        "org.eapo.service.esign.service.phoenix",
        "org.eapo.service.esign.crypto",
        "org.eapo.service.esign.exception"
})

 */
public class EsignApplication {

private static Logger logger = LoggerFactory.getLogger(EsignApplication.class.getName());

    public static void main(String[] args) throws Exception {

        doIt();
      /*
        logger.info("Starting Esign service...");
        addBouncyCastleAsSecurityProvider();
        ApplicationContext context = SpringApplication.run(EsignApplication.class, args);
        logger.info("Esign service is ready");


        if ("true".equalsIgnoreCase(context.getEnvironment().getProperty("esign.recreatekeys"))) {
            logger.warn("Recreation certificate!");
            context.getBean(RootCertificateCreator.class).generateSelfSignedX509Certificate();
        }

       */
    }


    public static void addBouncyCastleAsSecurityProvider() {
        logger.debug("Adding BouncyCastleAsSecurityProvider");
        Security.setProperty("crypto.policy", "unlimited");
        Security.addProvider(new BouncyCastleProvider());
        logger.debug("BouncyCastle is ready!");
    }

    public static void doIt() throws Exception {

        System.setProperty("java.rmi.server.useCodebaseOnly", "false");
        System.setProperty("java.security.policy", "C:\\Users\\AStal\\projects\\EAPO-UploaderGUI\\resources\\common\\java.pol");
        System.setProperty("hxHelperPropsFile", "C:\\Users\\AStal\\projects\\EAPO-UploaderGUI\\src\\main\\resources\\appload.properties");
        System.setProperty("DRPROPS", "C:\\Users\\AStal\\projects\\EAPO-UploaderGUI\\src\\main\\resources\\appload.properties");


        DocLoadManager docLoadManager = new DocLoadManager();

        String dossier="201990100";
        short type=212;
        String annotation = "";
        EPODate aDate = new EPODate();
        String doccode = "DOCRU";
        String docSource = "C:\\desc_amnd.pdf";
        String procedure="";
        boolean isSendMsg = false;
        String sendMsgToUser = "";
        String sendMsgToTeam = "";
        String textMailBox = "";
        String historyStr = "";
        String filter = ".pdf";
        File[] files = {new File(docSource)};
        docLoadManager.addFromLocation(files, filter);

        docLoadManager.load(dossier, type, annotation, aDate, doccode, docSource, procedure, isSendMsg, sendMsgToUser, sendMsgToTeam, textMailBox, historyStr);


    }

}

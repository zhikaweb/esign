package org.eapo.service.esign.service;


import org.eapo.service.esign.EsignApplication;
import org.eapo.service.esign.crypto.KeyStoreHelper;
import org.eapo.service.esign.service.stamper.UserStampCreator;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class StampCreatorTest {


    @Autowired
    UserStampCreator userStampCreator;

    @Autowired
    KeyStoreHelper keyStoreHelper;

    @Value("${esigner.crypto.keystore.password}")
    private String rootPassword;


  //  @Ignore
    @Test
    public void test() throws NoSuchAlgorithmException, CertificateException, NoSuchProviderException, KeyStoreException, IOException {

        EsignApplication.addBouncyCastleAsSecurityProvider();

        X509Certificate certificate = (X509Certificate) keyStoreHelper.load("astal").getCertificate("astal");

        byte[] bytes = userStampCreator.build(certificate);

        try (FileOutputStream fos = new FileOutputStream("C:\\TEMP\\doc-signer\\img.png")) {
            fos.write(bytes);
        }


    }

    ;


}

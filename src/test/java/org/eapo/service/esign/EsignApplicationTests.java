package org.eapo.service.esign;

import org.bouncycastle.operator.OperatorCreationException;
import org.eapo.service.esign.crypto.RootCertificateCreator;
import org.eapo.service.esign.crypto.UserCertificateCreator;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

// @Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class EsignApplicationTests {


    @Value("${esigner.userstamp.file}")
    private String userStampFile;

    @Autowired
    private RootCertificateCreator rootCertificateCreator;


    @Autowired
    private UserCertificateCreator userCertificateCreator;

    @Before
    public void before() {
        EsignApplication.addBouncyCastleAsSecurityProvider();
    }

    @Test
    public void createCACert() throws Exception {

        rootCertificateCreator.generateSelfSignedX509Certificate();
    }

  //  @Ignore
    @Test
    public void createUserCert() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, OperatorCreationException, KeyStoreException, NoSuchProviderException, IOException {

        userCertificateCreator.create("Путин", "vputin");
    }

    @Ignore
    @Test
    public void read() throws IOException {
        ImageIO.read(new File(userStampFile));
    }
}
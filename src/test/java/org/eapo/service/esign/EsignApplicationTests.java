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
import java.nio.file.Files;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Objects;

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

        userCertificateCreator.create("Чудина Н.А.", "nchudina");
    }

    @Ignore
    @Test
    public void read() throws IOException {
        ImageIO.read(new File(userStampFile));
    }


   // @Ignore
    @Test
    public void generateAllUsersCerts() throws Exception {

        rootCertificateCreator.generateSelfSignedX509Certificate();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("users.txt").getFile());
        Files.readAllLines(file.toPath()).stream().filter(s->!s.contains("(--)")).forEach(string->{
            try {
                String[] split = string.split("\t");
                String logname = split[0];
                String nmuser = split[1];
                System.out.println(split[0] + "->" + split[1]);
                userCertificateCreator.create(nmuser, logname);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    @Test
    public void getCodeFromFile() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("excludeDoccodes.txt")).getFile());
        boolean a = Files.readAllLines(file.toPath()).stream().anyMatch(s1 -> s1.equals("Patt"));
        System.out.println(a);
    }

}

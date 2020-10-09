package org.eapo.service.esign.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;

@Service
public class KeyStoreHelper {

    private static Logger logger = LoggerFactory.getLogger(KeyStoreHelper.class.getName());

    public static final String CA = "EAPO_CA";

    private static final String KEYSTORE_EXT = ".pkcs";
    private static final String CERT_EXT = ".cert";



    @Value("${esigner.crypto.cryptoprovider}")
    private String cryptoprovider;

    @Value("${esigner.crypto.privatekey.format}")
    private String privateKeyFormat;

    @Value("${esigner.crypto.keystore.password}")
    private String keystorePassword;

    @Value("${esigner.crypto.keystore}")
    private
    String keystore;


    public KeyStore load(String certHolder) throws IOException, NoSuchProviderException, KeyStoreException, CertificateException, NoSuchAlgorithmException {

        String keystorePath = getKeyPath(certHolder);
        logger.debug("load key from keystore {}", keystorePath);
        FileInputStream inputStream = new FileInputStream(keystorePath);
        KeyStore keyStore = KeyStore.getInstance(privateKeyFormat, cryptoprovider);
        keyStore.load(inputStream, keystorePassword.toCharArray());
        return keyStore;
    }


    public void store(String certHolder,  PrivateKey key, Certificate cert) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {

        String pathToStore = getKeyPath(certHolder);
        logger.debug("Save key to keystore {}", pathToStore);

        KeyStore keyStore = KeyStore.getInstance(privateKeyFormat, cryptoprovider);
        keyStore.load(null, null);
        keyStore.setKeyEntry(certHolder, key, null, new java.security.cert.Certificate[]{cert});

        File file = new File(pathToStore);
        if (!file.createNewFile()){
            logger.error("Ошибка создания файла ");
        };

        FileOutputStream keyStoewFOS = new FileOutputStream(file);
        keyStore.store(keyStoewFOS, keystorePassword.toCharArray());
        keyStoewFOS.flush();
        keyStoewFOS.close();
    }

    public void store(String certHolder, Certificate cert) throws CertificateEncodingException, IOException {

        String pathToStore = getCertPath(certHolder);

        logger.debug("Save cert to certstore {}", pathToStore);

        File file = new File(pathToStore);
        if (!file.createNewFile()){
            logger.error("Ошибка создания файла ");
        };

        FileOutputStream certFOS = new FileOutputStream(file);
        certFOS.write(cert.getEncoded());
        certFOS.flush();
        certFOS.close();
    }

    private String getKeyPath(String certHolder){
        return Paths.get(keystore).resolve(certHolder.concat(KeyStoreHelper.KEYSTORE_EXT)).toString();
    }

    private String getCertPath(String certHolder){
        return Paths.get(keystore).resolve(certHolder.concat(KeyStoreHelper.CERT_EXT)).toString();
    }

}

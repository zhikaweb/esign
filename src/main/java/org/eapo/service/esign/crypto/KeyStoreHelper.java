package org.eapo.service.esign.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;

public class KeyStoreHelper {

    private static Logger logger = LoggerFactory.getLogger(KeyStoreHelper.class.getName());

    public static final String KEYSTORE_EXT = ".pkcs";
    public static final String CERT_EXT = ".cert";

    public static final String CA = "EAPO_CA";

    public static KeyStore load(String keystore, String privateKeyFormat, String cryptoprovider, String keystorePassword) throws IOException, NoSuchProviderException, KeyStoreException, CertificateException, NoSuchAlgorithmException {

        FileInputStream inputStream = new FileInputStream(keystore);
        KeyStore keyStore = KeyStore.getInstance(privateKeyFormat, cryptoprovider);
        keyStore.load(inputStream, keystorePassword.toCharArray());
        return keyStore;
    }


    public static void store(String privateKeyFormat, String cryptoprovider, String pathToStore, String keystoreKeyName, String keystorePassword, PrivateKey key, Certificate cert) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        logger.debug("Save key to keystore {}", pathToStore);

        KeyStore keyStore = KeyStore.getInstance(privateKeyFormat, cryptoprovider);
        keyStore.load(null, null);
        keyStore.setKeyEntry(keystoreKeyName, key, null, new java.security.cert.Certificate[]{cert});

        FileOutputStream keyStoewFOS = new FileOutputStream(pathToStore);
        keyStore.store(keyStoewFOS, keystorePassword.toCharArray());
        keyStoewFOS.flush();
        keyStoewFOS.close();
    }

    public static void store(Certificate cert, String pathToStore) throws CertificateEncodingException, IOException {
        logger.debug("Save cert to certstore {}", pathToStore);
        FileOutputStream certFOS = new FileOutputStream(pathToStore);
        certFOS.write(cert.getEncoded());
        certFOS.flush();
        certFOS.close();
    }

}

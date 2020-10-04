package org.eapo.service.esign.crypto;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;


@Service
public class CertificateCreator {

    /*
    static void addBouncyCastleAsSecurityProvider() {
        Security.addProvider(new BouncyCastleProvider());
    }
    */


    @Value("${esigner.crypto.keystore}")
    private
    String keystore;

    @Value("${esigner.crypto.certstore}")
    private
    String certstore;

    @Value("${esigner.crypto.keystore.password}")
    private
    String keystorePassword;

    @Value("${esigner.crypto.keystore.keyname}")
    String keystoreKeyName;

    @Value("${esigner.crypto.certholdername}")
    private
    String certHolderName;

    @Value("${esigner.crypto.cryptoprovider}")
    private
    String cryptoprovider;


    @Value("${esigner.crypto.keyalgorithm}")
    private
    String keyalgorithm;

    @Value("${esigner.crypto.hashalhorithm}")
    private
    String hashalhorithm;

    @Value("${esigner.crypto.privatekey.format}")
    private
    String privateKeyFormat;

    @Value("${esigner.crypto.cert.period}")
    private
    Integer certPeriod;

    @Value("${esigner.crypto.keygenerator.keysize}")
    Integer keySize;


    public X509Certificate generateSelfSignedX509Certificate() throws Exception {
        // addBouncyCastleAsSecurityProvider();

        // generate a key pair
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyalgorithm, cryptoprovider);
        keyPairGenerator.initialize(keySize, new SecureRandom());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();


        X509Certificate cert = generate(keyPair, hashalhorithm, certHolderName, certPeriod);

        KeyStore keyStore = KeyStore.getInstance(privateKeyFormat, cryptoprovider);

        keyStore.load(null, null);

        keyStore.setKeyEntry(keystoreKeyName, keyPair.getPrivate(), null, new java.security.cert.Certificate[]{cert});

        FileOutputStream keyStoewFOS = new FileOutputStream(keystore);

        keyStore.store(keyStoewFOS, keystorePassword.toCharArray());
        keyStoewFOS.flush();
        keyStoewFOS.close();

        FileOutputStream certFOS = new FileOutputStream(certstore);
        certFOS.write(cert.getEncoded());
        certFOS.flush();
        certFOS.close();


        return cert;
    }


    private X509Certificate generate(final KeyPair keyPair,
                                     final String hashAlgorithm,
                                     final String cn,
                                     final int days) throws Exception {
        final Instant now = Instant.now();
        final Date notBefore = Date.from(now);
        final Date notAfter = Date.from(now.plus(Duration.ofDays(days)));

        final ContentSigner contentSigner = new JcaContentSignerBuilder(hashAlgorithm).build(keyPair.getPrivate());
        final X500Name x500Name = new X500Name("CN=" + cn);
        final X509v3CertificateBuilder certificateBuilder =
                new JcaX509v3CertificateBuilder(x500Name,
                        BigInteger.valueOf(now.toEpochMilli()),
                        notBefore,
                        notAfter,
                        x500Name,
                        keyPair.getPublic())
                        .addExtension(Extension.basicConstraints, true, new BasicConstraints(true));

        return new JcaX509CertificateConverter()
                .setProvider(new BouncyCastleProvider()).getCertificate(certificateBuilder.build(contentSigner));
    }


}
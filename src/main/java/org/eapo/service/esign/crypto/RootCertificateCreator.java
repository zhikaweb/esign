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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;


@Service
public class RootCertificateCreator {

    private static Logger logger = LoggerFactory.getLogger(RootCertificateCreator.class.getName());

    @Value("${esigner.crypto.certholdername}")
    private
    String certHolderName;

    @Value("${esigner.crypto.cryptoprovider}")
    private
    String cryptoprovider;


    @Value("${esigner.crypto.keyalgorithm}")
    private
    String keyalgorithm;

    @Value("${esigner.crypto.hashalgorithm}")
    private String hashAlgorithm;


    @Value("${esigner.crypto.cert.period}")
    private
    Integer certPeriod;

    @Value("${esigner.crypto.keygenerator.keysize}")
    private
    Integer keySize;

    @Autowired
    KeyStoreHelper keyStoreHelper;

    public X509Certificate generateSelfSignedX509Certificate() throws Exception {

        logger.info("Start generateSelfSignedX509Certificate");

        logger.debug("Key pair generation with keyalgorithm {} by {} cryptoprovider", keyalgorithm, cryptoprovider);
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyalgorithm, cryptoprovider);
        keyPairGenerator.initialize(keySize, new SecureRandom());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        logger.debug("Certificate for {} creation by hashAlgorithm {} period {} days", certHolderName, hashAlgorithm, certPeriod);
        X509Certificate cert = generate(keyPair, hashAlgorithm, certHolderName, certPeriod);


        keyStoreHelper.store(KeyStoreHelper.CA, keyPair.getPrivate(), cert);
        keyStoreHelper.store(KeyStoreHelper.CA, cert);

        logger.debug("New certificate was created!");
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

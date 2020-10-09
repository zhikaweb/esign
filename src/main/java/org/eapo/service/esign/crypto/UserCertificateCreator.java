package org.eapo.service.esign.crypto;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.security.auth.x500.X500Principal;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class UserCertificateCreator {


    private static Logger logger = LoggerFactory.getLogger(UserCertificateCreator.class.getName());

    @Value("${esigner.crypto.cryptoprovider}")
    private
    String cryptoprovider;


    @Value("${esigner.crypto.keyalgorithm}")
    private
    String keyalgorithm;

    @Value("${esigner.crypto.keygenerator.keysize}")
    private
    Integer keySize;

    @Value("${esigner.crypto.hashalgorithm}")
    private String hashAlgorithm;


    @Value("${esigner.crypto.keystore.password}")
    private String rootPassword;



    @Value("${esigner.crypto.cert.period}")
    private
    Integer certPeriod;

    @Autowired
    KeyStoreHelper keyStoreHelper;

    public X509Certificate create(String user, String certHolder) throws OperatorCreationException, NoSuchProviderException, NoSuchAlgorithmException, IOException, CertificateException, KeyStoreException, UnrecoverableKeyException {


        logger.info("Creating certificate for {}", user);

        logger.debug("Reading CA cert from store");


        KeyStore rootStore = keyStoreHelper.load(KeyStoreHelper.CA);

        java.security.cert.Certificate cert = rootStore.getCertificate(KeyStoreHelper.CA);
        X509Certificate rootx509Cert = (X509Certificate) cert;

        logger.debug("Generating key pair");
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyalgorithm, cryptoprovider);
        keyPairGenerator.initialize(keySize, new SecureRandom());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        PrivateKey rootPrivateKey = (PrivateKey) rootStore.getKey(KeyStoreHelper.CA, rootPassword.toCharArray());

        logger.debug("Generating certificate request");
        String principal = "CN=".concat(user);
        PKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(
                new X500Principal(principal), keyPair.getPublic());
        JcaContentSignerBuilder csBuilder = new JcaContentSignerBuilder(hashAlgorithm);
        ContentSigner signer = csBuilder.build(keyPair.getPrivate());
        PKCS10CertificationRequest csr = p10Builder.build(signer);


        X500Name x500Name = new X500Name("CN=" + user);

        final Instant now = Instant.now();
        final Date notBefore = Date.from(now);
        final Date notAfter = Date.from(now.plus(Duration.ofDays(certPeriod)));

        logger.debug("Generating issued cert builder");
        X509v3CertificateBuilder issuedCertBuilder = new X509v3CertificateBuilder(x500Name,
                BigInteger.valueOf(now.toEpochMilli()),
                notBefore,
                notAfter,
                csr.getSubject(),
                csr.getSubjectPublicKeyInfo());

        JcaX509ExtensionUtils issuedCertExtUtils = new JcaX509ExtensionUtils();


        logger.debug("Adding issued cert builder extensions");
        issuedCertBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));
        issuedCertBuilder.addExtension(Extension.authorityKeyIdentifier, false, issuedCertExtUtils.createAuthorityKeyIdentifier(rootx509Cert));
        issuedCertBuilder.addExtension(Extension.subjectKeyIdentifier, false, issuedCertExtUtils.createSubjectKeyIdentifier(csr.getSubjectPublicKeyInfo()));
        issuedCertBuilder.addExtension(Extension.keyUsage, false, new KeyUsage(KeyUsage.keyEncipherment));


        JcaContentSignerBuilder csrBuilder = new JcaContentSignerBuilder(hashAlgorithm).setProvider(cryptoprovider);
        ContentSigner csrContentSigner = csrBuilder.build(rootPrivateKey);


        logger.debug("Generating certificate");
        X509CertificateHolder issuedCertHolder = issuedCertBuilder.build(csrContentSigner);
        X509Certificate issuerCert =  new JcaX509CertificateConverter().setProvider(cryptoprovider).getCertificate(issuedCertHolder);


        keyStoreHelper.store(certHolder,  keyPair.getPrivate(), issuerCert);


        return issuerCert;
    }



}

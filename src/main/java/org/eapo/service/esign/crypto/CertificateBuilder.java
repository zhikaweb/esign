package org.eapo.service.esign.crypto;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;

import java.security.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.x509.X509V3CertificateGenerator;


public class CertificateBuilder {
    public static void main(String[] args) throws Exception {
        X509Certificate selfSignedX509Certificate = new CertificateBuilder().generateSelfSignedX509Certificate();
        System.out.println(selfSignedX509Certificate);
    }

    public X509Certificate generateSelfSignedX509Certificate() throws Exception
             {
        addBouncyCastleAsSecurityProvider();

        // generate a key pair
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
        keyPairGenerator.initialize(4096, new SecureRandom());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();


                 X509Certificate cert =  generate(keyPair,"SHA256WITHRSA","Eurasian patent office",365);
        /*
        // build a certificate generator
        X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
        X500Principal dnName = new X500Principal("cn=EAPO");

        // add some options
        certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        certGen.setSubjectDN(new X509Name("dc=EAPO"));
        certGen.setIssuerDN(dnName); // use the same
        // yesterday
        certGen.setNotBefore(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
        // in 2 years
        certGen.setNotAfter(new Date(System.currentTimeMillis() + 2 * 365 * 24 * 60 * 60 * 1000));
        certGen.setPublicKey(keyPair.getPublic());
        certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");
        certGen.addExtension(X509Extensions.ExtendedKeyUsage, true,
                new ExtendedKeyUsage(KeyPurposeId.id_kp_timeStamping));

        // finally, sign the certificate with the private key of the same KeyPair
        X509Certificate cert = certGen.generate(keyPair.getPrivate(), "BC");
*/


        KeyStore keyStore = KeyStore.getInstance("PKCS12","BC");

        keyStore.load(null,null);

        keyStore.setKeyEntry("key",keyPair.getPrivate(),null,new java.security.cert.Certificate[]{cert});

                 FileOutputStream outCert = new FileOutputStream("C:\\testcert\\eapo.cert");

                 keyStore.store(outCert, "password".toCharArray());
                 outCert.flush();
                 outCert.close();


        /*
        BASE64Encoder b64 = new BASE64Encoder();
        BufferedWriter out = new BufferedWriter(new FileWriter("C:\\is_rsa"));
        out.write(b64.encode(keyPair.getPrivate().getEncoded()));
        out.close();

        BufferedWriter outCert = new BufferedWriter(new FileWriter("C:\\eapo.cert"));
        outCert.write(b64.encode(cert.getEncoded()));
        outCert.close();
*/


        //   keyPair.getPrivate().getEncoded()


        return cert;
    }

    public static void addBouncyCastleAsSecurityProvider() {
        Security.addProvider(new BouncyCastleProvider());
    }


    public static X509Certificate generate(final KeyPair keyPair,
                                           final String hashAlgorithm,
                                           final String cn,
                                           final int days)
            throws Exception
    {
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
                      //  .addExtension(Extension.subjectKeyIdentifier, false, createSubjectKeyId(keyPair.getPublic()))
                      //  .addExtension(Extension.authorityKeyIdentifier, false, createAuthorityKeyId(keyPair.getPublic()))
                        .addExtension(Extension.basicConstraints, true, new BasicConstraints(true));

        return new JcaX509CertificateConverter()
                .setProvider(new BouncyCastleProvider()).getCertificate(certificateBuilder.build(contentSigner));
    }


}

package org.eapo.service.esign.service;

import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

@Service
public class SignerPdfServiceImpl implements SignerPdfService {


    @Autowired
    StampTextCreator stampText;

    @Value("${esigner.crypto.keystore}")
    private String keystore;

    @Value("${esigner.crypto.certstore}")
    private String certstore;

    @Value("${esigner.crypto.keystore.password}")
    private String keystorePassword;

    @Value("${esigner.crypto.keystore.keyname}")
    String keystoreKeyName;

    @Value("${esigner.crypto.certholdername}")
    private String certHolderName;

    @Value("${esigner.crypto.cryptoprovider}")
    private String cryptoprovider;

    @Value("${esigner.crypto.privatekey.format}")
    private String privateKeyFormat;

    @Value("${esigner.stamp.rectangle.llx}")
    float stampLlx;

    @Value("${esigner.stamp.rectangle.lly}")
    float stampLly;

    @Value("${esigner.stamp.rectangle.urx}")
    float stampUrx;

    @Value("${esigner.stamp.rectangle.ury}")
    float stampUry;

    @Value("${esigner.stamp.page}")
    Integer stampPage;

    @Override
    public byte[] sign(byte[] pdf) throws Exception {

        FileInputStream inputStream = new FileInputStream(keystore);

        KeyStore keyStore = KeyStore.getInstance(privateKeyFormat, cryptoprovider);

        keyStore.load(inputStream, keystorePassword.toCharArray());

        java.security.cert.Certificate cert = keyStore.getCertificate(keystoreKeyName);
        X509Certificate x509 = (X509Certificate) cert;
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(keystoreKeyName, keystorePassword.toCharArray());

        PdfReader reader = new PdfReader(pdf);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0');
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setCertificate(cert);

        appearance.setLayer2Text(stampText.getCertText(x509));


        setVisibleSignatureRotated(stamper, appearance, new Rectangle(stampLlx, stampLly, stampUrx, stampUry), stampPage, null);

        ExternalSignature externalSignature = new PrivateKeySignature(privateKey, "SHA-256", null);
        ExternalDigest externalDigest = new BouncyCastleDigest();
        MakeSignature.signDetached(appearance, externalDigest, externalSignature, new Certificate[]{cert}, null, null, null, 0, MakeSignature.CryptoStandard.CMS);


        os.flush();
        os.close();


        return os.toByteArray();
    }


    private static void setVisibleSignatureRotated(PdfStamper stamper, PdfSignatureAppearance appearance, Rectangle pageRect, int page, String fieldName) throws Exception {
        float height = pageRect.getHeight();
        float width = pageRect.getWidth();
        float llx = pageRect.getLeft();
        float lly = pageRect.getBottom();
        // Visual signature is configured as if it were going to be a regular horizontal visual signature.
        appearance.setVisibleSignature(new Rectangle(llx, lly, llx + height, lly + width), page, null);
        // We trigger premature appearance creation, so independent parts of it can be modified right away.
        appearance.getAppearance();
        // Now we correct the width and height.
        appearance.setVisibleSignature(new Rectangle(llx, lly, llx + width, lly + height), page, fieldName);
        appearance.getTopLayer().setWidth(width);
        appearance.getTopLayer().setHeight(height);


        PdfTemplate n2Layer = appearance.getLayer(2);
        n2Layer.setWidth(width);
        n2Layer.setHeight(height);

        PdfTemplate t = PdfTemplate.createTemplate(stamper.getWriter(), height, width);
        ByteBuffer internalBuffer = t.getInternalBuffer();
        internalBuffer.write(n2Layer.toString().getBytes());
        n2Layer.reset();
        Image textImg = Image.getInstance(t);
        textImg.setInterpolation(true);
        textImg.scaleAbsolute(height, width);
        textImg.setRotationDegrees((float) 90);
        textImg.setAbsolutePosition(0, 0);
        n2Layer.addImage(textImg);
    }


}

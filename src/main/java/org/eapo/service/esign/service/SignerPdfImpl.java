package org.eapo.service.esign.service;

import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.security.*;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;

public class SignerPdfImpl implements SignerPdf {
    @Override
    public byte[] sign(byte[] pdf) throws Exception  {

        FileInputStream inputStream = new FileInputStream("C:\\testcert\\eapo.cert");

        KeyStore keyStore = KeyStore.getInstance("PKCS12","BC");

        keyStore.load(inputStream,"password".toCharArray());

        java.security.cert.Certificate cert = keyStore.getCertificate("key");
        X509Certificate x509 = (X509Certificate) cert;
        PrivateKey privateKey = (PrivateKey)keyStore.getKey("key","password".toCharArray());

        PdfReader reader = new PdfReader(pdf);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0');
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setCertificate( cert);

        appearance.setLayer2Text("Signed by " + x509.getIssuerDN() + "at " + new Date()
                + "\n Certificate "   + x509.getSerialNumber()
                + "\n Valid to " + x509.getNotAfter() );


        setVisibleSignatureRotated(stamper, appearance, new Rectangle(0, 50, 50, 1000), 1, null);



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

package org.eapo.service.esign.service;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.security.*;
import org.eapo.service.esign.crypto.KeyStoreHelper;
import org.eapo.service.esign.exception.EsignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

@Service
public class SignerPdfServiceImpl implements SignerPdfService {

    private static Logger logger = LoggerFactory.getLogger(SignerPdfServiceImpl.class.getName());

    @Autowired
    StampTextCreator stampText;


    @Autowired
    KeyStoreHelper keyStoreHelper;

    @Value("${esigner.crypto.keystore}")
    private String keystore;


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


    @Value("${esigner.stamp.rotation}")
    float stampRotation;

    @Value("${esigner.stamp.page}")
    Integer stampPage;

    @Value("${esigner.crypto.hashalgorithm.short}")
    private String hashAlgorithm;

    @Value("${esigner.stamp.position.x}")
    private float stampAbsX;
    @Value("${esigner.stamp.position.y}")
    private float stampAbsY;

    @Override
    public byte[] sign(byte[] pdf, String certHolder) {

        X509Certificate x509 = null;
        PrivateKey privateKey = null;

        logger.debug("getting data from keystore...");
        try {
          //  FileInputStream inputStream = new FileInputStream(keystore);
            KeyStore keyStore = keyStoreHelper.load(certHolder);

             java.security.cert.Certificate cert = keyStore.getCertificate(certHolder);
             x509 = (X509Certificate) cert;
             privateKey = (PrivateKey) keyStore.getKey(certHolder, keystorePassword.toCharArray());


        } catch (NoSuchFileException e){

            try {

                logger.warn("Сертификат пользователя {} не обнаружен. Берем корневик!", certHolder);

                KeyStore keyStore = keyStoreHelper.load(KeyStoreHelper.CA);

                java.security.cert.Certificate cert = keyStore.getCertificate(KeyStoreHelper.CA);
                x509 = (X509Certificate) cert;
                privateKey = (PrivateKey) keyStore.getKey(KeyStoreHelper.CA, keystorePassword.toCharArray());

            } catch (Exception ex){

                throw new EsignException("Error on load ROOT Cert! " + ex.getMessage());
            }

        }

        catch (FileNotFoundException e){
            logger.error("Key store {} not found!", keystore);
            throw new EsignException("Key store " + keystore + " not found!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            logger.error("NoSuchProvider {} ", cryptoprovider);
            throw new EsignException("Provider " + cryptoprovider + " not found!");
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new EsignException(e.getMessage());
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        logger.debug("Set stamp and sign...");

      try {
          PdfReader reader = new PdfReader(pdf);

          PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0');
          PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
          appearance.setCertificate(x509);

          logger.debug("set Text Layer at stamp...");
          appearance.setLayer2Text(stampText.getCertText(x509));

          logger.debug("setVisibleSignatureRotated...");
        //  setVisibleSignatureRotated(stamper, appearance, new Rectangle(stampLlx, stampLly, stampUrx, stampUry), stampPage, null);

          ExternalSignature externalSignature = new PrivateKeySignature(privateKey, hashAlgorithm, null);
          ExternalDigest externalDigest = new BouncyCastleDigest();

          logger.debug("Making signature...");
          MakeSignature.signDetached(appearance, externalDigest, externalSignature, new Certificate[]{x509}, null, null, null, 0, MakeSignature.CryptoStandard.CMS);


          os.flush();
          os.close();

      } catch (Exception e){
          logger.error(e.getMessage());
          throw new EsignException(e.getMessage());
      }
        logger.debug("Stamp & sign process finished");
        return os.toByteArray();
    }


    private void setVisibleSignatureRotated(PdfStamper stamper, PdfSignatureAppearance appearance, Rectangle pageRect, int page, String fieldName) throws DocumentException, IOException {
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
        textImg.setRotationDegrees(stampRotation);
        textImg.setAbsolutePosition(stampAbsX, stampAbsY);
        n2Layer.addImage(textImg);
    }


}

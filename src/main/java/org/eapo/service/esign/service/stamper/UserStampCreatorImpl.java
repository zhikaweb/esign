package org.eapo.service.esign.service.stamper;

import org.eapo.service.esign.exception.EsignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;

@Service
public class UserStampCreatorImpl implements UserStampCreator {

    private static Logger logger = LoggerFactory.getLogger(UserStampCreatorImpl.class.getName());


    @Value("${esigner.userstamp.file}")
    private String userStampFile;

    @Value("${esigner.userstamp.font}")
    private String userStampFont;

    @Value("${esigner.userstamp.font.size}")
    private Integer userStampFontSize;

    @Value("${esigner.userstamp.dateformat:dd.MM.yyyy}")
    private String dateFormat;

    @Override
    public byte[] build(X509Certificate certificate) {

        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);

        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(userStampFile));
            //  img =  ImageIO.read(new File(getClass().getClassLoader().getResource(userStampFile).getFile()));
        } catch (IOException e) {
            logger.error("Cant read stamp template {} : {}", userStampFile, e.getMessage());
            throw new EsignException("Cant read stamp template ", e);
        }

        Graphics2D gO = img.createGraphics();
        gO.setColor(Color.black);
        gO.setFont(new Font(userStampFont, Font.BOLD, userStampFontSize));

        gO.drawString(certificate.getSerialNumber().toString(),
                (int) (img.getWidth() / 2.7),
                (int) (img.getHeight() / 1.6));
        gO.drawString(certificate.getIssuerX500Principal().getName(),
                (int) (img.getWidth() / 2.7), (int) (img.getHeight() / 1.3));


        String validPeriod = dateFormatter.format(certificate.getNotBefore()) + "-" + dateFormatter.format(certificate.getNotAfter());

        gO.drawString(validPeriod,
                (int) (img.getWidth() / 2.7), (int) (img.getHeight() / 1.1));


        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ImageIO.write(img, "jpg", baos);
            baos.flush();
            baos.close();
        } catch (Exception e) {
            logger.error("Cant create  stamp  : {}", e.getMessage());
            throw new EsignException("Cant create stamp", e);
        }


        return baos.toByteArray();


    }
}

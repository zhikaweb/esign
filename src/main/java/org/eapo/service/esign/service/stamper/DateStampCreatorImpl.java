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

@Service
public class DateStampCreatorImpl implements DateStampCreator {

    private static Logger logger = LoggerFactory.getLogger(DateStampCreatorImpl.class.getName());

    @Value("${esigner.datastamp.file}")
    private String dataStampFile;

    @Value("${esigner.datastamp.font:SansSerif}")
    private String dataStampFont;

    @Value("${esigner.datastamp.font.size:12}")
    private Integer dataStampFontSize;

    @Override
    public byte[] build(String date) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(dataStampFile));
            //  img =  ImageIO.read(new File(getClass().getClassLoader().getResource(userStampFile).getFile()));
        } catch (IOException e) {
            logger.error("Cant read stamp template {} : {}", dataStampFile, e.getMessage());
            throw new EsignException("Cant read stamp template ", e);
        }

        Graphics2D gO = img.createGraphics();
        gO.setColor(Color.black);
        gO.setFont(new Font(dataStampFont, Font.BOLD, dataStampFontSize));

        gO.drawString("ОТПРАВЛЕНО",
                (int) (img.getWidth() / 20),
                (int) (img.getHeight() / 2));

        gO.drawString(date,
                (int) (img.getWidth() / 20),
                (int) (img.getHeight() / 1));


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

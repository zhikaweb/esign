package org.eapo.service.esign.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@Service
public class UserStampCreatorImpl implements UserStampCreator {

    @Value("${esigner.userstamp.file}")
    private String userStampFile;

    @Value("${esigner.userstamp.font}")
    private String userStampFont;

    @Value("${esigner.userstamp.font.size}")
    private Integer userStampFontSize;

    @Override
    public byte[] build(String user, String certNumber) throws IOException {
        BufferedImage img = ImageIO.read(new File(getClass().getClassLoader().getResource(userStampFile).getFile()));

        Graphics2D gO = img.createGraphics();
        gO.setColor(Color.black);
        gO.setFont(new Font(userStampFont, Font.BOLD, userStampFontSize));

        gO.drawString(certNumber, img.getWidth() / 3, (int) (img.getHeight() / 1.55));
        gO.drawString(user, img.getWidth() / 3, (int) (img.getHeight() / 1.3));


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", baos);
        baos.flush();

        return baos.toByteArray();


    }
}

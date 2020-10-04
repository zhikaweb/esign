package org.eapo.service.esign.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class StampBuilder {

    private String file, certNumber, certOwner, certExpireTo;


    public StampBuilder(String file) {
        this.file = file;
    }

    public StampBuilder setCertNumber(String certNumber) {
        this.certNumber = certNumber;
        return this;
    }

    public StampBuilder setCertOwner(String certOwner) {
        this.certOwner = certOwner;
        return this;
    }

    public StampBuilder setCertExpireTo(String certExpireTo) {
        this.certExpireTo = certExpireTo;
        return this;
    }

    public byte[] build() throws IOException {
        BufferedImage img = ImageIO.read(new File(getClass().getClassLoader().getResource(file).getFile()));

        Graphics2D gO = img.createGraphics();
        gO.setColor(Color.black);
        gO.setFont(new Font("SansSerif", Font.BOLD, 10));

        gO.drawString(this.certNumber, img.getWidth() / 3, (int) (img.getHeight() / 1.55));
        gO.drawString(this.certOwner, img.getWidth() / 3, (int) (img.getHeight() / 1.3));
        gO.drawString(this.certExpireTo, img.getWidth() / 3, (int) (img.getHeight() / 1.13));


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", baos);
        baos.flush();

        return baos.toByteArray();


    }
}

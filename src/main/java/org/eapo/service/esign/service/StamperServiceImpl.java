package org.eapo.service.esign.service;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import org.eapo.service.esign.exception.EsignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class StamperServiceImpl implements StamperService{

    private static Logger logger = LoggerFactory.getLogger(StamperServiceImpl.class.getName());


    @Value("${esigner.userstamp.position.height}")
    float stampPositionHeight;

    @Value("${esigner.userstamp.position.width}")
    Integer stampPositionWidth;

    @Autowired
    UserStampCreator userStampCreator;

    @Override
    public byte[] doStamp(byte[] pdf, String user) {

        logger.debug("Making stamp for user {}", user);

        byte[] stamp = userStampCreator.build(user, "");


        InputStream pdfStream = new ByteArrayInputStream(pdf);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfReader pdfReader = new PdfReader(pdfStream);

            PdfStamper pdfStamper = new PdfStamper(pdfReader, baos);

            PdfContentByte content = pdfStamper.getOverContent(pdfReader.getNumberOfPages());
            Image deliverImg = Image.getInstance(stamp);

            Rectangle r = pdfReader.getPageSize(pdfReader.getNumberOfPages());

            float width = stampPositionWidth + r.getWidth() - deliverImg.getWidth();
            float height = stampPositionHeight;

            deliverImg.setAbsolutePosition(width, height);

            logger.debug("Adding stamp image to pdf...");
            content.addImage(deliverImg);

            pdfStamper.close();
        } catch (Exception e) {
            logger.error("Cant create  stamp  : {}", e.getMessage());
            throw new EsignException("Cant create stamp",e);
        }

        logger.debug("Stamp processing finished!");
        return baos.toByteArray();


    }
}

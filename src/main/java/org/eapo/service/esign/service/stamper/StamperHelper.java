package org.eapo.service.esign.service.stamper;

import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import org.eapo.service.esign.exception.EsignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
public class StamperHelper {

    private static Logger logger = LoggerFactory.getLogger(StamperHelper.class.getName());

    public byte[] doStamp(byte[] pdf, byte[] stamp, float width, float height, int fPage, int lPage) {

        InputStream pdfStream = new ByteArrayInputStream(pdf);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfReader pdfReader = new PdfReader(pdfStream);

            PdfStamper pdfStamper = new PdfStamper(pdfReader, baos);

            //   PdfContentByte content = pdfStamper.getOverContent(pdfReader.getNumberOfPages());
            Image deliverImg = Image.getInstance(stamp);

            deliverImg.setAbsolutePosition(width, height);

            logger.debug("Adding stamp image...");
            PdfContentByte content;

            int lastPage = pdfReader.getNumberOfPages();
            if (lPage > 0) {
                lastPage = Math.min(lastPage, lPage);
            }

            logger.debug("Do stamp from page {} to page {}",fPage,lastPage);

            for (int page = fPage; page <= lastPage; page++) {
                logger.debug("Stamp at page {} ", page);
                content = pdfStamper.getOverContent(page);
                content.addImage(deliverImg);
            }

            pdfStamper.close();
        } catch (Exception e) {
            logger.error("Cant create  stamp  : {}", e.getMessage());
            throw new EsignException("Cant create stamp", e);
        }

        logger.debug("Stamp processing finished!");
        return baos.toByteArray();
    }

}

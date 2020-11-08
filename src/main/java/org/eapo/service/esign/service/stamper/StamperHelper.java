package org.eapo.service.esign.service.stamper;

import com.lowagie.text.DocumentException;
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
import java.util.List;

/**
 * ставит штамп stamp на файл pdf на позициях positions
 */

@Service
public class StamperHelper {

    private static Logger logger = LoggerFactory.getLogger(StamperHelper.class.getName());

    public byte[] doStamp(byte[] pdf, byte[] stamp, List<TextPositionFinder.Position> positions) {

        InputStream pdfStream = new ByteArrayInputStream(pdf);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfReader pdfReader = new PdfReader(pdfStream);

            PdfStamper pdfStamper = new PdfStamper(pdfReader, baos);

            //   PdfContentByte content = pdfStamper.getOverContent(pdfReader.getNumberOfPages());
            Image deliverImg = Image.getInstance(stamp);

            logger.debug("Adding stamp image...");
            PdfContentByte content;

            positions.forEach(position->{
                logger.debug("Stamp at page {} ", position.getPage());
                deliverImg.setAbsolutePosition(position.getX(), position.getY());
                try {
                    pdfStamper.getOverContent(position.getPage()).addImage(deliverImg);
                } catch (DocumentException e) {
                    logger.error("Error on stamp at page {}", position.getPage());
                    e.printStackTrace();
                }

            });

            pdfStamper.close();
        } catch (Exception e) {
            logger.error("Cant create  stamp  : {}", e.getMessage());
            throw new EsignException("Cant create stamp", e);
        }

        logger.debug("Stamp processing finished!");
        return baos.toByteArray();
    }

}

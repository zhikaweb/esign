package org.eapo.service.esign.service.converter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;

@Service
public class Converter2PdfServiceImpl implements Converter2PdfService {

    private static Logger logger = LoggerFactory.getLogger(Converter2PdfServiceImpl.class.getName());

    @Autowired
    Word2Pdf word2Pdf;

    @Override
    public byte[] convert(byte[] file) throws Exception {

        logger.debug("converting to pdf...");

        if (isPdf(file)) {
            logger.debug("file is pdf, skip converting");
            return file;
        }

        if (isDOCX(file)) {
            logger.debug("docx -> pdf converting");
            return word2Pdf.convert(file);
        }

        throw new IllegalArgumentException("Can't convert file to PDF!!!");

    }


    private boolean isPdf(byte[] bytes) throws IOException {
        String header = new String(Arrays.copyOfRange(bytes, 0, 4));
        logger.trace("header {}", header);
        return "%PDF".equalsIgnoreCase(header);
    }

    private boolean isDOCX(byte[] bytes) throws IOException {

        String header = new String(Arrays.copyOfRange(bytes, 0, 2));
        logger.trace("header {}", header);
        //  return true;
        return "PK".equalsIgnoreCase(header);
    }
}

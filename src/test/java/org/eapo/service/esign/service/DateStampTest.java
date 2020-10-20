package org.eapo.service.esign.service;

import com.lowagie.text.DocumentException;
import org.eapo.service.esign.service.stamper.DateStampService;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class DateStampTest {

    @Autowired
    DateStampService dateStampService;

    @Ignore
    @Test
    public void test() throws IOException, DocumentException {

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("document.pdf").getFile());
        byte[] pdf = Files.readAllBytes(file.toPath());

        byte[] res = dateStampService.doStamp(pdf, "2020.01.02");

        try (FileOutputStream fos = new FileOutputStream("C:\\res.pdf")) {
            fos.write(res);
        }
    }

}

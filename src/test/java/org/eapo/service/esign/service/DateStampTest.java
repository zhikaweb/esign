package org.eapo.service.esign.service;

import com.lowagie.text.DocumentException;
import org.eapo.service.esign.service.stamper.DateStampService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class DateStampTest {

    @Autowired
    DateStampService dateStampService;

    @Test
    public void test() throws IOException, DocumentException {

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("300_2.pdf").getFile());
        byte[] pdf = Files.readAllBytes(file.toPath());

        byte[] res = dateStampService.doStamp(pdf, "22.10.2020");

        try (FileOutputStream fos = new FileOutputStream("C:\\\\TEMP\\esigner\\res.pdf")) {
            fos.write(res);
        }
    }

}

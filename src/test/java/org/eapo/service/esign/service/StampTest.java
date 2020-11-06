package org.eapo.service.esign.service;

import com.lowagie.text.DocumentException;
import org.eapo.service.esign.EsignApplication;
import org.eapo.service.esign.service.stamper.StamperService;
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
public class StampTest {

    @Autowired
    StamperService stamperService;

    @Ignore
    @Test
    public void test() throws IOException, DocumentException {

        EsignApplication.addBouncyCastleAsSecurityProvider();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("document.pdf").getFile());

        byte[] pdf = Files.readAllBytes(file.toPath());

        byte[] res = stamperService.doStamp(pdf, "astal",1,1);

        try (FileOutputStream fos = new FileOutputStream("C:\\TEMP\\doc-signer\\res.pdf")) {
            fos.write(res);
        }
    }

}

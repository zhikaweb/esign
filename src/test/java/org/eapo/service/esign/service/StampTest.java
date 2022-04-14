package org.eapo.service.esign.service;

import com.lowagie.text.DocumentException;
import org.eapo.service.esign.EsignApplication;
import org.eapo.service.esign.service.stamper.StamperService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class StampTest {

    @Autowired
    StamperService stamperService;


    @Autowired
    SignerPdfService signerPdfService;

    @Value("${test.result.path.pdf}")
    private String resultPathPdf;

  //  @Ignore
    @Test
    public void test() throws Exception {

        EsignApplication.addBouncyCastleAsSecurityProvider();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("signature.pdf").getFile());

        byte[] pdf = Files.readAllBytes(file.toPath());

        List<String> certHolders = new ArrayList<>();
        certHolders.add("akondrat");
     //   certHolders.add("vputin");

        byte[] res = stamperService.doStamp(pdf, certHolders,1,1, "Patt");


        res =  signerPdfService.sign(res, certHolders);

        try (FileOutputStream fos = new FileOutputStream(resultPathPdf)) {
            fos.write(res);
        }
    }

}

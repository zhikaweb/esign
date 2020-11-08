package org.eapo.service.esign.service;

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
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Ignore
public class FlatTest {

    @Autowired
    PdfFlatter pdfFlatter;

    @Ignore
    @Test
    public void test() throws IOException {

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("signature.pdf").getFile());
        byte[] pdf_1 = Files.readAllBytes(file.toPath());

        file = new File(classLoader.getResource("sample.pdf").getFile());
        byte[] pdf_2 = Files.readAllBytes(file.toPath());


        List<byte[]> list = Arrays.asList(pdf_1, pdf_2);

        byte[] pdf = pdfFlatter.concat(list);


        try (FileOutputStream fos = new FileOutputStream("/home/astal/res.pdf")) {
            fos.write(pdf);
        }

    }
}

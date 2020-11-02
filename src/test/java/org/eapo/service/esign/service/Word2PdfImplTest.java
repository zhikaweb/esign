package org.eapo.service.esign.service;

import org.eapo.service.esign.service.converter.Word2PDfConverter;
import org.eapo.service.esign.service.converter.Word2PdfImpl;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class Word2PdfImplTest {

    @Ignore
    @Test
    public void convert() throws IOException {


        File src = new File("C:\\Users\\AStal\\12345.docx");
        byte[] pdf = new Word2PDfConverter().convert(Files.readAllBytes(src.toPath()));

        File file = new File("C:\\Users\\AStal\\res.pdf");

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(pdf);
        fileOutputStream.close();

        /*
        System.out.println(System.getProperty("java.library.path"));

        byte[] word = Files.readAllBytes(new File(getClass().getClassLoader().getResource("word4.doc").getFile()).toPath());

        byte[] res = new Word2PdfImpl().convert(word);
        File file = new File("C:\\Users\\AStal\\res.pdf");

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(res);
        fileOutputStream.close();
*/

    }
}
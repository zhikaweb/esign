package org.eapo.service.esign.service;

import org.eapo.service.esign.service.stamper.DateStampCreator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileOutputStream;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class DateStampCreatorTest {
    @Autowired
    DateStampCreator dateStampCreator;


    @Test
    public void test() throws IOException {
        byte[] bytes = dateStampCreator.build("2020-01-01");
        try (FileOutputStream fos = new FileOutputStream("C:\\img.png")) {
            fos.write(bytes);
        }
    }

}

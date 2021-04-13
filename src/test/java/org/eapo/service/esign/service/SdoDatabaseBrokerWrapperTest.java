package org.eapo.service.esign.service;

import org.eapo.service.esign.service.phoenix.DocLoadManagerWithPDF;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class SdoDatabaseBrokerWrapperTest {

   @Autowired
   DocLoadManagerWithPDF docLoadManagerWithPDF;


    @Test
    public void test() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        short type = 212;
        docLoadManagerWithPDF.load("201992249", type,
                "", sdf.parse("22-10-2019"),
                "EA001",
                "",
                "",
                false,
                "",
                "",
                "",
                "");

    }

}

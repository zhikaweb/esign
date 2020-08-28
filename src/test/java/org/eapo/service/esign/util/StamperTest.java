package org.eapo.service.esign.util;

import com.lowagie.text.DocumentException;
import org.junit.jupiter.api.Test;


import java.io.*;
import java.nio.file.Files;

class StamperTest {

    @Test
    void doStamp() throws IOException, DocumentException {


       byte[] pdf =  Files.readAllBytes(new File(getClass().getClassLoader().getResource ("document.pdf").getFile()).toPath());

       byte[] stamp =  new StampBuilder("stamp.png")
               .setCertNumber("342345456")
               .setCertOwner("Eurasian Patent Office")
               .setCertExpireTo("27-08-2020 / 27-08-2022")
               .build();    Files.readAllBytes(new File(getClass().getClassLoader().getResource ("stamp.png").getFile()).toPath());


      byte[] res =  new Stamper().doStamp(pdf, stamp);


      File file = new File("C:\\Users\\AStal\\res.pdf");

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(res);
        fileOutputStream.close();




    }
}
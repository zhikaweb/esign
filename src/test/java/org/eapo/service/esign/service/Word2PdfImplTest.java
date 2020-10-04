package org.eapo.service.esign.service;

import org.eapo.service.esign.service.converter.Word2PdfImpl;
import org.junit.Ignore;
import org.junit.Test;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;


class Word2PdfImplTest {

    @Ignore
    @Test
    void convert() throws IOException {


        // System.getProperty("java.library.path");


        // String filename = "C:\\Users\\AStal\\projects\\esign\\src\\main\\resources\\jacob.dll";
        System.out.println(System.getProperty("java.library.path"));

        //  new eapo.commons.word2pdf.Word2PDfConverter().convert(new File(getClass().getClassLoader().getResource ("word.doc").getFile()));


        byte[] word = Files.readAllBytes(new File(getClass().getClassLoader().getResource("word4.doc").getFile()).toPath());

        byte[] res = new Word2PdfImpl().convert(word);
        File file = new File("C:\\Users\\AStal\\res.pdf");

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(res);
        fileOutputStream.close();


    }
}
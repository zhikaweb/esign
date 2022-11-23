package org.eapo.service.esign.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.nio.file.Files;

@SpringBootTest
@AutoConfigureMockMvc
public class UploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void tooBigFileTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", Files.newInputStream(new File("src/test/resources/voina-i-mir.pdf").toPath()));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "POST");
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Origin, Accept, Authorization, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/document")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .headers(headers)
                        .content(file.getBytes())
                        .param("idappli", "test")
                        .param("odcorresp", "10")
                        .param("idletter", "EA000")
                        .param("certHolders", "AABBCCDD")
                        .param("fpage", "1")
                        .param("lpage", "10")
                        .param("save", "Store"))
                .andExpect(MockMvcResultMatchers.status().is(400));
    }

    // The following test fails with 500 status because some certificates/stamp templates are missing.
    // Postman test gives 200 with such file.
//    @Test
//    public void normalSizeFileTest() throws Exception {
//        MockMultipartFile file = new MockMultipartFile("fileEEE", Files.newInputStream(new File("src/test/resources/document.pdf").toPath()));
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
//        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "POST");
//        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Origin, Accept, Authorization, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .multipart("/document")
//                        .file("file", file.getBytes())
//                        .headers(headers)
//                        .param("file", "document")
//                        .param("idappli", "test")
//                        .param("odcorresp", "10")
//                        .param("idletter", "EA000")
//                        .param("certHolders", "AABBCCDD")
//                        .param("fpage", "1")
//                        .param("lpage", "10")
//                        .param("save", "Store"))
//                .andExpect(MockMvcResultMatchers.status().is(200));
//
//    }
}

package org.eapo.service.esign.service;

import org.eapo.phoenix.upload.service.Upload2PhoenixRequest;
import org.eapo.phoenix.upload.service.Upload2PhoenixResponse;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.ClassUtils;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WsPhoenixUploadTest {

    private Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

   // @LocalServerPort
    private int port = 8888;

    @BeforeEach
    public void init() throws Exception {
        marshaller.setPackagesToScan(ClassUtils.getPackageName(Upload2PhoenixRequest.class));
        marshaller.afterPropertiesSet();
    }

    @Ignore
    @Test
    public void testSendAndReceive() throws IOException {
        WebServiceTemplate ws = new WebServiceTemplate(marshaller);
        Upload2PhoenixRequest request = new Upload2PhoenixRequest();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("document.pdf").getFile());


        byte[] pdf = Files.readAllBytes(file.toPath());

        request.setFile(pdf);
        request.setDosier("200800011");
        request.setDoccode("DOCRU");




 //       assertThat((Upload2PhoenixResponse) ws.marshalSendAndReceive("http://192.168.7.148:"
 //               + port + "/ws", request) != null);
    }

}

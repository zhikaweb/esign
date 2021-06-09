package org.eapo.service.esign.service;


import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class UploadTest {


    String baseURL = "http://192.168.2.148:8888";
    String CHECK_FILE_ENDPOINT = "/check";

    @Test
    public void test(){

        String idappli = "X201619010";
        String odcorresp = "11";




        String url = baseURL.concat(CHECK_FILE_ENDPOINT)
                .concat("?idappli=")
                .concat(idappli)
                .concat("&odcorresp=")
                .concat(odcorresp);


        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {
            HttpGet request = new HttpGet(url);

            CloseableHttpResponse response = httpClient.execute(request);

            System.out.println(response.getStatusLine().getStatusCode());

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}

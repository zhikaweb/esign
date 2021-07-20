package org.eapo.service.esign.service;


import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eapo.service.esign.rest.PhoenixController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class UploadTest {


    private static Logger logger = LoggerFactory.getLogger(UploadTest.class.getName());


    String baseURL = "http://192.168.2.148:8888";
   // String CHECK_FILE_ENDPOINT = "/document/checkFile";
   private final String PHX_ENDPOINT = "/phoenix";


    @Test
    public void test(){

        String idappli = "X201619010";
        Integer odcorresp = 11;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        List<String> certHolders = Collections.singletonList("astal");
        sendRequest(idappli,odcorresp, "200800026", simpleDateFormat.format(new Date()), "EA001", certHolders);
    }


    public boolean sendRequest(String idappli, Integer odcorresp, String extidappli, String dtsend, String doccode, List<String> certHolders ){

        final String SEPARATOR = ";";

        StringBuilder csvBuilder = new StringBuilder();
        for(String cert : certHolders){
            csvBuilder.append(cert);
            csvBuilder.append(SEPARATOR);
        }

        String csv = csvBuilder.toString();
        System.out.println(csv);
        //OUTPUT: Milan,London,New York,San Francisco,

        //Remove last comma
        csv = csv.substring(0, csv.length() - SEPARATOR.length());


        String url = baseURL.concat(PHX_ENDPOINT)
                .concat("?idappli=")
                .concat(idappli)
                .concat("&odcorresp=")
                .concat(odcorresp.toString())
                .concat("&dosier=")
                .concat(extidappli)
                .concat("&dtsend=")
                .concat(dtsend)
                .concat("&doccode=")
                .concat(doccode)
                .concat("&certHolders=")
                .concat(csv);


        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {
            HttpGet request = new HttpGet(url);

            CloseableHttpResponse response = httpClient.execute(request);

            logger.info("Sending request for url {}", url);
            boolean isOK = HttpStatus.SC_OK == response.getStatusLine().getStatusCode();
            logger.info("Response is: {}", isOK);

            return isOK;

        } catch (Exception e){
            logger.error("exception on sending request:", e);
            return false;
        }
    }


}

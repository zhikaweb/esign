package org.eapo.service.esign;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static java.util.Arrays.asList;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class EsignApplicationTests {


    @Autowired
    private TestRestTemplate restTemplate;


    @Test
    public void contextLoader() {
        String fileName = "test.txt";
        byte[] fileContent = "this is file content".getBytes();

        HttpHeaders parts = new HttpHeaders();
        parts.setContentType(MediaType.TEXT_PLAIN);
        final ByteArrayResource byteArrayResource = new ByteArrayResource(fileContent) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };
        final HttpEntity<ByteArrayResource> partsEntity = new HttpEntity<>(byteArrayResource, parts);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.add("user-file", partsEntity);

        final ParameterizedTypeReference<String> typeReference = new ParameterizedTypeReference<String>() {
        };

        final ResponseEntity<String> exchange = restTemplate.exchange("http://localhost:8888/uploadFile", HttpMethod.POST, new HttpEntity<>(requestMap, headers), typeReference);
        if (exchange.getStatusCode().is2xxSuccessful()) {
            System.out.println("File uploaded = " + exchange.getBody());
        }
        //	assertThat(exchange.getStatusCode().is2xxSuccessful(), equalTo(true));
        //	assertThat(exchange.getBody().isSuccess(), equalTo(true));
    }



	/*
	@Test
	void contextLoads() {

		String filename = "aaa.docx";
		byte[] someByteArray = filename.getBytes();
		String url = "http://localhost:8888/uploadFile";


		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
	//	body.add("filename", "my.txt");
		body.add("file", new ByteArrayResource(someByteArray));

		HttpEntity<MultiValueMap<String, Object>> requestEntity =
				new HttpEntity<>(body, headers);
	//	try {

			ResponseEntity<String> response = restTemplate
					.postForEntity(url, requestEntity, String.class);

		System.out.println(response);

		/*
			ResponseEntity<String> response = restTemplate.exchange(
					url,
					HttpMethod.GET,
					requestEntity,
					String.class);

			System.out.println(response);

		} catch (HttpClientErrorException e) {
			e.printStackTrace();
		}

		 */


//	}

}

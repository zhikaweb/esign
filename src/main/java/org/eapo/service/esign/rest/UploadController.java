package org.eapo.service.esign.rest;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin
@RestController
public class UploadController {

    public static HttpHeaders getHeaders() {
        HttpHeaders header = addAccessControlAllowOrigin();
   //     header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        return header;
    }





    public static HttpHeaders addAccessControlAllowOrigin() {
        HttpHeaders headers = new HttpHeaders();
     //   headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, PUT, OPTIONS, DELETE");

        return headers;
    }

    @PostMapping("/uploadFile")
//    @RequestMapping(value="/uploadFile", produces = "application/json", method= RequestMethod.POST)
public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
//    public ResponseEntity<String> uploadFile() throws IOException {


        System.out.println("aaa");
       System.out.println(file.getBytes().length);

        return ResponseEntity.ok()
                .headers(getHeaders())
                .body("{\"ASDF\":1}");
}


    @RequestMapping(value="/upload", method= RequestMethod.GET)
    public ResponseEntity<String> upload() throws IOException {


        System.out.println("bbb");
      //  System.out.println(file.getBytes().length);

        return ResponseEntity.ok()
                .headers(getHeaders())
                .body("{\"ASDF\":1}");

    }

}

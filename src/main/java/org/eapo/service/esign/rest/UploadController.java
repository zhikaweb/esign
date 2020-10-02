package org.eapo.service.esign.rest;

import org.eapo.service.esign.service.SignerPdfImpl;
import org.eapo.service.esign.service.Word2PdfImpl;
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
public ResponseEntity<Resource> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {

       Word2PdfImpl word2Pdf = new Word2PdfImpl();
       byte[] pdf =  word2Pdf.convert(file.getBytes());
       byte[] signed =  new SignerPdfImpl().sign(pdf);
       return getResponse(getHeaders("signed.pdf"), signed);
}


    @RequestMapping(value="/upload", method= RequestMethod.GET)
    public ResponseEntity<String> upload() throws IOException {

        return ResponseEntity.ok()
                .headers(getHeaders())
                .body("{\"ASDF\":1}");

    }


    private ResponseEntity<Resource> getResponse(HttpHeaders header, byte[] res) {
        ByteArrayResource resource = new ByteArrayResource(res);
        return ResponseEntity.ok()
                .headers(header)
                .contentLength(res.length)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }


    public static HttpHeaders getHeaders(String filename) {
        HttpHeaders header = addAccessControlAllowOrigin();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        return header;
    }

}

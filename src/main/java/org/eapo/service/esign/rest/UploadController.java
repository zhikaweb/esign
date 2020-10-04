package org.eapo.service.esign.rest;

import org.eapo.service.esign.model.Document;
import org.eapo.service.esign.service.DocumentService;
import org.eapo.service.esign.service.SignerPdfService;
import org.eapo.service.esign.service.StamperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@RestController
public class UploadController {

    @Autowired
    DocumentService documentService;

    @Autowired
    SignerPdfService signerPdfService;

    @Autowired
    StamperService stamperService;


    private static HttpHeaders addAccessControlAllowOrigin() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, PUT, OPTIONS, DELETE");
        return headers;
    }

   // @PostMapping("/uploadFile")
   @RequestMapping(method = RequestMethod.POST, value = "/uploadFile", produces = MediaType.APPLICATION_PDF_VALUE)
   public ResponseEntity<Resource> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("idappli") String idappli, @RequestParam("odcorresp") Integer odcorresp, String signer) throws Exception {

        byte[] stamped = stamperService.doStamp(file.getBytes(),signer);

        byte[] signed = signerPdfService.sign(stamped);

        Document document = new Document(idappli, odcorresp, signed);
        documentService.save(document);
        return getResponse(getHeaders(document.getId() + ".pdf"), document.getBody());
    }

    @GetMapping("/downloadFile")
    public ResponseEntity<Resource> downloadFile(@RequestParam("idappli") String idappli, @RequestParam("odcorresp") Integer odcorresp) throws Exception {

        Document document = documentService.get(Document.createId(idappli, odcorresp));
        return getResponse(getHeaders(document.getId() + ".pdf"), document.getBody());
    }


    private ResponseEntity<Resource> getResponse(HttpHeaders header, byte[] res) {
        ByteArrayResource resource = new ByteArrayResource(res);
        return ResponseEntity.ok()
                .headers(header)
                .contentLength(res.length)
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(resource);
    }


    private static HttpHeaders getHeaders(String filename) {
        HttpHeaders header = addAccessControlAllowOrigin();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        header.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);
        return header;
    }

}

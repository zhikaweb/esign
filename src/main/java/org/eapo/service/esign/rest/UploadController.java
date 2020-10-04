package org.eapo.service.esign.rest;

import org.eapo.service.esign.model.Document;
import org.eapo.service.esign.service.UploadService;
import org.eapo.service.esign.util.HTTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@RestController
public class UploadController {

    private static Logger logger = LoggerFactory.getLogger(UploadController.class.getName());
    private static final String RESPONSE_FILE_EXT = ".pdf";

    @Autowired
    UploadService uploadService;


   @RequestMapping(method = RequestMethod.POST, value = "/uploadFile", produces = MediaType.APPLICATION_PDF_VALUE)
   public ResponseEntity<Resource> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("idappli") String idappli, @RequestParam("odcorresp") Integer odcorresp, String signer) throws Exception {

        logger.info("Uploading file idappli = {}, odcorresp = {}, signer = {}, file size = {}", idappli, odcorresp, signer, file.getBytes());
        Document document = uploadService.uploadFile(file,idappli,odcorresp,signer);
        String fileName = document.getId() + RESPONSE_FILE_EXT;
        logger.debug("Sending response file {}", fileName);
        return getResponse(HTTPUtil.getHeaders(fileName), document.getBody());
    }

    @GetMapping("/downloadFile")
    public ResponseEntity downloadFile(@RequestParam("idappli") String idappli, @RequestParam("odcorresp") Integer odcorresp) throws Exception {

        logger.info("Downloading document by idappli = {}, odcorresp = {}", idappli, odcorresp);
        Document document = uploadService.downloadFile(idappli,odcorresp);

        if (document==null){
            logger.warn("Document by idappli = {}, odcorresp = {} not found!", idappli, odcorresp);
            return ResponseEntity.notFound().allow(HttpMethod.POST).build();
        }

        String fileName = document.getId() + RESPONSE_FILE_EXT;
        logger.debug("Sending response file {}", fileName);
        return getResponse(HTTPUtil.getHeaders(fileName), document.getBody());
    }




    private ResponseEntity<Resource> getResponse(HttpHeaders header, byte[] res) {
        ByteArrayResource resource = new ByteArrayResource(res);
        return ResponseEntity.ok()
                .headers(header)
                .contentLength(res.length)
                .contentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE))
                .body(resource);
    }





}

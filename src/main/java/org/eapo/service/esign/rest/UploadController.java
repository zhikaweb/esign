package org.eapo.service.esign.rest;

import org.eapo.service.esign.model.Document;
import org.eapo.service.esign.service.UploadService;
import org.eapo.service.esign.util.HTTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CrossOrigin
@RestController()
@RequestMapping("/document")
public class UploadController {

    private static Logger logger = LoggerFactory.getLogger(UploadController.class.getName());
    private static final String RESPONSE_FILE_EXT = ".pdf";

    @Autowired
    UploadService uploadService;


    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<Resource> uploadFile(@RequestParam("file") MultipartFile file,
                                               @RequestParam("idappli") String idappli,
                                               @RequestParam("odcorresp") Integer odcorresp,
                                               @RequestParam("signer") String signer,
                                               @RequestParam("certHolder") String certHolder,
                                               @RequestParam(value = "save", defaultValue = "true") String saveToStore) throws Exception {

        logger.info("Uploading file idappli = {}, odcorresp = {}, signer = {}, file size = {}", idappli, odcorresp, signer, file.getBytes());
        Document document = uploadService.uploadFile(file, idappli, odcorresp, signer, certHolder, saveToStore);
        String fileName = document.getId() + RESPONSE_FILE_EXT;
        logger.debug("Sending response file {}", fileName);
        return getResponse(HTTPUtil.getHeaders(fileName), document.getBody());
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity downloadFile(@RequestParam("idappli") String idappli, @RequestParam("odcorresp") Integer odcorresp) throws Exception {

        logger.info("Downloading document by idappli = {}, odcorresp = {}", idappli, odcorresp);
        Document document = uploadService.downloadFile(idappli, odcorresp);

        if (document == null) {
            logger.warn("Document by idappli = {}, odcorresp = {} not found!", idappli, odcorresp);
            return ResponseEntity.notFound().allow(HttpMethod.GET).build();
        }

        String fileName = document.getId() + RESPONSE_FILE_EXT;
        logger.debug("Sending response file {}", fileName);
        return getResponse(HTTPUtil.getHeaders(fileName), document.getBody());
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity deleteFile(@RequestParam("idappli") String idappli, @RequestParam("odcorresp") Integer odcorresp) throws Exception {

        logger.info("Deleting document by idappli = {}, odcorresp = {}", idappli, odcorresp);
        Long id = uploadService.deleteFile(idappli, odcorresp);

        if (id == null) {
            logger.warn("Document by idappli = {}, odcorresp = {} not found!", idappli, odcorresp);
            return ResponseEntity.notFound().allow(HttpMethod.DELETE).build();
        }

        logger.info("Document by idappli = {}, odcorresp = {} removed!");
        return ResponseEntity.ok().allow(HttpMethod.DELETE).build();
    }


    @RequestMapping(value = "/filterexists", method = RequestMethod.POST)
    public ResponseEntity<List<Document>> uploadFile(@RequestBody List<Document> documents){

        List<Document> storedDocs = new ArrayList<>();

        documents.forEach(doc->{
           if (uploadService.isExists(doc)){
               storedDocs.add(doc);
           }

        });
        return ResponseEntity.ok().body(storedDocs);
    };


        private ResponseEntity<Resource> getResponse(HttpHeaders header, byte[] res) {
        Resource resource = new ByteArrayResource(res);
        return ResponseEntity.ok()
                .headers(header)
                .contentLength(res.length)
                .contentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE))
                .body(resource);
    }


}

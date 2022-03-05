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

import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin
@RestController()
@RequestMapping("/document")
public class UploadController {

    public static final String CERTHOLDERS_DELIMETR = ";";
    private static final String RESPONSE_JOIN_FILENAME = "united";
    private static Logger logger = LoggerFactory.getLogger(UploadController.class.getName());
    private static final String RESPONSE_FILE_EXT = ".pdf";

    @Autowired
    UploadService uploadService;


    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<Resource> uploadFile(@RequestParam("file") MultipartFile file,
                                               @RequestParam("idappli") String idappli,
                                               @RequestParam("odcorresp") Integer odcorresp,
                                               @RequestParam("idletter") String idletter,
                                               @RequestParam("certHolders") String certHolders,
                                               @RequestParam(value = "fpage", defaultValue = "1") Integer fpage,
                                               @RequestParam(value = "lpage", defaultValue = "-1") Integer lpage,
                                               @RequestParam(value = "save", defaultValue = "true") String saveToStore) throws Exception {


        List<String> certHoldersList = Arrays.asList(certHolders.split(CERTHOLDERS_DELIMETR));
        logger.info("Uploading file idappli = {}, odcorresp = {}, file size = {}", idappli, odcorresp, file.getBytes().length);
        Document document = uploadService.uploadFile(file, idappli, odcorresp, certHoldersList, saveToStore, fpage, lpage, idletter);
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


    @RequestMapping(value = "/checkFile", method = RequestMethod.GET)
    public ResponseEntity checkFile(@RequestParam("idappli") String idappli, @RequestParam("odcorresp") Integer odcorresp) throws Exception {

        logger.info("Downloading document by idappli = {}, odcorresp = {}", idappli, odcorresp);
        Document document = new Document(idappli, odcorresp, null);

        if (!uploadService.isExists(document)){
            return ResponseEntity.notFound().allow(HttpMethod.GET).build();
        };

        return ResponseEntity.ok().allow(HttpMethod.GET).build();
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
    }

    @RequestMapping(value = "flat", method = RequestMethod.POST, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity flatList(@RequestBody List<Document> documents){
        logger.info("Downloading {} documents as one pdf ", documents.size());
        byte[] united = uploadService.flat(documents);
        String fileName = getUnitedName();
        logger.debug("Sending response file {}", fileName);
        return getResponse(HTTPUtil.getHeaders(fileName), united);
    }


    private ResponseEntity<Resource> getResponse(HttpHeaders header, byte[] res) {
        Resource resource = new ByteArrayResource(res);
        return ResponseEntity.ok()
                .headers(header)
                .contentLength(res.length)
                .contentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE))
                .body(resource);
    }

    private String getUnitedName() {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return RESPONSE_JOIN_FILENAME.concat("(").concat(date).concat(")").concat(RESPONSE_FILE_EXT);
    }


}

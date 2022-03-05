package org.eapo.service.esign.rest;


import org.eapo.service.esign.model.Document;
import org.eapo.service.esign.service.phoenix.PhoenixService;
import org.eapo.service.esign.service.stamper.DateStampService;
import org.eapo.service.esign.service.store.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.eapo.service.esign.rest.UploadController.CERTHOLDERS_DELIMETR;

@CrossOrigin
@RestController()
@RequestMapping("/phoenix")
public class PhoenixController {

    private static Logger logger = LoggerFactory.getLogger(PhoenixController.class.getName());

    @Autowired
    PhoenixService phoenixService;


    @Autowired
    DateStampService dateStampService;

    @Autowired
    DocumentService documentService;

    @Value("${esigner.userstamp.dateformat:dd.MM.yyyy}")
    private String dateFormat;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity signAndUpload(@RequestParam("dosier") String dosier,
                                           @RequestParam("idappli") String idappli,
                                           @RequestParam("odcorresp") Integer odcorresp,
                                           @RequestParam(value = "dtsend", required = false) @DateTimeFormat(pattern="dd.MM.yyyy") Date dtsend,
                                           @RequestParam(value = "doccode", defaultValue = "DOCRU") String doccode,
                                           @RequestParam(value = "certHolders", required = false) String certHolders,
                                           @RequestParam(value = "doSavePDF", defaultValue = "true") boolean doSavePDF

                                        ) throws Exception {


        List<String> certHoldersList = null;
        if (certHolders!=null){
           certHoldersList = Arrays.asList(certHolders.split(CERTHOLDERS_DELIMETR));
        } else {
             certHoldersList = Collections.emptyList();
        }

        logger.info("Saving document with idappli {} odcorresp {} and doccode {} to dosier {}", idappli, odcorresp, doccode, dosier);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

        Document document = documentService.get(Document.createId(idappli, odcorresp));
        if (document == null) {
            logger.error("Document with idappli {} and odcorresp {} not found", idappli, odcorresp);
            return ResponseEntity.notFound().build();
        }
        try {

            byte[] res = document.getBody();
            if (dtsend!=null) {
                res = dateStampService.doStamp(document.getBody(), simpleDateFormat.format(dtsend), doccode);
            }

            Date date =  dtsend!=null?dtsend:new Date();

            phoenixService.signAndUpload(dosier, certHoldersList, res, doccode, date,doSavePDF);

        } catch (Exception e) {
            logger.error("Error on saving document with idappli {} and odcorresp {} to phoenix : {}", idappli, odcorresp, e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(500).body(e.getMessage());//.build();
        }
        return ResponseEntity.ok().build();
    }


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                               @RequestParam("dosier") String dosier,
                                               @RequestParam(value = "doccode", defaultValue = "DOCRU") String doccode,
                                               @RequestParam(value = "dtsend", required = false) @DateTimeFormat(pattern="dd.MM.yyyy") Date dtsend,
                                               @RequestParam(value = "doSavePDF", defaultValue = "true") boolean doSavePDF
                                       ) throws Exception {

        logger.info("Uploading document to dosier {} and doccode {}", dosier, doccode);

        Date date =  dtsend!=null?dtsend:new Date();

        try {
            byte[] document = file.getBytes();
            logger.info("Document size is {}", document.length);
            phoenixService.upload(dosier, document, doccode, date, doSavePDF);
        } catch (Exception e) {
            logger.error("Error on uploading document to dosier {} and doccode {}", dosier, doccode);
            e.printStackTrace();

            return ResponseEntity.status(500).body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

}

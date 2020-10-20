package org.eapo.service.esign.rest;


import org.eapo.service.esign.model.Document;
import org.eapo.service.esign.service.phoenix.PhoenixService;
import org.eapo.service.esign.service.stamper.DateStampService;
import org.eapo.service.esign.service.store.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Resource> upload(@RequestParam("dosier") String dosier,
                                           @RequestParam("idappli") String idappli,
                                           @RequestParam("odcorresp") Integer odcorresp,
                                           @RequestParam(value = "dtsend", defaultValue = "") String dtsend,
                                           @RequestParam(value = "doccode", defaultValue = "DOCRU") String doccode) throws Exception {


        logger.info("Saving document with idappli {} odcorresp {} and doccode {} to dosier {}", idappli, odcorresp, doccode, dosier);

        Document document = documentService.get(Document.createId(idappli, odcorresp));
        if (document == null) {
            logger.error("Document with idappli {} and odcorresp {} not found", idappli, odcorresp);
            return ResponseEntity.notFound().build();
        }
        try {

            byte[] res = document.getBody();
            if (!"".equals(dtsend)) {
                res = dateStampService.doStamp(document.getBody(), dtsend);
            }
            phoenixService.upload(dosier, res, doccode);

        } catch (Exception e) {
            logger.error("Error on saving document with idappli {} and odcorresp {} to phoenix : {}", idappli, odcorresp, e.getMessage());
            e.printStackTrace();
        }


        return ResponseEntity.ok().build();

    }
}

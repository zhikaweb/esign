package org.eapo.service.esign.service;


import org.eapo.service.esign.exception.EsignException;
import org.eapo.service.esign.model.Document;
import org.eapo.service.esign.service.converter.Converter2PdfService;
import org.eapo.service.esign.service.store.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UploadServiceImpl implements UploadService{

    private static Logger logger = LoggerFactory.getLogger(UploadServiceImpl.class.getName());


    @Autowired
    DocumentService documentService;

    @Autowired
    SignerPdfService signerPdfService;

    @Autowired
    StamperService stamperService;

    @Autowired
    Converter2PdfService converter2PdflService;

    @Override
    public Document uploadFile(MultipartFile file, String idappli, Integer odcorresp, String signer)  {

        byte[] pdf = null;

        try {
            logger.debug("Converting to pdf...");
            pdf = converter2PdflService.convert(file.getBytes());
        } catch (Exception e){

            logger.error("error converting to pdf!");
            throw  new EsignException("error converting to pdf!",e);
        }

        byte[] stamped = null;

        try {
            logger.debug("Set stamp to file for signer {} ...", signer);
            stamped = stamperService.doStamp(pdf, signer);
        } catch (Exception e){
            logger.error("error setting user stamp!");
            throw  new EsignException("error setting user stamp!",e);
        }


        byte[] signed = null;
        try {
            logger.debug("Adding e-signature...");
            signed = signerPdfService.sign(stamped);

        } catch (Exception e){
            logger.error("error sign process!");
            throw  new EsignException("error sign process!",e);
        }

        logger.debug("Saving to document store...");
        Document document = new Document(idappli, odcorresp, signed);
        documentService.save(document);

        logger.debug("Sign & save process finished");
        return document;


    }

    @Override
    public Document downloadFile(String idappli, Integer odcorresp)  {
        return documentService.get(Document.createId(idappli, odcorresp));
    }

    @Override
    public Long deleteFile(String idappli, Integer odcorresp) {
        return documentService.delete(Document.createId(idappli, odcorresp));
    }
}

package org.eapo.service.esign.service;


import org.eapo.service.esign.model.Document;
import org.eapo.service.esign.service.converter.Converter2PdfService;
import org.eapo.service.esign.service.store.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    public Document uploadFile(MultipartFile file, String idappli, Integer odcorresp, String signer) throws Exception {

        logger.debug("Converting to pdf...");
        byte[] pdf = converter2PdflService.convert(file.getBytes());

        logger.debug("Set stamp to file for signer {} ...", signer);
        byte[] stamped = stamperService.doStamp(pdf,signer);

        logger.debug("Adding e-signature...");
        byte[] signed = signerPdfService.sign(stamped);

        logger.debug("Saving to document store...");
        Document document = new Document(idappli, odcorresp, signed);
        documentService.save(document);

        logger.debug("Sign & save process finished");
        return document;


    }

    @Override
    public Document downloadFile(String idappli, Integer odcorresp) throws Exception {
        return documentService.get(Document.createId(idappli, odcorresp));
    }
}

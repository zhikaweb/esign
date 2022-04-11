package org.eapo.service.esign.service;


import com.lowagie.text.DocumentException;
import org.eapo.service.esign.exception.EsignException;
import org.eapo.service.esign.model.Document;
import org.eapo.service.esign.service.converter.Converter2PdfService;
import org.eapo.service.esign.service.stamper.StamperService;
import org.eapo.service.esign.service.store.DocumentService;
import org.eapo.service.esign.util.DoccodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UploadServiceImpl implements UploadService {

    private static Logger logger = LoggerFactory.getLogger(UploadServiceImpl.class.getName());


    @Autowired
    DocumentService documentService;

    @Autowired
    StamperService stamperService;

    @Autowired
    Converter2PdfService converter2PdflService;

    @Autowired
    PdfFlatter pdfFlatter;

    @Override
    public Document uploadFile(MultipartFile file,
                               String idappli,
                               Integer odcorresp,
                               List<String> certHolders,
                               String saveToStore,
                               Integer fpage,
                               Integer lpage,
                               String idletter) {

        byte[] pdf;

        try {
            logger.debug("Converting to pdf...");
            pdf = converter2PdflService.convert(file.getBytes());
        } catch (Exception e) {

            logger.error("error converting to pdf : {}", e.getMessage()) ;
            e.printStackTrace();

            throw new EsignException("error converting to pdf!", e);
        }

        Document document;
        DoccodeUtil doccodeUtil = new DoccodeUtil();

        if (!doccodeUtil.isDoccodeExists(idletter)) {
            byte[] stamped;

            try {
                stamped = stamperService.doStamp(pdf, certHolders, fpage, lpage);
            } catch (Exception e) {
                logger.error("error setting user stamp!");
                throw new EsignException("error setting user stamp!", e);
            }
            document = new Document(idappli, odcorresp, stamped);
        } else {
            document = new Document(idappli, odcorresp, pdf);
        }

        if (Boolean.TRUE.toString().equalsIgnoreCase(saveToStore)) {
            logger.debug("Saving to document store...");
            documentService.save(document);
        } else {
            logger.info("Skip saving document to store...");
        }

        logger.debug("Sign & save process finished");
        return document;
    }

    @Override
    public Document downloadFile(String idappli, Integer odcorresp) {
        return documentService.get(Document.createId(idappli, odcorresp));
    }

    @Override
    public Long deleteFile(String idappli, Integer odcorresp) {
        return documentService.delete(Document.createId(idappli, odcorresp));
    }

    @Override
    public boolean isExists(Document doc) {
        return documentService.isExists(doc);
    }

    @Override
    public byte[] flat(List<Document> documents) {

        List<byte[]> pdfs = new ArrayList<>();

        documents.stream()
                .map(document -> documentService.get(Document.createId(document.getIdappli(), document.getOdcorresp())).getBody())
                .filter(p->p.length>0).forEach(pdfs::add);

       return pdfFlatter.concat(pdfs);
    }
}

package org.eapo.service.esign.service.phoenix;

import org.eapo.service.esign.exception.EsignException;
import org.eapo.service.esign.service.SignerPdfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;

@Service
public class PhoenixServiceImpl implements PhoenixService {

    private static Logger logger = LoggerFactory.getLogger(PhoenixServiceImpl.class.getName());

  //  @Autowired
  //  DocLoadManagerImpl docLoadManager;


    @Autowired
    SignerPdfService signerPdfService;


    @Override
    public void upload(String dossier, byte[] pdf, String doccode, Date date) throws Exception {

        logger.info("uploading!");

        DocLoadManager docLoadManager = new DocLoadManagerImpl();

        short type = 212;
        String annotation = "";

        java.sql.Date sDate = date==null? new java.sql.Date(System.currentTimeMillis()):new java.sql.Date(date.getTime()) ;



        // String docSource = "C:\\desc_amnd.pdf";

        File f = Files.createTempFile(dossier + "_" + System.currentTimeMillis(), ".pdf").toFile();

        logger.info("tmp file {}", f.getAbsolutePath());

        FileOutputStream fileOutputStream = new FileOutputStream(f);
        fileOutputStream.write(pdf);
        fileOutputStream.close();


        String procedure = "";
        boolean isSendMsg = false;
        String sendMsgToUser = "";
        String sendMsgToTeam = "";
        String textMailBox = "";
        String historyStr = "";
        String filter = ".pdf";
        File[] files = {new File(f.getAbsolutePath())};
        docLoadManager.addFromLocation(files, filter);


        logger.info("uploading file to dosier = {}, type = {}, annotation = {}, aDate = {}, doccode = {}, path = {}, procedure = {}, isSendMsg = {}, sendMsgToUser = {}, sendMsgToTeam = {}, textMailBox = {}, historyStr = {} ", dossier, type, annotation, sDate, doccode, f.getAbsolutePath(), procedure, isSendMsg, sendMsgToUser, sendMsgToTeam, textMailBox, historyStr);

        docLoadManager.load(dossier, type, annotation, sDate, doccode, f.getAbsolutePath(), procedure, isSendMsg, sendMsgToUser, sendMsgToTeam, textMailBox, historyStr);

        logger.info("file {} was uploaded!", f.getAbsolutePath());

    }

    @Override
    public void signAndUpload(String dossier, List<String> certHolders, byte[] pdf, String doccode, Date date) throws Exception {

        byte[] signed;

        try {
            logger.debug("Adding e-signature...");
            signed = signerPdfService.sign(pdf, certHolders);

        } catch (Exception e) {
            logger.error("error sign process!");
            throw new EsignException("error sign process!", e);
        }
        upload(dossier, signed, doccode, date);

    }

}

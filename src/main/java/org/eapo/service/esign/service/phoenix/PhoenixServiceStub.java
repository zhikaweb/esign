package org.eapo.service.esign.service.phoenix;

import org.epo.utils.EPODate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Date;

@Service
public class PhoenixServiceStub implements PhoenixService {

    private static Logger logger = LoggerFactory.getLogger(PhoenixServiceStub.class.getName());
    @Override
    public void upload(String dossier, byte[] pdf, String doccode, Date date) throws Exception {

        logger.info("uploading!");

        DocLoadManager docLoadManager = new DocLoadManager();

        short type = 212;
        String annotation = "";
        java.sql.Date sDate = new java.sql.Date(date.getTime());
        EPODate aDate = new EPODate(sDate);

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


        logger.info("uploading file to dosier = {}, type = {}, annotation = {}, aDate = {}, doccode = {}, path = {}, procedure = {}, isSendMsg = {}, sendMsgToUser = {}, sendMsgToTeam = {}, textMailBox = {}, historyStr = {} ", dossier, type, annotation, aDate, doccode, f.getAbsolutePath(), procedure, isSendMsg, sendMsgToUser, sendMsgToTeam, textMailBox, historyStr);

        docLoadManager.load(dossier, type, annotation, aDate, doccode, f.getAbsolutePath(), procedure, isSendMsg, sendMsgToUser, sendMsgToTeam, textMailBox, historyStr);

        logger.info("file {} was uploaded!", f.getAbsolutePath());

    }
}

package org.eapo.service.esign.service.phoenix;

import madras.database.SdoDatabaseBroker;
import madras.model.SignedDocument;
import madras.model.SignedFile;
import org.apache.commons.io.IOUtils;
import org.eapo.service.esign.exception.EsignException;
import org.eapo.service.esign.service.SignerPdfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class PhoenixServiceImpl implements PhoenixService {

    private static Logger logger = LoggerFactory.getLogger(PhoenixServiceImpl.class.getName());

  //  @Autowired
  //  DocLoadManagerImpl docLoadManager;


    @Autowired
    SignerPdfService signerPdfService;


    private final String  selectDocInfoSql = " SELECT d.DOCKEY, p.PCKKEY, p.PCKPXI, d.DOCPAGES, d.DOCPCKOFFSET " +
            " FROM tph014_document d, tph035_package p, tph013_docctl dct " +
            " WHERE dct.dctkey=d.docdctkey and d.docpckkey=p.pckkey " +
            " and p.PCKDOSKEY = (SELECT DOSKEY FROM tph019_dossier WHERE DOSORINUMBER = ?) " +
            " and d.DOCFLAGDEL = 0 and p.PCKDATEFORMAL = ? and dct.DCTCODE = ? ";


    @Autowired
    MadrasDatabaseBroker madrasDatabaseBroker;// = new MadrasDatabaseBroker();

    @Autowired
    private SdoDatabaseBroker sdo;// = new SdoDatabaseBroker();

    private SimpleDateFormat signedDocDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void upload(String dossier, byte[] pdf, String doccode, Date date, boolean doSavePDF) throws Exception {

        logger.info("uploading!");

        DocLoadManager docLoadManager = new DocLoadManagerImpl();

        short type = 212;
        String annotation = "";

        java.sql.Date sDate = date==null? new java.sql.Date(System.currentTimeMillis()):new java.sql.Date(date.getTime()) ;

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


        if (doSavePDF) {
            logger.info("getting docInfoList :dossier: {}  date: {}, doccode: {}", dossier, date, doccode);

            List<Object[]> docInfoList = readTiffDocInfo(dossier, date, doccode);



            if (docInfoList == null) {
                logger.error(" docInfoList - document not found");
                return;
            }
            if (docInfoList.size() > 1) {
                logger.error(" docInfoList - more than one document! ");
                return;
            }

            String docKey = (String) docInfoList.get(0)[0];
            logger.info("docKey: " + docKey);

            try {
                uploadPDF(docKey, doccode, date, dossier, pdf);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Error at upload PDF process..." + e.getMessage());
                throw e;
            }
        }


    }

    // По необяснимой причине свежесохранный tiff не всегда виден сразу поэтому вставляем такой жуткий костыль

    private List<Object[]> readTiffDocInfo(String dossier, Date date, String doccode) throws Exception {

        for (int i = 1; i < 11; i++) {
        MadrasDatabaseBroker mdb = new MadrasDatabaseBroker();
        List<Object[]> docInfoList = mdb.getValuesList(selectDocInfoSql, new Object[]{dossier, date, doccode});
        logger.info("getting docInfoList attempt {}", i);

        if (docInfoList.size() > 0) {
            return docInfoList;
        }
        Thread.sleep(300);
    }

        return null;
    }

    @Override
    public void signAndUpload(String dossier, List<String> certHolders, byte[] pdf, String doccode, Date date, boolean doSavePDF) throws Exception {

        byte[] signed;

        try {
            logger.debug("Adding e-signature...");
            signed = signerPdfService.sign(pdf, certHolders);

        } catch (Exception e) {
            logger.error("error sign process!");
            throw new EsignException("error sign process!", e);
        }
        upload(dossier, signed, doccode, date, doSavePDF);

    }

    private void uploadPDF(String docKey, String madrasCode, Date docDate, String appNum, byte[] pdf) throws Exception {
        // DMS
        SignedDocument signedDoc = new SignedDocument();
        signedDoc.setId(docKey);
        signedDoc.setDocCode(madrasCode);
        signedDoc.setDocDate(signedDocDateFormat.format(docDate));
        signedDoc.setDocKey(docKey);
        signedDoc.setDosNumber(appNum);
        signedDoc.setSigningDate(new Date());
        // TODO открывать транзакцию
        madrasDatabaseBroker.insert(signedDoc);

        // SDO
        SignedFile signedFile = new SignedFile();
        signedFile.setId(docKey);
        signedFile.setOffset(0);
        signedFile.setDataSize((long)pdf.length);
        signedFile.setFileData(pdf);
        sdo.insert(signedFile);
        // TODO закрывать транзакцию

    }


}

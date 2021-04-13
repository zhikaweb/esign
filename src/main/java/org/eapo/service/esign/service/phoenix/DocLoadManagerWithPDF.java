package org.eapo.service.esign.service.phoenix;
/*
import eapo.corresp.sign.MadrasDatabaseBroker;
import gryphon.date.DateUtil;
import madras.model.Document;

import org.springframework.beans.factory.annotation.Autowired;
 */
import madras.model.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.List;


@Service
public class DocLoadManagerWithPDF implements DocLoadManager{

    private final String  selectDocInfoSql = " SELECT d.DOCKEY, p.PCKKEY, p.PCKPXI, d.DOCPAGES, d.DOCPCKOFFSET " +
    " FROM tph014_document d, tph035_package p, tph013_docctl dct " +
            " WHERE dct.dctkey=d.docdctkey and d.docpckkey=p.pckkey " +
            " and p.PCKDOSKEY = (SELECT DOSKEY FROM tph019_dossier WHERE DOSORINUMBER = ?) " +
            " and d.DOCFLAGDEL = 0 and p.PCKDATEFORMAL = ? and dct.DCTCODE = ? ";


   @Autowired
    MadrasDatabaseBroker madrasDatabaseBroker;// = new MadrasDatabaseBroker();

    @Override
    public void load(String dossier, short type, String annotation, Date aDate, String doccode, String docSource, String procedure, boolean isSendMsg, String sendMsgToUser, String sendMsgToTeam, String textMailBox, String historyStr) throws Exception {

        List<Object[]> docInfoList = madrasDatabaseBroker.getValuesList(selectDocInfoSql, new Object[]{dossier, aDate, doccode});

        if (docInfoList.size()==0){

            return;
        }
        if (docInfoList.size()>1){
            return;
        }

        String docKey = (String) docInfoList.get(0)[0];

        Document document = madrasDatabaseBroker.select1(Document.class, docKey);

    }

    @Override
    public void addFromLocation(File[] files, String filter) {

    }
}

package org.eapo.service.esign.service.phoenix;

import org.epo.utils.EPODate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class PhoenixServiceStub  implements PhoenixService{

    private static Logger logger = LoggerFactory.getLogger(PhoenixServiceStub.class.getName());

    @Override
    public void upload(String dossier, byte[] file, String doccode) throws Exception {

        logger.info("uploading!");




        DocLoadManager docLoadManager = new DocLoadManager();

        short type=212;
        String annotation = "";
        EPODate aDate = new EPODate();

        String docSource = "C:\\desc_amnd.pdf";
        String procedure="";
        boolean isSendMsg = false;
        String sendMsgToUser = "";
        String sendMsgToTeam = "";
        String textMailBox = "";
        String historyStr = "";
        String filter = ".pdf";
        File[] files = {new File(docSource)};
        docLoadManager.addFromLocation(files, filter);

        docLoadManager.load(dossier, type, annotation, aDate, doccode, docSource, procedure, isSendMsg, sendMsgToUser, sendMsgToTeam, textMailBox, historyStr);



    }
}

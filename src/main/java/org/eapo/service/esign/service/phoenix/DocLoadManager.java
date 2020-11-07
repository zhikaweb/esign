package org.eapo.service.esign.service.phoenix;

import java.io.File;
import java.util.Date;

public interface DocLoadManager {

    void load(String dossier, short type, String annotation, Date aDate, String doccode, String docSource, String procedure,
                     boolean isSendMsg, String sendMsgToUser, String sendMsgToTeam, String textMailBox, String historyStr) throws Exception;

    void addFromLocation(File[] files, String filter);
}

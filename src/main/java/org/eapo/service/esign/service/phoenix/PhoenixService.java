package org.eapo.service.esign.service.phoenix;

import java.util.Date;
import java.util.List;

public interface PhoenixService {
    void upload(String dossier, byte[] file, String doccode, Date date, boolean doSavePDF) throws Exception;

    void signAndUpload(String dossier, List<String> certHolders, byte[] pdf, String doccode, Date date, boolean doSavePDF) throws Exception;
}

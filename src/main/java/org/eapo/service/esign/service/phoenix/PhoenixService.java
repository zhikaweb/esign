package org.eapo.service.esign.service.phoenix;

import java.util.Date;

public interface PhoenixService {
    void upload(String dossier, byte[] file, String doccode, Date date) throws Exception;
}

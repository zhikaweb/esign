package org.eapo.service.esign.service.phoenix;

public interface PhoenixService {
    void upload(String dossier, byte[] file, String doccode) throws Exception;
}

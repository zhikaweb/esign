package org.eapo.service.esign.service;

import org.eapo.service.esign.model.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface UploadService {
    Document uploadFile(MultipartFile file, String idappli, Integer odcorresp, String signer);
    Document downloadFile(String idappli, Integer odcorresp) throws Exception;
    Long deleteFile(String idappli, Integer odcorresp);
}

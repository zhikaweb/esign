package org.eapo.service.esign.service;

import org.eapo.service.esign.model.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface UploadService {
    Document uploadFile(MultipartFile file,
                        String idappli,
                        Integer odcorresp,
                        List<String> certHolder,
                        String saveToStore,
                        Integer fpage,
                        Integer lpage);

    Document downloadFile(String idappli, Integer odcorresp) throws Exception;

    Long deleteFile(String idappli, Integer odcorresp);

    boolean isExists(Document doc);

    byte[] flat(List<Document> documents);
}

package org.eapo.service.esign.service.store;

import org.eapo.service.esign.model.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
 @Profile("store-stub")
public class DocumentServiceStub implements DocumentService {
    @Override
    public String save(Document document) {
        return null;
    }

    @Override
    public Document get(String id) {

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("stub-document.pdf").getFile());

        try {
            byte[] pdf  = Files.readAllBytes(file.toPath());
            return new Document("1",2,pdf);
        } catch (Exception e) {
            return null;
        }


    }

    @Override
    public Long delete(String id) {
        return null;
    }
}

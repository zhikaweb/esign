package org.eapo.service.esign.service.store;

import org.eapo.service.esign.model.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

 @Service
 @Profile("store-stub")
public class DocumentServiceStub implements DocumentService {
    @Override
    public String save(Document document) {
        return null;
    }

    @Override
    public Document get(String id) {
        return null;
    }

    @Override
    public Long delete(String id) {
        return null;
    }
}

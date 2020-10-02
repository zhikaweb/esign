package org.eapo.service.esign.service;

import org.eapo.service.esign.model.Document;

public interface DocumentService {
    String save(Document document);
    Document get(String id);
}

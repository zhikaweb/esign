package org.eapo.service.esign.service.store;

import org.eapo.service.esign.model.Document;
import org.eapo.service.esign.service.SignerPdfServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
@Profile("file-store")
public class DocumentServiceFileImpl implements DocumentService {

    private static Logger logger = LoggerFactory.getLogger(DocumentServiceFileImpl.class.getName());

    @Value("${esigner.file.store.path}")
    private String fileStorePath;

    @Override
    public String save(Document document) {

        logger.info("save document {}", document);

        try {
            Path path = Paths.get(fileStorePath, document.getId() + ".pdf");
            FileOutputStream fileOutputStream = new FileOutputStream(path.toFile());
            fileOutputStream.write(document.getBody());
            fileOutputStream.close();
            return document.getId();
        } catch (Exception e) {
            logger.error("error on saving document: {} ", e.getMessage());
            return null;
        }
    }

    @Override
    public Document get(String id) {

        logger.info("getting document {}", id);

        Path path = Paths.get(fileStorePath, id + ".pdf");

        if (! path.toFile().exists()){
            logger.debug(" document with id {} not found", id);
            return null;
        }

        byte[] body;
        try {
            body = Files.readAllBytes(path);
            return new Document(id, body);
        } catch (IOException e) {
            logger.error("error on getting document: {} ", e.getMessage());
            return null;
        }

    }

    @Override
    public Long delete(String id) {
        logger.info("delete document with id {}", id);
        Path path = Paths.get(fileStorePath, id + ".pdf");
        return path.toFile().delete() ? 1L : 0L;
    }

    @Override
    public boolean isExists(Document document) {
        logger.info("check  document {}", document);

        Path path = Paths.get(fileStorePath, document.getId() + ".pdf");
        return path.toFile().exists();
    }
}

package org.eapo.service.esign.service.store;

import org.eapo.service.esign.model.Document;
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

    @Value("${esigner.file.store.path}")
    private String fileStorePath;

    @Override
    public String save(Document document) {


        try {
            Path path = Paths.get(fileStorePath, document.getId() + ".pdf");
            FileOutputStream fileOutputStream = new FileOutputStream(path.toFile());
            fileOutputStream.write(document.getBody());
            fileOutputStream.close();
            return document.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Document get(String id) {
        Path path = Paths.get(fileStorePath, id + ".pdf");

        byte[] body;
        try {
            body = Files.readAllBytes(path);
            return new Document(id, body);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public Long delete(String id) {
        Path path = Paths.get(fileStorePath, id + ".pdf");
        return path.toFile().delete() ? 1L : 0L;
    }
}

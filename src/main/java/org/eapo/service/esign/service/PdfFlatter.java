package org.eapo.service.esign.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;


// объединяет несколько pdf в один
@Service
public class PdfFlatter {

    public byte[] concat(List<byte[]> docs) {

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            Document document = new Document();
            PdfCopy copy = new PdfSmartCopy(document, byteArrayOutputStream);
            document.open();


            docs.forEach(doc -> {
                PdfReader reader = null;
                try {
                    reader = new PdfReader(doc);
                    copy.addDocument(reader);
                } catch (Exception e1) {
                    e1.printStackTrace();
                } finally {
                    if (reader != null) {
                        reader.close();
                    }

                }
            });
            document.close();
            return byteArrayOutputStream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

}

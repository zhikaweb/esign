package org.eapo.service.esign.service;


import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class Word2PdfImpl implements Word2Pdf {

    public byte[] convert(byte[] docx) throws IOException {
        // 1) Load docx with POI XWPFDocument
        XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(docx));

        // 2) Convert POI XWPFDocument 2 PDF with iText


        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfOptions options = null;// PDFViaITextOptions.create().fontEncoding( "windows-1250" );
        PdfConverter.getInstance().convert(document, out, options);
        return out.toByteArray();

    }

}

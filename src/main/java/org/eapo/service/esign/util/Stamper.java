package org.eapo.service.esign.util;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import java.io.*;

public class Stamper {

public byte[] doStamp(byte[] pdf, byte[] stamp) throws IOException, DocumentException {


    InputStream pdfStream = new ByteArrayInputStream(pdf);

    PdfReader pdfReader = new PdfReader(pdfStream);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();


    PdfStamper pdfStamper = new PdfStamper(pdfReader, baos);

    PdfContentByte content = pdfStamper.getOverContent(pdfReader.getNumberOfPages());
    Image deliverImg = Image.getInstance(stamp);
    //      deliverImg.setScaleToFitLineWhenOverflow(true);
    //  deliverImg.setAbsolutePosition(300f, 10f);
    //        deliverImg.setAbsolutePosition(0f, 0f);
    Rectangle r = pdfReader.getPageSize(pdfReader.getNumberOfPages());

    float width = r.getWidth() - deliverImg.getWidth();
    float height = 0f;


    deliverImg.setAbsolutePosition(width, height);

    content.addImage(deliverImg);
    pdfStamper.close();

    return baos.toByteArray();

}

}

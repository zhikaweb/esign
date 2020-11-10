package org.eapo.service.esign.service.converter;

import org.eapo.service.esign.ws.Convert2PDFClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Word2PDfConverter implements Word2Pdf{

	public static final Integer wdFormatPdf = new Integer(17);

	@Autowired
	Convert2PDFClient convert2PDFClient;

	@Override
	public byte[] convert(byte[] docx) throws Exception {

		return convert2PDFClient.convert(docx);
	}

}

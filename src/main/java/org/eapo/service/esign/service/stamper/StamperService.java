package org.eapo.service.esign.service.stamper;

import com.lowagie.text.DocumentException;

import java.io.IOException;

public interface StamperService {

    byte[] doStamp(byte[] pdf, String certHolder, Integer fpage, Integer lpage) throws IOException, DocumentException;

}

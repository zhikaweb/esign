package org.eapo.service.esign.service.stamper;

import com.lowagie.text.DocumentException;

import java.io.IOException;
import java.util.List;

public interface StamperService {

    byte[] doStamp(byte[] pdf, List<String> certHolder, Integer fpage, Integer lpage, String idletter) throws IOException, DocumentException;

}

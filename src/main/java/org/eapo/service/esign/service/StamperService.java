package org.eapo.service.esign.service;

import com.lowagie.text.DocumentException;

import java.io.IOException;

public interface StamperService {

    byte[] doStamp(byte[] pdf, String user) throws IOException, DocumentException;

}

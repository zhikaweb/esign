package org.eapo.service.esign.service.stamper;

import com.lowagie.text.DocumentException;

import java.io.IOException;

// @Service
public class StamperServiceStub implements StamperService {
    @Override
    public byte[] doStamp(byte[] pdf, String user) throws IOException, DocumentException {
        return pdf;
    }
}

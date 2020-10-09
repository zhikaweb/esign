package org.eapo.service.esign.service;

import com.lowagie.text.DocumentException;
import org.springframework.stereotype.Service;

import java.io.IOException;

// @Service
public class StamperServiceStub implements StamperService {
    @Override
    public byte[] doStamp(byte[] pdf, String user) throws IOException, DocumentException {
        return pdf;
    }
}

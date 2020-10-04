package org.eapo.service.esign.service.converter;

import java.io.IOException;

public interface Converter2PdfService {
    byte[] convert(byte[] file)throws IOException;
}

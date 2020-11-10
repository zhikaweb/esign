package org.eapo.service.esign.service.converter;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface Word2Pdf {

    byte[] convert(byte[] file) throws IOException, Exception;
}

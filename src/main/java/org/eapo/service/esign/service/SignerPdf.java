package org.eapo.service.esign.service;

public interface SignerPdf {

    byte[] sign(byte[] pdf) throws Exception;
}

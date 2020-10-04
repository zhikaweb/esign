package org.eapo.service.esign.service;

/**
 * Установка ЭЦП
 **/


public interface SignerPdfService {
    byte[] sign(byte[] pdf) throws Exception;
}

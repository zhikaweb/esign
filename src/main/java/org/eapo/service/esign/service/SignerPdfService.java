package org.eapo.service.esign.service;

import java.util.List;

/**
 * Установка ЭЦП
 **/


public interface SignerPdfService {
    byte[] sign(byte[] pdf, List<String> certHolder) throws Exception;
}

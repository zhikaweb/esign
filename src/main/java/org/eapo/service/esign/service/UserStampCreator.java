package org.eapo.service.esign.service;

import java.io.IOException;
import java.security.cert.X509Certificate;

/**
 * Создает "штампик" с логотипом ведомства и ФИО подписанта
 */

public interface UserStampCreator {
    byte[] build(X509Certificate certificate);
}

package org.eapo.service.esign.service.stamper;

import java.security.cert.X509Certificate;

/**
 * Создает "штампик" с логотипом ведомства и ФИО подписанта
 */

public interface UserStampCreator {
    byte[] build(X509Certificate certificate);
}

package org.eapo.service.esign.service;

import java.io.IOException;

/**
 * Создает "штампик" с логотипом ведомства и ФИО подписанта
 */

public interface UserStampCreator {
    byte[] build(String user, String sertNumber);
}

package org.eapo.service.esign.service;

import java.security.cert.X509Certificate;

/**
 * В
 */

public interface StampTextCreator {
    String getCertText(X509Certificate x509);
}

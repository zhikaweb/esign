package org.eapo.service.esign.service;

import org.springframework.stereotype.Service;

import java.security.cert.X509Certificate;
import java.util.Date;

@Service
public class StampTextCreatorImpl implements StampTextCreator {

    public String getCertText(X509Certificate x509) {
        return "Signed by " + x509.getIssuerDN() + "at " + new Date()
                + "\n Certificate " + x509.getSerialNumber()
                + "\n Valid to " + x509.getNotAfter();
    }

}

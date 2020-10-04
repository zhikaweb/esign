package org.eapo.service.esign.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.cert.X509Certificate;
import java.util.Date;

@Service
public class StampTextCreatorImpl implements StampTextCreator {

    private static Logger logger = LoggerFactory.getLogger(StampTextCreatorImpl.class.getName());


    public String getCertText(X509Certificate x509) {
        logger.debug("Making Text for esign stamp...");
        return "Signed by " + x509.getIssuerDN() + "at " + new Date()
                + "\n Certificate " + x509.getSerialNumber()
                + "\n Valid to " + x509.getNotAfter();
    }

}

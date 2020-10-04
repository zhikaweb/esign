package org.eapo.service.esign.crypto;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("reinstallkeys")
public class InitializingKeysBean implements InitializingBean {

    @Autowired
    private CertificateCreator certificateCreator;

    @Override
    public void afterPropertiesSet() throws Exception {
        certificateCreator.generateSelfSignedX509Certificate();
    }
}
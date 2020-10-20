package org.eapo.service.esign.service.stamper;

import org.eapo.service.esign.crypto.KeyStoreHelper;
import org.eapo.service.esign.exception.EsignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.NoSuchFileException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

@Service
public class StamperServiceImpl implements StamperService {

    private static Logger logger = LoggerFactory.getLogger(StamperServiceImpl.class.getName());

    @Autowired
    StamperHelper stamperHelper;

    @Value("${esigner.userstamp.position.height}")
    float stampPositionHeight;

    @Value("${esigner.userstamp.position.width}")
    Integer stampPositionWidth;

    @Autowired
    UserStampCreator userStampCreator;

    @Autowired
    KeyStoreHelper keyStoreHelper;

    @Override
    public byte[] doStamp(byte[] pdf, String certHolder) {

        logger.debug("Making stamp for user {}", certHolder);

        X509Certificate cert = null;
        try {
            KeyStore keyStore = keyStoreHelper.load(certHolder);
            cert = (X509Certificate) keyStore.getCertificate(certHolder);
        } catch (NoSuchFileException e) {

            try {
                logger.warn("Сертификат пользователя {} не обнаружен. Берем корневик!", certHolder);
                KeyStore keyStore = keyStoreHelper.load(KeyStoreHelper.CA);
                cert = (X509Certificate) keyStore.getCertificate(KeyStoreHelper.CA);
            } catch (Exception ex) {
                logger.error("Ошибка чтения корневика! {}", ex.getMessage());

            }

        } catch (Exception e) {
            logger.error("Cant read keystore {}", e.getMessage());
            throw new EsignException("Cant read keystore", e);
        }


        byte[] stamp = userStampCreator.build(cert);

        float width = stampPositionWidth;// + r.getWidth() - deliverImg.getWidth();
        float height = stampPositionHeight;

        return stamperHelper.doStamp(pdf, stamp, width, height);
    }


}
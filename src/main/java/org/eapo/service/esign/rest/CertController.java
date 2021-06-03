package org.eapo.service.esign.rest;

import org.eapo.service.esign.crypto.RootCertificateCreator;
import org.eapo.service.esign.crypto.UserCertificateCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController()
@RequestMapping("/cert")
public class CertController {

    private static Logger logger = LoggerFactory.getLogger(CertController.class.getName());


    @Autowired
    private RootCertificateCreator rootCertificateCreator;

    @Autowired
    private UserCertificateCreator userCertificateCreator;

    @RequestMapping(method = RequestMethod.GET)
    public String create(@RequestParam("name") String name, @RequestParam("logname") String logname) throws Exception {

        try {
            userCertificateCreator.create(name, logname);
        } catch (Exception e){
            String err = "Ошибка генерации сертификата для пользователя ".concat(name).concat(" ").concat(logname).concat(":").concat(e.getMessage());
            logger.error(err);
            return err;
        }
        String res = "Сертификат для пользователя ".concat(name).concat(" добавлен в файл ").concat(logname).concat(".pkcs");
        logger.info(res);
        return res;
    }

}

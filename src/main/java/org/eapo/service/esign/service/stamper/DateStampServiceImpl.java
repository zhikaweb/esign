package org.eapo.service.esign.service.stamper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DateStampServiceImpl implements DateStampService {

    private static Logger logger = LoggerFactory.getLogger(DateStampServiceImpl.class.getName());


    @Autowired
    StamperHelper stamperHelper;

    @Autowired
    DateStampCreator dateStampCreator;


    @Value("${esigner.datestamp.position.height:600}")
    float datePositionHeight;

    @Value("${esigner.datestamp.position.width:50}")
    Integer datePositionWidth;

    @Override
    public byte[] doStamp(byte[] pdf, String date) {

        byte[] stamp = dateStampCreator.build(date);

        float width = datePositionWidth;
        float height = datePositionHeight;

        return stamperHelper.doStamp(pdf, stamp, width, height);

    }
}

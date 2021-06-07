package org.eapo.service.esign.service.stamper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
public class DateStampServiceImpl implements DateStampService {

    private static Logger logger = LoggerFactory.getLogger(DateStampServiceImpl.class.getName());


    @Autowired
    StamperHelper stamperHelper;

    @Autowired
    DateStampCreator dateStampCreator;

    @Autowired
    TextPositionFinder textPositionFinder;

  //  @Value("${esigner.datestamp.position.height:643}")
  //  float datePositionHeight;

  //  @Value("${esigner.datestamp.position.width:165}")
  //  Integer datePositionWidth;

    @Value("${esigner.datestamp.pattern:DatePattern}")
    String dateStampPattern;

    @Override
    public byte[] doStamp(byte[] pdf, String date) {

        byte[] stamp = dateStampCreator.build(date);

        TextPositionFinder.Position position = getPosition(pdf);

        // если нашли паттерн - ставим штампик с датой
        if (position.isFound()) {
            logger.info("DatePattern {} found x={} y={} page={}", dateStampPattern, position.getX(), position.getY(), position.getPage() );
            return stamperHelper.doStamp(pdf, stamp, Collections.singletonList(position));
        }
        // если не нашли -  не ставим никакой штамп
        else {
            logger.info("DatePattern {} not found", dateStampPattern);
            return pdf;
        }
    }

    /**
     * Находим позицию паттерна (шаблона) для штампа на первой странице.
     *
     * @param pdf
     * @return
     */

    private TextPositionFinder.Position getPosition(byte[] pdf) {

     //   float width = datePositionWidth;
     //   float height = datePositionHeight;

        TextPositionFinder.Position position = null;

        try {
            List<TextPositionFinder.Position> positions = textPositionFinder.position(pdf,dateStampPattern);
             position = positions.get(0);

        } catch (IOException e) {
            e.printStackTrace();
            position = new TextPositionFinder.Position();
        }

        /*
        if (!position.isFound()){
            position.setPage(1);
            position.setX(width);
            position.setY(height);
        }

         */
        return position;
    }
}

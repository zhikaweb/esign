package org.eapo.service.esign.service.stamper;

import org.eapo.service.esign.util.DoccodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
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

        List<TextPositionFinder.Position> positions = getPositions(pdf);

        // если нашли паттерн - ставим штампик с датой
        if (!positions.isEmpty()) {
//            logger.info("DatePattern {} found x={} y={} page={}", dateStampPattern, position.getX(), position.getY(), position.getPage() );
            logger.info("DatePattern {} found x={} y={} page={}", dateStampPattern, positions.get(0).getX(), positions.get(0).getY(), positions.get(0).getPage() );
            return stamperHelper.doStamp(pdf, stamp, positions);
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

    private List<TextPositionFinder.Position> getPositions(byte[] pdf) {

     //   float width = datePositionWidth;
     //   float height = datePositionHeight;

        List<TextPositionFinder.Position> positions = null;

        try {
            positions = textPositionFinder.position(pdf,dateStampPattern);
//             position = positions.get(0);

        } catch (IOException e) {
            e.printStackTrace();
            positions = new ArrayList<>();
        }

        /*
        if (!position.isFound()){
            position.setPage(1);
            position.setX(width);
            position.setY(height);
        }

         */
        return positions;
    }
}

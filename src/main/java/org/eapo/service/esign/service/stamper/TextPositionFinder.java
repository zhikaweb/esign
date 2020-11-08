package org.eapo.service.esign.service.stamper;


import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.TextMarginFinder;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 ищет паттерн pattern в документе pdf на каждой странице
 возвращает список Position - по одной позиции для каждой страницы
 если паттерн не найден на странице то Position isFound = false
 если паттерн найден на странице то Position isFound = true и координаты
*/

@Service
public class TextPositionFinder {

    public List<Position> position(byte[] pdf, String pattern) throws IOException {


        PdfReader reader = new PdfReader(new ByteArrayInputStream(pdf));


        PdfReaderContentParser parser = new PdfReaderContentParser(reader);

        List<Position> positions = new ArrayList<>();


        for (int page = 1; page < reader.getNumberOfPages(); page++) {

            Position position = new Position();
            position.setPage(page);
            position.setFound(false);
            positions.add(position);

            parser.processContent(page, new TextMarginFinder() {
                @Override
                public void renderText(TextRenderInfo renderInfo) {
                    super.renderText(renderInfo);

                    System.out.println(renderInfo.getText());
                    if (renderInfo.getText().contains(pattern)) {

                        float x = renderInfo.getBaseline().getStartPoint().get(0);
                        float y = renderInfo.getBaseline().getStartPoint().get(1);
                        position.setX(x);
                        position.setY(y);
                        position.setFound(true);
                    }
                }
            });
        }


        return positions;
    }

    ;

    public static class Position {


        private float x;
        private float y;


        private boolean found = false;
        private int page;

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public boolean isFound() {
            return found;
        }

        public void setFound(boolean found) {
            this.found = found;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getPage() {
            return page;
        }
    }

}

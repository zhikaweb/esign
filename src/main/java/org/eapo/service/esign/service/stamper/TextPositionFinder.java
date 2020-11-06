package org.eapo.service.esign.service.stamper;


import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.TextMarginFinder;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class TextPositionFinder {

   public Position position(byte[] pdf, String pattern) throws IOException {


        PdfReader reader = new PdfReader(new ByteArrayInputStream(pdf));


        PdfReaderContentParser parser = new PdfReaderContentParser(reader);

        Position position = new Position();


        for (int page = 1; page < reader.getNumberOfPages(); page++) {

            parser.processContent(page, new TextMarginFinder() {
                @Override
                public void renderText(TextRenderInfo renderInfo) {
                    super.renderText(renderInfo);

                    if (pattern.equals(renderInfo.getText())) {

                        float x = renderInfo.getBaseline().getStartPoint().get(0);
                        float y = renderInfo.getBaseline().getStartPoint().get(1);
                        position.x = x;
                        position.y = y;
                        position.found = true;
                    }
                }
            });
        }


        return position;
    }

    ;

    public static class Position {


        private float x;
        private float y;

        private boolean found = false;

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
    }

}

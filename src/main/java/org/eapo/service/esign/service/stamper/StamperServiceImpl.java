package org.eapo.service.esign.service.stamper;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfReader;
import org.eapo.service.esign.crypto.KeyStoreHelper;
import org.eapo.service.esign.exception.EsignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.NoSuchFileException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class StamperServiceImpl implements StamperService {

    private static Logger logger = LoggerFactory.getLogger(StamperServiceImpl.class.getName());

    @Autowired
    StamperHelper stamperHelper;

    @Value("${esigner.userstamp.position.height}")
    float stampPositionHeight;

    @Value("${esigner.userstamp.position.width}")
    Integer stampPositionWidth;

    @Value("${esigner.userstamp.pattern:Signature}")
    String stampPattern;

    @Autowired
    UserStampCreator userStampCreator;

    @Autowired
    KeyStoreHelper keyStoreHelper;

    @Autowired
    TextPositionFinder textPositionFinder;

    public byte[] doStamp(byte[] pdf, List<String> certHolders, Integer fPage, Integer lPage) throws IOException, DocumentException {

        int i = 1;
        for (String certHolder: certHolders) {
            pdf = doStamp(pdf, certHolder, i, fPage, lPage);
            i++;
        }
        return pdf;
    }


    public byte[] doStamp(byte[] pdf, String certHolder, int pos, Integer fPage, Integer lPage) throws IOException, DocumentException {
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

        final float width = stampPositionWidth;// + r.getWidth() - deliverImg.getWidth();
        final float height = stampPositionHeight;


        int lastPage = getNumberOfPages(pdf);

        List<TextPositionFinder.Position> positions;

        // если задан шаблон поиска - ищем его в документе
        if (stampPattern.length()>0) {
            String pattern = stampPattern + pos;
            positions = textPositionFinder.position(pdf, pattern);
            positions.forEach(p -> {
                if (!p.isFound()) {
                    p.setX(width);
                    p.setY(height);
                }
            });
        } else {
            // если шаблон поиска не задан - считаем что не нашли совпадения ни на одной странице
           positions = IntStream.range(1, lastPage+1).mapToObj(i->{
                TextPositionFinder.Position position = new TextPositionFinder.Position();
                position.setPage(i);
                position.setFound(false);
                return position;
            }).collect(Collectors.toList());
        }

        // штампики ставим до последней указанной страницы
            if (lPage > 0) {
                lastPage = Math.min(lastPage, lPage);
            }

            final int finalLastPage = lastPage;

            // отбираем страницы которые попадают в диапазон для штампиков
            List<TextPositionFinder.Position> stampPositions = positions.stream()
                    .filter(p->((p.getPage()>=fPage) && (p.getPage()<=finalLastPage)))
                    .collect(Collectors.toList());

            // ставим штампики в нужные позиции
        return stamperHelper.doStamp(pdf, stamp, stampPositions);

    }

    private int getNumberOfPages(byte[] pdf){
        try(InputStream pdfStream = new ByteArrayInputStream(pdf)){
            PdfReader pdfReader = new PdfReader(pdfStream);
            return pdfReader.getNumberOfPages();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

}

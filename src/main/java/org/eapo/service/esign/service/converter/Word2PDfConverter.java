package org.eapo.service.esign.service.converter;

/*
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import commons.msoffice.wrappers.Documents;
import commons.msoffice.wrappers.Word;
import commons.msoffice.wrappers.WordDocument;
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;

/**
 * Конвертирует вордовский файл DOCX в PDF.
 * 
 * @author etyurin
 *
 */
@Service
public class Word2PDfConverter implements Word2Pdf{
	@Override
	public byte[] convert(byte[] file) throws IOException {
		return file;
	}
/*
	public static final Integer wdFormatPdf = new Integer(17);

	private Word word = Word.getInstance();;

	private static Logger logger = LoggerFactory.getLogger(Word2PDfConverter.class.getName());
	@Override
	public byte[] convert(byte[] docx) throws IOException {
		File tmpWord = File.createTempFile("tmp-word-", ".docx");
		FileOutputStream fileOutputStream = new FileOutputStream(tmpWord);
		fileOutputStream.write(docx);
		fileOutputStream.close();
		File tmpPdf =  convert(tmpWord);
		return Files.readAllBytes(tmpPdf.toPath());
	}

	private synchronized File convert(File docxFile) throws IOException {
		logger.debug("convert doc {} ", docxFile.getAbsolutePath());
        boolean quitWord = this.word == null;

		WordDocument wordDoc = null;
		File pdfFile = File.createTempFile("word-", ".pdf");
		try {
			Documents documents = word.getDocuments();
			logger.debug("open doc");
			Variant var = Dispatch.call(documents, "Open", docxFile.getAbsolutePath());
			wordDoc = new WordDocument(var.toDispatch());
			logger.debug("SaveAs2");
			wordDoc.invoke("SaveAs2", new Variant(pdfFile.getAbsolutePath()), new Variant(wdFormatPdf));
			logger.debug("closeNotSave");
	        wordDoc.closeNotSave();
	        wordDoc = null;
		} finally {
			if (wordDoc != null) {
				wordDoc.closeNotSave();		
			}
			if (quitWord) {
				word.quit();
			}
		}
		return pdfFile;
	}

*/
}

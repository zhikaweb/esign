package eapo.commons.word2pdf;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import commons.Logger;
import commons.msoffice.wrappers.Documents;
import commons.msoffice.wrappers.Word;
import commons.msoffice.wrappers.WordDocument;
/**
 * Конвертирует вордовский файл DOCX в PDF.
 * 
 * @author etyurin
 *
 */
public class Word2PDfConverter {
	public static final Integer wdFormatPdf = new Integer(17);
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private Word word;
	
	public File convert(File docxFile) throws IOException {
        debug("convert doc: "+docxFile.getAbsolutePath());
        boolean quitWord = this.word == null;
        Word _word = getWord();
		WordDocument wordDoc = null;
		File pdfFile = File.createTempFile("word-", ".pdf");
		try {
			Documents documents = _word.getDocuments();
			debug("open doc");
			Variant var = Dispatch.call(documents, "Open", docxFile.getAbsolutePath());
			debug(var.getvt()+": "+var);
			//		WordDocument wordDoc = documents.open(docxFile.getAbsolutePath());
			wordDoc = new WordDocument(var.toDispatch());
			debug("saveAs2");
			wordDoc.invoke("SaveAs2", new Variant(pdfFile.getAbsolutePath()), new Variant(wdFormatPdf));
			debug("closeNotSave");
	        wordDoc.closeNotSave();
	        wordDoc = null;
		} finally {
			if (wordDoc != null) {
				wordDoc.closeNotSave();		
			}
			if (quitWord) {
				_word.quit();				
			}
		}
		return pdfFile;
	}

	private void debug(String s) {
		Logger.debug(sdf.format(new Date())+" "+s);
	}

	public Word getWord() {
		if (word == null) {
			word = Word.getInstance();
		}
		return word;
	}
	
	public void setWord(Word word) {
		this.word = word;
	}
}

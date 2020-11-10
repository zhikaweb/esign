/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eapo.service.esign.service.phoenix;


import org.eapo.phoenix.phxhelper.PhoenixHistory;
import org.eapo.phoenix.phxhelper.PhoenixMessage;
import org.epo.utils.*;
import org.epoline.impexp.jsf.online.imp.cl.OnlineImportServiceDelegate;
import org.epoline.impexp.jsf.online.imp.dl.LoadDocument;
import org.epoline.impexp.jsf.online.imp.dl.OnlineImportSession;

import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

//import java.io.PrintStream;
// import org.eapo.phoenix.phxhelper.phxh_Exception;

/**
 * @author Dmitriy Merkushov
 */

//@Service
public class DocLoadManagerImpl implements DocLoadManager {
  /*
    @Override
    public void load(String dossier, short type, String annotation, Date aDate, String doccode, String docSource, String procedure, boolean isSendMsg, String sendMsgToUser, String sendMsgToTeam, String textMailBox, String historyStr) throws Exception {

    }

    @Override
    public void addFromLocation(File[] files, String filter) {

    }

*/

    private List<String> toAddList = new LinkedList<String>();
    private String defDoccode;
    private String defDirectory = null;
    private boolean sureByDefault = false;
    byte[] buf = new byte[2048];
    private String[] docSourceOptions;
    private String[] procedureOptions;
    private boolean editableDoccode = true;
    private boolean selectableSource = true;
    private boolean selectableSourceKind = true;
    private int maxTiffPages = 9998;
    private Properties props;
    public static final short PDF = 212;
    public static final short PS = 235;
    public static final short BLOB = 134;
    public static final short ZBLOB = 433;
    public static final short TIFF = 402;
//	public static final String DOCCODEPROCEDURE = "<DocCode>";

    public DocLoadManagerImpl()
            throws Exception {
        initialize();
    }

    public String getDefaultDoccode() {
        return this.defDoccode;
    }

    public boolean isSureByDefault() {
        return this.sureByDefault;
    }

    public void sortList(List<String> aList) {
        String[] tmpArray = new String[aList.size()];

        int counter = 0;
        Iterator<String> iter = aList.iterator();
        while (iter.hasNext()) {
            tmpArray[(counter++)] = iter.next();
        }

        Arrays.sort(tmpArray);

        aList.clear();
        for (int i = 0; i < tmpArray.length; ++i) {
            aList.add(tmpArray[i]);
        }
    }

    private void addFromLocation(File location, String forcedExt) {
        if (location.isDirectory()) {
            List<String> tmpList = new ArrayList<String>();
            File[] tmpAllFiles = location.listFiles();
            for (int i = 0; i < tmpAllFiles.length; ++i) {
                if (tmpAllFiles[i].isFile()) {
                    String lastBit = null;
                    String tmp = tmpAllFiles[i].getAbsolutePath();
                    if ((forcedExt != null) && (tmp.length() > forcedExt.length())) {
                        lastBit = tmp.substring(tmp.length() - forcedExt.length());
                        lastBit = lastBit.toLowerCase();
                    }
                    if ((forcedExt != null) && (((lastBit == null) ||
                            (!(lastBit.equals(forcedExt)))))) {
                        continue;
                    }
                    tmpList.add(tmp);
                } else {
                    addFromLocation(tmpAllFiles[i], forcedExt);
                }

            }

            sortList(tmpList);
            this.toAddList.addAll(tmpList);
        } else {
            this.toAddList.add(location.getAbsolutePath());
        }
    }

    public void addFromLocation(File[] location, String forcedExt) {
        List<String> fileList = new ArrayList<String>();
        Stack<File> dirStack = new Stack<File>();

        for (int i = 0; i < location.length; ++i) {
            if (location[i].isFile()) {
                fileList.add(location[i].getAbsolutePath());
            } else {
                dirStack.push(location[i]);
            }

        }

        while (!(dirStack.isEmpty())) {
            addFromLocation(dirStack.pop(), forcedExt);
        }

        sortList(fileList);
        Iterator<String> iter = fileList.iterator();
        while (iter.hasNext()) {
            addFromLocation(new File(iter.next()), forcedExt);
        }
    }

    public void addLocation(File[] location) {
        for (int i = 0; i < location.length; ++i) {
            this.toAddList.add(location[i].getAbsolutePath());
        }
    }

    public List<String> getToAddList() {
        return this.toAddList;
    }

    public void removeItems(int[] theItems) {
        for (int i = 0; i < theItems.length; ++i) {
            this.toAddList.remove(theItems[(theItems.length - 1 - i)]);
        }
    }

    public void removeAllItems() {
        this.toAddList.clear();
    }

    private OnlineImportServiceDelegate getOnlineImportDelegate() throws Exception {
        return new OnlineImportServiceDelegate(this.props, null);
    }

    private int zipFiles(File inputFile, ZipOutputStream zippedStream, String beginPath) throws Exception {
        int retVal = 0;

        if (inputFile.isDirectory()) {
            File[] tmpAllFiles = inputFile.listFiles();
            if (tmpAllFiles.length != 0) {
                for (int i = 0; i < tmpAllFiles.length; ++i) {
                    String tmpBeginPath = "";
                    if (beginPath == null) {
                        tmpBeginPath = "";
                    } else {
                        tmpBeginPath = beginPath + inputFile.getName() + '/';
                    }

                    retVal += zipFiles(tmpAllFiles[i], zippedStream, tmpBeginPath);
                }
            } else {
                if (beginPath == null) {
                    throw new Exception("Selected empty directory as root: " + inputFile.getName());
                }

                zippedStream.putNextEntry(new ZipEntry(beginPath + inputFile.getName() + '/'));
                zippedStream.closeEntry();
            }
        } else {
            ZipEntry entry = new ZipEntry(beginPath + inputFile.getName());
            zippedStream.putNextEntry(entry);
            BufferedInputStream bis = null;
            try {
                int size;
                bis = new BufferedInputStream(new FileInputStream(inputFile));

                while ((size = bis.read(this.buf, 0, this.buf.length)) != -1) {
                    zippedStream.write(this.buf, 0, size);
                }
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException ee) {
                        System.err.println("Error closing temp file");
                        ee.printStackTrace();
                    }
                }
            }

            zippedStream.closeEntry();
            ++retVal;
        }

        return retVal;
    }

    private String fileForBlob(File inputFile, boolean alwaysZip) throws Exception {
        if ((inputFile.isFile()) && (!(alwaysZip))) {
            return inputFile.getAbsolutePath();
        }
        if ((inputFile.isDirectory()) || ((inputFile.isFile()) && (alwaysZip))) {
            String tempFile = this.props.getProperty("TEMPUPLOADFILE", "c:/data/blaat.zip");

            ZipOutputStream out =
                    new ZipOutputStream(new FileOutputStream(tempFile));
            try {
                out.setLevel(2);
                int number = zipFiles(inputFile, out, ((inputFile.isFile()) && (alwaysZip)) ? "" : null);
                if (number == 0) {
                    throw new Exception("Selected empty directory: " + inputFile.getName());
                }
            } finally {
                try {
                    out.close();
                } catch (Exception localException) {
                }
            }
            return tempFile;
        }

        throw new Exception("File is not directory or file, HELP!");
    }

    public void load(String dossier, short type, String annotation, Date sDate, String doccode, String docSource, String procedure,
                     boolean isSendMsg, String sendMsgToUser, String sendMsgToTeam, String textMailBox, String historyStr) throws Exception {
        boolean success = false;

        EPODate aDate = null;
           if (sDate!=null) {
              aDate = new EPODate(new java.sql.Date(sDate.getTime()));
           }
        if (this.toAddList.size() == 0) {
            throw new Exception("No documents found");
        }

        if ((type == 402) && (this.toAddList.size() > this.maxTiffPages)) {
            throw new Exception("Only " + this.maxTiffPages + " pages are allowed when loading tiffs");
        }

        OnlineImportServiceDelegate oisDelegate = getOnlineImportDelegate();
        OnlineImportSession session = oisDelegate.startSession();
        boolean multiple = this.toAddList.size() > 1;

        Iterator<String> iter = this.toAddList.iterator();
        int counter = 1;
        boolean isSetNOfPages = true;
        try {
            if (type == 402) {
                TIFF_Document tiffDoc = new TIFF_Document();
                while (iter.hasNext()) {
                    TIFF_Page page = new TIFF_Page(iter.next());
                    tiffDoc.addPage(page);
                }

                LoadDocument doc1 = new LoadDocument();
                doc1.setDossier(dossier);
                doc1.setLegalDate(aDate);
                doc1.setCreateMessage(true);
                doc1.setSource(docSource);
                if (!(procedure.equals("<DocCode>"))) {
                    doc1.setProcedure(procedure);
                }
                doc1.setImageDocument(tiffDoc);
                doc1.setDocCode(doccode);

                String targetAnno = annotation.trim();
                if (targetAnno.length() > 0) {
                    doc1.setAnnotation(targetAnno);
                }
                //doc1.setSource("SCAN");
                sendMessage(doc1, dossier, sendMsgToTeam, sendMsgToUser, textMailBox, isSendMsg, isSetNOfPages);

                oisDelegate.addDocument(session, doc1);
            } else {
                while (iter.hasNext()) {
                    LoadDocument doc1 = new LoadDocument();
                    doc1.setDossier(dossier);
                    doc1.setLegalDate(aDate);
                    doc1.setCreateMessage(true);
                    doc1.setSource(docSource);
                    if (!(procedure.equals("<DocCode>"))) {
                        doc1.setProcedure(procedure);
                    }
                    if (type == 212) {
                        PDF_Document pdf = new PDF_Document(iter.next());
                        doc1.setImageDocument(pdf);
                    } else if (type == 235) {
                        PS_Document aPS = new PS_Document(iter.next());
                        doc1.setImageDocument(aPS);
                    } else if ((type == 134) || (type == 433)) {
                        String theFile = iter.next();
                        File fileWrap = new File(theFile);

                        BLOB_Document aBLOB = new BLOB_Document(fileForBlob(fileWrap, type == 433));
                        doc1.setImageDocument(aBLOB);

                        isSetNOfPages = false;        // No pages allowed for BLOB
                    } else {
                        throw new Exception("Invalid input format");
                    }

                    doc1.setDocCode(doccode);

                    String targetAnno = annotation.trim();
                    if (multiple) {
                        if (targetAnno.length() > 0) {
                            targetAnno = targetAnno + " " + counter + " of " + this.toAddList.size();
                        } else {
                            targetAnno = counter + " of " + this.toAddList.size();
                        }
                    }

                    if (targetAnno.length() > 0) {
                        doc1.setAnnotation(targetAnno);
                    }
                    //doc1.setSource("SCAN");
                    sendMessage(doc1, dossier, sendMsgToTeam, sendMsgToUser, textMailBox, isSendMsg, isSetNOfPages);

                    oisDelegate.addDocument(session, doc1);
                    ++counter;
                }
            }
            addHistory(dossier, doccode, sendMsgToUser, historyStr, isSendMsg);

            oisDelegate.stopSession(session, true);

            success = true;
        } finally {
            if (!(success)) {
                try {
                    oisDelegate.stopSession(session, false);
                } catch (Exception localException) {
                }
            }
        }
    }

    private void sendMessage(LoadDocument doc1, String dossier, String sendMsgToTeam, String sendMsgToUser, String text, boolean isSendMsg, boolean isSetNOfPages) {
        if (isSendMsg) {
            doc1.setCreateMessage(true);
            doc1.setPriority(1);
            //doc1.setPackageID("PackageID");
            doc1.setCountryCode("EA");
            doc1.setSoftcopy(true);
            if (isSetNOfPages) {
                doc1.setNofPages(1);
            }
            doc1.setSoftcopy(true);
            doc1.setMessageText(text);
            doc1.setTargetTeam(sendMsgToTeam);
            doc1.setTargetUser(sendMsgToUser);
        }
    }

    private void addHistory(String dossier, String doccode, String sendMsgToUser, String historyStr, boolean isSendMsg) throws Exception {
        new PhoenixHistory().add(dossier, doccode, historyStr, sendMsgToUser);
    }

    public String getDefaultTeamMailboxForUser(String user) throws Exception {
        return new PhoenixMessage().getDefaultTeamMailboxForUser(user);
    }

    public String getDefDirectory() {
        return this.defDirectory;
    }


    public void initialize() throws Exception {
        this.props = new Properties();

        String theFile = System.getProperty("DRPROPS", "appload.properties");
        this.props.load(new FileInputStream(theFile));
        //	PropertyConfigurator.configure(this.props);

        StringTokenizer tokenizer = new StringTokenizer(this.props.getProperty("OPTIONALDOCSOURCES", ""), ";");
        List<String> tmpList = new LinkedList<String>();
        while (tokenizer.hasMoreTokens()) {
            String tmpSource = tokenizer.nextToken().trim();

            if (tmpSource.length() > 4) {
                throw new Exception("Document source too long:" + tmpSource);
            }
            tmpList.add(tmpSource);
        }
        this.docSourceOptions = tmpList.toArray(new String[0]);

        tokenizer = new StringTokenizer(this.props.getProperty("OPTIONALPROCEDURES", ""), ";");
        tmpList = new LinkedList<String>();
        tmpList.add("<DocCode>");
        while (tokenizer.hasMoreTokens()) {
            String tmpProcedure = tokenizer.nextToken().trim();

            tmpList.add(tmpProcedure);
        }
        this.procedureOptions = tmpList.toArray(new String[0]);

        this.editableDoccode = this.props.getProperty("EDITABLEDOCCODE", "TRUE").equalsIgnoreCase("TRUE");
        this.selectableSource = this.props.getProperty("SELECTABLESOURCE", "TRUE").equalsIgnoreCase("TRUE");
        this.selectableSourceKind = this.props.getProperty("SELECTABLESOURCEKIND", "TRUE").equalsIgnoreCase("TRUE");
        this.defDoccode = this.props.getProperty("DEFDOCCODE");
        if (this.defDoccode == null) {
            this.defDoccode = "";
        }
        try {
            this.maxTiffPages = Integer.parseInt(this.props.getProperty("MAXTIFFPAGES", "9998"));
        } catch (NumberFormatException localNumberFormatException) {
        }
        this.defDirectory = this.props.getProperty("DEFDIRECTORY");
        this.sureByDefault = this.props.getProperty("SUREBYDEFAULT", "No").startsWith("Y");
    }

    public String[] getDocSourceOptions() {
        return this.docSourceOptions;
    }

    public String[] getProcedureOptions() {
        return this.procedureOptions;
    }

    public short getPreselectedType() {
        String type = this.props.getProperty("PRESELECTEDTYPE", "PDF");

        if (type.equals("BLOB")) {
            return 134;
        }
        if (type.equals("ZBLOB")) {
            return 433;
        }
        if (type.equals("PS")) {
            return 235;
        }
        if (type.equals("TIFF")) {
            return 402;
        }
        return 212;
    }

    public void setDocSourceOptions(String[] docSourceOptions) {
        this.docSourceOptions = docSourceOptions;
    }

    public boolean isEditableDoccode() {
        return this.editableDoccode;
    }

    public boolean isSelectableSource() {
        return this.selectableSource;
    }

    public boolean isSelectableSourceKind() {
        return this.selectableSourceKind;
    }

}

package org.eapo.service.esign.service;

import org.eapo.service.esign.service.phoenix.DocLoadManager;
import org.eapo.service.esign.service.phoenix.PhoenixService;
import org.epo.utils.EPODate;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class PhoenixTest {

    @Autowired
    PhoenixService phoenixService;

    @Test
    public void upload() throws Exception {

        System.setProperty("java.security.policy", "src\\main\\resources\\java.pol");
        System.setProperty("java.rmi.server.useCodebaseOnly", "false");
     //   System.setProperty("java.security.policy", "C:\\Users\\AStal\\projects\\EAPO-UploaderGUI\\resources\\common\\java.pol");
        System.setProperty("hxHelperPropsFile", "C:\\Users\\AStal\\projects\\EAPO-UploaderGUI\\src\\main\\resources\\appload.properties");
        System.setProperty("DRPROPS", "C:\\Users\\AStal\\projects\\EAPO-UploaderGUI\\src\\main\\resources\\appload.properties");

        DocLoadManager docLoadManager = new DocLoadManager();

        String dossier="201990100";
        short type=212;
        String annotation = "";
        EPODate aDate = new EPODate();
        String doccode = "DOCRU";
        String docSource = "C:\\desc_amnd.pdf";
        String procedure="";
        boolean isSendMsg = false;
        String sendMsgToUser = "";
        String sendMsgToTeam = "";
        String textMailBox = "";
        String historyStr = "";
        String filter = ".pdf";
        File[] files = {new File(docSource)};
        docLoadManager.addFromLocation(files, filter);

        docLoadManager.load(dossier, type, annotation, aDate, doccode, docSource, procedure, isSendMsg, sendMsgToUser, sendMsgToTeam, textMailBox, historyStr);


        /*

        System.setProperty("java.rmi.server.useCodebaseOnly", "false");
        System.setProperty("java.security.policy", "C:\\Users\\AStal\\projects\\esign\\src\\main\\resources\\java.pol");
        System.setProperty("hxHelperPropsFile", "C:\\Users\\AStal\\projects\\EAPO-UploaderGUI\\src\\main\\resources\\appload.properties");
        System.setProperty("DRPROPS", "C:\\Users\\AStal\\projects\\EAPO-UploaderGUI\\src\\main\\resources\\appload.properties");

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("document.pdf").getFile());

        byte[] pdf  = Files.readAllBytes(file.toPath());

        phoenixService.upload("200800011", pdf, "DOCRU");
*/

    }

}

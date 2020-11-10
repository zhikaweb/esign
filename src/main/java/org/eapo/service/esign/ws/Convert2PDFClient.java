package org.eapo.service.esign.ws;


import org.eapo.pdfconverter.service.Convert2PDFRequest;
import org.eapo.pdfconverter.service.Convert2PDFResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;


public class Convert2PDFClient extends WebServiceGatewaySupport {

    private static Logger logger = LoggerFactory.getLogger(Convert2PDFClient.class.getName());

public byte[] convert(byte[] docx) throws WSException {

    Convert2PDFRequest request = new Convert2PDFRequest();
    request.setFile(docx);
    logger.info("sending docx to ws-service");
    Convert2PDFResponse response = (Convert2PDFResponse) getWebServiceTemplate()
            .marshalSendAndReceive(request);

    logger.info("getting ws response");
    if (response.isConverted()){
        return response.getFile();
    } else {
        logger.error("Error at ws service: {}", response.getMessage());
        throw new WSException(response.getMessage());
    }
}

}

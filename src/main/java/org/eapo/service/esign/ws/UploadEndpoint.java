package org.eapo.service.esign.ws;

import org.eapo.phoenix.upload.service.Upload2PhoenixRequest;
import org.eapo.phoenix.upload.service.Upload2PhoenixResponse;
import org.eapo.service.esign.service.phoenix.PhoenixService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.Date;


@Endpoint
public class UploadEndpoint {

	private static Logger logger = LoggerFactory.getLogger(UploadEndpoint.class.getName());

	private static final String NAMESPACE_URI = "http://service.upload.phoenix.eapo.org";

	@Autowired
	PhoenixService phoenixService;

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "upload2PhoenixRequest")
	@ResponsePayload
	public Upload2PhoenixResponse upload(@RequestPayload Upload2PhoenixRequest request) throws Exception {

        Upload2PhoenixResponse response = new Upload2PhoenixResponse();

		Date dtsend = null;
        if (request.getDtsend()!=null) {
			dtsend = request.getDtsend().toGregorianCalendar().getTime();
		}
		try {
			phoenixService.upload(request.getDosier(), request.getFile(), request.getDoccode(), dtsend);
			response.setIsSaved(true);
			response.setMessage("OK");
			logger.info("File was uploaded for dossier {}", request.getDosier());
		} catch (Exception e) {
			response.setIsSaved(false);
			response.setMessage(e.getMessage());
			logger.error("File was NOT uploaded for dossier {}, exception is {}", request.getDosier(), e.getMessage());
			logger.error("Uploading error", e);
		}
		return response;
	}
}

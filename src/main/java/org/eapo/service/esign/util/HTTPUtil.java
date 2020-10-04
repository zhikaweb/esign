package org.eapo.service.esign.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class HTTPUtil {

    public static HttpHeaders getHeaders(String filename) {
        HttpHeaders headers = getCommonHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);
        return headers;

    }


    public static HttpHeaders getCommonHeaders() {
        HttpHeaders headers = new HttpHeaders();
     //   headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        return headers;
    }
}
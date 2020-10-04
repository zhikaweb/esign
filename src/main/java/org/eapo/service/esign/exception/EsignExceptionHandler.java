package org.eapo.service.esign.exception;

import org.eapo.service.esign.util.HTTPUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class EsignExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value
            = {EsignException.class})
    public ResponseEntity<Object> scannerException(Exception ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        return handleExceptionInternal(ex, bodyOfResponse,
                HTTPUtil.getCommonHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

}
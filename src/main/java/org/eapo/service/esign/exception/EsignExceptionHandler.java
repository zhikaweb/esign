package org.eapo.service.esign.exception;

import lombok.extern.slf4j.Slf4j;
import org.eapo.service.esign.util.HTTPUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class EsignExceptionHandler extends ResponseEntityExceptionHandler {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @ExceptionHandler(value
            = {EsignException.class})
    public ResponseEntity<Object> scannerException(Exception ex, WebRequest request) {
        log.error(ex.getMessage());
        log.info("Inside first handler");
        String bodyOfResponse = ex.getMessage();
        return handleExceptionInternal(ex, bodyOfResponse,
                HTTPUtil.getCommonHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public @ResponseBody ResponseEntity<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        log.error(ex.getMessage());
        log.info("Inside second handler");
        EsignError error = new EsignError(HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                String.format("!!! Размер файла не может превышать %s !!!", maxFileSize));
        HttpHeaders headers = new HttpHeaders(HTTPUtil.getCommonHeaders());
        headers.setAccessControlAllowOrigin("*");
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(error, headers, HttpStatus.BAD_REQUEST);
    }

}
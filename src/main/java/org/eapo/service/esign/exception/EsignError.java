package org.eapo.service.esign.exception;

import lombok.Data;

import java.util.Date;

@Data
public class EsignError {
    private int status;
    private String stackTrace;
    private String messageForFront;
    private Date timestamp;

    public EsignError(int status, String stackTrace, String messageForFront) {
        this.status = status;
        this.stackTrace = stackTrace;
        this.messageForFront = messageForFront;
        this.timestamp = new Date();
    }
}

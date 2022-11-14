package org.eapo.service.esign.exception;

import lombok.Data;

import java.util.Date;

@Data
public class EsignError {
    private int status;
    private String message;
    private Date timestamp;

    public EsignError(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = new Date();
    }
}

package org.eapo.service.esign.exception;

public class EsignException extends RuntimeException {

    public EsignException() {
    }

    public EsignException(String s) {
        super(s);
    }

    public EsignException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public EsignException(Throwable throwable) {
        super(throwable);
    }

    public EsignException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}

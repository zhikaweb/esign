package org.eapo.service.esign.model;

import java.io.Serializable;

public class Document implements Serializable {

    private static final String ID_DELIMITER = "-";
    private String idappli;
    private Integer odcorresp;
    private Integer version = 1;


    public static String createId(String idappli, Integer odcorresp) {
        return idappli + ID_DELIMITER + odcorresp;
    }

    public Document(String idappli, Integer odcorresp, byte[] body) {
        this.idappli = idappli;
        this.odcorresp = odcorresp;
        this.body = body;
    }

    public byte[] getBody() {
        return body;
    }

    private byte[] body;

    public String getIdappli() {
        return idappli;
    }

    public void setIdappli(String idappli) {
        this.idappli = idappli;
    }

    public Integer getOdcorresp() {
        return odcorresp;
    }

    public void setOdcorresp(Integer odcorresp) {
        this.odcorresp = odcorresp;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getId() {
        return createId(getIdappli(), getOdcorresp());
    }

}
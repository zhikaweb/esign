package org.eapo.service.esign.service.stamper;

public interface DateStampService {
    byte[] doStamp(byte[] pdf, String date, String doccode);
}

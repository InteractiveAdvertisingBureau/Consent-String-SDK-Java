package com.iab.gdpr.exception;

public class VendorConsentCreateException extends VendorConsentException {
    public VendorConsentCreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public VendorConsentCreateException(String message) {
        super(message);
    }
}

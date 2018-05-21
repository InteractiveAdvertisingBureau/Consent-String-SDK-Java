package com.iab.gdpr.exception;

public class VendorConsentException extends GdprException {
    public VendorConsentException(String message, Throwable cause) {
        super(message, cause);
    }

    public VendorConsentException(String message) {
        super(message);
    }
}

package com.iab.gdpr.exception;

public class VendorConsentParseException extends VendorConsentException {
    public VendorConsentParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public VendorConsentParseException(String message) {
        super(message);
    }
}

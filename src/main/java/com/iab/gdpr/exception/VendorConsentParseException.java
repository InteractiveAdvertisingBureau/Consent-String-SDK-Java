package com.iab.gdpr.exception;

/**
 * Exception for the case where consent string cannot be parsed
 */
public class VendorConsentParseException extends VendorConsentException {

    public VendorConsentParseException(String message) {
        super(message);
    }
}

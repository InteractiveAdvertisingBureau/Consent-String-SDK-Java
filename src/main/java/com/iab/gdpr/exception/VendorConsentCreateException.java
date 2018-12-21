package com.iab.gdpr.exception;

/**
 * Exception for the case where consent string cannot be created
 */
public class VendorConsentCreateException extends VendorConsentException {

    public VendorConsentCreateException(String message) {
        super(message);
    }
}

package com.iab.gdpr.exception;

/**
 * Generic vendor consent exception
 */
public class VendorConsentException extends RuntimeException {

    public VendorConsentException(String message) {
        super(message);
    }
}

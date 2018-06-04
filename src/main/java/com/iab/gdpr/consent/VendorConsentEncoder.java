package com.iab.gdpr.consent;

import java.util.Base64;

/**
 * Encode {@link VendorConsent} to Base64 string
 */
public class VendorConsentEncoder {

    // As per the GDPR framework guidelines padding should be omitted
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

    /**
     * Encode vendor consent to Base64 string
     * @param vendorConsent vendor consent
     * @return Base64 encoded string
     */
    public static String toBase64String(VendorConsent vendorConsent) {
        return ENCODER.encodeToString(vendorConsent.toByteArray());
    }

}

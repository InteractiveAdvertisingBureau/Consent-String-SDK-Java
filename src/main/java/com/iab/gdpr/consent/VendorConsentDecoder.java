package com.iab.gdpr.consent;

import com.iab.gdpr.Bits;
import com.iab.gdpr.consent.implementation.v1.ByteBufferBackedVendorConsent;

import java.util.Base64;

import static com.iab.gdpr.GdprConstants.VERSION_BIT_OFFSET;
import static com.iab.gdpr.GdprConstants.VERSION_BIT_SIZE;

/**
 * {@link VendorConsent} decoder from Base64 string. Right now only version 1 is know, but eventually
 * this can be extended to support new versions
 */
public class VendorConsentDecoder {

    private static final Base64.Decoder BASE64_DECODER = Base64.getUrlDecoder();

    public static VendorConsent fromBase64String(String consentString) {
        if (isNullOrEmpty(consentString))
            throw new IllegalArgumentException("Null or empty consent string passed as an argument");

        return fromByteArray(BASE64_DECODER.decode(consentString));
    }

    public static VendorConsent fromByteArray(byte[] bytes) {
        if (bytes == null || bytes.length == 0)
            throw new IllegalArgumentException("Null or empty consent bytes passed as an argument");

        final Bits bits = new Bits(bytes);
        final int version = getVersion(bits);
        switch (version) {
            case 1:
                return new ByteBufferBackedVendorConsent(bits);
            default:
                throw new IllegalStateException("Unsupported version: " + version);
        }
    }

    /**
     * Get the version field from bitmap
     * @param bits bitmap
     * @return a version number
     */
    private static int getVersion(Bits bits) {
        return bits.getInt(VERSION_BIT_OFFSET, VERSION_BIT_SIZE);
    }

    /**
     * Utility method to check whether string is empty or null
     * @param string value to check
     * @return a boolean value of the check
     */
    private static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }
}

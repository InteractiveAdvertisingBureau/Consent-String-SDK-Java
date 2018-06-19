package com.iab.gdpr.consent;

import com.iab.gdpr.Bits;
import com.iab.gdpr.consent.implementation.v1.ByteBufferBackedVendorConsent;
import org.junit.Test;

import java.util.Base64;

import static com.iab.gdpr.GdprConstants.VERSION_BIT_OFFSET;
import static com.iab.gdpr.GdprConstants.VERSION_BIT_SIZE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class VendorConsentDecoderTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNullConsentString() {
        // Given: null consent string
        String consentString = null;

        // When: decoder is called
        final VendorConsent vendorConsent = VendorConsentDecoder.fromBase64String(consentString);

        // Then IllegalArgumentException exception is thrown
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullConsentBytes() {
        // Given: null consent string
        byte[] consentBytes = null;

        // When: decoder is called
        final VendorConsent vendorConsent = VendorConsentDecoder.fromByteArray(consentBytes);

        // Then IllegalArgumentException exception is thrown
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyConsentString() {
        // Given: empty consent string
        String consentString = "";

        // When: decoder is called
        final VendorConsent vendorConsent = VendorConsentDecoder.fromBase64String(consentString);

        // Then IllegalArgumentException exception is thrown

    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyConsentBytes() {
        // Given: empty consent string
        byte[] consentBytes = new byte[0];

        // When: decoder is called
        final VendorConsent vendorConsent = VendorConsentDecoder.fromByteArray(consentBytes);

        // Then IllegalArgumentException exception is thrown

    }

    @Test(expected = IllegalStateException.class)
    public void testUnknownVersion() {
        // Given: unknown version number in consent string
        final Bits bits = new Bits(new byte[100]);
        bits.setInt(VERSION_BIT_OFFSET, VERSION_BIT_SIZE, 10);

        // When: decoder is called
        final VendorConsent vendorConsent = VendorConsentDecoder.fromBase64String(Base64.getUrlEncoder().withoutPadding().encodeToString(bits.toByteArray()));

        // Then IllegalStateException exception is thrown
    }

    @Test
    public void testVersion1() {
        // Given: version 1 consent string
        final String consentString = "BOOlLqOOOlLqTABABAENAk-AAAAXx7_______9______9uz_Gv_r_f__3nW8_39P3g_7_O3_7m_-zzV48_lrQV1yPAUCgA";

        // When: decoder is called
        final VendorConsent vendorConsent = VendorConsentDecoder.fromBase64String(consentString);

        // Then: v1 ByteBufferVendorConsent is returned
        assertThat(vendorConsent.getClass(),is(ByteBufferBackedVendorConsent.class));

    }

}
package com.iab.gdpr.consent;

import com.iab.gdpr.consent.implementation.v1.ByteBufferBackedVendorConsent;
import com.iab.gdpr.util.Utils;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

public class VendorConsentEncoderTest {

    @Test
    public void testEncode() {
        // Given: vendor consent binary string
        final String binaryString = "000011" + // Version
                "001110001110110011010000101000000000" +  // Created
                "001110001110110011010000101000000000" +  // Updated
                "000000001111"                         +  // CMP ID
                "000000000101"                         +  // CMP version
                "010010"                               +  // Content screen ID
                "000100001101"                         +  // Language code
                "000010010110"                         +  // Vendor list version
                "111110000000001000000001"             +  // Allowed purposes bitmap
                "0000000000100000"                     +  // Max vendor ID
                "0"                                    +  // Bit field encoding
                "10000000000000000000000010000100"        // Vendor bits in bit field
                ;

        // And: ByteBufferBackedVendorConsent constructed from binary string
        ByteBufferBackedVendorConsent vendorConsent = new ByteBufferBackedVendorConsent(Utils.fromBinaryString(binaryString));

        // When: encode is called
        final String base64String = VendorConsentEncoder.toBase64String(vendorConsent);


        // Then: encoded string is returned
        assertThat(vendorConsent.getAllowedPurposesBits(),is(notNullValue()));
    }
}
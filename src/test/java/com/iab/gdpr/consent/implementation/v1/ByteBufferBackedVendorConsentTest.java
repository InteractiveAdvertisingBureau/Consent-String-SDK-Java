package com.iab.gdpr.consent.implementation.v1;

import com.iab.gdpr.consent.VendorConsent;
import com.iab.gdpr.consent.VendorConsentDecoder;
import com.iab.gdpr.exception.VendorConsentParseException;
import com.iab.gdpr.util.Utils;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.iab.gdpr.Purpose.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ByteBufferBackedVendorConsentTest {

    @Test
    public void testVersion() {
        // Given: version field set to 3
        final String binaryString = "000011" + "000000000000";

        // When: object is constructed
        ByteBufferBackedVendorConsent vendorConsent = new ByteBufferBackedVendorConsent(Utils.fromBinaryString(binaryString));

        // Then: correct version is returned
        assertThat(vendorConsent.getVersion(),is(3));
    }

    @Test
    public void testgetConsentRecordCreated() {
        // Given: created date of Monday, June 4, 2018 12:00:00 AM, epoch = 1528070400
        final String binaryString = "000011" + // Version
                "001110001110110011010000101000000000" +   // Created
                "0000";

        // When: object is constructed
        ByteBufferBackedVendorConsent vendorConsent = new ByteBufferBackedVendorConsent(Utils.fromBinaryString(binaryString));

        // Then: correct created timestamp is returned
        assertThat(vendorConsent.getConsentRecordCreated(),is(LocalDateTime.of(2018,6,4,0,0,0).toInstant(ZoneOffset.UTC)));
    }

    @Test
    public void testgetConsentRecordLastUpdated() {
        // Given: updated date of Monday, June 4, 2018 12:00:00 AM, epoch = 1528070400
        final String binaryString = "000011" + // Version
                "001110001110110011010000101000000000" +   // Created
                "001110001110110011010000101000000000" +   // Updated
                "0000";

        // When: object is constructed
        ByteBufferBackedVendorConsent vendorConsent = new ByteBufferBackedVendorConsent(Utils.fromBinaryString(binaryString));

        // Then: correct updated timestamp is returned
        assertThat(vendorConsent.getConsentRecordLastUpdated(),is(LocalDateTime.of(2018,6,4,0,0,0).toInstant(ZoneOffset.UTC)));
    }

    @Test
    public void testgetCmpId() {
        // Given: CMP ID of 15
        final String binaryString = "000011" + // Version
                "001110001110110011010000101000000000" +  // Created
                "001110001110110011010000101000000000" +  // Updated
                "000000001111"                         +  // CMP ID
                "0000";

        // When: object is constructed
        ByteBufferBackedVendorConsent vendorConsent = new ByteBufferBackedVendorConsent(Utils.fromBinaryString(binaryString));

        // Then: correct cmp ID is returned
        assertThat(vendorConsent.getCmpId(),is(15));
    }

    @Test
    public void testgetCmpVersion() {
        // Given: CMP version of 5
        final String binaryString = "000011" + // Version
                "001110001110110011010000101000000000" +  // Created
                "001110001110110011010000101000000000" +  // Updated
                "000000001111"                         +  // CMP ID
                "000000000101"                         +  // CMP version
                "0000";

        // When: object is constructed
        ByteBufferBackedVendorConsent vendorConsent = new ByteBufferBackedVendorConsent(Utils.fromBinaryString(binaryString));

        // Then: correct cmp version is returned
        assertThat(vendorConsent.getCmpVersion(),is(5));
    }

    @Test
    public void testgetConsentScreen() {
        // Given: content screen ID of 18
        final String binaryString = "000011" + // Version
                "001110001110110011010000101000000000" +  // Created
                "001110001110110011010000101000000000" +  // Updated
                "000000001111"                         +  // CMP ID
                "000000000101"                         +  // CMP version
                "010010"                               +  // Content screen ID
                "0000";

        // When: object is constructed
        ByteBufferBackedVendorConsent vendorConsent = new ByteBufferBackedVendorConsent(Utils.fromBinaryString(binaryString));

        // Then: correct content screen ID is returned
        assertThat(vendorConsent.getConsentScreen(),is(18));
    }

    @Test
    public void testgetConsentLanguage() {
        // Given: language code of EN
        final String binaryString = "000011" + // Version
                "001110001110110011010000101000000000" +  // Created
                "001110001110110011010000101000000000" +  // Updated
                "000000001111"                         +  // CMP ID
                "000000000101"                         +  // CMP version
                "010010"                               +  // Content screen ID
                "000100001101"                         +  // Language code
                "0000";

        // When: object is constructed
        ByteBufferBackedVendorConsent vendorConsent = new ByteBufferBackedVendorConsent(Utils.fromBinaryString(binaryString));

        // Then: correct language code is returned
        assertThat(vendorConsent.getConsentLanguage(),is("EN"));
    }

    @Test
    public void testgetVendorListVersion() {
        // Given: vendor list version of 150
        final String binaryString = "000011" + // Version
                "001110001110110011010000101000000000" +  // Created
                "001110001110110011010000101000000000" +  // Updated
                "000000001111"                         +  // CMP ID
                "000000000101"                         +  // CMP version
                "010010"                               +  // Content screen ID
                "000100001101"                         +  // Language code
                "000010010110"                         +  // vendor list version
                "0000";

        // When: object is constructed
        ByteBufferBackedVendorConsent vendorConsent = new ByteBufferBackedVendorConsent(Utils.fromBinaryString(binaryString));

        // Then: correct vendor list version is returned
        assertThat(vendorConsent.getVendorListVersion(),is(150));
    }

    @Test
    public void testgetAllowedPurposes() {
        // Given: allowed purposes of 1,2,3,4,5,15,24
        final String binaryString = "000011" + // Version
                "001110001110110011010000101000000000" +  // Created
                "001110001110110011010000101000000000" +  // Updated
                "000000001111"                         +  // CMP ID
                "000000000101"                         +  // CMP version
                "010010"                               +  // Content screen ID
                "000100001101"                         +  // Language code
                "000010010110"                         +  // Vendor list version
                "111110000000001000000001"             +  // Allowed purposes bitmap
                "0000";

        // When: object is constructed
        ByteBufferBackedVendorConsent vendorConsent = new ByteBufferBackedVendorConsent(Utils.fromBinaryString(binaryString));

        // Then: correct allowed versions are returned
        assertThat(vendorConsent.getAllowedPurposeIds(),is(new HashSet<>(Arrays.asList(1,2,3,4,5,15,24))));
        assertThat(vendorConsent.getAllowedPurposes(),is(new HashSet<>(Arrays.asList(STORAGE_AND_ACCESS,PERSONALIZATION,AD_SELECTION,CONTENT_DELIVERY,MEASUREMENT,UNDEFINED))));
        assertThat(vendorConsent.getAllowedPurposesBits(),is(16253441));
        assertTrue(vendorConsent.isPurposeAllowed(1));
        assertTrue(vendorConsent.isPurposeAllowed(STORAGE_AND_ACCESS));
        assertTrue(vendorConsent.isPurposeAllowed(2));
        assertTrue(vendorConsent.isPurposeAllowed(PERSONALIZATION));
        assertTrue(vendorConsent.isPurposeAllowed(3));
        assertTrue(vendorConsent.isPurposeAllowed(AD_SELECTION));
        assertTrue(vendorConsent.isPurposeAllowed(4));
        assertTrue(vendorConsent.isPurposeAllowed(CONTENT_DELIVERY));
        assertTrue(vendorConsent.isPurposeAllowed(5));
        assertTrue(vendorConsent.isPurposeAllowed(MEASUREMENT));
        assertTrue(vendorConsent.isPurposeAllowed(15));
        assertTrue(vendorConsent.isPurposeAllowed(24));
    }

    @Test
    public void testgetMaxVendorId() {
        // Given: max vendor ID of 382
        final String binaryString = "000011" + // Version
                "001110001110110011010000101000000000" +  // Created
                "001110001110110011010000101000000000" +  // Updated
                "000000001111"                         +  // CMP ID
                "000000000101"                         +  // CMP version
                "010010"                               +  // Content screen ID
                "000100001101"                         +  // Language code
                "000010010110"                         +  // Vendor list version
                "111110000000001000000001"             +  // Allowed purposes bitmap
                "0000000101111110"                     +  // Max vendor ID
                "0000";

        // When: object is constructed
        ByteBufferBackedVendorConsent vendorConsent = new ByteBufferBackedVendorConsent(Utils.fromBinaryString(binaryString));

        // Then: correct max vendor ID is returned
        assertThat(vendorConsent.getMaxVendorId(),is(382));
    }

    @Test
    public void testBitFieldEncoding() {
        // Given: vendors 1,25 and 30 in bit field, with max vendor ID of 32
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

        // When: object is constructed
        ByteBufferBackedVendorConsent vendorConsent = new ByteBufferBackedVendorConsent(Utils.fromBinaryString(binaryString));

        // Then: correct vendor IDs are allowed
        assertTrue(vendorConsent.isVendorAllowed(1));
        assertTrue(vendorConsent.isVendorAllowed(25));
        assertTrue(vendorConsent.isVendorAllowed(30));
        assertThat(vendorConsent.getAllowedVendorIds(), is(new HashSet<>(Arrays.asList(1, 25, 30))));

        assertFalse(vendorConsent.isVendorAllowed(2));
        assertFalse(vendorConsent.isVendorAllowed(3));
        assertFalse(vendorConsent.isVendorAllowed(31));
        assertFalse(vendorConsent.isVendorAllowed(32));

        // Vendors outside range [1, MaxVendorId] are not allowed
        assertFalse(vendorConsent.isVendorAllowed(-99));
        assertFalse(vendorConsent.isVendorAllowed(-1));
        assertFalse(vendorConsent.isVendorAllowed(0));
        assertFalse(vendorConsent.isVendorAllowed(33));
        assertFalse(vendorConsent.isVendorAllowed(34));
        assertFalse(vendorConsent.isVendorAllowed(99));
    }

    @Test
    public void testRangeEncodingDefaultFalse() {
        // Given: vendors 1-25 and 30 with consent, with max vendor ID of 32
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
                "1"                                    +  // Range encoding
                "0"                                    +  // Default 0=No Consent
                "000000000010"                         +  // Number of entries = 2
                "1"                                    +  // First entry range = 1
                "0000000000000001"                     +  // First entry from = 1
                "0000000000011001"                     +  // First entry to = 25
                "0"                                    +  // Second entry single = 0
                "0000000000011110"                        // Second entry value = 30
                ;

        // When: object is constructed
        ByteBufferBackedVendorConsent vendorConsent = new ByteBufferBackedVendorConsent(Utils.fromBinaryString(binaryString));

        // Then: correct vendor IDs are allowed
        assertTrue(vendorConsent.isVendorAllowed(1));
        assertTrue(vendorConsent.isVendorAllowed(10));
        assertTrue(vendorConsent.isVendorAllowed(25));
        assertTrue(vendorConsent.isVendorAllowed(30));

        Set<Integer> expectedVendorIds = IntStream
                .concat(IntStream.rangeClosed(1, 25), IntStream.of(30))
                .boxed()
                .collect(Collectors.toSet());
        assertThat(vendorConsent.getAllowedVendorIds(), is(expectedVendorIds));

        assertFalse(vendorConsent.isVendorAllowed(26));
        assertFalse(vendorConsent.isVendorAllowed(28));
        assertFalse(vendorConsent.isVendorAllowed(31));
        assertFalse(vendorConsent.isVendorAllowed(32));

        // Vendors outside range [1, MaxVendorId] are not allowed
        assertFalse(vendorConsent.isVendorAllowed(-99));
        assertFalse(vendorConsent.isVendorAllowed(-1));
        assertFalse(vendorConsent.isVendorAllowed(0));
        assertFalse(vendorConsent.isVendorAllowed(33));
        assertFalse(vendorConsent.isVendorAllowed(34));
        assertFalse(vendorConsent.isVendorAllowed(99));
    }

    @Test
    public void testRangeEncodingDefaultTrue() {
        // Given: vendors 1 and 25-30 without consent, with max vendor ID of 32
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
                "1"                                    +  // Range encoding
                "1"                                    +  // Default 1=Consent
                "000000000010"                         +  // Number of entries = 2
                "0"                                    +  // First entry single = 0
                "0000000000000001"                     +  // First entry value = 1
                "1"                                    +  // Second entry range = 1
                "0000000000011001"                     +  // Second entry from = 25
                "0000000000011110"                        // Second entry to = 30
                ;

        // When: object is constructed
        ByteBufferBackedVendorConsent vendorConsent = new ByteBufferBackedVendorConsent(Utils.fromBinaryString(binaryString));

        // Then: correct vendor IDs are allowed
        assertFalse(vendorConsent.isVendorAllowed(1));
        assertFalse(vendorConsent.isVendorAllowed(25));
        assertFalse(vendorConsent.isVendorAllowed(27));
        assertFalse(vendorConsent.isVendorAllowed(30));

        assertTrue(vendorConsent.isVendorAllowed(2));
        assertTrue(vendorConsent.isVendorAllowed(15));
        assertTrue(vendorConsent.isVendorAllowed(31));
        assertTrue(vendorConsent.isVendorAllowed(32));

        Set<Integer> expectedVendorIds = IntStream
                .concat(IntStream.rangeClosed(2, 24), IntStream.rangeClosed(31, 32))
                .boxed()
                .collect(Collectors.toSet());
        assertThat(vendorConsent.getAllowedVendorIds(), is(expectedVendorIds));

        // Vendors outside range [1, MaxVendorId] are not allowed
        assertFalse(vendorConsent.isVendorAllowed(-99));
        assertFalse(vendorConsent.isVendorAllowed(-1));
        assertFalse(vendorConsent.isVendorAllowed(0));
        assertFalse(vendorConsent.isVendorAllowed(33));
        assertFalse(vendorConsent.isVendorAllowed(34));
        assertFalse(vendorConsent.isVendorAllowed(99));
    }

    @Test(expected = VendorConsentParseException.class)
    public void testInvalidVendorId1() {
        // Given: invalid vendor ID in range
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
                "1"                                    +  // Range encoding
                "1"                                    +  // Default 1=Consent
                "000000000010"                         +  // Number of entries = 2
                "0"                                    +  // First entry single = 0
                "0000000000000001"                     +  // First entry value = 1
                "1"                                    +  // Second entry range = 1
                "0000000000101000"                     +  // Second entry from = 40 - INVALID
                "0000000000011110"                        // Second entry to = 30
                ;

        // When: object is constructed
        ByteBufferBackedVendorConsent vendorConsent = new ByteBufferBackedVendorConsent(Utils.fromBinaryString(binaryString));

        // And: vendor check is performed
        assertTrue(vendorConsent.isVendorAllowed(32));

        // Then: exception is raised
    }

    @Test(expected = VendorConsentParseException.class)
    public void testInvalidVendorId2() {
        // Given: invalid vendor ID in range
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
                "1"                                    +  // Range encoding
                "1"                                    +  // Default 1=Consent
                "000000000010"                         +  // Number of entries = 2
                "0"                                    +  // First entry single = 0
                "0000000000101000"                     +  // First entry value = 40 - INVALID
                "1"                                    +  // Second entry range = 1
                "0000000000011001"                     +  // Second entry from = 25
                "0000000000011110"                        // Second entry to = 30
                ;

        // When: object is constructed
        ByteBufferBackedVendorConsent vendorConsent = new ByteBufferBackedVendorConsent(Utils.fromBinaryString(binaryString));

        // And: vendor check is performed
        assertTrue(vendorConsent.isVendorAllowed(32));

        // Then: exception is raised
    }

    @Test(expected = VendorConsentParseException.class)
    public void testInvalidVendorId3() {
        // Given: invalid vendor ID in range
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
                "1"                                    +  // Range encoding
                "1"                                    +  // Default 1=Consent
                "000000000010"                         +  // Number of entries = 2
                "0"                                    +  // First entry single = 0
                "0000000000101000"                     +  // First entry value = 40 - INVALID
                "1"                                    +  // Second entry range = 1
                "0000000000011001"                     +  // Second entry from = 25
                "0000000000011110"                        // Second entry to = 30
                ;

        // When: object is constructed
        ByteBufferBackedVendorConsent vendorConsent = new ByteBufferBackedVendorConsent(Utils.fromBinaryString(binaryString));

        // And: allowed vendor IDs are obtained
        vendorConsent.getAllowedVendorIds();

        // Then: exception is raised
    }

    @Test(expected = VendorConsentParseException.class)
    public void testInvalidVendorId4() {
        // Given: invalid vendor ID in range
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
                "1"                                    +  // Range encoding
                "1"                                    +  // Default 1=Consent
                "000000000010"                         +  // Number of entries = 2
                "0"                                    +  // First entry single = 0
                "0000000000101000"                     +  // First entry value = 40 - INVALID
                "1"                                    +  // Second entry range = 1
                "0000000000011001"                     +  // Second entry from = 25
                "0000000000011110"                        // Second entry to = 30
                ;

        // When: object is constructed
        ByteBufferBackedVendorConsent vendorConsent = new ByteBufferBackedVendorConsent(Utils.fromBinaryString(binaryString));

        // And: allowed vendor IDs are obtained
        vendorConsent.getAllowedVendorIds();

        // Then: exception is raised
    }
    /*
        Below are tests for encoded strings from previous version of the code
     */

    @Test
    public void testRealString1() {
        // Given: known vendor consent string
        final String consentString = "BOOlLqOOOlLqTABABAENAk-AAAAXx7_______9______9uz_Gv_r_f__3nW8_39P3g_7_O3_7m_-zzV48_lrQV1yPAUCgA";

        // When: vendor consent is constructed
        final VendorConsent vendorConsent = VendorConsentDecoder.fromBase64String(consentString);

        // Then: values match expectation
        assertEquals(380, vendorConsent.getMaxVendorId());
        assertTrue(vendorConsent.isVendorAllowed(380));
        assertFalse(vendorConsent.isVendorAllowed(379));

        Set<Integer> expectedVendorIds = new HashSet<>(Arrays.asList(1, 2, 3, 4, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
            17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43,
            44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71,
            72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 97, 98, 100,
            101, 102, 104, 105, 108, 109, 110, 111, 112, 113, 114, 115, 119, 120, 122, 124, 125, 126, 127, 128, 129,
            130, 131, 132, 133, 134, 136, 138, 139, 140, 141, 142, 143, 144, 145, 147, 148, 149, 150, 151, 152, 153,
            154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 167, 168, 169, 170, 173, 174, 175, 177, 179,
            180, 182, 183, 184, 185, 188, 189, 190, 191, 192, 193, 194, 195, 197, 198, 199, 200, 201, 202, 203, 205,
            208, 209, 210, 211, 212, 213, 215, 216, 217, 218, 224, 225, 226, 227, 228, 229, 230, 231, 232, 234, 235,
            236, 237, 238, 239, 240, 241, 244, 245, 246, 248, 249, 251, 252, 253, 254, 255, 256, 257, 258, 259, 260,
            261, 262, 264, 265, 266, 269, 270, 272, 273, 274, 275, 276, 277, 278, 279, 280, 281, 282, 284, 285, 288,
            289, 290, 291, 294, 295, 297, 299, 301, 302, 303, 304, 308, 309, 310, 311, 314, 315, 316, 317, 318, 319,
            320, 323, 325, 326, 328, 330, 331, 333, 339, 341, 343, 344, 345, 347, 349, 350, 351, 354, 358, 359, 360,
            361, 369, 371, 378, 380));
        assertThat(vendorConsent.getAllowedVendorIds(), is(expectedVendorIds));
    }

    @Test
    public void testRealString2() {
        // Given: known vendor consent string
        final String consentString = "BN5lERiOMYEdiAOAWeFRAAYAAaAAptQ";

        // When: vendor consent is constructed
        final VendorConsent vendorConsent = VendorConsentDecoder.fromBase64String(consentString);

        // Then: values match expectation
        assertThat(vendorConsent.getCmpId(), is(14));
        assertThat(vendorConsent.getCmpVersion(), is(22));
        assertThat(vendorConsent.getConsentLanguage(), is("FR"));
        assertThat(vendorConsent.getConsentRecordCreated(), is(Instant.ofEpochMilli(14924661858L * 100)));
        assertThat(vendorConsent.getConsentRecordLastUpdated(), is(Instant.ofEpochMilli(15240021858L * 100)));
        assertThat(vendorConsent.getAllowedPurposeIds().size(), is(5));
        assertThat(vendorConsent.getAllowedPurposesBits(), is(6291482));

        assertTrue(vendorConsent.isPurposeAllowed(2));
        assertFalse(vendorConsent.isPurposeAllowed(1));
        assertTrue(vendorConsent.isPurposeAllowed(21));
        assertTrue(vendorConsent.isVendorAllowed(1));
        assertTrue(vendorConsent.isVendorAllowed(5));
        assertTrue(vendorConsent.isVendorAllowed(7));
        assertTrue(vendorConsent.isVendorAllowed(9));
        assertFalse(vendorConsent.isVendorAllowed(0));
        assertFalse(vendorConsent.isVendorAllowed(10));

        Set<Integer> expectedVendorIds = new HashSet<>(Arrays.asList(1, 2, 4, 5, 7, 9));
        assertThat(vendorConsent.getAllowedVendorIds(), is(expectedVendorIds));
    }

    @Test
    public void testRealString3() {
        // Given: known vendor consent string
        final String consentString = "BN5lERiOMYEdiAKAWXEND1HoSBE6CAFAApAMgBkIDIgM0AgOJxAnQA";

        // When: vendor consent is constructed
        final VendorConsent vendorConsent = VendorConsentDecoder.fromBase64String(consentString);

        // Then: values match expectation
        assertThat(vendorConsent.getCmpId(), is(10));
        assertThat(vendorConsent.getCmpVersion(), is(22));
        assertThat(vendorConsent.getConsentLanguage(), is("EN"));
        assertThat(vendorConsent.getConsentRecordCreated(), is(Instant.ofEpochMilli(14924661858L * 100)));
        assertThat(vendorConsent.getConsentRecordLastUpdated(), is(Instant.ofEpochMilli(15240021858L * 100)));
        assertThat(vendorConsent.getAllowedPurposeIds().size(), is(8));
        assertThat(vendorConsent.getAllowedPurposesBits(), is(2000001));

        assertTrue(vendorConsent.isPurposeAllowed(4));
        assertFalse(vendorConsent.isPurposeAllowed(1));
        assertTrue(vendorConsent.isPurposeAllowed(24));
        assertFalse(vendorConsent.isPurposeAllowed(25));
        assertFalse(vendorConsent.isPurposeAllowed(0));
        assertFalse(vendorConsent.isVendorAllowed(1));
        assertFalse(vendorConsent.isVendorAllowed(3));
        assertTrue(vendorConsent.isVendorAllowed(225));
        assertTrue(vendorConsent.isVendorAllowed(5000));
        assertTrue(vendorConsent.isVendorAllowed(515));
        assertFalse(vendorConsent.isVendorAllowed(0));
        assertFalse(vendorConsent.isVendorAllowed(411));
        assertFalse(vendorConsent.isVendorAllowed(3244));

        Set<Integer> expectedVendorIds = IntStream.concat(IntStream.of(20), IntStream.rangeClosed(200, 410))
                .boxed()
                .collect(Collectors.toSet());
        expectedVendorIds.add(515);
        expectedVendorIds.addAll(IntStream.rangeClosed(5000, 5024).boxed().collect(Collectors.toSet()));
        assertThat(vendorConsent.getAllowedVendorIds(), is(expectedVendorIds));
    }

    @Test
    public void testRealString4() {
        // Given: known vendor consent string
        final String consentString = "BOOMzbgOOQww_AtABAFRAb-AAAsvOA3gACAAkABgArgBaAF0AMAA1gBuAH8AQQBSgCoAL8AYQBigDIAM0AaABpgDYAOYAdgA8AB6gD4AQoAiABFQCMAI6ASABIgCTAEqAJeATIBQQCiAKSAU4BVQCtAK-AWYBaQC2ALcAXMAvAC-gGAAYcAxQDGAGQAMsAZsA0ADTAGqANcAbMA4ADjAHKAOiAdQB1gDtgHgAeMA9AD2AHzAP4BAACBAEEAIbAREBEgCKQEXARhZeYA";

        // When: vendor consent is constructed
        final VendorConsent vendorConsent = VendorConsentDecoder.fromBase64String(consentString);

        // Then: values match expectation
        assertThat(vendorConsent.getCmpId(), is(45));
        assertThat(vendorConsent.getCmpVersion(), is(1));
        assertThat(vendorConsent.getConsentLanguage(), is("FR"));
        assertThat(vendorConsent.getConsentRecordCreated(), is(Instant.ofEpochMilli(15270622944L * 100)));
        assertThat(vendorConsent.getConsentRecordLastUpdated(), is(Instant.ofEpochMilli(15271660607L * 100)));
        assertThat(vendorConsent.getAllowedPurposeIds().size(), is(5));

        assertTrue(vendorConsent.isPurposeAllowed(1));
        assertTrue(vendorConsent.isPurposeAllowed(2));
        assertTrue(vendorConsent.isPurposeAllowed(3));
        assertTrue(vendorConsent.isPurposeAllowed(4));
        assertTrue(vendorConsent.isPurposeAllowed(5));
        assertFalse(vendorConsent.isPurposeAllowed(6));
        assertFalse(vendorConsent.isPurposeAllowed(25));
        assertFalse(vendorConsent.isPurposeAllowed(0));
        assertTrue(vendorConsent.isVendorAllowed(1));
        assertFalse(vendorConsent.isVendorAllowed(5));
        assertTrue(vendorConsent.isVendorAllowed(45));
        assertFalse(vendorConsent.isVendorAllowed(47));
        assertFalse(vendorConsent.isVendorAllowed(146));
        assertTrue(vendorConsent.isVendorAllowed(147));

        Set<Integer> expectedVendorIds = new HashSet<>(Arrays.asList(1, 2, 3, 4, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
            17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43,
            45, 46, 48, 49, 50, 51, 52, 53, 55, 56, 57, 58, 59, 60, 61, 62, 63, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74,
            75, 76, 77, 78, 79, 80, 81, 82, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 97, 98, 100, 101, 102, 104,
            105, 108, 109, 110, 111, 112, 113, 114, 115, 118, 120, 122, 124, 125, 126, 127, 128, 129, 130, 131, 132,
            133, 136, 138, 140, 141, 142, 144, 145, 147, 149, 151, 153, 154, 155, 156, 157, 158, 159, 160, 162, 163,
            164, 167, 168, 169, 170, 173, 174, 175, 179, 180, 182, 183, 185, 188, 189, 190, 192, 193, 194, 195, 197,
            198, 200, 203, 205, 208, 209, 210, 211, 213, 215, 217, 224, 225, 226, 227, 229, 232, 234, 235, 237, 240,
            241, 244, 245, 246, 249, 254, 255, 256, 258, 260, 269, 273, 274, 276, 279, 280, 45811));
        assertThat(vendorConsent.getAllowedVendorIds(), is(expectedVendorIds));
    }

    @Test
    public void testRealString5() {
        // Given: known vendor consent string
        final String consentString = "BONZt-1ONZt-1AHABBENAO-AAAAHCAEAASABmADYAOAAeA";

        // When: vendor vendorConsent is constructed
        final VendorConsent vendorConsent = VendorConsentDecoder.fromBase64String(consentString);

        // Then: values match expectation
        assertTrue(vendorConsent.isPurposeAllowed(1));
        assertTrue(vendorConsent.isPurposeAllowed(3));
        assertTrue(vendorConsent.isVendorAllowed(28));
        assertFalse(vendorConsent.isVendorAllowed(1));
        assertFalse(vendorConsent.isVendorAllowed(3));
        assertTrue(vendorConsent.isVendorAllowed(27));

        Set<Integer> expectedVendorIds = new HashSet<>(Arrays.asList(9, 25, 27, 28, 30));
        assertThat(vendorConsent.getAllowedVendorIds(), is(expectedVendorIds));
    }

    @Test
    public void testRealString6() {
        // Given: known vendor consent string
        final String consentString = "BOOj_adOOj_adABABADEAb-AAAA-iATAAUAA2ADAAMgAgABIAC0AGQANAAcAA-ACKAEwAKIAaABFACQAHIAP0B9A";

        // When: vendor vendorConsent is constructed
        final VendorConsent vendorConsent = VendorConsentDecoder.fromBase64String(consentString);

        // Then: values match expectation
        assertThat(vendorConsent.getVersion(),is(1));
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        assertThat(vendorConsent.getConsentRecordCreated(),is(LocalDateTime.parse("2018-05-30 08:48:54.100",formatter).toInstant(ZoneOffset.UTC)));
        assertThat(vendorConsent.getConsentRecordLastUpdated(),is(LocalDateTime.parse("2018-05-30 08:48:54.100",formatter).toInstant(ZoneOffset.UTC)));
        assertThat(vendorConsent.getCmpId(),is(1));
        assertThat(vendorConsent.getCmpVersion(),is(1));
        assertThat(vendorConsent.getConsentScreen(),is(0));
        assertThat(vendorConsent.getConsentLanguage(),is("DE"));
        assertThat(vendorConsent.getAllowedPurposeIds(),is(new HashSet<>(Arrays.asList(1,2,3,4,5))));
        assertThat(vendorConsent.getMaxVendorId(),is(1000));
        assertTrue(vendorConsent.isVendorAllowed(10));
        assertTrue(vendorConsent.isVendorAllowed(13));
        assertTrue(vendorConsent.isVendorAllowed(24));
        assertTrue(vendorConsent.isVendorAllowed(25));
        assertTrue(vendorConsent.isVendorAllowed(32));
        assertTrue(vendorConsent.isVendorAllowed(36));
        assertTrue(vendorConsent.isVendorAllowed(45));
        assertTrue(vendorConsent.isVendorAllowed(50));
        assertTrue(vendorConsent.isVendorAllowed(52));
        assertTrue(vendorConsent.isVendorAllowed(56));
        assertTrue(vendorConsent.isVendorAllowed(62));
        assertTrue(vendorConsent.isVendorAllowed(69));
        assertTrue(vendorConsent.isVendorAllowed(76));
        assertTrue(vendorConsent.isVendorAllowed(81));
        assertTrue(vendorConsent.isVendorAllowed(104));
        assertTrue(vendorConsent.isVendorAllowed(138));
        assertTrue(vendorConsent.isVendorAllowed(144));
        assertTrue(vendorConsent.isVendorAllowed(228));
        assertTrue(vendorConsent.isVendorAllowed(253));
        assertTrue(vendorConsent.isVendorAllowed(1000));

        Set<Integer> expectedVendorIds = new HashSet<>(
                Arrays.asList(10, 13, 24, 25, 32, 36, 45, 50, 52, 56, 62, 69, 76, 81, 104, 138, 144, 228, 253, 1000));
        assertThat(vendorConsent.getAllowedVendorIds(), is(expectedVendorIds));
    }

    @Test(expected = VendorConsentParseException.class)
    public void testCorruptString() {
        final String corruptConsentString = "BOUy_skOUy_skABABBENA8-AAAAbN7"; // Cut short at 22 bytes

        // When: decoder is called
        final VendorConsent vendorConsent = VendorConsentDecoder.fromBase64String(corruptConsentString);

        vendorConsent.isVendorAllowed(10);
        fail("VendorConsentParseException expected");
    }

}

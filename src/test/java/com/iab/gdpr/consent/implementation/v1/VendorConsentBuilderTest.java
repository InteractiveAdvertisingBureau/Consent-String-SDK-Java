package com.iab.gdpr.consent.implementation.v1;

import com.iab.gdpr.Purpose;
import com.iab.gdpr.consent.range.RangeEntry;
import com.iab.gdpr.consent.range.SingleRangeEntry;
import com.iab.gdpr.consent.range.StartEndRangeEntry;
import com.iab.gdpr.consent.VendorConsent;
import com.iab.gdpr.exception.VendorConsentCreateException;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.iab.gdpr.Purpose.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class VendorConsentBuilderTest {

    private Instant now;

    @Before
    public void setUp() {
        now = LocalDateTime.now().withNano(0).toInstant(ZoneOffset.UTC);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPurpose() {
        // Given: invalid purpose ID in the set
        final Set<Integer> allowedPurposes = new HashSet<>(Arrays.asList(1, 2, 3, 99));

        // When: passing set to the builder
        new VendorConsentBuilder().withAllowedPurposeIds(allowedPurposes);

        // Then: exception is thrown
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidVendorEncodingType() {
        // Given: invalid vendor encoding type
        int vendorEncodingType = 3;

        // When: passing vendor type to builder
        new VendorConsentBuilder().withVendorEncodingType(vendorEncodingType);

        // Then: exception is thrown
    }

    @Test(expected = VendorConsentCreateException.class)
    public void testInvalidVendorListVersion() {
        // Given: invalid vendor list version - 50
        int vendorListVersion = -50;

        // When: trying to build using invalid value
        final VendorConsent vendorConsent = new VendorConsentBuilder()
                .withConsentRecordCreatedOn(now)
                .withConsentRecordLastUpdatedOn(now)
                .withConsentLanguage("EN")
                .withVendorListVersion(vendorListVersion)
                .build();

        // Then: exception is thrown
    }

    @Test(expected = VendorConsentCreateException.class)
    public void testInvalidMaxVendorId() {
        // Given: invalid max vendor ID = -1
        int maxVendorId = -1;

        // When: trying to build using invalid value
        final VendorConsent vendorConsent = new VendorConsentBuilder()
                .withConsentRecordCreatedOn(now)
                .withConsentRecordLastUpdatedOn(now)
                .withConsentLanguage("EN")
                .withVendorListVersion(10)
                .withMaxVendorId(maxVendorId)
                .build();

        // Then: exception is thrown
    }

    @Test(expected = VendorConsentCreateException.class)
    public void testInvalidRangeEntry() {
        // Given: max vendor ID of 300;
        int maxVendorId = 300;

        // And: list of range entries with values that are > max vendor ID
        final List<RangeEntry> rangeEntries = Arrays.asList(
                new SingleRangeEntry(1),
                new StartEndRangeEntry(100,400)
        );

        // When: trying to build using invalid value
        final VendorConsent vendorConsent = new VendorConsentBuilder()
                .withConsentRecordCreatedOn(now)
                .withConsentRecordLastUpdatedOn(now)
                .withConsentLanguage("EN")
                .withVendorListVersion(10)
                .withMaxVendorId(maxVendorId)
                .withVendorEncodingType(1)
                .withRangeEntries(rangeEntries)
                .build();

        // Then: exception is thrown
    }

    @Test
    public void testValidBidFieldEncoding() {
        // Given: set of consent string parameters
        final Set<Integer> allowedPurposes = new HashSet<>(Arrays.asList(1, 2, 3, 24));
        final int cmpId = 1;
        final int cmpVersion = 1;
        final int consentScreenID = 1;
        final String consentLanguage = "EN";
        final int vendorListVersion = 10;
        final int maxVendorId = 180;
        final int vendorEncodingType = 0; // Bit field
        final Set<Integer> allowedVendors = new HashSet<>(Arrays.asList(1, 10, 180));

        // When: vendor consent is build
        final VendorConsent vendorConsent = new VendorConsentBuilder()
                .withConsentRecordCreatedOn(now)
                .withConsentRecordLastUpdatedOn(now)
                .withCmpID(cmpId)
                .withCmpVersion(cmpVersion)
                .withConsentScreenID(consentScreenID)
                .withConsentLanguage(consentLanguage)
                .withVendorListVersion(vendorListVersion)
                .withAllowedPurposeIds(allowedPurposes)
                .withMaxVendorId(maxVendorId)
                .withVendorEncodingType(vendorEncodingType)
                .withBitField(allowedVendors)
                .build();


        // Then: values in vendor consent match parameters
        assertThat(vendorConsent.getVersion(),is(1));
        assertThat(vendorConsent.getConsentRecordCreated(),is(now));
        assertThat(vendorConsent.getConsentRecordLastUpdated(),is(now));
        assertThat(vendorConsent.getCmpId(),is(cmpId));
        assertThat(vendorConsent.getCmpVersion(),is(cmpVersion));
        assertThat(vendorConsent.getConsentScreen(),is(consentScreenID));
        assertThat(vendorConsent.getVendorListVersion(),is(vendorListVersion));
        assertThat(vendorConsent.getAllowedPurposeIds(),is(allowedPurposes));
        assertThat(vendorConsent.getMaxVendorId(),is(maxVendorId));

        assertTrue(vendorConsent.isPurposeAllowed(1));
        assertTrue(vendorConsent.isPurposeAllowed(2));
        assertTrue(vendorConsent.isPurposeAllowed(3));
        assertTrue(vendorConsent.isPurposeAllowed(24));

        assertTrue(vendorConsent.isVendorAllowed(1));
        assertTrue(vendorConsent.isVendorAllowed(10));
        assertTrue(vendorConsent.isVendorAllowed(180));

        assertFalse(vendorConsent.isPurposeAllowed(4));
        assertFalse(vendorConsent.isPurposeAllowed(5));

        assertFalse(vendorConsent.isVendorAllowed(5));
        assertFalse(vendorConsent.isVendorAllowed(11));
        assertFalse(vendorConsent.isVendorAllowed(179));
    }

    @Test
    public void testValidRangedEncoding() {
        // Given: set of consent string parameters
        final Set<Purpose> allowedPurposes = new HashSet<>(Arrays.asList(STORAGE_AND_ACCESS, PERSONALIZATION, AD_SELECTION, CONTENT_DELIVERY, MEASUREMENT));
        final int cmpId = 10;
        final int cmpVersion = 3;
        final int consentScreenID = 4;
        final String consentLanguage = "FR";
        final int vendorListVersion = 231;
        final int maxVendorId = 400;
        final int vendorEncodingType = 1;
        final List<RangeEntry> rangeEntries = Arrays.asList(
                new SingleRangeEntry(10),
                new StartEndRangeEntry(100,200),
                new SingleRangeEntry(350)
        );

        // When: vendor consent is build
        final VendorConsent vendorConsent = new VendorConsentBuilder()
                .withConsentRecordCreatedOn(now)
                .withConsentRecordLastUpdatedOn(now)
                .withCmpID(cmpId)
                .withCmpVersion(cmpVersion)
                .withConsentScreenID(consentScreenID)
                .withConsentLanguage(consentLanguage)
                .withVendorListVersion(vendorListVersion)
                .withAllowedPurposes(allowedPurposes)
                .withMaxVendorId(maxVendorId)
                .withVendorEncodingType(vendorEncodingType)
                .withDefaultConsent(false)
                .withRangeEntries(rangeEntries)
                .build();


        // Then: values in vendor consent match parameters
        assertThat(vendorConsent.getVersion(),is(1));
        assertThat(vendorConsent.getConsentRecordCreated(),is(now));
        assertThat(vendorConsent.getConsentRecordLastUpdated(),is(now));
        assertThat(vendorConsent.getCmpId(),is(cmpId));
        assertThat(vendorConsent.getCmpVersion(),is(cmpVersion));
        assertThat(vendorConsent.getConsentScreen(),is(consentScreenID));
        assertThat(vendorConsent.getVendorListVersion(),is(vendorListVersion));
        assertThat(vendorConsent.getAllowedPurposes(),is(allowedPurposes));
        assertThat(vendorConsent.getMaxVendorId(),is(maxVendorId));

        assertTrue(vendorConsent.isPurposeAllowed(STORAGE_AND_ACCESS));
        assertTrue(vendorConsent.isPurposeAllowed(PERSONALIZATION));
        assertTrue(vendorConsent.isPurposeAllowed(AD_SELECTION));
        assertTrue(vendorConsent.isPurposeAllowed(CONTENT_DELIVERY));
        assertTrue(vendorConsent.isPurposeAllowed(MEASUREMENT));

        assertTrue(vendorConsent.isVendorAllowed(10));
        assertTrue(vendorConsent.isVendorAllowed(150));
        assertTrue(vendorConsent.isVendorAllowed(350));

        assertFalse(vendorConsent.isVendorAllowed(50));
        assertFalse(vendorConsent.isVendorAllowed(240));
    }
}
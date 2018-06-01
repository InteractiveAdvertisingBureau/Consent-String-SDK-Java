package com.iab.gdpr;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.iab.gdpr.VendorConsent;
import org.hamcrest.Matchers;
import org.junit.Test;

import com.iab.gdpr.exception.VendorConsentException;

public class VendorConsentTest {
	
	@Test
	public void testWithBitSizeMultipleOfEight() {
	    String consentString = "BOOlLqOOOlLqTABABAENAk-AAAAXx7_______9______9uz_Gv_r_f__3nW8_39P3g_7_O3_7m_-zzV48_lrQV1yPAUCgA";
	    VendorConsent vendorConsent = VendorConsent.fromBase64String(consentString);
	    assertTrue(vendorConsent.getMaxVendorId() == 380);
	    assertTrue(vendorConsent.getBitfield().size() == 380);
	    assertTrue(vendorConsent.isVendorAllowed(380));
	    assertFalse(vendorConsent.isVendorAllowed(379));
	    
	    // test creation of vendor string
        VendorConsent consent = VendorConsent.fromBase64String(consentString);

        VendorConsent.Builder builder = new VendorConsent.Builder();
        builder.withVersion(consent.getVersion());
        builder.withConsentRecordCreatedOn(consent.getConsentRecordCreated());
        builder.withConsentRecordLastUpdatedOn(consent.getConsentRecordLastUpdated());

        builder.withCmpID(consent.getCmpId());
        builder.withCmpVersion(consent.getCmpVersion());
        builder.withConsentScreenID(consent.getConsentScreen());
        builder.withConsentLanguage(consent.getConsentLanguage());
        builder.withVendorListVersion(consent.getVendorListVersion());
        builder.withAllowedPurposes(consent.getAllowedPurposes());

        builder.withMaxVendorId(consent.getMaxVendorId());
        builder.withVendorEncodingType(consent.getVendorEncodingType());
        List<Integer> vendorConsetBit = new ArrayList<>(consent.getMaxVendorId());
        for(int v = 1; v <= consent.getMaxVendorId(); v++) {
            if(consent.isVendorAllowed(v)) {
                vendorConsetBit.add(v);
            }
        }
        builder.withBitField(vendorConsetBit);
        builder.withDefaultConsent(consent.isDefaultConsent());
        builder.withRangeEntries(consent.getRangeEntries());

        VendorConsent underTest = builder.build();

        assertThat(underTest.getConsentString(), Matchers.is(consentString));	    
	}
	
    @Test
    public void testBitField() {
        String consentString = "BN5lERiOMYEdiAOAWeFRAAYAAaAAptQ";
        VendorConsent consent = VendorConsent.fromBase64String(consentString);

        assertThat(consent.getCmpId(), Matchers.is(14));
        assertThat(consent.getCmpVersion(), Matchers.is(22));
        assertThat(consent.getConsentLanguage(), Matchers.is("FR"));
        assertThat(consent.getConsentRecordCreated(), Matchers.is(Instant.ofEpochMilli(14924661858L * 100)));
        assertThat(consent.getConsentRecordLastUpdated(), Matchers.is(Instant.ofEpochMilli(15240021858L * 100)));
        assertThat(consent.getAllowedPurposes().size(), Matchers.is(5));
        assertThat(consent.getAllowedPurposesBits(), Matchers.is(6291482));

        assertTrue(consent.isPurposeAllowed(2));
        assertFalse(consent.isPurposeAllowed(1));
        assertTrue(consent.isPurposeAllowed(21));
        assertTrue(consent.isVendorAllowed(1));
        assertTrue(consent.isVendorAllowed(5));
        assertTrue(consent.isVendorAllowed(7));
        assertTrue(consent.isVendorAllowed(9));
        assertFalse(consent.isVendorAllowed(0));
        assertFalse(consent.isVendorAllowed(10));

        assertThat(consent.getConsentString(), Matchers.is(consentString));
    }

    @Test
    public void testRangeEntry() {
        String consentString = "BN5lERiOMYEdiAKAWXEND1HoSBE6CAFAApAMgBkIDIgM0AgOJxAnQA";

        VendorConsent consent = VendorConsent.fromBase64String(consentString);

        assertThat(consent.getCmpId(), Matchers.is(10));
        assertThat(consent.getCmpVersion(), Matchers.is(22));
        assertThat(consent.getConsentLanguage(), Matchers.is("EN"));
        assertThat(consent.getConsentRecordCreated(), Matchers.is(Instant.ofEpochMilli(14924661858L * 100)));
        assertThat(consent.getConsentRecordLastUpdated(), Matchers.is(Instant.ofEpochMilli(15240021858L * 100)));
        assertThat(consent.getAllowedPurposes().size(), Matchers.is(8));
        assertThat(consent.getAllowedPurposesBits(), Matchers.is(2000001));

        assertTrue(consent.isPurposeAllowed(4));
        assertFalse(consent.isPurposeAllowed(1));
        assertTrue(consent.isPurposeAllowed(24));
        assertFalse(consent.isPurposeAllowed(25));
        assertFalse(consent.isPurposeAllowed(0));
        assertFalse(consent.isVendorAllowed(1));
        assertFalse(consent.isVendorAllowed(3));
        assertTrue(consent.isVendorAllowed(225));
        assertTrue(consent.isVendorAllowed(5000));
        assertTrue(consent.isVendorAllowed(515));
        assertFalse(consent.isVendorAllowed(0));
        assertFalse(consent.isVendorAllowed(411));
        assertFalse(consent.isVendorAllowed(3244));

        assertThat(consent.getConsentString(), Matchers.is(consentString));
    }

    @Test
    public void testLongRangeEntry() {
        String consentString = "BOOMzbgOOQww_AtABAFRAb-AAAsvOA3gACAAkABgArgBaAF0AMAA1gBuAH8AQQBSgCoAL8AYQBigDIAM0AaABpgDYAOYAdgA8AB6gD4AQoAiABFQCMAI6ASABIgCTAEqAJeATIBQQCiAKSAU4BVQCtAK-AWYBaQC2ALcAXMAvAC-gGAAYcAxQDGAGQAMsAZsA0ADTAGqANcAbMA4ADjAHKAOiAdQB1gDtgHgAeMA9AD2AHzAP4BAACBAEEAIbAREBEgCKQEXARhZeYA";

        VendorConsent consent = VendorConsent.fromBase64String(consentString);

        assertThat(consent.getCmpId(), Matchers.is(45));
        assertThat(consent.getCmpVersion(), Matchers.is(1));
        assertThat(consent.getConsentLanguage(), Matchers.is("FR"));
        assertThat(consent.getConsentRecordCreated(), Matchers.is(Instant.ofEpochMilli(15270622944L * 100)));
        assertThat(consent.getConsentRecordLastUpdated(), Matchers.is(Instant.ofEpochMilli(15271660607L * 100)));
        assertThat(consent.getAllowedPurposes().size(), Matchers.is(5));

        assertTrue(consent.isPurposeAllowed(1));
        assertTrue(consent.isPurposeAllowed(2));
        assertTrue(consent.isPurposeAllowed(3));
        assertTrue(consent.isPurposeAllowed(4));
        assertTrue(consent.isPurposeAllowed(5));
        assertFalse(consent.isPurposeAllowed(6));
        assertFalse(consent.isPurposeAllowed(25));
        assertFalse(consent.isPurposeAllowed(0));
        assertTrue(consent.isVendorAllowed(1));
        assertFalse(consent.isVendorAllowed(5));
        assertTrue(consent.isVendorAllowed(45));
        assertFalse(consent.isVendorAllowed(47));
        assertFalse(consent.isVendorAllowed(146));
        assertTrue(consent.isVendorAllowed(147));

        assertThat(consent.getConsentString(), Matchers.is(consentString));
    }

    @Test
    public void testCreationOfConsentString() {
        String consentString = "BN5lERiOMYEdiAKAWXEND1HoSBE6CAFAApAMgBkIDIgM0AgOJxAnQA";

        VendorConsent consent = VendorConsent.fromBase64String(consentString);

        VendorConsent.Builder builder = new VendorConsent.Builder();
        builder.withVersion(consent.getVersion());
        builder.withConsentRecordCreatedOn(consent.getConsentRecordCreated());
        builder.withConsentRecordLastUpdatedOn(consent.getConsentRecordLastUpdated());

        builder.withCmpID(consent.getCmpId());
        builder.withCmpVersion(consent.getCmpVersion());
        builder.withConsentScreenID(consent.getConsentScreen());
        builder.withConsentLanguage(consent.getConsentLanguage());
        builder.withVendorListVersion(consent.getVendorListVersion());
        builder.withAllowedPurposes(consent.getAllowedPurposes());

        builder.withMaxVendorId(consent.getMaxVendorId());
        builder.withVendorEncodingType(consent.getVendorEncodingType());
        builder.withDefaultConsent(consent.isDefaultConsent());
        builder.withRangeEntries(consent.getRangeEntries());

        VendorConsent underTest = builder.build();

        assertThat(underTest.getConsentString(), Matchers.is(consentString));
    }

    @Test(expected = VendorConsentException.class)
    public void testCreationOfConsentStringFails() {
        // given
        String consentString = "BN5lERiOMYEdiAKAWXEND1HoSBE6CAFAApAMgBkIDIgM0AgOJxAnQA";
        VendorConsent consent = VendorConsent.fromBase64String(consentString);

        VendorConsent.Builder builder = new VendorConsent.Builder();
        builder.withVersion(consent.getVersion());
        builder.withConsentRecordCreatedOn(consent.getConsentRecordCreated());
        builder.withConsentRecordLastUpdatedOn(consent.getConsentRecordLastUpdated());
        builder.withCmpID(consent.getCmpId());
        builder.withCmpVersion(consent.getCmpVersion());
        builder.withConsentScreenID(consent.getConsentScreen());
        builder.withVendorListVersion(consent.getVendorListVersion());
        builder.withAllowedPurposes(consent.getAllowedPurposes());
        builder.withMaxVendorId(consent.getMaxVendorId());
        builder.withVendorEncodingType(consent.getVendorEncodingType());
        builder.withDefaultConsent(consent.isDefaultConsent());
        builder.withRangeEntries(consent.getRangeEntries());

        // when
        builder.withConsentLanguage("ENE");
        VendorConsent underTest = builder.build();
    }

    @Test
    public void testRangeEntryConsent() {
        String consentString = "BONZt-1ONZt-1AHABBENAO-AAAAHCAEAASABmADYAOAAeA";
        VendorConsent consent = VendorConsent.fromBase64String(consentString);

        assertTrue(consent.isPurposeAllowed(1));
        assertTrue(consent.isPurposeAllowed(3));
        assertTrue(consent.isVendorAllowed(28));
        assertFalse(consent.isVendorAllowed(1));
        assertFalse(consent.isVendorAllowed(3));
        assertTrue(consent.isVendorAllowed(27));
    }
}
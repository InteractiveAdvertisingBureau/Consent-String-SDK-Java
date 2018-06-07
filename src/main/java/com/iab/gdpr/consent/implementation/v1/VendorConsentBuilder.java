package com.iab.gdpr.consent.implementation.v1;

import com.iab.gdpr.Bits;
import com.iab.gdpr.GdprConstants;
import com.iab.gdpr.Purpose;
import com.iab.gdpr.consent.range.RangeEntry;
import com.iab.gdpr.consent.VendorConsent;
import com.iab.gdpr.exception.VendorConsentCreateException;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.iab.gdpr.GdprConstants.*;

/**
 * Builder for version 1 of vendor consent
 */
public class VendorConsentBuilder {

    private static final int VERSION = 1;

    private Instant consentRecordCreated;
    private Instant consentRecordLastUpdated;
    private int cmpID;
    private int cmpVersion;
    private int consentScreenID;
    private String consentLanguage;
    private int vendorListVersion;
    private int maxVendorId;
    private int vendorEncodingType;
    private Set<Integer> allowedPurposes = new HashSet<>(PURPOSES_SIZE);
    private Set<Integer> vendorsBitField; // used when bit field encoding is used
    private List<RangeEntry> rangeEntries; // used when range entry encoding is used
    private boolean defaultConsent;

    /**
     * With creation date
     * @param consentRecordCreated Epoch deciseconds when record was created
     * @return builder
     */
    public VendorConsentBuilder withConsentRecordCreatedOn(Instant consentRecordCreated) {
        this.consentRecordCreated = consentRecordCreated;
        return this;
    }

    /**
     * With update date
     * @param consentRecordLastUpdated Epoch deciseconds when consent string was last updated
     * @return builder
     */
    public VendorConsentBuilder withConsentRecordLastUpdatedOn(Instant consentRecordLastUpdated) {
        this.consentRecordLastUpdated = consentRecordLastUpdated;
        return this;
    }

    /**
     * With CMP id
     * @param cmpID Consent Manager Provider ID that last updated the consent string
     * @return builder
     */
    public VendorConsentBuilder withCmpID(int cmpID) {
        this.cmpID = cmpID;
        return this;
    }

    /**
     * With CMP version
     * @param cmpVersion Consent Manager Provider version
     * @return builder
     */
    public VendorConsentBuilder withCmpVersion(int cmpVersion) {
        this.cmpVersion = cmpVersion;
        return this;
    }

    /**
     * With consent screen ID
     * @param consentScreenID Screen number in the CMP where consent was given
     * @return builder
     */
    public VendorConsentBuilder withConsentScreenID(int consentScreenID) {
        this.consentScreenID = consentScreenID;
        return this;
    }

    /**
     * With consent language
     * @param consentLanguage Two-letter ISO639-1 language code that CMP asked for consent in
     * @return builder
     */
    public VendorConsentBuilder withConsentLanguage(String consentLanguage) {
        this.consentLanguage = consentLanguage;
        return this;
    }

    /**
     * With vendor list version
     * @param vendorListVersion Version of vendor list used in most recent consent string update
     * @return builder
     */
    public VendorConsentBuilder withVendorListVersion(int vendorListVersion) {
        this.vendorListVersion = vendorListVersion;
        return this;
    }

    /**
     * With allowed purpose IDs
     * @param allowedPurposeIds set of allowed purposes
     * @return builder
     */
    public VendorConsentBuilder withAllowedPurposeIds(Set<Integer> allowedPurposeIds) {
        // Validate
        Objects.requireNonNull(allowedPurposeIds, "Argument allowedPurposeIds is null");
        final boolean invalidPurposeIdFound = allowedPurposeIds.stream().anyMatch(purposeId -> purposeId < 0 || purposeId > PURPOSES_SIZE);
        if (invalidPurposeIdFound) throw new IllegalArgumentException("Invalid purpose ID found");

        this.allowedPurposes = allowedPurposeIds;
        return this;
    }

    /**
     * With allowed purposes
     * @param allowedPurposes set of allowed purposes
     * @return builder
     */
    public VendorConsentBuilder withAllowedPurposes(Set<Purpose> allowedPurposes) {
        // Validate
        Objects.requireNonNull(allowedPurposes, "Argument allowedPurposes is null");

        this.allowedPurposes = allowedPurposes.stream().map(Purpose::getId).collect(Collectors.toSet());
        return this;
    }

    /**
     * With max vendor ID
     * @param maxVendorId The maximum VendorId for which consent values are given.
     * @return builder
     */
    public VendorConsentBuilder withMaxVendorId(int maxVendorId) {
        this.maxVendorId = maxVendorId;
        return this;
    }

    /**
     * With vendor encoding type
     * @param vendorEncodingType 0=BitField 1=Range
     * @return builder
     */
    public VendorConsentBuilder withVendorEncodingType(int vendorEncodingType) {
        if (vendorEncodingType < 0 || vendorEncodingType > 1)
            throw new IllegalArgumentException("Illegal value for argument vendorEncodingType:" + vendorEncodingType);

        this.vendorEncodingType = vendorEncodingType;
        return this;
    }

    /**
     * With bit field entries
     * @param bitFieldEntries set of VendorIds for which the vendors have consent
     * @return builder
     */
    public VendorConsentBuilder withBitField(Set<Integer> bitFieldEntries) {
        this.vendorsBitField = bitFieldEntries;
        return this;
    }

    /**
     * With range entries
     * @param rangeEntries List of VendorIds or a range of VendorIds for which the vendors have consent
     * @return builder
     */
    public VendorConsentBuilder withRangeEntries(List<RangeEntry> rangeEntries) {
        this.rangeEntries = rangeEntries;
        return this;
    }

    /**
     * With default consent
     * @param defaultConsent Default consent for VendorIds not covered by a RangeEntry. 0=No Consent 1=Consent
     * @return builder
     */
    public VendorConsentBuilder withDefaultConsent(boolean defaultConsent) {
        this.defaultConsent = defaultConsent;
        return this;
    }

    /**
     * Validate supplied values and build {@link VendorConsent} object
     * @return vendor consent object
     */
    public VendorConsent build() {

        Objects.requireNonNull(consentRecordCreated, "consentRecordCreated must be set");
        Objects.requireNonNull(consentRecordLastUpdated, "consentRecordLastUpdated must be set");
        Objects.requireNonNull(consentLanguage, "consentLanguage must be set");

        if (vendorListVersion <=0 )
            throw new VendorConsentCreateException("Invalid value for vendorListVersion:" + vendorListVersion);

        if (maxVendorId <=0 )
            throw new VendorConsentCreateException("Invalid value for maxVendorId:" + maxVendorId);

        // For range encoding, check if each range entry is valid
        if (vendorEncodingType == VENDOR_ENCODING_RANGE) {
            Objects.requireNonNull(rangeEntries, "Range entries must be set");
            final boolean invalidRangeEntriesFound = rangeEntries.stream().anyMatch(rangeEntry -> !rangeEntry.valid(maxVendorId));
            if (invalidRangeEntriesFound) throw new VendorConsentCreateException("Invalid range entries found");
        }

        // Calculate size of bit buffer in bits
        final int bitBufferSizeInBits;
        if (vendorEncodingType == VENDOR_ENCODING_RANGE) {
            final int rangeEntrySectionSize = rangeEntries.stream().mapToInt(RangeEntry::size).sum();
            bitBufferSizeInBits = RANGE_ENTRY_OFFSET + rangeEntrySectionSize;
        } else {
            bitBufferSizeInBits = VENDOR_BITFIELD_OFFSET + this.maxVendorId;
        }

        // Create new bit buffer
        final boolean bitsFit = (bitBufferSizeInBits % 8) == 0;
        final Bits bits = new Bits(new byte[bitBufferSizeInBits / 8 + (bitsFit ? 0 : 1)]);

        // Set fields in bit buffer
        bits.setInt(VERSION_BIT_OFFSET, VERSION_BIT_SIZE, VERSION);
        bits.setInstantToEpochDeciseconds(CREATED_BIT_OFFSET, CREATED_BIT_SIZE, consentRecordCreated);
        bits.setInstantToEpochDeciseconds(UPDATED_BIT_OFFSET, UPDATED_BIT_SIZE, consentRecordLastUpdated);
        bits.setInt(CMP_ID_OFFSET, CMP_ID_SIZE, this.cmpID);
        bits.setInt(CMP_VERSION_OFFSET, CMP_VERSION_SIZE, cmpVersion);
        bits.setInt(CONSENT_SCREEN_SIZE_OFFSET, CONSENT_SCREEN_SIZE, consentScreenID);
        bits.setSixBitString(CONSENT_LANGUAGE_OFFSET, CONSENT_LANGUAGE_SIZE, consentLanguage);
        bits.setInt(VENDOR_LIST_VERSION_OFFSET, VENDOR_LIST_VERSION_SIZE, vendorListVersion);

        // Set purposes bits
        for (int i = 0; i < PURPOSES_SIZE; i++) {
            if (allowedPurposes.contains(i+1))
                bits.setBit(PURPOSES_OFFSET + i);
            else
                bits.unsetBit(PURPOSES_OFFSET + i);
        }

        bits.setInt(MAX_VENDOR_ID_OFFSET, MAX_VENDOR_ID_SIZE, maxVendorId);
        bits.setInt(ENCODING_TYPE_OFFSET, ENCODING_TYPE_SIZE, vendorEncodingType);

        // Set the bit field or range sections
        if (vendorEncodingType == VENDOR_ENCODING_RANGE) {
            // Range encoding
            if (defaultConsent) {
                bits.setBit(DEFAULT_CONSENT_OFFSET);
            } else {
                bits.unsetBit(DEFAULT_CONSENT_OFFSET);
            }
            bits.setInt(NUM_ENTRIES_OFFSET, NUM_ENTRIES_SIZE, rangeEntries.size());

            int currentOffset = GdprConstants.RANGE_ENTRY_OFFSET;

            for (RangeEntry rangeEntry : rangeEntries) {
                currentOffset = rangeEntry.appendTo(bits, currentOffset);
            }

        } else {
            // Bit field encoding
            for (int i = 0; i < maxVendorId; i++) {
                if (vendorsBitField.contains(i+1))
                    bits.setBit(VENDOR_BITFIELD_OFFSET+i);
                else
                    bits.unsetBit(VENDOR_BITFIELD_OFFSET + i);
            }
        }

        return new ByteBufferBackedVendorConsent(bits);
    }
}

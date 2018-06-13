package com.iab.gdpr.consent.implementation.v1;


import com.iab.gdpr.Bits;
import com.iab.gdpr.Purpose;
import com.iab.gdpr.consent.VendorConsent;
import com.iab.gdpr.exception.VendorConsentParseException;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.iab.gdpr.GdprConstants.*;

/**
 * Implementation of {@link VendorConsent}. This implementation uses byte buffer (wrapped with {@link Bits})
 * as a storage of consent string values and parses individual fields on demand.
 *
 * This should work well in environment where vendor consent string is decoded, couple of isPurposeAllowed()/isVendorAllowed()
 * calls are made and then value of the consent is discarded.
 *
 * In the environment where decoded consent string is kept for longer time with numerous isPurposeAllowed()/isVendorAllowed()
 * calls a different implementation may be needed that would cache results of those calls.
 *
 */
public class ByteBufferBackedVendorConsent implements VendorConsent {
    private final Bits bits;

    public ByteBufferBackedVendorConsent(Bits bits) {
        this.bits = bits;
    }

    @Override
    public int getVersion() {
        return bits.getInt(VERSION_BIT_OFFSET, VERSION_BIT_SIZE);
    }

    @Override
    public Instant getConsentRecordCreated() {
        return bits.getInstantFromEpochDeciseconds(CREATED_BIT_OFFSET, CREATED_BIT_SIZE);
    }

    @Override
    public Instant getConsentRecordLastUpdated() {
        return bits.getInstantFromEpochDeciseconds(UPDATED_BIT_OFFSET, UPDATED_BIT_SIZE);
    }

    @Override
    public int getCmpId() {
        return bits.getInt(CMP_ID_OFFSET, CMP_ID_SIZE);
    }

    @Override
    public int getCmpVersion() {
        return bits.getInt(CMP_VERSION_OFFSET, CMP_VERSION_SIZE);
    }

    @Override
    public int getConsentScreen() {
        return bits.getInt(CONSENT_SCREEN_SIZE_OFFSET, CONSENT_SCREEN_SIZE);
    }

    @Override
    public String getConsentLanguage() {
        return bits.getSixBitString(CONSENT_LANGUAGE_OFFSET, CONSENT_LANGUAGE_SIZE);
    }

    @Override
    public int getVendorListVersion() {
        return bits.getInt(VENDOR_LIST_VERSION_OFFSET, VENDOR_LIST_VERSION_SIZE);
    }

    @Override
    public Set<Integer> getAllowedPurposeIds() {
        final Set<Integer> allowedPurposes = new HashSet<>();
        for (int i = PURPOSES_OFFSET; i < PURPOSES_OFFSET + PURPOSES_SIZE; i++) {
            if (bits.getBit(i)) {
                allowedPurposes.add(i - PURPOSES_OFFSET + 1);
            }
        }
        return allowedPurposes;
    }

    @Override
    public Set<Purpose> getAllowedPurposes() {
        return getAllowedPurposeIds().stream().map(Purpose::valueOf).collect(Collectors.toSet());
    }

    @Override
    public int getAllowedPurposesBits() {
        return bits.getInt(PURPOSES_OFFSET, PURPOSES_SIZE);
    }

    @Override
    public int getMaxVendorId() {
        return bits.getInt(MAX_VENDOR_ID_OFFSET, MAX_VENDOR_ID_SIZE);
    }

    @Override
    public boolean isPurposeAllowed(int purposeId) {
        if (purposeId < 1 || purposeId > PURPOSES_SIZE) return false;
        return bits.getBit(PURPOSES_OFFSET + purposeId - 1);
    }

    @Override
    public boolean isPurposeAllowed(Purpose purpose) {
        return isPurposeAllowed(purpose.getId());
    }

    @Override
    public boolean isVendorAllowed(int vendorId) {
        final int maxVendorId = getMaxVendorId();
        if (vendorId < 0 || vendorId > maxVendorId) return false;

        if (encodingType() == VENDOR_ENCODING_RANGE) {
            final boolean defaultConsent = bits.getBit(DEFAULT_CONSENT_OFFSET);
            final boolean present = isVendorPresentInRange(vendorId);
            return present != defaultConsent;
        } else {
            return bits.getBit(VENDOR_BITFIELD_OFFSET + vendorId - 1);
        }
    }

    @Override
    public byte[] toByteArray() {
        return bits.toByteArray();
    }

    /**
     *
     * @return the encoding type - 0=BitField 1=Range
     */
    private int encodingType() {
        return bits.getInt(ENCODING_TYPE_OFFSET, ENCODING_TYPE_SIZE);
    }

    /**
     * Check whether specified vendor ID is present in the range section of the bits. This assumes that
     * encoding type was already checked and is VENDOR_ENCODING_RANGE
     * @param vendorId vendor ID to check
     * @return boolean value of vendor ID presence
     */
    private boolean isVendorPresentInRange(int vendorId) {
        final int numEntries = bits.getInt(NUM_ENTRIES_OFFSET, NUM_ENTRIES_SIZE);
        final int maxVendorId = getMaxVendorId();
        int currentOffset = RANGE_ENTRY_OFFSET;
        for (int i = 0; i < numEntries; i++) {
            boolean range = bits.getBit(currentOffset);
            currentOffset++;
            if (range) {
                int startVendorId = bits.getInt(currentOffset, VENDOR_ID_SIZE);
                currentOffset += VENDOR_ID_SIZE;
                int endVendorId = bits.getInt(currentOffset, VENDOR_ID_SIZE);
                currentOffset += VENDOR_ID_SIZE;

                if (startVendorId > endVendorId || endVendorId > maxVendorId) {
                    throw new VendorConsentParseException(
                            "Start VendorId must not be greater than End VendorId and "
                                    + "End VendorId must not be greater than Max Vendor Id");
                }
                if (vendorId >= startVendorId && vendorId <= endVendorId) return true;

            } else {
                int singleVendorId = bits.getInt(currentOffset, VENDOR_ID_SIZE);
                currentOffset += VENDOR_ID_SIZE;

                if (singleVendorId > maxVendorId) {
                    throw new VendorConsentParseException(
                            "VendorId in the range entries must not be greater than Max VendorId");
                }

                if (singleVendorId == vendorId) return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ByteBufferBackedVendorConsent that = (ByteBufferBackedVendorConsent) o;
        return Arrays.equals(bits.toByteArray(), that.bits.toByteArray());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bits.toByteArray());
    }

    @Override
    public String toString() {
        return "ByteBufferVendorConsent{" +
                "Version=" + getVersion() +
                ",Created=" + getConsentRecordCreated() +
                ",LastUpdated=" + getConsentRecordLastUpdated() +
                ",CmpId=" + getCmpId() +
                ",CmpVersion=" + getCmpVersion() +
                ",ConsentScreen=" + getConsentScreen() +
                ",ConsentLanguage=" + getConsentLanguage() +
                ",VendorListVersion=" + getVendorListVersion() +
                ",PurposesAllowed=" + getAllowedPurposeIds() +
                ",MaxVendorId=" + getMaxVendorId() +
                ",EncodingType=" + encodingType() +
                "}";
    }
}

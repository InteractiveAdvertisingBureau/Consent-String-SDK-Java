package com.iab.gdpr.util;

import static com.iab.gdpr.GdprConstants.CMP_ID_OFFSET;
import static com.iab.gdpr.GdprConstants.CMP_ID_SIZE;
import static com.iab.gdpr.GdprConstants.CMP_VERSION_OFFSET;
import static com.iab.gdpr.GdprConstants.CONSENT_LANGUAGE_OFFSET;
import static com.iab.gdpr.GdprConstants.CONSENT_LANGUAGE_SIZE;
import static com.iab.gdpr.GdprConstants.CONSENT_SCREEN_SIZE;
import static com.iab.gdpr.GdprConstants.CONSENT_SCREEN_SIZE_OFFSET;
import static com.iab.gdpr.GdprConstants.CREATED_BIT_OFFSET;
import static com.iab.gdpr.GdprConstants.CREATED_BIT_SIZE;
import static com.iab.gdpr.GdprConstants.DEFAULT_CONSENT_OFFSET;
import static com.iab.gdpr.GdprConstants.ENCODING_TYPE_OFFSET;
import static com.iab.gdpr.GdprConstants.ENCODING_TYPE_SIZE;
import static com.iab.gdpr.GdprConstants.MAX_VENDOR_ID_OFFSET;
import static com.iab.gdpr.GdprConstants.MAX_VENDOR_ID_SIZE;
import static com.iab.gdpr.GdprConstants.NUM_ENTRIES_OFFSET;
import static com.iab.gdpr.GdprConstants.NUM_ENTRIES_SIZE;
import static com.iab.gdpr.GdprConstants.PURPOSES_OFFSET;
import static com.iab.gdpr.GdprConstants.PURPOSES_SIZE;
import static com.iab.gdpr.GdprConstants.RANGE_ENTRY_OFFSET;
import static com.iab.gdpr.GdprConstants.UPDATED_BIT_OFFSET;
import static com.iab.gdpr.GdprConstants.UPDATED_BIT_SIZE;
import static com.iab.gdpr.GdprConstants.VENDOR_BITFIELD_OFFSET;
import static com.iab.gdpr.GdprConstants.VENDOR_ENCODING_RANGE;
import static com.iab.gdpr.GdprConstants.VENDOR_ID_SIZE;
import static com.iab.gdpr.GdprConstants.VENDOR_LIST_VERSION_OFFSET;
import static com.iab.gdpr.GdprConstants.VENDOR_LIST_VERSION_SIZE;
import static com.iab.gdpr.GdprConstants.VERSION_BIT_OFFSET;
import static com.iab.gdpr.GdprConstants.VERSION_BIT_SIZE;

import java.util.ArrayList;
import java.util.List;

import com.iab.gdpr.Bits;
import com.iab.gdpr.GdprConstants;
import com.iab.gdpr.VendorConsent;

public class ConsentStringParser {
    private Bits bits;

    public ConsentStringParser(byte[] consent) {
        this.bits = new Bits(consent);
    }

    public VendorConsent parse() {
        VendorConsent.Builder builder = new VendorConsent.Builder();

                builder.withVersion(bits.getInt(VERSION_BIT_OFFSET, VERSION_BIT_SIZE));
        builder.withConsentRecordCreatedOn(bits.getInstantFromEpochDeciseconds(CREATED_BIT_OFFSET, CREATED_BIT_SIZE));
        builder.withConsentRecordLastUpdatedOn(bits.getInstantFromEpochDeciseconds(UPDATED_BIT_OFFSET, UPDATED_BIT_SIZE));

        builder.withCmpID(bits.getInt(CMP_ID_OFFSET, CMP_ID_SIZE));
        builder.withCmpVersion(bits.getInt(CMP_VERSION_OFFSET, GdprConstants.CMP_VERSION_SIZE));
        builder.withConsentScreenID(bits.getInt(CONSENT_SCREEN_SIZE_OFFSET, CONSENT_SCREEN_SIZE));
        builder.withConsentLanguage(bits.getSixBitString(CONSENT_LANGUAGE_OFFSET, CONSENT_LANGUAGE_SIZE));

        builder.withVendorListVersion(bits.getInt(VENDOR_LIST_VERSION_OFFSET, VENDOR_LIST_VERSION_SIZE));

        List<Integer> allowedPurposes = new ArrayList<>();
        for (int i = PURPOSES_OFFSET, ii = PURPOSES_OFFSET + PURPOSES_SIZE; i < ii; i++) {
            if (bits.getBit(i)) {
                allowedPurposes.add(i - PURPOSES_OFFSET + 1);
            }
        }
        builder.withAllowedPurposes(allowedPurposes);

        int maxVendorId = bits.getInt(MAX_VENDOR_ID_OFFSET, MAX_VENDOR_ID_SIZE);
        builder.withMaxVendorId(maxVendorId);
        int vendorEncodingType = bits.getInt(ENCODING_TYPE_OFFSET, ENCODING_TYPE_SIZE);
        builder.withVendorEncodingType(vendorEncodingType);

        List<VendorConsent.RangeEntry> rangeEntries = new ArrayList<>();
        if (vendorEncodingType == VENDOR_ENCODING_RANGE) {
            builder.withDefaultConsent(bits.getBit(DEFAULT_CONSENT_OFFSET));
            int numEntries = bits.getInt(NUM_ENTRIES_OFFSET, NUM_ENTRIES_SIZE);

            for (int i = 0, currentOffset = RANGE_ENTRY_OFFSET + 1; i < numEntries; i++, currentOffset++) {
                boolean range = bits.getBit(currentOffset - 1);
                if (range) {
                    int startVendorId = bits.getInt(currentOffset, VENDOR_ID_SIZE);
                    currentOffset += VENDOR_ID_SIZE;
                    int endVendorId = bits.getInt(currentOffset, VENDOR_ID_SIZE);
                    currentOffset += VENDOR_ID_SIZE;
                    rangeEntries.add(new VendorConsent.RangeEntry(startVendorId, endVendorId));
                } else {
                    int vendorId = bits.getInt(currentOffset, VENDOR_ID_SIZE);
                    currentOffset += VENDOR_ID_SIZE;
                    rangeEntries.add(new VendorConsent.RangeEntry(vendorId));
                }
            }
            builder.withRangeEntries(rangeEntries);
        } else {
            List<Integer> bitField = new ArrayList<>(maxVendorId);
            for (int i = VENDOR_BITFIELD_OFFSET; i < VENDOR_BITFIELD_OFFSET + maxVendorId; i++) {
                if (bits.getBit(i)) {
                    bitField.add(i);
                }
            }
            builder.withBitField(bitField);
        }

        return builder.build();
    }
}

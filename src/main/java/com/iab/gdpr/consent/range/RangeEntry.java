package com.iab.gdpr.consent.range;

import com.iab.gdpr.Bits;

/**
 * Range entry  of the vendor consent range section. Range entry is a single or range of VendorIds
 * whose consent value is the opposite of DefaultConsent.
 */
public interface RangeEntry {

    /**
     * @return Size of range entry in bits
     */
    int size();

    /**
     * Append this range entry to the bit buffer
     * @param buffer bit buffer
     * @param currentOffset current offset in the buffer
     * @return new offset
     */
    int appendTo(Bits buffer, int currentOffset);

    /**
     * Check if range entry is valid for the specified max vendor id
     * @param maxVendorId max vendor id
     * @return true if range entry is valid, false otherwise
     */
    boolean valid(int maxVendorId);
}

package com.iab.gdpr.consent.range;

import com.iab.gdpr.Bits;

import static com.iab.gdpr.GdprConstants.VENDOR_ID_SIZE;

/**
 * {@link RangeEntry} with single vendor ID
 */
public class SingleRangeEntry implements RangeEntry {
    private final int singeVendorId;

    public SingleRangeEntry(int singeVendorId) {
        this.singeVendorId = singeVendorId;
    }

    @Override
    public int size() {
        // One bit for SingleOrRange flag, VENDOR_ID_SIZE for single vendor ID
        return 1+VENDOR_ID_SIZE;
    }

    @Override
    public int appendTo(Bits buffer, int currentOffset) {
        int newOffset = currentOffset;
        buffer.unsetBit(newOffset++); // 0=Single
        buffer.setInt(newOffset, VENDOR_ID_SIZE, singeVendorId);
        newOffset += VENDOR_ID_SIZE;
        return newOffset;
    }

    @Override
    public boolean valid(int maxVendorId) {
        return singeVendorId > 0 && singeVendorId <= maxVendorId;
    }
}

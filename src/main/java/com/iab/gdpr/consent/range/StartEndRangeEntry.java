package com.iab.gdpr.consent.range;

import com.iab.gdpr.Bits;

import static com.iab.gdpr.GdprConstants.VENDOR_ID_SIZE;

/**
 * {@link RangeEntry} with start and end vendor IDs
 */
public class StartEndRangeEntry implements RangeEntry{
    private final int startVendorId;
    private final int endVendorId;

    public StartEndRangeEntry(int startVendorId, int endVendorId) {
        this.startVendorId = startVendorId;
        this.endVendorId = endVendorId;
    }

    @Override
    public int size() {
        // One bit for SingleOrRange flag, 2 * VENDOR_ID_SIZE for 2 vendor IDs
        return 1+VENDOR_ID_SIZE * 2;
    }

    @Override
    public int appendTo(Bits buffer, int currentOffset) {
        int newOffset = currentOffset;
        buffer.setBit(newOffset++); // 1=Range
        buffer.setInt(newOffset, VENDOR_ID_SIZE, startVendorId);
        newOffset += VENDOR_ID_SIZE;
        buffer.setInt(newOffset, VENDOR_ID_SIZE, endVendorId);
        newOffset += VENDOR_ID_SIZE;
        return newOffset;
    }

    @Override
    public boolean valid(int maxVendorId) {
        return startVendorId > 0 && endVendorId > 0 && startVendorId < endVendorId && endVendorId <= maxVendorId;
    }
}

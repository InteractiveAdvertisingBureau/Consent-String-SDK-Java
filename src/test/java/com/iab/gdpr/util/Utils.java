package com.iab.gdpr.util;

import com.iab.gdpr.Bits;

/**
 * Testing utility functions
 */
public class Utils {

    /**
     * Create bit buffer from string representation
     * @param binaryString binary string
     * @return bit buffer
     */
    public static Bits fromBinaryString(String binaryString) {
        final int length = binaryString.length();
        final boolean bitsFit = (length % 8) == 0;
        final Bits bits = new Bits(new byte[length / 8 + (bitsFit ? 0 : 1)]);

        for (int i = 0; i < length; i++)
            if (binaryString.charAt(i) == '1')
                bits.setBit(i);
            else
                bits.unsetBit(i);

        return bits;
    }
}

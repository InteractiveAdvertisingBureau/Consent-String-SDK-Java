package com.iab.gdpr;

import java.time.Instant;

import com.iab.gdpr.exception.VendorConsentCreateException;
import com.iab.gdpr.exception.VendorConsentException;
import com.iab.gdpr.exception.VendorConsentParseException;

/*
 * since java.util.BitSet is inappropiate to use here--as it reversed the bit order of the consent string
 * implement bitwise operations here
 */
public class Bits {
    // big endian
    private static final byte[] bytePows = { -128, 64, 32, 16, 8, 4, 2, 1 };
    private final byte[] bytes;

    public Bits(byte[] b) {
        this.bytes = b;
    }

    /**
     *
     * @param index:
     *            the nth number bit to get from the bit string
     * @return boolean bit, true if the bit is switched to 1, false otherwise
     */
    public boolean getBit(int index) {
        int byteIndex = index / 8;
        int bitExact = index % 8;
        byte b = bytes[byteIndex];
        return (b & bytePows[bitExact]) != 0;
    }

    /**
     *
     * @param index:
     *            set the nth number bit from the bit string
     */
    public void setBit(int index) {
        int byteIndex = index / 8;
        int shift = (byteIndex + 1) * 8 - index - 1;
        bytes[byteIndex] |= 1 << shift;
    }

    /**
     *
     * @param index:
     *            unset the nth number bit from the bit string
     */
    public void unsetBit(int index) {
        int byteIndex = index / 8;
        int shift = (byteIndex + 1) * 8 - index - 1;
        bytes[byteIndex] &= ~(1 << shift);
    }

    /**
     * interprets n number of bits as a big endiant int
     *
     * @param startInclusive:
     *            the nth to begin interpreting from
     * @param size:
     *            the number of bits to interpret
     * @return
     * @throws VendorConsentException
     *             when the bits cannot fit in an int sized field
     */
    public int getInt(int startInclusive, int size) throws VendorConsentException {
        if (size > Integer.SIZE) {
            throw new VendorConsentParseException("can't fit bit range in int " + size);
        }
        int val = 0;
        int sigMask = 1;
        int sigIndex = size - 1;

        for (int i = 0; i < size; i++) {
            if (getBit(startInclusive + i)) {
                val += (sigMask << sigIndex);
            }
            sigIndex--;
        }
        return val;
    }

    /**
     * Writes an integer value into a bit array of given size
     *
     * @param startInclusive:
     *            the nth to begin writing to
     * @param size:
     *            the number of bits available to write
     * @param to:
     *            the integer to write out
     * @throws VendorConsentException
     *             when the bits cannot fit into the provided size
     */
    public void setInt(int startInclusive, int size, int to) throws VendorConsentException {
        if (size > Integer.SIZE || to > maxOfSize(size) || to < 0) {
            throw new VendorConsentCreateException("can't fit integer into bit range of size" + size);
        }

        setNumber(startInclusive, size, to);
    }

    /**
     * interprets n bits as a big endian long
     *
     * @param startInclusive:
     *            the nth to begin interpreting from
     * @param size:the
     *            number of bits to interpret
     * @return the long value create by interpretation of provided bits
     * @throws VendorConsentException
     *             when the bits cannot fit in an int sized field
     */
    public long getLong(int startInclusive, int size) throws VendorConsentException {
        if (size > Long.SIZE) {
            throw new VendorConsentParseException("can't fit bit range in long: " + size);
        }
        long val = 0;
        long sigMask = 1;
        int sigIndex = size - 1;

        for (int i = 0; i < size; i++) {
            if (getBit(startInclusive + i)) {
                val += (sigMask << sigIndex);
            }
            sigIndex--;
        }
        return val;
    }

    /**
     * Writes a long value into a bit array of given size
     *
     * @param startInclusive:
     *            the nth to begin writing to
     * @param size:
     *            the number of bits available to write
     * @param to:
     *            the long number to write out
     * @throws VendorConsentException
     *             when the bits cannot fit into the provided size
     */
    public void setLong(int startInclusive, int size, long to) throws VendorConsentException {
        if (size > Long.SIZE || to > maxOfSize(size) || to < 0) {
            throw new VendorConsentCreateException("can't fit long into bit range of size " + size);
        }

        setNumber(startInclusive, size, to);
    }

    /**
     * returns an {@link Instant} derived from interpreting the given interval on the bit string as long representing
     * the number of demiseconds from the unix epoch
     *
     * @param startInclusive:
     *            the bit from which to begin interpreting
     * @param size:
     *            the number of bits to interpret
     * @return
     * @throws VendorConsentException
     *             when the number of bits requested cannot fit in a long
     */
    public Instant getInstantFromEpochDeciseconds(int startInclusive, int size) throws VendorConsentException {
        long epochDemi = getLong(startInclusive, size);
        return Instant.ofEpochMilli(epochDemi * 100);
    }

    public void setInstantToEpochDeciseconds(int startInclusive, int size, Instant instant)
            throws VendorConsentException {
        setLong(startInclusive, size, instant.toEpochMilli() / 100);
    }

    /**
     * @return the number of bits in the bit string
     *
     */
    public int length() {
        return bytes.length * 8;
    }

    /**
     * This method interprets the given interval in the bit string as a series of six bit characters, where 0=A and 26=Z
     *
     * @param startInclusive:
     *            the nth bit in the bitstring from which to start the interpretation
     * @param size:
     *            the number of bits to include in the string
     * @return the string given by the above interpretation
     * @throws VendorConsentException
     *             when the requested interval is not a multiple of six
     */
    public String getSixBitString(int startInclusive, int size) throws VendorConsentException {
        if (size % 6 != 0) {
            throw new VendorConsentParseException("string bit length must be multiple of six: " + size);
        }
        int charNum = size / 6;
        StringBuilder val = new StringBuilder();
        for (int i = 0; i < charNum; i++) {
            int charCode = getInt(startInclusive + (i * 6), 6) + 65;
            val.append((char) charCode);
        }
        return val.toString().toUpperCase();
    }

    /**
     * This method interprets characters, as 0=A and 26=Z and writes to the given interval in the bit string as a series
     * of six bits
     *
     * @param startInclusive:
     *            the nth bit in the bitstring from which to start writing
     * @param size:
     *            the size of the bitstring
     * @param to:
     *            the string given by the above interpretation
     * @throws VendorConsentException
     *             when the requested interval is not a multiple of six
     */
    public void setSixBitString(int startInclusive, int size, String to) throws VendorConsentException {
        if (size % 6 != 0 || size / 6 != to.length()) {
            throw new VendorConsentCreateException(
                    "bit array size must be multiple of six and equal to 6 times the size of string");
        }
        char[] values = to.toCharArray();
        for (int i = 0; i < values.length; i++) {
            int charCode = values[i] - 65;
            setInt(startInclusive + (i * 6), 6, charCode);
        }
    }

    /**
     *
     * @return a string representation of the byte array passed in the constructor. for example, a bit array of [4]
     *         yields a String of "0100"
     */
    public String getBinaryString() {
        StringBuilder s = new StringBuilder();
        int size = length();
        for (int i = 0; i < size; i++) {
            if (getBit(i)) {
                s.append("1");
            } else {
                s.append("0");
            }
        }
        return s.toString();
    }

    public byte[] toByteArray() {
        return bytes;
    }

    private void setNumber(int startInclusive, int size, long to) {
        for (int i = size - 1; i >= 0; i--) {
            int index = startInclusive + i;
            int byteIndex = index / 8;
            int shift = (byteIndex + 1) * 8 - index - 1;
            bytes[byteIndex] |= (to % 2) << shift;
            to /= 2;
        }
    }

    private long maxOfSize(int size) {
        long max = 0;
        for (int i = 0; i < size; i++) {
            max += Math.pow(2, i);
        }
        return max;
    }
}

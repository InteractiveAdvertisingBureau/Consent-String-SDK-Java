package com.iab.gdpr;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import com.iab.gdpr.exception.GdprException;
import com.iab.gdpr.exception.VendorConsentCreateException;
import com.iab.gdpr.exception.VendorConsentException;
import com.iab.gdpr.exception.VendorConsentParseException;
import com.iab.gdpr.util.ConsentStringParser;

import static com.iab.gdpr.GdprConstants.PURPOSES_OFFSET;
import static com.iab.gdpr.GdprConstants.PURPOSES_SIZE;

/**
 * This class implements a builder and a factory method for the IAB consent as specified in
 * https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/master/
 * Draft_for_Public_Comment_Transparency%20%26%20Consent%20Framework%20-%20cookie%20and%20vendor%20list%20format%
 * 20specification%20v1.0a.pdf
 *
 * @deprecated This is deprecated and will be removed in future versions
 *   Use {@link com.iab.gdpr.consent.VendorConsentDecoder} to decode vendor consent string
 *   Use {@link com.iab.gdpr.consent.implementation.v1.VendorConsentBuilder} to build version 1 consent string
 *   Use {@link com.iab.gdpr.consent.VendorConsentEncoder} to encode vendor consent to string
 *
 */
@Deprecated
public class VendorConsent {
    private static Decoder decoder = Base64.getUrlDecoder();
    // As per the GDPR framework guidelines padding should be ommitted
    private static Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    private final Bits bits;
    // fields contained in the consent string
    private final int version;
    private final Instant consentRecordCreated;
    private final Instant consentRecordLastUpdated;
    private final int cmpID;
    private final int cmpVersion;
    private final int consentScreenID;
    private final String consentLanguage;
    private final int vendorListVersion;
    private final int maxVendorId;
    private final int vendorEncodingType;
    private final List<Boolean> allowedPurposes;
    // only used when bitfield is enabled
    private List<Boolean> bitfield;
    // only used when range entry is enabled
    private boolean defaultConsent;
    private List<RangeEntry> rangeEntries;
    private String consentString;
    private List<Integer> integerPurposes;

    private VendorConsent(Builder builder) throws VendorConsentException {
        this.version = builder.version;
        this.consentRecordCreated = builder.consentRecordCreated;
        this.consentRecordLastUpdated = builder.consentRecordLastUpdated;
        this.cmpID = builder.cmpID;
        this.cmpVersion = builder.cmpVersion;
        this.consentScreenID = builder.consentScreenID;
        this.consentLanguage = builder.consentLanguage;
        this.vendorListVersion = builder.vendorListVersion;
        this.maxVendorId = builder.maxVendorId;
        this.vendorEncodingType = builder.vendorEncodingType;
        this.allowedPurposes = builder.allowedPurposes;

        if (this.vendorEncodingType == GdprConstants.VENDOR_ENCODING_RANGE) {
            this.defaultConsent = builder.defaultConsent;
            if (builder.rangeEntries.stream().anyMatch(rangeEntry -> rangeEntry.endVendorId > maxVendorId)) {
                throw new VendorConsentCreateException("VendorId in range entry is greater than Max VendorId");
            }
            this.rangeEntries = builder.rangeEntries;
        } else {
            this.bitfield = new ArrayList<>(this.maxVendorId);
            IntStream.range(0, this.maxVendorId).forEach(i -> this.bitfield.add(false));
            for (int vendorId : builder.vendorsBitField) {
                if (vendorId > maxVendorId || vendorId < 1) {
                    throw new VendorConsentCreateException("VendorId in bit field is greater than Max VendorId or less than 1");
                }
                this.bitfield.set(vendorId - 1, true);
            }
        }

        this.integerPurposes = builder.integerPurposes;

        if (this.vendorEncodingType == GdprConstants.VENDOR_ENCODING_RANGE) {
            int rangeEntrySize = rangeEntries.size(); // one bit SingleOrRange flag per entry
            for (RangeEntry entry : rangeEntries) {
                if (entry.endVendorId == entry.startVendorId) {
                    rangeEntrySize += GdprConstants.VENDOR_ID_SIZE;
                } else {
                    rangeEntrySize += GdprConstants.VENDOR_ID_SIZE * 2;
                }
            }
            int bitSize = GdprConstants.RANGE_ENTRY_OFFSET + rangeEntrySize;
            boolean bitsFit = (bitSize % 8) == 0;
            this.bits = new Bits(new byte[bitSize / 8 + (bitsFit ? 0 : 1)]);
        } else {
            int bitSize = GdprConstants.VENDOR_BITFIELD_OFFSET + this.maxVendorId;
            boolean bitsFit = (bitSize % 8) == 0;
            this.bits = new Bits(new byte[(bitSize / 8 + (bitsFit ? 0 : 1))]);
        }

        bits.setInt(GdprConstants.VERSION_BIT_OFFSET, GdprConstants.VERSION_BIT_SIZE, this.version);
        bits.setInstantToEpochDeciseconds(GdprConstants.CREATED_BIT_OFFSET, GdprConstants.CREATED_BIT_SIZE, this.consentRecordCreated);
        bits.setInstantToEpochDeciseconds(GdprConstants.UPDATED_BIT_OFFSET, GdprConstants.UPDATED_BIT_SIZE, this.consentRecordLastUpdated);

        bits.setInt(GdprConstants.CMP_ID_OFFSET, GdprConstants.CMP_ID_SIZE, this.cmpID);
        bits.setInt(GdprConstants.CMP_VERSION_OFFSET, GdprConstants.CMP_VERSION_SIZE, this.cmpVersion);
        bits.setInt(GdprConstants.CONSENT_SCREEN_SIZE_OFFSET, GdprConstants.CONSENT_SCREEN_SIZE, this.consentScreenID);
        bits.setSixBitString(GdprConstants.CONSENT_LANGUAGE_OFFSET, GdprConstants.CONSENT_LANGUAGE_SIZE, this.consentLanguage);

        bits.setInt(GdprConstants.VENDOR_LIST_VERSION_OFFSET, GdprConstants.VENDOR_LIST_VERSION_SIZE, this.vendorListVersion);

        int i = 0;
        for (boolean purpose : allowedPurposes) {
            if (purpose) {
                bits.setBit(PURPOSES_OFFSET + i++);
            } else {
                bits.unsetBit(PURPOSES_OFFSET + i++);
            }
        }

        bits.setInt(GdprConstants.MAX_VENDOR_ID_OFFSET, GdprConstants.MAX_VENDOR_ID_SIZE, this.maxVendorId);
        bits.setInt(GdprConstants.ENCODING_TYPE_OFFSET, GdprConstants.ENCODING_TYPE_SIZE, this.vendorEncodingType);

        if (vendorEncodingType == GdprConstants.VENDOR_ENCODING_RANGE) {
            if (defaultConsent) {
                bits.setBit(GdprConstants.DEFAULT_CONSENT_OFFSET);
            } else {
                bits.unsetBit(GdprConstants.DEFAULT_CONSENT_OFFSET);
            }
            bits.setInt(GdprConstants.NUM_ENTRIES_OFFSET, GdprConstants.NUM_ENTRIES_SIZE, rangeEntries.size());

            int currentOffset = GdprConstants.RANGE_ENTRY_OFFSET;

            for (RangeEntry entry : rangeEntries) {
                if (entry.endVendorId > entry.startVendorId) { // range
                    bits.setBit(currentOffset++);
                    bits.setInt(currentOffset, GdprConstants.VENDOR_ID_SIZE, entry.startVendorId);
                    currentOffset += GdprConstants.VENDOR_ID_SIZE;
                    bits.setInt(currentOffset, GdprConstants.VENDOR_ID_SIZE, entry.endVendorId);
                    currentOffset += GdprConstants.VENDOR_ID_SIZE;
                } else {
                    bits.unsetBit(currentOffset++);
                    bits.setInt(currentOffset, GdprConstants.VENDOR_ID_SIZE, entry.startVendorId);
                    currentOffset += GdprConstants.VENDOR_ID_SIZE;
                }
            }
        } else {
            int bitfieldOffset = GdprConstants.VENDOR_BITFIELD_OFFSET;
            for (boolean vendorBit : bitfield) {
                if (vendorBit) {
                    bits.setBit(bitfieldOffset);
                }
                ++bitfieldOffset;
            }
        }
        this.consentString = encoder.encodeToString(bits.toByteArray());
    }

    /**
     * Constructor.
     *
     * @param consentString
     *            (required). The binary user consent data encoded as url and filename safe base64 string
     *
     * @throws GdprException
     *             if the consent string cannot be parsed
     */
    public static VendorConsent fromBase64String(String consentString) throws GdprException {
        try {
            if (isNullOrEmpty(consentString)) {
                throw new VendorConsentParseException("Consent String is empty or null");
            } else {
                byte[] consentAsBytes = decoder.decode(consentString);
                ConsentStringParser parser = new ConsentStringParser(consentAsBytes);
                return parser.parse();
            }
        } catch (VendorConsentException e) {
            throw new GdprException("Error parsing IAB Consent String", e.getCause());
        }
    }

    /**
     * @return the string passes in the constructor.
     *
     */
    public String getConsentString() {
        return consentString;
    }

    /**
     * @return the {@link Instant} at which the consent record was created
     */
    public Instant getConsentRecordCreated() {
        return consentRecordCreated;
    }

    /**
     *
     * @return the {@link Instant} at which the cookie was last updated
     */
    public Instant getConsentRecordLastUpdated() {
        return consentRecordLastUpdated;
    }

    /**
     *
     * @return the version of the cookie format used in this consent string
     */
    public int getVersion() {
        return version;
    }

    /**
     *
     * @return the id of the consent management partner that created this consent string
     */
    public int getCmpId() {
        return cmpID;
    }

    /**
     * @return the version of the cmp being used
     */
    public int getCmpVersion() {
        return cmpVersion;
    }

    /**
     *
     * @return the id of the string through which the user gave consent in the CMP UI
     */
    public int getConsentScreen() {
        return consentScreenID;
    }

    /**
     * @return The two letter ISO639-1 language code in which the CMP asked for consent
     */
    public String getConsentLanguage() {
        return consentLanguage;
    }

    public int getMaxVendorId() {
        return maxVendorId;
    }

    public boolean isDefaultConsent() {
        return defaultConsent;
    }

    public int getVendorEncodingType() {
        return vendorEncodingType;
    }

    public List<RangeEntry> getRangeEntries() {
        return rangeEntries;
    }

    public List<Boolean> getBitfield() {
        return bitfield;
    }

    public String getBinaryString() {
        return bits.getBinaryString();
    }

    /**
     *
     * @return a list of purpose id's which are permitted according to this consent string
     */
    public List<Integer> getAllowedPurposes() {
        if (integerPurposes != null) {
            return integerPurposes;
        }
        List<Integer> purposes = new ArrayList<>();
        for (int i = 1, ii = allowedPurposes.size(); i <= ii; i++) {
            if (isPurposeAllowed(i)) {
                purposes.add(i);
            }
        }
        integerPurposes = purposes;
        return purposes;

    }

    /**
     *
     * @return an integer equivalent of allowed purpose id bits according to this consent string
     */
    public int getAllowedPurposesBits() {
        return bits.getInt(PURPOSES_OFFSET, PURPOSES_SIZE);
    }

    /**
     *
     * @return the vendor list version which was used in creating this consent string
     */
    public int getVendorListVersion() {
        return vendorListVersion;
    }

    /**
     * @return a boolean describing the user consent status for a particular purpose. The lowest purpose ID is 1.
     */
    public boolean isPurposeAllowed(int purposeId) {
        if (purposeId < 1 || purposeId > allowedPurposes.size()) {
            return false;
        }
        return allowedPurposes.get(purposeId - 1);
    }

    public boolean arePurposesAllowed(List<Integer> purposeIds) {
        boolean allow = true;
        for (int purposeId : purposeIds) {
            allow = allow && isPurposeAllowed(purposeId);
        }
        return allow;
    }

    private boolean findVendorIdInRange(int vendorId) {
        for (RangeEntry entry : rangeEntries) {
            if (entry.containsVendorId(vendorId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return a boolean describing if a vendor has consented to a particular vendor. The lowest vendor ID is 1.
     *
     *         This method, along with {@link #isPurposeAllowed(int)} fully describes the user consent for a particular
     *         action by a given vendor.
     */
    public boolean isVendorAllowed(int vendorId) {
        if (vendorEncodingType == GdprConstants.VENDOR_ENCODING_RANGE) {
            boolean present = findVendorIdInRange(vendorId);
            return present != defaultConsent;
        } else {
            if (vendorId > 0 && vendorId <= maxVendorId) {
                return bitfield.get(vendorId - 1);
            }
            return false;
        }
    }

    private static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        VendorConsent consent = (VendorConsent) o;
        return version == consent.version && cmpID == consent.cmpID && cmpVersion == consent.cmpVersion
                && consentScreenID == consent.consentScreenID && vendorListVersion == consent.vendorListVersion
                && maxVendorId == consent.maxVendorId && vendorEncodingType == consent.vendorEncodingType
                && defaultConsent == consent.defaultConsent && Objects.equals(bits, consent.bits)
                && Objects.equals(consentRecordCreated, consent.consentRecordCreated)
                && Objects.equals(consentRecordLastUpdated, consent.consentRecordLastUpdated)
                && Objects.equals(consentLanguage, consent.consentLanguage)
                && Objects.equals(allowedPurposes, consent.allowedPurposes)
                && Objects.equals(consentString, consent.consentString)
                && Objects.equals(rangeEntries, consent.rangeEntries)
                && Objects.equals(integerPurposes, consent.integerPurposes);
    }

    @Override
    public int hashCode() {

        return Objects.hash(bits, version, consentRecordCreated, consentRecordLastUpdated, cmpID, cmpVersion,
                consentScreenID, consentLanguage, vendorListVersion, maxVendorId, vendorEncodingType, allowedPurposes,
                consentString, rangeEntries, defaultConsent, integerPurposes);
    }

    @Override
    public String toString() {
        return "VendorConsent{" + "bits=" + bits + ", version=" + version + ", consentRecordCreated="
                + consentRecordCreated + ", consentRecordLastUpdated=" + consentRecordLastUpdated + ", cmpID=" + cmpID
                + ", cmpVersion=" + cmpVersion + ", consentScreenID=" + consentScreenID + ", consentLanguage='"
                + consentLanguage + '\'' + ", vendorListVersion=" + vendorListVersion + ", maxVendorId=" + maxVendorId
                + ", vendorEncodingType=" + vendorEncodingType + ", allowedPurposes=" + allowedPurposes
                + ", consentString='" + consentString + '\'' + ", rangeEntries=" + rangeEntries + ", defaultConsent="
                + defaultConsent + ", integerPurposes=" + integerPurposes + '}';
    }

    // static classes
    public static class RangeEntry {
        /**
         * This class corresponds to the RangeEntry field given in the consent string specification.
         */
        private final int endVendorId;
        private final int startVendorId;

        public RangeEntry(int vendorId) {
            this.endVendorId = this.startVendorId = vendorId;
        }

        public RangeEntry(int startId, int endId) {
            this.endVendorId = endId;
            this.startVendorId = startId;
        }

        public boolean containsVendorId(int vendorId) {
            return vendorId >= startVendorId && vendorId <= endVendorId;
        }

        public boolean idIsGreaterThanMax(int vendorId) {
            return vendorId > endVendorId;
        }

        public boolean isIsLessThanMin(int vendorId) {
            return vendorId < startVendorId;
        }

        @Override
        public String toString() {
            return "RangeEntry{" + "endVendorId=" + endVendorId + ", startVendorId=" + startVendorId + '}';
        }
    }

    public static class Builder {
        private int version;
        private Instant consentRecordCreated;
        private Instant consentRecordLastUpdated;
        private int cmpID;
        private int cmpVersion;
        private int consentScreenID;
        private String consentLanguage;
        private int vendorListVersion;
        private int maxVendorId;
        private int vendorEncodingType;
        private List<Boolean> allowedPurposes = new ArrayList<>(PURPOSES_SIZE);
        // only used when bitfield is enabled
        private List<Integer> vendorsBitField;
        // only used when range entry is enabled
        private List<RangeEntry> rangeEntries;
        private boolean defaultConsent;
        private List<Integer> integerPurposes = null;

        /**
         * @param version
         *            Version of the Consent String Format
         */
        public Builder withVersion(int version) {
            this.version = version;
            return this;
        }

        /**
         * @param consentRecordCreated
         *            Epoch deciseconds when consent string was first created
         */
        public Builder withConsentRecordCreatedOn(Instant consentRecordCreated) {
            this.consentRecordCreated = consentRecordCreated;
            return this;
        }

        /**
         * @param consentRecordLastUpdated
         *            Epoch deciseconds when consent string was last updated
         */
        public Builder withConsentRecordLastUpdatedOn(Instant consentRecordLastUpdated) {
            this.consentRecordLastUpdated = consentRecordLastUpdated;
            return this;
        }

        /**
         * @param cmpID
         *            Consent Manager Provider ID that last updated the consent string
         */
        public Builder withCmpID(int cmpID) {
            this.cmpID = cmpID;
            return this;
        }

        /**
         * @param cmpVersion
         *            Consent Manager Provider version
         */
        public Builder withCmpVersion(int cmpVersion) {
            this.cmpVersion = cmpVersion;
            return this;
        }

        /**
         * @param consentScreenID
         *            Screen number in the CMP where consent was given
         */
        public Builder withConsentScreenID(int consentScreenID) {
            this.consentScreenID = consentScreenID;
            return this;
        }

        /**
         * @param consentLanguage
         *            Two-letter ISO639-1 language code that CMP asked for consent in. Each letter should be encoded as
         *            6 bits, A=0..Z=25 . This will result in the base64url-encoded bytes spelling out the language code
         *            (in uppercase).
         */
        public Builder withConsentLanguage(String consentLanguage) {
            this.consentLanguage = consentLanguage;
            return this;
        }

        /**
         * @param vendorListVersion
         *            Version of vendor list used in most recent consent string update
         */
        public Builder withVendorListVersion(int vendorListVersion) {
            this.vendorListVersion = vendorListVersion;
            return this;
        }

        /**
         * @param allowedPurposes
         *            For each Purpose, one bit: 0=No Consent 1=Consent Purposes are listed in the global Vendor List.
         *            Resultant consent value is the ?AND? of the applicable bit(s) from this field and a vendor?s
         *            specific consent bit. Purpose #1 maps to the first (most significant) bit, purpose #24 maps to the
         *            last (least significant) bit.
         */
        public Builder withAllowedPurposes(List<Integer> allowedPurposes) {
            this.integerPurposes = allowedPurposes;
            for (int i = 0; i < PURPOSES_SIZE; i++) {
                this.allowedPurposes.add(false);
            }
            for (int purpose : allowedPurposes) {
                this.allowedPurposes.set(purpose - 1, true);
            }
            return this;
        }

        /**
         * @param maxVendorId
         *            The maximum VendorId for which consent values are given.
         */
        public Builder withMaxVendorId(int maxVendorId) {
            this.maxVendorId = maxVendorId;
            return this;
        }

        /**
         * @param vendorEncodingType
         *            0=BitField 1=Range
         */
        public Builder withVendorEncodingType(int vendorEncodingType) {
            this.vendorEncodingType = vendorEncodingType;
            return this;
        }

        /**
         * @param bitFieldEntries
         *            List of VendorIds for which the vendors have consent
         */
        public Builder withBitField(List<Integer> bitFieldEntries) {
            this.vendorsBitField = bitFieldEntries;
            return this;
        }

        /**
         * @param rangeEntries
         *            List of VendorIds or a range of VendorIds for which the vendors have consent
         */
        public Builder withRangeEntries(List<RangeEntry> rangeEntries) {
            this.rangeEntries = rangeEntries;
            return this;
        }

        /**
         * @param defaultConsent
         *            Default consent for VendorIds not covered by a RangeEntry. 0=No Consent 1=Consent
         */
        public Builder withDefaultConsent(boolean defaultConsent) {
            this.defaultConsent = defaultConsent;
            return this;
        }

        public VendorConsent build() {
            return new VendorConsent(this);
        }
    }
}
package com.iab.gdpr.consent;

import com.iab.gdpr.Purpose;

import java.time.Instant;
import java.util.Set;

/**
 * Representation of the values in the vendor consent string.
 *
 * Combination of {@link VendorConsent#isPurposeAllowed(int)} and {@link VendorConsent#isVendorAllowed(int)} methods
 * fully describes user's consent for particular action by a particular vendor
 *
 */
public interface VendorConsent {

    /**
     *
     * @return the version of consent string format
     */
    int getVersion();

    /**
     * @return the {@link Instant} at which the consent string was created
     */
    Instant getConsentRecordCreated();

    /**
     *
     * @return the {@link Instant} at which consent string was last updated
     */
    Instant getConsentRecordLastUpdated();

    /**
     *
     * @return the Consent Manager Provider ID that last updated the consent string
     */
    int getCmpId();

    /**
     *
     * @return the Consent Manager Provider version
     */
    int getCmpVersion();

    /**
     *
     * @return the screen number in the CMP where consent was given
     */
    int getConsentScreen();

    /**
     *
     * @return the two-letter ISO639-1 language code that CMP asked for consent in
     */
    String getConsentLanguage();

    /**
     *
     * @return version of vendor list used in most recent consent string update.
     */
    int getVendorListVersion();

    /**
     *
     * @return the set of purpose id's which are permitted according to this consent string
     */
    Set<Integer> getAllowedPurposeIds();

    /**
     *
     * @return the set of allowed purposes which are permitted according to this consent string
     */
    Set<Purpose> getAllowedPurposes();

    /**
     *
     * @return an integer equivalent of allowed purpose id bits according to this consent string
     */
    int getAllowedPurposesBits();

    /**
     *
     * @return the maximum VendorId for which consent values are given.
     */
    int getMaxVendorId();

    /**
     * Check whether purpose with specified ID is allowed
     * @param purposeId purpose ID
     * @return true if purpose is allowed in this consent, false otherwise
     */
    boolean isPurposeAllowed(int purposeId);

    /**
     * Check whether specified purpose is allowed
     * @param purpose purpose to check
     * @return true if purpose is allowed in this consent, false otherwise
     */
    boolean isPurposeAllowed(Purpose purpose);

    /**
     * Check whether vendor with specified ID is allowd
     * @param vendorId vendor ID
     * @return a boolean describing if a user has consented to a particular vendor
     */
    boolean isVendorAllowed(int vendorId);

    /**
     *
     * @return the value of this consent as byte array
     */
    byte[] toByteArray();

}

package com.iab.gdpr;

/**
 * 
 * Enumeration of currently defined purposes by IAB
 *
 *  @see <a href="https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework">https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework</a>
 *
 */
public enum Purpose {

    STORAGE_AND_ACCESS(1), // The storage of information, or access to information that is already stored, on user device such as accessing advertising identifiers
                           // and/or other device identifiers, and/or using cookies or similar technologies.

    PERSONALIZATION(2),    // The collection and processing of information about user of a site to subsequently personalize advertising for them in other contexts,
                           // i.e. on other sites or apps, over time. Typically, the content of the site or app is used to make inferences about user interests, which inform future selections.

    AD_SELECTION(3),       // The collection of information and combination with previously collected information, to select and deliver advertisements and to measure the delivery
                           // and effectiveness of such advertisements. This includes using previously collected information about user interests to select ads, processing data about
                           // what advertisements were shown, how often they were shown, when and where they were shown, and whether they took any action related to the advertisement,
                           // including for example clicking an ad or making a purchase.

    CONTENT_DELIVERY(4),   // The collection of information, and combination with previously collected information, to select and deliver content and to measure the delivery and
                           // effectiveness of such content. This includes using previously collected information about user interests to select content, processing data about
                           // what content was shown, how often or how long it was shown, when and where it was shown, and whether they took any action related to the content,
                           // including for example clicking on content.

    MEASUREMENT(5),        // The collection of information about user use of content, and combination with previously collected information, used to measure, understand,
                           // and report on user usage of content.

    UNDEFINED(-1),         // Purpose ID that is currently not defined
    ;

    private final int id;

    Purpose(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    /**
     * Map numeric purpose ID to Enum
     * @param purposeId purpose ID
     * @return Enum value of purpose
     */
    public static Purpose valueOf(int purposeId) {
        switch(purposeId) {
            case 1: return STORAGE_AND_ACCESS;
            case 2: return PERSONALIZATION;
            case 3: return AD_SELECTION;
            case 4: return CONTENT_DELIVERY;
            case 5: return MEASUREMENT;
            default: return UNDEFINED;
        }
    }
}

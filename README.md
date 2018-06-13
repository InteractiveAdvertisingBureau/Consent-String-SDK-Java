
# Transparency and Consent Framework: Consent-String-SDK-Java

Encode and decode web-safe base64 consent information with the IAB EU's GDPR Transparency and Consent Framework.

This library is a Java reference implementation for dealing with consent strings in the IAB EU's GDPR Transparency and Consent Framework.  
It should be used by anyone who receives or sends consent information like vendors that receive consent data from a partner, or consent management platforms that need to encode/decode the global cookie.

The IAB specification for the consent string format is available on the [IAB Github](https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/master/Consent%20string%20and%20vendor%20list%20formats%20v1.1%20Final.md) (section "Vendor Consent Cookie Format").

**This library supports the version v1.1 of the specification. It can encode and decode consent strings with version bit 1.**

## IAB Europe Transparency and Consent Framework 

In November 2017, IAB Europe and a cross-section of the publishing and advertising industry, announced a new Transparency & Consent Framework to help publishers, advertisers and technology companies comply with key elements of GDPR. The Framework will give the publishing and advertising industries a common language with which to communicate consumer consent for the delivery of relevant online advertising and content. 

Framework Technical specifications available at: https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework 


# Consent String SDK (Java)
- [Installation](#installation)
- [Usage](#usage)
- [Building](#building)
- [Contributing](#contributing)
- [Versioning](#versioning)


## Installation

Add dependency to you pom.xml

```
<dependency>
  <groupId>com.conversantmedia.gdpr</groupId>
  <artifactId>consent-string-sdk-java</artifactId>
  <version>2.0.1</version>
</dependency>
```

## Usage

### Decoding consent string

```
final VendorConsent vendorConsent = VendorConsentDecoder.fromBase64String(consentString);

if (vendorConsent.isVendorAllowed(vendorId) && vendorConsent.isPurposeAllowed(STORAGE_AND_ACCESS) {
   ...
} else {
   ...
}

```

### Creating vendor consent
```
final VendorConsent vendorConsent = new VendorConsentBuilder()
        .withConsentRecordCreatedOn(now)
        .withConsentRecordLastUpdatedOn(now)
        .withCmpID(cmpId)
        .withCmpVersion(cmpVersion)
        .withConsentScreenID(consentScreenID)
        .withConsentLanguage(consentLanguage)
        .withVendorListVersion(vendorListVersion)
        .withAllowedPurposes(allowedPurposes)
        .withMaxVendorId(maxVendorId)
        .withVendorEncodingType(vendorEncodingType)
        .withDefaultConsent(false)
        .withRangeEntries(rangeEntries)
        .build();
```

### Encoding vendor consent to string
```
final String base64String = VendorConsentEncoder.toBase64String(vendorConsent); 
```

## Building

Use Gradle command to build the project
```
git clone https://github.com/InteractiveAdvertisingBureau/Consent-String-SDK-Java.git
cd  Consent-String-SDK-Java
./gradlew build
```

## Contributing

### Branching 
We use following branching setup
1. **master** branch is the current branch where active development is taking place and is associated with the latest release
1. Previous releases are under branches release/x, where x is the major release version, for example release/1. Those branches are used for bug bixes in previous releases
1. Each release version is associated with a git tag, for example tag v2.0.0

### Pull request procedures
1. Make sure there is a unit test for each added feature. If pull request is for bug fix, create a unit test that would trigger a bug
1. Make sure **all** tests pass
1. Update this document if usage is changing
 

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/tags). 

## About 

#### About IAB Tech Lab  

The IAB Technology Laboratory (Tech Lab) is a non-profit research and development consortium that produces and provides standards, software, and services to drive growth of an effective and sustainable global digital media ecosystem. Comprised of digital publishers and ad technology firms, as well as marketers, agencies, and other companies with interests in the interactive marketing arena, IAB Tech Lab aims to enable brand and media growth via a transparent, safe, effective supply chain, simpler and more consistent measurement, and better advertising experiences for consumers, with a focus on mobile and ?TV?/digital video channel enablement. The IAB Tech Lab portfolio includes the DigiTrust real-time standardized identity service designed to improve the digital experience for consumers, publishers, advertisers, and third-party platforms. Board members include AppNexus, ExtremeReach, Google, GroupM, Hearst Digital Media, Integral Ad Science, Index Exchange, LinkedIn, MediaMath, Microsoft, Moat, Pandora, PubMatic, Quantcast, Telaria, The Trade Desk, and Yahoo! Japan. Established in 2014, the IAB Tech Lab is headquartered in New York City with an office in San Francisco and representation in Seattle and London.

Learn more about IAB Tech Lab here: [https://www.iabtechlab.com/](https://www.iabtechlab.com/)

#### About IAB Europe 

IAB Europe is the voice of digital business and the leading European-level industry association for the interactive advertising ecosystem. Its mission is to promote the development of this innovative sector by shaping the regulatory environment, investing in research and education, and developing and facilitating the uptake of business standards.
 
Learn more about IAB Europe here: [https://www.iabeurope.eu/](https://www.iabeurope.eu/)


#### Contributors and Technical Governance

GDPR Technical Working Group members provide contributions to this repository. Participants in the GDPR Technical Working group must be members of IAB Tech Lab. Technical Governance for the project is provided by the IAB Tech Lab GDPR Commit Group. 

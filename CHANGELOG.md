# Changelog
All notable changes to this project will be documented in this file.

## [Unreleased]

## [2.0.1] - 06-13-2018

### Changed
- Optimize isPurposeAllowed() to just check the bit instead of creating unnecessary Set. Thanks to **hydrogen2**

## [2.0.0] - 06-07-2018

### Changed
- Refactored code to separate vendor consent string decoding, encoding and representation concerns
- Deprecated VendorConsent class
- Setup Maven publishing
- Updated documentation
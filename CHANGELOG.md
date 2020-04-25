# Changelog

## [0.1.5](https://github.com/SIMBAChain/libsimba4j/compare/v0.1.4...v0.1.5) (2020-04-25)


### Features

* Upated Jackson databind library from 2.9.10.1 to 2.9.10.4 because of security issue. 


## [0.1.4](https://github.com/SIMBAChain/libsimba4j/compare/v0.1.3...v0.1.4) (2019-11-28)


### Bug Fixes

* Balance amount is now a string
* transaction hex numbers are properly converted to BigInteger


### ⚠ BREAKING CHANGES

* Removed `FullTransaction.getErrorDetails()` method.


## [0.1.3](https://github.com/SIMBAChain/libsimba4j/compare/v0.1.2...v0.1.3) (2019-11-13)


### Bug Fixes

* Mnemonic Wallet was not recreating the same keys for the same mnemonic

### Features

* Added `Wallet.getPrivateKey()` method.


### ⚠ BREAKING CHANGES

* Removed `Wallet.loadOrCreateMnemonicWallet(String passkey)` method.


## [0.1.2](https://github.com/SIMBAChain/libsimba4j/compare/v0.1.1...v0.1.2) (2019-11-06)


### Features

* Upated Jackson databind library from 2.9.10 to 2.9.10.1 because of security issue. 



## [0.1.1](https://github.com/SIMBAChain/libsimba4j/compare/v0.1.0-alpha...v0.1.1) (2019-10-21)


### Features

* Improved nonce error handling.
* Asynch Interface.

### Bug Fixes

* Various minor fixes.



## 0.1.0-alpha (2019-09-30)

* Initial checkin.
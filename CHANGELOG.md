# Changelog

## [0.1.8](https://github.com/SIMBAChain/libsimba4j/compare/v0.1.7...v0.1.8) (2021-05-20)


### Features

* Updated Jackson databind library to 2.10.5.1

## [0.1.7](https://github.com/SIMBAChain/libsimba4j/compare/v0.1.6...v0.1.7) (2020-06-28)


### Features

* Updated Jackson databind library

## [0.1.8](https://github.com/SIMBAChain/libsimba4j/compare/v0.1.7...v0.1.8) (2020-05-18)


* Broke out generic HTTP functionality into SimbaClient  

## [0.1.6](https://github.com/SIMBAChain/libsimba4j/compare/v0.1.5...v0.1.6) (2020-05-18)


### Features

* Updated Jackson databind library from 2.9.10.4 to 2.10.4.
* Replaced some classes with interfaces for extensibility.
* Added some new interfaces for extensibility.
* Updated copyright  

### ⚠ BREAKING CHANGES

* Removed `Simba.waitForTransactionInitialized` method. This state is being deprecated.

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

* Updated Jackson databind library from 2.9.10 to 2.9.10.1 because of security issue. 



## [0.1.1](https://github.com/SIMBAChain/libsimba4j/compare/v0.1.0-alpha...v0.1.1) (2019-10-21)


### Features

* Improved nonce error handling.
* Asynch Interface.

### Bug Fixes

* Various minor fixes.



## 0.1.0-alpha (2019-09-30)

* Initial checkin.
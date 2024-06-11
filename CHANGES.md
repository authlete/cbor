CHANGES
=======

1.18 (2024-06-11)
-----------------

- `CWTKeyProofBuilder` class
  * Added a new command line option `--issued-at TIME` which specifies
    the issuance time.

1.17 (2024-06-10)
-----------------

- `CWTKeyProofBuilder` class
  * Fixed a bug in the `buildProtectedHeader(COSEKey)` method. A COSE key
    should be wrapped into a byte string before being embedded in the
    protected header.

1.16 (2024-06-05)
-----------------

- `IssuerSignedBuilder` class
  * Fixed a bug in the `prepareIssuerAuthPayload(IssuerNameSpaces)` method.
    The definitions of `IssuerAuth` and `MobileSecurityObjectBytes` in the
    ISO/IEC 18013-5 specification give the impression that
    `MobileSecurityObjectBytes` (which starts with a CBOR tag) is directly
    used as the payload of `COSE_Sign1`. However, it is necessary to
    further convert `MobileSecurityObjectBytes` into a byte string.

- `SigStructure` class
  * Changed the type of the fourth argument (`payload`) of the 4-argument
    constructor from `CBORItem` to `CBORByteArray`.
  * Changed the type of the third argument (`payload`) of the 3-argument
    constructor from `CBORItem` to `CBORByteArray`.
  * Changed the return type of the `getPayload()` method from `CBORItem` to
   `CBORByteArray`.

- `SigStructureBuilder` class
  * Removed the `payload(CBORItem)` method.

1.15 (2024-05-11)
-----------------

- `COSEKey` class
  * Add the `toPublic()` method.
  * Add the `createPrivateKey()` method.
  * Add the `createPublicKey()` method.
  * Add the `copy()` method.

- `COSEEC2Key` class
  * Override the `toPublic()` method.
  * Override the `createPrivateKey()` method.
  * Override the `createPublicKey()` method.

- New types
  * `CWTKeyProofBuilder` class

- Others
  * Add the `bin/generate-cwt-key-proof` script.

1.14 (2023-12-24)
-----------------

- Lower the minimum Java version from 11 to 8.

- Switch from JUnit 4 to JUnit 5.

- `CBORItemList` class
  * Add the `CBORItemList(T...)` constructor.

- `CBORPairList` class
  * Add the `CBORPairList(T...)` constructor.

- `ECDSA` class
  * Load the `BouncyCastleProvider` and adjust the algorithm name passed to
    the `Signature.getInstance(String)` method when the JRE is older than 9.

1.13 (2023-12-23)
-----------------

- `CBORItem` class
  * Fix the `encodeToBase64Url()` method to drop padding characters.

1.12 (2023-12-23)
-----------------

- New Types
  * `com.authlete.mdoc.constants` package with the following classes.
    - `MDLClaimNames` class
    - `MDLConstants` class

1.11 (2023-12-22)
-----------------

- `CBORPairsBuilder` class
  * Add the `add(Object, Object)` method.
  * Add the `addUnlessNull(Object, Object)` method.

- `COSEKey` class
  * Add the `fromJwk(Map<String, Object>)` method.

1.10 (2023-12-05)
-----------------

- `CBORByteArray` class
  * Bugfix of `toString` and `prettify`.

1.9 (2023-12-05)
----------------

- `CBORByteArray` class
  * Bugfix of tag number comparison.

1.8 (2023-12-05)
----------------

- `CBORItem` class
  * Add the `toString(Number)` method.

- `CBORByteArray` class
  * Add the `toString(Number)` method.

1.7 (2023-12-05)
----------------

Introduce a mechanism to support CBOR tags of "Content Hints" (RFC 8949 Section 3.4.5).

- `CBORByteArray` class
  * Add the `prettify(String, String, Number)` method.
  * Remove the `prettify(String, String)` method.

- `CBORItem` class
  * Add the `prettify(String, String, Number)` method.
  * Remove the `prettify(String, String)` method.

- `CBORItemList` class
  * Add the `prettify(String, String, Number)` method.
  * Remove the `prettify(String, String)` method.

- `CBORPairList` class
  * Add the `prettify(String, String, Number)` method.
  * Remove the `prettify(String, String)` method.

- `CBORTaggedItem` class
  * Add the `prettify(String, String, Number)` method.
  * Remove the `prettify(String, String)` method.

1.6 (2023-12-03)
----------------

- `CBORDouble` class
  * Add `POSITIVE_INFINITY`.
  * Add `NEGATIVE_INFINITY`.
  * Add `NaN`.

- `CBORFloat` class
  * Add `POSITIVE_INFINITY`.
  * Add `NEGATIVE_INFINITY`.
  * Add `NaN`.

1.5 (2023-12-03)
----------------

- `CBORByteArray` class
  * Add the `CBORByteArray(byte[], CBORItem)` constructor.
  * Add the `CBORByteArray(byte[], List<? extends CBORItem>)` constructor.
  * Add the `getDecodedContent()` method.
  * Add the `prettify(String, String)` method.
  * Remove the `CBORByteArray(byte[], boolean)` constructor.
  * Remove the `decodeValue()` method.

- `CBORizer` class
  * Add the `getDiagnosticNotationPrefix()` method.
  * Add the `setDiagnosticNotationPrefix(String)` method.
  * Add the `getDiagnosticNotationParser()` method.
  * Add the `setDiagnosticNotationParser(CBORDiagnosticNotationParser)` method.

- `CBORItem` class
  * Add the `getComment()` method.
  * Add the `setComment(String)` method.
  * Add the `encodeToBase64()` method.
  * Add the `encodeToBase64Url()` method.
  * Add the `encodeToHex()` method.
  * Add the `prettify()` method.
  * Add the `prettify(String, String)` method.

- `CBORItemList` class
  * Change the type of the argument passed to the constructor from `List<CBORItem>` to
    `List<? extends CBORItem>`. This change affects other classes, mainly subclasses.
  * Add the `prettify(String, String)` method.

- `CBORPair` class
  * Add the `CBORPair(CBORItem, CBORItem, String, String)` constructor.
  * Add the `getKeyComment()` method.
  * Add the `setKeyComment(String)` method.
  * Add the `getValueComment()` method.
  * Add the `setValueComment(String)` method.
  * Add the `prettify(String, String)` method.

- `CBORPairList` class
  * Change the type of the argument passed to the constructor from `List<CBORPair>` to
    `List<? extends CBORPair>`. This change affects other classes, mainly subclasses.
  * Add the `prettify(String, String)` method.
  * Add the `findByKey(Object)` method.

- `CBORSign1Builder` class
  * Add the `payload(CBORItem)` method.

- `CBORSignBuilder` class
  * Add the `payload(CBORItem)` method.

- `CBORTaggedItem` class
  * Add the `prettify(String, String)` method.

- `COSEEllipticCurves` class
  * Add `P_256_NAME`.
  * Add `P_384_NAME`.
  * Add `P_521_NAME`.
  * Add `X25519_NAME`.
  * Add `X448_NAME`.
  * Add `Ed25519_NAME`.
  * Add `Ed448_NAME`.
  * Add `SECP256K1_NAME`.

- `COSEHeaderBuilder` class
  * Add the `alg(Object)` method.
  * Add the `x5chain(List<X509Certificate> chain)` method.

- `COSEHeaderParameters' class
  * Add the `getNameByValue(int)` method.

- `COSEKeyBuilder` class
  * Add the `okpXInBase64Url(String)` method.
  * Add the `okpDInBase64Url(String)` method.
  * Add the `baseIvInBase64Url(String)` method.
  * Add the `ec2XInBase64Url(String)` method.
  * Add the `ec2YInBase64Url(String)` method.
  * Add the `ec2DInBase64Url(String)` method.
  * Add the `rsaNInBase64Url(String)` method.
  * Add the `rsaEInBase64Url(String)` method.
  * Add the `rsaDInBase64Url(String)` method.
  * Add the `rsaPInBase64Url(String)` method.
  * Add the `rsaQInBase64Url(String)` method.
  * Add the `rsaDPInBase64Url(String)` method.
  * Add the `rsaDQInBase64Url(String)` method.
  * Add the `rsaQInvInBase64Url(String)` method.
  * Add the `rsaRIInBase64Url(String)` method.
  * Add the `rsaDIInBase64Url(String)` method.
  * Add the `rsaTIInBase64Url(String)` method.
  * Add the `symmetricKInBase64Url(String)` method.
  * Add the `hssLmsPubInBase64Url(String)` method.

- `COSEMessageType` enum
  * Add the `getName()` method.

- `SigStructure` class
  * Change the type of the `payload` argument of the constructors from
    `CBORByteArray` to `CBORItem`. This is because the "ISO/IEC 18013-5:2021"
    standard requires that the payload start with a tag instead of a byte string.

- `SigStructureBuilder` class
  * Add the `payload(CBORItem)` method.

- New Types
  * `CBORPairsBuilder` class
  * `CBORDiagnosticNotationException` class
  * `CBORDiagnosticNotationParser` interface
  * `com.authlete.mdoc` package containing new 36 public classes.

1.4 (2023-08-14)
----------------

- `COSEMessage` class
  * Change the type of the first argument of the constructor from `int` to `COSEMessageType`.
  * Add the `getType()` method.

- New Types
  * `COSEMessageType` enum
  * `CWTClaims` class
  * `CWTClaimsSet` class
  * `CWTClaimsSetBuilder` class

1.3 (2023-08-12)
----------------

- `COSEAlgorithms` enum
  * Add `RS512`, `RS384`, `RS256` and `ES256K`.

- `COSEEC2Key` class
  * Override the `isPrivate()` method.
  * Override the `addJwkProperties(Map)` method.

- `COSEKey` class
  * Add the `isPrivate()` method.
  * Add the `toJwk()` method.
  * Add the `addJwkProperties(Map)` method.

- `COSEOKPKey` class
  * Override the `isPrivate()` method.
  * Override the `addJwkProperties(Map)` method.

1.2 (2023-08-11)
----------------

- Support the `x5chain` header parameter (RFC 9360).

1.1 (2023-06-04)
----------------

- Basic components for COSE.
- Signing and verification for ECDSA.

1.0 (2023-05-22)
----------------

The initial implementation that supports CBOR.

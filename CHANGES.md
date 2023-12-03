CHANGES
=======

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

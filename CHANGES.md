CHANGES
=======

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

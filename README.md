# Java Library for CBOR, COSE and CWT

## Overview

This is a Java library for **CBOR** (Concise Binary Object Representation),
**COSE** (CBOR Object Signing and Encryption) and **CWT** (CBOR Web Token).

NOTE: Under Development

- [x] CBOR
- [ ] COSE
- [ ] CWT

## License

  Apache License, Version 2.0

## Maven

```xml
<dependency>
    <groupId>com.authlete</groupId>
    <artifactId>cbor</artifactId>
    <version>${cbor.version}</version>
</dependency>
```

Check the [CHANGES.md](CHANGES.md) file to know the latest version.

## Source Code

  <code>https://github.com/authlete/cbor</code>

## JavaDoc

  <code>https://authlete.github.io/cbor</code>

## Standard Specifications

The following is a non-exhaustive list of standard specifications related to CBOR.
The list does not necessarily mean that this library supports all of them.

### IETF RFC

- [RFC 7049][RFC_7049] Concise Binary Object Representation (CBOR) | obsoleted by [RFC 8949][RFC_8949]
- [RFC 8152][RFC_8152] CBOR Object Signing and Encryption (COSE) | obsoleted by [RFC 9052][RFC_9052], [RFC 9053][RFC_9053]
- [RFC 8392][RFC_8392] CBOR Web Token (CWT)
- [RFC 8610][RFC_8610] Concise Data Definition Language (CDDL): A Notational Convention to Express Concise Binary Object Representation (CBOR) and JSON Data Structures
- [RFC 8613][RFC_8613] Object Security for Constrained RESTful Environments (OSCORE)
- [RFC 8742][RFC_8742] Concise Binary Object Representation (CBOR) Sequences
- [RFC 8746][RFC_8746] Concise Binary Object Representation (CBOR) Tags for Typed Arrays
- [RFC 8943][RFC_8943] Concise Binary Object Representation (CBOR) Tags for Date
- [RFC 8949][RFC_8949] Concise Binary Object Representation (CBOR)
- [RFC 9052][RFC_9052] CBOR Object Signing and Encryption (COSE): Structures and Process
- [RFC 9053][RFC_9053] CBOR Object Signing and Encryption (COSE): Initial Algorithms
- [RFC 9090][RFC_9090] Concise Binary Object Representation (CBOR) Tags for Object Identifiers
- [RFC 9338][RFC_9338] CBOR Object Signing and Encryption (COSE): Countersignatures

### IANA Assignments

- IANA: [Concise Binary Object Representation (CBOR) Simple Values][IANA_cbor_simple_values]
- IANA: [Concise Binary Object Representation (CBOR) Tags][IANA_cbor_tags]
- IANA: [CBOR Object Signing and Encryption (COSE)][IANA_cose]
- IANA: [CBOR Web Token (CWT) Claims][IANA_cwt]

## Description

### CBOR Reading

A byte sequence of CBOR data items is parsed to instances of common Java
classes by going through the steps below in the shown order.

1. a byte sequence (`InputStream` or `byte` array)
2. `CBORTokenizer`
3. `CBORDecoder`
4. `CBORParser`

Developers may use the `CBORTokenizer` class and the `CBORDecoder` class
directly, but the simplest way is to use the `CBORParser` class.

The usage of the `CBORParser` class is simple. It's just to create an instance
and call the `next()` method. The following code is an example to read a CBOR
data item that represents a text string `"a"`.

```java
// === String ===

// CBOR data item representing "a".
//
//   major = 3  ; text string
//   info  = 1  ; length
//
byte[] input = { (byte)0x61, (byte)0x61 };

// Parse the CBOR data item.
Object object = new CBORParser(input).next();

// The object should be a String instance.
assertEquals("a", object);
```

#### Integers

Unsigned integers (major type = 0), negative integers (major type = 1), and
bignums (tag number = 2 or 3; [RFC 8949, Section 3.4.3. Bignums][RFC_8949_bignums])
are converted to instances of `Integer`, `Long` or `BigInteger`. Which Java
class is chosen depends on the actual value. Using the tag for bignums does
not necessarily result in that `BigInteger` is chosen. Conversely, if the
number represented using the major type 0 is greater than `Long.MAX_VALUE`,
`BigInteger` is chosen.

The following are examples to parse integers.

```java
// === Integer ===

// CBOR data item representing 100.
//
//   major =  0  ; unsigned integer
//   info  = 24  ; followed by 1 byte
//
byte[] input = { (byte)0x18, (byte)0x64 };

// Parse the CBOR data item.
Object object = new CBORParser(input).next();

// The object should be an Integer instance.
assertEquals(Integer.valueOf(100), object);
```

```java
// === Long ===

// CBOR data item representing 1000000000000.
//
//   major =  0  ; unsigned integer
//   info  = 27  ; followed by 8 bytes
//
byte[] input = {
        (byte)0x1b, (byte)0x00, (byte)0x00, (byte)0x00,
        (byte)0xe8, (byte)0xd4, (byte)0xa5, (byte)0x10,
        (byte)0x00
};

// Parse the CBOR data item.
Object object = new CBORParser(input).next();

// The object should be a Long instance.
assertEquals(Long.valueOf(1000000000000L), object);
```

```java
// === BigInteger ===

// CBOR data items representing 18446744073709551616.
//
//   major = 6  ; tag
//   info  = 2  ; tag number 2, bignum (RFC 8949 Section 3.4.3)
//
//   major = 2  ; byte string
//   info  = 9  ; length
//
byte[] input = {
        (byte)0xC2, (byte)0x49, (byte)0x01, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00
};

// Parse the CBOR data item.
Object object = new CBORParser(input).next();

// The object should be a BigInteger instance.
assertEquals(new BigInteger("18446744073709551616"), object);
```

#### Floating-Point Numbers

Half-precision floating-point numbers (major type = 7, additional info = 25)
and single-precision floating-point numbers (major type = 7, additional info
= 26) are converted to `Float` instances. As Java does not natively support
half-precision floating-point numbers, CBOR half-precision floating-point
numbers become single-precision ones after conversion.

```java
// === Float ===

// CBOR data item representing -0.625f in the IEEE 754
// floating-point half-precision format.
//
//   major =  7  ; floating point
//   info  = 25  ; half precision
//
byte[] input = { (byte)0xF9, (byte)0xB9, (byte)0x00 };

// Parse the CBOR data item.
Object object = new CBORParser(input).next();

// The object should be a Float instance.
assertEquals(Float.valueOf(-0.625f), object);
```

On the other hand, double-precision numbers (major type 7, additional info = 27)
are converted to `Double` instances.

```java
// === Double ==

// CBOR data item representing 1.1 in the IEEE 754
// floating-point double-precision format.
//
//   major =  7  ; floating point
//   info  = 27  ; double precision
//
byte[] input = {
        (byte)0xFB, (byte)0x3F, (byte)0xF1, (byte)0x99,
        (byte)0x99, (byte)0x99, (byte)0x99, (byte)0x99,
        (byte)0x9A
};

#### Boolean

`false` (major type = 7, additional info = 20) and `true` (major type = 7,
additional info = 21) are converted to `Boolean.FALSE` and `Boolean.TRUE`,
respectively.

```java
// === Boolean ===

// CBOR data item representing true.
//
//   major =  7  ; simple value
//   info  = 21  ; true
//
byte[] input = { (byte)0xF5 };

// Parse the CBOR data item.
Object object = new CBORParser(input).next();

// The object should be the existing Boolean instance.
assertSame(Boolean.TRUE, object);
```

#### Byte Arrays

Byte strings (major type = 2) are converted to `byte[]` instances.

```java
// === byte[] ===
//
// CBOR data item representing h'01020304'.
//
//   major = 2  ; byte string
//   info  = 4  ; length
//
byte[] input = {
        (byte)0x44, (byte)0x01, (byte)0x02, (byte)0x03,
        (byte)0x04
};

// Expected result.
byte[] expected = {
        (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04
};

// Parse the CBOR data item.
Object object = new CBORParser(input).next();

// The object should be a byte array.
assertSame(byte[].class, object.getClass());
assertArrayEquals(expected, (byte[])object);
```

#### Arrays

Arrays (major type = 4) are converted to instances of `List<Object>`.

```java
// === Collection ===

// CBOR data items representing [1, 2, 3].
byte[] input = {
        (byte)0x83, (byte)0x01, (byte)0x02, (byte)0x03
};

// Parse the CBOR data item.
Object object = new CBORParser(input).next();

// The object should implement the List interface.
assertTrue(object instanceof List);
List<Object> list = (List<Object>)object;

assertEquals(1, list.get(0));
assertEquals(2, list.get(1));
assertEquals(3, list.get(2));
```

#### Maps

Maps (major type = 5) are converted to instances of `Map<Object, Object>`.

```java
// === Map ===

// CBOR data items representing {"a": 1, "b": [2, 3]}.
byte[] input = {
        (byte)0xA2, (byte)0x61, (byte)0x61, (byte)0x01,
        (byte)0x61, (byte)0x62, (byte)0x82, (byte)0x02,
        (byte)0x03
};

// Parse the CBOR data item.
Object object = new CBORParser(input).next();

// The object should implement the Map interface.
assertTrue(object instanceof Map);
Map<Object, Object> map = (Map<Object, Object>)object;

// "a": 1
assertEquals(1, map.get("a"));

// "b": [2, 3]
List<Object> sublist = (List<Object>)map.get("b");
assertEquals(2, sublist.get(0));
assertEquals(3, sublist.get(1));
```

#### Other Types

Simple values (major type = 7, additional info <= 24) are converted to
`Integer` instances.

The following are converted to `null`.

| type      | major type | additional info |
|:----------|:----------:|:---------------:|
| null      |      7     |        22       |
| undefined |      7     |        23       |
| break     |      7     |        31       |

#### Tags

The tokenizer (`CBORTokenizer`) and the decoder (`CBORDecoder`) that work
behind the scenes recognize tags (major type = 6), but the parser
(`CBORParser`) drops tags when it converts intermediate objects (`CBORItem`
instances) into instances of common Java classes.

For instance, as the example below shows, the `next()` method of `CBORParser`
ignores the tag number 32 ([RFC 8949, Section 3.4.5.3. Encoded Text][RFC_8949_encoded_text]),
which indicates that the following text string is a URI, and simply returns
the tag content as if the tag did not exist.

```java
// === URI ===

// CBOR data item representing "http://www.example.com" with
// the tag number 32 (RFC 8949, Section 3.4.5.3 Encoded Text).
//
//   Diagnostic: 32("http://www.example.com")
//   Encoded:    0xd82076687474703a2f2f7777772e6578616d706c652e636f6d
//
byte[] input = {
        (byte)0xd8, (byte)0x20, (byte)0x76, (byte)0x68,
        (byte)0x74, (byte)0x74, (byte)0x70, (byte)0x3a,
        (byte)0x2f, (byte)0x2f, (byte)0x77, (byte)0x77,
        (byte)0x77, (byte)0x2e, (byte)0x65, (byte)0x78,
        (byte)0x61, (byte)0x6d, (byte)0x70, (byte)0x6c,
        (byte)0x65, (byte)0x2e, (byte)0x63, (byte)0x6f,
        (byte)0x6d
};

// Expected text.
String expectedText = "http://www.example.com";

// Parse the CBOR data item.
Object object = new CBORParser(input).next();

// The object should be a String instance.
assertEquals(expectedText, object);
```

If you wish that text strings tagged by the tag number 32 are converted into
`URI` instances automatically, you need to register a tag processor that does
the job into the decoder.

The example below shows how to register a tag processor for the purpose. The
`CPUri` class used in the example is a tag processor which implements the
`CBORTagProcessor` interface.

```java
// Create a parser.
CBORParser parser = new CBORParser(input);

// Register a tag processor that converts a text string tagged
// by the tag number 32 into a URI instance.
parser.getDecoder().getOptions().setTagProcessor(32, new CPUri());

// Parse the CBOR data item.
object = parser.next();

// The object should be a URI instance.
assertEquals(URI.class, object.getClass());
assertEquals(expectedText, ((URI)object).toString());
```

Note that a tag processor for the tag number 2 and 3
([RFC 8949, Section 3.4.3. Bignums][RFC_8949_bignums]) is registered by
default because the following is written in the constructor of the
`CBORDefaultDecoderOptions` class.

```java
// RFC 8949, 3.4.3. Bignums
setTagProcessor(2, CPBignum.INSTANCE);
setTagProcessor(3, CPBignum.INSTANCE);
```

### CBOR Writing

TBW

## Contact

Authlete Contact Form: https://www.authlete.com/contact/

<!-- ==================== LINKS ==================== -->

[RFC_7049]: https://www.rfc-editor.org/rfc/rfc7049.html
[RFC_8152]: https://www.rfc-editor.org/rfc/rfc8152.html
[RFC_8392]: https://www.rfc-editor.org/rfc/rfc8392.html
[RFC_8610]: https://www.rfc-editor.org/rfc/rfc8610.html
[RFC_8613]: https://www.rfc-editor.org/rfc/rfc8613.html
[RFC_8742]: https://www.rfc-editor.org/rfc/rfc8742.html
[RFC_8746]: https://www.rfc-editor.org/rfc/rfc8746.html
[RFC_8943]: https://www.rfc-editor.org/rfc/rfc8943.html
[RFC_8949]: https://www.rfc-editor.org/rfc/rfc8949.html
[RFC_8949_bignums]: https://www.rfc-editor.org/rfc/rfc8949.html#name-bignums
[RFC_8949_encoded_text]: https://www.rfc-editor.org/rfc/rfc8949.html#name-encoded-text
[RFC_9052]: https://www.rfc-editor.org/rfc/rfc9052.html
[RFC_9053]: https://www.rfc-editor.org/rfc/rfc9053.html
[RFC_9090]: https://www.rfc-editor.org/rfc/rfc9090.html
[RFC_9338]: https://www.rfc-editor.org/rfc/rfc9338.html

[IANA_cbor_simple_values]: https://www.iana.org/assignments/cbor-simple-values/cbor-simple-values.xhtml
[IANA_cbor_tags]: https://www.iana.org/assignments/cbor-tags/cbor-tags.xhtml
[IANA_cose]: https://www.iana.org/assignments/cose/cose.xhtml
[IANA_cwt]: https://www.iana.org/assignments/cwt/cwt.xhtml

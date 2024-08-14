# Java Library for CBOR, COSE, CWT and mdoc

## Overview

This is a Java library for **CBOR** (Concise Binary Object Representation),
**COSE** (CBOR Object Signing and Encryption), **CWT** (CBOR Web Token) and
**mdoc** ([ISO/IEC 18013-5:2021][ISO_IEC_18013_5_2021]).

The current implementation supports the features below. More features will
be added as the need arises.

- [x] CBOR
- [x] COSE
  - [x] Basic Components
  - [x] Signature
    - [x] `ES256`
    - [x] `ES384`
    - [x] `ES512`
    - [x] `EdDSA`
    - [ ] ?
  - [ ] Encryption
  - [ ] MAC
- [x] CWT
  - [x] Tag Processor
  - [x] CWT Claims Set
  - [x] CWT Key Proof
- [x] mdoc

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

  <code>https://authlete.github.io/cbor/index.html?overview-summary.html</code>

## Standard Specifications

The following is a non-exhaustive list of standard specifications related to CBOR.
The list does not necessarily mean that this library supports all of them.

### IETF RFC

- [RFC 7049][RFC_7049] Concise Binary Object Representation (CBOR) | obsoleted by [RFC 8949][RFC_8949]
- [RFC 8152][RFC_8152] CBOR Object Signing and Encryption (COSE) | obsoleted by [RFC 9052][RFC_9052], [RFC 9053][RFC_9053]
- [RFC 8230][RFC_8230] Using RSA Algorithms with CBOR Object Signing and Encryption (COSE) Messages
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
- [RFC 9360][RFC_9360] CBOR Object Signing and Encryption (COSE): Header Parameters for Carrying and Referencing X.509 Certificates

### IANA Assignments

- IANA: [Concise Binary Object Representation (CBOR) Simple Values][IANA_cbor_simple_values]
- IANA: [Concise Binary Object Representation (CBOR) Tags][IANA_cbor_tags]
- IANA: [CBOR Object Signing and Encryption (COSE)][IANA_cose]
- IANA: [CBOR Web Token (CWT) Claims][IANA_cwt]

### ISO/IEC

- [ISO/IEC 18013-5:2021][ISO_IEC_18013_5_2021] Personal identification - ISO-compliant driving licence, Part 5: Mobile driving licence (mDL) application

### OpenID

- [OpenID for Verifiable Credential Issuance][OID4VCI]

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
```

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

Note that some tag processors are registered by default because the following
is written in the constructor of the `CBORDefaultDecoderOptions` class.

```java
// RFC 8949 CBOR, 3.4.3. Bignums
setTagProcessor(2, CPBignum.INSTANCE);
setTagProcessor(3, CPBignum.INSTANCE);

// RFC 9052 COSE
setTagProcessor(16, COSEMessageTagProcessor.INSTANCE);  // 16: COSE_Encrypt0
setTagProcessor(17, COSEMessageTagProcessor.INSTANCE);  // 17: COSE_Mac0
setTagProcessor(18, COSEMessageTagProcessor.INSTANCE);  // 18: COSE_Sign1

// RFC 8392 CWT
setTagProcessor(61, CWTTagProcessor.INSTANCE);

// RFC 9052 COSE
setTagProcessor(96, COSEMessageTagProcessor.INSTANCE);  // 96: COSE_Encrypt
setTagProcessor(97, COSEMessageTagProcessor.INSTANCE);  // 97: COSE_Mac
setTagProcessor(98, COSEMessageTagProcessor.INSTANCE);  // 98: COSE_Sign
```

### CBOR Writing

The `CBORItem` class represents a CBOR data item. All objects that support
CBOR encoding and decoding have the class as their root.

#### encode Method

The `CBORItem` class defines an abstract method `encode(OutputStream)`.
By calling the method, a CBOR-encoded byte sequence that represents the
CBOR data item is written into the specified output stream.

```java
// A CBOR data item.
CBORItem item = ...;

// An output stream.
OutputStream outputStream = ...;

// Write the CBOR-encoded byte sequence that represents
// the CBOR data item into the output stream.
item.encode(outputStream);
```

The `CBORItem` class also provides a utility method `encode()`. The method
returns a byte array that represents a CBOR-encoded byte sequence of the CBOR
data item. The implementation of the method calls the `encode(OutputStream)`
method internally to build the byte array.

```java
// A CBOR data item.
CBORItem item = ...;

// The CBOR-encoded byte sequence of the CBOR data item.
byte[] encoded = item.encode();
```

#### CBORizer

The `CBORizer` class is a utility that converts Java primitives and instances
of common Java classes into instances of `CBORItem` subclasses.

For example, the `cborizeInteger(int)` method of the `CBORizer` class generates
a `CBORInteger` instance from a given integer.

```java
// Build a CBORInteger instance from a Java 'int'.
CBORInteger item = new CBORizer().cborizeInteger(10);
```

Once you get an instance of `CBORItem` subclass, you can get the CBOR encoding
of the CBOR data item by calling the `encode(OutputStream)` method or the
`encode()` method as explained in the previous section.

Among the utility methods provided by the `CBORizer` class, the following does
CBOR conversion recursively as necessary.

- `cborizeCollection(Collection)`
- `cborizeMap(Map)`
- `cborizeObject(Object)`
- `cborize(Object)`

The following example shows that the `cborizeMap(Map)` method works recursively.
Also, the example shows that the `encode()` method works recursively, too.

```java
// Create a Map instance that represents the following:
//
//   {"a": 1, "b": [2, 3]}
//
Map<Object, Object> map = new LinkedHashMap<>();
map.put("a", 1);
map.put("b", List.of(2, 3));

// Convert the Map instance into a CBOR map. The cborizeMap()
// method returns an instance of the CBORPairList class (which
// represents a CBOR map) unless the given map is null.
CBORPairList pairList = (CBORPairList)new CBORizer().cborizeMap(map);

// Get the CBOR encoding of the CBOR map. The encode() method
// of the CBORPairList class calls the encode(OutputStream)
// method of the key-value pairs it holds repeatedly. As a
// result, the whole content of the CBOR map is encoded into
// a byte array. The resultant byte array will hold:
//
//   A26161016162820203
//
byte[] encoded = pairList.encode();
```

The `cborizeObject(Object)` method delegates conversion to one of other
`cborizeXxx` methods according to the class of the given object. If the
class of the given object is unknown, `cborizeObject(Object)` gets the
string representation of the given object by calling `toString()` method
and calls the `cborizeString(String)` method with the string.

The `cborize(Object)` method is just an alias of `cborizeObject(Object)`.

#### CBOROutputStream

The `CBOROutputStream` class extends `FilterOutputStream` and provides
`writeXxx` methods that write the CBOR-encoded byte sequence of given
variable or instance into the wrapped output stream.

```java
// Actual output stream
OutputStream out = ...;

// Wrap the output stream.
CBOROutputStream cout = new CBOROutputStream(out);

// Write the CBOR-encoding of "a" into the output stream.
// As a result, two bytes (0x61, 0x61) are written.
cout.writeString("a");
```

#### toString method

The `toString()` method of `CBORItem` subclasses returns the string
representation that conforms to the diagnostic notation which is
described in [8. Diagnostic Notation][RFC_8949_diagnostic_notation] of
[RFC 8949][RFC_8949].

The following example shows that the content of a CBOR byte string, which
the `CBORByteArray` class represents, is represented as a hex string enclosed
with `h'` and `'`.

```java
// Binary data.
byte[] data = {(byte)0x01, (byte)0x02, (byte)0x03};

// A CBOR byte string that holds the binary data.
CBORByteArray byteString = new CBORByteArray(data);

// Get the string representation of the CBOR byte string.
// The resultant string will hold:
//
//   h'010203'
//
String str = byteString.toString();
```

If it is indicated that the content of a byte string is decodable as CBOR
data items when a `CBORByteArray` instance is created, the content is decoded
when the `toString()` method is called.

```java
// The content of a byte string.
byte[] data = {(byte)0x01};

// Create a byte string without marking the content as decodable.
CBORByteArray byteString1 = new CBORByteArray(data, false);

// Create a byte string with marking the content as decodable.
CBORByteArray byteString2 = new CBORByteArray(data, true);

// The string representation is a hex string.
assertEquals("h'01", byteString1.toString());

// The content was interpreted as a CBOR unsigned integer.
// The pair of "<<" and ">>" indicates that the content of the
// byte string was interpreted as embedded CBOR data items.
assertEquals("<<1>>", byteString2.toString());
```

The following example shows that the `toString()` method works recursively
when a CBOR data item is a CBOR array or a CBOR map.

```java
// Create a Map instance.
Map<Object, Object> map = new LinkedHashMap<>();
map.put("a", 1);
map.put("b", List.of(2, 3));

// Convert the Map instance into a CBOR map.
CBORItem item = new CBORizer().cborize(map);

// The item is printed as follows.
//
//   {"a": 1, "b": [2, 3]}
//
System.out.println(item);
```

#### Other Methods for String Representations

The following methods of the `CBORItem` class are available since version 1.5.

- `encodeToBase64()`
- `encodeToBase64Url()`
- `encodeToHex()`
- `prettify()`

The last `prettify()` method returns the CBOR Diagnostic Notation representation
with pretty formatting.

### COSE Class Hierarchy

[RFC 9052][RFC_9052], the main specification of **COSE** (CBOR Object Signing
and Encryption), defines the following 6 data objects. They are called COSE
messages.

| COSE Message  | Semantics                                   | CBOR Tag |
|:--------------|:--------------------------------------------|:--------:|
| COSE_Sign     | COSE Signed Data Object                     |    98    |
| COSE_Sign1    | COSE Single Signer Data Object              |    18    |
| COSE_Encrypt  | COSE Encrypted Data Object                  |    96    |
| COSE_Encrypt0 | COSE Single Recipient Encrypted Data Object |    16    |
| COSE_Mac      | COSE MACed Data Object                      |    97    |
| COSE_Mac0     | COSE MAC without Recipients Object          |    17    |

This library provides Java classes that correspond to the COSE messages.
Below shows the inheritance tree of CBOR data item including the classes.

- `CBORItem`
  - `CBORBreak` (CBOR break)
  - `CBORItemList` (CBOR array)
    - `COSEObject`
      - `COSEMessage`
        - `COSEEncrypt` (**COSE_Encrypt**)
        - `COSEEncrypt0` (**COSE_Encrypt0**)
        - `COSEMac` (**COSE_Mac**)
        - `COSEMac0` (**COSE_Mac0**)
        - `COSESign` (**COSE_Sign**)
        - `COSESign1` (**COSE_Sign1**)
      - `COSERecipient` (COSE_recipient)
      - `COSESignature` (COSE_Signature)
    - `SigStructure` (Sig_structure)
  - `CBORNull` (CBOR null)
  - `CBORPairList` (CBOR map)
    - `COSEKey` (COSE_Key)
      - `COSEEC2Key`
      - `COSEOKPKey`
    - `COSEUnprotectedHeader`
    - `CWTClaimsSet`
  - `CBORTaggedItem` (CBOR tag)
    - `CWT` (CWT)
  - `CBORUndefined` (CBOR undefined)
  - `CBORValue`
    - `CBORBoolean` (CBOR false and CBOR true)
    - `CBORByteArray` (CBOR byte string)
      - `COSEProtectedHeader`
    - `CBORNumber`
      - `CBORBigInteger` (CBOR integer, CBOR bignum)
      - `CBORDouble` (CBOR double-precision floating point)
      - `CBORFloat` (CBOR single-precision floating point)
      - `CBORInteger` (CBOR integer)
      - `CBORLong` (CBOR integer)
    - `CBORSimpleValue` (CBOR simple value)
    - `CBORString` (CBOR text string)
    - `CBORUri`

### COSE Message Reading

If the tag processor for COSE messages (`COSEMessageTagProcessor`) is
registered and COSE messages are properly preceded by corresponding CBOR tags,
the `CBORDecoder` class generates `COSEMessage` instances accordingly.

In the following example, a COSE_Sign1 object is preceded by the tag number 18.
Therefore, the tag content becomes an instance of the `COSESign1` class.

NOTE: Subsequent examples use `CBORDecoder` instead of `CBORParser` because
`CBORParser` converts instances of `CBORItem` subclasses into instances of
general-purpose common Java classes such as `List` and `Map` and drops
CBOR-specific information.

```java
// RFC 9052, C.2.1. Single ECDSA Signature
//
//   18(
//     [
//       / protected h'a10126' / << {
//           / alg / 1:-7 / ECDSA 256 /
//         } >>,
//       / unprotected / {
//         / kid / 4:'11'
//       },
//       / payload / 'This is the content.',
//       / signature / h'8eb33e4ca31d1c465ab05aac34cc6b23d58fef5c083106c4
//   d25a91aef0b0117e2af9a291aa32e14ab834dc56ed2a223444547e01f11d3b0916e5
//   a4c345cacb36'
//     ]
//   )
//
byte[] input = HexFormat.of().parseHex(
        "d28443a10126a1044231315454686973206973207468652063" +
        "6f6e74656e742e58408eb33e4ca31d1c465ab05aac34cc6b23" +
        "d58fef5c083106c4d25a91aef0b0117e2af9a291aa32e14ab8" +
        "34dc56ed2a223444547e01f11d3b0916e5a4c345cacb36");

// Decode the byte array as a CBOR data item. By default, the tag processor
// for COSE messages (COSEMessageTagProcessor) is registered.
CBORItem item = new CBORDecoder(input).next();

// The decoded CBOR data item should be a CBOR tag whose tag number is 18.
CBORTaggedItem tagged = (CBORTaggedItem)item;

// The tag content should be a COSE_Sign1 object.
COSESign1 sign1 = (COSESign1)tagged.getTagContent();
```

On the other hand, if the tag is omitted, the byte array is decoded as just
a CBOR array.

```java
// Skip the leading tag and decode the remaining bytes.
item = new CBORDecoder(input, 1, input.length - 1).next();

// The byte array is decoded as a CBOR array.
CBORItemList itemList = (CBORItemList)item;
```

However, if you know a certain CBOR data item represents a COSE message, you
can construct an instance of the corresponding COSE message class by passing
the CBOR data item to the `build(CBORItem)` method.

```java
// Construct an instance of COSESign1 from a CBOR data item.
sign1 = COSESign1.build(item);
```

### COSE Message Building

If you are familiar with the structure of COSE messages, it is possible to
construct an instance of a COSE message class by combining `List`, `Map`
and byte arrays and passing the resultant data to the `build(List)` method
of the target COSE message class.

```java
// A list that represents COSE_Sign1.
List<Object> list = List.of(
        // Protected header
        HexFormat.of().parseHex("a10126"),  // <<{1:-7}>>

        // Unprotected header
        Map.of(4, "11".getBytes(StandardCharsets.UTF_8)),  // {4:'11'}

        // Payload
        "This is the content.".getBytes(StandardCharsets.UTF_8),

        // Signature
        HexFormat.of().parseHex(
                "8eb33e4ca31d1c465ab05aac34cc6b23d58fef5c0831" +
                "06c4d25a91aef0b0117e2af9a291aa32e14ab834dc56" +
                "ed2a223444547e01f11d3b0916e5a4c345cacb36")
);

// Construct a COSESign1 instance from List.
COSESign1 sign1 = COSESign1.build(list);
```

In practice, it is better to use dedicated builder classes. The example
below constructs a `COSESign1` instance by utilizing the `COSESign1Builder`
class.

```java
// Construct a COSESign1 instance by utilizing builder classes.
COSESign1 sign1 = new COSESign1Builder()
        // Protected header
        .protectedHeader(
                // <<{1:-7}>>
                new COSEProtectedHeaderBuilder().alg(COSEAlgorithms.ES256).build()
        )
        // Unprotected header
        .unprotectedHeader(
                // {4:'11'}
                new COSEUnprotectedHeaderBuilder().kid("11").build()
        )
        // Payload
        .payload("This is the content.")
        // Signature
        .signature(
                HexFormat.of().parseHex(
                        "8eb33e4ca31d1c465ab05aac34cc6b23d58fef5c0831" +
                        "06c4d25a91aef0b0117e2af9a291aa32e14ab834dc56" +
                        "ed2a223444547e01f11d3b0916e5a4c345cacb36")
        )
        // Construct a COSESign1 instance.
        .build();
```

### COSE Key

The COSE specification defines how to represent keys as CBOR data items.
Basically, keys are represented as CBOR maps.

The following is an example of public key excerpted from
[C.7.1. Public Keys][RFC_9052_public_keys] of [RFC 9052][RFC_9052].

```javascript
{
    -1:1,
    -2:h'bac5b11cad8f99f9c72b05cf4b9e26d244dc189f745228255a219a86d6a09eff',
    -3:h'20138bf82dc1b6d562be0fa54ab7804a3a64b6d72ccfed6b6fb6ed28bbfc117e',
    1:2,
    2:'11'
}
```

Since the labels and values in the example above are numbers instead of
meaningful text strings (in order to compact the size), it is hard to
understand the content of the key.

The following is a human-readable interpretation of the above key with the
information in [IANA: CBOR Object Signing and Encryption (COSE)][IANA_cose].

```json
{
    "crv": "P-256",
    "x":   "bac5b11cad8f99f9c72b05cf4b9e26d244dc189f745228255a219a86d6a09eff",
    "y":   "20138bf82dc1b6d562be0fa54ab7804a3a64b6d72ccfed6b6fb6ed28bbfc117e",
    "kty": "EC2",
    "kid": "11"
}
```

In this library, a COSE key is represented by the `COSEKey` class. In addition,
for some key types, dedicated subclasses exist. The `COSEOKPKey` class is for
the key type `OKP`, and the `COSEEC2Key` class is for the key type `EC2`.

If a COSE key is already available as a CBOR data item, a `COSEKey` instance
can be constructed by passing the item to the `COSEKey.build(CBORItem)` method.

```java
// A byte sequence that represents a COSE key of the key type 'EC2'.
byte[] input = HexFormat.of().parseHex(
        "a52001215820bac5b11cad8f99f9c72b05cf4b9e26d244dc189f74522825" +
        "5a219a86d6a09eff22582020138bf82dc1b6d562be0fa54ab7804a3a64b6" +
        "d72ccfed6b6fb6ed28bbfc117e010202423131");

// Decode the byte sequence as a CBOR data item.
CBORItem item = new CBORDecoder(input).next();

// Construct a COSEKey instance from the CBOR data item. If there
// exists a dedicated subclass for the key type, an instance of
// the subclass is returned.
COSEEC2Key key = (COSEEC2Key)COSEKey.build(item);
```

It is possible to build a `COSEKey` instance from a `Map` instance by using
the `COSEKey.build(Map)` method.

```java
byte[] x = HexFormat.of().parseHex(
        "bac5b11cad8f99f9c72b05cf4b9e26d244dc189f745228255a219a86d6a09eff");
byte[] y = HexFormat.of().parseHex(
        "20138bf82dc1b6d562be0fa54ab7804a3a64b6d72ccfed6b6fb6ed28bbfc117e");

// Prepare a Map instance that represents a COSE key.
Map<Object, Object> map = new LinkedHashMap<>();
map.put(-1, 1);
map.put(-2, x);
map.put(-3, y);
map.put(1, 2);
map.put(2, "11".getBytes(StandardCharsets.UTF_8));

// Construct a COSEKey instance from the map.
COSEEC2Key key = (COSEEC2Key)COSEKey.build(map);
```

A slightly better way is to use named constants instead of bare numbers.

```java
// Prepare a Map instance that represents a COSE key.
Map<Object, Object> map = new LinkedHashMap<>();
map.put(COSEKeyTypeParameters.EC2_CRV, COSEEllipticCurves.P_256);
map.put(COSEKeyTypeParameters.EC2_X, x);
map.put(COSEKeyTypeParameters.EC2_Y, y);
map.put(COSEKeyCommonParameters.KTY, COSEKeyTypes.EC2);
map.put(COSEKeyCommonParameters.KID, "11".getBytes(StandardCharsets.UTF_8));
```

A much better way is to use the `COSEKeyBuilder` class that is dedicated to
building a `COSEKey` instance.

```java
// Construct a COSEEC2Key instance by utilizing COSEKeyBuilder.
COSEEC2Key ec2key = new COSEKeyBuilder()
        .ec2CrvP256()
        .ec2X(x)
        .ec2Y(y)
        .ktyEC2()
        .kid("11")
        .buildEC2Key();
```

The `COSEEC2Key` class provides methods to create `ECPublicKey` and
`ECPrivateKey` instances. For example, an `ECPublicKey` instance can be
created from the `COSEEC2Key` instance in the above example as follows.

```java
// Create a public key that implements the ECPublicKey interface.
ECPublicKey pubKey = ec2key.toECPublicKey();
```

Likewise, an `ECPrivateKey` instance can be created as follows.

```java
// RFC 9052, C.7.2. Private Keys
//
// {
//   1:2,
//   2:'11',
//   -1:1,
//   -2:h'bac5b11cad8f99f9c72b05cf4b9e26d244dc189f745228255a219a86d6a09eff',
//   -3:h'20138bf82dc1b6d562be0fa54ab7804a3a64b6d72ccfed6b6fb6ed28bbfc117e',
//   -4:h'57c92077664146e876760c9520d054aa93c3afb04e306705db6090308507b4d3'
// }

byte[] x = HexFormat.of().parseHex(
        "bac5b11cad8f99f9c72b05cf4b9e26d244dc189f745228255a219a86d6a09eff");
byte[] y = HexFormat.of().parseHex(
        "20138bf82dc1b6d562be0fa54ab7804a3a64b6d72ccfed6b6fb6ed28bbfc117e");
byte[] d = HexFormat.of().parseHex(
        "57c92077664146e876760c9520d054aa93c3afb04e306705db6090308507b4d3");

// Create a private key that implements the ECPrivateKey interface.
ECPrivateKey priKey = new COSEKeyBuilder()
        .ktyEC2()
        .kid("11")
        .ec2CrvP256()
        .ec2X(x)
        .ec2Y(y)
        .ec2D(d)
        .buildEC2Key()
        .toECPrivateKey();
```

### COSE Signature Verification

The `COSEVerifier` class is dedicated to verifying the signature(s) of
`COSESign` and `COSESign1`.

The `COSEVerifier` class provides the following constructors.

- `COSEVerifier(java.security.Key)`
- `COSEVerifier(com.authlete.cose.KeyGetter)`

If the key for signature verification is already determined and available
before a `COSEVerifier` instance is created, the constructor that takes the
`Key` instance is suitable.

```java
// The key for signature verification.
ECPublicKey pubKey = ...;

// Create a COSEVerifier instance with the key.
COSEVerifier verifier = new COSEVerifier(pubKey);
```

On the other hand, the constructor that takes the `KeyGetter` instance is
suitable when you need to defer key preparation until verification is
actually performed.

The `KeyGetter` interface has the following method.

```java
Key get(int keyOperation, int algorithm, byte[] keyID) throws COSEException;
```

The `get` method is called with `keyOperation` = `COSEKeyOperations.VERIFY`
in the context of signature verification.

A useful point of the `KeyGetter` interface is that the `COSEVerifier` class
calls the `get` method with the algorithm identifier and the optional key ID
that the `COSEVerifier` class has retrieved from the target `COSESign` or
`COSESign1` object.

In either way, once you create a `COSEVerifier` instance, you can verify a
`COSESign` or `COSESign1` object by calling a `verify` method.

```java
// A target object to verify.
COSESign1 sign1 = ...;

// Verify the signature. If the COSEVerifier instance has been created
// with a KeyGetter instance, the "get" method of the instance is called
// from within the implementation of the "verify" method.
boolean valid = verifier.verify(sign1);
```

### COSE Signing

The COSE specification defines the target to sign as Sig_structure.
The structure is defined in
[4.4. Signing and Verification Process][RFC_9052_signing_and_verification_process]
of [RFC 9052][RFC_9052] as follows.

```
Sig_structure = [
    context : "Signature" / "Signature1",
    body_protected : empty_or_serialized_map,
    ? sign_protected : empty_or_serialized_map,
    external_aad : bstr,
    payload : bstr
]
```

A signer needs to prepare Sig_structure data before signing.

As Sig_structure is a CBOR array, it is possible to prepare Sig_structure
by using `CBORizer`. But, it is easier to use the `SigStructureBuilder` class
that is dedicated to building Sig_structure. The following is an example of
Sig_structure creation by using the `SigStructureBuilder`. In this library,
the `SigStructure` class represents Sig_structure.

```java
// Signature algorithm
int algorithm = COSEAlgorithms.ES256;

// Protected header
COSEProtectedHeader protectedHeader =
        new COSEProtectedHeaderBuilder().alg(algorithm).build();

// Unprotected header
COSEUnprotectedHeader unprotectedHeader =
        new COSEUnprotectedHeaderBuilder().kid("11").build();

// Payload
String payload = "This is the content.";

// Sig_structure
SigStructure structure = new SigStructureBuilder()
        .signature1()
        .bodyAttributes(protectedHeader)
        .payload(payload)
        .build();
```

Once you have a `SigStructure` instance, you can generate a signature for it
by using the `COSESigner` class.

The `COSESigner` class provides two constructors as the `COSEVerifier` class
does. The example below creates a `COSESigner` instance with a private key
for signing and signs the Sig_structure prepared above. As a result, a
signature is generated.

```java
// A private key for signing.
ECPrivateKey priKey = ...;

// Create a signer with the private key.
COSESigner signer = new COSESigner(priKey);

// Sign the Sig_structure (= generate a signature).
byte[] signature = signer.sign(structure, algorithm);
```

Signatures generated by `COSESigner` are supposed to be used to construct
`COSESign` and `COSESign1` objects. The example below constructs a `COSESign1`
object with the signature generated above.

```java
// COSESign1
COSESign1 sign1 = new COSESign1Builder()
        .protectedHeader(protectedHeader)
        .unprotectedHeader(unprotectedHeader)
        .payload(payload)
        .signature(signature)
        .build();
```

When `COSEVerifier` verifies a signature, the same Sig_structure must be
built. However, you don't have to build it manually on verification because
the implementation of the `verify` method of `COSEVerifier` does it behind
the scenes on behalf of you.

For your curiosity, the implementation of `verify(COSESign1, byte[])` method
is excerpted here.

```java
public boolean verify(COSESign1 sign1, byte[] externalData) throws COSEException
{
    // 'alg' (1) in the protected header or the unprotected header.
    int alg = retrieveAlg(sign1);

    // 'kid' (4) in the protected header or the unprotected header.
    byte[] kid = retrieveKid(sign1);

    // Get a key for verifying the signature.
    Key key = getKey(alg, kid);

    // Prepare "ToBeSigned" (RFC 9052 Section 4.4), which is the data
    // for which the signature was generated.
    SigStructure toBeSigned = new SigStructureBuilder()
            .sign1(sign1).externalData(externalData).build();

    // Verify the signature.
    return verify(key, alg, toBeSigned.encode(), sign1.getSignature().getValue());
}
```

### CWT Reading

When a CWT CBOR tag (61) is prepended to a COSE message, the CBOR decoder
returns a `CWT` instance.

```java
// Signed CWT Example, copied from RFC 8392.
//
// / CWT CBOR tag / 61(
//   / COSE_Sign1 CBOR tag / 18(
//     [
//       / protected / << {
//         / alg / 1: -7 / ECDSA 256 /
//       } >>,
//       / unprotected / {
//         / kid / 4: h'4173796d6d657472696345434453413
//                      23536' / 'AsymmetricECDSA256' /
//       },
//       / payload / << {
//         / iss / 1: "coap://as.example.com",
//         / sub / 2: "erikw",
//         / aud / 3: "coap://light.example.com",
//         / exp / 4: 1444064944,
//         / nbf / 5: 1443944944,
//         / iat / 6: 1443944944,
//         / cti / 7: h'0b71'
//       } >>,
//       / signature / h'5427c1ff28d23fbad1f29c4c7c6a555e601d6fa29f
//                       9179bc3d7438bacaca5acd08c8d4d4f96131680c42
//                       9a01f85951ecee743a52b9b63632c57209120e1c9e
//                       30'
//     ]
//   )
// )
byte[] encodedCWT = HexFormat.of().parseHex(
        // CWT CBOR Tag
        "d83d" +
        // COSE_Sign1 CBOR Tag + COSE_Sign1 (RFC 8392 A.3. Example Signed CWT)
        "d28443a10126a104524173796d6d657472696345434453413235365850a701756" +
        "36f61703a2f2f61732e6578616d706c652e636f6d02656572696b77037818636f" +
        "61703a2f2f6c696768742e6578616d706c652e636f6d041a5612aeb0051a5610d" +
        "9f0061a5610d9f007420b7158405427c1ff28d23fbad1f29c4c7c6a555e601d6f" +
        "a29f9179bc3d7438bacaca5acd08c8d4d4f96131680c429a01f85951ecee743a5" +
        "2b9b63632c57209120e1c9e30");

// Decode the byte array as a CBOR item. As the input data has a CWT CBOR tag,
// the decoder returns an instance of the CWT class.
CWT cwt = (CWT)new CBORDecoder(encodedCWT).next();
```

The `CWT` class provides the `getMessage()` method that returns the COSE
message of the CWT as a `COSEMessage` instance.

```java
// The COSE message of the CWT.
COSEMessage message = cwt.getMessage();
```

The type of the COSE message can be checked by the `getType()` method of the
`COSEMessage` instance. As the example above contains a COSE_Sign1 message,
the `getType()` method returns `COSEMessageType.COSE_SIGN1` and the
`COSEMessage` instance can be cast to the `COSESign1` class.

```java
// The type of the COSE message in the example is COSE_Sign1.
assertEquals(COSEMessageType.COSE_SIGN1, message.getType());

// The COSEMessage instance can be cast to the COSESign1 class.
COSESign1 sign1 = (COSESign1)message;
```

[RFC 8392][RFC_8392] defines "CWT Claims Set" as _"The CBOR map that contains
the claims conveyed by the CWT."_

The `CWTClaimsSet` class in this library represents the CWT Claims Set. The
class provides the `build(CBORItem payload)` method to build a `CWTClaimsSet`
instance from the payload of a COSE message.

```java
// The payload of the COSE_Sign1 message. The type of the returned object is
// either CBORByteArray (CBOR byte string) or CBORNull (CBOR null).
CBORItem payload = sign1.getPayload();

// Interpret the payload as CWT Claims Set.
CWTClaimsSet claims = CWTClaimsSet.build(payload);
```

The `CWTClaimsSet` class provides methods for some known claims. For example,
the `getIss()` method returns the value of the "iss" claim (whose claim key
is 1) as a string.

```java
// Expected claim values
String expectedIss = "coap://as.example.com";
String expectedSub = "erikw";
String expectedAud = "coap://light.example.com";
Date   expectedExp = new Date(1444064944 * 1000L);
Date   expectedNbf = new Date(1443944944 * 1000L);
Date   expectedIat = new Date(1443944944 * 1000L);
byte[] expectedCti = { (byte)0x0b, (byte)0x71 };

// Test if actual claim values are equal to the expected ones.
assertEquals(expectedIss, claims.getIss());
assertEquals(expectedSub, claims.getSub());
assertEquals(expectedAud, claims.getAud());
assertEquals(expectedExp, claims.getExp());
assertEquals(expectedNbf, claims.getNbf());
assertEquals(expectedIat, claims.getIat());
assertArrayEquals(expectedCti, claims.getCti());
```

### CWT Claims Set Builder

The `CWTClaimsSetBuilder` class is provided as a utility to create an
instance of the `CWTClaimsSet` class.

```java
// Claim values.
String iss   = "ISS";
String sub   = "SUB";
String aud   = "AUD";
Date   iat   = new Date(1000000L);
Date   nbf   = new Date(2000000L);
Date   exp   = new Date(3000000L);
String cti   = "CTI";
String nonce = "NONCE";

// Build a new CWTClaimsSet instance with the claim values.
CWTClaimsSet claims = new CWTClaimsSetBuilder()
        .iss(iss)
        .sub(sub)
        .aud(aud)
        .iat(iat)
        .nbf(nbf)
        .exp(exp)
        .cti(cti)
        .nonce(nonce)
        .build();
```

The `CWTClaimsSet` class is a subclass of the `CBORPairList`. In other words,
the `CWTClaimsSet` class is a kind of CBOR map. Therefore, a `CWTClaimsSet`
instance can be used as the payload of a CWT by embedding the CBOR-encoding
representation of the `CWTClaimsSet` instance into a CBOR byte string.

```java
// The CBOR-encoding representation of CWT Claims Set.
byte[] encodedClaims = claims.encode();

// Embed the encoded claims into a byte string.
CBORByteArray payload = new CBORByteArray(encodedClaims);

// Create a COSE message with the payload.
COSESign1 message = new COSESign1Builder()
        .payload(payload)
        // abbreviated
        .build();

// The COSE message can be wrapped by the CWT class as necessary.
CWT cwt = new CWT(message);
```

### CWT Key Proof

[OpenID for Verifiable Credential Issuance][OID4VCI] defines the structure
of a key proof in the CWT format. This library provides a utility class,
`CWTKeyProofBuilder`, to generate a key proof that complies with the
specification.

```java
// The identifier of the client application.
String client = "my_client_id";

// The identifier of the credential issuer.
String issuer = "https://credential-issuer.example.com";

// The value of 'c_nonce' issued by the authorization server or
// the credential issuer.
String nonce = "my_nonce";

// A private key for signing. The public key corresponding to
// this private key will be embedded in the protected header.
COSEKey key = ...;

// Generate a CWT representing a key proof.
CWT cwt = new CWTKeyProofBuilder()
    .setClient(client)
    .setIssuer(issuer)
    .setNonce(nonce)
    .setKey(key)
    .build();

// The base64url representation of the key proof.
String base64url = cwt.encodeToBase64Url();
```

The variable, `base64url`, in the example above holds a value like below.

```
2D3ShFidowEmA3RvcGVuaWQ0dmNpLXByb29mK2N3dGhDT1NFX0tleaYBAgJYK0pvWWcwS
mNhS1VPN00zRDlfUVR1UjR4Zl90MWhZZkNsVG95cS05dC1oWXMDJiABIVgguNp2r4R7DN
p3gPrnhtc93CY7gSDRdMwTris8ZPoeCGQiWCBFEpNEFZfxlYyevnC8dY9KOVjQhqAOyI7
m5XtohBHbO6BYR6QDeCVodHRwczovL2NyZWRlbnRpYWwtaXNzdWVyLmV4YW1wbGUuY29t
BhpmPeFaAWxteV9jbGllbnRfaWQKSG15X25vbmNlWEB6nBjmxfuVI7LVcQKXggEATC-AV
N_5VifZEC6NLJL-vpFaAppWRgW5QkDmTouV7WGDnXoomamaWzazgVgcvrWo
```

The following is the CBOR Diagnostic Notation representation of the example
CWT key proof above. FYI: [CBOR Zone][CBOR_ZONE] can be used to decode CBOR
data.

```
61(18(/ COSE_Sign1 / [
  / protected / <<
    {
      1: -7,
      3: "openid4vci-proof+cwt",
      "COSE_Key": {
        1: 2,
        2: h'4a6f5967304a63614b554f374d3344395f515475523478665f7431685966436c546f79712d39742d685973',
        3: -7,
        -1: 1,
        -2: h'b8da76af847b0cda7780fae786d73ddc263b8120d174cc13ae2b3c64fa1e0864',
        -3: h'451293441597f1958c9ebe70bc758f4a3958d086a00ec88ee6e57b688411db3b'
      }
    }
  >>,
  / unprotected / {
  },
  h'a403782568747470733a2f2f63726564656e7469616c2d6973737565722e6578616d706c652e636f6d061a663de15a016c6d795f636c69656e745f69640a486d795f6e6f6e6365',
  h'7a9c18e6c5fb9523b2d57102978201004c2f8054dff95627d9102e8d2c92febe915a029a564605b94240e64e8b95ed61839d7a2899a99a5b36b381581cbeb5a8'
]))
```

The `CWTKeyProofBuilder` provides the `main(String[])` method for invocation
from the command line. The method accepts the following arguments.

| option            | description |
|:------------------|:------------|
| `--issuer ISSUER` | REQUIRED. This option specifies the identifier of the credential issuer. |
| `--key FILE`      | REQUIRED. This option specifies the file containing a private key in the JWK format. |
| `--client CLIENT` | OPTIONAL. This option specifies the identifier of the client application. |
| `--nonce NONCE`   | OPTIONAL. This option specifies the value of `c_nonce` that has been issued by the server. |
| `--help`          | This option shows the help text. |

The [generate-cwt-key-proof](bin/generate-cwt-key-proof) script is a wrapper
for the command line invocation.

```
./bin/generate-cwt-key-proof \
  --issuer https://credential-issuer.example.com \
  --key private-key.jwk \
  --client my_client_id \
  --nonce my_nonce
```

### mdoc Building

The `com.authlete.mdoc` package contains classes for building an **mdoc** that
complies with the [ISO/IEC 18013-5:2021][ISO_IEC_18013_5_2021] standard.

The central utility class is `IssuerSignedBuilder`. The class uses the following
as input to create an instance of the `IssuerSigned` structure which is defined in
the "8.3.2.1.2.2 Device retrieval mdoc response" section of the ISO/IEC standard.

- Doc Type
- Claims
- Validity Information
- Device Key (optional)
- Issuer Key
- Certificate Chain for Issuer Key

An mdoc, which is represented by the `Document` structure, contains an instance of
the `IssuerSigned` structure.

The following is a sample code that creates an instance of the `DeviceResponse`
structure containing a `Document` instance.

```java
// Doc Type
String docType = "com.example.doctype";

// Claims
Map<String, Object> claims = new Gson().fromJson(
        "{\n" +
        "  \"com.example.namespace1\": {\n" +
        "    \"claimName1\": \"claimValue1\"\n" +
        "  },\n" +
        "  \"com.example.namespace2\": {\n" +
        "    \"claimName2\": \"claimValue2\"\n" +
        "  }\n" +
        "}",
        Map.class
);

// Validity Information
ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC).withNano(0);
ValidityInfo validityInfo =
        new ValidityInfo(now, now, now.plusYears(10));

// Issuer Key
COSEEC2Key issuerKey = new COSEKeyBuilder()
        .ktyEC2()
        .ec2CrvP256()
        .ec2XInBase64Url("Qw7367PjIwU17ckX_G4ZqLW2EjPG0efV0cYzhvq2Ujk")
        .ec2YInBase64Url("Mpq3N90VZIBBOqvYgAHi4ZfOSK2gM09_UozgVdRCrt4")
        .ec2DInBase64Url("IzdjF8wyUSqsCbz8kh6ysJOUcK003aCt9hIGFiGWlzI")
        .buildEC2Key();

// Certificate for the issuer key in the PEM format.
String issuerCertPem =
        "-----BEGIN CERTIFICATE-----\n" +
        "MIIBXzCCAQSgAwIBAgIGAYwpA4/aMAoGCCqGSM49BAMCMDYxNDAyBgNVBAMMKzNf\n" +
        "d1F3Y3Qxd28xQzBST3FfWXRqSTRHdTBqVXRiVTJCQXZteEltQzVqS3MwHhcNMjMx\n" +
        "MjAyMDUzMjI4WhcNMjQwOTI3MDUzMjI4WjA2MTQwMgYDVQQDDCszX3dRd2N0MXdv\n" +
        "MUMwUk9xX1l0akk0R3UwalV0YlUyQkF2bXhJbUM1aktzMFkwEwYHKoZIzj0CAQYI\n" +
        "KoZIzj0DAQcDQgAEQw7367PjIwU17ckX/G4ZqLW2EjPG0efV0cYzhvq2Ujkymrc3\n" +
        "3RVkgEE6q9iAAeLhl85IraAzT39SjOBV1EKu3jAKBggqhkjOPQQDAgNJADBGAiEA\n" +
        "o4TsuxDl5+3eEp6SHDrBVn1rqOkGGLoOukJhelndGqICIQCpocrjWDwrWexoQZOO\n" +
        "rwnEYRBmmfhaPor2OZCrbP3U6w==\n" +
        "-----END CERTIFICATE-----\n";

// Certificate for the issuer key as X509Certificate.
X509Certificate issuerCert = (X509Certificate)
        CertificateFactory.getInstance("X.509")
                .generateCertificate(new ByteArrayInputStream(
                        issuerCertPem.getBytes(StandardCharsets.UTF_8)));

// Certificate Chain for Issuer Key
List<X509Certificate> issuerCertChain = List.of(issuerCert);

// Build an "IssuerSigned" instance.
IssuerSigned issuerSigned = new IssuerSignedBuilder()
        .setDocType(docType)
        .setClaims(claims)
        .setValidityInfo(validityInfo)
        .setIssuerKey(issuerKey)
        .setIssuerCertChain(issuerCertChain)
        .build();

// Build a "Document" instance.
Document document = new Document(docType, issuerSigned);

// Build a "DeviceResponse" instance.
DeviceResponse deviceResponse = new DeviceResponse(List.of(document));
```

Because the `DeviceResponse` structure and all the nested components are CBOR
items, utility methods of the `CBORItem` class such as `encodeToHex()` and
`prettify()` can be used to print information about the `DeviceResponse` instance.

```java
// Hex
System.out.println(deviceResponse.encodeToHex());

// Diagnostic Notation
System.out.println(deviceResponse.prettify());
```

The following are example outputs.

```
a36776657273696f6e63312e3069646f63756d656e747381a267646f63547970657363
6f6d2e6578616d706c652e646f63747970656c6973737565725369676e6564a26a6e61
6d65537061636573a276636f6d2e6578616d706c652e6e616d6573706163653181d818
5859a4686469676573744944016672616e646f6d5068ad59e8a6b21f099aa9e6632df0
a82c71656c656d656e744964656e7469666965726a636c61696d4e616d65316c656c65
6d656e7456616c75656b636c61696d56616c75653176636f6d2e6578616d706c652e6e
616d6573706163653281d8185859a4686469676573744944026672616e646f6d5026ab
e1cdb4784a9d20cfcac684b9d8fd71656c656d656e744964656e7469666965726a636c
61696d4e616d65326c656c656d656e7456616c75656b636c61696d56616c7565326a69
7373756572417574688443a10126a118215901633082015f30820104a0030201020206
018c29038fda300a06082a8648ce3d04030230363134303206035504030c2b335f7751
77637431776f314330524f715f59746a49344775306a55746255324241766d78496d43
356a4b73301e170d3233313230323035333232385a170d323430393237303533323238
5a30363134303206035504030c2b335f775177637431776f314330524f715f59746a49
344775306a55746255324241766d78496d43356a4b733059301306072a8648ce3d0201
06082a8648ce3d03010703420004430ef7ebb3e3230535edc917fc6e19a8b5b61233c6
d1e7d5d1c63386fab65239329ab737dd156480413aabd88001e2e197ce48ada0334f7f
528ce055d442aede300a06082a8648ce3d0403020349003046022100a384ecbb10e5e7
edde129e921c3ac1567d6ba8e90618ba0eba42617a59dd1aa2022100a9a1cae3583c2b
59ec6841938eaf09c461106699f85a3e8af63990ab6cfdd4ebd818590131a567766572
73696f6e63312e306f646967657374416c676f726974686d675348412d3235366c7661
6c756544696765737473a276636f6d2e6578616d706c652e6e616d65737061636531a1
0158203431a8678f368d0d7e0f68f0b3ced7ee48b1a38ced261bf2f9d39f7638cda898
76636f6d2e6578616d706c652e6e616d65737061636532a10258204b8fb0c863f3cf19
1140f00af3aa736da93c3b12ebc18a351a22aa87b78df66167646f635479706573636f
6d2e6578616d706c652e646f63747970656c76616c6964697479496e666fa366736967
6e6564c074323032332d31322d30335430313a35313a31355a6976616c696446726f6d
c074323032332d31322d30335430313a35313a31355a6a76616c6964556e74696cc074
323033332d31322d30335430313a35313a31355a5840537c4b3ecb14c034d529c0309b
b224e0c7381aa992f8f2dd55b7322bda20a19cd8667c1e6747e6bb04f89806623f2b42
dc788def91ae26b39082f5983cddc61a6673746174757300
```

```
{
  "version": "1.0",
  "documents": [
    {
      "docType": "com.example.doctype",
      "issuerSigned": {
        "nameSpaces": {
          "com.example.namespace1": [
            24(<<
              {
                "digestID": 1,
                "random": h'68ad59e8a6b21f099aa9e6632df0a82c',
                "elementIdentifier": "claimName1",
                "elementValue": "claimValue1"
              }
            >>)
          ],
          "com.example.namespace2": [
            24(<<
              {
                "digestID": 2,
                "random": h'26abe1cdb4784a9d20cfcac684b9d8fd',
                "elementIdentifier": "claimName2",
                "elementValue": "claimValue2"
              }
            >>)
          ]
        },
        "issuerAuth": / COSE_Sign1 / [
          / protected / <<
            {
              / alg / 1: -7 / ES256 /
            }
          >>,
          / unprotected / {
            / x5chain / 33:
            h'3082015f30820104a0030201020206018c29038fda300a06082a8648
            ce3d04030230363134303206035504030c2b335f775177637431776f31
            4330524f715f59746a49344775306a55746255324241766d78496d4335
            6a4b73301e170d3233313230323035333232385a170d32343039323730
            35333232385a30363134303206035504030c2b335f775177637431776f
            314330524f715f59746a49344775306a55746255324241766d78496d43
            356a4b733059301306072a8648ce3d020106082a8648ce3d0301070342
            0004430ef7ebb3e3230535edc917fc6e19a8b5b61233c6d1e7d5d1c633
            86fab65239329ab737dd156480413aabd88001e2e197ce48ada0334f7f
            528ce055d442aede300a06082a8648ce3d0403020349003046022100a3
            84ecbb10e5e7edde129e921c3ac1567d6ba8e90618ba0eba42617a59dd
            1aa2022100a9a1cae3583c2b59ec6841938eaf09c461106699f85a3e8a
            f63990ab6cfdd4eb'
          },
          / payload / 24(<<
            {
              "version": "1.0",
              "digestAlgorithm": "SHA-256",
              "valueDigests": {
                "com.example.namespace1": {
                  1: h'3431a8678f368d0d7e0f68f0b3ced7ee48b1a38ced261bf2f9d39f7638cda898'
                },
                "com.example.namespace2": {
                  2: h'4b8fb0c863f3cf191140f00af3aa736da93c3b12ebc18a351a22aa87b78df661'
                }
              },
              "docType": "com.example.doctype",
              "validityInfo": {
                "signed": 0("2023-12-03T01:51:15Z"),
                "validFrom": 0("2023-12-03T01:51:15Z"),
                "validUntil": 0("2033-12-03T01:51:15Z")
              }
            }
          >>),
          / signature /
          h'537c4b3ecb14c034d529c0309bb224e0c7381aa992f8f2dd55b7322bda
          20a19cd8667c1e6747e6bb04f89806623f2b42dc788def91ae26b39082f5
          983cddc61a'
        ]
      }
    }
  ],
  "status": 0
}
```

#### Claims for mdoc

The claims given by the `setClaims(Map<String, Object>)` method are used to
construct `IssuerSignedItem` instances.

The keys of the top-level properties in the claims map must be strings
representing **name spaces**, and their values must be JSON objects, each of
which contains claims under the corresponding name space.

The following JSON shows the structure that the claims map should have.

```json
{
    "com.example.namespace1" : {
        "claimName1": "claimValue1",
        "claimName2": true,
        "claimName3": 1
    },
    "com.example.namespace2" : {
        "claimName4": [ "element1", "element2" ],
        "claimName5": {
          "subClaimName1": "subClaimValue1"
        }
    }
}
```

Types of claim values can be strings, boolean values, integers, floating-point
numbers, arrays (`List`) and maps (`Map`), which are natural representations of
JSON values.

However, there may be cases where CBOR-specific data need to be embedded. For
example, the `"birth_date"` claim may require the "full-date" tag (defined in
[RFC 8943][RFC_8943]) and the `"portrait"` claim may require binary data.

To embed CBOR-specific data, a `CBORizer` instance with an implementation of
the `CBORDiagnosticNotationParser` interface needs to be set by calling the
`setCBORizer(CBORizer)` method. Such `CBORizer` will interpret strings written
in the CBOR Diagnostic Notation
([RFC 8949, 8. Diagnostic Notation][RFC_8949_diagnostic_notation], and
[RFC 8610, Appendix G. Extended Diagnostic Notation][RFC_8610_appendix_G])
with a special prefix (e.g. `"cbor:"`), and convert them into CBOR-specific
data. As a result, input data like below will be accepted and CBOR-specific
data will be embedded accordingly.

```json
{
    "com.example.namespace3": {
        "birth_date": "cbor:1004(\"1974-05-06\")",
        "portrait": "cbor:h'0102.....'"
    }
}
```

## Contact

Authlete Contact Form: https://www.authlete.com/contact/

<!-- ==================== LINKS ==================== -->

[RFC_7049]: https://www.rfc-editor.org/rfc/rfc7049.html
[RFC_8152]: https://www.rfc-editor.org/rfc/rfc8152.html
[RFC_8230]: https://www.rfc-editor.org/rfc/rfc8230.html
[RFC_8392]: https://www.rfc-editor.org/rfc/rfc8392.html
[RFC_8610]: https://www.rfc-editor.org/rfc/rfc8610.html
[RFC_8610_appendix_G]: https://www.rfc-editor.org/rfc/rfc8610#appendix-G
[RFC_8613]: https://www.rfc-editor.org/rfc/rfc8613.html
[RFC_8742]: https://www.rfc-editor.org/rfc/rfc8742.html
[RFC_8746]: https://www.rfc-editor.org/rfc/rfc8746.html
[RFC_8943]: https://www.rfc-editor.org/rfc/rfc8943.html
[RFC_8949]: https://www.rfc-editor.org/rfc/rfc8949.html
[RFC_8949_bignums]: https://www.rfc-editor.org/rfc/rfc8949.html#name-bignums
[RFC_8949_diagnostic_notation]: https://www.rfc-editor.org/rfc/rfc8949#name-diagnostic-notation
[RFC_8949_encoded_text]: https://www.rfc-editor.org/rfc/rfc8949.html#name-encoded-text
[RFC_9052]: https://www.rfc-editor.org/rfc/rfc9052.html
[RFC_9052_public_keys]: https://www.rfc-editor.org/rfc/rfc9052.html#name-public-keys
[RFC_9052_signing_and_verification_process]: https://www.rfc-editor.org/rfc/rfc9052.html#name-signing-and-verification-pr
[RFC_9053]: https://www.rfc-editor.org/rfc/rfc9053.html
[RFC_9090]: https://www.rfc-editor.org/rfc/rfc9090.html
[RFC_9338]: https://www.rfc-editor.org/rfc/rfc9338.html
[RFC_9360]: https://www.rfc-editor.org/rfc/rfc9360.html

[IANA_cbor_simple_values]: https://www.iana.org/assignments/cbor-simple-values/cbor-simple-values.xhtml
[IANA_cbor_tags]: https://www.iana.org/assignments/cbor-tags/cbor-tags.xhtml
[IANA_cose]: https://www.iana.org/assignments/cose/cose.xhtml
[IANA_cwt]: https://www.iana.org/assignments/cwt/cwt.xhtml

[ISO_IEC_18013_5_2021]: https://www.iso.org/standard/69084.html

<!-- As the OID4VCI spec is unstable, the link here points to the WG draft. -->
[OID4VCI]: https://openid.github.io/OpenID4VCI/openid-4-verifiable-credential-issuance-wg-draft.html

[CBOR_ZONE]: https://cbor.zone/

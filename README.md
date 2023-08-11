# Java Library for CBOR, COSE and CWT

## Overview

This is a Java library for **CBOR** (Concise Binary Object Representation),
**COSE** (CBOR Object Signing and Encryption) and **CWT** (CBOR Web Token).

The current implementation supports the features below. More features will
be added as the need arises.

- [x] CBOR
- [x] COSE
  - [x] Basic Components
  - [x] Signature
    - [x] `ES256`
    - [x] `ES384`
    - [x] `ES512`
    - [ ] ?
  - [ ] Encryption
  - [ ] MAC
- [x] CWT
  - [x] Tag Processor
  - [ ] ?

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

## Contact

Authlete Contact Form: https://www.authlete.com/contact/

<!-- ==================== LINKS ==================== -->

[RFC_7049]: https://www.rfc-editor.org/rfc/rfc7049.html
[RFC_8152]: https://www.rfc-editor.org/rfc/rfc8152.html
[RFC_8230]: https://www.rfc-editor.org/rfc/rfc8230.html
[RFC_8392]: https://www.rfc-editor.org/rfc/rfc8392.html
[RFC_8610]: https://www.rfc-editor.org/rfc/rfc8610.html
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

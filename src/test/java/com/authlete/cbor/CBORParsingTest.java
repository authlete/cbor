/*
 * Copyright (C) 2023 Authlete, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.authlete.cbor;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import com.authlete.cbor.tag.CPUri;


public class CBORParsingTest
{
    @Test
    public void test_boolean() throws IOException
    {
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
    }


    @Test
    public void test_integer() throws IOException
    {
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
    }


    @Test
    public void test_long() throws IOException
    {
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
    }


    @Test
    public void test_float() throws IOException
    {
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
    }


    @Test
    public void test_double() throws IOException
    {
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

        // Parse the CBOR data item.
        Object object = new CBORParser(input).next();

        // The object should be a Double instance.
        assertEquals(Double.valueOf(1.1), object);
    }


    @Test
    public void test_big_integer() throws IOException
    {
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
    }


    @Test
    public void test_byte_array() throws IOException
    {
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
    }

    @Test
    public void test_byte_array_cbor() throws IOException
    {
        // === byte[] ===
        //
        // CBOR data item representing tagged CBOR data
        // encoded d818456449455446.
        //
        //   major = 24  ; byte string
        //
        byte[] input = {
                (byte)0xd8, (byte)0x18, (byte)0x45, (byte)0x64,
                (byte)0x49, (byte)0x45, (byte)0x54, (byte)0x46
        };

        // Expected result.
        String expected ="IETF";

        // Parse the CBOR data item.
        Object object = new CBORParser(input).next();

        // The object should be a String instance.
        assertSame(String.class, object.getClass());
        assertEquals(expected, (String)object);
    }

    @Test
    public void test_string() throws IOException
    {
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
    }


    @SuppressWarnings("unchecked")
    @Test
    public void test_collection() throws IOException
    {
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
    }


    @SuppressWarnings("unchecked")
    @Test
    public void test_map() throws IOException
    {
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
    }


    @Test
    public void test_uri() throws IOException
    {
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
    }
}

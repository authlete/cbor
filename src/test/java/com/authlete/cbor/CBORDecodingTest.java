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


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;


public class CBORDecodingTest
{
    private static byte[] fromHex(String hex)
    {
        try
        {
            return Hex.decodeHex(hex);
        }
        catch (DecoderException e)
        {
            // This should not happen.
            e.printStackTrace();
            return null;
        }
    }


    private static CBORItem decode(String hex)
    {
        try
        {
            return new CBORDecoder(fromHex(hex)).next();
        }
        catch (IOException e)
        {
            // This should not happen.
            e.printStackTrace();
            return null;
        }
    }


    private static List<CBORItem> decodeAll(String hex)
    {
        try
        {
            return new CBORDecoder(fromHex(hex)).all();
        }
        catch (IOException e)
        {
            // This should not happen.
            e.printStackTrace();
            return null;
        }
    }


    private static void testBoolean(Boolean expected, String inputHex)
    {
        testBoolean(expected, decode(inputHex));
    }


    private static void testBoolean(Boolean expected, CBORItem item)
    {
        assertEquals(CBORBoolean.class, item.getClass());
        assertEquals(expected, ((CBORBoolean)item).getValue());
    }


    private static void testInteger(int expected, String inputHex)
    {
        testInteger(expected, decode(inputHex));
    }


    private static void testInteger(int expected, CBORItem item)
    {
        assertEquals(CBORInteger.class, item.getClass());
        assertEquals(Integer.valueOf(expected), ((CBORInteger)item).getValue());
    }


    private static void testLong(long expected, String inputHex)
    {
        testLong(expected, decode(inputHex));
    }


    private static void testLong(long expected, CBORItem item)
    {
        assertEquals(CBORLong.class, item.getClass());
        assertEquals(Long.valueOf(expected), ((CBORLong)item).getValue());
    }


    private static void testBigInteger(String expected, String inputHex)
    {
        testBigInteger(expected, decode(inputHex));
    }


    private static void testBigInteger(String expected, CBORItem item)
    {
        assertEquals(CBORBigInteger.class, item.getClass());
        assertEquals(expected, ((CBORBigInteger)item).getValue().toString());
    }


    private static void testFloat(float expected, String inputHex)
    {
        testFloat(expected, decode(inputHex));
    }


    private static void testFloat(float expected, CBORItem item)
    {
        assertEquals(CBORFloat.class, item.getClass());
        assertEquals(Float.valueOf(expected), ((CBORFloat)item).getValue());
    }


    private static void testDouble(double expected, String inputHex)
    {
        testDouble(expected, decode(inputHex));
    }


    private static void testDouble(double expected, CBORItem item)
    {
        assertEquals(CBORDouble.class, item.getClass());
        assertEquals(Double.valueOf(expected), ((CBORDouble)item).getValue());
    }


    private static void testByteArray(byte[] expected, String inputHex)
    {
        testByteArray(expected, decode(inputHex));
    }


    private static void testByteArray(byte[] expected, CBORItem item)
    {
        assertEquals(CBORByteArray.class, item.getClass());
        assertArrayEquals(expected, ((CBORByteArray)item).getValue());
    }


    private static void testString(String expected, String inputHex)
    {
        testString(expected, decode(inputHex));
    }


    private static void testString(String expected, CBORItem item)
    {
        assertEquals(CBORString.class, item.getClass());
        assertEquals(expected, ((CBORString)item).getValue());
    }


    private static void testSimpleValue(int expected, String inputHex)
    {
        testSimpleValue(expected, decode(inputHex));
    }


    private static void testSimpleValue(int expected, CBORItem item)
    {
        assertEquals(CBORSimpleValue.class, item.getClass());
        assertEquals(Integer.valueOf(expected), ((CBORSimpleValue)item).getValue());
    }


    private static CBORTaggedItem testTag(Number expected, String inputHex)
    {
        return testTag(expected, decode(inputHex));
    }


    private static CBORTaggedItem testTag(Number expected, CBORItem item)
    {
        assertEquals(CBORTaggedItem.class, item.getClass());

        CBORTaggedItem tagged = (CBORTaggedItem)item;
        assertEquals(expected.toString(), tagged.getTagNumber().toString());

        return tagged;
    }


    private static CBORItemList testArray(String inputHex)
    {
        return testArray(decode(inputHex));
    }


    private static CBORItemList testArray(CBORItem item)
    {
        assertEquals(CBORItemList.class, item.getClass());

        return (CBORItemList)item;
    }


    private static CBORPairList testMap(String inputHex)
    {
        return testMap(decode(inputHex));
    }


    private static CBORPairList testMap(CBORItem item)
    {
        assertEquals(CBORPairList.class, item.getClass());

        return (CBORPairList)item;
    }


    private static void testByteArrayToString(String expected, String inputHex, boolean decodable)
    {
        List<CBORItem> decoded = (decodable ? decodeAll(inputHex) : null);

        CBORByteArray ba = new CBORByteArray(fromHex(inputHex), decoded);

        assertEquals(expected, ba.toString());
    }


    @Test
    public void test_rfc8949_appendixA_00()
    {
        // Diagnostic: 0
        // Encoded:    0x00
        testInteger(0, "00");
    }


    @Test
    public void test_rfc8949_appendixA_01()
    {
        // Diagnostic: 1
        // Encoded:    0x01
        testInteger(1, "01");
    }


    @Test
    public void test_rfc8949_appendixA_02()
    {
        // Diagnostic: 10
        // Encoded:    0x0a
        testInteger(10, "0a");
    }


    @Test
    public void test_rfc8949_appendixA_03()
    {
        // Diagnostic: 23
        // Encoded:    0x17
        testInteger(23, "17");
    }


    @Test
    public void test_rfc8949_appendixA_04()
    {
        // Diagnostic: 24
        // Encoded:    0x1818
        testInteger(24, "1818");
    }


    @Test
    public void test_rfc8949_appendixA_05()
    {
        // Diagnostic: 25
        // Encoded:    0x1819
        testInteger(25, "1819");
    }


    @Test
    public void test_rfc8949_appendixA_06()
    {
        // Diagnostic: 100
        // Encoded:    0x1864
        testInteger(100, "1864");
    }


    @Test
    public void test_rfc8949_appendixA_07()
    {
        // Diagnostic: 1000
        // Encoded:    0x1903e8
        testInteger(1000, "1903e8");
    }


    @Test
    public void test_rfc8949_appendixA_08()
    {
        // Diagnostic: 1000000
        // Encoded:    0x1a000f4240
        testInteger(1000000, "1a000f4240");
    }


    @Test
    public void test_rfc8949_appendixA_09()
    {
        // Diagnostic: 1000000000000
        // Encoded:    0x1b000000e8d4a51000
        testLong(1000000000000L, "1b000000e8d4a51000");
    }


    @Test
    public void test_rfc8949_appendixA_10()
    {
        // Diagnostic: 18446744073709551615
        // Encoded:    0x1bffffffffffffffff
        testBigInteger("18446744073709551615", "1bffffffffffffffff");
    }


    @Test
    public void test_rfc8949_appendixA_11()
    {
        // Diagnostic: 18446744073709551616
        // Encoded:    0xc249010000000000000000
        testBigInteger("18446744073709551616", "c249010000000000000000");
    }


    @Test
    public void test_rfc8949_appendixA_12()
    {
        // Diagnostic: -18446744073709551616
        // Encoded:    0x3bffffffffffffffff
        testBigInteger("-18446744073709551616", "3bffffffffffffffff");
    }


    @Test
    public void test_rfc8949_appendixA_13()
    {
        // Diagnostic: -18446744073709551617
        // Encoded:    0xc349010000000000000000
        testBigInteger("-18446744073709551617", "c349010000000000000000");
    }


    @Test
    public void test_rfc8949_appendixA_14()
    {
        // Diagnostic: -1
        // Encoded:    0x20
        testInteger(-1, "20");
    }


    @Test
    public void test_rfc8949_appendixA_15()
    {
        // Diagnostic: -10
        // Encoded:    0x29
        testInteger(-10, "29");
    }


    @Test
    public void test_rfc8949_appendixA_16()
    {
        // Diagnostic: -100
        // Encoded:    0x3863
        testInteger(-100, "3863");
    }


    @Test
    public void test_rfc8949_appendixA_17()
    {
        // Diagnostic: -1000
        // Encoded:    0x3903e7
        testInteger(-1000, "3903e7");
    }


    @Test
    public void test_rfc8949_appendixA_18()
    {
        // Diagnostic: 0.0
        // Encoded:    0xf90000
        testFloat(0.0f, "f90000");
    }


    @Test
    public void test_rfc8949_appendixA_19()
    {
        // Diagnostic: -0.0
        // Encoded:    0xf98000
        testFloat(-0.0f, "f98000");
    }


    @Test
    public void test_rfc8949_appendixA_20()
    {
        // Diagnostic: 1.0
        // Encoded:    0xf93c00
        testFloat(1.0f, "f93c00");
    }


    @Test
    public void test_rfc8949_appendixA_21()
    {
        // Diagnostic: 1.1
        // Encoded:    0xfb3ff199999999999a
        testDouble(1.1, "fb3ff199999999999a");
    }


    @Test
    public void test_rfc8949_appendixA_22()
    {
        // Diagnostic: 1.5
        // Encoded:    0xf93e00
        testFloat(1.5f, "f93e00");
    }


    @Test
    public void test_rfc8949_appendixA_23()
    {
        // Diagnostic: 65504.0
        // Encoded:    0xf97bff
        testFloat(65504.0f, "f97bff");
    }


    @Test
    public void test_rfc8949_appendixA_24()
    {
        // Diagnostic: 100000.0
        // Encoded:    0xfa47c35000
        testFloat(100000.0f, "fa47c35000");
    }


    @Test
    public void test_rfc8949_appendixA_25()
    {
        // Diagnostic: 3.4028234663852886e+38
        // Encoded:    0xfa7f7fffff
        testFloat(3.4028234663852886e+38f, "fa7f7fffff");
    }


    @Test
    public void test_rfc8949_appendixA_26()
    {
        // Diagnostic: 1.0e+300
        // Encoded:    0xfb7e37e43c8800759c
        testDouble(1.0e+300, "fb7e37e43c8800759c");
    }


    @Test
    public void test_rfc8949_appendixA_27()
    {
        // Diagnostic: 5.960464477539063e-8
        // Encoded:    0xf90001
        testFloat(5.960464477539063e-8f, "f90001");
    }


    @Test
    public void test_rfc8949_appendixA_28()
    {
        // Diagnostic: 0.00006103515625
        // Encoded:    0xf90400
        testFloat(0.00006103515625f, "f90400");
    }


    @Test
    public void test_rfc8949_appendixA_29()
    {
        // Diagnostic: -4.0
        // Encoded:    0xf9c400
        testFloat(-4.0f, "f9c400");
    }


    @Test
    public void test_rfc8949_appendixA_30()
    {
        // Diagnostic: -4.1
        // Encoded:    0xfbc010666666666666
        testDouble(-4.1, "fbc010666666666666");
    }


    @Test
    public void test_rfc8949_appendixA_31()
    {
        // Diagnostic: Infinity
        // Encoded:    0xf97c00
        testFloat(Float.POSITIVE_INFINITY, "f97c00");
    }


    @Test
    public void test_rfc8949_appendixA_32()
    {
        // Diagnostic: NaN
        // Encoded:    0xf97e00
        testFloat(Float.NaN, "f97e00");
    }


    @Test
    public void test_rfc8949_appendixA_33()
    {
        // Diagnostic: -Infinity
        // Encoded:    0xf9fc00
        testFloat(Float.NEGATIVE_INFINITY, "f9fc00");
    }


    @Test
    public void test_rfc8949_appendixA_34()
    {
        // Diagnostic: Infinity
        // Encoded:    0xfa7f800000
        testFloat(Float.POSITIVE_INFINITY, "fa7f800000");
    }


    @Test
    public void test_rfc8949_appendixA_35()
    {
        // Diagnostic: NaN
        // Encoded:    0xfa7fc00000
        testFloat(Float.NaN, "fa7fc00000");
    }


    @Test
    public void test_rfc8949_appendixA_36()
    {
        // Diagnostic: -Infinity
        // Encoded:    0xfaff800000
        testFloat(Float.NEGATIVE_INFINITY, "faff800000");
    }


    @Test
    public void test_rfc8949_appendixA_37()
    {
        // Diagnostic: Infinity
        // Encoded:    0xfb7ff0000000000000
        testDouble(Double.POSITIVE_INFINITY, "fb7ff0000000000000");
    }


    @Test
    public void test_rfc8949_appendixA_38()
    {
        // Diagnostic: NaN
        // Encoded:    0xfb7ff8000000000000
        testDouble(Double.NaN, "fb7ff8000000000000");
    }


    @Test
    public void test_rfc8949_appendixA_39()
    {
        // Diagnostic: -Infinity
        // Encoded:    fbfff0000000000000
        testDouble(Double.NEGATIVE_INFINITY, "fbfff0000000000000");
    }


    @Test
    public void test_rfc8949_appendixA_40()
    {
        // Diagnostic: false
        // Encoded:    0xf4
        testBoolean(Boolean.FALSE, "f4");
    }


    @Test
    public void test_rfc8949_appendixA_41()
    {
        // Diagnostic: true
        // Encoded:    0xf5
        testBoolean(Boolean.TRUE, "f5");
    }


    @Test
    public void test_rfc8949_appendixA_42()
    {
        // Diagnostic: null
        // Encoded:    0xf6
        assertEquals(CBORNull.class, decode("f6").getClass());
    }


    @Test
    public void test_rfc8949_appendixA_43()
    {
        // Diagnostic: undefined
        // Encoded:    0xf7
        assertEquals(CBORUndefined.class, decode("f7").getClass());
    }


    @Test
    public void test_rfc8949_appendixA_44()
    {
        // Diagnostic: simple(16)
        // Encoded:    0xf0
        testSimpleValue(16, "f0");
    }


    @Test
    public void test_rfc8949_appendixA_45()
    {
        // Diagnostic: simple(255)
        // Encoded:    0xf8ff
        testSimpleValue(255, "f8ff");
    }


    @Test
    public void test_rfc8949_appendixA_46()
    {
        // Diagnostic: 0("2013-03-21T20:04:00Z")
        // Encoded:    0xc074323031332d30332d32315432303a30343a30305a

        // Tag
        CBORTaggedItem tagged = testTag(0, "c074323031332d30332d32315432303a30343a30305a");

        // Tag content
        testString("2013-03-21T20:04:00Z", tagged.getTagContent());
    }


    @Test
    public void test_rfc8949_appendixA_47()
    {
        // Diagnostic: 1(1363896240)
        // Encoded:    0xc11a514b67b0

        // Tag
        CBORTaggedItem tagged = testTag(1, "c11a514b67b0");

        // Tag content
        testInteger(1363896240, tagged.getTagContent());
    }


    @Test
    public void test_rfc8949_appendixA_48()
    {
        // Diagnostic: 1(1363896240.5)
        // Encoded:    0xc1fb41d452d9ec200000

        // Tag
        CBORTaggedItem tagged = testTag(1, "c1fb41d452d9ec200000");

        // Tag content
        testDouble(1363896240.5, tagged.getTagContent());
    }


    @Test
    public void test_rfc8949_appendixA_49()
    {
        // Diagnostic: 23(h'01020304')
        // Encoded:    0xd74401020304

        // Tag
        CBORTaggedItem tagged = testTag(23, "d74401020304");

        // Tag content
        testByteArray(fromHex("01020304"), tagged.getTagContent());
    }


    @Test
    public void test_rfc8949_appendixA_50()
    {
        // Diagnostic: 24(h'6449455446')
        // Encoded:    0xd818456449455446

        // Tag
        CBORTaggedItem tagged = testTag(24, "d818456449455446");

        // Tag content
        testByteArray(fromHex("6449455446"), tagged.getTagContent());
    }


    @Test
    public void test_rfc8949_appendixA_51()
    {
        // Diagnostic: 32("http://www.example.com")
        // Encoded:    0xd82076687474703a2f2f7777772e6578616d706c652e636f6d

        // Tag
        CBORTaggedItem tagged = testTag(32, "d82076687474703a2f2f7777772e6578616d706c652e636f6d");

        // Tag content
        testString("http://www.example.com", tagged.getTagContent());
    }


    @Test
    public void test_rfc8949_appendixA_52()
    {
        // Diagnostic: h''
        // Encoded:    0x40
        testByteArray(fromHex(""), "40");
    }


    @Test
    public void test_rfc8949_appendixA_53()
    {
        // Diagnostic: h'01020304'
        // Encoded:    0x4401020304
        testByteArray(fromHex("01020304"), "4401020304");
    }


    @Test
    public void test_rfc8949_appendixA_54()
    {
        // Diagnostic: ""
        // Encoded:    0x60
        testString("", "60");
    }


    @Test
    public void test_rfc8949_appendixA_55()
    {
        // Diagnostic: "a"
        // Encoded:    0x6161
        testString("a", "6161");
    }


    @Test
    public void test_rfc8949_appendixA_56()
    {
        // Diagnostic: "IETF"
        // Encoded:    0x6449455446
        testString("IETF", "6449455446");
    }


    @Test
    public void test_rfc8949_appendixA_57()
    {
        // Diagnostic: "\"\\"
        // Encoded:    0x62225c
        testString("\"\\", "62225c");
    }


    @Test
    public void test_rfc8949_appendixA_58()
    {
        // Diagnostic: "\u00fc"
        // Encoded:    0x62c3bc
        testString("\u00fc", "62c3bc");
    }


    @Test
    public void test_rfc8949_appendixA_59()
    {
        // Diagnostic: "\u6c34"
        // Encoded:    0x63e6b0b4
        testString("\u6c34", "63e6b0b4");
    }


    @Test
    public void test_rfc8949_appendixA_60()
    {
        // Diagnostic: "\ud800\udd51"
        // Encoded:    0x64f0908591
        testString("\ud800\udd51", "64f0908591");
    }


    @Test
    public void test_rfc8949_appendixA_61()
    {
        // Diagnostic: []
        // Encoded:    0x80

        // Array
        CBORItemList list = testArray("80");

        // Array size
        assertEquals(0, list.getItems().size());
    }


    @Test
    public void test_rfc8949_appendixA_62()
    {
        // Diagnostic: [1, 2, 3]
        // Encoded:    0x83010203

        // Array
        CBORItemList list = testArray("83010203");

        // Array size
        List<? extends CBORItem> items = list.getItems();
        assertEquals(3, items.size());

        testInteger(1, items.get(0));
        testInteger(2, items.get(1));
        testInteger(3, items.get(2));
    }


    @Test
    public void test_rfc8949_appendixA_63()
    {
        // Diagnostic: [1, [2, 3], [4, 5]]
        // Encoded:    0x8301820203820405

        // Array
        CBORItemList list = testArray("8301820203820405");

        // Array size
        List<? extends CBORItem> items = list.getItems();
        assertEquals(3, items.size());

        // Element 0
        testInteger(1, items.get(0));

        // Element 1
        CBORItemList element1 = testArray(items.get(1));
        assertEquals(2, element1.getItems().size());

        // Element 1 - Element 0
        testInteger(2, element1.getItems().get(0));

        // Element 1 - Element 1
        testInteger(3, element1.getItems().get(1));

        // Element 2
        CBORItemList element2 = testArray(items.get(2));
        assertEquals(2, element2.getItems().size());

        // Element 2 - Element 0
        testInteger(4, element2.getItems().get(0));

        // Element 2 - Element 1
        testInteger(5, element2.getItems().get(1));
    }


    @Test
    public void test_rfc8949_appendixA_64()
    {
        // Diagnostic: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25]
        // Encoded:    0x98190102030405060708090a0b0c0d0e0f101112131415161718181819

        // Array
        CBORItemList list = testArray("98190102030405060708090a0b0c0d0e0f101112131415161718181819");

        // Array size
        List<? extends CBORItem> items = list.getItems();
        assertEquals(25, items.size());

        for (int i = 0; i < 25; i++)
        {
            CBORItem item = items.get(i);
            testInteger(i + 1, item);
        }
    }


    @Test
    public void test_rfc8949_appendixA_65()
    {
        // Diagnostic: {}
        // Encoded:    0xa0

        // Map
        CBORPairList list = testMap("a0");

        // Map size
        assertEquals(0, list.getPairs().size());
    }


    @Test
    public void test_rfc8949_appendixA_66()
    {
        // Diagnostic: {1: 2, 3: 4}
        // Encoded:    0xa201020304

        // Map
        CBORPairList list = testMap("a201020304");

        // Map size
        assertEquals(2, list.getPairs().size());

        // 1: 2
        CBORPair pair0 = list.getPairs().get(0);
        testInteger(1, pair0.getKey());
        testInteger(2, pair0.getValue());

        // 3: 4
        CBORPair pair1 = list.getPairs().get(1);
        testInteger(3, pair1.getKey());
        testInteger(4, pair1.getValue());
    }


    @Test
    public void test_rfc8949_appendixA_67()
    {
        // Diagnostic: {"a": 1, "b": [2, 3]}
        // Encoded:    0xa26161016162820203

        // Map
        CBORPairList list = testMap("a26161016162820203");

        // Map size
        assertEquals(2, list.getPairs().size());

        // "a": 1
        CBORPair pair0 = list.getPairs().get(0);
        testString("a", pair0.getKey());
        testInteger(1, pair0.getValue());

        // "b": [2, 3]
        CBORPair pair1 = list.getPairs().get(1);
        testString("b", pair1.getKey());

        CBORItemList pair1Value = testArray(pair1.getValue());
        assertEquals(2, pair1Value.getItems().size());
        testInteger(2, pair1Value.getItems().get(0));
        testInteger(3, pair1Value.getItems().get(1));
    }


    @Test
    public void test_rfc8949_appendixA_68()
    {
        // Diagnostic: ["a", {"b": "c"}]
        // Encoded:    0x826161a161626163

        // Array
        CBORItemList list = testArray("826161a161626163");

        // Array size
        assertEquals(2, list.getItems().size());

        // Element 0: "a"
        testString("a", list.getItems().get(0));

        // Element 1: {"b": "c"}
        CBORPairList element1 = testMap(list.getItems().get(1));
        assertEquals(1, element1.getPairs().size());

        CBORPair pair0 = element1.getPairs().get(0);
        testString("b", pair0.getKey());
        testString("c", pair0.getValue());
    }


    @Test
    public void test_rfc8949_appendixA_69()
    {
        // Diagnostic: {"a": "A", "b": "B", "c": "C", "d": "D", "e": "E"}
        // Encoded:    0xa56161614161626142616361436164614461656145

        // Map
        CBORPairList list = testMap("a56161614161626142616361436164614461656145");
        List<? extends CBORPair> pairs = list.getPairs();
        assertEquals(5, pairs.size());

        String[] keys   = { "a", "b", "c", "d", "e" };
        String[] values = { "A", "B", "C", "D", "E" };

        for (int i = 0; i < 5; i++)
        {
            CBORPair pair = pairs.get(i);

            testString(keys[i],   pair.getKey());
            testString(values[i], pair.getValue());
        }
    }


    @Test
    public void test_rfc8949_appendixA_70()
    {
        // Diagnostic: (_ h'0102', h'030405')
        // Encoded:    0x5f42010243030405ff
        testByteArray(fromHex("0102030405"), "5f42010243030405ff");
    }


    @Test
    public void test_rfc8949_appendixA_71()
    {
        // Diagnostic: (_ "strea", "ming")
        // Encoded:    0x7f657374726561646d696e67ff
        testString("streaming", "7f657374726561646d696e67ff");
    }


    @Test
    public void test_rfc8949_appendixA_72()
    {
        // Diagnostic: [_ ]
        // Encoded:    0x9fff

        // Array
        CBORItemList list = testArray("9fff");
        assertEquals(0, list.getItems().size());
    }


    @Test
    public void test_rfc8949_appendixA_73()
    {
        // Diagnostic: [_ 1, [2, 3], [_ 4, 5]]
        // Encoded:    0x9f018202039f0405ffff

        // Array
        CBORItemList list = testArray("9f018202039f0405ffff");

        // Array size
        List<? extends CBORItem> items = list.getItems();
        assertEquals(3, items.size());

        // Element 0
        testInteger(1, items.get(0));

        // Element 1
        CBORItemList element1 = testArray(items.get(1));
        assertEquals(2, element1.getItems().size());

        // Element 1 - Element 0
        testInteger(2, element1.getItems().get(0));

        // Element 1 - Element 1
        testInteger(3, element1.getItems().get(1));

        // Element 2
        CBORItemList element2 = testArray(items.get(2));
        assertEquals(2, element2.getItems().size());

        // Element 2 - Element 0
        testInteger(4, element2.getItems().get(0));

        // Element 2 - Element 1
        testInteger(5, element2.getItems().get(1));
    }


    @Test
    public void test_rfc8949_appendixA_74()
    {
        // Diagnostic: [_ 1, [2, 3], [4, 5]]
        // Encoded:    0x9f01820203820405ff

        // Array
        CBORItemList list = testArray("9f01820203820405ff");

        // Array size
        List<? extends CBORItem> items = list.getItems();
        assertEquals(3, items.size());

        // Element 0
        testInteger(1, items.get(0));

        // Element 1
        CBORItemList element1 = testArray(items.get(1));
        assertEquals(2, element1.getItems().size());

        // Element 1 - Element 0
        testInteger(2, element1.getItems().get(0));

        // Element 1 - Element 1
        testInteger(3, element1.getItems().get(1));

        // Element 2
        CBORItemList element2 = testArray(items.get(2));
        assertEquals(2, element2.getItems().size());

        // Element 2 - Element 0
        testInteger(4, element2.getItems().get(0));

        // Element 2 - Element 1
        testInteger(5, element2.getItems().get(1));
    }


    @Test
    public void test_rfc8949_appendixA_75()
    {
        // Diagnostic: [1, [2, 3], [_ 4, 5]]
        // Encoded:    0x83018202039f0405ff

        // Array
        CBORItemList list = testArray("83018202039f0405ff");

        // Array size
        List<? extends CBORItem> items = list.getItems();
        assertEquals(3, items.size());

        // Element 0
        testInteger(1, items.get(0));

        // Element 1
        CBORItemList element1 = testArray(items.get(1));
        assertEquals(2, element1.getItems().size());

        // Element 1 - Element 0
        testInteger(2, element1.getItems().get(0));

        // Element 1 - Element 1
        testInteger(3, element1.getItems().get(1));

        // Element 2
        CBORItemList element2 = testArray(items.get(2));
        assertEquals(2, element2.getItems().size());

        // Element 2 - Element 0
        testInteger(4, element2.getItems().get(0));

        // Element 2 - Element 1
        testInteger(5, element2.getItems().get(1));
    }


    @Test
    public void test_rfc8949_appendixA_76()
    {
        // Diagnostic: [1, [_ 2, 3], [4, 5]]
        // Encoded:    0x83019f0203ff820405

        // Array
        CBORItemList list = testArray("83019f0203ff820405");

        // Array size
        List<? extends CBORItem> items = list.getItems();
        assertEquals(3, items.size());

        // Element 0
        testInteger(1, items.get(0));

        // Element 1
        CBORItemList element1 = testArray(items.get(1));
        assertEquals(2, element1.getItems().size());

        // Element 1 - Element 0
        testInteger(2, element1.getItems().get(0));

        // Element 1 - Element 1
        testInteger(3, element1.getItems().get(1));

        // Element 2
        CBORItemList element2 = testArray(items.get(2));
        assertEquals(2, element2.getItems().size());

        // Element 2 - Element 0
        testInteger(4, element2.getItems().get(0));

        // Element 2 - Element 1
        testInteger(5, element2.getItems().get(1));
    }


    @Test
    public void test_rfc8949_appendixA_77()
    {
        // Diagnostic: [_ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25]
        // Encoded:    0x9f0102030405060708090a0b0c0d0e0f101112131415161718181819ff

        // Array
        CBORItemList list = testArray("9f0102030405060708090a0b0c0d0e0f101112131415161718181819ff");

        // Array size
        List<? extends CBORItem> items = list.getItems();
        assertEquals(25, items.size());

        for (int i = 0; i < 25; i++)
        {
            CBORItem item = items.get(i);
            testInteger(i + 1, item);
        }
    }


    @Test
    public void test_rfc8949_appendixA_78()
    {
        // Diagnostic: {_ "a": 1, "b": [_ 2, 3]}
        // Encoded:    0xbf61610161629f0203ffff

        // Map
        CBORPairList list = testMap("bf61610161629f0203ffff");

        // Map size
        assertEquals(2, list.getPairs().size());

        // "a": 1
        CBORPair pair0 = list.getPairs().get(0);
        testString("a", pair0.getKey());
        testInteger(1, pair0.getValue());

        // "b": [2, 3]
        CBORPair pair1 = list.getPairs().get(1);
        testString("b", pair1.getKey());

        CBORItemList pair1Value = testArray(pair1.getValue());
        assertEquals(2, pair1Value.getItems().size());
        testInteger(2, pair1Value.getItems().get(0));
        testInteger(3, pair1Value.getItems().get(1));
    }


    @Test
    public void test_rfc8949_appendixA_79()
    {
        // Diagnostic: ["a", {_ "b": "c"}]
        // Encoded:    0x826161bf61626163ff

        // Array
        CBORItemList list = testArray("826161bf61626163ff");

        // Array size
        assertEquals(2, list.getItems().size());

        // Element 0: "a"
        testString("a", list.getItems().get(0));

        // Element 1: {"b": "c"}
        CBORPairList element1 = testMap(list.getItems().get(1));
        assertEquals(1, element1.getPairs().size());

        CBORPair pair0 = element1.getPairs().get(0);
        testString("b", pair0.getKey());
        testString("c", pair0.getValue());
    }


    @Test
    public void test_rfc8949_appendixA_80()
    {
        // Diagnostic: {_ "Fun": true, "Amt": -2}
        // Encoded:    0xbf6346756ef563416d7421ff

        // Map
        CBORPairList list = testMap("bf6346756ef563416d7421ff");
        List<? extends CBORPair> pairs = list.getPairs();

        assertEquals(2, pairs.size());

        // "Fun": true
        CBORPair pair0 = pairs.get(0);
        testString("Fun", pair0.getKey());
        testBoolean(Boolean.TRUE, pair0.getValue());

        // "Amt": -2
        CBORPair pair1 = pairs.get(1);
        testString("Amt", pair1.getKey());
        testInteger(-2, pair1.getValue());
    }


    @Test
    public void test_half_precision_01()
    {
        testFloat(1.0f, "f93c00");
    }


    @Test
    public void test_half_precision_02()
    {
        testFloat(1.0009765625f, "f93c01");
    }


    @Test
    public void test_half_precision_03()
    {
        testFloat(1.001953125f, "f93c02");
    }


    @Test
    public void test_half_precision_04()
    {
        testFloat(1.9990234375f, "f93fff");
    }


    @Test
    public void test_half_precision_05()
    {
        testFloat(2f, "f94000");
    }


    @Test
    public void test_half_precision_06()
    {
        testFloat(-2f, "f9c000");
    }


    @Test
    public void test_half_precision_07()
    {
        testFloat(65472f, "f97bfe");
    }


    @Test
    public void test_half_precision_08()
    {
        testFloat(65504f, "f97bff");
    }


    @Test
    public void test_half_precision_09()
    {
        testFloat(-65504f, "f9fbff");
    }


    @Test
    public void test_half_precision_10()
    {
        testFloat((float)Math.pow(2, -14), "f90400");
    }


    @Test
    public void test_half_precision_11()
    {
        testFloat((float)Math.pow(2, -24), "f90001");
    }


    @Test
    public void test_half_precision_12()
    {
        testFloat(0f, "f90000");
    }


    @Test
    public void test_half_precision_13()
    {
        testFloat(-0f, "f98000");
    }


    @Test
    public void test_half_precision_14()
    {
        testFloat(Float.POSITIVE_INFINITY, "f97c00");
    }


    @Test
    public void test_half_precision_15()
    {
        testFloat(Float.NEGATIVE_INFINITY, "f9fc00");
    }


    @Test
    public void test_half_precision_16()
    {
        testFloat(Float.NaN, "f97d00");
    }


    @Test
    public void test_half_precision_17()
    {
        testFloat(2.5f, "f94100");
    }


    @Test
    public void test_half_precision_18()
    {
        testFloat(-0.625f, "f9b900");
    }


    @Test
    public void test_half_precision_19()
    {
        testFloat((float)Math.pow(2, -16), "f90100");
    }


    @Test
    public void test_rfc8610_appendixG_3_1()
    {
        testByteArrayToString("h'01'", "01", false);
        testByteArrayToString("<<1>>", "01", true);
    }


    @Test
    public void test_rfc8610_appendixG_3_2()
    {
        testByteArrayToString("<<1, 2>>", "0102", true);
    }


    @Test
    public void test_rfc8610_appendixG_3_3()
    {
        testByteArrayToString("<<\"foo\", null>>", "63666F6FF6", true);
    }


    @Test
    public void test_rfc8610_appendixG_3_4()
    {
        testByteArrayToString("h''", "", false);
        testByteArrayToString("<<>>", "", true);
    }


    @Test
    public void test_float_positive_infinity()
    {
        testFloat(Float.POSITIVE_INFINITY, CBORFloat.POSITIVE_INFINITY);
    }


    @Test
    public void test_float_negative_infinity()
    {
        testFloat(Float.NEGATIVE_INFINITY, CBORFloat.NEGATIVE_INFINITY);
    }


    @Test
    public void test_float_nan()
    {
        testFloat(Float.NaN, CBORFloat.NaN);
    }


    @Test
    public void test_double_positive_infinity()
    {
        testDouble(Double.POSITIVE_INFINITY, CBORDouble.POSITIVE_INFINITY);
    }


    @Test
    public void test_double_negative_infinity()
    {
        testDouble(Double.NEGATIVE_INFINITY, CBORDouble.NEGATIVE_INFINITY);
    }


    @Test
    public void test_double_nan()
    {
        testDouble(Double.NaN, CBORDouble.NaN);
    }


    @Test
    public void test_rfc8610_appendixG_3_1_with_tag_24()
    {
        // 1
        CBORInteger nestedItem = new CBORInteger(1);

        // <<1>>
        CBORByteArray tagContent = new CBORByteArray(nestedItem.encode());

        // 24(<<1>>)
        CBORItem item = new CBORTaggedItem(24, tagContent);

        String expected = "24(<<1>>)";
        String actual   = item.toString();

        assertEquals(expected, actual);
    }


    @Test
    public void test_rfc8610_appendixG_3_2_with_tag_24() throws IOException
    {
        // <<1, 2>>
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(new CBORInteger(1).encode());
        out.write(new CBORInteger(2).encode());
        CBORItem tagContent = new CBORByteArray(out.toByteArray());

        // 24(<<1, 2>>)
        CBORItem item = new CBORTaggedItem(24, tagContent);

        String expected = "24(<<1, 2>>)";
        String actual   = item.toString();

        assertEquals(expected, actual);
    }


    @Test
    public void test_rfc8610_appendixG_3_3_with_tag_24() throws IOException
    {
        // <<"foo", null>>
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(new CBORString("foo").encode());
        out.write(CBORNull.INSTANCE.encode());
        CBORItem tagContent = new CBORByteArray(out.toByteArray());

        // 24(<<"foo", null>>)
        CBORItem item = new CBORTaggedItem(24, tagContent);

        String expected = "24(<<\"foo\", null>>)";
        String actual   = item.toString();

        assertEquals(expected, actual);
    }


    @Test
    public void test_rfc8610_appendixG_3_4_with_tag_24()
    {
        // <<>>
        CBORItem tagContent = new CBORByteArray(new byte[] {});

        // 24(<<>>)
        CBORItem item = new CBORTaggedItem(24, tagContent);

        String expected = "24(<<>>)";
        String actual   = item.toString();

        assertEquals(expected, actual);
    }
}

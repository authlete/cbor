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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;


public class CBOREncodingTest
{
    private static final CBORizer cborizer = new CBORizer();


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


    private static byte[] encode(Object object)
    {
        if (object instanceof CBORItem)
        {
            return ((CBORItem)object).encode();
        }

        return cborizer.cborizeObject(object).encode();
    }


    private static void test(Object input, String expectedHex)
    {
        byte[] expected = fromHex(expectedHex);
        byte[] actual   = encode(input);

        assertArrayEquals(expected, actual);
    }


    private static void testBoolean(Boolean input, String expectedHex)
    {
        test(input, expectedHex);
    }


    private static void testInteger(int input, String expectedHex)
    {
        test(input, expectedHex);
    }


    private static void testLong(long input, String expectedHex)
    {
        test(input, expectedHex);
    }


    private static void testBigInteger(String input, String expectedHex)
    {
        test(new BigInteger(input), expectedHex);
    }


    private static void testFloat(float input, String expectedHex)
    {
        test(input, expectedHex);
    }


    private static void testDouble(double input, String expectedHex)
    {
        test(input, expectedHex);
    }


    private static void testByteArray(byte[] input, String expectedHex)
    {
        test(input, expectedHex);
    }


    private static void testString(String input, String expectedHex)
    {
        test(input, expectedHex);
    }


    private static void testSimpleValue(int input, String expectedHex)
    {
        test(new CBORSimpleValue(input), expectedHex);
    }


    private static void testArray(List<?> list, String expectedHex)
    {
        test(list, expectedHex);
    }


    private static void testMap(Map<?,?> map, String expectedHex)
    {
        test(map, expectedHex);
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

        // Java does not have "half" precision floating point.
        // testFloat(0.0f, "f90000");
    }


    @Test
    public void test_rfc8949_appendixA_19()
    {
        // Diagnostic: -0.0
        // Encoded:    0xf98000

        // Java does not have "half" precision floating point.
        // testFloat(-0.0f, "f98000");
    }


    @Test
    public void test_rfc8949_appendixA_20()
    {
        // Diagnostic: 1.0
        // Encoded:    0xf93c00

        // Java does not have "half" precision floating point.
        // testFloat(1.0f, "f93c00");
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

        // Java does not have "half" precision floating point.
        // testFloat(1.5f, "f93e00");
    }


    @Test
    public void test_rfc8949_appendixA_23()
    {
        // Diagnostic: 65504.0
        // Encoded:    0xf97bff

        // Java does not have "half" precision floating point.
        // testFloat(65504.0f, "f97bff");
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

        // Java does not have "half" precision floating point.
        // testFloat(5.960464477539063e-8f, "f90001");
    }


    @Test
    public void test_rfc8949_appendixA_28()
    {
        // Diagnostic: 0.00006103515625
        // Encoded:    0xf90400

        // Java does not have "half" precision floating point.
        // testFloat(0.00006103515625f, "f90400");
    }


    @Test
    public void test_rfc8949_appendixA_29()
    {
        // Diagnostic: -4.0
        // Encoded:    0xf9c400

        // Java does not have "half" precision floating point.
        // testFloat(-4.0f, "f9c400");
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

        // Java does not have "half" precision floating point.
        // testFloat(Float.POSITIVE_INFINITY, "f97c00");
    }


    @Test
    public void test_rfc8949_appendixA_32()
    {
        // Diagnostic: NaN
        // Encoded:    0xf97e00

        // Java does not have "half" precision floating point.
        // testFloat(Float.NaN, "f97e00");
    }


    @Test
    public void test_rfc8949_appendixA_33()
    {
        // Diagnostic: -Infinity
        // Encoded:    0xf9fc00

        // Java does not have "half" precision floating point.
        // testFloat(Float.NEGATIVE_INFINITY, "f9fc00");
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
        test(null, "f6");
        test(CBORNull.INSTANCE, "f6");
    }


    @Test
    public void test_rfc8949_appendixA_43()
    {
        // Diagnostic: undefined
        // Encoded:    0xf7

        test(CBORUndefined.INSTANCE, "f7");
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
        CBORTaggedItem item = new CBORTaggedItem(0, new CBORString("2013-03-21T20:04:00Z"));

        test(item, "c074323031332d30332d32315432303a30343a30305a");
    }


    @Test
    public void test_rfc8949_appendixA_47()
    {
        // Diagnostic: 1(1363896240)
        // Encoded:    0xc11a514b67b0

        // Tag
        CBORTaggedItem item = new CBORTaggedItem(1, new CBORInteger(1363896240));

        test(item, "c11a514b67b0");
    }


    @Test
    public void test_rfc8949_appendixA_48()
    {
        // Diagnostic: 1(1363896240.5)
        // Encoded:    0xc1fb41d452d9ec200000

        // Tag
        CBORTaggedItem item = new CBORTaggedItem(1, new CBORDouble(1363896240.5));

        test(item, "c1fb41d452d9ec200000");
    }


    @Test
    public void test_rfc8949_appendixA_49()
    {
        // Diagnostic: 23(h'01020304')
        // Encoded:    0xd74401020304

        // Tag
        CBORTaggedItem item = new CBORTaggedItem(23, new CBORByteArray(fromHex("01020304")));

        test(item, "d74401020304");
    }


    @Test
    public void test_rfc8949_appendixA_50()
    {
        // Diagnostic: 24(h'6449455446')
        // Encoded:    0xd818456449455446

        // Tag
        CBORTaggedItem item = new CBORTaggedItem(24, new CBORByteArray(fromHex("6449455446")));

        test(item, "d818456449455446");
    }


    @Test
    public void test_rfc8949_appendixA_51()
    {
        // Diagnostic: 32("http://www.example.com")
        // Encoded:    0xd82076687474703a2f2f7777772e6578616d706c652e636f6d

        // Tag
        CBORTaggedItem item = new CBORTaggedItem(32, new CBORString("http://www.example.com"));

        test(item, "d82076687474703a2f2f7777772e6578616d706c652e636f6d");
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
        testArray(Collections.emptyList(), "80");
    }


    @Test
    public void test_rfc8949_appendixA_62()
    {
        // Diagnostic: [1, 2, 3]
        // Encoded:    0x83010203

        List<Integer> list = Arrays.asList(1,2,3);

        testArray(list, "83010203");
    }


    @Test
    public void test_rfc8949_appendixA_63()
    {
        // Diagnostic: [1, [2, 3], [4, 5]]
        // Encoded:    0x8301820203820405

        List<Object> list = new ArrayList<>();
        list.add(1);
        list.add(Arrays.asList(2,3));
        list.add(Arrays.asList(4,5));

        testArray(list, "8301820203820405");
    }


    @Test
    public void test_rfc8949_appendixA_64()
    {
        // Diagnostic: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25]
        // Encoded:    0x98190102030405060708090a0b0c0d0e0f101112131415161718181819

        List<Integer> list = new ArrayList<>();

        for (int i = 1; i <= 25; i++)
        {
            list.add(i);
        }

        testArray(list, "98190102030405060708090a0b0c0d0e0f101112131415161718181819");
    }


    @Test
    public void test_rfc8949_appendixA_65()
    {
        // Diagnostic: {}
        // Encoded:    0xa0

        Map<Object,Object> map = Collections.emptyMap();

        testMap(map, "a0");
    }


    @Test
    public void test_rfc8949_appendixA_66()
    {
        // Diagnostic: {1: 2, 3: 4}
        // Encoded:    0xa201020304

        Map<Integer,Integer> map = new LinkedHashMap<>();
        map.put(1, 2);
        map.put(3, 4);

        testMap(map, "a201020304");
    }


    @Test
    public void test_rfc8949_appendixA_67()
    {
        // Diagnostic: {"a": 1, "b": [2, 3]}
        // Encoded:    0xa26161016162820203

        Map<Object,Object> map = new LinkedHashMap<>();
        map.put("a", 1);
        map.put("b", Arrays.asList(2,3));

        testMap(map, "a26161016162820203");
    }


    @Test
    public void test_rfc8949_appendixA_68()
    {
        // Diagnostic: ["a", {"b": "c"}]
        // Encoded:    0x826161a161626163

        List<Object> list = Arrays.asList("a", Collections.singletonMap("b","c"));

        testArray(list, "826161a161626163");
    }


    @Test
    public void test_rfc8949_appendixA_69()
    {
        // Diagnostic: {"a": "A", "b": "B", "c": "C", "d": "D", "e": "E"}
        // Encoded:    0xa56161614161626142616361436164614461656145

        Map<String,String> map = new LinkedHashMap<>();
        map.put("a", "A");
        map.put("b", "B");
        map.put("c", "C");
        map.put("d", "D");
        map.put("e", "E");

        testMap(map, "a56161614161626142616361436164614461656145");
    }


    @Test
    public void test_rfc8949_appendixA_70()
    {
        // Diagnostic: (_ h'0102', h'030405')
        // Encoded:    0x5f42010243030405ff
    }


    @Test
    public void test_rfc8949_appendixA_71()
    {
        // Diagnostic: (_ "strea", "ming")
        // Encoded:    0x7f657374726561646d696e67ff
    }


    @Test
    public void test_rfc8949_appendixA_72()
    {
        // Diagnostic: [_ ]
        // Encoded:    0x9fff
    }


    @Test
    public void test_rfc8949_appendixA_73()
    {
        // Diagnostic: [_ 1, [2, 3], [_ 4, 5]]
        // Encoded:    0x9f018202039f0405ffff
    }


    @Test
    public void test_rfc8949_appendixA_74()
    {
        // Diagnostic: [_ 1, [2, 3], [4, 5]]
        // Encoded:    0x9f01820203820405ff
    }


    @Test
    public void test_rfc8949_appendixA_75()
    {
        // Diagnostic: [1, [2, 3], [_ 4, 5]]
        // Encoded:    0x83018202039f0405ff
    }


    @Test
    public void test_rfc8949_appendixA_76()
    {
        // Diagnostic: [1, [_ 2, 3], [4, 5]]
        // Encoded:    0x83019f0203ff820405
    }


    @Test
    public void test_rfc8949_appendixA_77()
    {
        // Diagnostic: [_ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25]
        // Encoded:    0x9f0102030405060708090a0b0c0d0e0f101112131415161718181819ff
    }


    @Test
    public void test_rfc8949_appendixA_78()
    {
        // Diagnostic: {_ "a": 1, "b": [_ 2, 3]}
        // Encoded:    0xbf61610161629f0203ffff
    }


    @Test
    public void test_rfc8949_appendixA_79()
    {
        // Diagnostic: ["a", {_ "b": "c"}]
        // Encoded:    0x826161bf61626163ff
    }


    @Test
    public void test_rfc8949_appendixA_80()
    {
        // Diagnostic: {_ "Fun": true, "Amt": -2}
        // Encoded:    0xbf6346756ef563416d7421ff
    }
}

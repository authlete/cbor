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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;


public class CBOROutputStreamTest
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


    private static void testBoolean(String expectedHex, boolean input)
    {
        byte[] expected = fromHex(expectedHex);
        byte[] result   = writeBoolean(input);

        assertArrayEquals(expected, result);
    }


    private static byte[] writeBoolean(boolean value)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CBOROutputStream cos = new CBOROutputStream(baos);

        try
        {
            cos.writeBoolean(value);
        }
        catch (IOException e)
        {
            // This never happens.
        }

        return baos.toByteArray();
    }


    private static void testInteger(String expectedHex, int input)
    {
        byte[] expected = fromHex(expectedHex);
        byte[] result   = writeInteger(input);

        assertArrayEquals(expected, result);
    }


    private static byte[] writeInteger(int value)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CBOROutputStream cos = new CBOROutputStream(baos);

        try
        {
            cos.writeInteger(value);
        }
        catch (IOException e)
        {
            // This never happens.
        }

        return baos.toByteArray();
    }


    private static void testLong(String expectedHex, long input)
    {
        byte[] expected = fromHex(expectedHex);
        byte[] result   = writeLong(input);

        assertArrayEquals(expected, result);
    }


    private static byte[] writeLong(long value)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CBOROutputStream cos = new CBOROutputStream(baos);

        try
        {
            cos.writeLong(value);
        }
        catch (IOException e)
        {
            // This never happens.
        }

        return baos.toByteArray();
    }


    private static void testFloat(String expectedHex, float input)
    {
        byte[] expected = fromHex(expectedHex);
        byte[] result   = writeFloat(input);

        assertArrayEquals(expected, result);
    }


    private static byte[] writeFloat(float value)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CBOROutputStream cos = new CBOROutputStream(baos);

        try
        {
            cos.writeFloat(value);
        }
        catch (IOException e)
        {
            // This never happens.
        }

        return baos.toByteArray();
    }


    private static void testDouble(String expectedHex, double input)
    {
        byte[] expected = fromHex(expectedHex);
        byte[] result   = writeDouble(input);

        assertArrayEquals(expected, result);
    }


    private static byte[] writeDouble(double value)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CBOROutputStream cos = new CBOROutputStream(baos);

        try
        {
            cos.writeDouble(value);
        }
        catch (IOException e)
        {
            // This never happens.
        }

        return baos.toByteArray();
    }


    private static void testBigInteger(String expectedHex, BigInteger input)
    {
        byte[] expected = fromHex(expectedHex);
        byte[] result   = writeBigInteger(input);

        assertArrayEquals(expected, result);
    }


    private static byte[] writeBigInteger(BigInteger value)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CBOROutputStream cos = new CBOROutputStream(baos);

        try
        {
            cos.writeBigInteger(value);
        }
        catch (IOException e)
        {
            // This never happens.
        }

        return baos.toByteArray();
    }


    private static void testByteArray(String expectedHex, byte[] input)
    {
        byte[] expected = fromHex(expectedHex);
        byte[] result   = writeByteArray(input);

        assertArrayEquals(expected, result);
    }


    private static byte[] writeByteArray(byte[] value)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CBOROutputStream cos = new CBOROutputStream(baos);

        try
        {
            cos.writeByteArray(value);
        }
        catch (IOException e)
        {
            // This never happens.
        }

        return baos.toByteArray();
    }


    private static void testString(String expectedHex, String input)
    {
        byte[] expected = fromHex(expectedHex);
        byte[] result   = writeString(input);

        assertArrayEquals(expected, result);
    }


    private static byte[] writeString(String value)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CBOROutputStream cos = new CBOROutputStream(baos);

        try
        {
            cos.writeString(value);
        }
        catch (IOException e)
        {
            // This never happens.
        }

        return baos.toByteArray();
    }


    private static void testCollection(String expectedHex, Collection<?> input)
    {
        byte[] expected = fromHex(expectedHex);
        byte[] result   = writeCollection(input);

        assertArrayEquals(expected, result);
    }


    private static byte[] writeCollection(Collection<?> value)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CBOROutputStream cos = new CBOROutputStream(baos);

        try
        {
            cos.writeCollection(value);
        }
        catch (IOException e)
        {
            // This never happens.
        }

        return baos.toByteArray();
    }


    private static void testMap(String expectedHex, Map<?,?> input)
    {
        byte[] expected = fromHex(expectedHex);
        byte[] result   = writeMap(input);

        assertArrayEquals(expected, result);
    }


    private static byte[] writeMap(Map<?,?> value)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CBOROutputStream cos = new CBOROutputStream(baos);

        try
        {
            cos.writeMap(value);
        }
        catch (IOException e)
        {
            // This never happens.
        }

        return baos.toByteArray();
    }


    private static void testObject(String expectedHex, Object input)
    {
        byte[] expected = fromHex(expectedHex);
        byte[] result   = writeObject(input);

        assertArrayEquals(expected, result);
    }


    private static byte[] writeObject(Object value)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CBOROutputStream cos = new CBOROutputStream(baos);

        try
        {
            cos.writeObject(value);
        }
        catch (IOException e)
        {
            // This never happens.
        }

        return baos.toByteArray();
    }


    @Test
    public void test_boolean_false()
    {
        testBoolean("f4", false);
    }


    @Test
    public void test_boolean_true()
    {
        testBoolean("f5", true);
    }


    @Test
    public void test_integer()
    {
        testInteger("1864", 100);
    }


    @Test
    public void test_long()
    {
        testLong("1b000000e8d4a51000", 1000000000000L);
    }


    @Test
    public void test_big_integer()
    {
        testBigInteger("c249010000000000000000", new BigInteger("18446744073709551616"));
    }


    @Test
    public void test_float()
    {
        testFloat("fa7f7fffff", 3.4028234663852886e+38f);
    }


    @Test
    public void test_double()
    {
        testDouble("fb3ff199999999999a", 1.1);
    }


    @Test
    public void test_byte_array()
    {
        byte[] input = {
                (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04
        };

        testByteArray("4401020304", input);
    }


    @Test
    public void test_string()
    {
        testString("6161", "a");
    }


    @Test
    public void test_collection()
    {
        List<Integer> input = List.of(1, 2, 3);

        testCollection("83010203", input);
    }


    @Test
    public void test_map()
    {
        Map<Object, Object> input = new LinkedHashMap<>();

        input.put("a", 1);
        input.put("b", List.of(2,3));

        testMap("a26161016162820203", input);
    }


    @Test
    public void test_object()
    {
        Object input = new Object() {
            @Override
            public String toString() { return "a"; }
        };

        testObject("6161", input);
    }
}

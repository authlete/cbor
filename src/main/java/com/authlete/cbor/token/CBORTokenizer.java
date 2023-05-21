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
package com.authlete.cbor.token;


import static com.authlete.cbor.CBORConstants.BIG_INTEGER_LONG_MAX;
import static com.authlete.cbor.CBORConstants.BIG_INTEGER_MINUS_ONE;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import com.authlete.cbor.CBORInsufficientDataException;
import com.authlete.cbor.CBORInvalidInfoException;
import com.authlete.cbor.CBORInvalidSimpleValueException;
import com.authlete.cbor.CBORMalformedUtf8Exception;
import com.authlete.cbor.CBORTooLongException;


/**
 * A tokenizer that tokenizes the content of an input stream of CBOR data items
 * so that the CBOR decoder ({@link com.authlete.cbor.CBORDecoder CBORDecoder})
 * can construct CBOR data items ({@link com.authlete.cbor.CBORItem CBORItem}).
 */
public class CBORTokenizer
{
    private final InputStream inputStream;
    private int readCount;


    /**
     * A constructor with an input stream of CBOR data items.
     *
     * @param inputStream
     *         An input stream to read CBOR data items from.
     */
    public CBORTokenizer(InputStream inputStream)
    {
        if (inputStream == null)
        {
            throw new NullPointerException(
                    "The input stream given to the constructor is null.");
        }

        this.inputStream = inputStream;
    }


    /**
     * Get the number of bytes read from the input stream so far.
     *
     * @return
     *         The number of bytes from the input stream so far.
     */
    public int getReadCount()
    {
        return readCount;
    }


    /**
     * Get the next CBOR token from the input stream.
     *
     * @return
     *         A CBOR token. When no more CBOR token is available,
     *         {@code null} is returned.
     *
     * @throws IOException
     */
    public CBORToken<?> next() throws IOException
    {
        // Read the first byte of a CBOR token.
        int firstByte = read();

        // If the end of the input stream has been reached.
        if (firstByte < 0)
        {
            // No more CBOR tokens.
            return null;
        }

        // The first byte consists of a major type and additional information.
        //
        //   * The range of the major type is from 0 to 7.
        //   * The range of the additional information is from 0 to 31.
        //
        int major = (0x000000E0 & firstByte) >> 5;
        int info  = (0x0000001F & firstByte);

        // Dispatch according to the major type.
        switch (major)
        {
            case 0:
                // Unsigned integer
                return nextUnsignedInteger(major, info);

            case 1:
                // Negative integer
                return nextNegativeInteger(major, info);

            case 2:
                // Byte string
                return nextByteString(major, info);

            case 3:
                // Text string
                return nextTextString(major, info);

            case 4:
                // Array
                return nextArray(major, info);

            case 5:
                // Map
                return nextMap(major, info);

            case 6:
                // Tag
                return nextTag(major, info);

            case 7:
                // Floating-point numbers and simple values
                return nextMajor7(major, info);

            default:
                // This never happens.
                return null;
        }
    }


    private int read() throws IOException
    {
        // Read one byte from the input stream.
        int value = inputStream.read();

        // If the end of the stream has not been reached.
        if (0 <= value)
        {
            // Succeeded in reading one byte.
            readCount++;
        }

        // The value from the byte read from the input stream, or
        // -1 if the end of the input stream has been reached.
        return value;
    }


    private byte[] readBytes(int major, int info, int length) throws IOException
    {
        // The buffer into which the data is read.
        byte[] buf;

        try
        {
            buf = new byte[length];
        }
        catch (OutOfMemoryError cause)
        {
            // The length is too long.
            throw new CBORTooLongException(major, info, readCount, length);
        }

        // The start offset in the buffer at which the data is written.
        int off = 0;

        // The maximum number of bytes to read.
        int len = length;

        while (0 < len)
        {
            // Read some bytes from the input stream.
            int count = inputStream.read(buf, off, len);

            // If the end of the input stream has been reached.
            if (count < 0)
            {
                // Failed to read the designated number of bytes.
                return null;
            }

            off += count;
            len -= count;

            // Increment the total number of bytes read from the input stream.
            readCount += count;
        }

        return buf;
    }


    private Number readNumber(int major, int info) throws IOException
    {
        // info: 31
        // major: byte string, text string, array, or map
        if (info == 31 && (2 <= major && major <= 5))
        {
            // Indefinite.
            return null;
        }

        // info: from 28 to 31
        if (28 <= info)
        {
            // The additional information is invalid in this context.
            throw new CBORInvalidInfoException(major, info, readCount - 1);
        }

        // info: 0 to 23
        if (info < 24)
        {
            // The additional information is an immediate value.
            return Long.valueOf(info);
        }

        // info: from 24 to 27

        // The number of bytes to read.
        //
        //   info == 24   -->   1 bytes
        //   info == 25   -->   2 bytes
        //   info == 26   -->   4 bytes
        //   info == 27   -->   8 bytes
        //
        int length = pow(2, (info - 24));

        // Read some bytes that represent a number.
        byte[] buffer = readBytes(major, info, length);

        // If the necessary number of bytes were not read.
        if (buffer == null)
        {
            // The necessary number of bytes are not available.
            throw new CBORInsufficientDataException(major, info, readCount, length);
        }

        // Parse the content in the buffer as a number.
        return parseNumber(buffer);
    }


    private static Number parseNumber(byte[] buffer)
    {
        switch (buffer.length)
        {
            case 1:
                // Long
                return Long.valueOf(
                    ((buffer[0] & 0xFFL)      ));

            case 2:
                // Long
                return Long.valueOf(
                    ((buffer[0] & 0xFFL) <<  8) |
                    ((buffer[1] & 0xFFL)      ));

            case 4:
                // Long
                return Long.valueOf(
                    ((buffer[0] & 0xFFL) << 24) |
                    ((buffer[1] & 0xFFL) << 16) |
                    ((buffer[2] & 0xFFL) <<  8) |
                    ((buffer[3] & 0xFFL)      ));

            case 8:
                break;

            default:
                // This should never happen.
                throw new AssertionError(String.format(
                        "Unexpected length '%d' in parseNumber()", buffer.length));
        }

        // A special case where bytes in the buffer are all zero.
        // This case is handled before BigInteger(int, byte[]) is called.
        if (allZero(buffer))
        {
            // Long
            return Long.valueOf(0L);
        }

        // Parse the content of the buffer as a positive integer.
        BigInteger value = new BigInteger(1 /* meaning 'positive' */, buffer);

        // If the value represented by the BigInteger instance can be
        // represented by Java 'long' without losing information.
        if (value.compareTo(BIG_INTEGER_LONG_MAX) <= 0)
        {
            // Long
            return Long.valueOf(value.longValue());
        }

        // BigInteger
        return value;
    }


    private static float parseHalf(byte[] buffer)
    {
        // sign (1 bit)
        boolean positive = (buffer[0] & 0x80) == 0;

        // exponent (5 bits)
        int exponent = (buffer[0] & 0x7C) >> 2;

        // fraction (10 bits)
        int fraction = ((buffer[0] & 0x03) << 8) |
                       ((buffer[1] & 0xFF)     ) ;

        // Handle special cases.
        if (exponent == 0)
        {
            if (fraction == 0)
            {
                return positive ? 0.0F : -0.0F;
            }
        }
        else if (exponent == 0x1F)
        {
            if (fraction == 0)
            {
                return positive ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
            }
            else
            {
                return Float.NaN;
            }
        }

        // Bits that represent a single-precision floating point.
        int bits = 0;

        // sign (1 bit)
        if (!positive)
        {
            bits |= 0x8000_0000;
        }

        // In the case where the original half-precision floating-point number
        // is a subnormal number.
        if (exponent == 0)
        {
            // After the original number is converted to a single-precision
            // floating-point number, it is not a "subnormal" number anymore.
            // As a result, an implicit leading bit with value 1 is added to
            // the fraction. That is, the fraction is treated as if the value
            // were (fraction |= 0x400).

            // Shift the fraction until its leftmost bit becomes 1.
            while ((0x200 & fraction) == 0)
            {
                fraction <<= 1;
                exponent--;
            }

            // Shift once more and drop the leftmost bit. The bit will be
            // implicitly complemented because the resultant single-precision
            // number will not be a subnormal number.
            fraction <<= 1;
            fraction &= 0x3FF;
        }

        // exponent (8 bits)
        //
        // "112" here is the difference between the exponent bias of the half
        // precision (-15) and the exponent bias of the single precision (-127).
        bits |= (exponent + 112) << 23;

        // fraction (23 bits)
        //
        // "13" here is the difference between the size of the fraction part of
        // the half precision (10) and the size of the fraction part of the
        // single precision (23).
        bits |= fraction << 13;

        return Float.intBitsToFloat(bits);
    }


    private static float parseSingle(byte[] buffer)
    {
        // IEEE 754 Single-Precision Float
        return Float.intBitsToFloat(
                ((buffer[0] & 0xFF) << 24) |
                ((buffer[1] & 0xFF) << 16) |
                ((buffer[2] & 0xFF) <<  8) |
                ((buffer[3] & 0xFF)      ) );
    }


    private static double parseDouble(byte[] buffer)
    {
        // IEEE 754 Double-Precision Float
        return Double.longBitsToDouble(
                ((buffer[0] & 0xFFL) << 56) |
                ((buffer[1] & 0xFFL) << 48) |
                ((buffer[2] & 0xFFL) << 40) |
                ((buffer[3] & 0xFFL) << 32) |
                ((buffer[4] & 0xFFL) << 24) |
                ((buffer[5] & 0xFFL) << 16) |
                ((buffer[6] & 0xFFL) <<  8) |
                ((buffer[7] & 0xFFL)      ) );
    }


    private static int pow(int base, int exponent)
    {
        if (exponent == 0)
        {
            return 1;
        }

        int value = base;

        for (int i = 1; i < exponent; i++)
        {
            value *= base;
        }

        return value;
    }


    private static boolean allZero(byte[] buffer)
    {
        for (int i = 0; i < buffer.length; i++)
        {
            if (buffer[i] != 0)
            {
                return false;
            }
        }

        return true;
    }


    private byte[] readLengthAndBytes(int major, int info) throws IOException
    {
        // Get the length of the data.
        Number length = readNumber(major, info);

        if (length == null)
        {
            // Indefinite.
            return null;
        }

        if (length instanceof BigInteger)
        {
            // If 'length' is an instance of BigInteger, it means that the
            // length is greater than Long.MAX_VALUE. Too long.
            throw new CBORTooLongException(major, info, readCount, length);
        }

        if (Integer.MAX_VALUE < length.longValue())
        {
            // Greater than Integer.MAX_VALUE. Too long.
            throw new CBORTooLongException(major, info, readCount, length);
        }

        int len = length.intValue();

        // Read some bytes that represent a number.
        byte[] bytes = readBytes(major, info, len);

        // If the necessary number of bytes were not read.
        if (bytes == null)
        {
            // The necessary number of bytes are not available.
            throw new CBORInsufficientDataException(major, info, readCount, len);
        }

        // The bytes read from the input stream.
        return bytes;
    }


    private String buildUtf8String(
            int major, int info, byte[] bytes) throws CBORMalformedUtf8Exception
    {
        try
        {
            ByteBuffer byteBuffer  = ByteBuffer.wrap(bytes);
            CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();

            // Parse the given byte sequence as a UTF-8 string.
            return decoder.decode(byteBuffer).toString();
        }
        catch (Exception cause)
        {
            // The offset at which the malformed byte sequence starts.
            int offset = readCount - bytes.length;

            // Malformed UTF-8 byte sequence.
            throw new CBORMalformedUtf8Exception(major, info, offset, cause);
        }
    }


    private CTNumber<?> nextUnsignedInteger(int major, int info) throws IOException
    {
        // Get the value that represents an unsigned integer.
        Number value = readNumber(major, info);

        if (value instanceof BigInteger)
        {
            // Unsigned big integer
            return new CTUnsignedBigInteger(major, info, (BigInteger)value);
        }
        else
        {
            // Unsigned integer
            return new CTUnsignedInteger(major, info, (Long)value);
        }
    }


    private CTNumber<?> nextNegativeInteger(int major, int info) throws IOException
    {
        // Get the raw value that represents a positive integer.
        Number rawValue = readNumber(major, info);

        if (rawValue instanceof BigInteger)
        {
            // Negate the raw value and minus one.
            BigInteger value = BIG_INTEGER_MINUS_ONE.subtract((BigInteger)rawValue);

            // Negative big integer
            return new CTNegativeBigInteger(major, info, value);
        }
        else
        {
            // Negate the raw value and minus one.
            long value = -1L - rawValue.longValue();

            // Negative integer
            return new CTNegativeInteger(major, info, value);
        }
    }


    private CBORToken<?> nextByteString(int major, int info) throws IOException
    {
        // Read the length of the byte string and then read the content.
        byte[] bytes = readLengthAndBytes(major, info);

        if (bytes == null)
        {
            // The beginning of an indefinite-length byte string.
            return CTIndefiniteByteStringOpener.INSTANCE;
        }

        // Byte string
        return new CTByteString(major, info, bytes);
    }


    private CBORToken<?> nextTextString(int major, int info) throws IOException
    {
        // Read the length of the byte string and then read the content.
        byte[] bytes = readLengthAndBytes(major, info);

        if (bytes == null)
        {
            // The beginning of an indefinite-length text string.
            return CTIndefiniteTextStringOpener.INSTANCE;
        }

        // Convert the byte sequence to a UTF-8 string.
        String str = buildUtf8String(major, info, bytes);

        // Text string
        return new CTTextString(major, info, str);
    }


    private CBORToken<?> nextArray(int major, int info) throws IOException
    {
        // Get the number of elements in the array.
        Number size = readNumber(major, info);

        if (size == null)
        {
            // The beginning of an indefinite-length array.
            return CTIndefiniteArrayOpener.INSTANCE;
        }

        // Array opener
        return new CTArrayOpener(major, info, size);
    }


    private CBORToken<?> nextMap(int major, int info) throws IOException
    {
        // Get the number of entries in the map.
        Number size = readNumber(major, info);

        if (size == null)
        {
            // The beginning of an indefinite-length map.
            return CTIndefiniteMapOpener.INSTANCE;
        }

        // Map opener
        return new CTMapOpener(major, info, size);
    }


    private CBORToken<?> nextTag(int major, int info) throws IOException
    {
        // Get the tag number.
        Number number = readNumber(major, info);

        // Tag
        return new CTTag(major, info, number);
    }


    private CBORToken<?> nextMajor7(int major, int info) throws IOException
    {
        // info: from 0 to 19
        if (info <= 19)
        {
            // Simple value. The additional information is an immediate value.
            return new CTSimpleValue(major, info, info);
        }

        switch (info)
        {
            case 20:
                // false
                return CTFalse.INSTANCE;

            case 21:
                // true
                return CTTrue.INSTANCE;

            case 22:
                // null
                return CTNull.INSTANCE;

            case 23:
                // undefined
                return CTUndefined.INSTANCE;

            case 24:
                // Simple value from 32 to 255
                return nextSimpleValue(major, info);

            case 25:
            case 26:
            case 27:
                // Floating-point numbers
                return nextFloatPoint(major, info);

            case 28:
            case 29:
            case 30:
                // Reserved, not well-formed.
                throw new CBORInvalidInfoException(major, info, readCount - 1);

            case 31:
                // break
                return CTBreak.INSTANCE;

            default:
                // This never happens.
                return null;
        }
    }


    private CTSimpleValue nextSimpleValue(int major, int info) throws IOException
    {
        // Read one byte.
        int value = read();

        // If the end of the input stream has been reached.
        if (value < 0)
        {
            throw new CBORInsufficientDataException(major, info, readCount, 1);
        }

        if (value < 32)
        {
            // RFC 8949, 3.3. Floating-Point Numbers and Values with No Content
            //
            //   An encoder MUST NOT issue two-byte sequences that start with
            //   0xf8 (major type 7, additional information 24) and continue
            //   with a byte less than 0x20 (32 decimal).
            //
            throw new CBORInvalidSimpleValueException(major, info, readCount - 1, value);
        }

        return new CTSimpleValue(major, info, value);
    }


    private CBORToken<?> nextFloatPoint(int major, int info) throws IOException
    {
        // info: from 24 to 27

        // The number of bytes to read.
        //
        //   info == 25   -->   2 bytes
        //   info == 26   -->   4 bytes
        //   info == 27   -->   8 bytes
        //
        int length = pow(2, (info - 24));

        // Read some bytes that represent a number.
        byte[] buffer = readBytes(major, info, length);

        // If the necessary number of bytes were not read.
        if (buffer == null)
        {
            // The necessary number of bytes are not available.
            throw new CBORInsufficientDataException(major, info, readCount, length);
        }

        switch (length)
        {
            case 2:
                // IEEE 754 Half-Precision Float
                return new CTHalfFloatingPoint(major, info, parseHalf(buffer));

            case 4:
                // IEEE 754 Single-Precision Float
                return new CTSingleFloatingPoint(major, info, parseSingle(buffer));

            case 8:
                // IEEE 754 Double-Precision Float
                return new CTDoubleFloatingPoint(major, info, parseDouble(buffer));

            default:
                // This should never happen.
                throw new AssertionError(String.format(
                        "Unexpected length '%d' in nextFloatPoint()", buffer.length));
        }
    }
}

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


import static com.authlete.cbor.CBORConstants.BIG_INTEGER_LONG_MAX;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Base64;


/**
 * CBOR data item.
 */
public abstract class CBORItem
{
    /**
     * The comment attached to this CBOR item.
     */
    private String mComment;


    /**
     * Get the comment attached to this CBOR item.
     *
     * <p>
     * The attached comment may be used by the {@link #prettify()} method.
     * </p>
     *
     * @return
     *         The comment attached to this CBOR item.
     *
     * @since 1.5
     */
    public String getComment()
    {
        return mComment;
    }


    /**
     * Attach a comment to this CBOR item.
     *
     * <p>
     * The attached comment may be used by the {@link #prettify()} method.
     * </p>
     *
     * @param comment
     *         A comment to attach.
     *
     * @since 1.5
     */
    public void setComment(String comment)
    {
        mComment = comment;
    }


    /**
     * Convert this {@link CBORItem} instance into an instance of a common Java class.
     *
     * @return
     *         An instance of a common Java class that represents this CBOR data item.
     */
    public abstract Object parse();


    /**
     * Write the CBOR representation of this instance into the output stream.
     *
     * @param outputStream
     *         The output stream to write the CBOR representation into.
     *
     * @throws IOException
     */
    public abstract void encode(OutputStream outputStream) throws IOException;


    /**
     * Get the CBOR representation of this instance as a byte array.
     * The implementation of this method calls {@link #encode(OutputStream)}.
     *
     * @return
     *         The CBOR representation of this instance as a byte array.
     */
    public final byte[] encode()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try
        {
            // Let the subclass encode itself.
            encode(baos);
        }
        catch (IOException cause)
        {
            // This never happens.
            return null;
        }

        return baos.toByteArray();
    }


    /**
     * Get the CBOR representation of this instance in the base64 format.
     *
     * @return
     *         The CBOR representation of this instance in the base64 format.
     *
     * @since 1.5
     */
    public String encodeToBase64()
    {
        return Base64.getEncoder().encodeToString(encode());
    }


    /**
     * Get the CBOR representation of this instance in the base64url format.
     *
     * @return
     *         The CBOR representation of this instance in the base64url format.
     *
     * @since 1.5
     */
    public String encodeToBase64Url()
    {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(encode());
    }


    /**
     * Get the CBOR representation of this instance in the hexadecimal format.
     *
     * @return
     *         The CBOR representation of this instance in the hexadecimal format.
     *
     * @since 1.5
     */
    public String encodeToHex()
    {
        StringBuilder sb = new StringBuilder();

        for (byte b : encode())
        {
            sb.append(toHex(b));
        }

        return sb.toString();
    }


    private static String toHex(int b)
    {
        return String.format("%02x", (0xFF & b));
    }


    /**
     * Get the string representation of this CBOR item. The default
     * implementation of this method calls the {@code toString()} method.
     *
     * <p>
     * This method is called by the implementation of the
     * {@link CBORTaggedItem} class.
     * </p>
     *
     * @param tagNumber
     *         The tag number prepended to this CBOR item.
     *
     * @return
     *         The string representation of this CBOR item.
     *
     * @since 1.8
     */
    protected String toString(Number tagNumber)
    {
        return toString();
    }


    /**
     * Stringify this CBOR item and all the nested CBOR items in the pretty
     * format.
     *
     * @return
     *         The string expression of this CBOR item and all the nested
     *         CBOR items.
     *
     * @since 1.5
     */
    public String prettify()
    {
        return prettify("", "  ", null);
    }


    /**
     * Stringify this CBOR item and all the nested CBOR items in the pretty
     * format.
     *
     * <p>
     * Subclasses should override this method as necessary.
     * </p>
     *
     * @param indent
     *         The indent inherited from the upper CBOR item.
     *
     * @param indentUnit
     *         Additional indent that should be added when nested CBOR items
     *         are stringified.
     *
     * @param tagNumber
     *         The tag number of the tag wrapping this CBOR item. If this CBOR
     *         item is not wrapped, {@code null} is passed.
     *
     * @return
     *         The string expression of this CBOR item and all the nested
     *         CBOR items.
     *
     * @since 1.7
     */
    protected String prettify(String indent, String indentUnit, Number tagNumber)
    {
        String comment = getComment();
        String content = toString();

        if (comment != null)
        {
            return String.format("/ %s / %s", comment, content);
        }
        else
        {
            return content;
        }
    }


    /**
     * Write the CBOR representation of the specified major type and the number
     * into the output stream. The value of the additional information is
     * determined according to the actual value of the given number.
     *
     * <p>
     * The implementation of this method assumes the following.
     * </p>
     *
     * <ol>
     * <li>The given number is 0 or positive.
     * <li>The given number is an instance of {@code Integer}, {@code Long} or
     *     {@code BigInteger}.
     * <li>Even when the given number is an instance of {@code BigInteger}, its
     *     value does not exceed 18446744073709551615 (0xFFFF_FFFF_FFFF_FFFF).
     * </ol>
     *
     * @param outputStream
     *         The output stream to write the CBOR representation into.
     *
     * @param major
     *         The major type (from 0 to 7).
     *
     * @param number
     *         The number to write.
     *
     * @throws IOException
     *
     * @see <a href="https://www.rfc-editor.org/rfc/rfc8949.html#name-specification-of-the-cbor-e"
     *      >RFC 8949, 3. Specification of the CBOR Encoding</a>
     */
    protected void encodeMajorWithNumber(
            OutputStream outputStream, int major, Number number) throws IOException
    {
        // The major type occupies the leftmost 3 bits.
        int shiftedMajor = (major << 5);

        if (number instanceof Integer)
        {
            // Integer
            encodeMajorWithInteger(outputStream, shiftedMajor, number.intValue());
        }
        else if (number instanceof Long)
        {
            // Long
            encodeMajorWithLong(outputStream, shiftedMajor, number.longValue());
        }
        else if (number instanceof BigInteger)
        {
            // BigInteger
            encodeMajorWithBigInteger(outputStream, shiftedMajor, (BigInteger)number);
        }
        else
        {
            throw new AssertionError(String.format(
                    "The number given to encodeMajorWithNumber() is unexpected: %s",
                    number == null ? "null" : number.getClass().getSimpleName()));
        }
    }


    private void encodeMajorWithInteger(
            OutputStream outputStream, int shiftedMajor, int value) throws IOException
    {
        if (value < 24)
        {
            // Only 1 byte.
            outputStream.write(shiftedMajor | value);
        }
        else if (value <= 0xFF)
        {
            // Major and following 1 byte
            encodeMajorWith8Bits(outputStream, shiftedMajor, value);
        }
        else if (value <= 0xFFFF)
        {
            // Major and following 2 bytes
            encodeMajorWith16Bits(outputStream, shiftedMajor, value);
        }
        else
        {
            // Major and following 4 bytes
            encodeMajorWith32Bits(outputStream, shiftedMajor, value);
        }
    }


    private void encodeMajorWithLong(
            OutputStream outputStream, int shiftedMajor, long value) throws IOException
    {
        if (value <= Integer.MAX_VALUE)
        {
            // Integer
            encodeMajorWithInteger(outputStream, shiftedMajor, (int)value);
        }
        else if (value <= 0xFFFF_FFFF)
        {
            // Major and following 4 bytes
            encodeMajorWith32Bits(outputStream, shiftedMajor, (int)value);
        }
        else
        {
            // Major and following 8 bytes
            encodeMajorWith64Bits(outputStream, shiftedMajor, value);
        }
    }


    private void encodeMajorWithBigInteger(
            OutputStream outputStream, int shiftedMajor, BigInteger number) throws IOException
    {
        // 64 bits at most.
        long value = number.longValue();

        if (number.compareTo(BIG_INTEGER_LONG_MAX) <= 0)
        {
            // Long
            encodeMajorWithLong(outputStream, shiftedMajor, value);
        }
        else
        {
            // Major and following 8 bytes
            encodeMajorWith64Bits(outputStream, shiftedMajor, value);
        }
    }


    private void encodeMajorWith8Bits(
            OutputStream outputStream, int shiftedMajor, int value) throws IOException
    {
        outputStream.write(shiftedMajor | 24);
        outputStream.write(value);
    }


    private void encodeMajorWith16Bits(
            OutputStream outputStream, int shiftedMajor, int value) throws IOException
    {
        outputStream.write(shiftedMajor | 25);
        outputStream.write(value >> 8);
        outputStream.write(value     );
    }


    private void encodeMajorWith32Bits(
            OutputStream outputStream, int shiftedMajor, int value) throws IOException
    {
        outputStream.write(shiftedMajor | 26);
        outputStream.write(value >> 24);
        outputStream.write(value >> 16);
        outputStream.write(value >>  8);
        outputStream.write(value      );
    }


    private void encodeMajorWith64Bits(
            OutputStream outputStream, int shiftedMajor, long value) throws IOException
    {
        outputStream.write(shiftedMajor | 27);
        outputStream.write((int)(value >> 56));
        outputStream.write((int)(value >> 48));
        outputStream.write((int)(value >> 40));
        outputStream.write((int)(value >> 32));
        outputStream.write((int)(value >> 24));
        outputStream.write((int)(value >> 16));
        outputStream.write((int)(value >>  8));
        outputStream.write((int)(value      ));
    }
}

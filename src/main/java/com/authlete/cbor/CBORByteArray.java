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


import java.io.IOException;
import java.io.OutputStream;


/**
 * CBOR byte array (major type = 2).
 *
 * <p>
 * The {@link #getValue()} method of this class returns an instance of
 * byte array ({@code byte[]}).
 * </p>
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8949#name-major-types"
 *      >RFC 8949, 3.1. Major Types</a>
 */
public class CBORByteArray extends CBORValue<byte[]>
{
    /**
     * Decoder options without tag processors.
     */
    private static final CBORDecoderOptions DECODER_OPTIONS = new CBORDecoderOptions();


    /**
     * Whether the value can be decoded as a CBOR data item.
     */
    private final boolean decodable;


    /**
     * A constructor with the value.
     *
     * @param value
     *         The value of this byte string. Must not be null.
     */
    public CBORByteArray(byte[] value)
    {
        this(value, false);
    }


    /**
     * A constructor with the value and a flag indicating whether the value
     * can be decoded as a CBOR data item or not.
     *
     * @param value
     *         The value of this byte string. Must not be null.
     *
     * @param decodable
     *         {@code true} to indicate that the value can be decoded as a
     *         CBOR data item.
     *
     * @since 1.1
     */
    public CBORByteArray(byte[] value, boolean decodable)
    {
        super(value);

        this.decodable = decodable;
    }


    /**
     * Return <code>h'<i>{base64}</i>'</code>, or
     * {@code <<}<i>{CBOR data item}</i>{@code >>} if the value can be
     * decoded as a CBOR data item.
     *
     * @see <a href="https://www.rfc-editor.org/rfc/rfc8949#name-diagnostic-notation"
     *      >RFC 8949, 8. Diagnostic Notation</a>
     */
    @Override
    public String toString()
    {
        return toString(decodable);
    }


    /**
     * Get the string representation of this byte string.
     *
     * @param decode
     *         {@code true} to try to decode the content as a CBOR data item.
     *         {@code false} to try not to decode the content.
     *
     * @return
     *         The string representation of this byte string.
     *
     * @since 1.1
     */
    public String toString(boolean decode)
    {
        if (decode)
        {
            // Return the representation of the content enclosed with "<<" and ">>".
            return buildDecodedString();
        }
        else
        {
            // Return the hex representation enclosed with "h'" and "'"
            return buildString();
        }
    }


    private String buildDecodedString()
    {
        try
        {
            // Decode the content of this byte string as a CBOR data item.
            CBORItem item = decodeValue();

            // If the content of this byte string is empty.
            if (item == null)
            {
                // Return the hex representation.
                return "h''";
            }

            // Return the representation of the content enclosed with "<<" and ">>".
            return String.format("<<%s>>", item.toString());
        }
        catch (CBORDecoderException cause)
        {
            // The content of this byte string failed to be decoded as
            // a CBOR data item. Return the hex representation.
            return buildString();
        }
    }


    private String buildString()
    {
        StringBuilder sb = new StringBuilder("h'");

        for (byte b : getValue())
        {
            sb.append(toHex(b));
        }

        sb.append("'");

        return sb.toString();
    }


    private static String toHex(int b)
    {
        return String.format("%02x", (0xFF & b));
    }


    @Override
    public void encode(OutputStream outputStream) throws IOException
    {
        byte[] value = getValue();

        encodeMajorWithNumber(outputStream, 2 /* major */, value.length);

        outputStream.write(value);
    }


    /**
     * Decode the content of this byte string as a CBOR data item.
     *
     * @return
     *         A CBOR data item. If the value of this byte string is null or
     *         an empty byte array, {@code null} is returned.
     *
     * @throws CBORDecoderException
     *         The content failed to be parsed as a CBOR data item.
     *
     * @since 1.1
     */
    public CBORItem decodeValue() throws CBORDecoderException
    {
        // The content of this byte string.
        byte[] value = getValue();

        if (value == null || value.length == 0)
        {
            return null;
        }

        // Create a decoder without tag processors.
        CBORDecoder decoder = new CBORDecoder(value, DECODER_OPTIONS);

        try
        {
            // Decode the content as a CBOR data item.
            return decoder.next();
        }
        catch (CBORDecoderException cause)
        {
            // The content failed to be decoded as a CBOR data item.
            throw cause;
        }
        catch (IOException cause)
        {
            // The next() method of CBORDecoder does not throw IOException
            // when the instance was created with a byte array (not with
            // an input stream).
            throw new CBORDecoderException(cause);
        }
    }
}

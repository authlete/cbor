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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


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
     * Tag 24; Encoded CBOR data item.
     *
     * <p>
     * From <a href="https://www.rfc-editor.org/rfc/rfc8949#section-3.4.5.1"
     * >RFC 8949, 3.4.5.1. Encoded CBOR Data Item</a>:
     * </p>
     *
     * <p><i>
     * Sometimes it is beneficial to carry an embedded CBOR data item that is
     * not meant to be decoded immediately at the time the enclosing data item
     * is being decoded. Tag number 24 (CBOR data item) can be used to tag the
     * embedded byte string as a single data item encoded in CBOR format.
     * Contained items that aren't byte strings are invalid. A contained byte
     * string is valid if it encodes a well-formed CBOR data item; validity
     * checking of the decoded CBOR item is not required for tag validity (but
     * could be offered by a generic decoder as a special option).
     * </i></p>
     * </blockquote>
     */
    private static final int TAG_ENCODED_CBOR_DATA_ITEM = 24;


    /**
     * Decoder options without tag processors.
     */
    private static final CBORDecoderOptions DECODER_OPTIONS = new CBORDecoderOptions();


    /**
     * Decoded content.
     */
    private final List<? extends CBORItem> decodedContent;


    /**
     * A constructor with the value.
     *
     * @param value
     *         The value of this byte string. Must not be null.
     */
    public CBORByteArray(byte[] value)
    {
        this(value, /* decodedContent */ (CBORItem)null);
    }


    /**
     * A constructor with the value and the CBOR item represented by
     * the byte array.
     *
     * @param value
     *         The value of this byte string. Must not be null.
     *
     * @param decodedContent
     *         The CBOR item represented by the byte array.
     *
     * @since 1.5
     */
    public CBORByteArray(byte[] value, CBORItem decodedContent)
    {
        this(value, (decodedContent != null) ? Arrays.asList(decodedContent) : null);
    }


    /**
     * A constructor with the value and the list of CBOR items represented by
     * the byte array.
     *
     * @param value
     *         The value of this byte string. Must not be null.
     *
     * @param decodedContent
     *         The CBOR item represented by the byte array.
     *
     * @since 1.5
     */
    public CBORByteArray(byte[] value, List<? extends CBORItem> decodedContent)
    {
        super(value);

        this.decodedContent = decodedContent;
    }


    /**
     * Get the list of CBOR items represented by this byte string.
     *
     * @return
     *         The list of CBOR items represented by this byte string.
     *
     * @since 1.5
     */
    public List<? extends CBORItem> getDecodedContent()
    {
        return decodedContent;
    }


    /**
     * Return <code>h'<i>{base64}</i>'</code>, or
     * {@code <<}<i>{CBOR data item(s)}</i>{@code >>} if the value can be
     * decoded as a CBOR data item or CBOR data items.
     *
     * @see <a href="https://www.rfc-editor.org/rfc/rfc8949#name-diagnostic-notation"
     *      >RFC 8949, 8. Diagnostic Notation</a>
     */
    @Override
    public String toString()
    {
        return toString(decodedContent != null);
    }


    @Override
    protected String toString(Number tagNumber)
    {
        if (isEncodedCborDataItem(tagNumber))
        {
            return toString(true);
        }
        else
        {
            return toString(false);
        }
    }


    private static boolean isEncodedCborDataItem(Number tagNumber)
    {
        if (tagNumber == null)
        {
            return false;
        }

        return tagNumber.intValue() == TAG_ENCODED_CBOR_DATA_ITEM;
    }


    /**
     * Get the string representation of this byte string.
     *
     * @param decode
     *         {@code true} to try to decode the content as CBOR data item(s).
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
        List<? extends CBORItem> items = prepareDecodedContent();

        if (items != null)
        {
            // Return the representation of the content enclosed with "<<" and ">>".
            return items.stream().map(CBORItem::toString)
                    .collect(Collectors.joining(", ", "<<", ">>"));
        }
        else
        {
            // Return the hex representation.
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
    protected String prettify(String indent, String indentUnit, Number tagNumber)
    {
        // The comment attached to this CBOR item.
        String comment = (getComment() == null) ? ""
                : String.format("/ %s / ", getComment());

        if (isEncodedCborDataItem(tagNumber) || decodedContent != null)
        {
            return prettifyDecodedString(indent, indentUnit, comment);
        }
        else
        {
            return prettifyString(comment);
        }
    }


    private String prettifyDecodedString(
            String indent, String indentUnit, String comment)
    {
        List<? extends CBORItem> items = prepareDecodedContent();

        // If decoding the value as CBOR data failed.
        if (items == null && getValue() != null)
        {
            return prettifyString(comment);
        }

        if (items == null || items.size() == 0)
        {
            return String.format("%s<<%n%s>>", comment, indent);
        }

        String delimiter = String.format(",%n");
        String prefix    = String.format("%s<<%n", comment);
        String suffix    = String.format("%n%s>>", indent);

        // The indent for each item.
        final String subIndent = indent + indentUnit;

        return items.stream()
                .map(item -> String.format("%s%s", subIndent,
                        item.prettify(subIndent, indentUnit, null)))
                .collect(Collectors.joining(delimiter, prefix, suffix));
    }


    private List<? extends CBORItem> prepareDecodedContent()
    {
        if (decodedContent != null)
        {
            return decodedContent;
        }

        if (getValue() == null)
        {
            return null;
        }

        try
        {
            // Try to decode the value of this byte string as CBOR data.
            return new CBORDecoder(getValue(), DECODER_OPTIONS).all();
        }
        catch (IOException cause)
        {
            // Failed to decode the value of this byte string as CBOR data.
            return null;
        }
    }


    private String prettifyString(String comment)
    {
        return String.format("%s%s", comment, toString());
    }


    @Override
    public void encode(OutputStream outputStream) throws IOException
    {
        byte[] value = getValue();

        encodeMajorWithNumber(outputStream, 2 /* major */, value.length);

        outputStream.write(value);
    }
}

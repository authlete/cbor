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


import static com.authlete.cbor.CBORConstants.LONG_INT_MAX;
import static com.authlete.cbor.CBORConstants.LONG_INT_MIN;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import com.authlete.cbor.tag.CBORTagProcessor;
import com.authlete.cbor.tag.CPDefault;
import com.authlete.cbor.token.CBORToken;
import com.authlete.cbor.token.CBORTokenizer;
import com.authlete.cbor.token.CTArrayOpener;
import com.authlete.cbor.token.CTBigInteger;
import com.authlete.cbor.token.CTBreak;
import com.authlete.cbor.token.CTByteString;
import com.authlete.cbor.token.CTDoubleFloatingPoint;
import com.authlete.cbor.token.CTFalse;
import com.authlete.cbor.token.CTFloatingPoint;
import com.authlete.cbor.token.CTIndefiniteArrayOpener;
import com.authlete.cbor.token.CTIndefiniteByteStringOpener;
import com.authlete.cbor.token.CTIndefiniteMapOpener;
import com.authlete.cbor.token.CTIndefiniteTextStringOpener;
import com.authlete.cbor.token.CTInteger;
import com.authlete.cbor.token.CTMapOpener;
import com.authlete.cbor.token.CTNull;
import com.authlete.cbor.token.CTSimpleValue;
import com.authlete.cbor.token.CTTag;
import com.authlete.cbor.token.CTTextString;
import com.authlete.cbor.token.CTTrue;
import com.authlete.cbor.token.CTUndefined;


/**
 * A decoder that receives CBOR tokens ({@link CBORToken}) from the CBOR
 * tokenizer ({@link CBORTokenizer}) and constructs CBOR data items
 * ({@link CBORItem}).
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8949"
 *      >RFC 8949 Concise Binary Object Representation (CBOR)</a>
 */
public class CBORDecoder
{
    private final CBORTokenizer tokenizer;
    private final CBORDecoderOptions options;


    /**
     * A constructor with a byte array that holds CBOR data items.
     *
     * <p>
     * This constructor is an alias of
     * {@link #CBORDecoder(byte[], CBORDecoderOptions)
     * CBORDecoder}{@code (input, null)}.
     * </p>
     *
     * @param input
     *         A byte array that holds CBOR data items.
     */
    public CBORDecoder(byte[] input)
    {
        this(input, null);
    }


    /**
     * A constructor with a byte array that holds CBOR data items and options
     * to control the behavior of this decoder.
     *
     * <p>
     * This constructor is an alias of
     * {@link #CBORDecoder(InputStream, CBORDecoderOptions)
     * CBORDecoder}{@code (new ByteArrayInputStream(input), options)}.
     * </p>
     *
     * @param input
     *         A byte array that holds CBOR data items.
     *
     * @param options
     *         Options to control the behavior of this decoder.
     */
    public CBORDecoder(byte[] input, CBORDecoderOptions options)
    {
        this(new ByteArrayInputStream(input), options);
    }


    /**
     * A constructor with a byte array that holds CBOR data items.
     *
     * <p>
     * This constructor is an alias of
     * {@link #CBORDecoder(byte[], int, int, CBORDecoderOptions)
     * CBORDecoder}{@code (input, offset, length, null)}.
     * </p>
     *
     * @param input
     *         A byte array that holds CBOR data items.
     *
     * @param offset
     *         The offset in the byte array of the first byte to read.
     *
     * @param length
     *         The maximum number of bytes to read from the byte array.
     */
    public CBORDecoder(byte[] input, int offset, int length)
    {
        this(input, offset, length, null);
    }


    /**
     * A constructor with a byte array that holds CBOR data items and options
     * to control the behavior of this decoder.
     *
     * <p>
     * This constructor is an alias of
     * {@link #CBORDecoder(InputStream, CBORDecoderOptions)
     * CBORDecoder}{@code (new ByteArrayInputStream(input, offset, length), options)}.
     * </p>
     *
     * @param input
     *         A byte array that holds CBOR data items.
     *
     * @param offset
     *         The offset in the byte array of the first byte to read.
     *
     * @param length
     *         The maximum number of bytes to read from the byte array.
     *
     * @param options
     *         Options to control the behavior of this decoder.
     */
    public CBORDecoder(byte[] input, int offset, int length, CBORDecoderOptions options)
    {
        this(new ByteArrayInputStream(input, offset, length), options);
    }


    /**
     * A constructor with an input stream to read CBOR data items from.
     *
     * <p>
     * This constructor is an alias of
     * {@link #CBORDecoder(InputStream, CBORDecoderOptions)
     * CBORDecoder}{@code (inputStream, null)}.
     * </p>
     *
     * @param inputStream
     *         An input stream to read CBOR data items from.
     */
    public CBORDecoder(InputStream inputStream)
    {
        this(inputStream, null);
    }


    /**
     * A constructor with an input stream to read CBOR data items from and
     * options to control the behavior of this decoder.
     *
     * @param inputStream
     *         An input stream to read CBOR data items from.
     *
     * @param options
     *         Options to control the behavior of this decoder. If {@code null}
     *         is given, an instance of {@link CBORDefaultDecoderOptions} is
     *         created and used.
     */
    public CBORDecoder(InputStream inputStream, CBORDecoderOptions options)
    {
        this.tokenizer = new CBORTokenizer(inputStream);
        this.options   = (options != null) ? options : new CBORDefaultDecoderOptions();
    }


    /**
     * Get the options for this decoder.
     *
     * @return
     *         The options for this decoder.
     */
    public CBORDecoderOptions getOptions()
    {
        return options;
    }


    /**
     * Get the next CBOR data item from the input stream.
     *
     * @return
     *         A CBOR data item. When no more CBOR data item is available,
     *         {@code null} is returned.
     *
     * @throws IOException
     */
    public CBORItem next() throws IOException
    {
        return nextItem();
    }


    /**
     * Read all the CBOR data items from the input stream.
     *
     * @return
     *         A list of CBOR data items read from the input stream.
     *
     * @throws IOException
     *
     * @since 1.1
     */
    public List<CBORItem> all() throws IOException
    {
        List<CBORItem> list = new ArrayList<>();

        while (true)
        {
            // Get the next CBOR data item from the input stream.
            CBORItem item = next();

            // If the end of the input stream has been reached.
            if (item == null)
            {
                break;
            }

            list.add(item);
        }

        return list;
    }


    private CBORToken<?> nextToken() throws IOException
    {
        // Get the next CBOR token.
        return tokenizer.next();
    }


    private CBORItem nextItem() throws IOException
    {
        // Get the next CBOR token.
        CBORToken<?> token = nextToken();

        // If no more CBOR token is available.
        if (token == null)
        {
            // No more CBOR data item is available.
            return null;
        }

        // Integers that can be represented by Java 'long'.
        if (token instanceof CTInteger)
        {
            // Integer or Long
            return buildFromInteger((CTInteger)token);
        }

        // Integers that need BigInteger for representation.
        if (token instanceof CTBigInteger)
        {
            // BigInteger
            return new CBORBigInteger((BigInteger)token.getValue());
        }

        // Definite-length byte string
        if (token instanceof CTByteString)
        {
            // byte[]
            return new CBORByteArray((byte[])token.getValue());
        }

        // Indefinite-length byte string
        if (token instanceof CTIndefiniteByteStringOpener)
        {
            // byte[]
            return nextIndefiniteByteStringSequence();
        }

        // Definite-length text string
        if (token instanceof CTTextString)
        {
            // String
            return new CBORString((String)token.getValue());
        }

        // Indefinite-length text string
        if (token instanceof CTIndefiniteTextStringOpener)
        {
            // String
            return nextIndefiniteTextStringSequence();
        }

        // Definite-length array
        if (token instanceof CTArrayOpener)
        {
            // CBORItemList
            return nextArray(((CTArrayOpener)token).getSize());
        }

        // Indefinite-length array
        if (token instanceof CTIndefiniteArrayOpener)
        {
            // CBORItemList
            return nextIndefiniteArray();
        }

        // Definite-length map
        if (token instanceof CTMapOpener)
        {
            // CBORPairList
            return nextMap(((CTMapOpener)token).getSize());
        }

        // Indefinite-length map
        if (token instanceof CTIndefiniteMapOpener)
        {
            // CBORPairList
            return nextIndefiniteMap();
        }

        // Tag
        if (token instanceof CTTag)
        {
            return nextTag((CTTag)token);
        }

        // Simple value
        if (token instanceof CTSimpleValue)
        {
            // Simple value
            return new CBORSimpleValue((Integer)token.getValue());
        }

        // false
        if (token instanceof CTFalse)
        {
            return CBORBoolean.FALSE;
        }

        // true
        if (token instanceof CTTrue)
        {
            return CBORBoolean.TRUE;
        }

        // null
        if (token instanceof CTNull)
        {
            return CBORNull.INSTANCE;
        }

        // undefined
        if (token instanceof CTUndefined)
        {
            return CBORUndefined.INSTANCE;
        }

        // Floating-point number
        if (token instanceof CTFloatingPoint)
        {
            // Float or Double
            return buildFromFloatingPoint((CTFloatingPoint<?>)token);
        }

        // break
        if (token instanceof CTBreak)
        {
            return CBORBreak.INSTANCE;
        }

        // This should never happen.
        throw new AssertionError(String.format(
                "Unknown token '%s' in nextItem()", token.getClass().getSimpleName()));
    }


    private CBORItem buildFromInteger(CTInteger token)
    {
        Long value = token.getValue();

        // If the value is in the range of Java 'int'.
        if (LONG_INT_MIN.compareTo(value) <= 0 &&
            value.compareTo(LONG_INT_MAX) <= 0)
        {
            // Integer
            return new CBORInteger(value.intValue());
        }

        // Long
        return new CBORLong(value);
    }


    private CBORItem buildFromFloatingPoint(CTFloatingPoint<?> token)
    {
        if (token instanceof CTDoubleFloatingPoint)
        {
            // Double
            return new CBORDouble((Double)token.getValue());
        }
        else
        {
            // Float
            return new CBORFloat((Float)token.getValue());
        }
    }


    private CBORByteArray nextIndefiniteByteStringSequence() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        while (true)
        {
            // Get the next CBOR token.
            CBORToken<?> token = nextToken();

            // Byte string
            if (token instanceof CTByteString)
            {
                // Accumulate the bytes.
                baos.write((byte[])token.getValue());
            }
            // Break
            else if (token instanceof CTBreak)
            {
                // The end of the indefinite-length byte string sequence.
                break;
            }
            // The end of the input stream or an unexpected token.
            else
            {
                throw invalidToken("an indefinite-length byte string sequence", token);
            }
        }

        // byte[]
        return new CBORByteArray(baos.toByteArray());
    }


    private CBORString nextIndefiniteTextStringSequence() throws IOException
    {
        StringBuilder sb = new StringBuilder();

        while (true)
        {
            // Get the next CBOR token.
            CBORToken<?> token = nextToken();

            // Text string
            if (token instanceof CTTextString)
            {
                // Accumulate the string.
                sb.append((String)token.getValue());
            }
            // Break (OK)
            else if (token instanceof CTBreak)
            {
                // The end of the indefinite-length text string sequence.
                break;
            }
            // The end of the input stream of an unexpected token.
            else
            {
                throw invalidToken("an indefinite-length text string sequence", token);
            }
        }

        // String
        return new CBORString(sb.toString());
    }


    private CBORItemList nextArray(Number size) throws IOException
    {
        // The expected length of the array. The checkSize() method ensures
        // that the length is equal to or less than Integer.MAX_VALUE.
        int len = checkSize("array", size);

        List<CBORItem> list = new ArrayList<>();

        for (int i = 0; i < len; i++)
        {
            // Get the next data item.
            CBORItem item = nextItem();

            if (item == null)
            {
                // The end of the input stream was reached unexpectedly.
                throw insufficientData("an array");
            }

            if (item instanceof CBORBreak)
            {
                // An unexpected break appeared.
                throw unexpectedBreak("an array");
            }

            // Add a valid CBOR data item to the list.
            list.add(item);
        }

        // List of CBORItem
        return new CBORItemList(list);
    }


    private CBORItemList nextIndefiniteArray() throws IOException
    {
        List<CBORItem> list = new ArrayList<>();

        while (true)
        {
            // Get the next data item.
            CBORItem item = nextItem();

            if (item == null)
            {
                // The end of the input stream was reached unexpectedly.
                throw insufficientData("an indefinite-length array");
            }

            if (item instanceof CBORBreak)
            {
                // The end of the indefinite-length array.
                break;
            }

            // Add a valid CBOR data item to the list.
            list.add(item);
        }

        // List of CBORItem
        return new CBORItemList(list);
    }


    private CBORPairList nextMap(Number size) throws IOException
    {
        // The expected length of the map. The checkSize() method ensures
        // that the length is equal to or less than Integer.MAX_VALUE.
        int len = checkSize("map", size);

        List<CBORPair> list = new ArrayList<>();

        for (int i = 0; i < len; i++)
        {
            // Get the next data item as a map key.
            CBORItem key = nextItem();

            if (key == null)
            {
                // The end of the input stream was reached unexpectedly.
                throw insufficientData("a map key");
            }

            if (key instanceof CBORBreak)
            {
                // An unexpected break appeared.
                throw unexpectedBreak("a map key");
            }

            // Get the next data item as a map value.
            CBORItem value = nextItem();

            if (value == null)
            {
                // The end of the input stream was reached unexpectedly.
                throw insufficientData("a map value");
            }

            if (value instanceof CBORBreak)
            {
                // An unexpected break appeared.
                throw unexpectedBreak("a map value");
            }

            // Add a valid pair to the list.
            list.add(new CBORPair(key, value));
        }

        // List of CBORPair
        return new CBORPairList(list);
    }


    private CBORPairList nextIndefiniteMap() throws IOException
    {
        List<CBORPair> list = new ArrayList<>();

        while (true)
        {
            // Get the next data item as a map key.
            CBORItem key = nextItem();

            if (key == null)
            {
                // The end of the input stream was reached unexpectedly.
                throw insufficientData("a key of an indefinite-length map");
            }

            if (key instanceof CBORBreak)
            {
                // The end of the indefinite-length map.
                break;
            }

            // Get the next data item as a map value.
            CBORItem value = nextItem();

            if (value == null)
            {
                // The end of the input stream was reached unexpectedly.
                throw insufficientData("a value of an indefinite-length map");
            }

            if (value instanceof CBORBreak)
            {
                // An unexpected break appeared.
                throw unexpectedBreak("a value of an indefinite-length map");
            }

            // Add a valid pair to the list.
            list.add(new CBORPair(key, value));
        }

        // List of CBORPair
        return new CBORPairList(list);
    }


    private CBORItem nextTag(CTTag tag) throws IOException
    {
        // Get the next data item as a tag content.
        CBORItem item = nextItem();

        if (item == null)
        {
            // The end of the input stream was reached unexpectedly.
            throw insufficientData("a tag content");
        }

        // The tag number
        Number tagNumber = tag.getValue();

        // Get a processor for the tag.
        CBORTagProcessor processor = getTagProcessor(tagNumber);

        // If a processor for the tag is not registered.
        if (processor == null)
        {
            // Use the default processor.
            processor = CPDefault.INSTANCE;
        }

        // Let the processor process the data item.
        return processor.process(tagNumber, item);
    }


    private CBORTagProcessor getTagProcessor(Number tagNumber)
    {
        return options.getTagProcessor(tagNumber);
    }


    private CBORDecoderException invalidToken(String target, CBORToken<?> token)
    {
        if (token == null)
        {
            // The end of the input stream was reached during reading the target.
            return insufficientData(target);
        }
        else
        {
            // An unexpected CBOR token appeared during reading the target.
            return unexpectedToken(target, token);
        }
    }


    private CBORDecoderException insufficientData(String target)
    {
        // The end of the input stream was reached during reading the target.
        return new CBORInsufficientDataException(String.format(
                "The end of the input stream was reached during reading %s.", target),
                tokenizer.getReadCount());
    }


    private CBORDecoderException unexpectedToken(String target, CBORToken<?> token)
    {
        // An unexpected CBOR token appeared during reading the target.
        return new CBORDecoderException(String.format(
                "An unexpected CBOR token appeared during reading %s: token=%s, major=%d, info=%d",
                target, token.getClass().getSimpleName(), token.getMajor(), token.getInfo()),
                tokenizer.getReadCount());
    }


    private CBORDecoderException unexpectedBreak(String target)
    {
        // An unexpected break appeared during reading the target.
        return new CBORDecoderException(String.format(
                "An unexpected break appeared during reading %s.", target),
                tokenizer.getReadCount());
    }


    private CBORDecoderException tooBig(String target, Number size)
    {
        // The size of the CBOR target is impractically big.
        return new CBORTooLongException(String.format(
                "The size of the CBOR %s is impractically big: size=%s", target, size.toString()),
                tokenizer.getReadCount());
    }


    /**
     * Make sure that the size is equal to or less than Integer.MAX_VALUE.
     */
    private int checkSize(String target, Number size) throws CBORDecoderException
    {
        // If the given Number instance is a BigInteger instance.
        if (size instanceof BigInteger)
        {
            // The size is impractically big.
            throw tooBig(target, size);
        }

        // The length of the target in Long.
        Long length = (Long)size;

        // If the length of the target exceeds the range of int.
        if (LONG_INT_MAX.compareTo(length) < 0)
        {
            // The size is impractically big.
            throw tooBig(target, size);
        }

        // Convert to 'int'.
        return length.intValue();
    }
}

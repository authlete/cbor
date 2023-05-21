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
import java.io.InputStream;


/**
 * A parser that receives CBOR data items ({@link CBORItem}) from the CBOR
 * decoder ({@link CBORDecoder}) converts them into instances of common
 * Java classes.
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8949"
 *      >RFC 8949 Concise Binary Object Representation (CBOR)</a>
 */
public class CBORParser
{
    private final CBORDecoder decoder;


    /**
     * A constructor with a byte array that holds CBOR data items.
     *
     * <p>
     * This constructor is an alias of {@link #CBORParser(CBORDecoder)
     * CBORParser}{@code (new CBORDecoder(input))}.
     * </p>
     *
     * @param input
     *         A byte array that holds CBOR data items.
     */
    public CBORParser(byte[] input)
    {
        this(new CBORDecoder(input));
    }


    /**
     * A constructor with an input stream to read CBOR data items from.
     *
     * <p>
     * This constructor is an alias of {@link #CBORParser(CBORDecoder)
     * CBORParser}{@code (new CBORDecoder(inputStream))}.
     * </p>
     *
     * @param inputStream
     *         An input stream to read CBOR data items from.
     */
    public CBORParser(InputStream inputStream)
    {
        this(new CBORDecoder(inputStream));
    }


    /**
     * A constructor with a CBOR decoder.
     *
     * @param decoder
     *         A CBOR decoder used to decode CBOR data items in the input stream.
     *         Must not be {@code null}.
     */
    public CBORParser(CBORDecoder decoder)
    {
        if (decoder == null)
        {
            throw new NullPointerException(
                    "The CBOR decoder given to the constructor is null.");
        }

        this.decoder = decoder;
    }


    /**
     * Get the next CBOR data item from the input stream and convert it
     * into an instance of a common Java class.
     *
     * @return
     *         An object that represents a CBOR data item. When no more
     *         CBOR data item is available, {@code null} is returned.
     *
     * @throws IOException
     */
    public Object next() throws IOException
    {
        // Get the next CBOR data item.
        CBORItem item = decoder.next();

        if (item == null)
        {
            // No more CBOR data item is available.
            return null;
        }

        // Let the CBOR data item parse itself.
        return item.parse();
    }


    /**
     * Get the CBOR decoder used to decode CBOR data items in the input stream.
     *
     * @return
     *         The CBOR decoder.
     */
    public CBORDecoder getDecoder()
    {
        return decoder;
    }
}

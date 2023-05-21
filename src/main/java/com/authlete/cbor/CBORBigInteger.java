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


import static com.authlete.cbor.CBORConstants.BIG_INTEGER_MINUS_ONE;
import static com.authlete.cbor.CBORConstants.BIG_INTEGER_ULONG_MAX;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;


/**
 * CBOR big integer.
 *
 * <p>
 * The {@link #getValue()} method of this class returns an instance of
 * {@link BigInteger}.
 * </p>
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8949#name-major-types"
 *      >RFC 8949, 3.1. Major Types</a>
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8949#name-bignums"
 *      >RFC 8949, 3.4.3. Bignums</a>
 */
public class CBORBigInteger extends CBORNumber<BigInteger>
{
    public CBORBigInteger(BigInteger value)
    {
        super(value);
    }


    @Override
    public void encode(OutputStream outputStream) throws IOException
    {
        BigInteger value = getValue();

        // "unsigned" or "negative"?
        boolean unsigned = (0 <= value.signum());

        // If the value is negative.
        if (!unsigned)
        {
            // Convert to a positive number for encoding.
            value = BIG_INTEGER_MINUS_ONE.subtract(value);
        }

        // If it is possible to encode the big integer in 64 bits or less.
        if (value.compareTo(BIG_INTEGER_ULONG_MAX) <= 0)
        {
            // Encode with the major type 0 or 1.
            encode(outputStream, unsigned, value);
        }
        else
        {
            // Encode with a tag whose tag number is 2 or 3.
            encodeWithTag(outputStream, unsigned, value);
        }
    }


    private void encode(
            OutputStream outputStream, boolean unsigned, BigInteger value) throws IOException
    {
        // "unsigned" or "negative". See RFC 8949 Section 3.1 for details.
        int major = unsigned ? 0 : 1;

        encodeMajorWithNumber(outputStream, major, value);
    }


    private void encodeWithTag(
            OutputStream outputStream, boolean unsigned, BigInteger value) throws IOException
    {
        // "unsigned" or "negative". See RFC 8949 Section 3.4.3 for details.
        int tagNumber = unsigned ? 2 : 3;

        // A byte string representing the value of the big integer.
        CBORByteArray tagContent = new CBORByteArray(value.toByteArray());

        // Wrap the content with a tag.
        CBORTaggedItem item = new CBORTaggedItem(tagNumber, tagContent);

        item.encode(outputStream);
    }
}

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
package com.authlete.cbor.tag;


import static com.authlete.cbor.CBORConstants.BIG_INTEGER_INT_MAX;
import static com.authlete.cbor.CBORConstants.BIG_INTEGER_INT_MIN;
import static com.authlete.cbor.CBORConstants.BIG_INTEGER_LONG_MAX;
import static com.authlete.cbor.CBORConstants.BIG_INTEGER_LONG_MIN;
import static com.authlete.cbor.CBORConstants.BIG_INTEGER_MINUS_ONE;
import java.math.BigInteger;
import com.authlete.cbor.CBORBigInteger;
import com.authlete.cbor.CBORByteArray;
import com.authlete.cbor.CBORInteger;
import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORLong;
import com.authlete.cbor.CBORDecoderException;


/**
 * A tag processor for the tag number 2 and the tag number 3, which represent
 * an unsigned bignum and a negative bignum, respectively.
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8949#bignums"
 *      >RFC 8949, 3.4.3. Bignums</a>
 */
public class CPBignum implements CBORTagProcessor
{
    public static final CPBignum INSTANCE = new CPBignum();


    @Override
    public CBORItem process(Number tagNumber, CBORItem tagContent) throws CBORDecoderException
    {
        // As this tag processor is registered for the tag number 2 and the
        // tag number 3, the value of 'tagNumber' can be converted to 'int'
        // without information loss.
        int tag = tagNumber.intValue();

        // The tag number 2 is for an unsigned bignum.
        // The tag number 3 is for a negative bignum.
        boolean unsigned = (tag == 2);

        if (!(tagContent instanceof CBORByteArray))
        {
            throw new CBORDecoderException(String.format(
                    "The tag content for the tag number '%d' must be a byte string.", tag));
        }

        // The byte array that represents a bignum.
        byte[] data = ((CBORByteArray)tagContent).getValue();


        // A special case where bytes in the data are all zero.
        // This case is handled before BigInteger(int, byte[]) is called.
        if (allZero(data))
        {
            return new CBORInteger(unsigned ? 0 : -1);
        }

        // Parse the data as a positive integer.
        BigInteger value = new BigInteger(1 /* meaning 'positive' */, data);

        if (unsigned)
        {
            return buildUnsigned(value);
        }
        else
        {
            return buildNegative(value);
        }
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


    private static CBORItem buildUnsigned(BigInteger value)
    {
        if (value.compareTo(BIG_INTEGER_INT_MAX) <= 0)
        {
            return new CBORInteger(value.intValue());
        }

        if (value.compareTo(BIG_INTEGER_LONG_MAX) <= 0)
        {
            return new CBORLong(value.longValue());
        }

        return new CBORBigInteger(value);
    }


    private static CBORItem buildNegative(BigInteger value)
    {
        value = BIG_INTEGER_MINUS_ONE.subtract(value);

        if (BIG_INTEGER_INT_MIN.compareTo(value) <= 0)
        {
            return new CBORInteger(value.intValue());
        }

        if (BIG_INTEGER_LONG_MIN.compareTo(value) <= 0)
        {
            return new CBORLong(value.longValue());
        }

        return new CBORBigInteger(value);
    }
}

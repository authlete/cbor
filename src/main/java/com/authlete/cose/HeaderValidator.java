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
package com.authlete.cose;


import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.authlete.cbor.CBORBigInteger;
import com.authlete.cbor.CBORByteArray;
import com.authlete.cbor.CBORInteger;
import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORItemList;
import com.authlete.cbor.CBORLong;
import com.authlete.cbor.CBORPair;
import com.authlete.cbor.CBORString;
import com.authlete.cbor.CBORValue;
import com.authlete.cose.constants.COSEHeaderParameters;


/**
 * The implementation of the {@code validateParameters(List)} method of
 * {@link COSEProtectedHeader} and {@link COSEUnprotectedHeader}.
 *
 * @since 1.1
 */
class HeaderValidator
{
    static Map<Object, Object> validate(List<CBORPair> pairs, boolean unprotected)
    {
        Map<Object, Object> parameters = new LinkedHashMap<>();

        if (pairs == null)
        {
            return parameters;
        }

        for (CBORPair pair : pairs)
        {
            validateParameter(parameters, pair, unprotected);
        }

        return parameters;
    }


    private static void validateParameter(
            Map<Object, Object> parameters, CBORPair pair, boolean unprotected)
    {
        CBORItem key = pair.getKey();

        // A COSE header label is either an integer or a text string.
        Object label = ((key instanceof CBORString) || isInteger(key))
                     ? ((CBORValue<?>)key).getValue() : null;

        // If the COSE header label is neither an integer or a text string.
        if (label == null)
        {
            throw new IllegalArgumentException(
                    "A COSE header label must be an integer or a text string.");
        }

        // If the COSE header label is contained more than once.
        if (parameters.containsKey(label))
        {
            throw new IllegalArgumentException(String.format(
                    "The label '%s' is contained in the COSE header more than once.",
                    label.toString()));
        }

        // If the label is an integer that is in the range of Java 'int'.
        if (label instanceof Integer)
        {
            // Validate the value if the label is a known one.
            validateKnownParameter((Integer)label, pair.getValue(), unprotected);
        }

        // Add the label-value pair.
        parameters.put(label, pair.getValue().parse());
    }


    private static void validateKnownParameter(
            int label, CBORItem value, boolean unprotected)
    {
        switch (label)
        {
            case COSEHeaderParameters.ALG:
                validateAlg(value);
                break;

            case COSEHeaderParameters.CRIT:
                validateCrit(value, unprotected);
                break;

            case COSEHeaderParameters.CONTENT_TYPE:
                validateContentType(value);
                break;

            case COSEHeaderParameters.KID:
                validateKid(value);
                break;

            case COSEHeaderParameters.IV:
                validateIv(value);
                break;

            case COSEHeaderParameters.PARTIAL_IV:
                validatePartialIv(value);
                break;

            default:
                break;
        }
    }


    private static void validateAlg(CBORItem value)
    {
        // If the value of "alg (1)" is an integer or a text string.
        if (isInteger(value) || (value instanceof CBORString))
        {
            // OK
            return;
        }

        throw new IllegalArgumentException(
                "alg (1) must be an integer or a text string.");
    }


    private static void validateCrit(CBORItem value, boolean unprotected)
    {
        // If validation is performed for an unprotected header.
        if (unprotected)
        {
            throw new IllegalArgumentException(
                    "crit (2) must not be present in an unprotected header.");
        }

        // If the value of "crit (2)" is not a CBOR array.
        if (!(value instanceof CBORItemList))
        {
            throw new IllegalArgumentException(
                    "crit (2) must be a CBOR array.");
        }

        // Elements of "crit (2)".
        List<CBORItem> items = ((CBORItemList)value).getItems();

        // If "crit (2)" has no element.
        if (items == null || items.size() == 0)
        {
            throw new IllegalArgumentException(
                    "crit (2) must have at least one element.");
        }

        // For each element in "crit (2)".
        for (CBORItem item : items)
        {
            // If the element is an integer or a text string.
            if (isInteger(item) || (item instanceof CBORString))
            {
                // OK
                continue;
            }

            throw new IllegalArgumentException(
                    "Elements of crit (2) must be an integer or a text string.");
        }
    }


    private static void validateContentType(CBORItem value)
    {
        // If the value of "content type (3)" is a text string.
        if (value instanceof CBORString)
        {
            // OK
            return;
        }

        // If the value of "content type (3)" is not an integer.
        if (!isInteger(value))
        {
            throw new IllegalArgumentException(
                    "content type (3) must be an integer or a text string.");
        }

        // If the value of "content type (3)" is a negative integer.
        if (isNegativeInteger(value))
        {
            throw new IllegalArgumentException(
                    "content type (3) must not be a negative integer.");
        }
    }


    private static void validateKid(CBORItem value)
    {
        // If the value of "kid (4)" is a byte string.
        if (value instanceof CBORByteArray)
        {
            // OK
            return;
        }

        throw new IllegalArgumentException("kid (4) must be a byte string.");
    }


    private static void validateIv(CBORItem value)
    {
        // If the value of "IV (5)" is a byte string.
        if (value instanceof CBORByteArray)
        {
            // OK
            return;
        }

        throw new IllegalArgumentException("IV (5) must be a byte string.");
    }


    private static void validatePartialIv(CBORItem value)
    {
        // If the value of "Partial IV (6)" is a byte string.
        if (value instanceof CBORByteArray)
        {
            // OK
            return;
        }

        throw new IllegalArgumentException("Partial IV (6) must be a byte string.");
    }


    private static boolean isInteger(CBORItem item)
    {
        return (item instanceof CBORInteger   ) ||
               (item instanceof CBORLong      ) ||
               (item instanceof CBORBigInteger) ;
    }


    private static boolean isNegativeInteger(CBORItem item)
    {
        if (item instanceof CBORInteger)
        {
            return ((CBORInteger)item).getValue() < 0;
        }
        else if (item instanceof CBORLong)
        {
            return ((CBORLong)item).getValue() < 0L;
        }
        else if (item instanceof CBORBigInteger)
        {
            return ((CBORBigInteger)item).getValue().compareTo(BigInteger.ZERO) < 0;
        }
        else
        {
            // This should not happen.
            return false;
        }
    }
}

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
package com.authlete.cwt;


import java.math.BigInteger;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.authlete.cbor.CBORBigInteger;
import com.authlete.cbor.CBORByteArray;
import com.authlete.cbor.CBORConstants;
import com.authlete.cbor.CBORDouble;
import com.authlete.cbor.CBORFloat;
import com.authlete.cbor.CBORInteger;
import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORLong;
import com.authlete.cbor.CBORPair;
import com.authlete.cbor.CBORString;
import com.authlete.cbor.CBORValue;
import com.authlete.cwt.constants.CWTClaims;


/**
 * The implementation of the {@code validateClaims(List)} method of
 * {@link CWTClaimsSet}.
 *
 * @since 1.4
 */
class ClaimsValidator
{
    static Map<Object, Object> validate(List<? extends CBORPair> pairs)
    {
        Map<Object, Object> claims = new LinkedHashMap<>();

        if (pairs == null)
        {
            return claims;
        }

        for (CBORPair pair : pairs)
        {
            validateClaim(claims, pair);
        }

        return claims;
    }


    private static void validateClaim(Map<Object, Object> claims, CBORPair pair)
    {
        CBORItem key = pair.getKey();

        // It is not mentioned explicitly in RFC 8392 (CWT), we assume that
        // labels of claims follow the same rule as that of RFC 9052 (COSE).
        // That is, labels should be either an integer or a text string.
        Object label = ((key instanceof CBORString) || isInteger(key))
                     ? ((CBORValue<?>)key).getValue() : null;

        // If the label is neither an integer or a text string.
        if (label == null)
        {
            throw new IllegalArgumentException(
                    "A CWT claim key must be an integer or a text string.");
        }

        // If the label is contained more than once.
        if (claims.containsKey(label))
        {
            throw new IllegalArgumentException(String.format(
                    "The label '%s' is contained in the CWT payload more than once.",
                    label.toString()));
        }

        // The parsed value of the claim.
        Object value;

        // If the label is an integer that is in the range of Java 'int'.
        if (label instanceof Integer)
        {
            // Validate the value if the label is a known one.
            value = validateKnownClaim((Integer)label, pair.getValue());
        }
        else
        {
            value = parseValue(pair.getValue());
        }

        // Add the label-value pair.
        claims.put(label, value);
    }


    private static boolean isInteger(CBORItem item)
    {
        return (item instanceof CBORInteger   ) ||
               (item instanceof CBORLong      ) ||
               (item instanceof CBORBigInteger) ;
    }


    private static Object parseValue(CBORItem value)
    {
        return (value != null) ? value.parse() : null;
    }


    private static Object validateKnownClaim(int label, CBORItem value)
    {
        switch (label)
        {
            case CWTClaims.ISS:
                return validateTextString(value, "iss");

            case CWTClaims.SUB:
                return validateTextString(value, "sub");

            case CWTClaims.AUD:
                return validateTextString(value, "aud");

            case CWTClaims.EXP:
                return validateDate(value, "exp");

            case CWTClaims.NBF:
                return validateDate(value, "nbf");

            case CWTClaims.IAT:
                return validateDate(value, "iat");

            case CWTClaims.CTI:
                return validateByteString(value, "cti");

            case CWTClaims.NONCE:
                return validateByteString(value, "nonce");

            default:
                return parseValue(value);
        }
    }


    private static String validateTextString(CBORItem value, String claimName)
    {
        if (value instanceof CBORString)
        {
            return ((CBORString)value).getValue();
        }

        throw new IllegalArgumentException(String.format(
                "The value of the '%s' claim must be a text string.", claimName));
    }


    private static Date validateDate(CBORItem value, String claimName)
    {
        // Elapsed time in seconds since the Unix epoch.
        long seconds;

        // int or long
        if (value instanceof CBORInteger || value instanceof CBORLong)
        {
            // Convert int or long to long.
            seconds = ((Number)((CBORValue<?>)value).getValue()).longValue();
        }
        // BigInteger
        else if (value instanceof CBORBigInteger)
        {
            BigInteger bi = ((CBORBigInteger)value).getValue();

            if (bi.compareTo(CBORConstants.BIG_INTEGER_LONG_MIN) < 0 ||
                CBORConstants.BIG_INTEGER_LONG_MAX.compareTo(bi) < 0)
            {
                throw new IllegalArgumentException(String.format(
                        "The value of the '%s' claim is out of the valid range.", claimName));
            }

            // Convert BigInteger to long.
            seconds = bi.longValue();
        }
        // float or double
        else if (value instanceof CBORFloat || value instanceof CBORDouble)
        {
            // Convert float or double to double.
            double d = (double)((CBORValue<?>)value).getValue();

            if (d < Long.MIN_VALUE || Long.MAX_VALUE < d)
            {
                throw new IllegalArgumentException(String.format(
                        "The value of the '%s' claim is out of the valid range.", claimName));
            }

            // RFC 8392 (CWT) says that values of the known date claims are
            // either an integer or a floating-number. But it also says that
            // the claims have the same meaning of the corresponding ones
            // defined in RFC 7519 (JWT). If that is the case, The decimal
            // part of the value is meaningless.

            // Convert double to long.
            seconds = (long)d;
        }
        else
        {
            throw new IllegalArgumentException(String.format(
                    "The value of the '%s' claim must be either an integer or a floating-point number.", claimName));
        }

        if (seconds < 0)
        {
            throw new IllegalArgumentException(String.format(
                    "The value of the '%s' claim must not be negative.", claimName));
        }

        if (Long.MAX_VALUE / 1000L < seconds)
        {
            throw new IllegalArgumentException(String.format(
                    "The value of the '%s' claim is too big.", claimName));
        }

        // Convert seconds to milliseconds.
        long milliseconds = seconds * 1000L;

        return new Date(milliseconds);
    }


    private static byte[] validateByteString(CBORItem value, String claimName)
    {
        if (value instanceof CBORByteArray)
        {
            return ((CBORByteArray)value).getValue();
        }

        throw new IllegalArgumentException(String.format(
                "The value of the '%s' claim must be a byte string.", claimName));
    }
}

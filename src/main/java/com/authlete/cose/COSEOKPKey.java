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


import java.util.List;
import java.util.Map;
import com.authlete.cbor.CBORByteArray;
import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORPair;
import com.authlete.cbor.CBORString;
import com.authlete.cose.constants.COSEKeyTypeParameters;


/**
 * OKP Key
 *
 * @since 1.1
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9052.html#section-7"
 *      >RFC 9052, 7. Key Objects</a>
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9053.html#section-2.2"
 *      >RFC 9053, 2.2. Edwards-Curve Digital Signature Algorithm (EdDSA)</a>
 *
 * @see <a href="https://www.iana.org/assignments/cose/cose.xhtml#key-common-parameters"
 *      >IANA: COSE Key Common Parameters</a>
 *
 * @see <a href="https://www.iana.org/assignments/cose/cose.xhtml#key-type-parameters"
 *      >IANA: COSE Key Type Parameters</a>
 *
 * @see <a href="https://www.iana.org/assignments/cose/cose.xhtml#key-type"
 *      >IANA: COSE Key Types</a>
 *
 * @see <a href="https://www.iana.org/assignments/cose/cose.xhtml#elliptic-curves"
 *      >IANA: COSE Elliptic Curves</a>
 */
public class COSEOKPKey extends COSEKey
{
    private Object crv;
    private byte[] x;
    private byte[] d;


    /**
     * A constructor with key parameters.
     *
     * @param pairs
     *         Key parameters.
     */
    public COSEOKPKey(List<? extends CBORPair> pairs)
    {
        super(pairs);

        validateParameters(pairs);
    }

    private void validateParameters(List<? extends CBORPair> pairs)
    {
        for (CBORPair pair : pairs)
        {
            validateParameter(pair);
        }
    }


    private void validateParameter(CBORPair pair)
    {
        Object label = pair.getKey();

        // If the label is not an integer that is in the range of Java 'int'.
        if (!(label instanceof Integer))
        {
            // Unknown label.
            return;
        }

        // Validate the value if the label is a known one.
        validateKnownParameter((Integer)label, pair.getValue());
    }


    private void validateKnownParameter(int label, CBORItem value)
    {
        switch (label)
        {
            case COSEKeyTypeParameters.OKP_CRV:
                crv = validateCrv(value);
                break;

            case COSEKeyTypeParameters.OKP_X:
                x = validateX(value);
                break;

            case COSEKeyTypeParameters.OKP_D:
                d = validateD(value);
                break;

            default:
                break;
        }
    }


    private static Object validateCrv(CBORItem value)
    {
        if (isInteger(value) || (value instanceof CBORString))
        {
            return getRawValue(value);
        }

        throw new IllegalArgumentException(
                "crv (-1) must be an integer or a text string.");
    }


    private static byte[] validateX(CBORItem value)
    {
        if (value instanceof CBORByteArray)
        {
            return (byte[])getRawValue(value);
        }

        throw new IllegalArgumentException(
                "x (-2) must be a byte string.");
    }


    private static byte[] validateD(CBORItem value)
    {
        if (value instanceof CBORByteArray)
        {
            return (byte[])getRawValue(value);
        }

        throw new IllegalArgumentException(
                "d (-4) must be a byte string.");
    }


    @Override
    public boolean isPrivate()
    {
        return (d != null);
    }


    @Override
    protected void addJwkProperties(Map<String, Object> map)
    {
        // crv
        if (crv != null)
        {
            map.put("crv", toJwkCrv(crv));
        }

        // x
        if (x != null)
        {
            map.put("x", encodeByBase64Url(x));
        }

        // d
        if (d != null)
        {
            map.put("d", encodeByBase64Url(d));
        }
    }


    /**
     * Get the curve (the value of the {@code crv} parameter).
     *
     * <p>
     * The type of the value is an integer ({@code int}, {@code long} or
     * {@code BigInteger}) or a string ({@code String}).
     * </p>
     *
     * @return
     *         The curve.
     *
     * @see <a href="https://www.iana.org/assignments/cose/cose.xhtml#elliptic-curves"
     *      >IANA: COSE Elliptic Curves</a>
     */
    public Object getCrv()
    {
        return crv;
    }


    /**
     * Get the public key (the value of the {@code x} parameter).
     *
     * @return
     *         The public key.
     */
    public byte[] getX()
    {
        return x;
    }


    /**
     * Get the private key (the value of the {@code d} parameter).
     *
     * @return
     *         The private key.
     */
    public byte[] getD()
    {
        return d;
    }
}

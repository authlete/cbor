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


import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.List;
import com.authlete.cbor.CBORBoolean;
import com.authlete.cbor.CBORByteArray;
import com.authlete.cbor.CBORInteger;
import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORPair;
import com.authlete.cbor.CBORString;
import com.authlete.cose.constants.COSEKeyTypeParameters;


/**
 * ECDSA Key
 *
 * @since 1.1
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9052.html#section-7"
 *      >RFC 9052, 7. Key Objects</a>
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9053.html#section-2.1"
 *      >RFC 9053, 2.1. ECDSA</a>
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
public class COSEEC2Key extends COSEKey
{
    private Object crv;
    private byte[] x;
    private Object y;
    private byte[] d;


    /**
     * A constructor with key parameters.
     *
     * @param pairs
     *         Key parameters.
     */
    public COSEEC2Key(List<CBORPair> pairs)
    {
        super(pairs);

        validateParameters(pairs);
    }


    private void validateParameters(List<CBORPair> pairs)
    {
        for (CBORPair pair : pairs)
        {
            validateParameter(pair);
        }
    }


    private void validateParameter(CBORPair pair)
    {
        CBORItem label = pair.getKey();

        // If the label is not an integer that is in the range of Java 'int'.
        if (!(label instanceof CBORInteger))
        {
            // Unknown label.
            return;
        }

        // Validate the value if the label is a known one.
        validateKnownParameter(((CBORInteger)label).getValue(), pair.getValue());
    }


    private void validateKnownParameter(int label, CBORItem value)
    {
        switch (label)
        {
            case COSEKeyTypeParameters.EC2_CRV:
                crv = validateCrv(value);
                break;

            case COSEKeyTypeParameters.EC2_X:
                x = validateX(value);
                break;

            case COSEKeyTypeParameters.EC2_Y:
                y = validateY(value);
                break;

            case COSEKeyTypeParameters.EC2_D:
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


    private static Object validateY(CBORItem value)
    {
        if (value instanceof CBORByteArray || value instanceof CBORBoolean)
        {
            return getRawValue(value);
        }

        throw new IllegalArgumentException(
                "y (-3) must be a byte string or a boolean value.");
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
     * Get the x-coordinate (the value of the {@code x} parameter).
     *
     * @return
     *         The x-coordinate.
     */
    public byte[] getX()
    {
        return x;
    }


    /**
     * Get the y-coordinate (the value of the {@code y} parameter).
     *
     * <p>
     * The type of the value is either a byte array ({@code byte[]}) or boolean
     * ({@code Boolean}). When the point is compressed, the y-coordinate is
     * represented by using a single bit. See "2.3.3 Elliptic-Curve-Point-to-Octet-String
     * Conversion" and "2.3.4 Octet-String-to-Elliptic-Curve-Point Conversion"
     * in "<a href="https://www.secg.org/sec1-v2.pdf">SEC 1: Elliptic Curve
     * Cryptography</a>" for details.
     * </p>
     *
     * @return
     *         The y-coordinate.
     *
     * @see <a href="https://www.secg.org/sec1-v2.pdf">SEC 1: Elliptic Curve Cryptography</a>
     */
    public Object getY()
    {
        return y;
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


    /**
     * Convert this {@code COSEEC2Key} instance to an {@link ECPrivateKey} instance.
     *
     * @return
     *         A new {@link ECPrivateKey} instance.
     *
     * @throws COSEException
     */
    public ECPrivateKey toECPrivateKey() throws COSEException
    {
        return ECDSA.createPrivateKey(crv, d);
    }


    /**
     * Convert this {@code COSEEC2Key} instance to an {@link ECPublicKey} instance.
     *
     * @return
     *         A new {@link ECPublicKey} instance.
     *
     * @throws COSEException
     */
    public ECPublicKey toECPublicKey() throws COSEException
    {
        return ECDSA.createPublicKey(crv, x, y);
    }
}

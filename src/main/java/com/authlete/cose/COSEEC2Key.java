/*
 * Copyright (C) 2023-2024 Authlete, Inc.
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


import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.authlete.cbor.CBORBoolean;
import com.authlete.cbor.CBORByteArray;
import com.authlete.cbor.CBORInteger;
import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORPair;
import com.authlete.cbor.CBORPairList;
import com.authlete.cbor.CBORPairsBuilder;
import com.authlete.cbor.CBORString;
import com.authlete.cose.constants.COSEEllipticCurves;
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
    public COSEEC2Key(List<? extends CBORPair> pairs)
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


    @Override
    public boolean isPrivate()
    {
        return (d != null);
    }


    @Override
    public COSEKey toPublic() throws COSEException
    {
        // If this COSEKey instance represents a public key.
        if (!isPrivate())
        {
            return this;
        }

        // A pair list that contains only public parts.
        List<CBORPair> pairs = new ArrayList<>();

        // For each CBORPair that this COSEKey instance holds.
        for (CBORPair pair : getPairs())
        {
            // If the pair is not a private part.
            if (!isPrivatePart(pair))
            {
                // Add the public part.
                pairs.add(pair);
            }
        }

        // Create a new COSEKey instance from the pair list that
        // does not include private parts. As a result, the newly
        // created COSEKey instance becomes a public key.
        return build(new CBORPairList(pairs));
    }


    private static boolean isPrivatePart(CBORPair pair)
    {
        return isD(pair);
    }


    private static boolean isD(CBORPair pair)
    {
        // The key of the pair.
        CBORItem key = pair.getKey();

        // If the key is not an integer.
        if (!(key instanceof CBORInteger))
        {
            return false;
        }

        // Convert the key into a Java integer.
        int label = ((CBORInteger)key).getValue();

        // True if the label represents "d".
        return (label == COSEKeyTypeParameters.EC2_D);
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

        // y
        if (y != null)
        {
            map.put("y", toJwkY(y));
        }

        // d
        if (d != null)
        {
            map.put("d", encodeByBase64Url(d));
        }
    }


    private static Object toJwkY(Object y)
    {
        if (y instanceof byte[])
        {
            return encodeByBase64Url((byte[])y);
        }

        // The type of the value of 'y' is boolean.

        // TODO: The boolean value should be uncompressed to a big integer.
        //
        // cf. ECDSA.uncompressY(ECParameterSpec paramSpec, BigInteger x, boolean bit)

        return y;
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


    @Override
    public PrivateKey createPrivateKey() throws COSEException
    {
        return toECPrivateKey();
    }


    @Override
    public PublicKey createPublicKey() throws COSEException
    {
        return toECPublicKey();
    }


    static void addCoseKtySpecificParameters(
            CBORPairsBuilder builder, Map<String, Object> jwk) throws COSEException
    {
        // crv
        addCoseEC2Crv(builder, jwk);

        // x
        addCoseEC2X(builder, jwk);

        // y
        addCoseEC2Y(builder, jwk);

        // d
        addCoseEC2D(builder, jwk);
    }


    private static void addCoseEC2Crv(
            CBORPairsBuilder builder, Map<String, Object> jwk) throws COSEException
    {
        String crv = extractStringProperty(jwk, "crv", /* required */ false);

        if (crv == null)
        {
            return;
        }

        int value = COSEEllipticCurves.getValueByName(crv);

        if (value == 0)
        {
            throw new COSEException(String.format(
                    "The curve '%s' is not supported.", crv));
        }

        builder.add(COSEKeyTypeParameters.EC2_CRV, value);
    }


    private static void addCoseEC2X(
            CBORPairsBuilder builder, Map<String, Object> jwk) throws COSEException
    {
        byte[] value = extractBase64UrlProperty(jwk, "x", /* required */ false);

        builder.addUnlessNull(COSEKeyTypeParameters.EC2_X, value);
    }


    private static void addCoseEC2Y(
            CBORPairsBuilder builder, Map<String, Object> jwk) throws COSEException
    {
        byte[] value = extractBase64UrlProperty(jwk, "y", /* required */ false);

        builder.addUnlessNull(COSEKeyTypeParameters.EC2_Y, value);
    }


    private static void addCoseEC2D(
            CBORPairsBuilder builder, Map<String, Object> jwk) throws COSEException
    {
        byte[] value = extractBase64UrlProperty(jwk, "d", /* required */ false);

        builder.addUnlessNull(COSEKeyTypeParameters.EC2_D, value);
    }
}

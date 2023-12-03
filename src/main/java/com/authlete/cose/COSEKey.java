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


import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
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
import com.authlete.cbor.CBORPairList;
import com.authlete.cbor.CBORString;
import com.authlete.cbor.CBORValue;
import com.authlete.cbor.CBORizer;
import com.authlete.cose.constants.COSEAlgorithms;
import com.authlete.cose.constants.COSEEllipticCurves;
import com.authlete.cose.constants.COSEKeyCommonParameters;
import com.authlete.cose.constants.COSEKeyOperations;
import com.authlete.cose.constants.COSEKeyTypes;


/**
 * COSE Key
 *
 * @see 1.1
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9052.html#section-7"
 *      >RFC 9052, 7. Key Objects</a>
 */
public class COSEKey extends CBORPairList
{
    private Object kty;
    private byte[] kid;
    private Object alg;
    private List<Object> keyOps;
    private byte[] baseIv;


    /**
     * A constructor with key parameters.
     *
     * @param pairs
     *         Key parameters.
     */
    public COSEKey(List<? extends CBORPair> pairs)
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

        // Check the presence of kty.
        if (kty == null)
        {
            throw new IllegalArgumentException(
                    "A COSE key must contain kty (1)");
        }

        // TODO: Check if alg matches kty.
    }


    private void validateParameter(CBORPair pair)
    {
        CBORItem key = pair.getKey();

        // A COSE key label is either an integer or a text string.
        Object label = ((key instanceof CBORString) || isInteger(key))
                     ? getRawValue(key) : null;

        // If the COSE key label is neither an integer or a text string.
        if (label == null)
        {
            throw new IllegalArgumentException(
                    "A COSE key label must be an integer or a text string.");
        }

        // If the label is an integer that is in the range of Java 'int'.
        if (label instanceof Integer)
        {
            // Validate the value if the label is a known one.
            validateKnownParameter((Integer)label, pair.getValue());
        }
    }


    private void validateKnownParameter(int label, CBORItem value)
    {
        switch (label)
        {
            case COSEKeyCommonParameters.KTY:
                kty = validateKty(value);
                break;

            case COSEKeyCommonParameters.KID:
                kid = validateKid(value);
                break;

            case COSEKeyCommonParameters.ALG:
                alg = validateAlg(value);
                break;

            case COSEKeyCommonParameters.KEY_OPS:
                keyOps = validateKeyOps(value);
                break;

            case COSEKeyCommonParameters.BASE_IV:
                baseIv = validateBaseIv(value);
                break;

            default:
                break;
        }
    }


    static boolean isInteger(CBORItem item)
    {
        return (item instanceof CBORInteger   ) ||
               (item instanceof CBORLong      ) ||
               (item instanceof CBORBigInteger) ;
    }


    static Object getRawValue(CBORItem item)
    {
        return ((CBORValue<?>)item).getValue();
    }


    private static Object validateKty(CBORItem value)
    {
        if (isInteger(value) || (value instanceof CBORString))
        {
            return getRawValue(value);
        }

        throw new IllegalArgumentException(
                "kty (1) must be an integer or a text string.");
    }


    private static byte[] validateKid(CBORItem value)
    {
        if (value instanceof CBORByteArray)
        {
            return (byte[])getRawValue(value);
        }

        throw new IllegalArgumentException(
                "kid (2) must be a byte string.");
    }


    private static Object validateAlg(CBORItem value)
    {
        if (isInteger(value) || (value instanceof CBORString))
        {
            return getRawValue(value);
        }

        throw new IllegalArgumentException(
                "alg (3) must be an integer or a text string.");
    }


    private static List<Object> validateKeyOps(CBORItem value)
    {
        if (!(value instanceof CBORItemList))
        {
            throw new IllegalArgumentException(
                    "key_ops (4) must be a CBOR array.");
        }

        List<? extends CBORItem> items = ((CBORItemList)value).getItems();

        if (items == null || items.size() == 0)
        {
            throw new IllegalArgumentException(
                    "key_ops (4) must have at least one element.");
        }

        for (CBORItem item : items)
        {
            if (isInteger(item) || (item instanceof CBORString))
            {
                continue;
            }

            throw new IllegalArgumentException(
                    "Elements of key_ops (4) must be an integer or a text string.");
        }

        return ((CBORItemList)value).parse();
    }


    private static byte[] validateBaseIv(CBORItem value)
    {
        if (value instanceof CBORByteArray)
        {
            return (byte[])getRawValue(value);
        }

        throw new IllegalArgumentException(
                "Base IV (5) must be a byte string.");
    }


    /**
     * Get the set of parameters of this key as a {@code Map}.
     *
     * @return
     *         The set of parameters of this key.
     */
    public Map<Object, Object> getParameters()
    {
        return parse();
    }


    /**
     * Get the value of the "kty (1)" parameter.
     *
     * <p>
     * The type of the value is an integer ({@code int}, {@code long} or
     * {@code BigInteger}) or a string ({@code String}).
     * </p>
     *
     * @return
     *         The value of the "kty (1)" parameter.
     *
     * @see <a href="https://www.iana.org/assignments/cose/cose.xhtml#key-type"
     *      >IANA: COSE Key Types</a>
     */
    public Object getKty()
    {
        return kty;
    }


    /**
     * Get the value of the "kid (2)" parameter.
     *
     * @return
     *         The value of the "kid (2)" parameter. If the parameter is not
     *         contained, {@code null} is returned.
     */
    public byte[] getKid()
    {
        return kid;
    }


    /**
     * Get the value of the "alg (3)" parameter.
     *
     * <p>
     * The type of the value is an integer ({@code int}, {@code long} or
     * {@code BigInteger}) or a string ({@code String}).
     * </p>
     *
     * @return
     *         The value of the "alg (3)" parameter. If the parameter is not
     *         contained, {@code null} is returned.
     *
     * @see <a href="https://www.iana.org/assignments/cose/cose.xhtml#algorithms"
     *      >IANA: COSE Algorithms</a>
     */
    public Object getAlg()
    {
        return alg;
    }


    /**
     * Get the value of the "key_ops (4)" parameter.
     *
     * <p>
     * The type of elements in the returned list is an integer ({@code int},
     * {@code long} or {@code BigInteger}) or a string ({@code String}).
     * </p>
     *
     * @return
     *         The value of the "key_ops (4)" parameter. If the parameter is
     *         not contained, {@code null} is returned.
     */
    public List<Object> getKeyOps()
    {
        return keyOps;
    }


    /**
     * Get the value of the "Base IV (5)" parameter.
     *
     * @return
     *         The value of the "Base IV (5)" parameter. If the parameter is
     *         not contained, {@code null} is returned.
     */
    public byte[] getBaseIv()
    {
        return baseIv;
    }


    /**
     * Get the flag indicating whether this key contains private parameters.
     *
     * <p>
     * Subclasses of the {@link COSEKey} class are expected to override this
     * method.
     * </p>
     *
     * @return
     *         {@code true} if this key contains private parameters.
     *
     * @since 1.3
     */
    public boolean isPrivate()
    {
        return false;
    }


    /**
     * Convert this key to a {@link Map} instance that represents a JWK
     * (<a href="https://www.rfc-editor.org/rfc/rfc7517.html">RFC 7517
     * JSON Web Key (JWK)</a>).
     *
     * <p>
     * Subclasses of the {@link COSEKey} class should override the
     * {@link #addJwkProperties(Map)} method to add JWK properties that
     * are specific to them.
     * </p>
     *
     * @return
     *         A {@code Map} instance that represents a JWK.
     *
     * @since 1.3
     *
     * @see <a href="https://www.rfc-editor.org/rfc/rfc7517.html"
     *      >RFC 7517 JSON Web Key (JWK)</a>
     */
    public Map<String, Object> toJwk()
    {
        Map<String, Object> map = new LinkedHashMap<>();

        // kty
        map.put("kty", toJwkKty(kty));

        // alg
        if (alg != null)
        {
            map.put("alg", toJwkAlg(alg));
        }

        // kid
        if (kid != null)
        {
            map.put("kid", toJwkKid(kid));
        }

        // key_ops
        if (keyOps != null)
        {
            map.put("key_ops", toJwkKeyOps(keyOps));
        }

        // Additional properties.
        addJwkProperties(map);

        return map;
    }


    /**
     * Add JWK properties to the given map.
     *
     * <p>
     * This method is called from within the {@link #toJwk()} method.
     * Subclasses of the {@link COSEKey} class should override this method.
     * </p>
     *
     * @param map
     *         A map to which JWK properties should be added.
     *
     * @since 1.3
     */
    protected void addJwkProperties(Map<String, Object> map)
    {
        // Subclasses should override this method.
    }


    /**
     * Convert COSE 'kty' to JWK 'kty'.
     */
    private static String toJwkKty(Object kty)
    {
        // If the value of the 'kty' parameter is a string.
        if (kty instanceof String)
        {
            // Use the value as is.
            return (String)kty;
        }

        // The numeric identifier assigned to the key type.
        int identifier = ((Number)kty).intValue();

        switch (identifier)
        {
            case COSEKeyTypes.OKP:
                return "OKP";

            case COSEKeyTypes.EC2:
                return "EC";

            case COSEKeyTypes.RSA:
                return "RSA";

            default:
                // Convert the numeric identifier into a string.
                return ((Number)kty).toString();
        }
    }


    /**
     * Convert COSE 'alg' to JWK 'alg'.
     */
    private static String toJwkAlg(Object alg)
    {
        // If the value of the 'alg' parameter is a string.
        if (alg instanceof String)
        {
            // Use the value as is.
            return (String)alg;
        }

        // Get the algorithm name from the numeric identifier.
        String name = COSEAlgorithms.getNameByValue(((Number)alg).intValue());

        // If a name is assigned to the numeric identifier.
        if (name != null)
        {
            // The algorithm name.
            return name;
        }

        // Convert the numeric identifier into a string.
        return ((Number)alg).toString();
    }


    /**
     * Convert COSE 'kid' to JWK 'kid'.
     */
    private static String toJwkKid(byte[] kid)
    {
        try
        {
            // Convert the given byte array into a string on the assumption
            // that the byte array represents a UTF-8 byte sequence.
            return buildUtf8String(kid);
        }
        catch (Exception cause)
        {
            // Encode the given byte array by base64url.
            return encodeByBase64Url(kid);
        }
    }


    /**
     * Convert the given byte array into a string on the assumption
     * that the byte array represents a UTF-8 byte sequence.
     */
    private static String buildUtf8String(byte[] bytes) throws CharacterCodingException
    {
        ByteBuffer byteBuffer  = ByteBuffer.wrap(bytes);
        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();

        return decoder.decode(byteBuffer).toString();
    }


    /**
     * Encode the given byte array by base64url.
     */
    static String encodeByBase64Url(byte[] bytes)
    {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }


    /**
     * Convert COSE 'key_ops' to JWK 'key_ops'.
     */
    private static List<String> toJwkKeyOps(List<Object> keyOps)
    {
        List<String> ops = new ArrayList<>();

        for (Object keyOp : keyOps)
        {
            // Convert the COSE key operation into a JWK key operation.
            String op = toJwkKeyOp(keyOp);

            // If the conversion succeeded.
            if (op != null)
            {
                ops.add(op);
            }
        }

        return ops;
    }


    /**
     * Convert a COSE key operation to a JWK key operation.
     */
    private static String toJwkKeyOp(Object keyOp)
    {
        // If the value of the key operation is a string.
        if (keyOp instanceof String)
        {
            // Use the value as is.
            return (String)keyOp;
        }

        // The numeric identifier assigned to the key operation.
        int identifier = ((Number)keyOp).intValue();

        // cf. RFC 7517 Section 4.3. "key_ops" (Key Operations) Parameter

        switch (identifier)
        {
            case COSEKeyOperations.SIGN:
                return "sign";

            case COSEKeyOperations.VERIFY:
                return "verify";

            case COSEKeyOperations.ENCRYPT:
                return "encrypt";

            case COSEKeyOperations.DECRYPT:
                return "decrypt";

            case COSEKeyOperations.WRAP_KEY:
                return "wrapKey";

            case COSEKeyOperations.UNWRAP_KEY:
                return "unwrapKey";

            case COSEKeyOperations.DERIVE_KEY:
                return "deriveKey";

            case COSEKeyOperations.DERIVE_BITS:
                return "deriveBits";

            case COSEKeyOperations.MAC_CREATE:
            case COSEKeyOperations.MAC_VERIFY:
            default:
                // Convert the numeric identifier into a string.
                return ((Number)keyOp).toString();
        }
    }


    /**
     * Convert COSE 'crv' to JWK 'crv'.
     */
    static String toJwkCrv(Object crv)
    {
        // If the value of the 'crv' parameter is a string.
        if (crv instanceof String)
        {
            // Use the value as is.
            return (String)crv;
        }

        // Get the curve name from the numeric identifier.
        String name = COSEEllipticCurves.getNameByValue(((Number)crv).intValue());

        // If a name is assigned to the numeric identifier.
        if (name != null)
        {
            // The curve name.
            return name;
        }

        // Convert the numeric identifier into a string.
        return ((Number)crv).toString();
    }


    /**
     * Build an instance of {@link COSEKey} or its subclass when possible
     * (e.g. {@link COSEOKPKey} and {@link COSEEC2Key}) from the given CBOR
     * data item.
     *
     * @param item
     *         A CBOR data item that represents a COSE key. It must be a
     *         CBOR map.
     *
     * @return
     *         An instance of {@link COSEKey} or its subclass.
     *
     * @throws COSEException
     */
    public static COSEKey build(CBORItem item) throws COSEException
    {
        // If the given CBOR item is not a CBOR map.
        if (!(item instanceof CBORPairList))
        {
            throw new COSEException("A COSE key must be a CBOR map.");
        }

        // Key-value pairs in the CBOR map.
        List<? extends CBORPair> pairs = ((CBORPairList)item).getPairs();

        // If the CBOR map that represents a COSE key has no key-value pairs.
        if (pairs == null)
        {
            throw new COSEException("A COSE key must not be empty.");
        }

        // The value of the "kty (1)" parameter.
        Object kty = extractKty(pairs);

        // If the value of the "kty (1)" parameter is in the range of Java 'int'.
        if (kty instanceof Integer)
        {
            return buildKey((Integer)kty, pairs);
        }
        else
        {
            return buildKey(0, pairs);
        }
    }


    private static Object extractKty(List<? extends CBORPair> pairs) throws COSEException
    {
        // The "kty (1)" label.
        Integer labelKty = Integer.valueOf(COSEKeyCommonParameters.KTY);

        // Search the pairs for "kty".
        for (CBORPair pair : pairs)
        {
            CBORItem key = pair.getKey();

            // If the key is not an integer that is in the range of Java 'int'.
            if (!(key instanceof CBORInteger))
            {
                continue;
            }

            // If the label does not represent "kty".
            if (!labelKty.equals(((CBORInteger)key).getValue()))
            {
                continue;
            }

            try
            {
                // The value of the "kty (1)" parameter.
                return validateKty(pair.getValue());
            }
            catch (Exception cause)
            {
                // The type of the value is wrong.
                throw new COSEException(cause.getMessage(), cause);
            }
        }

        throw new COSEException("A COSE key must contain the kty (1) parameter.");
    }


    private static COSEKey buildKey(int kty, List<? extends CBORPair> pairs) throws COSEException
    {
        try
        {
            switch (kty)
            {
                case COSEKeyTypes.OKP:
                    return new COSEOKPKey(pairs);

                case COSEKeyTypes.EC2:
                    return new COSEEC2Key(pairs);

                    // TODO

                default:
                    return new COSEKey(pairs);
            }
        }
        catch (Exception cause)
        {
            throw new COSEException(cause.getMessage(), cause);
        }
    }


    /**
     * Build an instance of {@link COSEKey} or its subclass when possible
     * (e.g. {@link COSEOKPKey} and {@link COSEEC2Key}) from the given map.
     *
     * @param map
     *         A map that represents a COSE key.
     *
     * @return
     *         An instance of {@link COSEKey} or its subclass.
     *
     * @throws COSEException
     */
    public static COSEKey build(Map<Object, Object> map) throws COSEException
    {
        return build(new CBORizer().cborizeMap(map));
    }
}

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


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.authlete.cbor.CBORByteArray;
import com.authlete.cbor.CBORDecoder;
import com.authlete.cbor.CBORDecoderException;
import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORNull;
import com.authlete.cbor.CBORPair;
import com.authlete.cbor.CBORPairList;
import com.authlete.cbor.CBORizer;
import com.authlete.cwt.constants.CWTClaims;


/**
 * CWT Claims Set
 *
 * @since 1.4
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8392.html"
 *      >RFC 8392 CBOR Web Token (CWT)</a>
 *
 * @see <a href="https://www.iana.org/assignments/cwt/cwt.xhtml"
 *      >IANA: CBOR Web Token (CWT) Claims</a>
 */
public class CWTClaimsSet extends CBORPairList
{
    private String iss;
    private String sub;
    private String aud;
    private Date exp;
    private Date nbf;
    private Date iat;
    private byte[] cti;
    private byte[] nonce;


    /**
     * A constructor with label-value pairs that represent claims.
     *
     * @param pairs
     *         Claims.
     *
     * @throws IllegalArgumentException
     *         There is one or more claims that do not conform to the
     *         requirements of <a href=
     *         "https://www.rfc-editor.org/rfc/rfc8392.html#section-3"
     *         >RFC 8392 Section 3. Claims</a>.
     */
    public CWTClaimsSet(List<? extends CBORPair> pairs) throws IllegalArgumentException
    {
        super(pairs);

        validateClaims(pairs);
    }


    private void validateClaims(List<? extends CBORPair> pairs)
    {
        // Validate the label-value pairs.
        Map<Object, Object> map = ClaimsValidator.validate(pairs);

        // For each label-value pair.
        for (Map.Entry<Object, Object> entry : map.entrySet())
        {
            Object label = entry.getKey();

            // If the label is not an integer.
            if (!(label instanceof Integer))
            {
                // The label is a string or an integer which is out of the
                // range of Java 'int'. Anyway, this implementation does
                // nothing special for the label.
                continue;
            }

            // Initialize the instance field that corresponds to the label.
            initializeField((Integer)label, entry.getValue());
        }
    }


    private void initializeField(int label, Object value)
    {
        // Set up the instance field that corresponds to the label.
        switch (label)
        {
            case CWTClaims.ISS:
                iss = (String)value;
                break;

            case CWTClaims.SUB:
                sub = (String)value;
                break;

            case CWTClaims.AUD:
                aud = (String)value;
                break;

            case CWTClaims.EXP:
                exp = (Date)value;
                break;

            case CWTClaims.NBF:
                nbf = (Date)value;
                break;

            case CWTClaims.IAT:
                iat = (Date)value;
                break;

            case CWTClaims.CTI:
                cti = (byte[])value;
                break;

            case CWTClaims.NONCE:
                nonce = (byte[])value;
                break;

            default:
                break;
        }
    }


    /**
     * Get the value of the "iss (1)" claim.
     *
     * @return
     *         The value of the "iss (1)" claim.
     */
    public String getIss()
    {
        return iss;
    }


    /**
     * Get the value of the "sub (2)" claim.
     *
     * @return
     *         The value of the "sub (2)" claim.
     */
    public String getSub()
    {
        return sub;
    }


    /**
     * Get the value of the "aud (3)" claim.
     *
     * @return
     *         The value of the "aud (3)" claim.
     */
    public String getAud()
    {
        return aud;
    }


    /**
     * Get the value of the "exp (4)" claim.
     *
     * @return
     *         The value of the "exp (4)" claim.
     */
    public Date getExp()
    {
        return exp;
    }


    /**
     * Get the value of the "nbf (5)" claim.
     *
     * @return
     *         The value of the "nbf (5)" claim.
     */
    public Date getNbf()
    {
        return nbf;
    }


    /**
     * Get the value of the "iat (6)" claim.
     *
     * @return
     *         The value of the "iat (6)" claim.
     */
    public Date getIat()
    {
        return iat;
    }


    /**
     * Get the value of the "cti (7)" claim.
     *
     * @return
     *         The value of the "cti (7)" claim.
     */
    public byte[] getCti()
    {
        return cti;
    }


    /**
     * Get the value of the "cti (7)" claim as a string.
     *
     * <p>
     * This method assumes that the value of the {@code cti} claim represents
     * a UTF-8 byte sequence. Character sequences that are unmappable to the
     * UTF-8 encoding are replaced with the default replacement string. if
     * you want an exception to be raised when unmappable character sequences
     * are found, use the {@link #getCtiAsStringWithException()} method.
     * </p>
     *
     * @return
     *         The value of the "cti (7)" claim as a string.
     *
     * @see #getCtiAsStringWithException()
     */
    public String getCtiAsString()
    {
        return buildString(cti);
    }


    /**
     * Get the value of the "cti (7)" claim as a string.
     *
     * <p>
     * This method assumes that the value of the {@code cti} claim represents
     * a UTF-8 byte sequence. When unmappable character sequences are found,
     * an exception is raised.
     * </p>
     *
     * @return
     *         The value of the "cti (7)" claim as a string.
     *
     * @throws CharacterCodingException
     *         Unmappable character sequences are found.
     *
     * @see #getCtiAsString()
     */
    public String getCtiAsStringWithException() throws CharacterCodingException
    {
        return buildStringWithException(cti);
    }


    /**
     * Get the value of the "Nonce (10)" claim.
     *
     * @return
     *         The value of the "Nonce (10)" claim.
     */
    public byte[] getNonce()
    {
        return nonce;
    }


    /**
     * Get the value of the "Nonce (10)" claim as a string.
     *
     * <p>
     * This method assumes that the value of the {@code Nonce} claim represents
     * a UTF-8 byte sequence. Character sequences that are unmappable to the
     * UTF-8 encoding are replaced with the default replacement string. if
     * you want an exception to be raised when unmappable character sequences
     * are found, use the {@link #getNonceAsStringWithException()} method.
     * </p>
     *
     * @return
     *         The value of the "Nonce (10)" claim as a string.
     *
     * @see #getNonceAsStringWithException()
     */
    public String getNonceAsString()
    {
        return buildString(nonce);
    }


    /**
     * Get the value of the "Nonce (10)" claim as a string.
     *
     * <p>
     * This method assumes that the value of the {@code Nonce} claim represents
     * a UTF-8 byte sequence. When unmappable character sequences are found,
     * an exception is raised.
     * </p>
     *
     * @return
     *         The value of the "Nonce (10)" claim as a string.
     *
     * @throws CharacterCodingException
     *         Unmappable character sequences are found.
     *
     * @see #getNonceAsString()
     */
    public String getNonceAsStringWithException() throws CharacterCodingException
    {
        return buildStringWithException(nonce);
    }


    private static String buildString(byte[] bytes)
    {
        if (bytes == null)
        {
            return null;
        }

        return new String(bytes, StandardCharsets.UTF_8);
    }


    private static String buildStringWithException(byte[] bytes) throws CharacterCodingException
    {
        if (bytes == null)
        {
            return null;
        }

        ByteBuffer byteBuffer  = ByteBuffer.wrap(bytes);
        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();

        return decoder.decode(byteBuffer).toString();
    }


    /**
     * Build an instance of the {@link CWTClaimsSet} class based on the given
     * CBOR item.
     *
     * <p>
     * When the given CBOR item is an instance of {@link CWTClaimsSet}, the
     * given item is returned as is.
     * </p>
     *
     * <p>
     * When the given CBOR item is null or an instance of {@link CBORNull}
     * (which represents a CBOR null), a {@code CWTClaimsSet} instance that
     * has no claim is returned.
     * </p>
     *
     * <p>
     * When the given CBOR item is an instance of {@link CBORPairList} (which
     * represents a CBOR map), the key-value pairs in the instance are used as
     * claims of the returned {@code CWTClaimsSet} instance.
     * </p>
     *
     * <p>
     * When the given CBOR item is an instance of {@link CBORByteArray} (which
     * represents a CBOR byte string), this method interprets the content of
     * the byte string as a CBOR map and uses the key-value pairs in the map
     * as claims of the returned {@code CWTClaimsSet} instance. If the content
     * of the byte string is not a valid CBOR map, an exception is raised.
     * </p>
     *
     * <p>
     * In other case, an exception is raised.
     * </p>
     *
     * @param payload
     *         Data that represents CWT claims set.
     *
     * @return
     *         A new {@code CWTClaimsSet} instance built based on the given
     *         data. When the given data is an instance of the
     *         {@code CWTClaimsSet} class, the given data is returned as is.
     *
     * @throws CBORDecoderException
     *         The given data is invalid.
     */
    public static CWTClaimsSet build(CBORItem payload) throws CBORDecoderException
    {
        List<? extends CBORPair> pairs;

        if (payload instanceof CWTClaimsSet)
        {
            return (CWTClaimsSet)payload;
        }
        else if (payload == null || payload instanceof CBORNull)
        {
            pairs = null;
        }
        else if (payload instanceof CBORPairList)
        {
            pairs = ((CBORPairList)payload).getPairs();
        }
        else if (payload instanceof CBORByteArray)
        {
            pairs = extractPairs((CBORByteArray)payload);
        }
        else
        {
            throw new CBORDecoderException(String.format(
                    "Unexpected type for the CWT payload: %s",
                    payload.getClass().getSimpleName()));
        }

        if (pairs == null)
        {
            pairs = Collections.emptyList();
        }

        try
        {
            return new CWTClaimsSet(pairs);
        }
        catch (Exception cause)
        {
            throw new CBORDecoderException(String.format(
                    "The CWT payload is invalid: %s", cause.getMessage()), cause);
        }
    }


    private static List<? extends CBORPair> extractPairs(CBORByteArray payload) throws CBORDecoderException
    {
        try
        {
            // Create a decoder to interpret the content of the payload
            // as a sequence of CBOR items.
            CBORDecoder decoder = new CBORDecoder(payload.getValue());

            // The CWT payload must be a byte string which represents
            // a CBOR map. Extract the key-value pairs from the map.
            return ((CBORPairList)decoder.next()).getPairs();
        }
        catch (IOException cause)
        {
            throw new CBORDecoderException(
                    "The CWT payload failed to be parsed as a CBOR map.", cause);
        }
    }


    /**
     * Build an instance of the {@link CWTClaimsSet} class using the key-value
     * pairs in the given map as claims.
     *
     * @param map
     *         Data that represents CWT claims set. If null is given, a
     *         {@code CWTClaimsSet} instance that has no claim is returned.
     *
     * @return
     *         A new {@code CWTClaimsSet} instance built based on the given map.
     *
     * @throws IllegalArgumentException
     *         There is one or more claims that do not conform to the
     *         requirements of <a href=
     *         "https://www.rfc-editor.org/rfc/rfc8392.html#section-3"
     *         >RFC 8392 Section 3. Claims</a>.
     */
    public static CWTClaimsSet build(Map<Object, Object> map) throws IllegalArgumentException
    {
        if (map == null)
        {
            map = Collections.emptyMap();
        }

        // Convert the map into a CBOR map.
        CBORPairList pairList = (CBORPairList)new CBORizer().cborizeMap(map);

        return new CWTClaimsSet(pairList.getPairs());
    }
}

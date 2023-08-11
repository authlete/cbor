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


import static com.authlete.cose.constants.COSEHeaderParameters.ALG;
import static com.authlete.cose.constants.COSEHeaderParameters.CONTENT_TYPE;
import static com.authlete.cose.constants.COSEHeaderParameters.CRIT;
import static com.authlete.cose.constants.COSEHeaderParameters.IV;
import static com.authlete.cose.constants.COSEHeaderParameters.KID;
import static com.authlete.cose.constants.COSEHeaderParameters.PARTIAL_IV;
import static com.authlete.cose.constants.COSEHeaderParameters.X5CHAIN;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import com.authlete.cbor.CBORByteArray;
import com.authlete.cbor.CBORDecoder;
import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORPair;
import com.authlete.cbor.CBORPairList;
import com.authlete.cbor.CBORizer;


/**
 * COSE Protected Header
 *
 * @since 1.1
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9052.html#section-3"
 *      >RFC 9052, 3. Header Parameters</a>
 *
 * @see COSEProtectedHeaderBuilder
 */
public class COSEProtectedHeader extends CBORByteArray
{
    private final List<CBORPair> pairs;
    private Object alg;
    private List<Object> crit;
    private Object contentType;
    private byte[] kid;
    private byte[] iv;
    private byte[] partialIv;
    private List<X509Certificate> x5chain;


    /**
     * A constructor with the content of the header.
     *
     * <p>
     * This constructor is an alias of {@link #COSEProtectedHeader(byte[], List)
     * this}{@code (value, null)}.
     * </p>
     *
     * @param value
     *         The content of the header.
     */
    public COSEProtectedHeader(byte[] value)
    {
        this(value, null);
    }


    /**
     * A constructor with the content of the header and the decoded key-value
     * pairs of the content.
     *
     * <p>
     * It is the caller's responsibility to ensure that the decoded key-value
     * {@code pairs} matches the content ({@code value}).
     * </p>
     *
     * @param value
     *         The content of the header.
     *
     * @param pairs
     *         The decoded key-value pairs of the content.
     *
     * @throws IllegalArgumentException
     *         {@code pairs} contains one or more header parameters that do not
     *         conform to the requirements of <a href=
     *         "https://www.rfc-editor.org/rfc/rfc9052.html#section-3.1">3.1.
     *         Common COSE Header Parameters</a>.
     */
    public COSEProtectedHeader(byte[] value, List<CBORPair> pairs) throws IllegalArgumentException
    {
        super(value, true);

        validateParameters(pairs);

        this.pairs = pairs;
    }


    private void validateParameters(List<CBORPair> pairs)
    {
        // Validate the label-value pairs.
        Map<Object, Object> map = HeaderValidator.validate(
                pairs, false /* unprotected==false --> protected */);

        // For each label-value pair.
        for (Map.Entry<Object, Object> entry : map.entrySet())
        {
            Object label = entry.getKey();

            // If the label is not an integer.
            if (!(label instanceof Integer))
            {
                // The label is a string. Anyway, this implementation does
                // nothing special for the label.
                continue;
            }

            // Initialize the instance field that corresponds to the label.
            initializeField((Integer)label, entry.getValue());
        }
    }


    @SuppressWarnings("unchecked")
    private void initializeField(int label, Object value)
    {
        // Set up the instance field that corresponds to the label.
        switch (label)
        {
            case ALG:
                alg = value;
                break;

            case CRIT:
                crit = (List<Object>)value;
                break;

            case CONTENT_TYPE:
                contentType = value;
                break;

            case KID:
                kid = (byte[])value;
                break;

            case IV:
                iv = (byte[])value;
                break;

            case PARTIAL_IV:
                partialIv = (byte[])value;
                break;

            case X5CHAIN:
                x5chain = (List<X509Certificate>)value;
                break;

            default:
                break;
        }
    }


    /**
     * Get the set of parameters of this header as a list of {@link CBORPair}.
     *
     * <p>
     * The second argument given to the {@link #COSEProtectedHeader(byte[], List)}
     * constructor is returned.
     * </p>
     *
     * @return
     *         The set of parameters of this header.
     */
    public List<CBORPair> getPairs()
    {
        return pairs;
    }


    /**
     * Get the set of parameters of this header as a {@code Map}.
     *
     * <p>
     * A {@code Map} instance is built from the second argument given to the
     * {@link #COSEProtectedHeader(byte[], List)} constructor. If the second
     * argument was {@code null}, this method returns an empty {@code Map}
     * instance.
     * </p>
     *
     * @return
     *         The set of parameters of this header.
     */
    public Map<Object, Object> getParameters()
    {
        return new CBORPairList(pairs).parse();
    }


    /**
     * Get the value of the "alg (1)" parameter.
     *
     * <p>
     * The type of the value is an integer ({@code int}, {@code long} or
     * {@code BigInteger}) or a string ({@code String}).
     * </p>
     *
     * @return
     *         The value of the "alg (1)" parameter. If the header does not
     *         contain the parameter, {@code null} is returned.
     *
     * @see <a href="https://www.iana.org/assignments/cose/cose.xhtml#algorithms"
     *      >IANA: COSE Algorithms</a>
     */
    public Object getAlg()
    {
        return alg;
    }


    /**
     * Get the value of the "crit (2)" parameter.
     *
     * <p>
     * The type of elements in the returned list is an integer ({@code int},
     * {@code long} or {@code BigInteger}) or a string ({@code String}).
     * </p>
     *
     * @return
     *         The value of the "crit (2)" parameter. If the header does not
     *         contain the parameter, {@code null} is returned.
     */
    public List<Object> getCrit()
    {
        return crit;
    }


    /**
     * Get the value of the "content type (3)" parameter.
     *
     * <p>
     * The type of the value is an unsigned integer ({@code int}, {@code long}
     * or {@code BigInteger}) or a string ({@code String}).
     * </p>
     *
     * @return
     *         The value of the "content type (3)" parameter. If the header
     *         does not contain the parameter, {@code null} is returned.
     */
    public Object getContentType()
    {
        return contentType;
    }


    /**
     * Get the value of the "kid (4)" parameter.
     *
     * @return
     *         The value of the "kid (4)" parameter. If the header does not
     *         contain the parameter, {@code null} is returned.
     */
    public byte[] getKid()
    {
        return kid;
    }


    /**
     * Get the value of the "IV (5)" parameter.
     *
     * @return
     *         The value of the "IV (5)" parameter. If the header does not
     *         contain the parameter, {@code null} is returned.
     */
    public byte[] getIv()
    {
        return iv;
    }


    /**
     * Get the value of the "Partial IV (6)" parameter.
     *
     * @return
     *         The value of the "Partial IV (6)" parameter. If the header
     *         does not contain the parameter, {@code null} is returned.
     */
    public byte[] getPartialIv()
    {
        return partialIv;
    }


    /**
     * Get the value of the "x5chain (33)" parameter.
     *
     * @return
     *         The value of the "x5chain (33)" parameter. If the header does
     *         not contain the parameter, {@code null} is returned.
     *
     * @since 1.2
     */
    public List<X509Certificate> getX5Chain()
    {
        return x5chain;
    }


    /**
     * Interpret the given CBOR data item as a protected header and build
     * an instance of {@link COSEProtectedHeader} from it.
     *
     * @param header
     *         A CBOR data item that represents a protected header. It must be
     *         a byte string whose content is a CBOR map or empty.
     *
     * @return
     *         A protected header.
     *
     * @throws COSEException
     *         The given CBOR data item does not conform to the requirements
     *         of protected header.
     */
    public static COSEProtectedHeader build(CBORItem header) throws COSEException
    {
        // If the protected header is not a byte string.
        if (!(header instanceof CBORByteArray))
        {
            throw new COSEException(
                    "A protected header must be a byte string.");
        }

        // A byte array that represents a CBOR map.
        byte[] bytes = ((CBORByteArray)header).getValue();

        if (bytes.length == 0)
        {
            // This is allowed. Rather, the COSE specification recommends
            // a zero-length byte string to represent an empty protected header.
            return new COSEProtectedHeader(bytes);
        }

        // Create a decoder to decode the content of the byte array.
        CBORDecoder decoder = new CBORDecoder(bytes);

        // Read the first CBOR data item.
        CBORItem item = next(decoder);

        // If the content is not a CBOR map.
        if (!(item instanceof CBORPairList))
        {
            throw new COSEException(
                    "The content of a byte string that represents a protected header must be a CBOR map.");
        }

        // If there exist more CBOR data items.
        if (next(decoder) != null)
        {
            throw new COSEException(
                    "The content of the byte string that represents a protected header must not contain redundant CBOR data items.");
        }

        // Key-value pairs in the CBOR map.
        List<CBORPair> pairs = ((CBORPairList)item).getPairs();

        if (pairs.size() == 0)
        {
            // The COSE specification recommends a zero-length byte string over
            // a byte-string including an empty map, so a zero-length byte
            // string is used here.
            return new COSEProtectedHeader(new byte[] {});
        }

        try
        {
            // Create a protected header with the original byte array and the
            // decoded key-value pairs.
            return new COSEProtectedHeader(bytes, pairs);
        }
        catch (Exception cause)
        {
            // Validation of the pairs failed.
            throw new COSEException(cause.getMessage(), cause);
        }
    }


    private static CBORItem next(CBORDecoder decoder) throws COSEException
    {
        try
        {
            return decoder.next();
        }
        catch (IOException cause)
        {
            // The next() method of a CBORDecoder instance created with a byte
            // array will not throw an IOException, so this never happens.
            throw new COSEException(cause.getMessage(), cause);
        }
    }


    /**
     * Build a {@link COSEProtectedHeader} instance from the given map.
     *
     * @param map
     *         A map containing header parameters.
     *
     * @return
     *         A {@link COSEProtectedHeader} instance built from the given map.
     */
    public static COSEProtectedHeader build(Map<Object, Object> map)
    {
        if (map == null)
        {
            map = Map.of();
        }

        CBORPairList pairList = (CBORPairList)new CBORizer().cborizeMap(map);
        byte[]       value    = pairList.encode();

        return new COSEProtectedHeader(value, pairList.getPairs());
    }
}

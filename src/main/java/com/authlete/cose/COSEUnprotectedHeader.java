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
import static com.authlete.cose.constants.COSEHeaderParameters.IV;
import static com.authlete.cose.constants.COSEHeaderParameters.KID;
import static com.authlete.cose.constants.COSEHeaderParameters.PARTIAL_IV;
import static com.authlete.cose.constants.COSEHeaderParameters.X5CHAIN;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORPair;
import com.authlete.cbor.CBORPairList;
import com.authlete.cbor.CBORizer;


/**
 * COSE Unprotected Header
 *
 * @since 1.1
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9052.html#section-3"
 *      >RFC 9052, 3. Header Parameters</a>
 *
 * @see COSEUnprotectedHeaderBuilder
 */
public class COSEUnprotectedHeader extends CBORPairList
{
    private Object alg;
    private Object contentType;
    private byte[] kid;
    private byte[] iv;
    private byte[] partialIv;
    private List<X509Certificate> x5chain;


    /**
     * A constructor with key-value pairs that represent the header parameters.
     *
     * @param pairs
     *         Header parameters.
     *
     * @throws IllegalArgumentException
     *         There is one or more header parameters that do not conform to
     *         the requirements of <a href=
     *         "https://www.rfc-editor.org/rfc/rfc9052.html#section-3.1">3.1.
     *         Common COSE Header Parameters</a>.
     */
    public COSEUnprotectedHeader(List<? extends CBORPair> pairs) throws IllegalArgumentException
    {
        super(pairs);

        validateParameters(pairs);

        setComment("unprotected");
    }


    private void validateParameters(List<? extends CBORPair> pairs)
    {
        // Validate the label-value pairs.
        Map<Object, Object> map = HeaderValidator.validate(pairs, true /* unprotected */);

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
     * Get the set of parameters of this header as a {@code Map}.
     *
     * @return
     *         The set of parameters of this header.
     */
    public Map<Object, Object> getParameters()
    {
        return parse();
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
     * Interpret the given CBOR data item as an unprotected header and build
     * an instance of {@link COSEUnprotectedHeader} from it.
     *
     * @param header
     *         A CBOR data item that represents a protected header. It must be
     *         a CBOR map.
     *
     * @return
     *         An unprotected header.
     *
     * @throws COSEException
     *         The given CBOR data item does not conform to the requirements
     *         of unprotected header.
     */
    public static COSEUnprotectedHeader build(CBORItem header) throws COSEException
    {
        // If the unprotected header is not a CBOR map.
        if (!(header instanceof CBORPairList))
        {
            throw new COSEException(
                    "An unprotected header must be a CBOR map.");
        }

        // The key-value pairs in the header.
        List<? extends CBORPair> pairs = ((CBORPairList)header).getPairs();

        try
        {
            // Create an unprotected header with the key-value pairs.
            return new COSEUnprotectedHeader(pairs);
        }
        catch (Exception cause)
        {
            // Validation on the pairs failed.
            throw new COSEException(cause.getMessage(), cause);
        }
    }


    /**
     * Build a {@link COSEUnprotectedHeader} instance from the given map.
     *
     * @param map
     *         A map containing header parameters.
     *
     * @return
     *         A {@link COSEUnprotectedHeader} instance built from the given map.
     */
    public static COSEUnprotectedHeader build(Map<Object, Object> map)
    {
        if (map == null)
        {
            map = Collections.emptyMap();
        }

        // Convert the Java map into a CBOR map.
        CBORPairList pairList = (CBORPairList)new CBORizer().cborizeMap(map);

        // Add comments to the header parameters.
        COSEHeaderBuilder.addHeaderComments(pairList);

        return new COSEUnprotectedHeader(pairList.getPairs());
    }
}

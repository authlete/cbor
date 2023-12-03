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


import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.authlete.cbor.CBORByteArray;
import com.authlete.cbor.CBORInteger;
import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORItemList;
import com.authlete.cbor.CBORPair;
import com.authlete.cbor.CBORPairList;
import com.authlete.cose.constants.COSEAlgorithms;
import com.authlete.cose.constants.COSEHeaderParameters;


/**
 * The base class for {@link COSEProtectedHeaderBuilder} and
 * {@link COSEUnprotectedHeaderBuilder}.
 *
 * @param <TBuilder>
 *         The type of the subclass. Either {@link COSEProtectedHeaderBuilder}
 *         or {@link COSEUnprotectedHeaderBuilder}.
 *
 * @param <THeader>
 *         The type of the header to build. Either {@link COSEProtectedHeader}
 *         or {@link COSEUnprotectedHeader}.
 *
 * @since 1.1
 *
 * @see <a href="https://www.iana.org/assignments/cose/cose.xhtml#header-parameters"
 *      >IANA: COSE Header Parameters</a>
 */
public abstract class COSEHeaderBuilder
<TBuilder extends COSEHeaderBuilder<TBuilder, THeader>, THeader>
{
    private final Map<Object, Object> map = new LinkedHashMap<>();


    /**
     * Set a parameter.
     *
     * @param key
     *         The key of the parameter to set.
     *
     * @param value
     *         The value of the parameter to set.
     *
     * @return
     *         {@code this} object.
     */
    @SuppressWarnings("unchecked")
    public TBuilder put(Object key, Object value)
    {
        map.put(key, value);

        return (TBuilder)this;
    }


    /**
     * Set the "alg (1)" parameter.
     *
     * @param alg
     *         An identifier assigned to an algorithm.
     *
     * @return
     *         {@code this} object.
     *
     * @see <a href="https://www.iana.org/assignments/cose/cose.xhtml#algorithms"
     *      >IANA: COSE Algorithms</a>
     */
    public TBuilder alg(int alg)
    {
        return put(COSEHeaderParameters.ALG, alg);
    }


    /**
     * Set the "alg (1)" parameter.
     *
     * @param alg
     *         An algorithm name.
     *
     * @return
     *         {@code this} object.
     *
     * @see <a href="https://www.iana.org/assignments/cose/cose.xhtml#algorithms"
     *      >IANA: COSE Algorithms</a>
     */
    public TBuilder alg(String alg)
    {
        return put(COSEHeaderParameters.ALG, alg);
    }


    /**
     * Set the "alg (1)" parameter.
     *
     * @param alg
     *         The identifier or the name of an algorithm.
     *
     * @return
     *         {@code this} object.
     *
     * @see <a href="https://www.iana.org/assignments/cose/cose.xhtml#algorithms"
     *      >IANA: COSE Algorithms</a>
     *
     * @since 1.5
     */
    public TBuilder alg(Object alg)
    {
        return put(COSEHeaderParameters.ALG, alg);
    }


    /**
     * Set the "crit (2)" parameter.
     *
     * @param labels
     *         A list of labels. Elements must be integers and/or strings.
     *
     * @return
     *         {@code this} object.
     */
    public TBuilder crit(List<Object> labels)
    {
        return put(COSEHeaderParameters.CRIT, labels);
    }


    /**
     * Set the "content type (3)" parameter.
     *
     * @param contentType
     *         The content type.
     *
     * @return
     *         {@code this} object.
     */
    public TBuilder contentType(String contentType)
    {
        return put(COSEHeaderParameters.CONTENT_TYPE, contentType);
    }


    /**
     * Set the "content type (3)" parameter.
     *
     * @param contentType
     *         The content type.
     *
     * @return
     *         {@code this} object.
     */
    public TBuilder contentType(int contentType)
    {
        return put(COSEHeaderParameters.CONTENT_TYPE, contentType);
    }


    /**
     * Set the "kid (4)" parameter.
     *
     * @param kid
     *         The key ID.
     *
     * @return
     *         {@code this} object.
     */
    public TBuilder kid(byte[] kid)
    {
        return put(COSEHeaderParameters.KID, kid);
    }


    /**
     * Set the "kid (4)" parameter.
     *
     * @param kid
     *         The key ID. Its UTF-8 byte sequence is used.
     *
     * @return
     *         {@code this} object.
     */
    public TBuilder kid(String kid)
    {
        byte[] bytes = (kid != null) ? kid.getBytes(StandardCharsets.UTF_8): null;

        return kid(bytes);
    }


    /**
     * Set the "IV (5)" parameter.
     *
     * @param iv
     *         The full initialization vector.
     *
     * @return
     *         {@code this} object.
     */
    public TBuilder iv(byte[] iv)
    {
        return put(COSEHeaderParameters.IV, iv);
    }


    /**
     * Set the "Partial IV (6)" parameter.
     *
     * @param partialIv
     *         The partial initialization vector.
     *
     * @return
     *         {@code this} object.
     */
    public TBuilder partialIv(byte[] partialIv)
    {
        return put(COSEHeaderParameters.PARTIAL_IV, partialIv);
    }


    /**
     * Set the "x5chain (33)" parameter.
     *
     * @param chain
     *         The certificate chain.
     *
     * @return
     *         {@code this} object.
     * @throws CertificateEncodingException
     *
     * @since 1.5
     *
     * @see <a href="https://www.rfc-editor.org/rfc/rfc9360.html"
     *      >RFC 9360 CBOR Object Signing and Encryption (COSE):
     *       Header Parameters for Carrying and Referencing X.509 Certificates</a>
     */
    public TBuilder x5chain(List<X509Certificate> chain) throws CertificateEncodingException
    {
        // RFC 9360, 2. X.509 COSE Header Parameters, x5chain
        //
        //   This header parameter allows for a single X.509 certificate or
        //   a chain of X.509 certificates to be carried in the message.
        //
        //     * If a single certificate is conveyed, it is placed in a CBOR
        //       byte string.
        //
        //     * If multiple certificates are conveyed, a CBOR array of byte
        //       strings is used, with each certificate being in its own byte
        //       string.
        //

        List<CBORByteArray> ders = new ArrayList<>();

        for (X509Certificate cert : chain)
        {
            // ASN.1 DER
            ders.add(new CBORByteArray(cert.getEncoded()));
        }

        // If the given chain contains multiple certificates, the value of
        // the 'x5chain' parameter becomes a CBOR array. Otherwise, the
        // value becomes a single CBOR byte string.
        CBORItem x5chain = (2 <= ders.size()) ? new CBORItemList(ders) : ders.get(0);

        return put(COSEHeaderParameters.X5CHAIN, x5chain);
    }


    /**
     * Build a header instance.
     *
     * @return
     *         An instance of either {@link COSEProtectedHeader} or
     *         {@link COSEUnprotectedHeader}.
     */
    public THeader build()
    {
        return build(map);
    }


    abstract THeader build(Map<Object, Object> map);


    static void addHeaderComments(CBORPairList headerParameters)
    {
        if (headerParameters == null)
        {
            return;
        }

        List<? extends CBORPair> pairs = headerParameters.getPairs();

        if (pairs == null)
        {
            return;
        }

        for (CBORPair pair : pairs)
        {
            addHeaderComment(pair);
        }
    }


    private static void addHeaderComment(CBORPair pair)
    {
        if (!(pair.getKey() instanceof CBORInteger))
        {
            // The header parameter is not specified by an assigned identifier.
            return;
        }

        // The identifier assigned to the header parameter.
        int identifier = ((CBORInteger)pair.getKey()).getValue();

        // Get the name of the header parameter.
        String name = COSEHeaderParameters.getNameByValue(identifier);

        if (name != null)
        {
            // Set the name of the header parameter as a comment about the key.
            pair.setKeyComment(name);
        }

        // If the header parameter is 'alg'.
        if (identifier == COSEHeaderParameters.ALG)
        {
            // The name of the algorithm.
            String algorithmName = getAlgorithmName(pair.getValue());

            if (algorithmName != null)
            {
                // Set the name of the algorithm as a comment about the value.
                pair.setValueComment(algorithmName);
            }
        }
    }


    private static String getAlgorithmName(CBORItem algorithm)
    {
        if (!(algorithm instanceof CBORInteger))
        {
            // The algorithm is not specified by an assigned identifier.
            return null;
        }

        // The identifier assigned to the algorithm.
        int identifier = ((CBORInteger)algorithm).getValue();

        // Get the name of the algorithm.
        return COSEAlgorithms.getNameByValue(identifier);
    }
}

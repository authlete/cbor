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
package com.authlete.cose.constants;


import java.util.LinkedHashMap;
import java.util.Map;


/**
 * COSE Header Parameters
 *
 * <blockquote>
 * <table border="1" cellpadding="5" style="border-collapse: collapse;">
 *
 *   <tr bgcolor="orange">
 *     <th>Name</th>
 *     <th>Label</th>
 *     <th>Value Type</th>
 *     <th>Description</th>
 *     <th>Reference</th>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #ALG alg}</td>
 *     <td>1</td>
 *     <td>int / tstr</td>
 *     <td>Cryptographic algorithm to use</td>
 *     <td><a href="https://www.rfc-editor.org/rfc/rfc9052.html">RFC 9052</a></td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #CRIT crit}</td>
 *     <td>2</td>
 *     <td>[+ label]</td>
 *     <td>Critical headers to be understood</td>
 *     <td><a href="https://www.rfc-editor.org/rfc/rfc9052.html">RFC 9052</a></td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #CONTENT_TYPE content type}</td>
 *     <td>3</td>
 *     <td>tstr / uint</td>
 *     <td>Content type of the payload</td>
 *     <td><a href="https://www.rfc-editor.org/rfc/rfc9052.html">RFC 9052</a></td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #KID kid}</td>
 *     <td>4</td>
 *     <td>bstr</td>
 *     <td>Key identifier</td>
 *     <td><a href="https://www.rfc-editor.org/rfc/rfc9052.html">RFC 9052</a></td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #IV IV}</td>
 *     <td>5</td>
 *     <td>bstr</td>
 *     <td>Full Initialization Vector</td>
 *     <td><a href="https://www.rfc-editor.org/rfc/rfc9052.html">RFC 9052</a></td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #PARTIAL_IV Partial IV}</td>
 *     <td>6</td>
 *     <td>bstr</td>
 *     <td>Partial Initialization Vector</td>
 *     <td><a href="https://www.rfc-editor.org/rfc/rfc9052.html">RFC 9052</a></td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #X5CHAIN x5chain}</td>
 *     <td>33</td>
 *     <td>COSE_X509</td>
 *     <td>An ordered chain of X.509 certificates</td>
 *     <td><a href="https://www.rfc-editor.org/rfc/rfc9360.html">RFC 9360</a></td>
 *   </tr>
 *
 * </table>
 * </blockquote>
 *
 * @since 1.1
 *
 * @see <a href="https://www.iana.org/assignments/cose/cose.xhtml#header-parameters"
 *      >IANA: COSE Header Parameters</a>
 */
public final class COSEHeaderParameters
{
    /**
     * alg (1)
     */
    public static final int ALG = 1;

    /**
     * crit (2)
     */
    public static final int CRIT = 2;

    /**
     * content type (3)
     */
    public static final int CONTENT_TYPE = 3;

    /**
     * kid (4)
     */
    public static final int KID = 4;

    /**
     * IV (5)
     */
    public static final int IV = 5;

    /**
     * Partial IV (6)
     */
    public static final int PARTIAL_IV = 6;

    /**
     * x5chain (33)
     *
     * @see <a href="https://www.rfc-editor.org/rfc/rfc9360.html"
     *      >RFC 9360 CBOR Object Signing and Encryption (COSE): Header Parameters for Carrying and Referencing X.509 Certificates</a>
     *
     * @since 1.2
     */
    public static final int X5CHAIN = 33;


    private static final int[] values = {
            ALG, CRIT, CONTENT_TYPE, KID, IV, PARTIAL_IV, X5CHAIN,
    };


    private static final String[] names = {
            "alg", "crit", "content type", "kid", "IV", "Partial IV", "x5chain",
    };


    private static final Map<Integer, String> valueToNameMap = createValueToNameMap();


    private static Map<Integer, String> createValueToNameMap()
    {
        Map<Integer, String> map = new LinkedHashMap<>();

        for (int i = 0; i < values.length; i++)
        {
            map.put(values[i], names[i]);
        }

        return map;
    }


    /**
     * Get the name of the header parameter to which the integer identifier
     * has been assigned to.
     *
     * @param value
     *         An integer identifier assigned to a header parameter.
     *
     * @return
     *         The name of the header parameter to which the integer identifier
     *         has been assigned to. If the given identifier is not recognized,
     *         {@code null} is returned.
     *
     * @since 1.5
     */
    public static String getNameByValue(int value)
    {
        return valueToNameMap.getOrDefault(value, null);
    }


    private COSEHeaderParameters()
    {
    }
}

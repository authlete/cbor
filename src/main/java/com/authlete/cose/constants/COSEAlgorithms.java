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
 * COSE Algorithms
 *
 * <blockquote>
 * <table border="1" cellpadding="5" style="border-collapse: collapse;">
 *
 *   <tr bgcolor="orange">
 *     <th>Name</th>
 *     <th>Value</th>
 *     <th>Description</th>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #PS512}</td>
 *     <td>-39</td>
 *     <td>RSASSA-PSS w/ SHA-512</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #PS384}</td>
 *     <td>-38</td>
 *     <td>RSASSA-PSS w/ SHA-384</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #PS256}</td>
 *     <td>-37</td>
 *     <td>RSASSA-PSS w/ SHA-256</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #ES512}</td>
 *     <td>-36</td>
 *     <td>ECDSA w/ SHA-512</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #ES384}</td>
 *     <td>-35</td>
 *     <td>ECDSA w/ SHA-384</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #EdDSA}</td>
 *     <td>-8</td>
 *     <td>EdDSA</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #ES256}</td>
 *     <td>-7</td>
 *     <td>ECDSA w/ SHA-256</td>
 *   </tr>
 *
 * </table>
 * </blockquote>
 *
 * @since 1.1
 *
 * @see <a href="https://www.iana.org/assignments/cose/cose.xhtml#algorithms"
 *      >IANA: COSE Algorithms</a>
 */
public final class COSEAlgorithms
{
    /**
     * PS512 (-39); RSASSA-PSS w/ SHA-512
     */
    public static final int PS512 = -39;

    /**
     * PS384 (-38); RSASSA-PSS w/ SHA-384
     */
    public static final int PS384 = -38;

    /**
     * PS256 (-37); RSASSA-PSS w/ SHA-256
     */
    public static final int PS256 = -37;

    /**
     * ES512 (-36); ECDSA w/ SHA-512
     */
    public static final int ES512 = -36;

    /**
     * ES384 (-35); ECDSA w/ SHA-384
     */
    public static final int ES384 = -35;

    /**
     * EdDSA (-8); EdDSA
     */
    public static final int EdDSA = -8;

    /**
     * ES256 (-7); ECDSA w/ SHA-256
     */
    public static final int ES256 = -7;


    private static final int[] values = {
            PS512, PS384, PS256, ES512, ES384, EdDSA, ES256,
    };


    private static final String[] names = {
            "PS512", "PS384", "PS256", "ES512", "ES384", "EdDSA", "ES256",
    };


    private static final Map<Integer, String> valueToNameMap = createValueToNameMap();
    private static final Map<String, Integer> nameToValueMap = createNameToValueMap();


    private static Map<Integer, String> createValueToNameMap()
    {
        Map<Integer, String> map = new LinkedHashMap<>();

        for (int i = 0; i < values.length; i++)
        {
            map.put(values[i], names[i]);
        }

        return map;
    }


    private static Map<String, Integer> createNameToValueMap()
    {
        Map<String, Integer> map = new LinkedHashMap<>();

        for (int i = 0; i < names.length; i++)
        {
            map.put(names[i], values[i]);
        }

        return map;
    }


    /**
     * Get the integer identifier assigned to the algorithm.
     *
     * @param name
     *         An algorithm name such as {@code "ES256"}.
     *         If {@code null} is given, 0 is returned.
     *
     * @return
     *         The integer identifier assigned to the algorithm.
     *         If the given name is not recognized, 0 is returned.
     */
    public static int getValueByName(String name)
    {
        if (name == null)
        {
            return 0;
        }

        return nameToValueMap.getOrDefault(name, 0);
    }


    /**
     * Get the name of the algorithm to which the integer identifier
     * has been assigned to.
     *
     * @param value
     *         An integer identifier assigned to an algorithm.
     *
     * @return
     *         The name of the algorithm to which the integer identifier
     *         has been assigned to. If the given identifier is not
     *         recognized, {@code null} is returned.
     */
    public static String getNameByValue(int value)
    {
        return valueToNameMap.getOrDefault(value, null);
    }


    private COSEAlgorithms()
    {
    }
}

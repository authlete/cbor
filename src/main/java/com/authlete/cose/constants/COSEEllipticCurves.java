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
 * COSE Elliptic Curves
 *
 * <blockquote>
 * <table border="1" cellpadding="5" style="border-collapse: collapse;">
 *
 *   <tr bgcolor="orange">
 *     <th>Name</th>
 *     <th>Value</th>
 *     <th>Key Type</th>
 *     <th>Description</th>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #P_256 P-256}</td>
 *     <td>1</td>
 *     <td>{@link COSEKeyTypes#EC2 EC2}</td>
 *     <td>NIST P-256 also known as secp256r1</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #P_384 P-384}</td>
 *     <td>2</td>
 *     <td>{@link COSEKeyTypes#EC2 EC2}</td>
 *     <td>NIST P-384 also known as secp384r1</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #P_521 P-521}</td>
 *     <td>3</td>
 *     <td>{@link COSEKeyTypes#EC2 EC2}</td>
 *     <td>NIST P-521 also known as secp521r1</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #X25519 X25519}</td>
 *     <td>4</td>
 *     <td>{@link COSEKeyTypes#OKP OKP}</td>
 *     <td>X25519 for use w/ ECDH only</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #X448 X448}</td>
 *     <td>5</td>
 *     <td>{@link COSEKeyTypes#OKP OKP}</td>
 *     <td>X448 for use w/ ECDH only</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #Ed25519 Ed25519}</td>
 *     <td>6</td>
 *     <td>{@link COSEKeyTypes#OKP OKP}</td>
 *     <td>Ed25519 for use w/ EdDSA only</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #Ed448 Ed448}</td>
 *     <td>7</td>
 *     <td>{@link COSEKeyTypes#OKP OKP}</td>
 *     <td>Ed448 for use w/ EdDSA only</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #SECP256K1 secp256k1}</td>
 *     <td>8</td>
 *     <td>{@link COSEKeyTypes#EC2 EC2}</td>
 *     <td>Ed448 for use w/ EdDSA only</td>
 *   </tr>
 *
 * </table>
 * </blockquote>
 *
 * @since 1.1
 *
 * @see <a href="https://www.iana.org/assignments/cose/cose.xhtml#elliptic-curves"
 *      >IANA: COSE Elliptic Curves</a>
 */
public final class COSEEllipticCurves
{
    /** P-256 (1) */
    public static final int P_256 = 1;

    /** P-384 (2) */
    public static final int P_384 = 2;

    /** P-521 (3) */
    public static final int P_521 = 3;

    /** X25519 (4) */
    public static final int X25519 = 4;

    /** X448 (5) */
    public static final int X448 = 5;

    /** Ed25519 (6) */
    public static final int Ed25519 = 6;

    /** Ed448 (7) */
    public static final int Ed448 = 7;

    /** secp256k1 (8) */
    public static final int SECP256K1 = 8;

    /**
     * "P-256"
     *
     * @since 1.5
     */
    public static final String P_256_NAME = "P-256";

    /**
     * "P-384"
     *
     * @since 1.5
     */
    public static final String P_384_NAME = "P-384";

    /**
     * "P-521"
     *
     * @since 1.5
     */
    public static final String P_521_NAME = "P-521";

    /**
     * "X25519"
     *
     * @since 1.5
     */
    public static final String X25519_NAME = "X25519";

    /**
     * "X448"
     *
     * @since 1.5
     */
    public static final String X448_NAME = "X448";

    /**
     * "Ed25519"
     *
     * @since 1.5
     */
    public static final String Ed25519_NAME = "Ed25519";

    /**
     * "Ed448"
     *
     * @since 1.5
     */
    public static final String Ed448_NAME = "Ed448";

    /**
     * "secp256k1"
     *
     * @since 1.5
     */
    public static final String SECP256K1_NAME = "secp256k1";


    private static final int[] values = {
            P_256, P_384, P_521, X25519, X448, Ed25519, Ed448, SECP256K1,
    };


    private static final String[] names = {
            P_256_NAME, P_384_NAME, P_521_NAME,
            X25519_NAME, X448_NAME, Ed25519_NAME, Ed448_NAME, SECP256K1_NAME
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
     * Get the integer identifier assigned to the elliptic curve.
     *
     * @param name
     *         An elliptic curve name such as {@code "P-256"}.
     *         If {@code null} is given, 0 is returned.
     *
     * @return
     *         The integer identifier assigned to the elliptic curve.
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
     * Get the name of the elliptic curve to which the integer identifier
     * has been assigned to.
     *
     * @param value
     *         An integer identifier assigned to an elliptic curve.
     *
     * @return
     *         The name of the elliptic curve to which the integer identifier
     *         has been assigned to. If the given identifier is not recognized,
     *         {@code null} is returned.
     */
    public static String getNameByValue(int value)
    {
        return valueToNameMap.getOrDefault(value, null);
    }


    private COSEEllipticCurves()
    {
    }
}

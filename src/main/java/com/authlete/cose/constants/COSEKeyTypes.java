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


/**
 * COSE Key Types
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
 *     <td>{@link #OKP}</td>
 *     <td>1</td>
 *     <td>Octet Key Pair</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #EC2}</td>
 *     <td>2</td>
 *     <td>Elliptic Curve Keys w/ x- and y-coordinate pair</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #RSA}</td>
 *     <td>3</td>
 *     <td>RSA Key</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #SYMMETRIC Symmetric}</td>
 *     <td>4</td>
 *     <td>Symmetric Keys</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #HSS_LMS HSS-LMS}</td>
 *     <td>5</td>
 *     <td>Public key for HSS/LMS hash-based digital signature</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #WALNUT_DSA WalnutDSA}</td>
 *     <td>6</td>
 *     <td>WalnutDSA public key</td>
 *   </tr>
 *
 * </table>
 * </blockquote>
 *
 * @since 1.1
 *
 * @see <a href="https://www.iana.org/assignments/cose/cose.xhtml#key-type"
 *      >IANA: COSE Key Types</a>
 */
public final class COSEKeyTypes
{
    /** OKP (1) */
    public static final int OKP = 1;

    /** EC2 (2) */
    public static final int EC2 = 2;

    /** RSA (3) */
    public static final int RSA = 3;

    /** Symmetric (4) */
    public static final int SYMMETRIC = 4;

    /** HSS-LMS (5) */
    public static final int HSS_LMS = 5;

    /** WalnutDSA (6) */
    public static final int WALNUT_DSA = 6;


    private COSEKeyTypes()
    {
    }
}

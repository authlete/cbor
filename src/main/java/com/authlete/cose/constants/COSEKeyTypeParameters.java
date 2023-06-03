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
 * COSE Key Type Parameters
 *
 * <blockquote>
 * <table border="1" cellpadding="5" style="border-collapse: collapse;">
 *
 *   <tr bgcolor="orange">
 *     <th>Key Type</th>
 *     <th>Name</th>
 *     <th>Label</th>
 *     <th>CBOR Type</th>
 *     <th>Description</th>
 *   </tr>
 *
 *   <tr>
 *     <td rowspan="3">{@link COSEKeyTypes#OKP OKP}</td>
 *     <td>{@link #OKP_CRV crv}</td>
 *     <td>-1</td>
 *     <td>int / tstr</td>
 *     <td>EC identifier -- Taken from the "COSE Elliptic Curves" registry</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #OKP_X x}</td>
 *     <td>-2</td>
 *     <td>bstr</td>
 *     <td>Public key</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #OKP_D d}</td>
 *     <td>-4</td>
 *     <td>bstr</td>
 *     <td>Private key</td>
 *   </tr>
 *
 *   <tr>
 *     <td rowspan="4">{@link COSEKeyTypes#EC2 EC2}</td>
 *     <td>{@link #EC2_CRV crv}</td>
 *     <td>-1</td>
 *     <td>int / tstr</td>
 *     <td>EC identifier -- Taken from the "COSE Elliptic Curves" registry</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #EC2_X x}</td>
 *     <td>-2</td>
 *     <td>bstr</td>
 *     <td>x-coordinate</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #EC2_Y y}</td>
 *     <td>-3</td>
 *     <td>bstr / bool</td>
 *     <td>y-coordinate</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #EC2_D d}</td>
 *     <td>-4</td>
 *     <td>bstr</td>
 *     <td>Private key</td>
 *   </tr>
 *
 *   <tr>
 *     <td rowspan="12">{@link COSEKeyTypes#RSA RSA}</td>
 *     <td>{@link #RSA_N n}</td>
 *     <td>-1</td>
 *     <td>bstr</td>
 *     <td>The RSA modulus n</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #RSA_E e}</td>
 *     <td>-2</td>
 *     <td>bstr</td>
 *     <td>The RSA public exponent e</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #RSA_D d}</td>
 *     <td>-3</td>
 *     <td>bstr</td>
 *     <td>The RSA private exponent d</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #RSA_P p}</td>
 *     <td>-4</td>
 *     <td>bstr</td>
 *     <td>The prime factor p of n</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #RSA_Q q}</td>
 *     <td>-5</td>
 *     <td>bstr</td>
 *     <td>The prime factor q of n</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #RSA_DP dP}</td>
 *     <td>-6</td>
 *     <td>bstr</td>
 *     <td>dP is d mod (p - 1)</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #RSA_DQ dQ}</td>
 *     <td>-7</td>
 *     <td>bstr</td>
 *     <td>dQ is d mod (q - 1)</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #RSA_QINV qInv}</td>
 *     <td>-8</td>
 *     <td>bstr</td>
 *     <td>qInv is the CRT coefficient q^(-1) mod p</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #RSA_OTHER other}</td>
 *     <td>-9</td>
 *     <td>array</td>
 *     <td>Other prime infos, an array</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #RSA_R_I r_i}</td>
 *     <td>-10</td>
 *     <td>bstr</td>
 *     <td>A prime factor r_i of n, where i >= 3</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #RSA_D_I d_i}</td>
 *     <td>-11</td>
 *     <td>bstr</td>
 *     <td>d_i = d mod (r_i - 1)</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #RSA_T_I t_i}</td>
 *     <td>-12</td>
 *     <td>bstr</td>
 *     <td>The CRT coefficient t_i = (r_1 * r_2 * ... * r_(i-1))^(-1) mod r_i</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link COSEKeyTypes#SYMMETRIC Symmetric}</td>
 *     <td>{@link #SYMMETRIC_K k}</td>
 *     <td>-1</td>
 *     <td>bstr</td>
 *     <td>Key value</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link COSEKeyTypes#HSS_LMS HSS-LMS}</td>
 *     <td>{@link #HSS_LMS_PUB pub}</td>
 *     <td>-1</td>
 *     <td>bstr</td>
 *     <td>Public key for HSS/LMS hash-based digital signature</td>
 *   </tr>
 *
 *   <tr>
 *     <td rowspan="6">{@link COSEKeyTypes#WALNUT_DSA WalnutDSA}</td>
 *     <td>{@link #WALNUT_DSA_N N}</td>
 *     <td>-1</td>
 *     <td>unit</td>
 *     <td>Group and Matrix (NxN) size</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #WALNUT_DSA_Q q}</td>
 *     <td>-2</td>
 *     <td>unit</td>
 *     <td>Finite field F_q</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #WALNUT_DSA_T_VALUES t-values}</td>
 *     <td>-3</td>
 *     <td>array (of unit)</td>
 *     <td>List of T-values, entries in F_q</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #WALNUT_DSA_MATRIX_1 matrix 1}</td>
 *     <td>-4</td>
 *     <td>array (of array of unit)</td>
 *     <td>NxN Matrix of entries in F_q in column-major form</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #WALNUT_DSA_PERMUTATION_1 permutation 1}</td>
 *     <td>-5</td>
 *     <td>array (of unit)</td>
 *     <td>Permutation associated with matrix 1</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #WALNUT_DSA_MATRIX_2 matrix 2}</td>
 *     <td>-6</td>
 *     <td>array (of array of unit)</td>
 *     <td>NxN Matrix of entries in F_q in column-major form</td>
 *   </tr>
 *
 * </table>
 * </blockquote>
 *
 * @since 1.1
 *
 * @see <a href="https://www.iana.org/assignments/cose/cose.xhtml#key-type-parameters"
 *      >IANA: COSE Key Type Parameters</a>
 */
public final class COSEKeyTypeParameters
{
    /** OKP crv (-1) */
    public static final int OKP_CRV = -1;

    /** OKP x (-2) */
    public static final int OKP_X = -2;

    /** OKP d (-4) */
    public static final int OKP_D = -4;

    /** EC2 crv (-1) */
    public static final int EC2_CRV = -1;

    /** EC2 x (-2) */
    public static final int EC2_X = -2;

    /** EC2 y (-3) */
    public static final int EC2_Y = -3;

    /** EC2 d (-4) */
    public static final int EC2_D = -4;

    /** RSA n (-1) */
    public static final int RSA_N = -1;

    /** RSA e (-2) */
    public static final int RSA_E = -2;

    /** RSA d (-3) */
    public static final int RSA_D = -3;

    /** RSA p (-4) */
    public static final int RSA_P = -4;

    /** RSA q (-5) */
    public static final int RSA_Q = -5;

    /** RSA dP (-6) */
    public static final int RSA_DP = -6;

    /** RSA dQ (-7) */
    public static final int RSA_DQ = -7;

    /** RSA qInv (-8) */
    public static final int RSA_QINV = -8;

    /** RSA other (-9) */
    public static final int RSA_OTHER = -9;

    /** RSA r_i (-10) */
    public static final int RSA_R_I = -10;

    /** RSA d_i (-11) */
    public static final int RSA_D_I = -11;

    /** RSA t_i (-12) */
    public static final int RSA_T_I = -12;

    /** Symmetric k (-1) */
    public static final int SYMMETRIC_K = -1;

    /** HSS-LMS pub (-1) */
    public static final int HSS_LMS_PUB = -1;

    /** WalnutDSA N (-1) */
    public static final int WALNUT_DSA_N = -1;

    /** WalnutDSA q (-2) */
    public static final int WALNUT_DSA_Q = -2;

    /** WalnutDSA t-values (-3) */
    public static final int WALNUT_DSA_T_VALUES = -3;

    /** WalnutDSA matrix 1 (-4) */
    public static final int WALNUT_DSA_MATRIX_1 = -4;

    /** WalnutDSA permutation 1 (-5) */
    public static final int WALNUT_DSA_PERMUTATION_1 = -5;

    /** WalnutDSA matrix 2 (-6) */
    public static final int WALNUT_DSA_MATRIX_2 = -6;


    private COSEKeyTypeParameters()
    {
    }
}

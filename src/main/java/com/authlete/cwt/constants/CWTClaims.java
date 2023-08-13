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
package com.authlete.cwt.constants;


/**
 * CWT Claims
 *
 * <blockquote>
 * <table border="1" cellpadding="5" style="border-collapse: collapse;">
 *
 *   <tr bgcolor="orange">
 *     <th>Claim Name</th>
 *     <th>Claim Key</th>
 *     <th>Claim Value Type</th>
 *     <th>Claim Description</th>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #ISS iss}</td>
 *     <td>1</td>
 *     <td>text string</td>
 *     <td>Issuer</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #SUB sub}</td>
 *     <td>2</td>
 *     <td>text string</td>
 *     <td>Subject</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #AUD aud}</td>
 *     <td>3</td>
 *     <td>text string</td>
 *     <td>Audience</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #EXP exp}</td>
 *     <td>4</td>
 *     <td>integer or floating-point number</td>
 *     <td>Expiration Time</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #NBF nbf}</td>
 *     <td>5</td>
 *     <td>integer or floating-point number</td>
 *     <td>Not Before</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #IAT iat}</td>
 *     <td>6</td>
 *     <td>integer or floating-point number</td>
 *     <td>Issued At</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #CTI cti}</td>
 *     <td>7</td>
 *     <td>byte string</td>
 *     <td>CWT ID</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #CNF cnf}</td>
 *     <td>8</td>
 *     <td>map</td>
 *     <td>Confirmation</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #SCOPE scope}</td>
 *     <td>9</td>
 *     <td>byte string or text string</td>
 *     <td>The scope of an access token, as defined in [RFC6749].</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #NONCE Nonce}</td>
 *     <td>10</td>
 *     <td>byte string</td>
 *     <td>Nonce (TEMPORARY - registered 2022-03-23, extension registered 2023-02-13, expires 2024-03-23)</td>
 *   </tr>
 * </table>
 * </blockquote>
 *
 * @since 1.4
 *
 * @see <a href="https://www.iana.org/assignments/cwt/cwt.xhtml"
 *      >IANA: CBOR Web Token (CWT) Claims</a>
 */
public class CWTClaims
{
    /**
     * iss (1); Issuer
     */
    public static final int ISS = 1;

    /**
     * sub (2); Subject
     */
    public static final int SUB = 2;

    /**
     * aud (3); Audience
     */
    public static final int AUD = 3;

    /**
     * exp (4); Expiration Time
     */
    public static final int EXP = 4;

    /**
     * nbf (5); Not Before
     */
    public static final int NBF = 5;

    /**
     * iat (6); Issued At
     */
    public static final int IAT = 6;

    /**
     * cti (7); CWT ID
     */
    public static final int CTI = 7;

    /**
     * cnf (8); Confirmation
     */
    public static final int CNF = 8;

    /**
     * scope (9); The scope of an access token, as defined in [RFC6749].
     */
    public static final int SCOPE = 9;

    /**
     * Nonce (10); Nonce
     *
     * <p>
     * (TEMPORARY - registered 2022-03-23, extension registered 2023-02-13, expires 2024-03-23)
     * </p>
     */
    public static final int NONCE = 10;

    private CWTClaims()
    {
    }
}

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


import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import com.authlete.cwt.constants.CWTClaims;


/**
 * A utility to build an instance of the {@link CWTClaimsSet} class.
 *
 * @since 1.4
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8392.html"
 *      >RFC 8392 CBOR Web Token (CWT)</a>
 *
 * @see <a href="https://www.iana.org/assignments/cwt/cwt.xhtml"
 *      >IANA: CBOR Web Token (CWT) Claims</a>
 */
public class CWTClaimsSetBuilder
{
    private final Map<Object, Object> map = new LinkedHashMap<>();


    /**
     * Add a claim.
     *
     * @param key
     *         The key of the claim. A pre-defined numeric claim key
     *         (e.g. {@link CWTClaims#ISS}) or a string name.
     *
     * @param value
     *         The value of the claim.
     *
     * @return
     *         {@code this} object.
     */
    public CWTClaimsSetBuilder put(Object key, Object value)
    {
        map.put(key, value);

        return this;
    }


    private CWTClaimsSetBuilder putDate(Object key, Date date)
    {
        return put(key, date.getTime() / 1000L);
    }


    private CWTClaimsSetBuilder putByteString(Object key, String string)
    {
        return put(key, string.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * Set the "iss (1)" claim.
     *
     * @param issuer
     *         The value of the {@code iss} claim.
     *
     * @return
     *         {@code this} object.
     */
    public CWTClaimsSetBuilder iss(String issuer)
    {
        return put(CWTClaims.ISS, issuer);
    }


    /**
     * Set the "sub (2)" claim.
     *
     * @param subject
     *         The value of the {@code sub} claim.
     *
     * @return
     *         {@code this} object.
     */
    public CWTClaimsSetBuilder sub(String subject)
    {
        return put(CWTClaims.SUB, subject);
    }


    /**
     * Set the "aud (3)" claim.
     *
     * @param audience
     *         The value of the {@code aud} claim.
     *
     * @return
     *         {@code this} object.
     */
    public CWTClaimsSetBuilder aud(String audience)
    {
        return put(CWTClaims.AUD, audience);
    }


    /**
     * Set the "exp (4)" claim.
     *
     * @param expirationTime
     *         The value of the {@code exp} claim.
     *
     * @return
     *         {@code this} object.
     */
    public CWTClaimsSetBuilder exp(Date expirationTime)
    {
        return putDate(CWTClaims.EXP, expirationTime);
    }


    /**
     * Set the "exp (4)" claim.
     *
     * @param expirationTime
     *         The value of the {@code exp} claim.
     *         A time expressed in seconds elapsed since the Unix epoch.
     *
     * @return
     *         {@code this} object.
     */
    public CWTClaimsSetBuilder exp(long expirationTime)
    {
        return put(CWTClaims.EXP, expirationTime);
    }


    /**
     * Set the "nbf (5)" claim.
     *
     * @param notBefore
     *         The value of the {@code nbf} claim.
     *
     * @return
     *         {@code this} object.
     */
    public CWTClaimsSetBuilder nbf(Date notBefore)
    {
        return putDate(CWTClaims.NBF, notBefore);
    }


    /**
     * Set the "nbf (5)" claim.
     *
     * @param notBefore
     *         The value of the {@code nbf} claim.
     *         A time expressed in seconds elapsed since the Unix epoch.
     *
     * @return
     *         {@code this} object.
     */
    public CWTClaimsSetBuilder nbf(long notBefore)
    {
        return put(CWTClaims.NBF, notBefore);
    }


    /**
     * Set the "iat (6)" claim.
     *
     * @param issuedAt
     *         The value of the {@code iat} claim.
     *
     * @return
     *         {@code this} object.
     */
    public CWTClaimsSetBuilder iat(Date issuedAt)
    {
        return putDate(CWTClaims.IAT, issuedAt);
    }


    /**
     * Set the "iat (6)" claim.
     *
     * @param issuedAt
     *         The value of the {@code iat} claim.
     *         A time expressed in seconds elapsed since the Unix epoch.
     *
     * @return
     *         {@code this} object.
     */
    public CWTClaimsSetBuilder iat(long issuedAt)
    {
        return put(CWTClaims.IAT, issuedAt);
    }


    /**
     * Set the "cti (7)" claim.
     *
     * @param identifier
     *         The value of the {@code cti} claim (CWT ID).
     *         The UTF-8 byte sequence of the given string is used as
     *         the actual value of the claim.
     *
     * @return
     *         {@code this} object.
     */
    public CWTClaimsSetBuilder cti(String identifier)
    {
        return putByteString(CWTClaims.CTI, identifier);
    }


    /**
     * Set the "cti (7)" claim.
     *
     * @param identifier
     *         The value of the {@code cti} claim (CWT ID).
     *
     * @return
     *         {@code this} object.
     */
    public CWTClaimsSetBuilder cti(byte[] identifier)
    {
        return put(CWTClaims.CTI, identifier);
    }


    /**
     * Set the "Nonce (10)" claim.
     *
     * @param nonce
     *         The value of the {@code Nonce} claim.
     *         The UTF-8 byte sequence of the given string is used as
     *         the actual value of the claim.
     *
     * @return
     *         {@code this} object.
     */
    public CWTClaimsSetBuilder nonce(String nonce)
    {
        return putByteString(CWTClaims.NONCE, nonce);
    }


    /**
     * Set the "Nonce (10)" claim.
     *
     * @param nonce
     *         The value of the {@code Nonce} claim.
     *
     * @return
     *         {@code this} object.
     */
    public CWTClaimsSetBuilder nonce(byte[] nonce)
    {
        return put(CWTClaims.NONCE, nonce);
    }


    /**
     * Build an instance of the {@link CWTClaimsSet} class.
     *
     * @return
     *         An instance of the {@link CWTClaimsSet} class.
     */
    public CWTClaimsSet build()
    {
        return CWTClaimsSet.build(map);
    }
}

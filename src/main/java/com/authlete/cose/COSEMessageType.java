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


/**
 * COSE Message Type
 *
 * @since 1.4
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9052#section-2"
 *      >RFC 9052 CBOR Object Signing and Encryption (COSE): Structures and Process,
 *       Section 2. Basic COSE Structure</a>
 */
public enum COSEMessageType
{
    /**
     * COSE Signed Data Object; Tag = 98
     */
    COSE_SIGN(98, "COSE_Sign"),

    /**
     * COSE Single Signer Data Object; Tag = 18
     */
    COSE_SIGN1(18, "COSE_Sign1"),

    /**
     * COSE Encrypted Data Object; Tag = 96
     */
    COSE_ENCRYPT(96, "COSE_Encrypt"),

    /**
     * COSE Single Recipient Encrypted Data Object; Tag = 16
     */
    COSE_ENCRYPT0(16, "COSE_Encrypt0"),

    /**
     * COSE MACed Data Object; Tag = 97
     */
    COSE_MAC(97, "COSE_Mac"),

    /**
     * COSE Mac w/o Recipients Object; Tag = 17
     */
    COSE_MAC0(17, "COSE_Mac0"),
    ;


    private final int tagNumber;
    private final String name;


    private COSEMessageType(int tagNumber, String name)
    {
        this.tagNumber = tagNumber;
        this.name      = name;
    }


    /**
     * Get the tag number of this COSE message.
     *
     * @return
     *         The tag number.
     */
    public int getTagNumber()
    {
        return tagNumber;
    }


    /**
     * Get the name of this COSE message.
     *
     * @return
     *         The name such as {@code "COSE_Sign1"}.
     *
     * @since 1.5
     */
    public String getName()
    {
        return name;
    }
}

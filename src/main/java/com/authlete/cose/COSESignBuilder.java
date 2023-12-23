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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.authlete.cbor.CBORByteArray;
import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORItemList;
import com.authlete.cbor.CBORNull;


/**
 * A utility to build an instance of {@link COSESign}.
 *
 * @since 1.1
 */
public class COSESignBuilder
{
    private COSEProtectedHeader protectedHeader;
    private COSEUnprotectedHeader unprotectedHeader;
    private CBORItem payload;
    private List<CBORItem> signatures = new ArrayList<>();


    /**
     * Set a protected header.
     *
     * @param header
     *         A protected header.
     *
     * @return
     *         {@code this} object.
     */
    public COSESignBuilder protectedHeader(COSEProtectedHeader header)
    {
        this.protectedHeader = header;

        return this;
    }


    /**
     * Set an unprotected header.
     *
     * @param header
     *         An unprotected header.
     *
     * @return
     *         {@code this} object.
     */
    public COSESignBuilder unprotectedHeader(COSEUnprotectedHeader header)
    {
        this.unprotectedHeader = header;

        return this;
    }


    /**
     * Set a payload.
     *
     * @param payload
     *         A payload.
     *
     * @return
     *         {@code this} object.
     */
    public COSESignBuilder payload(CBORByteArray payload)
    {
        this.payload = payload;

        return this;
    }


    /**
     * Set a payload.
     *
     * @param payload
     *         A payload.
     *
     * @return
     *         {@code this} object.
     */
    public COSESignBuilder payload(byte[] payload)
    {
        CBORByteArray ba = (payload != null) ? new CBORByteArray(payload) : null;

        return payload(ba);
    }


    /**
     * Set a payload.
     *
     * @param payload
     *         A payload. Its UTF-8 byte sequence is used.
     *
     * @return
     *         {@code this} object.
     */
    public COSESignBuilder payload(String payload)
    {
        byte[] bytes = (payload != null) ? payload.getBytes(StandardCharsets.UTF_8) : null;

        return payload(bytes);
    }


    /**
     * Set a payload.
     *
     * @param payload
     *         A payload.
     *
     * @return
     *         {@code this} object.
     *
     * @since 1.5
     */
    public COSESignBuilder payload(CBORItem payload)
    {
        this.payload = payload;

        return this;
    }


    /**
     * Add a {@code COSE_Signature} element.
     *
     * @param signature
     *         A {@code COSE_Signature} element.
     *
     * @return
     *         {@code this} object.
     */
    public COSESignBuilder signature(COSESignature signature)
    {
        signatures.add(signature);

        return this;
    }


    /**
     * Build a {@link COSESign} instance.
     *
     * @return
     *         A new {@link COSESign} instance.
     *
     * @throws IllegalStateException
     *         No {@link COSESignature} has been set.
     */
    public COSESign build() throws IllegalStateException
    {
        if (signatures.size() == 0)
        {
            throw new IllegalStateException(
                    "The signature(COSESignature) method must be called at least once.");
        }

        if (protectedHeader == null)
        {
            protectedHeader = new COSEProtectedHeader(new byte[] {});
        }

        if (unprotectedHeader == null)
        {
            unprotectedHeader = new COSEUnprotectedHeader(Collections.emptyList());
        }

        if (payload == null)
        {
            payload = CBORNull.INSTANCE;
        }

        return new COSESign(
                protectedHeader, unprotectedHeader, payload,
                new CBORItemList(signatures));
    }
}

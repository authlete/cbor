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
import java.util.List;
import com.authlete.cbor.CBORByteArray;
import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORNull;


/**
 * A utility to build an instance of {@link COSESign1}.
 *
 * @since 1.1
 */
public class COSESign1Builder
{
    private COSEProtectedHeader protectedHeader;
    private COSEUnprotectedHeader unprotectedHeader;
    private CBORItem payload;
    private CBORByteArray signature;


    /**
     * Set a protected header.
     *
     * @param header
     *         A protected header.
     *
     * @return
     *         {@code this} object.
     */
    public COSESign1Builder protectedHeader(COSEProtectedHeader header)
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
    public COSESign1Builder unprotectedHeader(COSEUnprotectedHeader header)
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
    public COSESign1Builder payload(CBORByteArray payload)
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
    public COSESign1Builder payload(byte[] payload)
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
    public COSESign1Builder payload(String payload)
    {
        byte[] bytes = (payload != null) ? payload.getBytes(StandardCharsets.UTF_8) : null;

        return payload(bytes);
    }


    /**
     * Set a signature.
     *
     * @param signature
     *         A signature.
     *
     * @return
     *         {@code this} object.
     */
    public COSESign1Builder signature(CBORByteArray signature)
    {
        this.signature = signature;

        return this;
    }


    /**
     * Set a signature.
     *
     * @param signature
     *         A signature.
     *
     * @return
     *         {@code this} object.
     */
    public COSESign1Builder signature(byte[] signature)
    {
        CBORByteArray ba = (signature != null) ? new CBORByteArray(signature) : null;

        return signature(ba);
    }


    /**
     * Build a {@link COSESign1} instance.
     *
     * @return
     *         A new {@link COSESign1} instance.
     *
     * @throws IllegalStateException
     *         A signature is not set.
     */
    public COSESign1 build() throws IllegalStateException
    {
        if (signature == null)
        {
            throw new IllegalStateException("Signature is not set.");
        }

        if (protectedHeader == null)
        {
            protectedHeader = new COSEProtectedHeader(new byte[] {});
        }

        if (unprotectedHeader == null)
        {
            unprotectedHeader = new COSEUnprotectedHeader(List.of());
        }

        if (payload == null)
        {
            payload = CBORNull.INSTANCE;
        }

        return new COSESign1(protectedHeader, unprotectedHeader, payload, signature);
    }
}

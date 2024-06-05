/*
 * Copyright (C) 2023-2024 Authlete, Inc.
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


import com.authlete.cbor.CBORByteArray;
import com.authlete.cbor.CBORItemList;
import com.authlete.cbor.CBORString;


/**
 * Sig Signature
 *
 * <p>
 * This class represents {@code Sig_structure} which is defined in <a href=
 * "https://www.rfc-editor.org/rfc/rfc9052.html#section-4.4">4.4. Signing and
 * Verification Process</a> of <a href="https://www.rfc-editor.org/rfc/rfc9052.html"
 * >RFC 9052</a> as follows.
 * </p>
 *
 * <pre>
 * Sig_structure = [
 *     context : "Signature" / "Signature1",
 *     body_protected : empty_or_serialized_map,
 *     ? sign_protected : empty_or_serialized_map,
 *     external_aad : bstr,
 *     payload : bstr
 * ]
 * </pre>
 *
 * @since 1.1
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9052.html#section-4.4"
 *      >RFC 9052, 4.4. Signing and Verification Process</a>
 *
 * @see SigStructureBuilder
 */
public class SigStructure extends CBORItemList
{
    private static final CBORString CONTEXT_SIGNATURE  = new CBORString("Signature");
    private static final CBORString CONTEXT_SIGNATURE1 = new CBORString("Signature1");


    /**
     * A constructor with the protected header of the content, the protected
     * header of {@code COSE_Signature}, the external data and the payload.
     *
     * <p>
     * This constructor is for {@code COSE_Sign}.
     * </p>
     *
     * @param bodyAttributes
     *         The protected header of the content. Must not be null.
     *
     * @param signerAttributes
     *         The protected header of {@code COSE_Signature}. Must not be null.
     *
     * @param externalData
     *         The external data.
     *
     * @param payload
     *         The payload.
     */
    @SuppressWarnings("unchecked")
    public SigStructure(
            COSEProtectedHeader bodyAttributes,
            COSEProtectedHeader signerAttributes,
            CBORByteArray externalData, CBORByteArray payload)
    {
        super(CONTEXT_SIGNATURE, bodyAttributes, signerAttributes, externalData, payload);

        validateArgument("bodyAttributes",   bodyAttributes);
        validateArgument("signerAttributes", signerAttributes);
        validateArgument("externalData",     externalData);
        validateArgument("payload",          payload);
    }


    /**
     * A constructor with the protected header of the content, the external
     * data and the payload.
     *
     * <p>
     * This constructor is for {@code COSE_Sign1}.
     * </p>
     *
     * @param bodyAttributes
     *         The protected header of the content. Must not be null.
     *
     * @param externalData
     *         The external data.
     *
     * @param payload
     *         The payload.
     */
    @SuppressWarnings("unchecked")
    public SigStructure(
            COSEProtectedHeader bodyAttributes,
            CBORByteArray externalData, CBORByteArray payload)
    {
        super(CONTEXT_SIGNATURE1, bodyAttributes, externalData, payload);

        validateArgument("bodyAttributes", bodyAttributes);
        validateArgument("externalData",   externalData);
        validateArgument("payload",        payload);
    }


    private static void validateArgument(String name, Object value)
    {
        if (value == null)
        {
            throw new IllegalArgumentException(String.format(
                    "'%s' given to SigStructure's constructor must not be null", name));
        }
    }


    /**
     * Get the context.
     *
     * @return
     *         A {@link CBORString} representing either {@code "Signature"} or
     *         {@code "Signature1"}.
     */
    public CBORString getContext()
    {
        return (CBORString)getItems().get(0);
    }


    /**
     * Get the protected header of the content.
     *
     * @return
     *         The protected header of the content.
     */
    public COSEProtectedHeader getBodyAttributes()
    {
        return (COSEProtectedHeader)getItems().get(1);
    }


    /**
     * Get the protected header of {@code COSE_Signature}.
     *
     * <p>
     * Note that this is not available when the context is {@code "Signature1"}.
     * </p>
     *
     * @return
     *         The protected header of {@code COSE_Signature}.
     */
    public COSEProtectedHeader getSignerAttributes()
    {
        if (isSignature())
        {
            return (COSEProtectedHeader)getItems().get(2);
        }
        else
        {
            return null;
        }
    }


    /**
     * Get the external data.
     *
     * @return
     *         The external data.
     */
    public CBORByteArray getExternalData()
    {
        if (isSignature())
        {
            return (CBORByteArray)getItems().get(3);
        }
        else
        {
            return (CBORByteArray)getItems().get(2);
        }
    }


    /**
     * Get the payload.
     *
     * @return
     *         The payload.
     */
    public CBORByteArray getPayload()
    {
        if (isSignature())
        {
            return (CBORByteArray)getItems().get(4);
        }
        else
        {
            return (CBORByteArray)getItems().get(3);
        }
    }


    /**
     * Check if the context is {@code "Signature"}.
     *
     * @return
     *         {@code true} if the context is {@code "Signature"}.
     */
    public boolean isSignature()
    {
        return (getContext() == CONTEXT_SIGNATURE);
    }


    /**
     * Check if the context is {@code "Signature1"}.
     *
     * @return
     *         {@code true} if the context is {@code "Signature1"}.
     */
    public boolean isSignature1()
    {
        return (getContext() == CONTEXT_SIGNATURE1);
    }
}

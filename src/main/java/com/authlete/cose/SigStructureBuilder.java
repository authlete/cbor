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
import com.authlete.cbor.CBORByteArray;


/**
 * A utility to build a {@link SigStructure} instance.
 *
 * <p>
 * The {@link SigStructure} class represents {@code Sig_structure} which is defined in
 * <a href="https://www.rfc-editor.org/rfc/rfc9052.html#section-4.4">4.4. Signing
 * and Verification Process</a> of <a href="https://www.rfc-editor.org/rfc/rfc9052.html"
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
 * @see SigStructure
 */
public class SigStructureBuilder
{
    private boolean signature;
    private boolean signature1;
    private COSEProtectedHeader bodyAttributes;
    private COSEProtectedHeader signerAttributes;
    private CBORByteArray externalData;
    private CBORByteArray payload;


    /**
     * Set {@code "Signature"} to {@code Sig_structure.context}.
     *
     * @return
     *         {@code this} object.
     */
    public SigStructureBuilder signature()
    {
        this.signature  = true;
        this.signature1 = false;

        return this;
    }


    /**
     * Set {@code "Signature1"} to {@code Sig_structure.context}.
     *
     * @return
     *         {@code this} object.
     */
    public SigStructureBuilder signature1()
    {
        this.signature  = false;
        this.signature1 = true;

        return this;
    }


    /**
     * Set the attributes to {@code Sig_structure.body_protected}.
     *
     * @param attributes
     *         The protected header of the body information ({@code COSE_Sign}
     *         or {@code COSE_Sign1}).
     *
     * @return
     *         {@code this} object.
     */
    public SigStructureBuilder bodyAttributes(COSEProtectedHeader attributes)
    {
        this.bodyAttributes = attributes;

        return this;
    }


    /**
     * Set the attributes to {@code Sig_structure.sign_protected}.
     *
     * @param attributes
     *         The protected header of a {@code COSE_Signature}.
     *
     * @return
     *         {@code this} object.
     */
    public SigStructureBuilder signerAttributes(COSEProtectedHeader attributes)
    {
        this.signerAttributes = attributes;

        return this;
    }


    /**
     * Set the data to {@code Sig_structure.external_add}.
     *
     * @param data
     *         The externally supplied data.
     *
     * @return
     *         {@code this} object.
     */
    public SigStructureBuilder externalData(CBORByteArray data)
    {
        this.externalData = data;

        return this;
    }


    /**
     * Set the data to {@code Sig_structure.external_add}.
     *
     * @param data
     *         The externally supplied data.
     *
     * @return
     *         {@code this} object.
     */
    public SigStructureBuilder externalData(byte[] data)
    {
        CBORByteArray ba = (data != null) ? new CBORByteArray(data) : null;

        return externalData(ba);
    }


    /**
     * Set the payload to {@code Sig_structure.payload}.
     *
     * @param payload
     *         The payload to be signed.
     *
     * @return
     *         {@code this} object.
     */
    public SigStructureBuilder payload(CBORByteArray payload)
    {
        this.payload = payload;

        return this;
    }


    /**
     * Set the payload to {@code Sig_structure.payload}.
     *
     * @param payload
     *         The payload to be signed.
     *
     * @return
     *         {@code this} object.
     */
    public SigStructureBuilder payload(byte[] payload)
    {
        CBORByteArray ba = (payload != null) ? new CBORByteArray(payload) : null;

        return payload(ba);
    }


    /**
     * Set the payload to {@code Sig_structure.payload}.
     *
     * @param payload
     *         The payload to be signed. Its UTF-8 byte sequence is used.
     *
     * @return
     *         {@code this} object.
     */
    public SigStructureBuilder payload(String payload)
    {
        byte[] bytes = (payload != null) ? payload.getBytes(StandardCharsets.UTF_8) : null;

        return payload(bytes);
    }


    /**
     * Set some fields of {@code Sig_structure} based on the given
     * {@link COSESign} object that represents {@code COSE_Sign}
     * which is defined in <a href=
     * "https://www.rfc-editor.org/rfc/rfc9052.html#section-4.1"
     * >4.1. Signing with One or More Signers</a> of <a href=
     * "https://www.rfc-editor.org/rfc/rfc9052.html">RFC 9052</a>.
     *
     * <p>
     * The implementation of this method does the following.
     * </p>
     *
     * <ol>
     * <li>Set {@code "Signature"} to {@code Sig_structure.context}.
     * <li>Set {@code sign.}{@link COSESign#getProtectedHeader()
     *     getProtectedeHeader()} to {@code Sig_structure.body_protected}.
     * <li>Set {@code sign.}{@link COSESign#getPayload() getPayload()}
     *     to {@code Sig_sructure.payload} if the payload holds a
     *     {@link CBORByteArray} instance.
     * </ol>
     *
     * @param sign
     *         A {@link COSESign} object that represents {@code COSE_Sign}.
     *
     * @return
     *         {@code this} object.
     *
     * @see <a href="https://www.rfc-editor.org/rfc/rfc9052.html#section-4.1"
     *      >RFC 9052, 4.1. Signing with One or More Signers</a>
     */
    public SigStructureBuilder sign(COSESign sign)
    {
        // Signature
        signature();

        // Body attributes
        bodyAttributes(sign.getProtectedHeader());

        if (sign.getPayload() instanceof CBORByteArray)
        {
            // Payload
            payload((CBORByteArray)sign.getPayload());
        }

        return this;
    }


    /**
     * Set the protected header of the given {@link COSESignature} object to
     * {@code Sig_structure.sign_protected}.
     *
     * <p>
     * The {@link COSESignature} class represents {@code COSE_Signature}
     * which is defined in <a href=
     * "https://www.rfc-editor.org/rfc/rfc9052.html#section-4.1">4.1.
     * Signing with One or More Signers</a> of <a href=
     * "https://www.rfc-editor.org/rfc/rfc9052.html">RFC 9052</a>.
     * </p>
     *
     * @param signature
     *         A {@link COSESignature} object that represents
     *         {@code COSE_Signature}.
     *
     * @return
     *         {@code this} object.
     */
    public SigStructureBuilder signature(COSESignature signature)
    {
        // Signer attributes
        signerAttributes(signature.getProtectedHeader());

        return this;
    }


    /**
     * Set some fields of {@code Sig_structure} based on the given
     * {@link COSESign1} object that represents {@code COSE_Sign1}
     * which is defined in <a href=
     * "https://www.rfc-editor.org/rfc/rfc9052.html#section-4.2"
     * >4.2. Signing with One Signer</a> of <a href=
     * "https://www.rfc-editor.org/rfc/rfc9052.html">RFC 9052</a>.
     *
     * <p>
     * The implementation of this method does the following.
     * </p>
     *
     * <ol>
     * <li>Set {@code "Signature1"} to {@code Sig_structure.context}.
     * <li>Set {@code sign1.}{@link COSESign1#getProtectedHeader()
     *     getProtectedeHeader()} to {@code Sig_structure.body_protected}.
     * <li>Set {@code sign1.}{@link COSESign1#getPayload() getPayload()}
     *     to {@code Sig_sructure.payload} if the payload holds a
     *     {@link CBORByteArray} instance.
     * </ol>
     *
     * @param sign1
     *         A {@link COSESign1} object that represents {@code COSE_Sign1}.
     *
     * @return
     *         {@code this} object.
     *
     * @see <a href="https://www.rfc-editor.org/rfc/rfc9052.html#section-4.2"
     *      >RFC 9052, 4.2. Signing with One Signer</a>
     */
    public SigStructureBuilder sign1(COSESign1 sign1)
    {
        // Signature1
        signature1();

        // Body attributes
        bodyAttributes(sign1.getProtectedHeader());

        if (sign1.getPayload() instanceof CBORByteArray)
        {
            // Payload
            payload((CBORByteArray)sign1.getPayload());
        }

        return this;
    }


    /**
     * Build a {@link SigStructure} instance that represents {@code Sig_structure}.
     *
     * <p>
     * For this method to return a {@code SigStructure} instance successfully,
     * the following must be satisfied.
     * </p>
     *
     * <ol>
     * <li>{@link #signature()}, {@link #signature1()}, {@link #sign(COSESign)},
     *     or {@link #sign1(COSESign1)} must have been called to set
     *     {@code Sig_structure.context}.
     * <li>{@link #payload(CBORByteArray)}, {@link #sign(COSESign)} or
     *     {@link #sign1(COSESign1)} must have been called to set
     *     {@code Sig_structure.payload}.
     * </ol>
     *
     * @return
     *         A newly created {@link SigStructure} instance.
     *
     * @throws IllegalStateException
     */
    public SigStructure build() throws IllegalStateException
    {
        if (signature == false && signature1 == false)
        {
            throw new IllegalStateException(
                    "Either the signature() method or the signature1() method must be called.");
        }

        if (signature1 && signerAttributes != null)
        {
            throw new IllegalStateException(
                    "Signer attributes must not be given in the case of Signature1.");
        }

        if (payload == null)
        {
            throw new IllegalStateException("A payload must be given.");
        }

        if (bodyAttributes == null)
        {
            bodyAttributes = new COSEProtectedHeader(new byte[] {});
        }

        if (signature && signerAttributes == null)
        {
            signerAttributes = new COSEProtectedHeader(new byte[] {});
        }

        if (externalData == null)
        {
            externalData = new CBORByteArray(new byte[] {});
        }

        if (signature)
        {
            // For COSE_Sign ("Signature")
            return new SigStructure(bodyAttributes, signerAttributes, externalData, payload);
        }
        else
        {
            // For COSE_Sign1 ("Signature1")
            return new SigStructure(bodyAttributes, externalData, payload);
        }
    }
}

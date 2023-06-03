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


import java.util.List;
import com.authlete.cbor.CBORByteArray;
import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORizer;


/**
 * COSE_Signature
 *
 * <p>
 * {@code COSE_Signature} is defined in <a href=
 * "https://www.rfc-editor.org/rfc/rfc9052.html#section-4.1">4.1. Signing
 * with One or More Signers</a> of <a href=
 * "https://www.rfc-editor.org/rfc/rfc9052.html">RFC 9052</a> as follows.
 * </p>
 *
 * <pre>
 * COSE_Signature =  [
 *     Headers,
 *     signature : bstr
 * ]
 * </pre>
 *
 * @since 1.1
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9052.html#section-4.1"
 *      >RFC 9052, 4.1. Signing with One or More Signers</a>
 */
public class COSESignature extends COSEObject
{
    /**
     * A constructor with a protected header, an unprotected header and
     * a signature.
     *
     * @param protectedHeader
     *         A protected header. Must not be null.
     *
     * @param unprotectedHeader
     *         An unprotected header. Must not be null.
     *
     * @param signature
     *         A signature. Must not be null.
     */
    public COSESignature(
            COSEProtectedHeader protectedHeader,
            COSEUnprotectedHeader unprotectedHeader,
            byte[] signature)
    {
        this(protectedHeader, unprotectedHeader, new CBORByteArray(signature));
    }


    /**
     * A constructor with a protected header, an unprotected header and
     * a signature.
     *
     * @param protectedHeader
     *         A protected header. Must not be null.
     *
     * @param unprotectedHeader
     *         An unprotected header. Must not be null.
     *
     * @param signature
     *         A signature. Must not be null.
     */
    public COSESignature(
            COSEProtectedHeader protectedHeader,
            COSEUnprotectedHeader unprotectedHeader,
            CBORByteArray signature)
    {
        super(protectedHeader, unprotectedHeader, signature);

        validateSignature(signature);
    }


    private static void validateSignature(CBORByteArray signature)
    {
        if (signature == null)
        {
            throw new IllegalArgumentException(
                    "A signature given to COSESignature's constructor must not be null.");
        }
    }


    /**
     * Get the signature.
     *
     * @return
     *         The signature.
     */
    public CBORByteArray getSignature()
    {
        return (CBORByteArray)getItems().get(2);
    }


    /**
     * Build a {@code COSESignature} instance from the given CBOR data item.
     * The data item must be a CBOR array.
     *
     * @param item
     *         A CBOR data item that represents {@code COSE_Signature}.
     *
     * @return
     *         A {@code COSESignature} instance built from the given CBOR data item.
     *
     * @throws COSEException
     */
    public static COSESignature build(CBORItem item) throws COSEException
    {
        // Build the protected header, the unprotected header and the content.
        List<CBORItem> common = COSEObject.buildCommon(item, "COSE_Signature", 3);

        // In the context of COSESignature, the content is a signature.
        CBORItem signature = common.get(2);

        // If the third element is not a byte string.
        if (!(signature instanceof CBORByteArray))
        {
            throw new COSEException(
                    "The third element (signature) of COSE_Signature must be a byte string.");
        }

        try
        {
            return new COSESignature(
                    (COSEProtectedHeader)common.get(0),
                    (COSEUnprotectedHeader)common.get(1),
                    (CBORByteArray)signature);
        }
        catch (Exception cause)
        {
            throw new COSEException(cause.getMessage(), cause);
        }
    }


    /**
     * Build a {@code COSESignature} instance from the given object list.
     *
     * @param list
     *         A object list that represents {@code COSE_Signature}.
     *
     * @return
     *         A {@code COSESignature} instance built from the given object list.
     *
     * @throws COSEException
     */
    public static COSESignature build(List<Object> list) throws COSEException
    {
        return build(new CBORizer().cborizeCollection(list));
    }
}

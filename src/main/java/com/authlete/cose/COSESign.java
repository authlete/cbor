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


import java.util.ArrayList;
import java.util.List;
import com.authlete.cbor.CBORByteArray;
import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORItemList;
import com.authlete.cbor.CBORNull;
import com.authlete.cbor.CBORizer;


/**
 * COSE_Sign
 *
 * <p>
 * {@code COSE_Sign} is defined in <a href=
 * "https://www.rfc-editor.org/rfc/rfc9052.html#section-4.1">4.1. Signing
 * with One or More Signers</a> of <a href=
 * "https://www.rfc-editor.org/rfc/rfc9052.html">RFC 9052</a> as follows.
 * </p>
 *
 * <pre>
 * COSE_Sign = [
 *     Headers,
 *     payload : bstr / nil,
 *     signatures : [+ COSE_Signature]
 * ]
 * </pre>
 *
 * @since 1.1
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9052.html#section-4.1"
 *      >RFC 9052, 4.1. Signing with One or More Signers</a>
 *
 * @see COSESignBuilder
 */
public class COSESign extends COSEMessage
{
    /**
     * A constructor with a protected header, an unprotected header, a
     * payload and signatures.
     *
     * @param protectedHeader
     *         A protected header. Must not be null.
     *
     * @param unprotectedHeader
     *         An unprotected header. Must not be null.
     *
     * @param payload
     *         A payload. Must be either {@link CBORByteArray} or
     *         {@link CBORNull}.
     *
     * @param signatures
     *         Signatures. Must not be null. Items in the list must be
     *         instances of {@link COSESignature}. At least one element
     *         must be contained.
     */
    public COSESign(
            COSEProtectedHeader protectedHeader,
            COSEUnprotectedHeader unprotectedHeader,
            CBORItem payload,
            CBORItemList signatures)
    {
        super(COSEMessageType.COSE_SIGN,
                protectedHeader, unprotectedHeader, payload, signatures);

        validateSignatures(signatures);
    }


    private static void validateSignatures(CBORItemList signatures)
    {
        if (signatures == null)
        {
            throw new IllegalArgumentException(
                    "A signature list given to COSESign's constructor must not be null.");
        }

        List<CBORItem> items = signatures.getItems();

        if (items == null || items.size() == 0)
        {
            throw new IllegalArgumentException(
                    "A signature list given to COSESign's constructor must not be empty.");
        }

        for (CBORItem item : items)
        {
            if (item instanceof COSESignature)
            {
                continue;
            }

            throw new IllegalArgumentException(
                    "Items in the signature list given to COSESign's constructor must be instances of COSESignature.");
        }
    }


    /**
     * Get the payload.
     *
     * <p>
     * The type of the value is {@link CBORByteArray} or {@link CBORNull}.
     * </p>
     *
     * @return
     *         The payload.
     */
    public CBORItem getPayload()
    {
        return getItems().get(2);
    }


    /**
     * Get the list of signatures. Items in the list are {@link COSESignature}
     * instances.
     *
     * @return
     *         The list of signatures.
     */
    public CBORItemList getSignatures()
    {
        return (CBORItemList)getItems().get(3);
    }


    /**
     * Build a {@code COSESign} instance from the given CBOR data item.
     * The data item must be a CBOR array.
     *
     * @param item
     *         A CBOR data item that represents {@code COSE_Sign}.
     *
     * @return
     *         A {@code COSESign} instance built from the given CBOR data item.
     *
     * @throws COSEException
     */
    public static COSESign build(CBORItem item) throws COSEException
    {
        // Build the protected header, the unprotected header and the content.
        List<CBORItem> common = COSEObject.buildCommon(item, "COSE_Sign", 4);

        // The fourth element of COSE_Sign, which is a list of signatures.
        CBORItem fourth = common.get(3);

        // If the fourth element is not a CBOR array.
        if (!(fourth instanceof CBORItemList))
        {
            throw new COSEException(
                    "The fourth element (signatures) of COSE_Sign must be a CBOR array.");
        }

        // Signatures
        CBORItemList signatures = buildSignatures((CBORItemList)fourth);

        try
        {
            return new COSESign(
                    (COSEProtectedHeader)common.get(0),
                    (COSEUnprotectedHeader)common.get(1),
                    buildDecodableByteArrayIfPossible(common.get(2)),
                    signatures);
        }
        catch (Exception cause)
        {
            throw new COSEException(cause.getMessage(), cause);
        }
    }


    private static CBORItemList buildSignatures(CBORItemList list) throws COSEException
    {
        List<CBORItem> items = list.getItems();

        if (items == null || items.size() == 0)
        {
            throw new COSEException(
                    "COSE_Sign must contain at least one signature.");
        }

        List<CBORItem> signatures = new ArrayList<>();

        for (CBORItem item : items)
        {
            // Interpret the CBOR item as a COSE_Signature.
            COSESignature signature = COSESignature.build(item);

            // Add the signature.
            signatures.add(signature);
        }

        return new CBORItemList(signatures);
    }


    /**
     * Build a {@code COSESign} instance from the given object list.
     *
     * @param list
     *         A object list that represents {@code COSE_Sign}.
     *
     * @return
     *         A {@code COSESign} instance built from the given object list.
     *
     * @throws COSEException
     */
    public static COSESign build(List<Object> list) throws COSEException
    {
        return build(new CBORizer().cborizeCollection(list));
    }
}

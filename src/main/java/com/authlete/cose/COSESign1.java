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
import com.authlete.cbor.CBORNull;
import com.authlete.cbor.CBORizer;


/**
 * COSE_Sign1
 *
 * <p>
 * {@code COSE_Sign1} is defined in <a href=
 * "https://www.rfc-editor.org/rfc/rfc9052.html#section-4.2">4.2. Signing
 * with One Signer</a> of <a href="https://www.rfc-editor.org/rfc/rfc9052.html"
 * >RFC 9052</a> as follows.
 * </p>
 *
 * <pre>
 * COSE_Sign1 = [
 *     Headers,
 *     payload : bstr / nil,
 *     signature : bstr
 * ]
 * </pre>
 *
 * @since 1.1
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9052.html#section-4.2"
 *      >RFC 9052, 4.2. Signing with One Signer</a>
 *
 * @see COSESign1Builder
 */
public class COSESign1 extends COSEMessage
{
    /**
     * A constructor with a protected header, an unprotected header, a
     * payload and a signature.
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
     * @param signature
     *         A signature. Must not be null.
     */
    public COSESign1(
            COSEProtectedHeader protectedHeader,
            COSEUnprotectedHeader unprotectedHeader,
            CBORItem payload,
            CBORByteArray signature)
    {
        super(COSEMessageType.COSE_SIGN1,
                protectedHeader, unprotectedHeader, payload, signature);

        validateSignature(signature);
    }


    private static void validateSignature(CBORByteArray signature)
    {
        if (signature == null)
        {
            throw new IllegalArgumentException(
                    "A signature given to COSESign1's constructor must not be null.");
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
     * Get the signature.
     *
     * @return
     *         The signature.
     */
    public CBORByteArray getSignature()
    {
        return (CBORByteArray)getItems().get(3);
    }


    /**
     * Build a {@code COSESign1} instance from the given CBOR data item.
     * The data item must be a CBOR array.
     *
     * @param item
     *         A CBOR data item that represents {@code COSE_Sign1}.
     *
     * @return
     *         A {@code COSESign1} instance built from the given CBOR data item.
     *
     * @throws COSEException
     */
    public static COSESign1 build(CBORItem item) throws COSEException
    {
        // Build the protected header, the unprotected header and the content.
        List<CBORItem> common = COSEObject.buildCommon(item, "COSE_Sign1", 4);

        // The fourth element of COSE_Sign1, which is a signature.
        CBORItem signature = common.get(3);

        // If the fourth element is not a byte string.
        if (!(signature instanceof CBORByteArray))
        {
            throw new COSEException(
                    "The fourth element (signature) of COSE_Sign1 must be a byte string.");
        }

        try
        {
            return new COSESign1(
                    (COSEProtectedHeader)common.get(0),
                    (COSEUnprotectedHeader)common.get(1),
                    common.get(2),
                    (CBORByteArray)signature);
        }
        catch (Exception cause)
        {
            throw new COSEException(cause.getMessage(), cause);
        }
    }


    /**
     * Build a {@code COSESign1} instance from the given object list.
     *
     * @param list
     *         A object list that represents {@code COSE_Sign1}.
     *
     * @return
     *         A {@code COSESign1} instance built from the given object list.
     *
     * @throws COSEException
     */
    public static COSESign1 build(List<Object> list) throws COSEException
    {
        return build(new CBORizer().cborizeCollection(list));
    }
}

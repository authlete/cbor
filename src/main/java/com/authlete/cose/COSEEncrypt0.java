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
 * COSE_Encrypt0
 *
 * <p>
 * {@code COSE_Encrypt0} is defined in <a href=
 * "https://www.rfc-editor.org/rfc/rfc9052.html#section-5.2">5.2. Single
 * Recipient Encrypted</a> of <a href="https://www.rfc-editor.org/rfc/rfc9052.html"
 * >RFC 9052</a> as follows.
 * </p>
 *
 * <pre>
 * COSE_Encrypt0 = [
 *     Headers,
 *     ciphertext : bstr / nil,
 * ]
 * </pre>
 *
 * @since 1.1
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9052.html#section-5.2"
 *      >RFC 9052, 5.2. Single Recipient Encrypted</a>
 */
public class COSEEncrypt0 extends COSEMessage
{
    /**
     * A constructor with a protected header, an unprotected header and
     * a cipher text.
     *
     * @param protectedHeader
     *         A protected header. Must not be null.
     *
     * @param unprotectedHeader
     *         An unprotected header. Must not be null.
     *
     * @param ciphertext
     *         A cipher text. Must be either {@link CBORByteArray} or
     *         {@link CBORNull}.
     */
    public COSEEncrypt0(
            COSEProtectedHeader protectedHeader,
            COSEUnprotectedHeader unprotectedHeader,
            CBORItem ciphertext)
    {
        super(16, protectedHeader, unprotectedHeader, ciphertext);
    }


    /**
     * Get the cipher text.
     *
     * <p>
     * The type of the value is {@link CBORByteArray} or {@link CBORNull}.
     * </p>
     *
     * @return
     *         The cipher text.
     */
    public CBORItem getCiphertext()
    {
        return getItems().get(2);
    }


    /**
     * Build a {@code COSEEncrypt0} instance from the given CBOR data item.
     * The data item must be a CBOR array.
     *
     * @param item
     *         A CBOR data item that represents {@code COSE_Encrypt0}.
     *
     * @return
     *         A {@code COSEEncrypt0} instance built from the given CBOR data item.
     *
     * @throws COSEException
     */
    public static COSEEncrypt0 build(CBORItem item) throws COSEException
    {
        // Build the protected header, the unprotected header and the content.
        List<CBORItem> common = COSEObject.buildCommon(item, "COSE_Encrypt0", 3);

        try
        {
            return new COSEEncrypt0(
                    (COSEProtectedHeader)common.get(0),
                    (COSEUnprotectedHeader)common.get(1),
                    common.get(2));
        }
        catch (Exception cause)
        {
            throw new COSEException(cause.getMessage(), cause);
        }
    }


    /**
     * Build a {@code COSEEncrypt0} instance from the given object list.
     *
     * @param list
     *         A object list that represents {@code COSE_Encrypt0}.
     *
     * @return
     *         A {@code COSEEncrypt0} instance built from the given object list.
     *
     * @throws COSEException
     */
    public static COSEEncrypt0 build(List<Object> list) throws COSEException
    {
        return build(new CBORizer().cborizeCollection(list));
    }
}

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
 * COSE_Mac
 *
 * <p>
 * {@code COSE_Mac} is defined in <a href=
 * "https://www.rfc-editor.org/rfc/rfc9052.html#section-6.1">6.1. MACed
 * Message with Recipients</a> of <a href=
 * "https://www.rfc-editor.org/rfc/rfc9052.html">RFC 9052</a> as follows.
 * </p>
 *
 * <pre>
 * COSE_Mac = [
 *     Headers,
 *     payload : bstr / nil,
 *     tag : bstr,
 *     recipients : [+COSE_recipient]
 * ]
 * </pre>
 *
 * @since 1.1
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9052.html#section-6.1"
 *      >RFC 9052, 6.1. MACed Message with Recipients</a>
 */
public class COSEMac extends COSEMessage
{
    /**
     * A constructor with a protected header, an unprotected header, a
     * payload, a tag and recipients.
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
     * @param tag
     *         A tag. Must not be null.
     *
     * @param recipients
     *         Recipients. Must not be null. Items in the list must be
     *         instances of {@link COSERecipient}. At least one element
     *         must be contained.
     */
    public COSEMac(
            COSEProtectedHeader protectedHeader,
            COSEUnprotectedHeader unprotectedHeader,
            CBORItem payload,
            CBORByteArray tag,
            CBORItemList recipients)
    {
        super(COSEMessageType.COSE_MAC,
                protectedHeader, unprotectedHeader, payload, tag, recipients);

        validateTag(tag);
        validateRecipients(recipients);
    }


    private static void validateTag(CBORByteArray tag)
    {
        if (tag == null)
        {
            throw new IllegalArgumentException(
                    "A tag given to COSEMac's constructor must not be null.");
        }
    }


    private static void validateRecipients(CBORItemList recipients)
    {
        if (recipients == null)
        {
            throw new IllegalArgumentException(
                    "A recipient list given to COSEMac's constructor must not be null.");
        }

        List<CBORItem> items = recipients.getItems();

        if (items == null || items.size() == 0)
        {
            throw new IllegalArgumentException(
                    "A recipient list given to COSEMac's constructor must not be empty.");
        }

        for (CBORItem item : items)
        {
            if (item instanceof COSERecipient)
            {
                continue;
            }

            throw new IllegalArgumentException(
                    "Items in the recipient list given to COSEMac's constructor must be instances of COSERecipient.");
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
     * Get the tag.
     *
     * @return
     *         The tag.
     */
    public CBORByteArray getTag()
    {
        return (CBORByteArray)getItems().get(3);
    }


    /**
     * Get the list of recipients. Items in the list are {@link COSERecipient}
     * instances.
     *
     * @return
     *         The list of recipients.
     */
    public CBORItemList getRecipients()
    {
        return (CBORItemList)getItems().get(4);
    }


    /**
     * Build a {@code COSEMac} instance from the given CBOR data item.
     * The data item must be a CBOR array.
     *
     * @param item
     *         A CBOR data item that represents {@code COSE_Mac}.
     *
     * @return
     *         A {@code COSEMac} instance built from the given CBOR data item.
     *
     * @throws COSEException
     */
    public static COSEMac build(CBORItem item) throws COSEException
    {
        // Build the protected header, the unprotected header and the content.
        List<CBORItem> common = COSEObject.buildCommon(item, "COSE_Mac", 5);

        // The fourth element of COSE_Mac, which is a tag.
        CBORItem fourth = common.get(3);

        // If the fourth element is not a byte string.
        if (!(fourth instanceof CBORByteArray))
        {
            throw new COSEException(
                    "The fourth element (tag) of COSE_Mac must be a byte string.");
        }

        // The fifth element of COSE_Mac, which is a list of recipients.
        CBORItem fifth = common.get(4);

        // If the fifth element is not a CBOR array.
        if (!(fifth instanceof CBORItemList))
        {
            throw new COSEException(
                    "The fifth element (recipients) of COSE_Mac must be a CBOR array.");
        }

        // Recipients
        CBORItemList recipients = buildRecipients((CBORItemList)fourth);

        try
        {
            return new COSEMac(
                    (COSEProtectedHeader)common.get(0),
                    (COSEUnprotectedHeader)common.get(1),
                    buildDecodableByteArrayIfPossible(common.get(2)),
                    (CBORByteArray)fourth,
                    recipients);
        }
        catch (Exception cause)
        {
            throw new COSEException(cause.getMessage(), cause);
        }
    }


    private static CBORItemList buildRecipients(CBORItemList list) throws COSEException
    {
        List<CBORItem> items = list.getItems();

        if (items == null || items.size() == 0)
        {
            throw new COSEException(
                    "A list of recipients in COSE_Mac must contain at least one recipient.");
        }

        List<CBORItem> recipients = new ArrayList<>();

        for (CBORItem item : items)
        {
            // Interpret the CBOR item as a recipient.
            COSERecipient recipient = COSERecipient.build(item);

            // Add the recipient.
            recipients.add(recipient);
        }

        return new CBORItemList(recipients);
    }


    /**
     * Build a {@code COSEMac} instance from the given object list.
     *
     * @param list
     *         A object list that represents {@code COSE_Mac}.
     *
     * @return
     *         A {@code COSEMac} instance built from the given object list.
     *
     * @throws COSEException
     */
    public static COSEMac build(List<Object> list) throws COSEException
    {
        return build(new CBORizer().cborizeCollection(list));
    }
}

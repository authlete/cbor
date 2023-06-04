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
import java.util.Set;
import com.authlete.cbor.CBORByteArray;
import com.authlete.cbor.CBORDecoderException;
import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORItemList;
import com.authlete.cbor.CBORNull;


/**
 * COSE Object
 *
 * <p>
 * This class represents the basic COSE structure described in <a href=
 * "https://www.rfc-editor.org/rfc/rfc9052.html#section-2">2. Basic COSE
 * Structure</a> of <a href="https://www.rfc-editor.org/rfc/rfc9052.html"
 * >RFC 9052</a>.
 * </p>
 *
 * @since 1.1
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9052.html#section-2"
 *      >RFC 9052, 2. Basic COSE Structure</a>
 */
public abstract class COSEObject extends CBORItemList
{
    /**
     * A constructor with a protected header, an unprotected header, a content,
     * and additional items.
     *
     * @param protectedHeader
     *         The protected header. Must not be null.
     *
     * @param unprotectedHeader
     *         The unprotected header. Must not be null.
     *
     * @param content
     *         The content. Must be either {@link CBORByteArray} or
     *         {@link CBORNull}.
     *
     * @param additionalItems
     *         Additional items.
     *
     * @throws IllegalArgumentException
     *         {@code protectedHeader} is null, {@code unprotectedHeader} is
     *         null, or {@code content} is neither a {@link CBORByteArray}
     *         instance nor a {@link CBORNull} instance.
     */
    public COSEObject(
            COSEProtectedHeader protectedHeader,
            COSEUnprotectedHeader unprotectedHeader,
            CBORItem content,
            CBORItem... additionalItems) throws IllegalArgumentException
    {
        super(buildItems(
                protectedHeader, unprotectedHeader,
                content, additionalItems));

        validateProtectedHeader(protectedHeader);
        validateUnprotectedHeader(unprotectedHeader);
        validateContent(content);
    }


    private static List<CBORItem> buildItems(
            COSEProtectedHeader protectedHeader,
            COSEUnprotectedHeader unprotectedHeader,
            CBORItem content,
            CBORItem... additionalItems)
    {
        List<CBORItem> items = new ArrayList<>();

        items.add(protectedHeader);
        items.add(unprotectedHeader);
        items.add(content);

        for (CBORItem item : additionalItems)
        {
            items.add(item);
        }

        return items;
    }


    private static void validateProtectedHeader(COSEProtectedHeader header)
    {
        if (header == null)
        {
            throw new IllegalArgumentException(
                    "A protected header given to the constructor must not be null.");
        }
    }


    private static void validateUnprotectedHeader(COSEUnprotectedHeader header)
    {
        if (header == null)
        {
            throw new IllegalArgumentException(
                    "A unprotected header given to the constructor must not be null.");
        }
    }


    private static void validateContent(CBORItem content)
    {
        if (content instanceof CBORByteArray || content instanceof CBORNull)
        {
            // OK
            return;
        }

        throw new IllegalArgumentException(
                "A content given to the constructor must be an instance of either CBORByteArray or CBORNull.");
    }


    /**
     * Get the protected header of this COSE object.
     *
     * @return
     *         The protected header.
     */
    public COSEProtectedHeader getProtectedHeader()
    {
        return (COSEProtectedHeader)getItems().get(0);
    }


    /**
     * Get the unprotected header of this COSE object.
     *
     * @return
     *         The unprotected header.
     */
    public COSEUnprotectedHeader getUnprotectedHeader()
    {
        return (COSEUnprotectedHeader)getItems().get(1);
    }


    static List<CBORItem> buildCommon(
            CBORItem object, String name) throws COSEException
    {
        return buildCommon(object, name, 0);
    }


    static List<CBORItem> buildCommon(
            CBORItem object, String name, int size) throws COSEException
    {
        // Interpret the COSE object as a CBOR array and extract elements.
        List<CBORItem> elements = extractElements(object, name, size);

        // Build a protected header from the first element.
        COSEProtectedHeader protectedHeader =
                COSEProtectedHeader.build(elements.get(0));

        // Build an unprotected header from the second element.
        COSEUnprotectedHeader unprotectedHeader =
                COSEUnprotectedHeader.build(elements.get(1));

        // Make sure that the same label does not occur in both the protected
        // header and unprotected header parameters.
        checkLabelDuplicates(protectedHeader, unprotectedHeader);

        // Check content
        CBORItem content = checkContent(elements.get(2), name);

        // Validated elements.
        List<CBORItem> validated = new ArrayList<>();
        validated.add(protectedHeader);
        validated.add(unprotectedHeader);
        validated.add(content);

        // Additional items.
        for (int i = 3; i < size; i++)
        {
            validated.add(elements.get(i));
        }

        return validated;
    }


    private static List<CBORItem> extractElements(
            CBORItem object, String name, int size) throws COSEException
    {
        // If the COSE object is not a CBOR array.
        if (!(object instanceof CBORItemList))
        {
            throw new COSEException(String.format(
                    "%s must be a CBOR array.", name));
        }

        // Elements in the CBOR array.
        List<CBORItem> elements = ((CBORItemList)object).getItems();

        // If the COSE object is empty.
        if (elements == null)
        {
            throw new COSEException(String.format(
                    "A CBOR array that represents %s must not be empty.", name));
        }

        // If the caller of buildCommon() specified 'size'.
        if (0 < size)
        {
            if (elements.size() != size)
            {
                throw new COSEException(String.format(
                        "The size of a CBOR array that represents %s must be %d.", name, size));
            }
        }
        else
        {
            // Every COSE object must have at least 3 elements; They are
            // (1) protected header, (2) unprotected header and (3) content.
            if (elements.size() < 3)
            {
                throw new COSEException(String.format(
                        "A CBOR array that represents %s must have at least 3 elements.", name));
            }
        }

        return elements;
    }


    private static void checkLabelDuplicates(
            COSEProtectedHeader protectedHeader,
            COSEUnprotectedHeader unprotectedHeader) throws COSEException
    {
        Set<Object> protectedLabels   = protectedHeader  .getParameters().keySet();
        Set<Object> unprotectedLabels = unprotectedHeader.getParameters().keySet();

        int cmp = Integer.compare(protectedLabels.size(), unprotectedLabels.size());
        Set<Object> set1 = (cmp <= 0) ? protectedLabels : unprotectedLabels;
        Set<Object> set2 = (cmp >  0) ? protectedLabels : unprotectedLabels;

        for (Object element : set1)
        {
            // If the label in set1 does not exist in set2.
            if (!set2.contains(element))
            {
                // OK
                continue;
            }

            throw new COSEException(String.format(
                    "The label '%s' exists in both the protected header and the unprotected header.",
                    element.toString()));
        }
    }


    private static CBORItem checkContent(
            CBORItem content, String name) throws COSEException
    {
        if (content instanceof CBORByteArray || content instanceof CBORNull)
        {
            // OK
            return content;
        }

        throw new COSEException(String.format(
                "The third element of %s must be a byte array or null.", name));
    }


    static CBORItem buildDecodableByteArrayIfPossible(CBORItem item)
    {
        // If the CBOR data item is not a byte string.
        if (!(item instanceof CBORByteArray))
        {
            // Use the item as is.
            return item;
        }

        CBORByteArray bstr = (CBORByteArray)item;

        try
        {
            // Try to decode the content of the byte string as
            // CBOR data items.
            List<CBORItem> items = bstr.decodeValue();

            // If the byte array contains one or more CBOR data items.
            if (items != null && items.size() != 0)
            {
                // Create a new byte string marked as decodable.
                return new CBORByteArray(bstr.getValue(), true);
            }
        }
        catch (CBORDecoderException cause)
        {
            // The content of the byte string failed to be decoded
            // as a CBOR data item.
        }

        // Use the item as is.
        return item;
    }
}

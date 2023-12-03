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
package com.authlete.mdoc;


import java.util.List;
import com.authlete.cbor.CBORPair;
import com.authlete.cbor.CBORPairList;
import com.authlete.cbor.CBORPairsBuilder;
import com.authlete.cbor.CBORString;


/**
 * The {@code IssuerSignedItem} structure.
 *
 * <p>
 * For details, see ISO/IEC 18013-5:2021, 8.3.2.1.2.2 Device retrieval mdoc response.
 * </p>
 *
 * <h3>Definition</h3>
 *
 * <pre>
 * IssuerSignedItem = {
 *     "digestID" : uint,
 *     "random" : bstr,
 *     "elementIdentifier" : DataElementIdentifier,
 *     "elementValue" : DataElementValue
 * }
 * </pre>
 *
 * @since 1.5
 *
 * @see <a href="https://www.iso.org/standard/69084.html">ISO/IEC 18013-5:2021</a>
 *
 * @see IssuerSignedItemBytes
 */
public class IssuerSignedItem extends CBORPairList
{
    private static final CBORString LABEL_DIGEST_ID          = new CBORString("digestID");
    private static final CBORString LABEL_RANDOM             = new CBORString("random");
    private static final CBORString LABEL_ELEMENT_IDENTIFIER = new CBORString("elementIdentifier");
    private static final CBORString LABEL_ELEMENT_VALUE      = new CBORString("elementValue");


    private final int mDigestID;


    public IssuerSignedItem(
            int digestID, byte[] random, String elementIdentifier, Object elementValue)
    {
        super(createList(digestID, random, elementIdentifier, elementValue));

        mDigestID = digestID;
    }


    private static List<CBORPair> createList(
            int digestID, byte[] random, String elementIdentifier, Object elementValue)
    {
        return new CBORPairsBuilder()
                .add(          LABEL_DIGEST_ID,          digestID)
                .addUnlessNull(LABEL_RANDOM,             random)
                .addUnlessNull(LABEL_ELEMENT_IDENTIFIER, elementIdentifier)
                .addUnlessNull(LABEL_ELEMENT_VALUE,      elementValue)
                .build();
    }


    public int getDigestID()
    {
        return mDigestID;
    }
}

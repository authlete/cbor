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
import com.authlete.cose.COSESign1;


/**
 * The {@code IssuerSigned} structure.
 *
 * <p>
 * For details, see ISO/IEC 18013-5:2021, 8.3.2.1.2.2 Device retrieval mdoc response.
 * </p>
 *
 * <h3>Definition</h3>
 *
 * <pre>
 * IssuerSigned = {
 *     ? "nameSpaces" : {@link IssuerNameSpaces},
 *     "issuerAuth" : IssuerAuth
 * }
 *
 * IssuerAuth = COSE_Sign1
 * </pre>
 *
 * @since 1.5
 *
 * @see <a href="https://www.iso.org/standard/69084.html">ISO/IEC 18013-5:2021</a>
 *
 * @see Document
 */
public class IssuerSigned extends CBORPairList
{
    private static final CBORString LABEL_NAME_SPACES = new CBORString("nameSpaces");
    private static final CBORString LABEL_ISSUER_AUTH = new CBORString("issuerAuth");


    public IssuerSigned(IssuerNameSpaces nameSpaces, COSESign1 issuerAuth)
    {
        super(createList(nameSpaces, issuerAuth));
    }


    private static List<CBORPair> createList(
            IssuerNameSpaces nameSpaces, COSESign1 issuerAuth)
    {
        return new CBORPairsBuilder()
                .addUnlessNull(LABEL_NAME_SPACES, nameSpaces)
                .addUnlessNull(LABEL_ISSUER_AUTH, issuerAuth)
                .build();
    }
}

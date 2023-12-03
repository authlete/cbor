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
 * The {@code Document} structure.
 *
 * <p>
 * For details, see ISO/IEC 18013-5:2021, 8.3.2.1.2.2 Device retrieval mdoc response.
 * </p>
 *
 * <h3>Definition</h3>
 *
 * <pre>
 * Document = {
 *     "docType" : DocType,
 *     "issuerSigned" : {@link IssuerSigned},
 *     "deviceSigned" : {@link DeviceSigned},
 *     ? "errors" : {@link Errors}
 * }
 *
 * DocType = tstr
 * </pre>
 *
 * @since 1.5
 *
 * @see <a href="https://www.iso.org/standard/69084.html">ISO/IEC 18013-5:2021</a>
 *
 * @see DeviceResponse
 */
public class Document extends CBORPairList
{
    private static final CBORString LABEL_DOC_TYPE      = new CBORString("docType");
    private static final CBORString LABEL_ISSUER_SIGNED = new CBORString("issuerSigned");
    private static final CBORString LABEL_DEVICE_SIGNED = new CBORString("deviceSigned");
    private static final CBORString LABEL_ERRORS        = new CBORString("errors");


    public Document(String docType, IssuerSigned issuerSigned)
    {
        this(docType, issuerSigned, /* deviceSigned */ null, /* errors */ null);
    }


    public Document(
            String docType, IssuerSigned issuerSigned,
            DeviceSigned deviceSigned, Errors errors)
    {
        super(createList(docType, issuerSigned, deviceSigned, errors));
    }


    private static List<CBORPair> createList(
            String docType, IssuerSigned issuerSigned,
            DeviceSigned deviceSigned, Errors errors)
    {
        return new CBORPairsBuilder()
                .addUnlessNull(LABEL_DOC_TYPE,      docType)
                .addUnlessNull(LABEL_ISSUER_SIGNED, issuerSigned)
                .addUnlessNull(LABEL_DEVICE_SIGNED, deviceSigned)
                .addUnlessNull(LABEL_ERRORS,        errors)
                .build();
    }
}

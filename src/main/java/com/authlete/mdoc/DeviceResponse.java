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
 * The {@code DeviceResponse} structure.
 *
 * <p>
 * For details, see ISO/IEC 18013-5:2021, 8.3.2.1.2.2 Device retrieval mdoc response.
 * </p>
 *
 * <h3>Definition</h3>
 *
 * <pre>
 * DeviceResponse = {
 *     "version" : tstr,
 *     ? "documents" : [ + {@link Document} ],
 *     ? "documentErrors" : [ + {@link DocumentError} ],
 *     "status" : uint
 * }
 * </pre>
 *
 * @since 1.5
 *
 * @see <a href="https://www.iso.org/standard/69084.html">ISO/IEC 18013-5:2021</a>
 */
public class DeviceResponse extends CBORPairList
{
    private static final String DEFAULT_VERSION = "1.0";
    private static final int    DEFAULT_STATUS  = 0;     // OK


    private static final CBORString LABEL_VERSION         = new CBORString("version");
    private static final CBORString LABEL_DOCUMENTS       = new CBORString("documents");
    private static final CBORString LABEL_DOCUMENT_ERRORS = new CBORString("documentErrors");
    private static final CBORString LABEL_STATUS          = new CBORString("status");


    /**
     * A constructor with documents. This constructor is an alias of
     * {@link #DeviceResponse(String, List, List, int) DeviceResponse}{@code
     * ("1.0", documents, null, 0)}.
     *
     * @param documents
     *         Documents.
     */
    public DeviceResponse(List<Document> documents)
    {
        this(DEFAULT_VERSION, documents, /* documentErrors */ null, DEFAULT_STATUS);
    }


    public DeviceResponse(
            String version, List<Document> documents,
            List<DocumentError> documentErrors, int status)
    {
        super(createList(version, documents, documentErrors, status));
    }


    private static List<CBORPair> createList(
            String version, List<Document> documents,
            List<DocumentError> documentErrors, int status)
    {
        return new CBORPairsBuilder()
                .addUnlessNull(LABEL_VERSION,         version)
                .addUnlessNull(LABEL_DOCUMENTS,       documents)
                .addUnlessNull(LABEL_DOCUMENT_ERRORS, documentErrors)
                .add(          LABEL_STATUS,          status)
                .build();
    }
}

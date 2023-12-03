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


import com.authlete.cbor.CBORByteArray;
import com.authlete.cbor.CBORInteger;
import com.authlete.cbor.CBORPair;


/**
 * An entry of {@link DigestIDs}; A pair of a digest ID and a digest value.
 *
 * <p>
 * For details, see ISO/IEC 18013-5:2021, 9.1.2.4 Signing method and structure for MSO.
 * </p>
 *
 * <h3>Definition</h3>
 *
 * <pre>
 * DigestIDs = {
 *     + DigestID => Digest
 * }
 *
 * DigestID = uint
 * Digest = bstr
 * </pre>
 *
 * <p>
 * The input for the digest calculation is an {@link IssuerSignedItemBytes} element.
 * </p>
 *
 * @since 1.5
 *
 * @see <a href="https://www.iso.org/standard/69084.html">ISO/IEC 18013-5:2021</a>
 *
 * @see ValueDigests
 *
 * @see DigestIDs
 */
public class DigestIDsEntry extends CBORPair
{
    public DigestIDsEntry(CBORInteger digestID, CBORByteArray digest)
    {
        super(digestID, digest);
    }


    public DigestIDsEntry(CBORInteger digestID, byte[] digest)
    {
        this(digestID, new CBORByteArray(digest));
    }


    public DigestIDsEntry(int digestID, CBORByteArray digest)
    {
        this(new CBORInteger(digestID), digest);
    }


    public DigestIDsEntry(int digestID, byte[] digest)
    {
        this(new CBORInteger(digestID), new CBORByteArray(digest));
    }
}

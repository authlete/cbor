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
package com.authlete.cwt;


import com.authlete.cbor.CBORTaggedItem;
import com.authlete.cose.COSEMessage;


/**
 * A CBOR tagged item that represents CWT.
 *
 * @since 1.1
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8392.html"
 *      >CBOR Web Token (CWT)</a>
 */
public class CWT extends CBORTaggedItem
{
    public CWT(COSEMessage message)
    {
        // RFC 8392, 6. CWT CBOR Tag
        //
        //   If present, the CWT tag MUST prefix a tagged object using one of
        //   the COSE CBOR tags.  In this example, the COSE_Mac0 tag is used.
        //   The actual COSE_Mac0 object has been excluded from this example.
        //
        //       / CWT CBOR tag / 61(
        //         / COSE_Mac0 CBOR tag / 17(
        //           / COSE_Mac0 object /
        //         )
        //       )
        //
        super(61, message.getTagged());
    }


    /**
     * Get the wrapped COSE message.
     *
     * @return
     *         The wrapped COSE message (an instance of a subclass of
     *         {@link COSEMessage}).
     */
    public COSEMessage getMessage()
    {
        return (COSEMessage)((CBORTaggedItem)getTagContent()).getTagContent();
    }
}

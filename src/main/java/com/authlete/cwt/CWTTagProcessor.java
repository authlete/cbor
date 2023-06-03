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


import com.authlete.cbor.CBORDecoderException;
import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORTaggedItem;
import com.authlete.cbor.tag.CBORTagProcessor;
import com.authlete.cose.COSEMessage;


/**
 * A tag processor for CWT.
 *
 * @since 1.1
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8392.html"
 *      >CBOR Web Token (CWT)</a>
 */
public class CWTTagProcessor implements CBORTagProcessor
{
    public static final CWTTagProcessor INSTANCE = new CWTTagProcessor();


    @Override
    public CBORItem process(Number tagNumber, CBORItem tagContent) throws CBORDecoderException
    {
        if (tagContent instanceof CBORTaggedItem)
        {
            tagContent = ((CBORTaggedItem)tagContent).getTagContent();
        }

        // If the tag content is not a COSE message (e.g. COSESign1)
        if (!(tagContent instanceof COSEMessage))
        {
            throw new CBORDecoderException(
                    "CWT's tag content must be a COSE message.");
        }

        return new CWT((COSEMessage)tagContent);
    }
}

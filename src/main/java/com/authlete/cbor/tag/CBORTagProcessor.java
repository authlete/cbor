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
package com.authlete.cbor.tag;


import com.authlete.cbor.CBORDecoderException;
import com.authlete.cbor.CBORItem;


/**
 * The interface for tag processors.
 *
 * <p>
 * Tag processors can be registered by {@link
 * com.authlete.cbor.CBORDecoderOptions#setTagProcessor(Number, CBORTagProcessor)
 * CBORTagProcessor.setTagProcessor(Number, CBORTagProcessor)}.
 * </p>
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8949.html#name-tagging-of-items"
 *      >RFC 8949, 3.4. Tagging of Items</a>
 */
public interface CBORTagProcessor
{
    /**
     * Process a tag.
     *
     * <p>
     * The CBOR data item returned by this method replaces the current tagged
     * item ({@link com.authlete.cbor.CBORTaggedItem CBORTaggedItem}).
     * </p>
     *
     * <p>
     * The simplest implementation is to return {@code tagContent} as is. It
     * means that the tag is just removed. (cf. {@link CPUntag})
     * </p>
     *
     * <p>
     * The second simplest implementation is to create a {@code CBORTaggedItem}
     * instance with the tag number and the tag content passed as arguments and
     * return it. (cf. {@link CPDefault})
     * </p>
     *
     * @param tagNumber
     *         The tag number.
     *
     * @param tagContent
     *         The tag content.
     *
     * @return
     *         A CBOR data item which replaces the current tagged item
     *         ({@link com.authlete.cbor.CBORTaggedItem CBORTaggedItem}).
     *
     * @throws CBORDecoderException
     */
    CBORItem process(Number tagNumber, CBORItem tagContent) throws CBORDecoderException;
}

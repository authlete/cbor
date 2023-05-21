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
import com.authlete.cbor.CBORTaggedItem;


/**
 * A tag processor that returns a {@link CBORTaggedItem} instance that
 * has the tag number and the tag content passed as arguments of the
 * {@code process} method.
 */
public class CPDefault implements CBORTagProcessor
{
    public static final CPDefault INSTANCE = new CPDefault();


    @Override
    public CBORItem process(Number tagNumber, CBORItem tagContent) throws CBORDecoderException
    {
        return new CBORTaggedItem(tagNumber, tagContent);
    }
}

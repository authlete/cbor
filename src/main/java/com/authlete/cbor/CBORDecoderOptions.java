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
package com.authlete.cbor;


import java.util.HashMap;
import java.util.Map;
import com.authlete.cbor.tag.CBORTagProcessor;


/**
 * Options to control the behavior of the CBOR decoder ({@link CBORDecoder}).
 */
public class CBORDecoderOptions
{
    private final Map<String, CBORTagProcessor> tagProcessors = new HashMap<>();


    /**
     * Get the tag processor for the tag number.
     *
     * @param tagNumber
     *         A tag number.
     *
     * @return
     *         The tag processor for the tag number. If none is registered,
     *         {@code null} is returned.
     */
    public CBORTagProcessor getTagProcessor(Number tagNumber)
    {
        return tagProcessors.get(tagNumber.toString());
    }


    /**
     * Set a tag processor for the tag number.
     *
     * @param tagNumber
     *         A tag number.
     *
     * @param processor
     *         The tag processor for the tag number.
     */
    public void setTagProcessor(Number tagNumber, CBORTagProcessor processor)
    {
        tagProcessors.put(tagNumber.toString(), processor);
    }


    /**
     * Remove the tag processor that has been registered for the tag number.
     *
     * @param tagNumber
     *         A tag number.
     */
    public void removeTagProcessor(Number tagNumber)
    {
        tagProcessors.remove(tagNumber.toString());
    }
}

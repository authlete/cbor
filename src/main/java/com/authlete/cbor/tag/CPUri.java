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


import java.net.URI;
import com.authlete.cbor.CBORDecoderException;
import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORString;
import com.authlete.cbor.CBORUri;


/**
 * A tag processor for the tag number 32, which indicates that the following
 * text string is a URI.
 *
 * <p>
 * This processor confirms that the format of the following text string is a
 * valid URI. In addition, by default, it converts the text string into a
 * {@link URI} instance.
 * </p>
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8949.html#name-encoded-text"
 *      >RFC 8949 Concise Binary Object Representation (CBOR), 3.4.5.3. Encoded Text</a>
 */
public class CPUri implements CBORTagProcessor
{
    /**
     * Whether to convert the text string to a URI instance.
     */
    private final boolean conversion;


    /**
     * The default constructor, which is an alias of
     * {@link #CPUri(boolean) CPUri}{@code (true)}.
     */
    public CPUri()
    {
        this(true);
    }


    /**
     * A constructor with a boolean flag that indicates whether to perform
     * conversion from a text string to a {@link URI} instance or not.
     *
     * <P>
     * If {@code true} is given, the {@code process} method returns an instance
     * of {@link CBORUri}. Otherwise, if {@code false} is given, the
     * {@code process} method returns the tag content (text string).
     * </p>
     *
     * @param conversion
     *         {@code true} to perform conversion from a text string to a
     *         {@link URI} instance.
     */
    public CPUri(boolean conversion)
    {
        this.conversion = conversion;
    }


    @Override
    public CBORItem process(Number tagNumber, CBORItem tagContent) throws CBORDecoderException
    {
        // If the tag content is not a text string.
        if (!(tagContent instanceof CBORString))
        {
            throw new CBORDecoderException(String.format(
                    "The tag content for the tag number '%s' must be a text string.",
                    tagNumber.toString()));
        }

        // The text string.
        String text = ((CBORString)tagContent).getValue();

        URI uri;

        try
        {
            // Convert the text string to a URI.
            uri = new URI(text);
        }
        catch (Exception cause)
        {
            // The text string is not a valid URI.
            throw new CBORDecoderException(String.format(
                    "The text string tagged by the tag number '%s' is not a valid URI: %s",
                    tagNumber.toString(), text));
        }

        if (conversion)
        {
            // Convert String to URI.
            return new CBORUri(uri);
        }
        else
        {
            // No conversion.
            return tagContent;
        }
    }
}

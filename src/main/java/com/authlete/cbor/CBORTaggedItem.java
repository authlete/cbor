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


import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;


/**
 * A class that represents a CBOR data item wrapped by a tag.
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8949.html#name-tagging-of-items"
 *      >RFC 8949, 3.4. Tagging of Items</a>
 */
public class CBORTaggedItem extends CBORItem
{
    private final Number tagNumber;
    private final CBORItem tagContent;


    /**
     * A constructor with a tag number and a tag content.
     *
     * @param tagNumber
     *         A tag number. Must be an instance of {@code Integer},
     *         {@code Long} or {@code BigInteger}.
     *
     * @param tagContent
     *         A tag content. If {@code null} is given,
     *         {@link CBORNull#INSTANCE} is used.
     */
    public CBORTaggedItem(Number tagNumber, CBORItem tagContent)
    {
        if (tagNumber == null)
        {
            throw new NullPointerException(
                    "The tag number given to the constructor is null.");
        }

        if (!(tagNumber instanceof Integer) &&
            !(tagNumber instanceof Long) &&
            !(tagNumber instanceof BigInteger))
        {
            throw new IllegalArgumentException(
                    "The class of the tag number must be Integer, Long or BigInteger.");
        }

        this.tagNumber  = tagNumber;
        this.tagContent = (tagContent != null) ? tagContent : CBORNull.INSTANCE;
    }


    /**
     * Get the tag number.
     *
     * @return
     *         The tag number.
     */
    public Number getTagNumber()
    {
        return tagNumber;
    }


    /**
     * Get the tag content.
     *
     * @return
     *         The tag content.
     */
    public CBORItem getTagContent()
    {
        return tagContent;
    }


    /**
     * Return {@code "<tag number>(<tag content>)"}.
     * For example, {@code "1(1363896240)"}.
     */
    @Override
    public String toString()
    {
        return buildString();
    }


    private String buildString()
    {
        return String.format("%s(%s)", tagNumber.toString(), tagContent.toString());
    }


    @Override
    protected String prettify(String indent, String indentUnit)
    {
        String comment = (getComment() == null) ? ""
                : String.format("/ %s / ", getComment());

        return String.format("%s%s(%s)",
                comment,
                tagNumber.toString(),
                tagContent.prettify(indent, indentUnit));
    }


    @Override
    public void encode(OutputStream outputStream) throws IOException
    {
        encodeMajorWithNumber(outputStream, 6 /* major */, tagNumber);

        tagContent.encode(outputStream);
    }


    @Override
    public Object parse()
    {
        return tagContent.parse();
    }
}

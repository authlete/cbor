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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * A class that represents a CBOR array (major type = 4).
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8949.html#name-major-types"
 *      >RFC 8949, 3.1. Major Types</a>
 */
public class CBORItemList extends CBORItem
{
    private final List<? extends CBORItem> items;


    /**
     * A constructor with a list of CBOR data items that this CBOR array holds.
     *
     * @param items
     *         A list of CBOR data items.
     */
    public CBORItemList(List<? extends CBORItem> items)
    {
        this.items = items;
    }


    /**
     * Get the CBOR data items in this CBOR array.
     *
     * @return
     *         The CBOR data items.
     */
    public List<? extends CBORItem> getItems()
    {
        return items;
    }


    /**
     * Return <code>"[&lt;item&gt;, ...]"</code>.
     */
    @Override
    public String toString()
    {
        return buildString();
    }


    private String buildString()
    {
        if (items == null)
        {
            return "[]";
        }

        return items.stream().map(CBORItem::toString)
                .collect(Collectors.joining(", ", "[", "]"));
    }


    @Override
    protected String prettify(String indent, String indentUnit)
    {
        // The comment attached to this CBOR item.
        String comment = (getComment() == null) ? ""
                : String.format("/ %s / ", getComment());

        if (items == null || items.size() == 0)
        {
            return String.format("%s[%n%s]", comment, indent);
        }

        String delimiter = String.format(",%n");
        String prefix    = String.format("%s[%n", comment);
        String suffix    = String.format("%n%s]", indent);

        // The indent for each item.
        final String subIndent = indent + indentUnit;

        return items.stream()
                .map(item -> String.format("%s%s", subIndent, item.prettify(subIndent, indentUnit)))
                .collect(Collectors.joining(delimiter, prefix, suffix));
    }


    @Override
    public void encode(OutputStream outputStream) throws IOException
    {
        if (items == null)
        {
            encodeMajorWithNumber(outputStream, 4 /* major */, 0 /* size */);
            return;
        }

        encodeMajorWithNumber(outputStream, 4 /* major */, items.size());

        for (CBORItem item : items)
        {
            item.encode(outputStream);
        }
    }


    @Override
    public List<Object> parse()
    {
        List<Object> list = new ArrayList<>();

        if (items != null)
        {
            items.stream().map(CBORItem::parse).forEachOrdered(list::add);
        }

        return list;
    }
}

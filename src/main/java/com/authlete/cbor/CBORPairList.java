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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * A class that represents a CBOR map (major type = 5).
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8949.html#name-major-types"
 *      >RFC 8949, 3.1. Major Types</a>
 */
public class CBORPairList extends CBORItem
{
    private final List<? extends CBORPair> pairs;


    /**
     * A constructor with a list of key-value pairs that this CBOR map holds.
     *
     * @param pairs
     *         A list of key-value pairs.
     */
    public CBORPairList(List<? extends CBORPair> pairs)
    {
        this.pairs = pairs;
    }


    /**
     * A constructor with a list of key-value pairs that this CBOR map holds.
     *
     * @param pairs
     *         A list of key-value pairs.
     *
     * @since 1.14
     */
    @SuppressWarnings("unchecked")
    public <T extends CBORPair> CBORPairList(T... pairs)
    {
        this(Arrays.asList(pairs));
    }


    /**
     * Get the key-value pairs in this CBOR map.
     *
     * @return
     *         The key-value pairs.
     */
    public List<? extends CBORPair> getPairs()
    {
        return pairs;
    }


    /**
     * Return <code>"{&lt;key&gt;: &lt;value&gt;, ...}"</code>.
     */
    @Override
    public String toString()
    {
        return buildString();
    }


    private String buildString()
    {
        if (pairs == null)
        {
            return "{}";
        }

        return pairs.stream().map(CBORPair::toString)
                .collect(Collectors.joining(", ", "{", "}"));
    }


    @Override
    protected String prettify(String indent, String indentUnit, Number tagNumber)
    {
        // The comment attached to this CBOR item.
        String comment = (getComment() == null) ? ""
                : String.format("/ %s / ", getComment());

        if (pairs == null || pairs.size() == 0)
        {
            return String.format("%s{%n%s}", comment, indent);
        }

        String delimiter = String.format(",%n");
        String prefix    = String.format("%s{%n", comment);
        String suffix    = String.format("%n%s}", indent);

        // The indent for each pair.
        final String subIndent = indent + indentUnit;

        return pairs.stream()
                .map(pair -> String.format("%s%s",
                        subIndent, pair.prettify(subIndent, indentUnit)))
                .collect(Collectors.joining(delimiter, prefix, suffix));
    }


    @Override
    public void encode(OutputStream outputStream) throws IOException
    {
        if (pairs == null)
        {
            encodeMajorWithNumber(outputStream, 5 /* major */, 0 /* size */);
            return;
        }

        encodeMajorWithNumber(outputStream, 5 /* major */, pairs.size());

        for (CBORPair pair : pairs)
        {
            pair.encode(outputStream);
        }
    }


    @Override
    public Map<Object, Object> parse()
    {
        // LinkedHashMap is used to preserve the order in the CBOR map.
        Map<Object, Object> map = new LinkedHashMap<>();

        if (pairs != null)
        {
            for (CBORPair pair : pairs)
            {
                Object key   = pair.getKey()  .parse();
                Object value = pair.getValue().parse();

                map.put(key, value);
            }
        }

        return map;
    }


    /**
     * Find a pair that has the specified key.
     *
     * @param key
     *         A pair that has the specified key.
     *
     * @return
     *         A pair that has the specified key.
     *         {@code null} if not found.
     *
     * @since 1.5
     */
    public CBORPair findByKey(Object key)
    {
        if (key == null)
        {
            return null;
        }

        if (pairs == null)
        {
            return null;
        }

        for (CBORPair pair : pairs)
        {
            if (!(pair.getKey() instanceof CBORValue))
            {
                continue;
            }

            if (key.equals(((CBORValue<?>)pair.getKey()).getValue()))
            {
                return pair;
            }
        }

        return null;
    }
}

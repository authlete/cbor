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


/**
 * A class that represents a key-value pair of CBOR map.
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8949.html#name-major-types"
 *      >RFC 8949, 3.1. Major Types</a>
 */
public class CBORPair
{
    private final CBORItem key;
    private final CBORItem value;


    /**
     * A constructor with a key and a value.
     *
     * @param key
     *         A key. If {@code null} is given, {@link CBORNull#INSTANCE} is used.
     *
     * @param value
     *         A value. If {@code null} is given, {@link CBORNull#INSTANCE} is used.
     */
    public CBORPair(CBORItem key, CBORItem value)
    {
        this.key   = (key   != null) ? key   : CBORNull.INSTANCE;
        this.value = (value != null) ? value : CBORNull.INSTANCE;
    }


    /**
     * Get the key.
     *
     * @return
     *         The key.
     */
    public CBORItem getKey()
    {
        return key;
    }


    /**
     * Get the value.
     *
     * @return
     *         The value.
     */
    public CBORItem getValue()
    {
        return value;
    }


    /**
     * Return {@code "<key>: <value>"}.
     */
    @Override
    public String toString()
    {
        return buildString();
    }


    private String buildString()
    {
        return String.format("%s: %s", key, value);
    }


    /**
     * Write CBOR representations of the key and the value into the specified
     * output stream.
     *
     * @param outputStream
     *         An output stream to write CBOR representations into.
     *
     * @throws IOException
     */
    public void encode(OutputStream outputStream) throws IOException
    {
        key  .encode(outputStream);
        value.encode(outputStream);
    }
}

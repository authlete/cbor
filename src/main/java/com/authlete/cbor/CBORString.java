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
import java.nio.charset.StandardCharsets;


/**
 * CBOR text string (major type = 3).
 *
 * <p>
 * The {@link #getValue()} method of this class returns an instance of
 * {@link String}.
 * </p>
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8949#name-major-types"
 *      >RFC 8949, 3.1. Major Types</a>
 */
public class CBORString extends CBORValue<String>
{
    public CBORString(String value)
    {
        super(value);
    }


    /**
     * Return {@code "\"<value>\""}.
     */
    @Override
    public String toString()
    {
        return buildString();
    }


    private String buildString()
    {
        return String.format("\"%s\"", getValue());
    }


    @Override
    public void encode(OutputStream outputStream) throws IOException
    {
        byte[] value = getValue().getBytes(StandardCharsets.UTF_8);

        encodeMajorWithNumber(outputStream, 3 /* major */, value.length);

        outputStream.write(value);
    }
}

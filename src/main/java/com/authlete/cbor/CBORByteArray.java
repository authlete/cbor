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
 * CBOR byte array (major type = 2).
 *
 * <p>
 * The {@link #getValue()} method of this class returns an instance of
 * byte array ({@code byte[]}).
 * </p>
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8949#name-major-types"
 *      >RFC 8949, 3.1. Major Types</a>
 */
public class CBORByteArray extends CBORValue<byte[]>
{
    public CBORByteArray(byte[] value)
    {
        super(value);
    }


    /**
     * Return <code>h'<i>{base64}</i>'</code>.
     *
     * @see <a href="https://www.rfc-editor.org/rfc/rfc8949#name-diagnostic-notation"
     *      >RFC 8949, 8. Diagnostic Notation</a>
     */
    @Override
    public String toString()
    {
        return buildString();
    }


    private String buildString()
    {
        StringBuilder sb = new StringBuilder("h'");

        for (byte b : getValue())
        {
            sb.append(toHex(b));
        }

        sb.append("'");

        return sb.toString();
    }


    private static String toHex(int b)
    {
        return String.format("%02x", (0xFF & b));
    }


    @Override
    public void encode(OutputStream outputStream) throws IOException
    {
        byte[] value = getValue();

        encodeMajorWithNumber(outputStream, 2 /* major */, value.length);

        outputStream.write(value);
    }
}

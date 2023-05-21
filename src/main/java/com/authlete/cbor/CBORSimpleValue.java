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
 * CBOR Simple Value (major type = 7, additional information = 0 ~ 19 or 24).
 *
 * <p>
 * The {@link #getValue()} method of this class returns an instance of
 * {@link Integer}.
 * </p>
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8949#name-floating-point-numbers-and-"
 *      >RFC 8949, 3.3. Floating-Point Numbers and Values with No Content</a>
 */
public class CBORSimpleValue extends CBORValue<Integer>
{
    public CBORSimpleValue(Integer value)
    {
        super(value);

        // 0 ~ 19 and 32 ~ 255 are valid ranges
        if ((0 <= value && value <= 19) || (32 <= value && value <= 255))
        {
            // OK
            return;
        }

        throw new IllegalArgumentException(String.format(
                "The value given to the constructor (%d) is invalid for Simple Value.", value));
    }


    /**
     * Return {@code "simple(<value>)"}.
     *
     * @see <a href="https://www.rfc-editor.org/rfc/rfc8949.html#name-diagnostic-notation"
     *      >RFC 8949, 8. Diagnostic Notation</a>
     */
    @Override
    public String toString()
    {
        return buildString();
    }


    private String buildString()
    {
        return String.format("simple(%d)", getValue());
    }


    @Override
    public void encode(OutputStream outputStream) throws IOException
    {
        encodeMajorWithNumber(outputStream, 7 /* major */, getValue());
    }
}

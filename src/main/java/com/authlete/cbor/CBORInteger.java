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
 * CBOR integer (major type = 0 or 1).
 *
 * <p>
 * The {@link #getValue()} method of this class returns an instance of
 * {@link Integer}.
 * </p>
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8949#name-major-types"
 *      >RFC 8949, 3.1. Major Types</a>
 */
public class CBORInteger extends CBORNumber<Integer>
{
    public CBORInteger(Integer value)
    {
        super(value);
    }


    @Override
    public void encode(OutputStream outputStream) throws IOException
    {
        int value = getValue();

        if (0 <= value)
        {
            // Unsigned integer
            encodeMajorWithNumber(outputStream, 0 /* major */, value);
        }
        else
        {
            // Convert to a positive number for encoding.
            value += 1;
            value = -value;

            // Negative integer
            encodeMajorWithNumber(outputStream, 1 /* major */, value);
        }
    }
}

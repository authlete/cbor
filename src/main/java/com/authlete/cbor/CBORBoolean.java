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
 * CBOR boolean (major type = 7, additional information = 20 or 21).
 *
 * <p>
 * The {@link #getValue()} method of this class returns either
 * {@link Boolean#FALSE} or {@link Boolean#TRUE}.
 * </p>
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8949#name-floating-point-numbers-and-"
 *      >RFC 8949, 3.3. Floating-Point Numbers and Values with No Content</a>
 */
public class CBORBoolean extends CBORValue<Boolean>
{
    /**
     * CBOR false (major type = 7, additional information = 20).
     */
    public static final CBORBoolean FALSE = new CBORBoolean(Boolean.FALSE);


    /**
     * CBOR true (major type = 7, additional information = 21).
     */
    public static final CBORBoolean TRUE = new CBORBoolean(Boolean.TRUE);


    private CBORBoolean(Boolean value)
    {
        super(value);
    }


    @Override
    public void encode(OutputStream outputStream) throws IOException
    {
        // false
        //
        // Major = 7, Info = 20
        //
        // | major |   info    |
        // |-------+-----------|
        // | 1 1 1 | 1 0 1 0 0 |

        // true
        //
        // Major = 7, Info = 21
        //
        // | major |   info    |
        // |-------+-----------|
        // | 1 1 1 | 1 0 1 0 1 |

        outputStream.write(getValue() ? 0xF5 : 0xF4);
    }
}

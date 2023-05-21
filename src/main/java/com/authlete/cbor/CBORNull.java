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
 * CBOR null (major type = 7, additional information = 22).
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8949#name-the-break-stop-code"
 *      >RFC 8949, 3.2.1. The "break" Stop Code</a>
 */
public class CBORNull extends CBORItem
{
    public static final CBORNull INSTANCE = new CBORNull();


    private CBORNull()
    {
    }


    /**
     * Return {@code "null"}.
     */
    @Override
    public String toString()
    {
        return "null";
    }


    @Override
    public void encode(OutputStream outputStream) throws IOException
    {
        // Major = 7, Info = 22
        //
        // | major |   info    |
        // |-------+-----------|
        // | 1 1 1 | 1 0 1 1 0 |
        //
        outputStream.write(0xF6);
    }


    @Override
    public Object parse()
    {
        return null;
    }
}

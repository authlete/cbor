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
 * CBOR break (major type = 7, additional information = 31).
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8949#name-the-break-stop-code"
 *      >RFC 8949, 3.2.1. The "break" Stop Code</a>
 */
public class CBORBreak extends CBORItem
{
    /**
     * CBOR break (major type = 7, additional information = 31).
     */
    public static final CBORBreak INSTANCE = new CBORBreak();


    private CBORBreak()
    {
    }


    /**
     * Return {@code "break"}.
     */
    @Override
    public String toString()
    {
        return "break";
    }


    @Override
    public void encode(OutputStream outputStream) throws IOException
    {
        // Major = 7, Info = 31
        //
        // | major |   info    |
        // |-------+-----------|
        // | 1 1 1 | 1 1 1 1 1 |
        //
        outputStream.write(0xFF);
    }


    @Override
    public Object parse()
    {
        return null;
    }
}

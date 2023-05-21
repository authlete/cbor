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


/**
 * The exception raised when the value of a simple value is invalid.
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8949#name-floating-point-numbers-and-"
 *      >RFC 8949, 3.3. Floating-Point Numbers and Values with No Content</a>
 */
public class CBORInvalidSimpleValueException extends CBORDecoderException
{
    private static final long serialVersionUID = 1L;


    public CBORInvalidSimpleValueException(int major, int info, int offset, int value)
    {
        super(buildMessage(major, info, offset, value), offset);
    }


    private static String buildMessage(int major, int info, int offset, int value)
    {
        return String.format(
                "Invalid simple value: major=%d, info=%d, offset=%d, value=%d",
                major, info, offset, value);
    }
}

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
 * The exception raised when the byte sequence of a text string (major type = 3)
 * is not a valid UTF-8 byte sequence.
 */
public class CBORMalformedUtf8Exception extends CBORDecoderException
{
    private static final long serialVersionUID = 1L;


    public CBORMalformedUtf8Exception(int major, int info, int offset, Throwable cause)
    {
        super(buildMessage(major, info, offset, cause), offset);
    }


    private static String buildMessage(int major, int info, int offset, Throwable cause)
    {
        return String.format(
                "Malformed UTF-8 byte sequence: major=%d, info=%d, offset=%d, cause=%s",
                major, info, offset, cause.getMessage());
    }
}

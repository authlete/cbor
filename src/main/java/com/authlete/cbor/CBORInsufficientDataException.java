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
 * The exception raised when the end of the input stream was reached unexpectedly.
 */
public class CBORInsufficientDataException extends CBORDecoderException
{
    private static final long serialVersionUID = 1L;


    public CBORInsufficientDataException(String message, int offset)
    {
        super(message, offset);
    }


    public CBORInsufficientDataException(int major, int info, int offset, int length)
    {
        this(buildMessage(major, info, offset, length), offset);
    }


    private static String buildMessage(int major, int info, int offset, int length)
    {
        return String.format(
                "Insufficient data: major=%d, info=%d, offset=%d, necessary number of bytes=%d",
                major, info, offset, length);
    }
}

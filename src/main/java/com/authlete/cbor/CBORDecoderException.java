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


/**
 * The base exception for errors that the decoding process may encounter.
 */
public class CBORDecoderException extends IOException
{
    private static final long serialVersionUID = 1L;
    private static final int UNKNOWN_OFFSET = -1;


    private final int offset;


    public CBORDecoderException(String message)
    {
        this(message, UNKNOWN_OFFSET);
    }


    public CBORDecoderException(String message, int offset)
    {
        super(message);

        this.offset = offset;
    }


    public CBORDecoderException(Throwable cause)
    {
        this(cause, UNKNOWN_OFFSET);
    }


    public CBORDecoderException(Throwable cause, int offset)
    {
        super(cause);

        this.offset = offset;
    }


    public CBORDecoderException(String message, Throwable cause)
    {
        this(message, cause, UNKNOWN_OFFSET);
    }


    public CBORDecoderException(String message, Throwable cause, int offset)
    {
        super(message, cause);

        this.offset = offset;
    }


    /**
     * Get the offset in the input stream where the error was detected.
     *
     * <p>
     * Note that the offset value is the read position at the time the error
     * was detected or around, and there is no strict definition about the
     * offset value. In that sense, it's just a hint.
     * </p>
     *
     * <p>
     * A negative value is returned if no offset information is available.
     * </p>
     *
     * @return
     *         The offset where the error was detected. If no offset
     *         information is available, a negative value is returned.
     */
    public int getOffset()
    {
        return offset;
    }
}

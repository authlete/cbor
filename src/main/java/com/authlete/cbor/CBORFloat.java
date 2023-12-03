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
import com.authlete.cbor.token.CBORTokenizer;


/**
 * CBOR single-precision floating-point number
 * (major type = 7, additional information = 26).
 *
 * <p>
 * The {@link #getValue()} method of this class returns an instance of
 * {@link Float}.
 * </p>
 *
 * <p>
 * Note that the CBOR tokenizer ({@link CBORTokenizer}) converts a CBOR
 * half-precision floating-point number into a single-precision
 * floating-point number when it creates a {@link CBORFloat} instance
 * that corresponds to the half-precision floating-point number.
 * </p>
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8949#name-floating-point-numbers-and-"
 *      >RFC 8949, 3.3. Floating-Point Numbers and Values with No Content</a>
 */
public class CBORFloat extends CBORNumber<Float>
{
    /**
     * Positive infinity.
     *
     * @since 1.6
     */
    public static final CBORFloat POSITIVE_INFINITY = new CBORFloat(Float.POSITIVE_INFINITY);


    /**
     * Negative infinity.
     *
     * @since 1.6
     */
    public static final CBORFloat NEGATIVE_INFINITY = new CBORFloat(Float.NEGATIVE_INFINITY);


    /**
     * NaN.
     *
     * @since 1.6
     */
    public static final CBORFloat NaN = new CBORFloat(Float.NaN);


    public CBORFloat(Float value)
    {
        super(value);
    }


    @Override
    public void encode(OutputStream outputStream) throws IOException
    {
        // Major = 7, Info = 26 (IEE 754 Single-Precision Float)
        //
        // | major |   info    |
        // |-------+-----------|
        // | 1 1 1 | 1 1 0 1 0 |
        //
        outputStream.write(0xFA);

        // Representation according to the IEEE 754 floating-point
        // "single format" bit layout.
        int bits = Float.floatToRawIntBits(getValue());

        // Write the bits in the network byte order. The write() method writes
        // one byte. It writes the 8 low bits and ignores the 24 high bits.
        outputStream.write(bits >> 24);
        outputStream.write(bits >> 16);
        outputStream.write(bits >>  8);
        outputStream.write(bits      );
    }
}

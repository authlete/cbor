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
 * CBOR double-precision floating-point number
 * (major type = 7, additional information = 27).
 *
 * <p>
 * The {@link #getValue()} method of this class returns an instance of
 * {@link Double}.
 * </p>
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8949#name-floating-point-numbers-and-"
 *      >RFC 8949, 3.3. Floating-Point Numbers and Values with No Content</a>
 */
public class CBORDouble extends CBORNumber<Double>
{
    public CBORDouble(Double value)
    {
        super(value);
    }


    @Override
    public void encode(OutputStream outputStream) throws IOException
    {
        // Major = 7, Info = 27 (IEE 754 Double-Precision Float)
        //
        // | major |   info    |
        // |-------+-----------|
        // | 1 1 1 | 1 1 0 1 1 |
        //
        outputStream.write(0xFB);

        // Representation according to the IEEE 754 floating-point
        // "double format" bit layout.
        long bits = Double.doubleToRawLongBits(getValue());

        // Write the bits in the network byte order.
        outputStream.write((int)((bits >> 56) & 0xFF));
        outputStream.write((int)((bits >> 48) & 0xFF));
        outputStream.write((int)((bits >> 40) & 0xFF));
        outputStream.write((int)((bits >> 32) & 0xFF));
        outputStream.write((int)((bits >> 24) & 0xFF));
        outputStream.write((int)((bits >> 16) & 0xFF));
        outputStream.write((int)((bits >>  8) & 0xFF));
        outputStream.write((int)((bits      ) & 0xFF));
    }
}

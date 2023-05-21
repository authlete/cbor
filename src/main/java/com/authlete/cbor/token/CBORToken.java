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
package com.authlete.cbor.token;


/**
 * CBOR token that the CBOR tokenizer ({@link CBORTokenizer}) constructs.
 *
 * @param <TValue>
 *         The type of the value of the CBOR token.
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8949.html#name-specification-of-the-cbor-e"
 *      >RFC 8949, 3. Specification of the CBOR Encoding</a>
 */
public abstract class CBORToken<TValue>
{
    private final int major;
    private final int info;
    private final TValue value;


    /**
     * A constructor with a major type, additional information and a value.
     *
     * @param major
     *         A major type. From 0 to 7.
     *
     * @param info
     *         Additional information. From 0 to 31.
     *
     * @param value
     *         The value of this CBOR token.
     */
    protected CBORToken(int major, int info, TValue value)
    {
        this.major = major;
        this.info  = info;
        this.value = value;
    }


    /**
     * Get the major type.
     *
     * @return
     *         The major type.
     */
    public int getMajor()
    {
        return major;
    }


    /**
     * Get the additional information.
     *
     * @return
     *         The additional information.
     */
    public int getInfo()
    {
        return info;
    }


    /**
     * Get the value.
     *
     * @return
     *         The value.
     */
    public TValue getValue()
    {
        return value;
    }
}

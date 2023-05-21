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


import java.math.BigInteger;


/**
 * Constant values used in this CBOR implementation.
 */
public class CBORConstants
{
    /**
     * A {@link Long} instance that represents the value of {@link Integer#MIN_VALUE}.
     */
    public static final Long LONG_INT_MIN = Long.valueOf(Integer.MIN_VALUE);


    /**
     * A {@link Long} instance that represents the value of {@link Integer#MAX_VALUE}.
     */
    public static final Long LONG_INT_MAX = Long.valueOf(Integer.MAX_VALUE);


    /**
     * A {@link BigInteger} instance that represents the value of {@link Integer#MIN_VALUE}.
     */
    public static final BigInteger BIG_INTEGER_INT_MIN = BigInteger.valueOf(Integer.MIN_VALUE);


    /**
     * A {@link BigInteger} instance that represents the value of {@link Integer#MAX_VALUE}.
     */
    public static final BigInteger BIG_INTEGER_INT_MAX = BigInteger.valueOf(Integer.MAX_VALUE);


    /**
     * A {@link BigInteger} instance that represents the value of {@link Long#MIN_VALUE}.
     */
    public static final BigInteger BIG_INTEGER_LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);


    /**
     * A {@link BigInteger} instance that represents the value of {@link Long#MAX_VALUE}.
     */
    public static final BigInteger BIG_INTEGER_LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);


    /**
     * A {@link BigInteger} instance that represents the value of 18446744073709551615
     * (0xFFFF_FFFF_FFFF_FFFF).
     */
    public static final BigInteger BIG_INTEGER_ULONG_MAX = new BigInteger("18446744073709551615");


    /**
     * A {@link BigInteger} instance that represents the value of -1.
     */
    public static final BigInteger BIG_INTEGER_MINUS_ONE = BigInteger.valueOf(-1);
}

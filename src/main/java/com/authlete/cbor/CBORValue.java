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
 * A class that represents a CBOR data item that has a value.
 *
 * @param <TValue>
 *         The type of the value of the CBOR data item.
 */
public abstract class CBORValue<TValue> extends CBORItem
{
    private final TValue value;


    /**
     * A constructor with a value. The given value is returned from the
     * {@link #getValue()} method.
     *
     * @param value
     *         The value of this CBOR data item. Must not be {@code null}.
     */
    protected CBORValue(TValue value)
    {
        if (value == null)
        {
            throw new NullPointerException(
                    "The value given to the constructor is null.");
        }

        this.value = value;
    }


    /**
     * Get the value of this CBOR data item. This method returns the instance
     * given to the constructor.
     *
     * @return
     *         The value of this CBOR data item.
     */
    public TValue getValue()
    {
        return value;
    }


    /**
     * Return the string representation of the value
     * (= return {@link #getValue()}{@code .toString()}).
     */
    @Override
    public String toString()
    {
        return value.toString();
    }


    /**
     * Return the value of this CBOR data item (= return {@link #getValue()}).
     */
    @Override
    public TValue parse()
    {
        return getValue();
    }
}

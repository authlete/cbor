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


import java.util.ArrayList;
import java.util.List;


/**
 * Utility to create a list of {@link CBORPair}s.
 *
 * @since 1.5
 */
public class CBORPairsBuilder
{
    private final List<CBORPair> mList = new ArrayList<>();


    /**
     * Add a pair.
     *
     * @param label
     *         The label of the pair.
     *
     * @param value
     *         The value of the pair.
     *
     * @return
     *         {@code this} object.
     */
    public CBORPairsBuilder add(CBORItem label, CBORItem value)
    {
        mList.add(new CBORPair(label, value));

        return this;
    }


    /**
     * Add a pair.
     *
     * @param label
     *         The label of the pair.
     *
     * @param value
     *         The value of the pair. An instance of {@link CBORItem}
     *         corresponding to the given object will be created (or a shared
     *         object is reused) unless the given object is already an instance
     *         of {@code CBORItem}.
     *
     * @return
     *         {@code this} object.
     */
    public CBORPairsBuilder add(CBORItem label, Object value)
    {
        return add(label, new CBORizer().cborize(value));
    }


    /**
     * Add a pair unless the given value is null.
     *
     * @param label
     *         The label of the pair.
     *
     * @param value
     *         The value of the pair.
     *
     * @return
     *         {@code this} object.
     */
    public CBORPairsBuilder addUnlessNull(CBORItem label, CBORItem value)
    {
        if (value != null)
        {
            add(label, value);
        }

        return this;
    }


    /**
     * Add a pair.
     *
     * @param label
     *         The label of the pair.
     *
     * @param value
     *         The value of the pair. An instance of {@link CBORItem}
     *         corresponding to the given object will be created (or a shared
     *         object is reused) unless the given object is already an instance
     *         of {@code CBORItem}.
     *
     * @return
     *         {@code this} object.
     */
    public CBORPairsBuilder addUnlessNull(CBORItem label, Object value)
    {
        if (value != null)
        {
            add(label, value);
        }

        return this;
    }


    /**
     * Get the list of {@link CBORPair}s that contains pairs added so far.
     *
     * @return
     *         The list of {@link CBORPair}s.
     */
    public List<CBORPair> build()
    {
        return mList;
    }
}

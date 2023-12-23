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
 * A class that represents a key-value pair of CBOR map.
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8949.html#name-major-types"
 *      >RFC 8949, 3.1. Major Types</a>
 */
public class CBORPair
{
    private final CBORItem key;
    private final CBORItem value;
    private String keyComment;
    private String valueComment;


    /**
     * A constructor with a key and a value.
     *
     * @param key
     *         A key. If {@code null} is given, {@link CBORNull#INSTANCE} is used.
     *
     * @param value
     *         A value. If {@code null} is given, {@link CBORNull#INSTANCE} is used.
     */
    public CBORPair(CBORItem key, CBORItem value)
    {
        this(key, value, /* keyComment */ null, /* valueComment */ null);
    }


    /**
     * A constructor with a key, a value and a comment.
     *
     * @param key
     *         A key. If {@code null} is given, {@link CBORNull#INSTANCE} is used.
     *
     * @param value
     *         A value. If {@code null} is given, {@link CBORNull#INSTANCE} is used.
     *
     * @param keyComment
     *         A comment for the key. This comment is referenced by the
     *         {@link #prettify(String, String)} method.
     *
     * @param valueComment
     *         A comment for the value. This comment is referenced by the
     *         {@link #prettify(String, String)} method.
     *
     * @since 1.5
     */
    public CBORPair(CBORItem key, CBORItem value, String keyComment, String valueComment)
    {
        this.key          = (key   != null) ? key   : CBORNull.INSTANCE;
        this.value        = (value != null) ? value : CBORNull.INSTANCE;
        this.keyComment   = keyComment;
        this.valueComment = valueComment;
    }


    /**
     * Get the key.
     *
     * @return
     *         The key.
     */
    public CBORItem getKey()
    {
        return key;
    }


    /**
     * Get the value.
     *
     * @return
     *         The value.
     */
    public CBORItem getValue()
    {
        return value;
    }


    /**
     * Get the comment for the key.
     *
     * <p>
     * This comment is referenced by the {@link #prettify(String, String)} method.
     * </p>
     *
     * @return
     *         The comment for the key.
     *
     * @since 1.5
     */
    public String getKeyComment()
    {
        return keyComment;
    }


    /**
     * Set the comment for the key.
     *
     * <p>
     * This comment is referenced by the {@link #prettify(String, String)} method.
     * </p>
     *
     * @param comment
     *         The comment for the key.
     *
     * @return
     *         {@code this} object.
     *
     * @since 1.5
     */
    public CBORPair setKeyComment(String comment)
    {
        this.keyComment = comment;

        return this;
    }


    /**
     * Get the comment for the value.
     *
     * <p>
     * This comment is referenced by the {@link #prettify(String, String)} method.
     * </p>
     *
     * @return
     *         The comment for the value.
     *
     * @since 1.5
     */
    public String getValueComment()
    {
        return valueComment;
    }


    /**
     * Set the comment for the value.
     *
     * <p>
     * This comment is referenced by the {@link #prettify(String, String)} method.
     * </p>
     *
     * @param comment
     *         The comment for the value
     *
     * @return
     *         {@code this} object.
     *
     * @since 1.5
     */
    public CBORPair setValueComment(String comment)
    {
        this.valueComment = comment;

        return this;
    }


    /**
     * Return {@code "<key>: <value>"}.
     */
    @Override
    public String toString()
    {
        return buildString();
    }


    private String buildString()
    {
        return String.format("%s: %s", key, value);
    }


    /**
     * Stringify this CBOR pair.
     *
     * <p>
     * This method is called from the implementation of the
     * {@link CBORPairList#prettify(String, String, Number)
     * prettify(String, String, Number)} method of the
     * {@link CBORPairList} class.
     * </p>
     *
     * @param indent
     *         The indent inherited from the upper CBOR item.
     *
     * @param indentUnit
     *         Additional indent that should be added when nested CBOR items
     *         are stringified.
     *
     * @return
     *         The string expression of this CBOR pair and all the nested
     *         CBOR items.
     *
     * @since 1.5
     */
    public String prettify(String indent, String indentUnit)
    {
        // The comment for the key
        String kComment = (getKeyComment() == null) ? ""
                : String.format("/ %s / ", getKeyComment());

        // The comment for the value
        String vComment = (getValueComment() == null) ? ""
                : String.format(" / %s /", getValueComment());

        return String.format("%s%s: %s%s",
                kComment,
                key.  prettify(indent, indentUnit, null),
                value.prettify(indent, indentUnit, null),
                vComment
        );
    }


    /**
     * Write CBOR representations of the key and the value into the specified
     * output stream.
     *
     * @param outputStream
     *         An output stream to write CBOR representations into.
     *
     * @throws IOException
     */
    public void encode(OutputStream outputStream) throws IOException
    {
        key  .encode(outputStream);
        value.encode(outputStream);
    }
}

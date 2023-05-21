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


import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;


/**
 * An output stream to write CBOR representations of Java primitives and
 * instances of common Java classes.
 */
public class CBOROutputStream extends FilterOutputStream
{
    private final CBORizer cborizer = new CBORizer();


    public CBOROutputStream(OutputStream out)
    {
        super(out);
    }


    /**
     * Write the CBOR representation of the given value into this output stream.
     *
     * <p>
     * The implementation of this method cborizes the given value with
     * {@link CBORizer#cborizeBoolean(boolean)} and calls the
     * {@code encode(OutputStream)} method with this output stream.
     * </p>
     *
     * @param value
     *         A value to be encoded and written into this output stream.
     *
     * @return
     *         A {@link CBORBoolean} instance that corresponds to the given value.
     *
     * @throws IOException
     */
    public CBORBoolean writeBoolean(boolean value) throws IOException
    {
        CBORBoolean item = cborizer.cborizeBoolean(value);

        item.encode(this);

        return item;
    }


    /**
     * Write the CBOR representation of the given value into this output stream.
     *
     * <p>
     * The implementation of this method cborizes the given value with
     * {@link CBORizer#cborizeInteger(int)} and calls the
     * {@code encode(OutputStream)} method with this output stream.
     * </p>
     *
     * @param value
     *         A value to be encoded and written into this output stream.
     *
     * @return
     *         A {@link CBORInteger} instance that corresponds to the given value.
     *
     * @throws IOException
     */
    public CBORInteger writeInteger(int value) throws IOException
    {
        CBORInteger item = cborizer.cborizeInteger(value);

        item.encode(this);

        return item;
    }


    /**
     * Write the CBOR representation of the given value into this output stream.
     *
     * <p>
     * The implementation of this method cborizes the given value with
     * {@link CBORizer#cborizeLong(long)} and calls the
     * {@code encode(OutputStream)} method with this output stream.
     * </p>
     *
     * @param value
     *         A value to be encoded and written into this output stream.
     *
     * @return
     *         A {@link CBORLong} instance that corresponds to the given value.
     *
     * @throws IOException
     */
    public CBORLong writeLong(long value) throws IOException
    {
        CBORLong item = cborizer.cborizeLong(value);

        item.encode(this);

        return item;
    }


    /**
     * Write the CBOR representation of the given value into this output stream.
     *
     * <p>
     * The implementation of this method cborizes the given value with
     * {@link CBORizer#cborizeBigInteger(BigInteger)} and calls the
     * {@code encode(OutputStream)} method with this output stream.
     * </p>
     *
     * @param value
     *         A value to be encoded and written into this output stream.
     *
     * @return
     *         A {@link CBORBigInteger} instance or {@link CBORNull#INSTANCE}
     *         that corresponds to the given value.
     *
     * @throws IOException
     */
    public CBORItem writeBigInteger(BigInteger value) throws IOException
    {
        CBORItem item = cborizer.cborizeBigInteger(value);

        item.encode(this);

        return item;
    }


    /**
     * Write the CBOR representation of the given value into this output stream.
     *
     * <p>
     * The implementation of this method cborizes the given value with
     * {@link CBORizer#cborizeFloat(float)} and calls the
     * {@code encode(OutputStream)} method with this output stream.
     * </p>
     *
     * @param value
     *         A value to be encoded and written into this output stream.
     *
     * @return
     *         A {@link CBORFloat} instance that corresponds to the given value.
     *
     * @throws IOException
     */
    public CBORFloat writeFloat(float value) throws IOException
    {
        CBORFloat item = cborizer.cborizeFloat(value);

        item.encode(this);

        return item;
    }


    /**
     * Write the CBOR representation of the given value into this output stream.
     *
     * <p>
     * The implementation of this method cborizes the given value with
     * {@link CBORizer#cborizeDouble(double)} and calls the
     * {@code encode(OutputStream)} method with this output stream.
     * </p>
     *
     * @param value
     *         A value to be encoded and written into this output stream.
     *
     * @return
     *         A {@link CBORDouble} instance that corresponds to the given value.
     *
     * @throws IOException
     */
    public CBORDouble writeDouble(double value) throws IOException
    {
        CBORDouble item = cborizer.cborizeDouble(value);

        item.encode(this);

        return item;
    }


    /**
     * Write the CBOR representation of the given value into this output stream.
     *
     * <p>
     * The implementation of this method cborizes the given value with
     * {@link CBORizer#cborizeByteArray(byte[])} and calls the
     * {@code encode(OutputStream)} method with this output stream.
     * </p>
     *
     * @param value
     *         A value to be encoded and written into this output stream.
     *
     * @return
     *         A {@link CBORByteArray} instance or {@link CBORNull#INSTANCE}
     *         that corresponds to the given value.
     *
     * @throws IOException
     */
    public CBORItem writeByteArray(byte[] value) throws IOException
    {
        CBORItem item = cborizer.cborizeByteArray(value);

        item.encode(this);

        return item;
    }


    /**
     * Write the CBOR representation of the given value into this output stream.
     *
     * <p>
     * The implementation of this method cborizes the given value with
     * {@link CBORizer#cborizeString(String)} and calls the
     * {@code encode(OutputStream)} method with this output stream.
     * </p>
     *
     * @param value
     *         A value to be encoded and written into this output stream.
     *
     * @return
     *         A {@link CBORString} instance or {@link CBORNull#INSTANCE}
     *         that corresponds to the given value.
     *
     * @throws IOException
     */
    public CBORItem writeString(String value) throws IOException
    {
        CBORItem item = cborizer.cborizeString(value);

        item.encode(this);

        return item;
    }


    /**
     * Write the CBOR representation of the given value into this output stream.
     *
     * <p>
     * The implementation of this method cborizes the given value with
     * {@link CBORizer#cborizeCollection(Collection)} and calls the
     * {@code encode(OutputStream)} method with this output stream.
     * </p>
     *
     * @param value
     *         A value to be encoded and written into this output stream.
     *
     * @return
     *         A {@link CBORItemList} instance or {@link CBORNull#INSTANCE}
     *         that corresponds to the given value.
     *
     * @throws IOException
     */
    public CBORItem writeCollection(Collection<?> collection) throws IOException
    {
        CBORItem item = cborizer.cborizeCollection(collection);

        item.encode(this);

        return item;
    }


    /**
     * Write the CBOR representation of the given value into this output stream.
     *
     * <p>
     * The implementation of this method cborizes the given value with
     * {@link CBORizer#cborizeMap(Map)} and calls the
     * {@code encode(OutputStream)} method with this output stream.
     * </p>
     *
     * @param value
     *         A value to be encoded and written into this output stream.
     *
     * @return
     *         A {@link CBORPairList} instance or {@link CBORNull#INSTANCE}
     *         that corresponds to the given value.
     *
     * @throws IOException
     */
    public CBORItem writeMap(Map<?, ?> map) throws IOException
    {
        CBORItem item = cborizer.cborizeMap(map);

        item.encode(this);

        return item;
    }


    /**
     * Write the CBOR representation of the given value into this output stream.
     *
     * <p>
     * The implementation of this method cborizes the given value with
     * {@link CBORizer#cborizeObject(Object)} and calls the
     * {@code encode(OutputStream)} method with this output stream.
     * </p>
     *
     * @param value
     *         A value to be encoded and written into this output stream.
     *
     * @return
     *         An instance of {@link CBORItem} subclass that corresponds to
     *         the given value.
     *
     * @throws IOException
     */
    public CBORItem writeObject(Object object) throws IOException
    {
        CBORItem item = cborizer.cborizeObject(object);

        item.encode(this);

        return item;
    }
}

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * A converter that converts Java primitives and instances of common Java
 * classes into instances of {@link CBORItem} subclasses.
 */
public class CBORizer
{
    /**
     * The default value of the prefix to indicate that the substring of the
     * string given to the {@link #cborizeString(String)} method should be
     * interpreted as the CBOR Diagnostic Notation.
     */
    private static final String DEFAULT_DIAGNOSTIC_NOTATION_PREFIX = "cbor:";


    /**
     * The prefix to indicate that the substring of the string given to the
     * {@link #cborizeString(String)} method should be interpreted as the
     * CBOR Diagnostic Notation.
     */
    private String mDiagnosticNotationPrefix = DEFAULT_DIAGNOSTIC_NOTATION_PREFIX;


    /**
     * The parser that interprets the CBOR Diagnostic Notation.
     */
    private CBORDiagnosticNotationParser mDiagnosticNotationParser;


    /**
     * Get the prefix to indicate that the substring of the string given to
     * the {@link #cborizeString(String)} method should be interpreted as the
     * CBOR Diagnostic Notation.
     *
     * @return
     *         The prefix to invoke the CBOR Diagnostic Notation parser.
     *
     * @since 1.5
     *
     * @see <a href="https://www.rfc-editor.org/rfc/rfc8949#section-8"
     *      >RFC 8949 Concise Binary Object Representation (CBOR),
     *       8 Diagnostic Notation</a>
     *
     * @see <a href="https://www.rfc-editor.org/rfc/rfc8610#appendix-G"
     *      >RFC 8610 Concise Data Definition Language (CDDL):
     *       A Notational Convention to Express Concise Binary Object
     *       Representation (CBOR) and JSON Data Structures,
     *       Appendix G. Extended Diagnostic Notation</a>
     */
    public String getDiagnosticNotationPrefix()
    {
        return mDiagnosticNotationPrefix;
    }


    /**
     * Set the prefix to indicate that the substring of the string given to
     * the {@link #cborizeString(String)} method should be interpreted as the
     * CBOR Diagnostic Notation.
     *
     * <p>
     * For example, if the prefix is {@code "cbor:"} (which is the default value),
     * when {@code "cbor:h'0102'"} is given to the {@link #cborizeString(String)}
     * method, the substring after the prefix, which is {@code "h'0102'"} in this
     * example, is passed to the parser's {@link
     * CBORDiagnosticNotationParser#parseItem(String) parseItem(String)} method.
     * The parser will generate a {@link CBORByteArray} instance that contains
     * the two bytes, 0x01 and 0x02.
     * </p>
     *
     * <p>
     * If {@code null} or an empty string is set as the prefix, it will result in
     * that every non-null string is processed by the parser unconditionally.
     * Therefore, for example, a string {@code "123"} will be converted into a
     * {@link CBORInteger} instance that represents the decimal integer, 123.
     * If 123 needs to be processed as a string, {@code "\"123\""} must be given.
     * </p>
     *
     * <p>
     * If a CBOR Diagnostic Notation parser is not set to this {@code CBORizer}
     * instance, this prefix is not used, and conversion based on the CBOR
     * Diagnostic Notation is not performed.
     * </p>
     *
     * @param prefix
     *         The prefix to invoke the CBOR Diagnostic Notation parser.
     *
     * @return
     *         {@code this} object.
     *
     * @since 1.5
     *
     * @see <a href="https://www.rfc-editor.org/rfc/rfc8949#section-8"
     *      >RFC 8949 Concise Binary Object Representation (CBOR),
     *       8 Diagnostic Notation</a>
     *
     * @see <a href="https://www.rfc-editor.org/rfc/rfc8610#appendix-G"
     *      >RFC 8610 Concise Data Definition Language (CDDL):
     *       A Notational Convention to Express Concise Binary Object
     *       Representation (CBOR) and JSON Data Structures,
     *       Appendix G. Extended Diagnostic Notation</a>
     */
    public CBORizer setDiagnosticNotationPrefix(String prefix)
    {
        mDiagnosticNotationPrefix = prefix;

        return this;
    }


    /**
     * Get the parser that interprets the CBOR Diagnostic Notation.
     *
     * @return
     *         The CBOR Diagnostic Notation parser.
     *
     * @since 1.5
     *
     * @see <a href="https://www.rfc-editor.org/rfc/rfc8949#section-8"
     *      >RFC 8949 Concise Binary Object Representation (CBOR),
     *       8 Diagnostic Notation</a>
     *
     * @see <a href="https://www.rfc-editor.org/rfc/rfc8610#appendix-G"
     *      >RFC 8610 Concise Data Definition Language (CDDL):
     *       A Notational Convention to Express Concise Binary Object
     *       Representation (CBOR) and JSON Data Structures,
     *       Appendix G. Extended Diagnostic Notation</a>
     */
    public CBORDiagnosticNotationParser getDiagnosticNotationParser()
    {
        return mDiagnosticNotationParser;
    }


    /**
     * Set the parser that interprets the CBOR Diagnostic Notation.
     *
     * @param parser
     *         The CBOR Diagnostic Notation parser.
     *
     * @return
     *         {@code this} object.
     *
     * @since 1.5
     *
     * @see <a href="https://www.rfc-editor.org/rfc/rfc8949#section-8"
     *      >RFC 8949 Concise Binary Object Representation (CBOR),
     *       8 Diagnostic Notation</a>
     *
     * @see <a href="https://www.rfc-editor.org/rfc/rfc8610#appendix-G"
     *      >RFC 8610 Concise Data Definition Language (CDDL):
     *       A Notational Convention to Express Concise Binary Object
     *       Representation (CBOR) and JSON Data Structures,
     *       Appendix G. Extended Diagnostic Notation</a>
     */
    public CBORizer setDiagnosticNotationParser(CBORDiagnosticNotationParser parser)
    {
        mDiagnosticNotationParser = parser;

        return this;
    }


    /**
     * Return {@link CBORBoolean#FALSE} or {@link CBORBoolean#TRUE} according
     * to the given value.
     *
     * @param value
     *         A boolean value.
     *
     * @return
     *         {@link CBORBoolean#FALSE} or {@link CBORBoolean#TRUE}.
     */
    public CBORBoolean cborizeBoolean(boolean value)
    {
        return value ? CBORBoolean.TRUE : CBORBoolean.FALSE;
    }


    /**
     * Create a {@link CBORInteger} instance with the given value.
     *
     * @param value
     *         An integer.
     *
     * @return
     *         A new {@link CBORInteger} instance.
     */
    public CBORInteger cborizeInteger(int value)
    {
        return new CBORInteger(value);
    }


    /**
     * Create a {@link CBORLong} instance with the given value.
     *
     * @param value
     *         A long integer.
     *
     * @return
     *         A new {@link CBORLong} instance.
     */
    public CBORLong cborizeLong(long value)
    {
        return new CBORLong(value);
    }


    /**
     * Create a {@link CBORBigInteger} instance with the given value.
     *
     * @param value
     *         A big integer. When {@code null} is given,
     *         {@link CBORNull#INSTANCE} is returned.
     *
     * @return
     *         A new {@link CBORBigInteger} instance or {@link CBORNull#INSTANCE}.
     */
    public CBORItem cborizeBigInteger(BigInteger value)
    {
        if (value == null)
        {
            return CBORNull.INSTANCE;
        }

        return new CBORBigInteger(value);
    }


    /**
     * Create a {@link CBORFloat} instance with the given value.
     *
     * @param value
     *         A single-precision floating-point number.
     *
     * @return
     *         A new {@link CBORFloat} instance.
     */
    public CBORFloat cborizeFloat(float value)
    {
        return new CBORFloat(value);
    }


    /**
     * Create a {@link CBORDouble} instance with the given value.
     *
     * @param value
     *         A double-precision floating-point number.
     *
     * @return
     *         A new {@link CBORDouble} instance.
     */
    public CBORDouble cborizeDouble(double value)
    {
        return new CBORDouble(value);
    }


    /**
     * Create a {@link CBORByteArray} instance with the given value.
     *
     * @param value
     *         A byte array. When {@code null} is given,
     *         {@link CBORNull#INSTANCE} is returned.
     *
     * @return
     *         A new {@link CBORByteArray} instance or {@link CBORNull#INSTANCE}.
     */
    public CBORItem cborizeByteArray(byte[] value)
    {
        if (value == null)
        {
            return CBORNull.INSTANCE;
        }

        return new CBORByteArray(value);
    }


    /**
     * Create a {@link CBORString} instance with the given value.
     *
     * <p>
     * When {@code null} is given, {@link CBORNull#INSTANCE} is returned.
     * Otherwise, a {@link CBORString} instance that represents the given
     * string is returned.
     * </p>
     *
     * <p>
     * However, if a {@link CBORDiagnosticNotationParser} instance is set to
     * this {@code CBORizer} instance (cf. {@link
     * #setDiagnosticNotationParser(CBORDiagnosticNotationParser)}), and if
     * the prefix of the given string indicates that the string should be
     * interpreted as CBOR Diagnostic Notation (cf. {@link
     * #setDiagnosticNotationPrefix(String)}), the substring after the prefix
     * is passed to the {@link CBORDiagnosticNotationParser#parseItem(String)
     * parseItem(String)} method of the parser, and the object returned from
     * the parser is returned from this method.
     * </p>
     *
     * @param value
     *         An input string.
     *
     * @return
     *         A new CBOR item built from the given string. In typical cases,
     *         a {@link CBORString} instance that represents the given string
     *         is returned.
     */
    public CBORItem cborizeString(String value)
    {
        if (value == null)
        {
            return CBORNull.INSTANCE;
        }

        // The parser for CBOR Diagnostic Notation.
        CBORDiagnosticNotationParser parser = getDiagnosticNotationParser();

        // If no parser is set to this CBORizer instance.
        if (parser == null)
        {
            // Build a CBOR text string that represents the given string.
            return new CBORString(value);
        }

        // The prefix indicating whether the given string should be interpreted
        // as CBOR Diagnostic Notation or not.
        String prefix = getDiagnosticNotationPrefix();

        // If the prefix is null or an empty string. This happens only when the
        // developer has explicitly set the prefix so because the default value
        // of the prefix is neither null nor an empty string.
        if (prefix == null || prefix.isEmpty())
        {
            // Parse the given string as CBOR Diagnostic Notation unconditionally.
            return parser.parseItem(value);
        }

        // If the given string starts with the prefix.
        if (value.startsWith(prefix))
        {
            // Parse the substring after the prefix as CBOR Diagnostic Notation.
            return parser.parseItem(value.substring(prefix.length()));
        }

        // Build a CBOR text string that represents the given string.
        return new CBORString(value);
    }


    /**
     * Create a {@link CBORItemList} instance with the given collection.
     * Contents in the given collection are converted into instances of
     * {@link CBORItem} subclasses recursively.
     *
     * @param collection
     *         A collection of objects. When {@code null} is given,
     *         {@link CBORNull#INSTANCE} is returned.
     *
     * @return
     *         A new {@link CBORItemList} instance or {@link CBORNull#INSTANCE}.
     */
    public CBORItem cborizeCollection(Collection<?> collection)
    {
        if (collection == null)
        {
            return CBORNull.INSTANCE;
        }

        List<CBORItem> list = new ArrayList<>();

        for (Object element : collection)
        {
            // Convert the element into an instance of CBORItem subclass and
            // add the obtained object into the list.
            list.add(cborize(element));
        }

        return new CBORItemList(list);
    }


    /**
     * Create a {@link CBORPairList} instance with the given map. Contents
     * in the given map are converted into instances of {@link CBORItem}
     * subclasses recursively.
     *
     * @param map
     *         A map of key-value pairs. When {@code null} is given,
     *         {@link CBORNull#INSTANCE} is returned.
     *
     * @return
     *         A new {@link CBORPairList} instance or {@link CBORNull#INSTANCE}.
     */
    public CBORItem cborizeMap(Map<?,?> map)
    {
        if (map == null)
        {
            return CBORNull.INSTANCE;
        }

        List<CBORPair> pairs = new ArrayList<>();

        for (Map.Entry<?,?> entry : map.entrySet())
        {
            // Convert the key and value into instances of CBORItem subclasses.
            CBORItem key   = cborize(entry.getKey());
            CBORItem value = cborize(entry.getValue());

            // Create an instance of CBORPair with the converted key and value
            // and add the pair to the list.
            pairs.add(new CBORPair(key, value));
        }

        return new CBORPairList(pairs);
    }


    /**
     * Convert the given object into an instance of {@link CBORItem} subclass.
     * Conversion is performed recursively as necessary.
     *
     * <p>
     * The implementation of this method delegates conversion to one of other
     * {@code cborizeXxx} methods according to the class of the object. If the
     * class of the given object is unknown, {@link #cborizeString(String)
     * cborizeString}{@code (object.toString())} is called.
     * </p>
     *
     * <p>
     * When the given object is already an instance of {@code CBORItem} subclass,
     * the given object is returned as is.
     * </p>
     *
     * <p>
     * When {@code null} is given, {@link CBORNull#INSTANCE} is returned.
     * </p>
     *
     * @param object
     *         An object to be converted to an instance of {@link CBORItem}
     *         subclass.
     *
     * @return
     *         An instance of {@link CBORItem} subclass.
     */
    public CBORItem cborizeObject(Object object)
    {
        if (object == null)
        {
            // null
            return CBORNull.INSTANCE;
        }
        else if (object instanceof Boolean)
        {
            // boolean
            return cborizeBoolean((Boolean)object);
        }
        else if (object instanceof Byte)
        {
            // byte --> int
            return cborizeInteger(((Byte)object).intValue());
        }
        else if (object instanceof Short)
        {
            // short --> int
            return cborizeInteger(((Short)object).intValue());
        }
        else if (object instanceof Integer)
        {
            // int
            return cborizeInteger((Integer)object);
        }
        else if (object instanceof Long)
        {
            // long
            return cborizeLong((Long)object);
        }
        else if (object instanceof BigInteger)
        {
            // BigInteger
            return cborizeBigInteger((BigInteger)object);
        }
        else if (object instanceof Float)
        {
            // float
            return cborizeFloat((Float)object);
        }
        else if (object instanceof Double)
        {
            // double
            return cborizeDouble((Double)object);
        }
        else if (object instanceof byte[])
        {
            // byte[]
            return cborizeByteArray((byte[])object);
        }
        else if (object instanceof String)
        {
            // String
            return cborizeString((String)object);
        }
        else if (object instanceof Collection)
        {
            // Collection
            return cborizeCollection((Collection<?>)object);
        }
        else if (object instanceof Map)
        {
            // Map
            return cborizeMap((Map<?,?>)object);
        }
        else if (object instanceof CBORItem)
        {
            // CBORItem
            return (CBORItem)object;
        }
        else
        {
            // Other type --> String
            return cborizeString(object.toString());
        }
    }


    /**
     * Convert the given object into an instance of {@link CBORItem} subclass.
     * Conversion is performed recursively as necessary.
     *
     * <p>
     * This method is an alias of {@link #cborizeObject(Object)
     * cborizeObject}{@code (object)}.
     * </p>
     *
     * @param object
     *         An object to be converted to an instance of {@link CBORItem}
     *         subclass.
     *
     * @return
     *         An instance of {@link CBORItem} subclass.
     */
    public CBORItem cborize(Object object)
    {
        return cborizeObject(object);
    }
}

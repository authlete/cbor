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
 * Parser for the CBOR Diagnostic Notation.
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
public interface CBORDiagnosticNotationParser
{
    /**
     * Parse the given string as the CBOR Diagnostic Notation and
     * generates a corresponding {@link CBORItem} instance.
     *
     * <p>
     * For example, when {@code "h'0102'"} is given, an instance of
     * {@link CBORByteArray} should be returned. Likewise, when
     * {@code "0(\"2013-03-21T20:04:00Z\")"} is given, a {@link CBORTaggedItem}
     * instance that wraps a {@link CBORString} instance should be returned.
     * </p>
     *
     * @param input
     *         A string representing a CBOR item in the CBOR Diagnostic
     *         Notation.
     *
     * @return
     *         A {@link CBORItem} instance represented by the given string.
     *
     * @throws CBORDiagnosticNotationException
     *         The given string does not conform to the CBOR Diagnostic
     *         Notation, or the parser implementation does not support
     *         the notation used in the given string.
     */
    CBORItem parseItem(String input) throws CBORDiagnosticNotationException;
}

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
 * Exception related to the CBOR Diagnostic Notation.
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
public class CBORDiagnosticNotationException extends RuntimeException
{
    private static final long serialVersionUID = 1L;


    /**
     * The default constructor.
     */
    public CBORDiagnosticNotationException()
    {
    }


    /**
     * A constructor with a message describing this exception.
     *
     * @param message
     *         A message describing this exception.
     */
    public CBORDiagnosticNotationException(String message)
    {
        super(message);

    }


    /**
     * A constructor with the cause of this exception.
     *
     * @param cause
     *         The cause of this exception.
     */
    public CBORDiagnosticNotationException(Throwable cause)
    {
        super(cause);

    }


    /**
     * A constructor with a message describing this exception and
     * the cause of this exception.
     *
     * @param message
     *         A message describing this exception.
     *
     * @param cause
     *         The cause of this exception.
     */
    public CBORDiagnosticNotationException(String message, Throwable cause)
    {
        super(message, cause);

    }
}

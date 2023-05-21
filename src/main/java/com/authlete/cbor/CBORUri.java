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
import java.net.URI;
import com.authlete.cbor.tag.CPUri;


/**
 * CBOR data item whose value is a URI.
 *
 * <p>
 * This class is used by the {@link com.authlete.cbor.tag.CPUri CPUri} tag
 * processor.
 * </p>
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8949.html#name-encoded-text"
 *      >RFC 8949 Concise Binary Object Representation (CBOR), 3.4.5.3. Encoded Text</a>
 *
 * @see CPUri
 */
public class CBORUri extends CBORValue<URI>
{
    private final boolean tagged;
    private final Number tagNumber;


    /**
     * A constructor with a URI that this CBOR data item represents.
     *
     * <p>
     * This constructor is an alias of
     * {@link #CBORUri(URI, boolean) CBORUri}{@code (value, false)}.
     * </p>
     *
     * @param value
     *         A URI that this CBOR data item represents.
     */
    public CBORUri(URI value)
    {
        this(value, false);
    }


    /**
     * A constructor with a URI that this CBOR data item represents and a
     * boolean flag that indicates whether the {@code encode(OutputStream)}
     * method of this instance emits a tag or not.
     *
     * <p>
     * This constructor is an alias of
     * {@link #CBORUri(URI, boolean, Number) CBORUri}{@code (value, tagged, 32)}.
     * </p>
     *
     * @param value
     *         A URI that this CBOR data item represents.
     *
     * @param tagged
     *         {@code true} to let the {@code encode(OutputStream}} method
     *         emit a tag before writing the text string. {@code false} to
     *         let the {@code encode(OutputStream)} method write the text
     *         string only.
     */
    public CBORUri(URI value, boolean tagged)
    {
        this(value, tagged, 32);
    }


    /**
     * A constructor with (1) a URI that this CBOR data item represents, (2)
     * a boolean flag that indicates whether the {@code encode(OutputStream)}
     * method of this instance emits a tag or not, and (3) a tag number that
     * is used when a tag is emitted.
     *
     * @param value
     *         A URI that this CBOR data item represents.
     *
     * @param tagged
     *         {@code true} to let the {@code encode(OutputStream}} method
     *         emit a tag before writing the text string. {@code false} to
     *         let the {@code encode(OutputStream)} method write the text
     *         string only.
     *
     * @param tagNumber
     *         A tag number to be emitted.
     */
    public CBORUri(URI value, boolean tagged, Number tagNumber)
    {
        super(value);

        this.tagged    = tagged;
        this.tagNumber = tagNumber;
    }


    @Override
    public void encode(OutputStream outputStream) throws IOException
    {
        // Text string
        CBORString text = new CBORString(getValue().toString());

        if (tagged)
        {
            // Emit the tag and then write the text string.
            new CBORTaggedItem(tagNumber, text).encode(outputStream);
        }
        else
        {
            // Write the text string only.
            text.encode(outputStream);
        }
    }
}

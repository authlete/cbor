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
package com.authlete.cose;


import com.authlete.cbor.CBORByteArray;
import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORNull;
import com.authlete.cbor.CBORTaggedItem;


/**
 * COSE Message
 *
 * <p>
 * This class represents {@code COSE_Untagged_Message} which is defined in
 * <a href="https://www.rfc-editor.org/rfc/rfc9052.html#section-2">2. Basic
 * COSE Structure</a> of <a href="https://www.rfc-editor.org/rfc/rfc9052.html"
 * >RFC 9052</a>.
 * </p>
 *
 * @since 1.1
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9052.html#section-2"
 *      >RFC 9052, 2. Basic COSE Structure</a>
 */
public abstract class COSEMessage extends COSEObject
{
    private final COSEMessageType type;


    /**
     * A constructor.
     *
     * @param type
     *         The type of this COSE message.
     *
     * @param protectedHeader
     *         The protected header. Must not be null.
     *
     * @param unprotectedHeader
     *         The unprotected header. Must not be null.
     *
     * @param content
     *         The content. Must be either {@link CBORByteArray} or
     *         {@link CBORNull}.
     *
     * @param additionalItems
     *         Additional items.
     *
     * @throws IllegalArgumentException
     *         {@code type} is null,
     *         {@code protectedHeader} is null, {@code unprotectedHeader} is
     *         null, or {@code content} is neither a {@link CBORByteArray}
     *         instance nor a {@link CBORNull} instance.
     */
    public COSEMessage(
            COSEMessageType type,
            COSEProtectedHeader protectedHeader,
            COSEUnprotectedHeader unprotectedHeader,
            CBORItem content,
            CBORItem... additionalItems) throws IllegalArgumentException
    {
        super(protectedHeader, unprotectedHeader, content, additionalItems);

        if (type == null)
        {
            throw new IllegalArgumentException("The COSE message type is missing.");
        }

        this.type = type;

        setComment(type.getName());
    }


    /**
     * Get the type of this COSE message.
     *
     * @return
     *         The type of this COSE message.
     *
     * @since 1.4
     */
    public COSEMessageType getType()
    {
        return type;
    }


    /**
     * Get the tag number of this COSE message.
     *
     * <blockquote>
     * <table border="1" cellpadding="5" style="border-collapse: collapse;">
     *
     *   <tr bgcolor="orange">
     *     <th>CBOR Tag</th>
     *     <th>Data Item</th>
     *     <th>Java Class</th>
     *   </tr>
     *
     *   <tr>
     *     <td>16</td>
     *     <td>{@code COSE_Encrypt0}</td>
     *     <td>{@link COSEEncrypt0}</td>
     *   </tr>
     *
     *   <tr>
     *     <td>17</td>
     *     <td>{@code COSE_Mac0}</td>
     *     <td>{@link COSEMac0}</td>
     *   </tr>
     *
     *   <tr>
     *     <td>18</td>
     *     <td>{@code COSE_Sign1}</td>
     *     <td>{@link COSESign1}</td>
     *   </tr>
     *
     *   <tr>
     *     <td>96</td>
     *     <td>{@code COSE_Encrypt}</td>
     *     <td>{@link COSEEncrypt}</td>
     *   </tr>
     *
     *   <tr>
     *     <td>97</td>
     *     <td>{@code COSE_Mac}</td>
     *     <td>{@link COSEMac}</td>
     *   </tr>
     *
     *   <tr>
     *     <td>98</td>
     *     <td>{@code COSE_Sign}</td>
     *     <td>{@link COSESign}</td>
     *   </tr>
     *
     * </table>
     * </blockquote>
     *
     * @return
     *         The tag number.
     *
     * @see <a href="https://www.rfc-editor.org/rfc/rfc9052.html#section-2"
     *      >RFC 9052, 2. Basic COSE Structure</a>
     */
    public int getTagNumber()
    {
        return getType().getTagNumber();
    }


    /**
     * Wrap this COSE message with a CBOR tag.
     *
     * <p>
     * This method is equivalent to "<code>new {@link CBORTaggedItem#CBORTaggedItem(Number, CBORItem)
     * CBORTaggedItem}({@link #getTagNumber()}, this)</code>".
     * </p>
     *
     * @return
     *         A tagged COSE message.
     */
    public CBORTaggedItem getTagged()
    {
        return new CBORTaggedItem(getTagNumber(), this);
    }
}

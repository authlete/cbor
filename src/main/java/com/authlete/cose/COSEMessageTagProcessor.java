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


import com.authlete.cbor.CBORDecoderException;
import com.authlete.cbor.CBORItem;
import com.authlete.cbor.tag.CBORTagProcessor;


/**
 * A tag processor for COSE messages.
 *
 * @since 1.1
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9052.html"
 *      >RFC 9052: CBOR Object Signing and Encryption (COSE): Structures and Process</a>
 */
public class COSEMessageTagProcessor implements CBORTagProcessor
{
    public static COSEMessageTagProcessor INSTANCE = new COSEMessageTagProcessor();


    @Override
    public CBORItem process(Number tagNumber, CBORItem tagContent) throws CBORDecoderException
    {
        // It is known that the tag number is in the range of Java 'int'.
        int tag = tagNumber.intValue();

        COSEMessage message;

        try
        {
            // Interpret the tag content as a COSE message.
            message = buildMessage(tag, tagContent);
        }
        catch (Exception cause)
        {
            // Failed to parse the CBOR data item as a COSE message.
            throw new CBORDecoderException(String.format(
                    "The CBOR data item following the tag number '%s' failed to be parsed as a COSE message: %s",
                    tagNumber.toString(), cause.getMessage()), cause);
        }

        if (message == null)
        {
            // This tag processor has been registered for the wrong tag number.
            throw new CBORDecoderException(String.format(
                    "%s has been registered for the wrong tag number '%s'.",
                    getClass().getSimpleName(), tagNumber.toString()));
        }

        return message.getTagged();
    }


    private static COSEMessage buildMessage(int tag, CBORItem item) throws COSEException
    {
        switch (tag)
        {
            case 16:
                // COSE_Encrypt0
                return COSEEncrypt0.build(item);

            case 17:
                // COSE_Mac0
                return COSEMac0.build(item);

            case 18:
                // COSE_Sign1
                return COSESign1.build(item);

            case 96:
                // COSE_Encrypt
                return COSEEncrypt.build(item);

            case 97:
                // COSE_Mac
                return COSEMac.build(item);

            case 98:
                // COSE_Sign
                return COSESign.build(item);

            default:
                // This never happens unless this tag processor has been
                // registered for the wrong tag number.
                return null;
        }
    }
}

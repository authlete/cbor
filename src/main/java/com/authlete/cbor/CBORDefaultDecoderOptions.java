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


import com.authlete.cbor.tag.CPBignum;
import com.authlete.cose.COSEMessageTagProcessor;
import com.authlete.cwt.CWTTagProcessor;


/**
 * The default options for the CBOR decoder ({@link CBORDecoder}).
 *
 * <p>
 * The following tag processors are registered.
 * </p>
 *
 * <blockquote>
 * <table border="1" cellpadding="5" style="border-collapse: collapse;">
 *
 *   <tr bgcolor="orange">
 *     <th>tag number</th>
 *     <th>tag processor</th>
 *     <th>description</th>
 *   </tr>
 *
 *   <tr>
 *     <td>2</td>
 *     <td>{@link CPBignum}</td>
 *     <td><a href="https://www.rfc-editor.org/rfc/rfc8949.html#bignums">RFC 8949, 3.4.3. Bignums</a></td>
 *   </tr>
 *
 *   <tr>
 *     <td>3</td>
 *     <td>{@link CPBignum}</td>
 *     <td><a href="https://www.rfc-editor.org/rfc/rfc8949.html#bignums">RFC 8949, 3.4.3. Bignums</a></td>
 *   </tr>
 *
 *   <tr>
 *     <td>16</td>
 *     <td>{@link COSEMessageTagProcessor}</td>
 *     <td><a href="https://www.rfc-editor.org/rfc/rfc9052.html#section-5.2">RFC 9052, 5.2. Single Recipient Encrypted</a> (COSE_Encrypt0)</td>
 *   </tr>
 *
 *   <tr>
 *     <td>17</td>
 *     <td>{@link COSEMessageTagProcessor}</td>
 *     <td><a href="https://www.rfc-editor.org/rfc/rfc9052.html#section-6.2">RFC 9052, 6.2. MACed Messages with Implicit Key</a> (COSE_Mac0)</td>
 *   </tr>
 *
 *   <tr>
 *     <td>18</td>
 *     <td>{@link COSEMessageTagProcessor}</td>
 *     <td><a href="https://www.rfc-editor.org/rfc/rfc9052.html#section-4.2">RFC 9052, 4.2. Signing with One Signer</a> (COSE_Sign1)</td>
 *   </tr>
 *
 *   <tr>
 *     <td>61</td>
 *     <td>{@link CWTTagProcessor}</td>
 *     <td><a href="https://www.rfc-editor.org/rfc/rfc8392.html#section-6">RFC 8392, 6. CWT CBOR Tag</a></td>
 *   </tr>
 *
 *   <tr>
 *     <td>96</td>
 *     <td>{@link COSEMessageTagProcessor}</td>
 *     <td><a href="https://www.rfc-editor.org/rfc/rfc9052.html#section-5.1">RFC 9052, 5.1. Enveloped COSE Structure</a> (COSE_Encrypt)</td>
 *   </tr>
 *
 *   <tr>
 *     <td>97</td>
 *     <td>{@link COSEMessageTagProcessor}</td>
 *     <td><a href="https://www.rfc-editor.org/rfc/rfc9052.html#section-6.1">RFC 9052, 6.1. MACed Message with Recipients</a> (COSE_Mac)</td>
 *   </tr>
 *
 *   <tr>
 *     <td>98</td>
 *     <td>{@link COSEMessageTagProcessor}</td>
 *     <td><a href="https://www.rfc-editor.org/rfc/rfc9052.html#section-4.1">RFC 9052, 4.1. Signing with One or More Signers</a> (COSE_Sign)</td>
 *   </tr>
 *
 * </table>
 * </blockquote>
 */
public class CBORDefaultDecoderOptions extends CBORDecoderOptions
{
    public CBORDefaultDecoderOptions()
    {
        // RFC 8949 CBOR, 3.4.3. Bignums
        setTagProcessor(2, CPBignum.INSTANCE);
        setTagProcessor(3, CPBignum.INSTANCE);

        // RFC 9052 COSE
        setTagProcessor(16, COSEMessageTagProcessor.INSTANCE);  // 16: COSE_Encrypt0
        setTagProcessor(17, COSEMessageTagProcessor.INSTANCE);  // 17: COSE_Mac0
        setTagProcessor(18, COSEMessageTagProcessor.INSTANCE);  // 18: COSE_Sign1

        // RFC 8392 CWT
        setTagProcessor(61, CWTTagProcessor.INSTANCE);

        // RFC 9052 COSE
        setTagProcessor(96, COSEMessageTagProcessor.INSTANCE);  // 96: COSE_Encrypt
        setTagProcessor(97, COSEMessageTagProcessor.INSTANCE);  // 97: COSE_Mac
        setTagProcessor(98, COSEMessageTagProcessor.INSTANCE);  // 98: COSE_Sign
    }
}

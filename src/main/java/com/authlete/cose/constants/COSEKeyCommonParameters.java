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
package com.authlete.cose.constants;


/**
 * COSE Key Common Parameters
 *
 * <blockquote>
 * <table border="1" cellpadding="5" style="border-collapse: collapse;">
 *
 *   <tr bgcolor="orange">
 *     <th>Name</th>
 *     <th>Label</th>
 *     <th>CBOR Type</th>
 *     <th>Description</th>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #KTY kty}</td>
 *     <td>1</td>
 *     <td>tstr / int</td>
 *     <td>Identification of the key type</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #KID kid}</td>
 *     <td>2</td>
 *     <td>bstr</td>
 *     <td>Key identification value - match to kid in message</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #ALG alg}</td>
 *     <td>3</td>
 *     <td>tstr / int</td>
 *     <td>Key usage restriction to this algorithm</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #KEY_OPS key_ops}</td>
 *     <td>4</td>
 *     <td>[+ (tstr/int)]</td>
 *     <td>Restrict set of permissible operations</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #BASE_IV Base IV}</td>
 *     <td>5</td>
 *     <td>bstr</td>
 *     <td>Base IV to be XORed with Partial IVs</td>
 *   </tr>
 *
 * </table>
 * </blockquote>
 *
 * @since 1.1
 *
 * @see <a href="https://www.iana.org/assignments/cose/cose.xhtml#key-common-parameters"
 *      >IANA: COSE Key Common Parameters</a>
 */
public final class COSEKeyCommonParameters
{
    /** kty (1) */
    public static final int KTY = 1;

    /** kid (2) */
    public static final int KID = 2;

    /** alg (3) */
    public static final int ALG = 3;

    /** key_ops (4) */
    public static final int KEY_OPS = 4;

    /** Base IV (5) */
    public static final int BASE_IV = 5;


    private COSEKeyCommonParameters()
    {
    }
}

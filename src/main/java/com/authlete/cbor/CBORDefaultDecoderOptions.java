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
 *     <td><a href="https://www.rfc-editor.org/rfc/rfc8949#bignums">RFC 8949, 3.4.3. Bignums</a></td>
 *   </tr>
 *
 *   <tr>
 *     <td>3</td>
 *     <td>{@link CPBignum}</td>
 *     <td><a href="https://www.rfc-editor.org/rfc/rfc8949#bignums">RFC 8949, 3.4.3. Bignums</a></td>
 *   </tr>
 *
 * </table>
 * </blockquote>
 */
public class CBORDefaultDecoderOptions extends CBORDecoderOptions
{
    public CBORDefaultDecoderOptions()
    {
        // RFC 8949, 3.4.3. Bignums
        setTagProcessor(2, CPBignum.INSTANCE);
        setTagProcessor(3, CPBignum.INSTANCE);
    }
}

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
 * COSE Key Operations
 *
 * <blockquote>
 * <table border="1" cellpadding="5" style="border-collapse: collapse;">
 *
 *   <tr bgcolor="orange">
 *     <th>Name</th>
 *     <th>Value</th>
 *     <th>Description</th>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #SIGN sign}</td>
 *     <td>1</td>
 *     <td>The key is used to create signatures. Requires private key fields.</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #VERIFY verify}</td>
 *     <td>2</td>
 *     <td>The key is used for verification of signatures.</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #ENCRYPT encrypt}</td>
 *     <td>3</td>
 *     <td>The key is used for key transport encryption.</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #DECRYPT decrypt}</td>
 *     <td>4</td>
 *     <td>The key is used for key transport decryption. Requires private key fields.</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #WRAP_KEY wrap key}</td>
 *     <td>5</td>
 *     <td>The key is used for key wrap encryption.</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #UNWRAP_KEY unwrap key}</td>
 *     <td>6</td>
 *     <td>The key is used for key wrap decryption. Requires private key fields.</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #DERIVE_KEY derive key}</td>
 *     <td>7</td>
 *     <td>The key is used for deriving keys. Requires private key fields.</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #DERIVE_BITS derive bits}</td>
 *     <td>8</td>
 *     <td>The key is used for deriving bits not to be used as a key. Requires private key fields.</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #MAC_CREATE MAC create}</td>
 *     <td>9</td>
 *     <td>The key is used for creating MACs.</td>
 *   </tr>
 *
 *   <tr>
 *     <td>{@link #MAC_VERIFY MAC verify}</td>
 *     <td>10</td>
 *     <td>The key is used for validating MACs.</td>
 *   </tr>
 *
 * </table>
 * </blockquote>
 *
 * @since 1.1
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9052.html#x-table-key-ops"
 *      >RFC 9052, Table 5: Key Operation Values</a>
 */
public final class COSEKeyOperations
{
    /** sign (1) */
    public static final int SIGN = 1;

    /** verify (2) */
    public static final int VERIFY = 2;

    /** encrypt (3) */
    public static final int ENCRYPT = 3;

    /** decrypt (4) */
    public static final int DECRYPT = 4;

    /** wrap key (5) */
    public static final int WRAP_KEY = 5;

    /** unwrap key (6) */
    public static final int UNWRAP_KEY = 6;

    /** derive key (7) */
    public static final int DERIVE_KEY = 7;

    /** derive bits (8) */
    public static final int DERIVE_BITS = 8;

    /** MAC create (9) */
    public static final int MAC_CREATE = 9;

    /** MAC verify (10) */
    public static final int MAC_VERIFY = 10;


    private COSEKeyOperations()
    {
    }
}

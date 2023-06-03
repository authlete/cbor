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


import java.security.Key;


/**
 * Key getter.
 *
 * @since 1.1
 *
 * @see COSESigner
 * @see COSEVerifier
 */
public interface KeyGetter
{
    /**
     * Get a key suitable for the key operation with the algorithm.
     *
     * @param keyOperation
     *         The intended key operation such as
     *         {@link com.authlete.cose.constants.COSEKeyOperations#SIGN SIGN} and
     *         {@link com.authlete.cose.constants.COSEKeyOperations#VERIFY VERIFY}.
     *
     * @param algorithm
     *         The integer identifier of the algorithm for the key operation such as
     *         {@link com.authlete.cose.constants.COSEAlgorithms#ES256 ES256}.
     *
     * @param keyID
     *         A key ID of the target key. This may be null.
     *
     * @return
     *         A key suitable for the key operation and the algorithm.
     *         {@code null} if a suitable key is not available.
     *
     * @throws COSEException
     */
    Key get(int keyOperation, int algorithm, byte[] keyID) throws COSEException;
}

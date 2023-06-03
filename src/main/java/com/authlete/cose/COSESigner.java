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
import com.authlete.cose.constants.COSEAlgorithms;
import com.authlete.cose.constants.COSEKeyOperations;


/**
 * A signer for COSE.
 *
 * @since 1.1
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9052.html#section-4"
 *      >RFC 9052, 4. Signing Objects</a>
 */
public class COSESigner
{
    private final Key key;
    private final KeyGetter keyGetter;


    /**
     * A constructor with a private key for signing.
     *
     * @param key
     *         A private key for signing. Must not be null.
     */
    public COSESigner(Key key)
    {
        if (key == null)
        {
            throw new IllegalArgumentException("'key' must not be null.");
        }

        this.key       = key;
        this.keyGetter = null;
    }


    /**
     * A constructor with a key getter that is called later when signing is
     * performed.
     *
     * <p>
     * When signing is performed, the {@link KeyGetter#get(int, int, byte[])
     * get} method of the key getter is called with the first argument
     * {@link COSEKeyOperations#SIGN}.
     * </p>
     *
     * @param keyGetter
     *         A key getter that returns a private key for signing.
     *         Must not be null.
     */
    public COSESigner(KeyGetter keyGetter)
    {
        if (keyGetter == null)
        {
            throw new IllegalArgumentException("'keyGetter' must not be null.");
        }

        this.key       = null;
        this.keyGetter = keyGetter;
    }


    /**
     * Generate a signature for the Sig Structure with the specified algorithm.
     *
     * @param structure
     *         A Sig Structure for which a signature is generated.
     *
     * @param algorithm
     *         The integer identifier of an algorithm such as -7 (which means
     *         {@link COSEAlgorithms#ES256 ES256}). Identifiers are listed in
     *         <a href=
     *         "https://www.iana.org/assignments/cose/cose.xhtml#algorithms"
     *         >IANA: COSE Algorithms</a>.
     *
     * @return
     *         A generated signature.
     *
     * @throws COSEException
     */
    public byte[] sign(SigStructure structure, int algorithm) throws COSEException
    {
        return sign(structure, algorithm, (byte[])null);
    }


    /**
     * Generate a signature for the Sig Structure with the specified algorithm.
     *
     * @param structure
     *         A Sig Structure for which a signature is generated.
     *
     * @param algorithm
     *         The integer identifier of an algorithm such as -7 (which means
     *         {@link COSEAlgorithms#ES256 ES256}). Identifiers are listed in
     *         <a href=
     *         "https://www.iana.org/assignments/cose/cose.xhtml#algorithms"
     *         >IANA: COSE Algorithms</a>.
     *
     * @param keyID
     *         The key ID of the private key to use. This argument has a meaning
     *         only when this {@code COSESigner} instance has been created with
     *         a key getter. The key ID is passed to the
     *         {@link KeyGetter#get(int, int, byte[]) get} method of the key
     *         getter.
     *
     * @return
     *         A generated signature.
     *
     * @throws COSEException
     */
    public byte[] sign(SigStructure structure, int algorithm, byte[] keyID) throws COSEException
    {
        // Get a key for signing the structure.
        Key key = getKey(algorithm, keyID);

        // Generate a signature.
        return sign(key, algorithm, structure.encode());
    }


    private Key getKey(int algorithm, byte[] keyID) throws COSEException
    {
        if (key != null)
        {
            return key;
        }

        Key obtainedKey = keyGetter.get(COSEKeyOperations.SIGN, algorithm, keyID);

        if (obtainedKey == null)
        {
            throw new COSEException(String.format(
                    "A key for signing with the algorithm '%d' was not found.", algorithm));
        }

        return obtainedKey;
    }


    /**
     * Generate a signature for the data with the key and the algorithm.
     *
     * <p>
     * This method can be used for general purposes without depending on COSE
     * data structures such as {@code Sig_structure}.
     * </p>
     *
     * @param key
     *         A private key for signing.
     *
     * @param alg
     *         The integer identifier of an algorithm such as -7 (which means
     *         {@link COSEAlgorithms#ES256 ES256}). Identifiers are listed in
     *         <a href=
     *         "https://www.iana.org/assignments/cose/cose.xhtml#algorithms"
     *         >IANA: COSE Algorithms</a>.
     *
     * @param data
     *         Data for which a signature is generated.
     *
     * @return
     *         A generated signature.
     *
     * @throws COSEException
     */
    public static byte[] sign(Key key, int alg, byte[] data) throws COSEException
    {
        switch (alg)
        {
            // ECDSA
            case COSEAlgorithms.ES256:
            case COSEAlgorithms.ES384:
            case COSEAlgorithms.ES512:
                return ECDSA.sign(key, alg, data);

            // TODO

            default:
                throw new COSEException(String.format(
                        "The algorithm '%d' is not supported.", alg));
        }
    }
}

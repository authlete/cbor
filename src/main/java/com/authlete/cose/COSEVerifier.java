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
import com.authlete.cbor.CBORItem;
import com.authlete.cose.constants.COSEAlgorithms;
import com.authlete.cose.constants.COSEKeyOperations;


/**
 * A verifier for COSE.
 *
 * @since 1.1
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9052.html#section-4"
 *      >RFC 9052, 4. Signing Objects</a>
 */
public class COSEVerifier
{
    private final Key key;
    private final KeyGetter keyGetter;


    /**
     * A constructor with a public key for verification.
     *
     * @param key
     *         A public key for verification. Must not be null.
     */
    public COSEVerifier(Key key)
    {
        if (key == null)
        {
            throw new IllegalArgumentException("'key' must not be null.");
        }

        this.key       = key;
        this.keyGetter = null;
    }


    /**
     * A constructor with a key getter that is called later when verification
     * is performed.
     *
     * <p>
     * When verification is performed, the {@link KeyGetter#get(int, int, byte[])
     * get} method of the key getter is called with the first argument
     * {@link COSEKeyOperations#VERIFY}.
     * </p>
     *
     * @param keyGetter
     *         A key getter that returns a public key for verification.
     *         Must not be null.
     */
    public COSEVerifier(KeyGetter keyGetter)
    {
        if (keyGetter == null)
        {
            throw new IllegalArgumentException("'keyGetter' must not be null.");
        }

        this.key       = null;
        this.keyGetter = keyGetter;
    }


    /**
     * Verify a {@code COSE_Sign} object.
     *
     * <p>
     * This method is an alias of {@link #verify(COSESign, byte[])
     * verify}{@code (sign, (byte[])null)}.
     * </p>
     *
     * @param sign
     *         A {@code COSE_Sign} object to verify.
     *
     * @return
     *         {@code true} if any of signatures in the {@code COSE_Sign}
     *         object is valid. {@code false} if none of signatures in the
     *         {@code COSE_Sign} object are valid.
     *
     * @throws COSEException
     */
    public boolean verify(COSESign sign) throws COSEException
    {
        return verify(sign, (byte[])null);
    }


    /**
     * Verify a {@code COSE_Sign} object.
     *
     * @param sign
     *         A {@code COSE_Sign} object to verify.
     *
     * @param externalData
     *         Optional external data.
     *
     * @return
     *         {@code true} if any of signatures in the {@code COSE_Sign}
     *         object is valid. {@code false} if none of signatures in the
     *         {@code COSE_Sign} object are valid.
     *
     * @throws COSEException
     */
    public boolean verify(COSESign sign, byte[] externalData) throws COSEException
    {
        // For each signature in the list of signatures.
        for (CBORItem signature : sign.getSignatures().getItems())
        {
            // If the signature of the COSE_Signature is valid.
            if (verify(sign, (COSESignature)signature, externalData))
            {
                // The COSESign object has been verified as valid.
                return true;
            }
        }

        // None of the signatures have passed the verification.
        return false;
    }


    /**
     * Verify a combination of {@code COSE_Sign} and {@code COSE_Signature}.
     *
     * <p>
     * This method is an alias of {@link #verify(COSESign, COSESignature, byte[])
     * verify}{@code (sign, signature, (byte[])null)}.
     * </p>
     *
     * @param sign
     *         A {@code COSE_Sign} object to verify.
     *
     * @param signature
     *         A {@code COSE_Signature} object to verify.
     *
     * @return
     *         {@code true} if the signature of the {@code COSE_Signature}
     *         object is valid. {@code false} if the signature is invalid.
     *
     * @throws COSEException
     */
    public boolean verify(COSESign sign, COSESignature signature) throws COSEException
    {
        return verify(sign, signature, (byte[])null);
    }


    /**
     * Verify a combination of {@code COSE_Sign} and {@code COSE_Signature}.
     *
     * <p>
     * This method is called repeatedly from within {@link #verify(COSESign, byte[])}
     * for each {@code COSE_Signature} object in the {@code COSE_Sign} object.
     * </p>
     *
     * @param sign
     *         A {@code COSE_Sign} object to verify.
     *
     * @param signature
     *         A {@code COSE_Signature} object to verify.
     *
     * @param externalData
     *         Optional external data.
     *
     * @return
     *         {@code true} if the signature of the {@code COSE_Signature}
     *         object is valid. {@code false} if the signature is invalid.
     *
     * @throws COSEException
     */
    public boolean verify(
            COSESign sign, COSESignature signature, byte[] externalData) throws COSEException
    {
        // 'alg' (1) in the protected header or the unprotected header.
        int alg = retrieveAlg(signature);

        // 'kid' (4) in the protected header or the unprotected header.
        byte[] kid = retrieveKid(signature);

        // Get a key for verifying the signature.
        Key key = getKey(alg, kid);

        // Prepare "ToBeSigned" (RFC 9052 Section 4.4), which is the data
        // for which the signature was generated.
        SigStructure toBeSigned = new SigStructureBuilder()
                .sign(sign).signature(signature)
                .externalData(externalData).build();

        // Verify the signature.
        return verify(key, alg, toBeSigned.encode(), signature.getSignature().getValue());
    }


    /**
     * Verify a {@code COSE_Sign1} object.
     *
     * <p>
     * This method is an alias of {@link #verify(COSESign1, byte[])
     * verify}{@code (sign1, (byte[])null)}.
     * </p>
     *
     * @param sign1
     *         A {@code COSE_Sign1} object to verify.
     *
     * @return
     *         {@code true} if the signature of the {@code COSE_Sign1} object
     *         is valid. {@code false} if the signature is invalid.
     *
     * @throws COSEException
     */
    public boolean verify(COSESign1 sign1) throws COSEException
    {
        return verify(sign1, (byte[])null);
    }


    /**
     * Verify a {@code COSE_Sign1} object.
     *
     * @param sign1
     *         A {@code COSE_Sign1} object to verify.
     *
     * @param externalData
     *         Optional external data.
     *
     * @return
     *         {@code true} if the signature of the {@code COSE_Sign1} object
     *         is valid. {@code false} if the signature is invalid.
     *
     * @throws COSEException
     */
    public boolean verify(COSESign1 sign1, byte[] externalData) throws COSEException
    {
        // 'alg' (1) in the protected header or the unprotected header.
        int alg = retrieveAlg(sign1);

        // 'kid' (4) in the protected header or the unprotected header.
        byte[] kid = retrieveKid(sign1);

        // Get a key for verifying the signature.
        Key key = getKey(alg, kid);

        // Prepare "ToBeSigned" (RFC 9052 Section 4.4), which is the data
        // for which the signature was generated.
        SigStructure toBeSigned = new SigStructureBuilder()
                .sign1(sign1).externalData(externalData).build();

        // Verify the signature.
        return verify(key, alg, toBeSigned.encode(), sign1.getSignature().getValue());
    }


    private int retrieveAlg(COSEObject object) throws COSEException
    {
        // 'alg' (1) in the protected header.
        Object alg = object.getProtectedHeader().getAlg();

        if (alg == null)
        {
            // 'alg' (1) in the unprotected header.
            alg = object.getUnprotectedHeader().getAlg();
        }

        if (alg == null)
        {
            throw new COSEException(
                    "Neither the protected header nor the unprotected header contain the 'alg' (1) parameter.");
        }

        if (alg instanceof Number)
        {
            // Convert the object to Java 'int'. All known identifiers are
            // in the range of Java 'int'.
            return ((Number)alg).intValue();
        }

        // alg is a string.

        // Try to convert the algorithm name to an integer identifier.
        int identifier = COSEAlgorithms.getValueByName((String)alg);

        // If the algorithm is not known.
        if (identifier == 0)
        {
            throw new COSEException(String.format(
                    "The algorithm '%s' is not supported.", (String)alg));
        }

        return identifier;
    }


    private byte[] retrieveKid(COSEObject object)
    {
        // 'kid' (4) in the protected header.
        byte[] kid = object.getProtectedHeader().getKid();

        if (kid == null)
        {
            // 'kid' (4) in the unprotected header.
            kid = object.getUnprotectedHeader().getKid();
        }

        return kid;
    }


    private Key getKey(int algorithm, byte[] keyID) throws COSEException
    {
        if (key != null)
        {
            return key;
        }

        Key obtainedKey = keyGetter.get(COSEKeyOperations.VERIFY, algorithm, keyID);

        if (obtainedKey == null)
        {
            throw new COSEException(String.format(
                    "A key for verification with the algorithm '%d' was not found.", algorithm));
        }

        return obtainedKey;
    }


    /**
     * Verify the signature for the data with the key and the algorithm.
     *
     * <p>
     * This method can be used for general purposes without depending on COSE
     * data structures such as {@code COSE_Sign} and {@code COSE_Sign1}.
     * </p>
     *
     * @param key
     *         A public key for verification.
     *
     * @param alg
     *         The integer identifier of an algorithm such as -7 (which means
     *         {@link COSEAlgorithms#ES256 ES256}). Identifiers are listed in
     *         <a href=
     *         "https://www.iana.org/assignments/cose/cose.xhtml#algorithms"
     *         >IANA: COSE Algorithms</a>.
     *
     * @param data
     *         Data for which the signature was generated.
     *
     * @param signature
     *         A signature to verify.
     *
     * @return
     *         {@code true} if the signature is valid. {@code false} if the
     *         signature is invalid.
     *
     * @throws COSEException
     */
    public static boolean verify(
            Key key, int alg, byte[] data, byte[] signature) throws COSEException
    {
        switch (alg)
        {
            // ECDSA
            case COSEAlgorithms.ES256:
            case COSEAlgorithms.ES384:
            case COSEAlgorithms.ES512:
                return ECDSA.verify(key, alg, data, signature);
            case COSEAlgorithms.EdDSA:
                return EdDSA.verify(key, data, signature);

            default:
                throw new COSEException(String.format(
                        "The algorithm '%d' is not supported.", alg));
        }
    }
}

/*
 * Copyright (C) 2023-2025 Authlete, Inc.
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


import static com.authlete.cose.ECDSAConstants.PARAMETER_SPEC_P256;
import static com.authlete.cose.ECDSAConstants.PARAMETER_SPEC_P384;
import static com.authlete.cose.ECDSAConstants.PARAMETER_SPEC_P521;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import com.authlete.cose.constants.COSEAlgorithms;
import com.authlete.cose.constants.COSEEllipticCurves;


/**
 * ECDSA operations
 *
 * @since 1.1
 *
 * @see <a href="https://www.secg.org/sec1-v2.pdf"
 *      >SEC 1: Elliptic Curve Cryptography</a>
 */
class ECDSA
{
    private static final BigInteger TWO   = new BigInteger("2");
    private static final BigInteger THREE = new BigInteger("3");
    private static final boolean beforeJre9;
    private static Method sSqrtAndRemainder;


    static
    {
        // True if the version of the JRE is older than 9.
        beforeJre9 = System.getProperty("java.version").matches("^1\\.[0-8](\\..*)?$");
    }


    /**
     * Create an {@link ECPrivateKey} instance from the parameters.
     */
    static ECPrivateKey createPrivateKey(Object crv, byte[] d) throws COSEException
    {
        if (d == null)
        {
            throw new COSEException("The 'd' parameter is missing.");
        }

        // Key spec for the private key.
        ECPrivateKeySpec keySpec = createPrivateKeySpec(crv, d);

        try
        {
            // Create an ECPrivateKey instance.
            return (ECPrivateKey)getKeyFactory().generatePrivate(keySpec);
        }
        catch (InvalidKeySpecException cause)
        {
            throw new COSEException(String.format(
                    "The key spec is invalid: %s", cause.getMessage()), cause);
        }
    }


    /**
     * Create key spec for the private key.
     */
    private static ECPrivateKeySpec createPrivateKeySpec(Object crv, byte[] d) throws COSEException
    {
        // Get the parameter spec of the curve.
        ECParameterSpec paramSpec = getParameterSpec(crv);

        // Key spec for the private key.
        //
        // The first argument of the BigInteger constructor is used to ensure
        // that the private key value is non-negative. For details, see Issue 15.
        //
        //   Issue 15: JDK 21 and InvalidKeyException: The private key must be within the range [1, n - 1]
        //   https://github.com/authlete/cbor/issues/15
        //
        return new ECPrivateKeySpec(new BigInteger(1, d), paramSpec);
    }


    /**
     * Create an {@link ECPublicKey} instance from the parameters.
     */
    static ECPublicKey createPublicKey(Object crv, byte[] x, Object y) throws COSEException
    {
        if (x == null)
        {
            throw new COSEException("The 'x' parameter is missing.");
        }

        if (y == null)
        {
            throw new COSEException("The 'y' parameter is missing.");
        }

        // Key spec for the public key.
        ECPublicKeySpec keySpec = createPublicKeySpec(crv, x, y);

        try
        {
            // Create an ECPublicKey instance.
            return (ECPublicKey)getKeyFactory().generatePublic(keySpec);
        }
        catch (InvalidKeySpecException cause)
        {
            throw new COSEException(String.format(
                    "The key spec is invalid: %s", cause.getMessage()), cause);
        }
    }


    /**
     * Create key spec for the public key.
     */
    private static ECPublicKeySpec createPublicKeySpec(
            Object crv, byte[] x, Object y) throws COSEException
    {
        // Get the parameter spec of the curve.
        ECParameterSpec paramSpec = getParameterSpec(crv);

        // X-coordinate
        BigInteger xcoord = new BigInteger(1, x);

        // Y-coordinate
        BigInteger ycoord = (y instanceof byte[]) ? new BigInteger(1, (byte[])y)
                          : uncompressY(paramSpec, xcoord, (Boolean)y);

        // Key spec for the public key.
        return new ECPublicKeySpec(new ECPoint(xcoord, ycoord), paramSpec);
    }


    /**
     * Get the parameter spec of the curve.
     */
    private static ECParameterSpec getParameterSpec(Object crv) throws COSEException
    {
        // Get the identifier assigned to the curve.
        int curve = getCurveIdentifier(crv);

        switch (curve)
        {
            case COSEEllipticCurves.P_256:
                return PARAMETER_SPEC_P256;

            case COSEEllipticCurves.P_384:
                return PARAMETER_SPEC_P384;

            case COSEEllipticCurves.P_521:
                return PARAMETER_SPEC_P521;

            default:
                throw new COSEException(String.format(
                        "The curve '%s' is not supported.", crv));
        }
    }


    /**
     * Get the integer identifier assigned to the curve.
     */
    private static int getCurveIdentifier(Object crv) throws COSEException
    {
        if (crv == null)
        {
            throw new COSEException("The 'crv' parameter is missing.");
        }

        if (crv instanceof Integer)
        {
            // The integer identifier of the curve.
            return (Integer)crv;
        }

        if (crv instanceof String)
        {
            // Get the integer identifier from the curve name.
            return COSEEllipticCurves.getValueByName((String)crv);
        }

        // Unknown
        return 0;
    }


    private static BigInteger uncompressY(
            ECParameterSpec paramSpec, BigInteger x, boolean bit) throws COSEException
    {
        // References
        // ----------
        //
        // SEC 1: Elliptic Curve Cryptography
        // 2.3.4 Octet-String-to-Elliptic-Curve-Point Conversion
        // https://www.secg.org/sec1-v2.pdf
        //
        // Stack Overflow
        // https://stackoverflow.com/a/46289709/1174054
        //

        // Assuming the curve is P-256, P-384 or P-521 whose equation is
        //
        //   y^2 = x^3 - 3x + b
        //

        // The prime
        BigInteger p = ((ECFieldFp)paramSpec.getCurve().getField()).getP();

        // The parameter b.
        BigInteger b = paramSpec.getCurve().getB();

        // x^3, mod p
        BigInteger x3 = x.pow(3).mod(p);

        // 3x, mod p
        BigInteger threeX = THREE.multiply(x).mod(p);

        // x^3 - 3x + b, mod p
        BigInteger y2 = x3.subtract(threeX).add(b).mod(p);

        BigInteger y;
        BigInteger remainder;

        try
        {
            // Compute the square root.
            BigInteger[] result = sqrtAndRemainder(y2);

            y = result[0].mod(p);
            remainder = result[1];
        }
        catch (ArithmeticException cause)
        {
            throw new COSEException(
                    "The compressed elliptic curve point is invalid.");
        }

        // If the exact square root is not available.
        if (!BigInteger.ZERO.equals(remainder))
        {
            throw new COSEException(
                    "The compressed elliptic curve point is invalid.");
        }

        // If (y === bit mod 2) is not true
        if (y.mod(TWO).equals(BigInteger.ONE) != bit)
        {
            y = p.subtract(y);
        }

        return y;
    }


    private static BigInteger[] sqrtAndRemainder(BigInteger bi) throws COSEException
    {
        if (sSqrtAndRemainder == null)
        {
            try
            {
                // The sqrtAndRemainder() method, which is available since Java 9.
                sSqrtAndRemainder = BigInteger.class.getMethod("sqrtAndRemainder");
            }
            catch (Exception cause)
            {
                throw new COSEException(String.format(
                        "Cannot compute the value of the compressed elliptic curve point: %s",
                        cause.getMessage()), cause);
            }
        }

        try
        {
            // return bi.sqrtAndRemainder();
            return (BigInteger[])sSqrtAndRemainder.invoke(bi);
        }
        catch (ArithmeticException cause)
        {
            // The compressed elliptic curve point is invalid.
            throw cause;
        }
        catch (Exception cause)
        {
            throw new COSEException(String.format(
                    "Cannot compute the value of the compressed elliptic curve point: %s",
                    cause.getMessage()), cause);
        }
    }


    /**
     * Get a key factory for EC.
     */
    private static KeyFactory getKeyFactory() throws COSEException
    {
        try
        {
            return KeyFactory.getInstance("EC");
        }
        catch (NoSuchAlgorithmException cause)
        {
            throw new COSEException(
                    "The key factory for EC is not available.", cause);
        }
    }


    /**
     * Generate a signature.
     *
     * @param key
     *         A private key to use to sign the data. The key must implement
     *         the {@link ECPrivateKey} interface.
     *
     * @param algorithm
     *         The identifier of a signature algorithm. Must be one of
     *         {@link COSEAlgorithms#ES256 ES256}, {@link COSEAlgorithms#ES384 ES384}
     *         or {@link COSEAlgorithms#ES512 ES512}.
     *
     * @param data
     *         Data for which a signature is generated.
     *
     * @return
     *         A generated signature.
     *
     * @throws COSEException
     */
    static byte[] sign(Key key, int algorithm, byte[] data) throws COSEException
    {
        // Make sure that the key implements the ECPrivateKey interface.
        ECPrivateKey priKey = castByPrivateKey(key, algorithm);

        // Get a Signature instance that performs signing.
        Signature sig = getSignatureInstance(algorithm);

        // Initialize the Signature instance for signing.
        initializeForSigning(sig, priKey);

        // Set the data for which the signature is being generated.
        supplyData(sig, data);

        // Generate a signature.
        return generateSignature(sig);
    }


    /**
     * Verify a signature.
     *
     * @param key
     *         A public key to use to verify the signature. The key must
     *         implement the {@link ECPublicKey} interface.
     *
     * @param algorithm
     *         The identifier of a signature algorithm. Must be one of
     *         {@link COSEAlgorithms#ES256 ES256}, {@link COSEAlgorithms#ES384 ES384}
     *         or {@link COSEAlgorithms#ES512 ES512}.
     *
     * @param data
     *         Data for which the signature was generated.
     *
     * @param signature
     *         A signature to verify.
     *
     * @return
     *         {@code true} if the signature is valid.
     *
     * @throws COSEException
     */
    static boolean verify(
            Key key, int algorithm, byte[] data, byte[] signature) throws COSEException
    {
        // Make sure that the key implements the ECPublicKey interface.
        ECPublicKey pubKey = castByPublicKey(key, algorithm);

        // Get a Signature instance that performs verification.
        Signature sig = getSignatureInstance(algorithm);

        // Initialize the Signature instance for verification.
        initializeForVerification(sig, pubKey);

        // Set the data for which the signature was generated.
        supplyData(sig, data);

        // Verify the signature.
        return verifySignature(sig, signature);
    }


    /**
     * Cast the key by ECPrivateKey.
     */
    private static ECPrivateKey castByPrivateKey(Key key, int algorithm) throws COSEException
    {
        // If the key does not implement the ECPrivateKey interface.
        if (!(key instanceof ECPrivateKey))
        {
            throw new COSEException(String.format(
                    "A key to sign data with the algorithm '%s' must implement the ECPrivateKey interface.",
                    COSEAlgorithms.getNameByValue(algorithm)));
        }

        return (ECPrivateKey)key;
    }


    /**
     * Cast the key by ECPublicKey.
     */
    private static ECPublicKey castByPublicKey(Key key, int algorithm) throws COSEException
    {
        // If the key does not implement the ECPublicKey interface.
        if (!(key instanceof ECPublicKey))
        {
            throw new COSEException(String.format(
                    "A key to verify a signature signed with the algorithm '%s' must implement the ECPublicKey interface.",
                    COSEAlgorithms.getNameByValue(algorithm)));
        }

        return (ECPublicKey)key;
    }


    /**
     * Call Signature.getInstance(String).
     */
    private static Signature getSignatureInstance(int algorithm) throws COSEException
    {
        // Determine the algorithm name given to Signature.getInstance(String).
        String algorithmName = determineAlgorithmName(algorithm);

        // Ensure that the BouncyCastleProvider has been loaded if necessary.
        ensureProvider();

        try
        {
            // Get a Signature instance for the algorithm.
            return Signature.getInstance(algorithmName);
        }
        catch (NoSuchAlgorithmException cause)
        {
            throw new COSEException(String.format(
                    "Failed to get a Signature instance for the algorithm '%s'.",
                    algorithmName));
        }
    }


    private static String determineAlgorithmName(int algorithm) throws COSEException
    {
        switch (algorithm)
        {
            case COSEAlgorithms.ES256:
                return beforeJre9 ? "SHA256withPLAIN-ECDSA" : "SHA256withECDSAinP1363Format";

            case COSEAlgorithms.ES384:
                return beforeJre9 ? "SHA384withPLAIN-ECDSA" : "SHA384withECDSAinP1363Format";

            case COSEAlgorithms.ES512:
                return beforeJre9 ? "SHA512withPLAIN-ECDSA" : "SHA512withECDSAinP1363Format";

            default:
                // This should not happen.
                throw new COSEException(String.format(
                        "The ECDSA algorithm '%d' is not supported.", algorithm));
        }
    }


    private static void ensureProvider()
    {
        if (beforeJre9 == false)
        {
            // No need to load the BouncyCastleProvider.
            return;
        }

        BouncyCastleLoader.ensureBouncyCastleProviderIsLoaded();
    }


    /**
     * Call Signature.initSign(PrivateKey).
     */
    private static void initializeForSigning(Signature sig, PrivateKey priKey) throws COSEException
    {
        try
        {
            // Initialize the Signature instance for signing.
            sig.initSign(priKey);
        }
        catch (InvalidKeyException cause)
        {
            throw new COSEException(String.format(
                    "Failed to initialize the Signature instance for signing: %s",
                    cause.getMessage()), cause);
        }
    }


    /**
     * Call Signature.initVerify(PublicKey).
     */
    private static void initializeForVerification(Signature sig, PublicKey pubKey) throws COSEException
    {
        try
        {
            // Initialize the Signature instance for verification.
            sig.initVerify(pubKey);
        }
        catch (InvalidKeyException cause)
        {
            throw new COSEException(String.format(
                    "Failed to initialize the Signature instance for verification: %s",
                    cause.getMessage()), cause);
        }
    }


    /**
     * Call Signature.update(byte[]).
     */
    private static void supplyData(Signature sig, byte[] data) throws COSEException
    {
        try
        {
            // Supply the data for which the signature is generated.
            sig.update(data);
        }
        catch (SignatureException cause)
        {
            throw new COSEException(String.format(
                    "Failed to supply the Signature instance with the data: %s",
                    cause.getMessage()), cause);
        }
    }


    /**
     * Call Signature.sign().
     */
    private static byte[] generateSignature(Signature sig) throws COSEException
    {
        try
        {
            // Generate a signature.
            return sig.sign();
        }
        catch (SignatureException cause)
        {
            throw new COSEException(String.format(
                    "Failed to generate a signature: %s",
                    cause.getMessage()), cause);
        }
    }


    /**
     * Call Signature.verify(byte[]).
     */
    private static boolean verifySignature(Signature sig, byte[] signature) throws COSEException
    {
        try
        {
            // Verify the signature.
            return sig.verify(signature);
        }
        catch (SignatureException cause)
        {
            throw new COSEException(String.format(
                    "Failed to verify the signature: %s",
                    cause.getMessage()), cause);
        }
    }
}

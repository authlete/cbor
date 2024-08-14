package com.authlete.cose;

import java.security.*;

/**
 * EdDSA operations
 */
public class EdDSA {

    private static final Integer FIFTEEN = 15;
    private static final boolean beforeJre15 = isBeforeJre15();
    private static final String ED_25519 = "Ed25519";

    private static boolean isBeforeJre15()
    {
        String javaVersion = System.getProperty("java.version");
        // 1.8, 1.7 etc.
        if (javaVersion.startsWith("1."))
        {
            return true;
        }

        // 9, 10, 11 etc.
        int dotIndex = javaVersion.indexOf('.');
        // there is dot, get the major version
        if (dotIndex != -1)
        {
            return Integer.parseInt(javaVersion.substring(0, dotIndex)) < FIFTEEN;
        }
        // no dot, just check the whole string
        return Integer.parseInt(javaVersion) < FIFTEEN;
    }


    /**
     * Generate a new Ed25519 key pair
     *
     * @return A new key pair
     * @throws NoSuchAlgorithmException If the algorithm is not available
     */
    static KeyPair generateKeyPair() throws NoSuchAlgorithmException
    {
        ensureProvider();

        KeyPairGenerator kpg = KeyPairGenerator.getInstance(ED_25519);
        return kpg.generateKeyPair();
    }

    /**
     * Generate a signature using the EdDSA algorithm
     *
     * @param key
     *         The private key to sign the data The key must implement
     *         the {@link PrivateKey} interface.
     * @param data
     *        The data to sign
     *
     * @return
     *        The signature
     *
     * @throws COSEException
     */
    static byte[] sign(Key key, byte[] data) throws COSEException
    {

        if (!(key instanceof PrivateKey))
        {
            throw new COSEException("Key is not a private key");
        }

        PrivateKey privateKey = (PrivateKey) key;
        ensureProvider();
        Signature signature = getSignatureInstance();
        initializeSignatureForSigning(signature, privateKey);
        offerDataToSignature(signature, data);
        return sign(signature);
    }

    /**
     * Verify a signature using the EdDSA algorithm
     *
     * @param key
     *         The public key to verify the signature. The key must implement
     *         the {@link PublicKey} interface.
     * @param data
     *        The data to verify
     * @param signature
     *        The signature to verify
     *
     * @return
     *        True if the signature is valid, false otherwise
     *
     * @throws COSEException
     */
    static boolean verify(Key key, byte[] data, byte[] signature) throws COSEException
    {
        if (!(key instanceof PublicKey))
        {
            throw new COSEException("Key is not a public key");
        }

        PublicKey publicKey = (PublicKey) key;
        ensureProvider();
        Signature sig = getSignatureInstance();
        initializeSignatureForVerification(sig, publicKey);
        offerDataToSignature(sig, data);
        return verify(sig, signature);
    }


    private static Signature getSignatureInstance() throws COSEException
    {
        try {
            return Signature.getInstance(ED_25519);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new COSEException(
                    "Failed to get a Signature instance for the algorithm 'Ed25519'");
        }
    }

    private static void initializeSignatureForSigning(Signature signature, PrivateKey privateKey) throws COSEException
    {
        try
        {
            signature.initSign(privateKey);
        }
        catch (InvalidKeyException e)
        {
            throw new COSEException(
                    String.format("Failed to initialize the Signature instance for signing: %s", e.getMessage()), e);
        }
    }

    private static void offerDataToSignature(Signature signature, byte[] data) throws COSEException
    {
        try
        {
            signature.update(data);
        }
        catch (SignatureException e)
        {
            throw new COSEException(
                    String.format("Failed to offer data to the Signature instance: %s", e.getMessage()), e);
        }
    }

    private static byte[] sign(Signature signature) throws COSEException
    {
        try
        {
            return signature.sign();
        }
        catch (SignatureException e)
        {
            throw new COSEException(
                    String.format("Failed to generate a signature: %s", e.getMessage()), e);
        }
    }

    private static boolean verify(Signature sig, byte[] signature) throws COSEException
    {
        try
        {
            return sig.verify(signature);
        }
        catch (SignatureException e)
        {
            throw new COSEException(
                    String.format("Failed to verify the signature: %s", e.getMessage()), e);
        }
    }

    private static void initializeSignatureForVerification(Signature sig, PublicKey publicKey) throws COSEException
    {
        try
        {
            sig.initVerify(publicKey);
        }
        catch (InvalidKeyException e)
        {
            throw new COSEException(
                    String.format("Failed to initialize the Signature instance for verification: %s",
                            e.getMessage()), e);
        }
    }

    private static void ensureProvider()
    {
        if (!beforeJre15)
        {
            return;
        }
        BouncyCastleLoader.ensureBouncyCastleProviderIsLoaded();
    }
}
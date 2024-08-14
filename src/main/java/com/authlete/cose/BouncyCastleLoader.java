package com.authlete.cose;


import java.security.Provider;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


/**
 * A utility class to load the BouncyCastleProvider.
 *
 * @since 1.19
 *
 * @see <a href="https://github.com/authlete/cbor/pull/11"
 *      >PR 11: [feature] Added support for EdDSA signing/verify</a>
 */
class BouncyCastleLoader {

    private static boolean isBouncyCastleProviderLoaded;

    public static void ensureBouncyCastleProviderIsLoaded()
    {
        if (isBouncyCastleProviderLoaded)
        {
            // The BouncyCastleProvider has already been loaded.
            return;
        }
        for (Provider provider : Security.getProviders())
        {
            // If the BouncyCastleProvider has already been loaded somewhere else.
            if (provider instanceof BouncyCastleProvider)
            {
                isBouncyCastleProviderLoaded = true;
                return;
            }
        }

        // Load the BouncyCastleProvider.
        Security.addProvider(new BouncyCastleProvider());

        isBouncyCastleProviderLoaded = true;
    }
}

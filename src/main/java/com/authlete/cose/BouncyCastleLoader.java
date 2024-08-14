package com.authlete.cose;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Provider;
import java.security.Security;


/**
 * A utility class to load the BouncyCastleProvider.
 */
public class BouncyCastleLoader {

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

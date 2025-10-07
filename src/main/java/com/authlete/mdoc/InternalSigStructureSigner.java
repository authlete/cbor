package com.authlete.mdoc;

import com.authlete.cose.COSEEC2Key;
import com.authlete.cose.COSEException;
import com.authlete.cose.COSESigner;
import com.authlete.cose.SigStructure;
import com.authlete.mdoc.interfaces.SigStructureSigner;

import java.security.interfaces.ECPrivateKey;

public class InternalSigStructureSigner implements SigStructureSigner {
    private final COSEEC2Key issuerKey;

    public InternalSigStructureSigner(COSEEC2Key issuerKey) {
        this.issuerKey = issuerKey;
    }

    public byte[] sign(SigStructure sigStructure, int alg) throws COSEException {
        // The private key for signing.
        ECPrivateKey privateKey = issuerKey.toECPrivateKey();

        // Create a signer with the private key.
        COSESigner signer = new COSESigner(privateKey);

        // Sign the Sig_structure (= generate a signature).
        return signer.sign(sigStructure, alg);
    }
}

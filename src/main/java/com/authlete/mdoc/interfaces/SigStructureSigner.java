package com.authlete.mdoc.interfaces;

import com.authlete.cose.COSEEC2Key;
import com.authlete.cose.COSEException;
import com.authlete.cose.SigStructure;

public interface SigStructureSigner {
    byte[] sign(SigStructure sigStructure, int alg) throws COSEException;
}

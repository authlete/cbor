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
package com.authlete.mdoc;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.authlete.cbor.CBORDiagnosticNotationParser;
import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORString;
import com.authlete.cbor.CBORizer;
import com.authlete.cose.COSEEC2Key;
import com.authlete.cose.COSEException;
import com.authlete.cose.COSEKey;
import com.authlete.cose.COSEProtectedHeader;
import com.authlete.cose.COSEProtectedHeaderBuilder;
import com.authlete.cose.COSESign1;
import com.authlete.cose.COSESign1Builder;
import com.authlete.cose.COSESigner;
import com.authlete.cose.COSEUnprotectedHeader;
import com.authlete.cose.COSEUnprotectedHeaderBuilder;
import com.authlete.cose.SigStructure;
import com.authlete.cose.SigStructureBuilder;
import com.authlete.cose.constants.COSEAlgorithms;
import com.authlete.cose.constants.COSEEllipticCurves;


/**
 * Utility to build an {@link IssuerSigned} instance.
 *
 * <p>
 * An {@code IssuerSigned} instanced is used to create a {@link Document}
 * instance.
 * </p>
 *
 * @since 1.5
 *
 * @see <a href="https://www.iso.org/standard/69084.html">ISO/IEC 18013-5:2021</a>
 */
public class IssuerSignedBuilder
{
    private static final SecureRandom RANDOM = new SecureRandom();


    private String mDocType;
    private Map<String, Object> mClaims;
    private ValidityInfo mValidityInfo;
    private COSEKey mDeviceKey;
    private COSEEC2Key mIssuerKey;
    private List<X509Certificate> mIssuerCertChain;
    private CBORizer mCBORizer;


    /**
     * Get the DocType.
     *
     * <p>
     * The value is used as the value of the {@code "docType"} parameter of
     * the {@link MobileSecurityObject}.
     * </p>
     *
     * @return
     *         The DocType.
     */
    public String getDocType()
    {
        return mDocType;
    }


    /**
     * Set the DocType.
     *
     * <p>
     * The value is used as the value of the {@code "docType"} parameter of
     * the {@link MobileSecurityObject} structure.
     * </p>
     *
     * @param docType
     *         The DocType.
     *
     * @return
     *         return {@code this} object.
     */
    public IssuerSignedBuilder setDocType(String docType)
    {
        mDocType = docType;

        return this;
    }


    /**
     * Get the claims used to create {@link IssuerSignedItem}s.
     *
     * <p>
     * See the description of the {@link #setClaims(Map)} method for details
     * about the format.
     * </p>
     *
     * @return
     *         The claims used to create {@link IssuerSignedItem}s.
     */
    public Map<String, Object> getClaims()
    {
        return mClaims;
    }


    /**
     * Set the claims used to create {@link IssuerSignedItem}s.
     *
     * <p>
     * The keys of the top-level properties in the {@code claims} map must be
     * strings representing name spaces, and their values must be JSON objects,
     * each of which contains claims under the corresponding name space.
     * </p>
     *
     * <p>
     * The following JSON shows the structure that the {@code claims} map
     * should have.
     * </p>
     *
     * <pre>
     * {
     *     "com.example.namespace1" : {
     *         "claimName1": "claimValue1",
     *         "claimName2": true,
     *         "claimName3": 1
     *     },
     *     "com.example.namespace2" : {
     *         "claimName4": [ "element1", "element2" ],
     *         "claimName5": {
     *           "subClaimName1": "subClaimValue1"
     *         }
     *     }
     * }
     * </pre>
     *
     * <p>
     * Types of claim values can be strings, boolean values, integers,
     * floating-point numbers, arrays ({@code List}) and maps ({@code Map}),
     * which are natural representations of JSON values.
     * </p>
     *
     * <p>
     * However, there may be cases where CBOR-specific data need to be embedded.
     * For example, the {@code "birth_date"} claim may require the "full-date"
     * tag (defined in <a href="https://www.rfc-editor.org/rfc/rfc8943.html"
     * >RFC 8943</a>) and the {@code "portrait"} claim may require binary data.
     * </p>
     *
     * <p>
     * To embed CBOR-specific data, a {@link CBORizer} instance with an
     * implementation of the {@link CBORDiagnosticNotationParser} interface
     * needs to be set by calling the {@link #setCBORizer(CBORizer)} method.
     * Such {@code CBORizer} will interpret strings written in the CBOR
     * Diagnostic Notation (<a href=
     * "https://www.rfc-editor.org/rfc/rfc8949#section-8">RFC 8949 Section 8</a>,
     * <a href="https://www.rfc-editor.org/rfc/rfc8610#appendix-G">RFC 8610
     * Appendix G</a>) with a special prefix (e.g. {@code "cbor:"}), and
     * convert them into CBOR-specific data. As a result, input data like
     * below will be accepted and CBOR-specific data will be embedded
     * accordingly.
     * </p>
     *
     * <pre>
     * {
     *     "com.example.namespace3": {
     *         "birth_date": "cbor:1004(\"1974-05-06\")",
     *         "portrait": "cbor:h'0102.....'"
     *     }
     * }
     * </pre>
     *
     * @param claims
     *         The claims used to create {@link IssuerSignedItem}s.
     *
     * @return
     *         {@code this} object.
     */
    public IssuerSignedBuilder setClaims(Map<String, Object> claims)
    {
        mClaims = claims;

        return this;
    }


    /**
     * Get the validity information of the Mobile Security Object.
     *
     * <p>
     * The value is used as the value of the {@code "validityInfo"} parameter of
     * the {@link MobileSecurityObject} structure.
     * </p>
     *
     * @return
     *         The validity information of the Mobile Security Object.
     */
    public ValidityInfo getValidityInfo()
    {
        return mValidityInfo;
    }


    /**
     * Set the validity information of the Mobile Security Object.
     *
     * <p>
     * The value is used as the value of the {@code "validityInfo"} parameter of
     * the {@link MobileSecurityObject} structure.
     * </p>
     *
     * @param info
     *         The validity information of the Mobile Security Object.
     *
     * @return
     *         {@code this} object.
     */
    public IssuerSignedBuilder setValidityInfo(ValidityInfo info)
    {
        mValidityInfo = info;

        return this;
    }


    /**
     * Get the device key.
     *
     * <p>
     * The value is used as the value of the {@code "deviceKey"} parameter of
     * the {@link DeviceKeyInfo} structure.
     * </p>
     *
     * @return
     *         The device key.
     */
    public COSEKey getDeviceKey()
    {
        return mDeviceKey;
    }


    /**
     * Set the device key.
     *
     * <p>
     * The value is used as the value of the {@code "deviceKey"} parameter of
     * the {@link DeviceKeyInfo} structure.
     * </p>
     *
     * @param deviceKey
     *         The device key.
     *
     * @return
     *         {@code this} object.
     */
    public IssuerSignedBuilder setDeviceKey(COSEKey deviceKey)
    {
        mDeviceKey = deviceKey;

        return this;
    }


    /**
     * Get the issuer key used to sign the {@code IssuerAuth} structure
     * ({@link COSESign1}).
     *
     * @return
     *         The issuer key.
     */
    public COSEEC2Key getIssuerKey()
    {
        return mIssuerKey;
    }


    /**
     * Set the issuer key used to sign the {@code IssuerAuth} structure
     * ({@link COSESign1}).
     *
     * @param issuerKey
     *         The issuer key.
     *
     * @return
     *         {@code this} object.
     */
    public IssuerSignedBuilder setIssuerKey(COSEEC2Key issuerKey)
    {
        mIssuerKey = issuerKey;

        return this;
    }


    /**
     * Get the certificate chain for the issuer key.
     *
     * <p>
     * The value is embedded in the unprotected header of the {@code IssuerAuth}
     * structure ({@link COSESign1}) as the value of the {@code x5chain} header
     * parameter.
     * </p>
     *
     * @return
     *         The certificate chain for the issuer key.
     *
     * @see <a href="https://www.rfc-editor.org/rfc/rfc9360.html"
     *      >RFC 9360 CBOR Object Signing and Encryption (COSE):
     *       Header Parameters for Carrying and Referencing X.509 Certificates</a>
     */
    public List<X509Certificate> getIssuerCertChain()
    {
        return mIssuerCertChain;
    }


    /**
     * Set the certificate chain for the issuer key.
     *
     * <p>
     * The value is embedded in the unprotected header of the {@code IssuerAuth}
     * structure ({@link COSESign1}) as the value of the {@code x5chain} header
     * parameter.
     * </p>
     *
     * @param chain
     *         The certificate chain for the issuer key.
     *
     * @return
     *         {@code this} object.
     *
     * @see <a href="https://www.rfc-editor.org/rfc/rfc9360.html"
     *      >RFC 9360 CBOR Object Signing and Encryption (COSE):
     *       Header Parameters for Carrying and Referencing X.509 Certificates</a>
     */
    public IssuerSignedBuilder setIssuerCertChain(List<X509Certificate> chain)
    {
        mIssuerCertChain = chain;

        return this;
    }


    /**
     * Get the {@link CBORizer} to convert given claim values into CBOR items.
     *
     * <p>
     * If there are claim values expressed in the CBOR Diagnostic Notation like
     * {@code "cbor:h'0102'"} and {@code "cbor:0(\"2013-03-21T20:04:00Z\")"}, a
     * {@code CBORizer} instance with {@link CBORDiagnosticNotationParser} needs
     * to be explicitly set. Otherwise, strings expressed in the CBOR Diagnostic
     * Notation are embedded as CBOR text strings without any conversion.
     * </p>
     *
     * @return
     *         The {@code CBORizer}.
     */
    public CBORizer getCBORizer()
    {
        return mCBORizer;
    }


    /**
     * Set the {@link CBORizer} to convert given claim values into CBOR items.
     *
     * <p>
     * If there are claim values expressed in the CBOR Diagnostic Notation like
     * {@code "cbor:h'0102'"} and {@code "cbor:0(\"2013-03-21T20:04:00Z\")"}, a
     * {@code CBORizer} instance with {@link CBORDiagnosticNotationParser} needs
     * to be explicitly set. Otherwise, strings expressed in the CBOR Diagnostic
     * Notation are embedded as CBOR text strings without any conversion.
     * </p>
     *
     * @param cborizer
     *         The {@code CBORizer}.
     *
     * @return
     *         {@code this} object.
     */
    public IssuerSignedBuilder setCBORizer(CBORizer cborizer)
    {
        mCBORizer = cborizer;

        return this;
    }


    /**
     * Build an {@link IssuerSigned} instance.
     *
     * @return
     *         A new {@link IssuerSigned} instance.
     *
     * @throws COSEException
     *         Signing with the issuer key failed.
     *
     * @throws CertificateEncodingException
     *         The DER representation of an X.509 certificate in the issuer's
     *         certificate chain failed to be obtained.
     */
    public IssuerSigned build() throws COSEException, CertificateEncodingException
    {
        // Check some input parameters.
        checkInput();

        // nameSpaces
        IssuerNameSpaces issuerNameSpaces = buildIssuerNameSpaces();

        // issuerAuth
        COSESign1 issuerAuth = buildIssuerAuth(issuerNameSpaces);

        // IssuerSigned = {
        //   ? "nameSpaces" : IssuerNameSpaces
        //   "issuerAuth" : IssuerAuth
        // }
        return new IssuerSigned(issuerNameSpaces, issuerAuth);
    }


    private void checkInput()
    {
        if (getDocType() == null)
        {
            throw new IllegalStateException("Doc type is not set.");
        }

        if (getIssuerKey() == null)
        {
            throw new IllegalStateException("Issuer key is not set.");
        }

        if (getIssuerCertChain() == null)
        {
            throw new IllegalStateException("Issuer certificate chain is not set.");
        }

        if (getIssuerCertChain().size() == 0)
        {
            throw new IllegalStateException("Issuer certificate chain is empty.");
        }
    }


    private IssuerNameSpaces buildIssuerNameSpaces()
    {
        // The input claims. The expected structure is as follows.
        //
        //   {
        //       "namespace1": {
        //           "claimName1": "claimValue1",
        //           ...
        //       },
        //       "namespace2": {
        //           "claimName2": "claimValue2",
        //           ...
        //       }
        //   }
        //
        Map<String, Object> claims = prepareClaims();

        // The cborizer that converts claim values into CBOR items.
        CBORizer cborizer = prepareCBORizer();

        // Digest ID generator for IssuerSignedItem's. In this implementation,
        // digest IDs are unique across all name spaces.
        SequentialIntegerGenerator digestIdGenerator =
                new SequentialIntegerGenerator();

        // Entries in the IssuerNameSpaces structure.
        List<IssuerNameSpacesEntry> issuerNameSpacesEntries = new ArrayList<>();

        // For each pair of a name space and sub claims under the name space.
        for (Map.Entry<String, Object> entry : claims.entrySet())
        {
            // The name space.
            String nameSpace = entry.getKey();

            // The sub claims under the name space.
            Object subClaims = entry.getValue();

            // Create a list of IssuerSignedItemBytes from the sub claims.
            List<IssuerSignedItemBytes> itemBytesList =
                    buildItemBytesList(nameSpace, subClaims,
                            digestIdGenerator, cborizer);

            // NameSpace => [ + IssuerSignedItemBytes ]
            IssuerNameSpacesEntry issuerNameSpacesEntry =
                    new IssuerNameSpacesEntry(nameSpace, itemBytesList);

            // Add to the list.
            issuerNameSpacesEntries.add(issuerNameSpacesEntry);
        }

        // IssuerNameSpaces = {
        //   + NameSpace => [ + IssuerSignedItemBytes ]
        // }
        return new IssuerNameSpaces(issuerNameSpacesEntries);
    }


    @SuppressWarnings("unchecked")
    private List<IssuerSignedItemBytes> buildItemBytesList(
            String nameSpace, Object subClaims,
            SequentialIntegerGenerator digestIdGenerator, CBORizer cborizer)
    {
        // If the value corresponding to the name space is not a JSON object.
        if (!(subClaims instanceof Map))
        {
            throw new IllegalArgumentException(String.format(
                    "The value for the name space '%s' is not a JSON object.", nameSpace));
        }

        List<IssuerSignedItemBytes> itemBytesList = new ArrayList<>();

        // For each sub claim under the name space.
        for (Map.Entry<String, Object> subClaim : ((Map<String, Object>)subClaims).entrySet())
        {
            // Create an IssuerSignedItem from the sub claim.
            IssuerSignedItem item =
                    buildIssuerSignedItem(subClaim, digestIdGenerator, cborizer);

            // IssuerSignedItemBytes = #6.24(bstr .cbor IssuerSignedItem)
            IssuerSignedItemBytes itemBytes = new IssuerSignedItemBytes(item);

            // Add to the list.
            itemBytesList.add(itemBytes);
        }

        return itemBytesList;
    }


    private IssuerSignedItem buildIssuerSignedItem(
            Map.Entry<String, Object> subClaim,
            SequentialIntegerGenerator digestIdGenerator, CBORizer cborizer)
    {
        // Digest ID
        int digestID = digestIdGenerator.next();

        // Random bytes
        byte[] random = generateRandom();

        // Element identifier
        String elementIdentifier = subClaim.getKey();

        // Element value
        Object elementValue = cborizer.cborize(subClaim.getValue());

        // IssuerSignedItem = {
        //   "digestID" : uint,
        //   "random" : bstr,
        //   "elementIdentifier" : DataElementIdentifier,
        //   "elementValue" : DataElementValue
        // }
        return new IssuerSignedItem(digestID, random, elementIdentifier, elementValue);
    }


    private COSESign1 buildIssuerAuth(IssuerNameSpaces issuerNameSpaces)
            throws COSEException, CertificateEncodingException
    {
        // The algorithm for signing.
        int alg = determineIssuerAuthSigningAlgorithm();

        // Protected header
        COSEProtectedHeader protectedHeader = prepareIssuerAuthProtectedHeader(alg);

        // Unprotected header
        COSEUnprotectedHeader unprotectedHeader = prepareIssuerAuthUnprotectedHeader();

        // Payload
        CBORItem payload = prepareIssuerAuthPayload(issuerNameSpaces);

        // Sig_structure, which is the target to sign.
        SigStructure sigStructure = prepareSigStructure(protectedHeader, payload);

        // Sign the Sig_structure.
        byte[] signature = sign(sigStructure, alg);

        // IssuerAuth = COSE_Sign1  ; The payload is MobileSecurityObjectBytes
        return new COSESign1Builder()
                .protectedHeader(protectedHeader)
                .unprotectedHeader(unprotectedHeader)
                .payload(payload)
                .signature(signature)
                .build();
    }


    private int determineIssuerAuthSigningAlgorithm()
    {
        // The value of the 'alg' parameter of the issuer key.
        Object alg = getIssuerKey().getAlg();

        // If the issuer key contains the 'alg' parameter.
        if (alg != null)
        {
            return (alg instanceof String)
                    ? COSEAlgorithms.getValueByName((String)alg)
                    : ((Number)alg).intValue();
        }

        // The value of the 'crv' parameter of the issuer key.
        Object crv = getIssuerKey().getCrv();

        // If the issuer key does not contain the 'crv' parameter.
        if (crv == null)
        {
            throw new IllegalArgumentException(
                    "The issuer key does not contain the 'crv' parameter.");
        }

        // ISO/IEC 18013-5:2021, 9.1.2.4 Signing method and structure for MSO
        //
        //   The issuing authority infrastructure shall use one of the following
        //   signature algorithms for calculating the signature over the MSO:
        //   “ES256” (ECDSA with SHA-256), “ES384” (ECDSA with SHA-384), “ES512”
        //   (ECDSA with SHA-512) or “EdDSA” (EdDSA). ”ES256” shall be used with
        //   curves P-256 and brainpoolP256r1. “ES384” shall be used with curves
        //   P-384, brainpoolP320r1 and brainpoolP384r1. “ES512” shall be used
        //   with curves P-521 and brainpoolP512r1. “EdDSA” shall be used with
        //   curves Ed25519 and Ed448. For verifying the signature, the mdoc
        //   reader shall support all of these signature algorithms and curves.
        //

        // If the curve is P-256.
        if (crv.equals(COSEEllipticCurves.P_256) || crv.equals(COSEEllipticCurves.P_256_NAME))
        {
            // ES256
            return COSEAlgorithms.ES256;
        }
        // If the curve is P-384.
        else if (crv.equals(COSEEllipticCurves.P_384) || crv.equals(COSEEllipticCurves.P_384_NAME))
        {
            // ES384
            return COSEAlgorithms.ES384;
        }
        // If the curve is P-521.
        else if (crv.equals(COSEEllipticCurves.P_521) || crv.equals(COSEEllipticCurves.P_521_NAME))
        {
            // ES512
            return COSEAlgorithms.ES512;
        }
        // If the curve is Ed25519.
        else if (crv.equals(COSEEllipticCurves.Ed25519) || crv.equals(COSEEllipticCurves.Ed25519_NAME))
        {
            // EdDSA
            return COSEAlgorithms.EdDSA;
        }
        // If the curve is Ed448.
        else if (crv.equals(COSEEllipticCurves.Ed448) || crv.equals(COSEEllipticCurves.Ed448_NAME))
        {
            // EdDSA
            return COSEAlgorithms.EdDSA;
        }

        throw new IllegalArgumentException("The curve of the issuer key is not supported.");
    }


    private COSEProtectedHeader prepareIssuerAuthProtectedHeader(int alg)
    {
        // ISO/IEC 18013-5:2021, 9.1.2.4 Signing method and structure for MSO
        //
        //   The alg element (RFC 8152) shall be included as an element in the
        //   protected header. Other elements should not be present in the
        //   protected header.
        //

        // Build a protected header with the 'alg' parameter.
        return new COSEProtectedHeaderBuilder().alg(alg).build();
    }


    private COSEUnprotectedHeader prepareIssuerAuthUnprotectedHeader() throws CertificateEncodingException
    {
        // ISO/IEC 18013-5:2021, 9.1.2.4 Signing method and structure for MSO
        //
        //   The certificate containing the public key belonging to the private
        //   key used to sign the MSO shall be included as an x5chain element
        //   as described in RFC: CBOR Object Signing and Encryption (COSE):
        //   Headers for carrying and referencing X.509 certificates. It shall
        //   be included as an unprotected header element. The x5chain element
        //   shall include at least one certificate and may contain more.
        //

        // Build an unprotected header with the 'x5chain' parameter.
        return new COSEUnprotectedHeaderBuilder()
                .x5chain(getIssuerCertChain()).build();
    }


    private CBORItem prepareIssuerAuthPayload(IssuerNameSpaces issuerNameSpaces)
    {
        // MobileSecurityObjectBytes, which is used as the payload of IssuerAuth.
        CBORItem payload = buildMobileSecurityObjectBytes(issuerNameSpaces);

        // Add a comment just for CBORItem.prettify().
        payload.setComment("payload");

        return payload;
    }


    private SigStructure prepareSigStructure(
            COSEProtectedHeader protectedHeader, CBORItem payload)
    {
        return new SigStructureBuilder()
                .signature1()
                .bodyAttributes(protectedHeader)
                .payload(payload)
                .build();
    }


    private byte[] sign(SigStructure sigStructure, int alg) throws COSEException
    {
        // The private key for signing.
        ECPrivateKey privateKey = getIssuerKey().toECPrivateKey();

        // Create a signer with the private key.
        COSESigner signer = new COSESigner(privateKey);

        // Sign the Sig_structure (= generate a signature).
        return signer.sign(sigStructure, alg);
    }


    private MobileSecurityObjectBytes buildMobileSecurityObjectBytes(IssuerNameSpaces issuerNameSpaces)
    {
        // Build a Mobile Security Object (MSO).
        MobileSecurityObject mso = buildMobileSecurityObject(issuerNameSpaces);

        // MobileSecurityObjectBytes = #6.24(bstr .cbor MobileSecurityObject)
        return new MobileSecurityObjectBytes(mso);
    }


    private MobileSecurityObject buildMobileSecurityObject(IssuerNameSpaces issuerNameSpaces)
    {
        // Value digests
        ValueDigests valueDigests = buildValueDigests(issuerNameSpaces);

        // Device key info
        DeviceKeyInfo deviceKeyInfo = buildDeviceKeyInfo(issuerNameSpaces);

        // MobileSecurityObject = {
        //   "version" : tstr,
        //   "digestAlgorithm" : tstr,
        //   "valueDigests" : ValueDigests,
        //   "deviceKeyInfo" : DeviceKeyInfo,
        //   "docType" : tstr,
        //   "validityInfo" : ValidityInfo
        // }
        return new MobileSecurityObject(
                valueDigests, deviceKeyInfo, getDocType(), getValidityInfo());
    }


    @SuppressWarnings("unchecked")
    private ValueDigests buildValueDigests(IssuerNameSpaces issuerNameSpaces)
    {
        List<IssuerNameSpacesEntry> issuerNameSpacesEntries =
                (List<IssuerNameSpacesEntry>)issuerNameSpaces.getPairs();

        List<ValueDigestsEntry> valueDigestsEntries = new ArrayList<>();

        // For each "NameSpace => [ + IssuerSignedItemBytes ]"
        for (IssuerNameSpacesEntry issuerNameSpacesEntry : issuerNameSpacesEntries)
        {
            // The name space.
            CBORString nameSpace = issuerNameSpacesEntry.getNameSpace();

            // The list of IssuerSignedItemBytes.
            List<? extends IssuerSignedItemBytes> issuerSignedItemBytesList =
                    issuerNameSpacesEntry.getIssuerSignedItemBytesList();

            // Build a DigestIDs from the list of IssuerSignedItemBytes.
            DigestIDs digestIDs = buildDigestIDs(issuerSignedItemBytesList);

            // NameSpace => DigestIDs
            ValueDigestsEntry valueDigestsEntry =
                    new ValueDigestsEntry(nameSpace, digestIDs);

            // Add to the list.
            valueDigestsEntries.add(valueDigestsEntry);
        }

        // ValueDigests = {
        //   + NameSpace => DigestIDs
        // }
        return new ValueDigests(valueDigestsEntries);
    }


    private DigestIDs buildDigestIDs(
            List<? extends IssuerSignedItemBytes> issuerSignedItemBytesList)
    {
        List<DigestIDsEntry> digestIDsEntries = new ArrayList<>();

        for (IssuerSignedItemBytes issuerSignedItemBytes : issuerSignedItemBytesList)
        {
            // IssuerSignedItem
            IssuerSignedItem issuerSignedItem =
                    issuerSignedItemBytes.getIssuerSignedItem();

            // Digest ID
            int digestID = issuerSignedItem.getDigestID();

            // Compute the digest.
            byte[] digest = computeDigest(issuerSignedItemBytes.encode());

            // DigestID => Digest
            DigestIDsEntry digestIDsEntry = new DigestIDsEntry(digestID, digest);

            // Add to the list.
            digestIDsEntries.add(digestIDsEntry);
        }

        // DigestIDs = {
        //   + DigestID => Digest
        // }
        return new DigestIDs(digestIDsEntries);
    }


    private DeviceKeyInfo buildDeviceKeyInfo(IssuerNameSpaces issuerNameSpaces)
    {
        // Device key.
        COSEKey deviceKey = getDeviceKey();

        if (deviceKey == null)
        {
            return null;
        }

        // Key authorizations
        KeyAuthorizations keyAuthorizations = buildKeyAuthorizations(issuerNameSpaces);

        // DeviceKeyInfo = {
        //   "deviceKey" : DeviceKey,
        //   ? "keyAuthorizations" : KeyAuthorizations,
        //   ? "keyInfo" : KeyInfo
        // }
        return new DeviceKeyInfo(deviceKey, keyAuthorizations, null);
    }


    private KeyAuthorizations buildKeyAuthorizations(IssuerNameSpaces issuerNameSpaces)
    {
        // Authorized name spaces
        AuthorizedNameSpaces authorizedNameSpaces =
                buildAuthorizedNameSpaces(issuerNameSpaces);

        // KeyAuthorizations = {
        //   ? "nameSpaces" : AuthorizedNameSpaces,
        //   ? "dataElements" : AuthorizedDataElements
        // }
        return new KeyAuthorizations(authorizedNameSpaces, null);
    }

    @SuppressWarnings("unchecked")
    private AuthorizedNameSpaces buildAuthorizedNameSpaces(IssuerNameSpaces issuerNameSpaces)
    {
        List<? extends IssuerNameSpacesEntry> issuerNameSpaceEntries =
                (List<? extends IssuerNameSpacesEntry>)issuerNameSpaces.getPairs();

        List<CBORString> nameSpaces = issuerNameSpaceEntries.stream()
                .map(entry -> entry.getNameSpace())
                .collect(Collectors.toList());

        // AuthorizedNameSpaces = [ + NameSpace ]
        return new AuthorizedNameSpaces(nameSpaces);
    }


    private Map<String, Object> prepareClaims()
    {
        // The claims given to this builder as input data.
        Map<String, Object> claims = getClaims();

        if (claims == null)
        {
            claims = Collections.emptyMap();
        }

        return claims;
    }


    private CBORizer prepareCBORizer()
    {
        // The cborizer given to this builder.
        CBORizer cborizer = getCBORizer();

        if (cborizer == null)
        {
            cborizer = new CBORizer();
        }

        return cborizer;
    }


    private static byte[] generateRandom()
    {
        // ISO/IEC 18013-5:2021, 9.1.2.5 Message digest function
        //
        //   Each IssuerSignedItem shall also contain an unpredictable random
        //   or pseudorandom value. This value shall be different for each
        //   IssuerSignedItem and shall have a minimum length of 16 bytes.
        //
        return generateRandomBytes(16);
    }


    /**
     * Generate a byte array of the specified size containing a random value.
     */
    private static byte[] generateRandomBytes(int size)
    {
        byte[] bytes = new byte[size];

        RANDOM.nextBytes(bytes);

        return bytes;
    }


    private static byte[] computeDigest(byte[] input)
    {
        return computeDigest(input, "SHA-256");
    }


    /**
     * Compute the digest of the input with the specified hash algorithm.
     *
     * @throws IllegalArgumentException
     *         The specified hash algorithm is not supported.
     */
    private static byte[] computeDigest(byte[] input, String hashAlgorithm)
    {
        try
        {
            return MessageDigest.getInstance(hashAlgorithm).digest(input);
        }
        catch (NoSuchAlgorithmException cause)
        {
            // Error message
            String message = String.format(
                    "The hash algorithm '%s' is not supported.", hashAlgorithm);

            throw new RuntimeException(message, cause);
        }
    }


    private static class SequentialIntegerGenerator
    {
        private int number;

        public int next()
        {
            return ++number;
        }
    }
}

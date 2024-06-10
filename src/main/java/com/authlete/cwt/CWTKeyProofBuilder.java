/*
 * Copyright (C) 2024 Authlete, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.authlete.cwt;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;
import com.authlete.cbor.CBORByteArray;
import com.authlete.cose.COSEException;
import com.authlete.cose.COSEKey;
import com.authlete.cose.COSEProtectedHeader;
import com.authlete.cose.COSEProtectedHeaderBuilder;
import com.authlete.cose.COSESign1;
import com.authlete.cose.COSESign1Builder;
import com.authlete.cose.COSESigner;
import com.authlete.cose.SigStructure;
import com.authlete.cose.SigStructureBuilder;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;


/**
 * A utility to generate a CWT key proof, which is defined in the specification
 * of "OpenID for Verifiable Credential Issuance".
 *
 * <h3>EXAMPLE</h3>
 *
 * <pre style="border: 1px solid black; padding: 1em 0 0; margin: 1em;">
 * <span style="color: darkgreen;">// The identifier of the client application.</span>
 * String client = "my_client_id";
 *
 * <span style="color: darkgreen;">// The identifier of the credential issuer.</span>
 * String issuer = "https://credential-issuer.example.com";
 *
 * <span style="color: darkgreen;">// The value of 'c_nonce' issued by the authorization server
 * // or the credential issuer.</span>
 * String nonce = "my_nonce";
 *
 * <span style="color: darkgreen;">// A private key for signing. The public key corresponding to
 * // this private key will be embedded in the protected header.</span>
 * COSEKey key = ...;
 *
 * <span style="color: darkgreen;">// Generate a CWT representing a key proof.</span>
 * {@link CWT} cwt = new CWTKeyProofBuilder()
 *     .{@link #setClient(String) setClient}(client)
 *     .{@link #setIssuer(String) setIssuer}(issuer)
 *     .{@link #setNonce(String) setNonce}(nonce)
 *     .{@link #setKey(COSEKey) setKey}(key)
 *     .{@link #build()};
 *
 * <span style="color: darkgreen;">// The base64url representation of the key proof.</span>
 * String base64url = cwt.encodeToBase64Url();
 * </pre>
 *
 * <p>
 * The variable "{@code base64url}" in the example above holds a value like below.
 * </p>
 *
 * <div  style="border: 1px solid black; padding: 1em; margin: 1em;"">
 * <code><span style="word-break: break-all;"><!--
 * -->2D3ShFidowEmA3RvcGVuaWQ0dmNpLXByb29mK2N3dGhDT1NFX0tleaYBAgJYK0pvWWcwSm<!--
 * -->NhS1VPN00zRDlfUVR1UjR4Zl90MWhZZkNsVG95cS05dC1oWXMDJiABIVgguNp2r4R7DNp3<!--
 * -->gPrnhtc93CY7gSDRdMwTris8ZPoeCGQiWCBFEpNEFZfxlYyevnC8dY9KOVjQhqAOyI7m5X<!--
 * -->tohBHbO6BYR6QDeCVodHRwczovL2NyZWRlbnRpYWwtaXNzdWVyLmV4YW1wbGUuY29tBhpm<!--
 * -->PeFaAWxteV9jbGllbnRfaWQKSG15X25vbmNlWEB6nBjmxfuVI7LVcQKXggEATC-AVN_5Vi<!--
 * -->fZEC6NLJL-vpFaAppWRgW5QkDmTouV7WGDnXoomamaWzazgVgcvrWo</span></code>
 * </div>
 *
 * <p>
 * The following is the CBOR Diagnostic Notation representation of the example
 * CWT key proof above. Refer to <a href="https://www.rfc-editor.org/rfc/rfc8949#section-8"
 * >RFC 8949, 8. Diagnostic Notation</a> and <a href="https://www.rfc-editor.org/rfc/rfc8610#appendix-G"
 * >RFC 8610, Appendix G. Extended Diagnostic Notation</a> for details about the
 * CBOR Diagnostic Notation.
 * </p>
 *
 * <pre style="border: 1px solid black; padding: 1em 0 0; margin: 1em;">
 * 61(18(/ COSE_Sign1 / [
 *   / protected / <<
 *     {
 *       1: -7,
 *       3: "openid4vci-proof+cwt",
 *       "COSE_Key": {
 *         1: 2,
 *         2: h'4a6f5967304a63614b554f374d3344395f515475523478665f7431685966436c546f79712d39742d685973',
 *         3: -7,
 *         -1: 1,
 *         -2: h'b8da76af847b0cda7780fae786d73ddc263b8120d174cc13ae2b3c64fa1e0864',
 *         -3: h'451293441597f1958c9ebe70bc758f4a3958d086a00ec88ee6e57b688411db3b'
 *       }
 *     }
 *   >>,
 *   / unprotected / {
 *   },
 *   h'a403782568747470733a2f2f63726564656e7469616c2d6973737565722e6578616d706c652e636f6d061a663de15a016c6d795f636c69656e745f69640a486d795f6e6f6e6365',
 *   h'7a9c18e6c5fb9523b2d57102978201004c2f8054dff95627d9102e8d2c92febe915a029a564605b94240e64e8b95ed61839d7a2899a99a5b36b381581cbeb5a8'
 * ]))
 * </pre>
 *
 * <p>
 * FYI: <b>CBOR Zone</b> (<a href="https://cbor.zone/">https://cbor.zone/</a>)
 * can be used to decode CBOR data.
 * </p>
 *
 * <h3>COMMAND LINE INVOCATION</h3>
 *
 * <p>
 * This class provides the {@link #main(String[])} method for invocation
 * from the command line. Refer to the JavaDoc of the method for details.
 * The <code><a href="https://github.com/authlete/cbor/blob/main/bin/generate-cwt-key-proof"
 * >generate-cwt-key-proof</a></code> script is a wrapper for the command line
 * invocation.
 * </p>
 *
 * @since 1.15
 *
 * @see <a href="https://openid.github.io/OpenID4VCI/openid-4-verifiable-credential-issuance-wg-draft.html"
 *      >OpenID for Verifiable Credential Issuance</a>
 */
public class CWTKeyProofBuilder
{
    private static final String CONTENT_TYPE   = "openid4vci-proof+cwt";
    private static final String LABEL_COSE_KEY = "COSE_Key";


    private String client;
    private String issuer;
    private Date issuedAt;
    private String nonce;
    private COSEKey key;


    /**
     * The default constructor.
     */
    public CWTKeyProofBuilder()
    {
    }


    private CWTKeyProofBuilder(Options options)
    {
        this.client = options.client;
        this.issuer = options.issuer;
        this.nonce  = options.nonce;
        this.key    = options.key;
    }


    /**
     * Get the identifier of the client application. This value is used as the
     * value of the Claim Key 1 ({@code iss}).
     *
     * <p>
     * In most cases, this parameter is mandatory. Omission of this parameter is
     * allowed only when the access token has been issued by the pre-authorized
     * code flow with anonymous access. Refer to "OpenID for Verifiable Credential
     * Issuance" for details.
     * </p>
     *
     * @return
     *         The identifier of the client application.
     *
     * @see <a href="https://www.iana.org/assignments/cwt/cwt.xhtml#claims-registry"
     *      >IANA: CBOR Web Token (CWT) Claims</a>
     */
    public String getClient()
    {
        return client;
    }


    /**
     * Set the identifier of the client application. This value is used as the
     * value of the Claim Key 1 ({@code iss}).
     *
     * <p>
     * In most cases, this parameter is mandatory. Omission of this parameter is
     * allowed only when the access token has been issued by the pre-authorized
     * code flow with anonymous access. Refer to "OpenID for Verifiable Credential
     * Issuance" for details.
     * </p>
     *
     * @param client
     *         The identifier of the client application.
     *
     * @return
     *         {@code this} object.
     *
     * @see <a href="https://www.iana.org/assignments/cwt/cwt.xhtml#claims-registry"
     *      >IANA: CBOR Web Token (CWT) Claims</a>
     */
    public CWTKeyProofBuilder setClient(String client)
    {
        this.client = client;

        return this;
    }


    /**
     * Get the identifier of the credential issuer. This value is used as the
     * value of the Claim Key 3 ({@code aud}).
     *
     * <p>
     * This parameter is mandatory.
     * </p>
     *
     * @return
     *         The identifier of the credential issuer.
     *
     * @see <a href="https://www.iana.org/assignments/cwt/cwt.xhtml#claims-registry"
     *      >IANA: CBOR Web Token (CWT) Claims</a>
     */
    public String getIssuer()
    {
        return issuer;
    }


    /**
     * Set the identifier of the credential issuer. This value is used as the
     * value of the Claim Key 3 ({@code aud}).
     *
     * <p>
     * This parameter is mandatory.
     * </p>
     *
     * @param issuer
     *         The identifier of the credential issuer.
     *
     * @return
     *         {@code this} object.
     *
     * @see <a href="https://www.iana.org/assignments/cwt/cwt.xhtml#claims-registry"
     *      >IANA: CBOR Web Token (CWT) Claims</a>
     */
    public CWTKeyProofBuilder setIssuer(String issuer)
    {
        this.issuer = issuer;

        return this;
    }


    /**
     * Get the issuance time. This value is used as the value of the Claim Key 6
     * ({@code iat}).
     *
     * <p>
     * If this parameter has not been specified, the {@link #build()} method uses
     * the current time.
     * </p>
     *
     * @return
     *         The issuance time.
     *
     * @see <a href="https://www.iana.org/assignments/cwt/cwt.xhtml#claims-registry"
     *      >IANA: CBOR Web Token (CWT) Claims</a>
     */
    public Date getIssuedAt()
    {
        return issuedAt;
    }


    /**
     * Set the issuance time. This value is used as the value of the Claim Key 6
     * ({@code iat}).
     *
     * <p>
     * If this parameter has not been specified, the {@link #build()} method uses
     * the current time.
     * </p>
     *
     * @param issuedAt
     *         The issuance time.
     *
     * @return
     *         {@code this} object.
     *
     * @see <a href="https://www.iana.org/assignments/cwt/cwt.xhtml#claims-registry"
     *      >IANA: CBOR Web Token (CWT) Claims</a>
     */
    public CWTKeyProofBuilder setIssuedAt(Date issuedAt)
    {
        this.issuedAt = issuedAt;

        return this;
    }


    /**
     * Get the nonce value, which is the value of "{@code c_nonce}" issued by
     * the server (the authorization server or the credential issuer). This value
     * is used as the value of the Claim Key 10 ({@code Nonce}).
     *
     * <p>
     * If a {@code c_nonce} has been issued by the server (the authorization
     * server or the credential issuer), this parameter is mandatory.
     * </p>
     *
     * @return
     *         The nonce value.
     *
     * @see <a href="https://www.iana.org/assignments/cwt/cwt.xhtml#claims-registry"
     *      >IANA: CBOR Web Token (CWT) Claims</a>
     */
    public String getNonce()
    {
        return nonce;
    }


    /**
     * Set the nonce value, which is the value of "{@code c_nonce}" issued by
     * the server (the authorization server or the credential issuer). This value
     * is used as the value of the Claim Key 10 ({@code Nonce}).
     *
     * <p>
     * If a {@code c_nonce} has been issued by the server (the authorization
     * server or the credential issuer), this parameter is mandatory.
     * </p>
     *
     * @param nonce
     *         The nonce value.
     *
     * @return
     *         {@code this} object.
     *
     * @see <a href="https://www.iana.org/assignments/cwt/cwt.xhtml#claims-registry"
     *      >IANA: CBOR Web Token (CWT) Claims</a>
     */
    public CWTKeyProofBuilder setNonce(String nonce)
    {
        this.nonce = nonce;

        return this;
    }


    /**
     * Get the private key.
     *
     * <p>
     * The private key is used for signing the CWT key proof. In addition, the
     * public key corresponding to the private key is embedded in the protected
     * header of the CWT key proof as the value of "{@code COSE_Key}".
     * </p>
     *
     * <p>
     * In the current implementation, the key must be an instance of
     * {@link com.authlete.cose.COSEEC2Key COSEEC2Key}, because supported key
     * algorithms are {@code ES256}, {@code ES384} and {@code ES512} only.
     * </p>
     *
     * @return
     *         The private key.
     */
    public COSEKey getKey()
    {
        return key;
    }


    /**
     * Set the private key.
     *
     * <p>
     * The private key is used for signing the CWT key proof. In addition, the
     * public key corresponding to the private key is embedded in the protected
     * header of the CWT key proof as the value of "{@code COSE_Key}".
     * </p>
     *
     * <p>
     * In the current implementation, the key must be an instance of
     * {@link com.authlete.cose.COSEEC2Key COSEEC2Key}, because supported key
     * algorithms are {@code ES256}, {@code ES384} and {@code ES512} only.
     * </p>
     *
     * @param key
     *         The private key.
     *
     * @return
     *         {@code this} object.
     */
    public CWTKeyProofBuilder setKey(COSEKey key)
    {
        this.key = key;

        return this;
    }


    /**
     * Generate a CWT key proof.
     *
     * <p>
     * At least the issuer and the private key must be set before this method
     * is called.
     * </p>
     *
     * @return
     *         A {@link CWT} instance that represents a CWT key proof.
     *
     * @throws IllegalStateException
     *         A mandatory parameter is not set, or the specified key does not
     *         satisfy required conditions.
     *
     * @throws COSEException
     *         Failed to generate a CWT key proof.
     */
    public CWT build() throws IllegalStateException, COSEException
    {
        // Check the state of this CWTKeyProofBuilder instance to ensure
        // that necessary pieces of information have been set properly
        // for executing this 'build()' method.
        checkState();

        // The private key. This is used for signing. In addition, the
        // public key corresponding to this private key is embedded in
        // the protected header.
        COSEKey key = getKey();

        // Protected header
        COSEProtectedHeader protectedHeader = buildProtectedHeader(key);

        // Payload
        CBORByteArray payload = buildPayload();

        // Signature
        byte[] signature = buildSignature(protectedHeader, payload, key);

        // COSE_Sign1
        COSESign1 sign1 = buildSign1(protectedHeader, payload, signature);

        // CWT
        return new CWT(sign1);
    }


    private void checkState()
    {
        checkIssuer();
        checkKey();
    }


    private void checkIssuer()
    {
        // If 'issuer' is set.
        if (getIssuer() != null)
        {
            // OK.
            return;
        }

        // 'issuer' is necessary as it is used as the value of 'aud'.
        throw new IllegalStateException("'issuer' is not set.");
    }


    private void checkKey()
    {
        COSEKey key = getKey();

        // If 'key' is not set.
        if (key == null)
        {
            throw new IllegalStateException("'key' is not set.");
        }

        // If the key is not a private key.
        if (!key.isPrivate())
        {
            throw new IllegalStateException("The key is not a private key.");
        }

        // The algorithm of the key.
        Object alg = key.getAlg();

        // If the representation of the algorithm is not a number.
        if (!(alg instanceof Number))
        {
            throw new IllegalStateException("The representation of the algorithm of the key is not a number.");
        }
    }


    private static COSEProtectedHeader buildProtectedHeader(COSEKey key) throws COSEException
    {
        // OpenID for Verifiable Credential Issuance
        // 7.2.1.3. cwt Proof Type
        //
        //   Label 1 (alg):
        //     REQUIRED. A digital signature algorithm identifier such as per
        //     IANA "COSE Algorithms" registry [IANA.COSE.ALGS]. It MUST NOT
        //     be an identifier for a symmetric algorithm (MAC).
        //
        //   Label 3 (content type):
        //     REQUIRED. MUST be openid4vci-proof+cwt, which explicitly types
        //     the key proof CWT.
        //
        //   (string-valued) Label COSE_Key:
        //     OPTIONAL (byte string). COSE key material the new Credential
        //     shall be bound to. It MUST NOT be present if x5chain is present.
        //
        //   Label 33 (x5chain):
        //     OPTIONAL (byte string). As defined in [RFC9360], it contains an
        //     ordered array of X.509 certificates corresponding to the key used
        //     to sign the CWT. It MUST NOT be present if COSE_Key is present.
        //

        // Convert the private key to the corresponding public key.
        COSEKey pubKey = key.toPublic();

        // Wrap the public key into a byte string.
        CBORByteArray pubKeyEmbedded = new CBORByteArray(pubKey.encode(), pubKey);

        return new COSEProtectedHeaderBuilder()
                .alg(key.getAlg())
                .contentType(CONTENT_TYPE)
                .put(LABEL_COSE_KEY, pubKeyEmbedded)
                .build();
    }


    private CBORByteArray buildPayload()
    {
        CWTClaimsSet claims = buildClaims();

        // The CBOR representation of the CWT Claims Set.
        byte[] encodedClaims = claims.encode();

        // Embed the encoded claims into a byte string.
        return new CBORByteArray(encodedClaims);
    }


    private CWTClaimsSet buildClaims()
    {
        // OpenID for Verifiable Credential Issuance
        // 7.2.1.3. cwt Proof Type
        //
        //   Claim Key 1 (iss):
        //     OPTIONAL (text string). The value of this claim MUST be the
        //     client_id of the Client making the Credential request. This
        //     claim MUST be omitted if the access token authorizing the
        //     issuance call was obtained from a Pre-Authorized Code Flow
        //     through anonymous access to the token endpoint.
        //
        //   Claim Key 3 (aud):
        //     REQUIRED (text string). The value of this claim MUST be the
        //     Credential Issuer Identifier.
        //
        //   Claim Key 6 (iat):
        //     REQUIRED (integer or floating-point number). The value of
        //     this claim MUST be the time at which the key proof was issued.
        //
        //   Claim Key 10 (Nonce):
        //     OPTIONAL (byte string). The value of this claim MUST be a
        //     server-provided c_nonce converted from string to bytes. It
        //     MUST be present when the Wallet received a server-provided
        //     c_nonce.
        //
        CWTClaimsSetBuilder builder = new CWTClaimsSetBuilder();

        // Claim Key 1 (iss)
        if (getClient() != null)
        {
            builder.iss(getClient());
        }

        // Claim Key 3 (aud)
        builder.aud(getIssuer());

        // Claim Key 6 (iat)
        builder.iat(determineIssuedAt());

        // Claim Key 10 (Nonce)
        if (getNonce() != null)
        {
            builder.nonce(getNonce());
        }

        return builder.build();
    }


    private Date determineIssuedAt()
    {
        Date iat = getIssuedAt();

        // If the issuance time has not been set explicitly.
        if (iat == null)
        {
            // Use the current time.
            iat = new Date();
        }

        return iat;
    }


    private static byte[] buildSignature(
            COSEProtectedHeader protectedHeader, CBORByteArray payload,
            COSEKey signingKey) throws COSEException
    {
        // Sig_structure
        SigStructure structure = buildSigStructure(protectedHeader, payload);

        // Signer
        COSESigner signer = createSigner(signingKey);

        // The algorithm identifier as an integer.
        int alg = ((Number)signingKey.getAlg()).intValue();

        // The key ID (optional).
        byte[] kid = signingKey.getKid();

        // Sign the Sig_structure (= generate a signature).
        return signer.sign(structure, alg, kid);
    }


    private static SigStructure buildSigStructure(
            COSEProtectedHeader protectedHeader, CBORByteArray payload)
    {
        // Create a Sig_structure for COSE_Sign1.
        return new SigStructureBuilder()
                .signature1()
                .bodyAttributes(protectedHeader)
                .payload(payload)
                .build();
    }


    private static COSESigner createSigner(COSEKey signingKey) throws COSEException
    {
        return new COSESigner(signingKey.createPrivateKey());
    }


    private static COSESign1 buildSign1(
            COSEProtectedHeader protectedHeader, CBORByteArray payload, byte[] signature)
    {
        // Create a COSE_Sign1.
        return new COSESign1Builder()
                .protectedHeader(protectedHeader)
                .payload(payload)
                .signature(signature)
                .build();
    }


    /**
     * The entry point for invocation from the command line.
     * The following command line options are recognized.
     *
     * <blockquote>
     * <table border="1" cellpadding="5" style="border-collapse: collapse;">
     * <tr bgcolor="orange">
     *   <th>option</th>
     *   <th>description</th>
     * </tr>
     * <tr>
     *   <td><nobr><code>--issuer <i>ISSUER</i></code></nobr></td>
     *   <td>
     *     <p>
     *       REQUIRED. This option specifies the identifier of the credential issuer.
     *     </p>
     *   </td>
     * </tr>
     * <tr>
     *   <td><nobr><code>--key <i>FILE</i></code></nobr></td>
     *   <td>
     *     <p>
     *       REQUIRED. This option specifies the file containing a private key in the
     *       JWK format (<a href="https://www.rfc-editor.org/rfc/rfc7517.html">RFC 7517:
     *       JSON Web Key (JWK)</a>).
     *     </p>
     *   </td>
     * </tr>
     * <tr>
     *   <td><nobr><code>--client <i>CLIENT</i></code></nobr></td>
     *   <td>
     *     <p>
     *       OPTIONAL. This option specifies the identifier of the client application.
     *     </p>
     *   </td>
     * </tr>
     * <tr>
     *   <td><nobr><code>--nonce <i>NONCE</i></code></nobr></td>
     *   <td>
     *     <p>
     *       OPTIONAL. This option specifies the value of "{@code c_nonce}" that has
     *       been issued by the authorization server or the credential issuer.
     *     </p>
     *   </td>
     * </tr>
     * <tr>
     *   <td><code>--help</code></td>
     *   <td>
     *     <p>
     *       This option shows the help text.
     *     </p>
     *   </td>
     * </tr>
     * </table>
     * </blockquote>
     *
     * @param args
     *         The command line arguments.
     */
    public static void main(String[] args)
    {
        try
        {
            // Process the command line arguments.
            Options options = new Options().process(args);

            // Build a CWT key proof.
            CWT cwt = new CWTKeyProofBuilder(options).build();

            // The base64url representation of the CWT key proof.
            String rep = cwt.encodeToBase64Url();

            // Print the representation.
            System.out.println(rep);
        }
        catch (Exception cause)
        {
            System.err.format("ERROR: %s%n", cause.getMessage());

            if (!(cause instanceof IllegalArgumentException))
            {
                cause.printStackTrace(System.err);
            }

            System.exit(1);
        }
    }


    private static final String HELP =
            "USAGE:%n%n" +
            "  java %s%n" +
            "    --issuer ISSUER   # specifies the identifier of the credential issuer.%n" +
            "    --key FILE        # specifies the file containing a private key in the JWK format.%n" +
            "    [--client CLIENT] # specifies the identifier of the client application.%n" +
            "    [--nonce NONCE]   # specifies the 'c_nonce' value issued by the server.%n" +
            "    [--help]          # shows this help text.%n%n" +
            "NOTE:%n%n" +
            "  Supported key algorithms are ES256, ES384 and ES512 only.%n%n"
            ;


    private static class Options
    {
        public String client;
        public String issuer;
        public String nonce;
        public COSEKey key;


        public Options process(String[] args) throws IOException, COSEException
        {
            parse(args);
            validate();

            return this;
        }


        private void parse(String[] args) throws IOException, COSEException
        {
            for (int i = 0; i < args.length; i++)
            {
                String arg = args[i];

                switch (arg)
                {
                    case "--client":
                        client = next(arg, args, ++i);
                        break;

                    case "--issuer":
                        issuer = next(arg, args, ++i);
                        break;

                    case "--nonce":
                        nonce = next(arg, args, ++i);
                        break;

                    case "--key":
                        String file = next(arg, args, ++i);
                        key = readKey(file);
                        break;

                    case "--help":
                        help(0);
                        break;

                    default:
                        System.err.format("ERROR: Unexpected argument: %s%n%n", arg);
                        help(1);
                        break;
                }
            }
        }


        private String next(String arg, String[] args, int index)
        {
            if (args.length <= index)
            {
                throw new IllegalArgumentException(String.format(
                        "The option '%s' requires a following argument.", arg));
            }

            return args[index];
        }


        @SuppressWarnings("unchecked")
        private COSEKey readKey(String file) throws IOException, COSEException
        {
            // Path
            Path path = Paths.get(file);

            // The content in bytes.
            byte[] bytes = Files.readAllBytes(path);

            // Convert the bytes into a string.
            String string = new String(bytes, StandardCharsets.UTF_8);

            // Interpret the string as a JSON object, which should represent a JWK.
            Map<String, Object> map = new GsonBuilder()
                    .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                    .create()
                    .fromJson(string, Map.class);

            // Create a COSEKey instance from the map representing a JWK.
            return COSEKey.fromJwk(map);
        }


        private void help(int exitStatus)
        {
            // Show the help text.
            System.out.format(HELP, getClass().getName());
            System.exit(exitStatus);
        }


        private void validate()
        {
            if (issuer == null)
            {
                throw new IllegalArgumentException(
                        "The '--issuer ISSUER' option is mandatory.");
            }

            if (key == null)
            {
                throw new IllegalArgumentException(
                        "The '--key FILE' option is mandatory.");
            }
        }
    }
}

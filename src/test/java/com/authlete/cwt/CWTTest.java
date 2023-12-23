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
package com.authlete.cwt;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.IOException;
import java.util.Date;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;
import com.authlete.cbor.CBORByteArray;
import com.authlete.cbor.CBORDecoder;
import com.authlete.cbor.CBORDecoderException;
import com.authlete.cbor.CBORItem;
import com.authlete.cose.COSEMessage;
import com.authlete.cose.COSEMessageType;
import com.authlete.cose.COSESign1;


public class CWTTest
{
    /**
     * Signed CWT Example, copied from RFC 8392.
     *
     * <pre>
     * / CWT CBOR tag / 61(
     *   / COSE_Sign1 CBOR tag / 18(
     *     [
     *       / protected / << {
     *         / alg / 1: -7 / ECDSA 256 /
     *       } >>,
     *       / unprotected / {
     *         / kid / 4: h'4173796d6d657472696345434453413
     *                      23536' / 'AsymmetricECDSA256' /
     *       },
     *       / payload / << {
     *         / iss / 1: "coap://as.example.com",
     *         / sub / 2: "erikw",
     *         / aud / 3: "coap://light.example.com",
     *         / exp / 4: 1444064944,
     *         / nbf / 5: 1443944944,
     *         / iat / 6: 1443944944,
     *         / cti / 7: h'0b71'
     *       } >>,
     *       / signature / h'5427c1ff28d23fbad1f29c4c7c6a555e601d6fa29f
     *                       9179bc3d7438bacaca5acd08c8d4d4f96131680c42
     *                       9a01f85951ecee743a52b9b63632c57209120e1c9e
     *                       30'
     *     ]
     *   )
     * )
     * </pre>
     */
    private static final String CWT_EXAMPLE_HEX_STRING =
            // CWT CBOR Tag
            "d83d" +
            // COSE_Sign1 CBOR Tag + COSE_Sign1 (RFC 8392 A.3. Example Signed CWT)
            "d28443a10126a104524173796d6d657472696345434453413235365850a701756" +
            "36f61703a2f2f61732e6578616d706c652e636f6d02656572696b77037818636f" +
            "61703a2f2f6c696768742e6578616d706c652e636f6d041a5612aeb0051a5610d" +
            "9f0061a5610d9f007420b7158405427c1ff28d23fbad1f29c4c7c6a555e601d6f" +
            "a29f9179bc3d7438bacaca5acd08c8d4d4f96131680c429a01f85951ecee743a5" +
            "2b9b63632c57209120e1c9e30";


    /**
     * Convert the hex string into a byte array.
     */
    private static byte[] fromHex(String hex)
    {
        try
        {
            return Hex.decodeHex(hex);
        }
        catch (DecoderException cause)
        {
            cause.printStackTrace();

            fail("Failed to convert the given hexadecimal representation into a byte array: " + cause.getMessage());

            return null;
        }
    }


    /**
     * Decode the byte array as a CBOR item sequence and returns the first
     * CBOR item.
     */
    private static CBORItem decodeAsCborItem(byte[] bytes)
    {
        try
        {
            return new CBORDecoder(bytes).next();
        }
        catch (IOException cause)
        {
            cause.printStackTrace();

            fail("Failed to decode the given byte array as a CBOR item sequence: " + cause.getMessage());

            return null;
        }
    }


    private static CWTClaimsSet parseAsClaimsSet(CBORItem payload)
    {
        try
        {
            return CWTClaimsSet.build(payload);
        }
        catch (CBORDecoderException cause)
        {
            cause.printStackTrace();

            fail("Failed to parse the given byte array as a CWT claims set: " + cause.getMessage());

            return null;
        }
    }


    @Test
    public void test_read_cwt()
    {
        // Parse the byte array representing the CWT example as a CBOR item.
        CBORItem item = decodeAsCborItem(fromHex(CWT_EXAMPLE_HEX_STRING));

        // Because the CWT CBOR Tag is prepended, the decoder should return
        // an instance of the CWT class.
        assertTrue(item instanceof CWT);

        // Extract the COSE message of the CWT.
        COSEMessage cm = ((CWT)item).getMessage();

        // The type of the COSE message should be COSE_Sign1.
        assertEquals(COSEMessageType.COSE_SIGN1, cm.getType());

        // The COSEMessage instance can be cast to COSESign1.
        COSESign1 sign1 = (COSESign1)cm;

        // The payload of the COSE_Sign1 message.
        CBORItem payload = sign1.getPayload();

        // Parse the payload as CWTClaimsSet.
        CWTClaimsSet claims = parseAsClaimsSet(payload);

        String expectedIss = "coap://as.example.com";
        String expectedSub = "erikw";
        String expectedAud = "coap://light.example.com";
        Date   expectedExp = new Date(1444064944 * 1000L);
        Date   expectedNbf = new Date(1443944944 * 1000L);
        Date   expectedIat = new Date(1443944944 * 1000L);
        byte[] expectedCti = { (byte)0x0b, (byte)0x71 };

        assertEquals(expectedIss, claims.getIss());
        assertEquals(expectedSub, claims.getSub());
        assertEquals(expectedAud, claims.getAud());
        assertEquals(expectedExp, claims.getExp());
        assertEquals(expectedNbf, claims.getNbf());
        assertEquals(expectedIat, claims.getIat());
        assertArrayEquals(expectedCti, claims.getCti());
    }


    @Test
    public void test_write_claims()
    {
        String iss   = "ISS";
        String sub   = "SUB";
        String aud   = "AUD";
        Date   iat   = new Date(1000000L);
        Date   nbf   = new Date(2000000L);
        Date   exp   = new Date(3000000L);
        String cti   = "CTI";
        String nonce = "NONCE";

        CWTClaimsSet claims = new CWTClaimsSetBuilder()
                .iss(iss)
                .sub(sub)
                .aud(aud)
                .iat(iat)
                .nbf(nbf)
                .exp(exp)
                .cti(cti)
                .nonce(nonce)
                .build();

        // Encoded CBOR map
        byte[] encodedClaims = claims.encode();

        // Embed the CBOR map into a byte string.
        CBORByteArray payload = new CBORByteArray(encodedClaims);

        // Parse the payload.
        CWTClaimsSet parsedClaims = parseAsClaimsSet(payload);

        assertEquals(iss,   parsedClaims.getIss());
        assertEquals(sub,   parsedClaims.getSub());
        assertEquals(aud,   parsedClaims.getAud());
        assertEquals(iat,   parsedClaims.getIat());
        assertEquals(nbf,   parsedClaims.getNbf());
        assertEquals(exp,   parsedClaims.getExp());
        assertEquals(cti,   parsedClaims.getCtiAsString());
        assertEquals(nonce, parsedClaims.getNonceAsString());
    }
}

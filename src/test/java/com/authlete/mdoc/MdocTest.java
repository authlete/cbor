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


import static org.junit.Assert.assertEquals;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORValue;
import com.authlete.cose.COSEEC2Key;
import com.authlete.cose.COSEKeyBuilder;
import com.google.gson.Gson;


public class MdocTest
{
    @SuppressWarnings("unchecked")
    @Test
    public void test_01() throws Exception
    {
        // Doc Type
        String docType = "com.example.doctype";

        // Claims
        Map<String, Object> claims = new Gson().fromJson(
                "{\n" +
                "  \"com.example.namespace1\": {\n" +
                "    \"claimName1\": \"claimValue1\"\n" +
                "  },\n" +
                "  \"com.example.namespace2\": {\n" +
                "    \"claimName2\": \"claimValue2\"\n" +
                "  }\n" +
                "}",
                Map.class
        );

        // Validity Information
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC).withNano(0);
        ValidityInfo validityInfo =
                new ValidityInfo(now, now, now.plusYears(10));

        // Issuer key
        COSEEC2Key issuerKey = new COSEKeyBuilder()
                .ktyEC2()
                .ec2CrvP256()
                .ec2XInBase64Url("Qw7367PjIwU17ckX_G4ZqLW2EjPG0efV0cYzhvq2Ujk")
                .ec2YInBase64Url("Mpq3N90VZIBBOqvYgAHi4ZfOSK2gM09_UozgVdRCrt4")
                .ec2DInBase64Url("IzdjF8wyUSqsCbz8kh6ysJOUcK003aCt9hIGFiGWlzI")
                .buildEC2Key();

        // Certificate for the issuer key in the PEM format.
        String issuerCertPem =
                "-----BEGIN CERTIFICATE-----\n" +
                "MIIBXzCCAQSgAwIBAgIGAYwpA4/aMAoGCCqGSM49BAMCMDYxNDAyBgNVBAMMKzNf\n" +
                "d1F3Y3Qxd28xQzBST3FfWXRqSTRHdTBqVXRiVTJCQXZteEltQzVqS3MwHhcNMjMx\n" +
                "MjAyMDUzMjI4WhcNMjQwOTI3MDUzMjI4WjA2MTQwMgYDVQQDDCszX3dRd2N0MXdv\n" +
                "MUMwUk9xX1l0akk0R3UwalV0YlUyQkF2bXhJbUM1aktzMFkwEwYHKoZIzj0CAQYI\n" +
                "KoZIzj0DAQcDQgAEQw7367PjIwU17ckX/G4ZqLW2EjPG0efV0cYzhvq2Ujkymrc3\n" +
                "3RVkgEE6q9iAAeLhl85IraAzT39SjOBV1EKu3jAKBggqhkjOPQQDAgNJADBGAiEA\n" +
                "o4TsuxDl5+3eEp6SHDrBVn1rqOkGGLoOukJhelndGqICIQCpocrjWDwrWexoQZOO\n" +
                "rwnEYRBmmfhaPor2OZCrbP3U6w==\n" +
                "-----END CERTIFICATE-----\n";

        // Certificate for the issuer key as X509Certificate.
        X509Certificate issuerCert = (X509Certificate)
                CertificateFactory.getInstance("X.509")
                        .generateCertificate(new ByteArrayInputStream(
                                issuerCertPem.getBytes(StandardCharsets.UTF_8)));

        // Certificate chain for the issuer key.
        List<X509Certificate> issuerCertChain = List.of(issuerCert);

        // Build an "IssuerSigned" instance.
        IssuerSigned issuerSigned = new IssuerSignedBuilder()
                .setDocType(docType)
                .setClaims(claims)
                .setValidityInfo(validityInfo)
                .setIssuerKey(issuerKey)
                .setIssuerCertChain(issuerCertChain)
                .build();

        // Build a "Document" instance.
        Document document = new Document(docType, issuerSigned);

        // Build a "DeviceResponse" instance.
        @SuppressWarnings("unused")
        DeviceResponse deviceResponse = new DeviceResponse(List.of(document));

        // The actual value held by the document instance.
        String actualDocType = (String)((CBORValue<?>)
                document.findByKey("docType").getValue()).getValue();

        assertEquals(docType, actualDocType);

        //dump(deviceResponse);
    }


    @SuppressWarnings("unused")
    private static void dump(CBORItem item)
    {
        dump("Base64",    item.encodeToBase64());
        dump("Base64URL", item.encodeToBase64Url());
        dump("Hex",       item.encodeToHex());
        dump("Diagnostic Notiation", item.prettify());
    }


    private static void dump(String title, String content)
    {
        System.out.println(title);
        System.out.println(content);
        System.out.println();
    }
}

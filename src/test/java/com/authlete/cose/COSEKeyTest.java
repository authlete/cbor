/*
 * Copyright (C) 2023-2024 Authlete, Inc.
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


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import com.authlete.cose.constants.COSEAlgorithms;
import com.authlete.cose.constants.COSEEllipticCurves;
import com.authlete.cose.constants.COSEKeyOperations;
import com.authlete.cose.constants.COSEKeyTypes;


public class COSEKeyTest
{
    private static Map<String, Object> createJwkMap()
    {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("kty",     "EC");
        map.put("alg",     "ES256");
        map.put("kid",     "614zVgCx2TB6G3UFaSDa8OXtsOErqey5NuaLbeEIfnc");
        map.put("key_ops", Arrays.asList("sign"));
        map.put("crv",     "P-256");
        map.put("x",       "uPoOtJEwVeqcK981etm_Dt4GMcuurZgQmLTy01JP8Go");
        map.put("y",       "7U96jtdK6VCOFK-MzCKLDQtLyrCRBbO0iI1nVRXfeTU");
        map.put("d",       "3rPjomH365J633q6EJetrDXuZwv1akXpRhBMSzbocfY");


        return map;
    }


    private static String encodeByBase64Url(byte[] input)
    {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(input);
    }


    @Test
    public void test_to_cosekey() throws COSEException
    {
        Map<String, Object> map = createJwkMap();

        COSEKey coseKey = COSEKey.fromJwk(map);

        assertTrue(coseKey instanceof COSEEC2Key);
        COSEEC2Key ec2Key = (COSEEC2Key)coseKey;

        // kty
        assertEquals(Integer.valueOf(COSEKeyTypes.EC2), ec2Key.getKty());

        // alg
        assertEquals(Integer.valueOf(COSEAlgorithms.ES256), ec2Key.getAlg());

        // kid
        assertEquals(map.get("kid"), new String(ec2Key.getKid(), StandardCharsets.UTF_8));

        // key_ops
        List<Object> keyOps = ec2Key.getKeyOps();
        assertNotNull(keyOps);
        assertEquals(1, keyOps.size());
        assertEquals(Integer.valueOf(COSEKeyOperations.SIGN), keyOps.get(0));

        // crv
        assertEquals(Integer.valueOf(COSEEllipticCurves.P_256), ec2Key.getCrv());

        // x
        assertEquals(map.get("x"), encodeByBase64Url(ec2Key.getX()));

        // y
        assertEquals(map.get("y"), encodeByBase64Url((byte[])ec2Key.getY()));

        // d
        assertEquals(map.get("d"), encodeByBase64Url(ec2Key.getD()));
    }


    @Test
    public void test_to_public() throws COSEException
    {
        // Create a private key.
        COSEKey privateKey = COSEKey.fromJwk(createJwkMap());
        assertTrue(privateKey.isPrivate());

        // Create a public key from the private key.
        COSEKey publicKey = privateKey.toPublic();
        assertFalse(publicKey.isPrivate());

        assertTrue(publicKey instanceof COSEEC2Key);
        COSEEC2Key ec2PublicKey = (COSEEC2Key)publicKey;

        // Confirm that the public key does not contain private parts.
        assertNull(ec2PublicKey.getD());
    }
}

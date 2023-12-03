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
package com.authlete.cose;


import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.authlete.cbor.CBORPair;
import com.authlete.cbor.CBORPairList;
import com.authlete.cbor.CBORizer;
import com.authlete.cose.constants.COSEEllipticCurves;
import com.authlete.cose.constants.COSEKeyCommonParameters;
import com.authlete.cose.constants.COSEKeyTypeParameters;
import com.authlete.cose.constants.COSEKeyTypes;


/**
 * A utility to build an instance of {@link COSEKey} or its subclass
 * (e.g. {@link COSEOKPKey} and {@link COSEEC2Key}).
 *
 * @since 1.1
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9052.html#name-key-objects"
 *      >RFC 9052, 7. Key Objects</a>
 *
 * @see <a href="https://www.iana.org/assignments/cose/cose.xhtml#key-common-parameters"
 *      >IANA: COSE Key Common Parameters</a>
 *
 * @see <a href="https://www.iana.org/assignments/cose/cose.xhtml#key-type-parameters"
 *      >IANA: COSE Key Type Parameters</a>
 *
 * @see <a href="https://www.iana.org/assignments/cose/cose.xhtml#key-type"
 *      >IANA: COSE Key Types</a>
 *
 * @see <a href="https://www.iana.org/assignments/cose/cose.xhtml#elliptic-curves"
 *      >IANA: COSE Elliptic Curves</a>
 */
public class COSEKeyBuilder
{
    /**
     * Decoder for base64url.
     */
    private static final Base64.Decoder BASE64URL_DECODER = Base64.getUrlDecoder();


    private final Map<Object, Object> map = new LinkedHashMap<>();


    /**
     * Decode the given string in the base64url format into a byte array.
     *
     * @param input
     *         A string in the base64url format.
     *
     * @return
     *         A decoded byte array.
     */
    private static byte[] decodeBase64Url(String input)
    {
        return BASE64URL_DECODER.decode(input);
    }


    /**
     * Set a parameter.
     *
     * @param key
     *         The key of the parameter.
     *
     * @param value
     *         The value of the parameter.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder put(Object key, Object value)
    {
        map.put(key, value);

        return this;
    }


    /**
     * Set the "kty (1)" parameter.
     *
     * @param kty
     *         The key type.
     *
     * @return
     *         {@code this} object.
     *
     * @see <a href="https://www.iana.org/assignments/cose/cose.xhtml#key-type"
     *      >IANA: COSE Key Types</a>
     */
    public COSEKeyBuilder kty(String kty)
    {
        return put(COSEKeyCommonParameters.KTY, kty);
    }


    /**
     * Set the "kty (1)" parameter.
     *
     * @param kty
     *         The key type.
     *
     * @return
     *         {@code this} object.
     *
     * @see <a href="https://www.iana.org/assignments/cose/cose.xhtml#key-type"
     *      >IANA: COSE Key Types</a>
     */
    public COSEKeyBuilder kty(int kty)
    {
        return put(COSEKeyCommonParameters.KTY, kty);
    }


    /**
     * Set "OKP (1)" to the "kty (1)" parameter.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder ktyOKP()
    {
        return kty(COSEKeyTypes.OKP);
    }


    /**
     * Set "EC2 (2)" to the "kty (1)" parameter.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder ktyEC2()
    {
        return kty(COSEKeyTypes.EC2);
    }


    /**
     * Set "RSA (3)" to the "kty (1)" parameter.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder ktyRSA()
    {
        return kty(COSEKeyTypes.RSA);
    }


    /**
     * Set "Symmetric (4)" to the "kty (1)" parameter.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder ktySymmetric()
    {
        return kty(COSEKeyTypes.SYMMETRIC);
    }


    /**
     * Set "HSS-LMS (5)" to the "kty (1)" parameter.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder ktyHSSLMS()
    {
        return kty(COSEKeyTypes.HSS_LMS);
    }


    /**
     * Set "WalnutDSA (6)" to the "kty (1)" parameter.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder ktyWalnutDSA()
    {
        return kty(COSEKeyTypes.WALNUT_DSA);
    }


    /**
     * Set the "kid (2)" parameter.
     *
     * @param kid
     *         The key ID.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder kid(byte[] kid)
    {
        return put(COSEKeyCommonParameters.KID, kid);
    }


    /**
     * Set the "kid (2)" parameter.
     *
     * @param kid
     *         The key ID. Its UTF-8 byte sequence is used.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder kid(String kid)
    {
        byte[] bytes = (kid != null) ? kid.getBytes(StandardCharsets.UTF_8): null;

        return kid(bytes);
    }


    /**
     * Set the "alg (3)" parameter.
     *
     * @param alg
     *         The algorithm.
     *
     * @return
     *         {@code this} object.
     *
     * @see <a href="https://www.iana.org/assignments/cose/cose.xhtml#algorithms"
     *      >IANA: COSE Algorithms</a>
     */
    public COSEKeyBuilder alg(String alg)
    {
        return put(COSEKeyCommonParameters.ALG, alg);
    }


    /**
     * Set the "alg (3)" parameter.
     *
     * @param alg
     *         The algorithm.
     *
     * @return
     *         {@code this} object.
     *
     * @see <a href="https://www.iana.org/assignments/cose/cose.xhtml#algorithms"
     *      >IANA: COSE Algorithms</a>
     */
    public COSEKeyBuilder alg(int alg)
    {
        return put(COSEKeyCommonParameters.ALG, alg);
    }


    /**
     * Set the "key_ops (4)" parameter.
     *
     * @param operations
     *         Key operations.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder keyOps(List<Object> operations)
    {
        return put(COSEKeyCommonParameters.KEY_OPS, operations);
    }


    /**
     * Set the "Base IV (5)" parameter.
     *
     * @param baseIv
     *         The base IV.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder baseIv(byte[] baseIv)
    {
        return put(COSEKeyCommonParameters.BASE_IV, baseIv);
    }


    /**
     * Set the "Base IV (5)" parameter.
     *
     * @param baseIv
     *         The base IV, expressed in the base64url format.
     *
     * @return
     *         {@code this} object.
     *
     * @since 1.5
     */
    public COSEKeyBuilder baseIvInBase64Url(String baseIv)
    {
        return baseIv(decodeBase64Url(baseIv));
    }


    /**
     * Set the "crv (-1)" parameter for the key type "OKP".
     *
     * @param crv
     *         The curve.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder okpCrv(int crv)
    {
        return put(COSEKeyTypeParameters.OKP_CRV, crv);
    }


    /**
     * Set the "crv (-1)" parameter for the key type "OKP".
     *
     * @param crv
     *         The curve.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder okpCrv(String crv)
    {
        return put(COSEKeyTypeParameters.OKP_CRV, crv);
    }


    /**
     * Set "X25519 (4)" to the "crv (-1)" parameter for the key type "OKP".
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder okpCrvX25519()
    {
        return okpCrv(COSEEllipticCurves.X25519);
    }


    /**
     * Set "X448 (5)" to the "crv (-1)" parameter for the key type "OKP".
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder okpCrvX448()
    {
        return okpCrv(COSEEllipticCurves.X448);
    }


    /**
     * Set "Ed25519 (6)" to the "crv (-1)" parameter for the key type "OKP".
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder okpCrvEd25519()
    {
        return okpCrv(COSEEllipticCurves.Ed25519);
    }


    /**
     * Set "Ed448 (7)" to the "crv (-1)" parameter for the key type "OKP".
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder okpCrvEd448()
    {
        return okpCrv(COSEEllipticCurves.Ed448);
    }


    /**
     * Set the "x (-2)" parameter for the key type "OKP".
     *
     * @param x
     *         The public key.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder okpX(byte[] x)
    {
        return put(COSEKeyTypeParameters.OKP_X, x);
    }


    /**
     * Set the "x (-2)" parameter for the key type "OKP".
     *
     * @param x
     *         The public key, expressed in the base64url format.
     *
     * @return
     *         {@code this} object.
     *
     * @since 1.5
     */
    public COSEKeyBuilder okpXInBase64Url(String x)
    {
        return okpX(decodeBase64Url(x));
    }


    /**
     * Set the "d (-4)" parameter for the key type "OKP".
     *
     * @param d
     *         The private key.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder okpD(byte[] d)
    {
        return put(COSEKeyTypeParameters.OKP_D, d);
    }


    /**
     * Set the "d (-4)" parameter for the key type "OKP".
     *
     * @param d
     *         The private key, expressed in the base64url format.
     *
     * @return
     *         {@code this} object.
     *
     * @since 1.5
     */
    public COSEKeyBuilder okpDInBase64Url(String d)
    {
        return okpD(decodeBase64Url(d));
    }


    /**
     * Set the "crv (-1)" parameter for the key type "EC2".
     *
     * @param crv
     *         The curve.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder ec2Crv(int crv)
    {
        return put(COSEKeyTypeParameters.EC2_CRV, crv);
    }


    /**
     * Set the "crv (-1)" parameter for the key type "EC2".
     *
     * @param crv
     *         The curve.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder ec2Crv(String crv)
    {
        return put(COSEKeyTypeParameters.EC2_CRV, crv);
    }


    /**
     * Set "P-256 (1)" to the "crv (-1)" parameter for the key type "EC2".
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder ec2CrvP256()
    {
        return ec2Crv(COSEEllipticCurves.P_256);
    }


    /**
     * Set "P-384 (2)" to the "crv (-1)" parameter for the key type "EC2".
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder ec2CrvP384()
    {
        return ec2Crv(COSEEllipticCurves.P_384);
    }


    /**
     * Set "P-521 (3)" to the "crv (-1)" parameter for the key type "EC2".
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder ec2CrvP521()
    {
        return ec2Crv(COSEEllipticCurves.P_521);
    }


    /**
     * Set "secp256k1 (8)" to the "crv (-1)" parameter for the key type "EC2".
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder ec2CrvSecp256k1()
    {
        return ec2Crv(COSEEllipticCurves.SECP256K1);
    }


    /**
     * Set the "x (-2)" parameter for the key type "EC2".
     *
     * @param x
     *         The x-coordinate.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder ec2X(byte[] x)
    {
        return put(COSEKeyTypeParameters.EC2_X, x);
    }


    /**
     * Set the "x (-2)" parameter for the key type "EC2".
     *
     * @param x
     *         The x-coordinate, expressed in the base64url format.
     *
     * @return
     *         {@code this} object.
     *
     * @since 1.5
     */
    public COSEKeyBuilder ec2XInBase64Url(String x)
    {
        return ec2X(decodeBase64Url(x));
    }


    /**
     * Set the "y (-3)" parameter for the key type "EC2".
     *
     * @param y
     *         The y-coordinate.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder ec2Y(byte[] y)
    {
        return put(COSEKeyTypeParameters.EC2_Y, y);
    }


    /**
     * Set the "y (-3)" parameter for the key type "EC2".
     *
     * @param y
     *         The y-coordinate, expressed in the base64url format.
     *
     * @return
     *         {@code this} object.
     *
     * @since 1.5
     */
    public COSEKeyBuilder ec2YInBase64Url(String y)
    {
        return ec2Y(decodeBase64Url(y));
    }


    /**
     * Set the "y (-3)" parameter for the key type "EC2".
     *
     * @param y
     *         The y-coordinate.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder ec2Y(boolean y)
    {
        return put(COSEKeyTypeParameters.EC2_Y, y);
    }


    /**
     * Set the "d (-4)" parameter for the key type "EC2".
     *
     * @param d
     *         The private key.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder ec2D(byte[] d)
    {
        return put(COSEKeyTypeParameters.EC2_D, d);
    }


    /**
     * Set the "d (-4)" parameter for the key type "EC2".
     *
     * @param d
     *         The private key, expressed in the base64url format.
     *
     * @return
     *         {@code this} object.
     *
     * @since 1.5
     */
    public COSEKeyBuilder ec2DInBase64Url(String d)
    {
        return ec2D(decodeBase64Url(d));
    }


    /**
     * Set the "n (-1)" parameter for the key type "RSA".
     *
     * @param n
     *         The RSA modulus n.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder rsaN(byte[] n)
    {
        return put(COSEKeyTypeParameters.RSA_N, n);
    }


    /**
     * Set the "n (-1)" parameter for the key type "RSA".
     *
     * @param n
     *         The RSA modulus n, expressed in the base64url format.
     *
     * @return
     *         {@code this} object.
     *
     * @since 1.5
     */
    public COSEKeyBuilder rsaNInBase64Url(String n)
    {
        return rsaN(decodeBase64Url(n));
    }


    /**
     * Set the "e (-2)" parameter for the key type "RSA".
     *
     * @param e
     *         The RSA public exponent e.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder rsaE(byte[] e)
    {
        return put(COSEKeyTypeParameters.RSA_E, e);
    }


    /**
     * Set the "e (-2)" parameter for the key type "RSA".
     *
     * @param e
     *         The RSA public exponent e, expressed in the base64url format.
     *
     * @return
     *         {@code this} object.
     *
     * @since 1.5
     */
    public COSEKeyBuilder rsaEInBase64Url(String e)
    {
        return rsaE(decodeBase64Url(e));
    }


    /**
     * Set the "d (-3)" parameter for the key type "RSA".
     *
     * @param d
     *         The RSA private exponent d.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder rsaD(byte[] d)
    {
        return put(COSEKeyTypeParameters.RSA_D, d);
    }


    /**
     * Set the "d (-3)" parameter for the key type "RSA".
     *
     * @param d
     *         The RSA private exponent d, expressed in the base64url format.
     *
     * @return
     *         {@code this} object.
     *
     * @since 1.5
     */
    public COSEKeyBuilder rsaDInBase64Url(String d)
    {
        return rsaD(decodeBase64Url(d));
    }


    /**
     * Set the "p (-4)" parameter for the key type "RSA".
     *
     * @param p
     *         The prime factor p of n.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder rsaP(byte[] p)
    {
        return put(COSEKeyTypeParameters.RSA_P, p);
    }


    /**
     * Set the "p (-4)" parameter for the key type "RSA".
     *
     * @param p
     *         The prime factor p of n, expressed in the base64url format.
     *
     * @return
     *         {@code this} object.
     *
     * @since 1.5
     */
    public COSEKeyBuilder rsaPInBase64Url(String p)
    {
        return rsaP(decodeBase64Url(p));
    }


    /**
     * Set the "q (-5)" parameter for the key type "RSA".
     *
     * @param q
     *         The prime factor q of n.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder rsaQ(byte[] q)
    {
        return put(COSEKeyTypeParameters.RSA_Q, q);
    }


    /**
     * Set the "q (-5)" parameter for the key type "RSA".
     *
     * @param q
     *         The prime factor q of n, expressed in the base64url format.
     *
     * @return
     *         {@code this} object.
     *
     * @since 1.5
     */
    public COSEKeyBuilder rsaQInBase64url(String q)
    {
        return rsaQ(decodeBase64Url(q));
    }


    /**
     * Set the "dP (-6)" parameter for the key type "RSA".
     *
     * @param dP
     *         d mod (p -1)
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder rsaDP(byte[] dP)
    {
        return put(COSEKeyTypeParameters.RSA_DP, dP);
    }


    /**
     * Set the "dP (-6)" parameter for the key type "RSA".
     *
     * @param dP
     *         d mod (p -1), expressed in the base64url format.
     *
     * @return
     *         {@code this} object.
     *
     * @since 1.5
     */
    public COSEKeyBuilder rsaDPInBase64Url(String dP)
    {
        return rsaDP(decodeBase64Url(dP));
    }


    /**
     * Set the "dQ (-7)" parameter for the key type "RSA".
     *
     * @param dQ
     *         d mod (q -1)
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder rsaDQ(byte[] dQ)
    {
        return put(COSEKeyTypeParameters.RSA_DQ, dQ);
    }


    /**
     * Set the "dQ (-7)" parameter for the key type "RSA".
     *
     * @param dQ
     *         d mod (q -1), expressed in the base64url format.
     *
     * @return
     *         {@code this} object.
     *
     * @since 1.5
     */
    public COSEKeyBuilder rsaDQInBase64Url(String dQ)
    {
        return rsaDQ(decodeBase64Url(dQ));
    }


    /**
     * Set the "qInv (-8)" parameter for the key type "RSA".
     *
     * @param qInv
     *         The CRT coefficient q^(-1) mod p.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder rsaQInv(byte[] qInv)
    {
        return put(COSEKeyTypeParameters.RSA_QINV, qInv);
    }


    /**
     * Set the "qInv (-8)" parameter for the key type "RSA".
     *
     * @param qInv
     *         The CRT coefficient q^(-1) mod p, expressed in the base64url format.
     *
     * @return
     *         {@code this} object.
     *
     * @since 1.5
     */
    public COSEKeyBuilder rsaQInvInBase64Url(String qInv)
    {
        return rsaQInv(decodeBase64Url(qInv));
    }


    /**
     * Set the "other (-9)" parameter for the key type "RSA".
     *
     * @param other
     *         Other prime infos, an array.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder rsaOther(List<Object> other)
    {
        return put(COSEKeyTypeParameters.RSA_OTHER, other);
    }


    /**
     * Set the "r_i (-10)" parameter for the key type "RSA".
     *
     * @param r_i
     *         A prime factor r_i of n, where i >= 3.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder rsaRI(byte[] r_i)
    {
        return put(COSEKeyTypeParameters.RSA_R_I, r_i);
    }


    /**
     * Set the "r_i (-10)" parameter for the key type "RSA".
     *
     * @param r_i
     *         A prime factor r_i of n, where i >= 3.
     *         Expressed in the base64url format.
     *
     * @return
     *         {@code this} object.
     *
     * @since 1.5
     */
    public COSEKeyBuilder rsaRIInBase64Url(String r_i)
    {
        return rsaRI(decodeBase64Url(r_i));
    }


    /**
     * Set the "d_i (-11)" parameter for the key type "RSA".
     *
     * @param d_i
     *         d mod (r_i - 1).
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder rsaDI(byte[] d_i)
    {
        return put(COSEKeyTypeParameters.RSA_D_I, d_i);
    }


    /**
     * Set the "d_i (-11)" parameter for the key type "RSA".
     *
     * @param d_i
     *         d mod (r_i - 1).
     *         Expressed in the base64url format.
     *
     * @return
     *         {@code this} object.
     *
     * @since 1.5
     */
    public COSEKeyBuilder rsaDIInBase64Url(String d_i)
    {
        return rsaDI(decodeBase64Url(d_i));
    }


    /**
     * Set the "t_i (-12)" parameter for the key type "RSA".
     *
     * @param t_i
     *         The CRT coefficient t_i = (r_1 * r_2 * ... * r_(i-1))^(-1) mod r_i.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder rsaTI(byte[] t_i)
    {
        return put(COSEKeyTypeParameters.RSA_T_I, t_i);
    }


    /**
     * Set the "t_i (-12)" parameter for the key type "RSA".
     *
     * @param t_i
     *         The CRT coefficient t_i = (r_1 * r_2 * ... * r_(i-1))^(-1) mod r_i.
     *         Expressed in the base64url format.
     *
     * @return
     *         {@code this} object.
     *
     * @since 1.5
     */
    public COSEKeyBuilder rsaTIInBase64Url(String t_i)
    {
        return rsaTI(decodeBase64Url(t_i));
    }


    /**
     * Set the "k (-1)" parameter for the key type "Symmetric".
     *
     * @param k
     *         The key value.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder symmetricK(byte[] k)
    {
        return put(COSEKeyTypeParameters.SYMMETRIC_K, k);
    }


    /**
     * Set the "k (-1)" parameter for the key type "Symmetric".
     *
     * @param k
     *         The key value, expressed in the base64url format.
     *
     * @return
     *         {@code this} object.
     *
     * @since 1.5
     */
    public COSEKeyBuilder symmetricKInBase64Url(String k)
    {
        return symmetricK(decodeBase64Url(k));
    }


    /**
     * Set the "pub (-1)" parameter for the key type "HSS-LMS".
     *
     * @param pub
     *         The public key.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder hssLmsPub(byte[] pub)
    {
        return put(COSEKeyTypeParameters.HSS_LMS_PUB, pub);
    }


    /**
     * Set the "pub (-1)" parameter for the key type "HSS-LMS".
     *
     * @param pub
     *         The public key, expressed in the base64url format.
     *
     * @return
     *         {@code this} object.
     *
     * @since 1.5
     */
    public COSEKeyBuilder hssLmsPubInBase64Url(String pub)
    {
        return hssLmsPub(decodeBase64Url(pub));
    }


    /**
     * Set the "N (-1)" parameter for the key type "WalnutDSA".
     *
     * @param N
     *         Group and Matrix (NxN) size.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder walnutDsaN(Number N)
    {
        return put(COSEKeyTypeParameters.WALNUT_DSA_N, N);
    }


    /**
     * Set the "q (-2)" parameter for the key type "WalnutDSA".
     *
     * @param q
     *         Finite field F_q.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder walnutDsaQ(Number q)
    {
        return put(COSEKeyTypeParameters.WALNUT_DSA_Q, q);
    }


    /**
     * Set the "t-values (-3)" parameter for the key type "WalnutDSA".
     *
     * @param tValues
     *         List of T-values, entries in F_q.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder walnutDsaTValues(List<Number> tValues)
    {
        return put(COSEKeyTypeParameters.WALNUT_DSA_T_VALUES, tValues);
    }


    /**
     * Set the "matrix 1 (-4)" parameter for the key type "WalnutDSA".
     *
     * @param matrix1
     *         NxN Matrix of entries in F_q in column-major form.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder walnutDsaMatrix1(List<List<Number>> matrix1)
    {
        return put(COSEKeyTypeParameters.WALNUT_DSA_MATRIX_1, matrix1);
    }


    /**
     * Set the "permutation 1 (-5)" parameter for the key type "WalnutDSA".
     *
     * @param permutation1
     *         Permutation associated with matrix 1.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder walnutDsaPermutation1(List<Number> permutation1)
    {
        return put(COSEKeyTypeParameters.WALNUT_DSA_PERMUTATION_1, permutation1);
    }


    /**
     * Set the "matrix 2 (-6)" parameter for the key type "WalnutDSA".
     *
     * @param matrix2
     *         NxN Matrix of entries in F_q in column-major form.
     *
     * @return
     *         {@code this} object.
     */
    public COSEKeyBuilder walnutDsaMatrix2(List<List<Number>> matrix2)
    {
        return put(COSEKeyTypeParameters.WALNUT_DSA_MATRIX_2, matrix2);
    }


    private List<? extends CBORPair> toPairs()
    {
        return ((CBORPairList)new CBORizer().cborizeMap(map)).getPairs();
    }


    /**
     * Build a {@link COSEOKPKey} instance.
     *
     * @return
     *         A {@link COSEOKPKey} instance.
     */
    public COSEOKPKey buildOKPKey()
    {
        return new COSEOKPKey(toPairs());
    }


    /**
     * Build a {@link COSEEC2Key} instance.
     *
     * @return
     *         A {@link COSEEC2Key} instance.
     */
    public COSEEC2Key buildEC2Key()
    {
        return new COSEEC2Key(toPairs());
    }


    /**
     * Build an instance of {@link COSEKey} or its subclass when possible
     * (e.g. {@link COSEOKPKey} and {@link COSEEC2Key}).
     *
     * @return
     *         {@code this} object.
     *
     * @throws COSEException
     */
    public COSEKey build() throws COSEException
    {
        return COSEKey.build(map);
    }
}

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


import java.math.BigInteger;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;


/**
 * ECDSA constants
 *
 * @since 1.1
 *
 * @see <a href="https://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.186-4.pdf"
 *      >Digital Signature Standard (DSS)</a>
 */
class ECDSAConstants
{
    //
    // Numbers for the following parameter specs are copied from jose4j's
    // EllipticCurves.java.
    //
    //   https://github.com/RbkGh/Jose4j/blob/master/src/main/java/org/jose4j/keys/EllipticCurves.java
    //


    /**
     * Parameter spec of P-256
     */
    static final ECParameterSpec PARAMETER_SPEC_P256 = new ECParameterSpec(
            new EllipticCurve(
                    new ECFieldFp(new BigInteger(
                            "11579208921035624876269744694940757353008614341529" +
                            "0314195533631308867097853951")),
                    new BigInteger(
                            "11579208921035624876269744694940757353008614341529" +
                            "0314195533631308867097853948"),
                    new BigInteger(
                            "41058363725152142129326129780047268409114441015993" +
                            "725554835256314039467401291")
            ),
            new ECPoint(
                    new BigInteger(
                            "48439561293906451759052585252797914202762949526041" +
                            "747995844080717082404635286"),
                    new BigInteger(
                            "36134250956749795798585127919587881956611106672985" +
                            "015071877198253568414405109")
            ),
            new BigInteger(
                            "11579208921035624876269744694940757352999695522413" +
                            "5760342422259061068512044369"),
            1
    );


    /**
     * Parameter spec of P-384
     */
    static final ECParameterSpec PARAMETER_SPEC_P384 = new ECParameterSpec(
            new EllipticCurve(
                    new ECFieldFp(new BigInteger(
                            "39402006196394479212279040100143613805079739270465" +
                            "44666794829340424572177149687032904726608825893800" +
                            "1861606973112319")),
                    new BigInteger(
                            "39402006196394479212279040100143613805079739270465" +
                            "44666794829340424572177149687032904726608825893800" +
                            "1861606973112316"),
                    new BigInteger(
                            "27580193559959705877849011840389048093056905856361" +
                            "56852142870730198868924130986086513626076488374510" +
                            "7765439761230575")
            ),
            new ECPoint(
                    new BigInteger(
                            "26247035095799689268623156744566981891852923491109" +
                            "21338781561590092551885473805008902238805397571978" +
                            "6650872476732087"),
                    new BigInteger(
                            "83257109614890299855467512895201081792878530488613" +
                            "15594709205902480503199884419224438643760392947333" +
                            "078086511627871")
            ),
            new BigInteger(
                            "39402006196394479212279040100143613805079739270465" +
                            "44666794690527962765939911326356939895630815229491" +
                            "3554433653942643"),
            1
    );


    /**
     * Parameter spec of P-521
     */
    static final ECParameterSpec PARAMETER_SPEC_P521 = new ECParameterSpec(
            new EllipticCurve(
                    new ECFieldFp(new BigInteger(
                            "68647976601306097149819007990813932172694353001433" +
                            "05409394463459185543183397656052122559640661454554" +
                            "97729631139148085803712198799971664381257402829111" +
                            "5057151")),
                    new BigInteger(
                            "68647976601306097149819007990813932172694353001433" +
                            "05409394463459185543183397656052122559640661454554" +
                            "97729631139148085803712198799971664381257402829111" +
                            "5057148"),
                    new BigInteger(
                            "10938490380737342745111123907668055699362075989516" +
                            "83748994586394495953116150735016013708737573759623" +
                            "24859213229670631330943845253159101291214232748847" +
                            "8985984")
            ),
            new ECPoint(
                    new BigInteger(
                            "26617408020502170632287687167233609607298591687569" +
                            "73147706671368418802944996427808491545080627771902" +
                            "35209424122506555866215711354557091681416163731589" +
                            "5999846"),
                    new BigInteger(
                            "37571800257700204635455072244911836035944551347697" +
                            "62486694567779615544477440556316691234405012945539" +
                            "56214444453728942852258566672919658081012434427757" +
                            "8376784")
            ),
            new BigInteger(
                            "68647976601306097149819007990813932172694353001433" +
                            "05409394463459185543183397655394245057746333217197" +
                            "53296399637136332111386476861244038034037280889270" +
                            "7005449"),
            1
    );
}

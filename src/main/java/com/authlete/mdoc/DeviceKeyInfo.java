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


import java.util.List;
import com.authlete.cbor.CBORPair;
import com.authlete.cbor.CBORPairList;
import com.authlete.cbor.CBORPairsBuilder;
import com.authlete.cbor.CBORString;
import com.authlete.cose.COSEKey;


/**
 * The {@code DeviceKeyInfo} structure.
 *
 * <p>
 * For details, see ISO/IEC 18013-5:2021, 9.1.2.4 Signing method and structure for MSO.
 * </p>
 *
 * <h3>Definition</h3>
 *
 * <pre>
 * DeviceKeyInfo = {
 *     "deviceKey" : DeviceKey,
 *     ? "keyAuthorizations" : {@link KeyAuthorizations},
 *     ? "keyInfo" : {@link KeyInfo}
 * }
 *
 * DeviceKey = COSE_Key
 * </pre>
 *
 * @since 1.5
 *
 * @see <a href="https://www.iso.org/standard/69084.html">ISO/IEC 18013-5:2021</a>
 *
 * @see MobileSecurityObject
 */
public class DeviceKeyInfo extends CBORPairList
{
    private static final CBORString LABEL_DEVICE_KEY         = new CBORString("deviceKey");
    private static final CBORString LABEL_KEY_AUTHORIZATIONS = new CBORString("keyAuthorizations");
    private static final CBORString LABEL_KEY_INFO           = new CBORString("keyInfo");


    public DeviceKeyInfo(
            COSEKey deviceKey, KeyAuthorizations keyAuthorizations, KeyInfo keyInfo)
    {
        super(createList(deviceKey, keyAuthorizations, keyInfo));
    }


    private static List<CBORPair> createList(
            COSEKey deviceKey, KeyAuthorizations keyAuthorizations, KeyInfo keyInfo)
    {
        return new CBORPairsBuilder()
            .addUnlessNull(LABEL_DEVICE_KEY,         deviceKey)
            .addUnlessNull(LABEL_KEY_AUTHORIZATIONS, keyAuthorizations)
            .addUnlessNull(LABEL_KEY_INFO,           keyInfo)
            .build();
    }
}

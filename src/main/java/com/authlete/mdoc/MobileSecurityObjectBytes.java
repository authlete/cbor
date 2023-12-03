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


import com.authlete.cbor.CBORByteArray;
import com.authlete.cbor.CBORTaggedItem;


/**
 * The {@code MobileSecurityObjectBytes} structure, which is the payload
 * of the {@code IssuerAuth} structure.
 *
 * <p>
 * For details, see ISO/IEC 18013-5:2021, 9.1.2.4 Signing method and structure for MSO.
 * </p>
 *
 * <h3>Definition</h3>
 *
 * <pre>
 * MobileSecurityObjectBytes = #6.24(bstr .cbor {@link MobileSecurityObject})
 * </pre>
 *
 * @since 1.5
 *
 * @see <a href="https://www.iso.org/standard/69084.html">ISO/IEC 18013-5:2021</a>
 */
public class MobileSecurityObjectBytes extends CBORTaggedItem
{
    private final MobileSecurityObject mMobileSecurityObject;


    public MobileSecurityObjectBytes(MobileSecurityObject mso)
    {
        super(24, new CBORByteArray(mso.encode(), mso));

        mMobileSecurityObject = mso;
    }


    public MobileSecurityObject getMobileSecurityObject()
    {
        return mMobileSecurityObject;
    }
}

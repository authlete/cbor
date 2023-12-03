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


import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORPair;
import com.authlete.cbor.CBORPairList;
import com.authlete.cbor.CBORPairsBuilder;
import com.authlete.cbor.CBORString;
import com.authlete.cbor.CBORTaggedItem;


/**
 * The {@code ValidityInfo} structure containing information related to the
 * validity of the Mobile Security Object and its signature.
 *
 * <p>
 * For details, see ISO/IEC 18013-5:2021, 9.1.2.4 Signing method and structure for MSO.
 * </p>
 *
 * <h3>Definition</h3>
 *
 * <pre>
 * ValidityInfo = {
 *     "signed" : tdate,
 *     "validFrom" : tdate,
 *     "validUntil": tdate,
 *     ? "expectedUpdate" : tdate
 * }
 * </pre>
 *
 * @since 1.5
 *
 * @see <a href="https://www.iso.org/standard/69084.html">ISO/IEC 18013-5:2021</a>
 *
 * @see MobileSecurityObject
 */
public class ValidityInfo extends CBORPairList
{
    private static final CBORString LABEL_SIGNED          = new CBORString("signed");
    private static final CBORString LABEL_VALID_FROM      = new CBORString("validFrom");
    private static final CBORString LABEL_VALID_UNTIL     = new CBORString("validUntil");
    private static final CBORString LABEL_EXPECTED_UPDATE = new CBORString("expectedUpdate");


    public ValidityInfo(
            ZonedDateTime signed, ZonedDateTime validFrom, ZonedDateTime validUntil)
    {
        this(signed, validFrom, validUntil, /* expectedUpdate */ null);
    }


    public ValidityInfo(
            ZonedDateTime signed, ZonedDateTime validFrom,
            ZonedDateTime validUntil, ZonedDateTime expectedUpdate)
    {
        super(createList(signed, validFrom, validUntil, expectedUpdate));
    }


    private static List<CBORPair> createList(
            ZonedDateTime signed, ZonedDateTime validFrom,
            ZonedDateTime validUntil, ZonedDateTime expectedUpdate)
    {
        return new CBORPairsBuilder()
                .addUnlessNull(LABEL_SIGNED,          toValue(signed))
                .addUnlessNull(LABEL_VALID_FROM,      toValue(validFrom))
                .addUnlessNull(LABEL_VALID_UNTIL,     toValue(validUntil))
                .addUnlessNull(LABEL_EXPECTED_UPDATE, toValue(expectedUpdate))
                .build();
    }


    private static CBORItem toValue(ZonedDateTime dateTime)
    {
        if (dateTime == null)
        {
            return null;
        }

        // RFC 8949, 3.4.1. Standard Date/Time String
        //
        //   Tag number 0 contains a text string in the standard format
        //   described by the date-time production in [RFC3339], as
        //   refined by Section 3.3 of [RFC4287], representing the point
        //   in time described there. A nested item of another type or a
        //   text string that doesn't match the format described in
        //   [RFC4287] is invalid.

        return new CBORTaggedItem(0, buildString(dateTime));
    }


    private static CBORString buildString(ZonedDateTime dateTime)
    {
        // ISO/IEC 18013-5:2021, 9.1.2.4 Signing method and structure for MSO
        //
        //   The timestamps in the ValidityInfo structure shall not use
        //   fractions of seconds and shall use a UTC offset of 00:00,
        //   as indicated by the character “Z”.

        // Make sure that the time zone is UTC and that the field for
        // nanoseconds is not included.
        ZonedDateTime dt = dateTime.withZoneSameInstant(ZoneOffset.UTC).withNano(0);

        return new CBORString(dt.format(DateTimeFormatter.ISO_INSTANT));
    }
}

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


import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.authlete.cbor.CBORItemList;
import com.authlete.cbor.CBORString;


/**
 * A list of data element identifiers.
 *
 * <p>
 * For details, see ISO/IEC 18013-5:2021, 9.1.2.4 Signing method and structure for MSO.
 * </p>
 *
 * <h3>Definition</h3>
 *
 * <pre>
 * DataElementsArray = [ + DataElementIdentifier ]
 *
 * DataElementIdentifier = tstr
 * </pre>
 *
 * @since 1.5
 *
 * @see <a href="https://www.iso.org/standard/69084.html">ISO/IEC 18013-5:2021</a>
 *
 * @see AuthorizedDataElements
 */
public class DataElementsArray extends CBORItemList
{
    public DataElementsArray(List<CBORString> dataElementsIdentifiers)
    {
        super(dataElementsIdentifiers);
    }


    public DataElementsArray(Collection<String> dataElementIdentifiers)
    {
        super(createList(dataElementIdentifiers.stream()));
    }


    public DataElementsArray(String[] dataElementIdentifiers)
    {
        super(createList(Arrays.stream(dataElementIdentifiers)));
    }


    private static List<CBORString> createList(Stream<String> stream)
    {
        return stream
                .map(string -> new CBORString(string))
                .collect(Collectors.toList());
    }
}

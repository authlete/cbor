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


import java.util.Map;


/**
 * A utility to build an instance of {@link COSEProtectedHeader}.
 *
 * @since 1.1
 *
 * @see COSEProtectedHeader
 */
public class COSEProtectedHeaderBuilder
extends COSEHeaderBuilder<COSEProtectedHeaderBuilder, COSEProtectedHeader>
{
    @Override
    protected COSEProtectedHeader build(Map<Object, Object> map)
    {
        return COSEProtectedHeader.build(map);
    }
}

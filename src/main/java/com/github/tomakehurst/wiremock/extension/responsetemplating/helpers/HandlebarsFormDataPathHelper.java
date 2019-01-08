/*
 * Copyright (C) 2011 Thomas Akehurst
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.tomakehurst.wiremock.extension.responsetemplating.helpers;

import com.github.jknack.handlebars.Options;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HandlebarsFormDataPathHelper extends HandlebarsHelper<String> {

    @Override
    public Object apply(final String inputFormData, final Options options) throws IOException {
        if (inputFormData == null) {
            return "";
        }

        if (options == null || options.param(0, null) == null) {
            return this.handleError("The form data path cannot be empty");
        }

        final String formDataPath = options.param(0);
        Map formData = convertBodyToFormData(inputFormData);

        System.out.println("inputFormData=" + inputFormData);
        System.out.println("formDataPath=" + formDataPath);
        System.out.println("formData=" + formData);

        return getValueFromRequestObject(formDataPath, formData);
    }

    public static Map convertBodyToFormData(String body) {
        // Trying to create map of request body or query string parameters
        Map object = new HashMap();
        String[] pairedValues = body.split("&");
        for (String pair : pairedValues) {
            String[] values = pair.split("=");
            object.put(values[0], values.length > 1 ? decodeUTF8Value(values[1]) : "");
        }

        return object;
    }

    public static String decodeUTF8Value(String value) {

        String decodedValue = "";
        try {
            decodedValue = URLDecoder.decode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            System.err.println("[Body parse error] Can't decode one of the request parameter. It should be UTF-8 charset.");
        }

        return decodedValue;
    }

    public static CharSequence getValueFromRequestObject(String path, Map requestObject) {
        String fieldName = path;
        String[] fieldNames = fieldName.split("\\.");
        Object tempObject = requestObject;
        for (String field : fieldNames) {
            if (tempObject instanceof Map) {
                tempObject = ((Map) tempObject).get(field);
            }
        }
        return String.valueOf(tempObject);
    }
}

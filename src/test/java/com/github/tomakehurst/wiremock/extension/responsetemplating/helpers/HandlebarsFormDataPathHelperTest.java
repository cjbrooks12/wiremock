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

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.common.LocalNotifier;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import org.junit.Before;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.matching.MockRequest.mockRequest;
import static com.github.tomakehurst.wiremock.testsupport.NoFileSource.noFileSource;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class HandlebarsFormDataPathHelperTest extends HandlebarsHelperTestBase {

    private HandlebarsFormDataPathHelper helper;
    private ResponseTemplateTransformer transformer;

    @Before
    public void init() {
        helper = new HandlebarsFormDataPathHelper();
        transformer = new ResponseTemplateTransformer(true);

        LocalNotifier.set(new ConsoleNotifier(true));
    }

    @Test
    public void mergesASimpleValueFromRequestIntoResponseBody() {
        final ResponseDefinition responseDefinition = this.transformer.transform(
                mockRequest()
                        .url("/json").
                        body("test=success"),
                aResponse()
                        .withBody("{\"test\": \"{{formData request.body 'test'}}\"}").build(),
                noFileSource(),
                Parameters.empty());

        assertThat(responseDefinition.getBody(), is("{\"test\": \"success\"}"));
    }

    @Test
    public void mergesAComplexValueFromRequestIntoResponseBody() {
        final ResponseDefinition responseDefinition = this.transformer.transform(
                mockRequest()
                        .url("/json").
                        body("test=success%20and%20more"),
                aResponse()
                        .withBody("{\"test\": \"{{formData request.body 'test'}}\"}").build(),
                noFileSource(),
                Parameters.empty());

        assertThat(responseDefinition.getBody(), is("{\"test\": \"success and more\"}"));
    }

    @Test
    public void mergesAReallyComplexValueFromRequestIntoResponseBody() {
        final ResponseDefinition responseDefinition = this.transformer.transform(
                mockRequest()
                        .url("/json").
                        body("test=success%20and%20more%0Aand%20more%20and%20more"),
                aResponse()
                        .withBody("{\"test\": \"{{formData request.body 'test'}}\"}").build(),
                noFileSource(),
                Parameters.empty());

        assertThat(responseDefinition.getBody(), is("{\"test\": \"success and more\nand more and more\"}"));
    }
}

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
package com.github.tomakehurst.wiremock.matching;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.Json;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.testsupport.WireMatchers.equalToJson;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class MatchesFormDataPatternTest {

    private Mockery context;

    @Before
    public void init() {
        context = new Mockery();
    }

// Key only
//----------------------------------------------------------------------------------------------------------------------

    @Test
    public void matchesBasedOnKeyOnly() {
        StringValuePattern pattern = WireMock.matchingFormDataPattern("test", null);
        assertTrue("Expected match when key exists in form data", pattern.match("test=success").isExactMatch());
    }

    @Test
    public void doesNotMatchBasedOnKeyOnly() {
        StringValuePattern pattern = WireMock.matchingFormDataPattern("test", null);
        assertFalse("Expected no match when key does not exist in form data", pattern.match("other=failure").isExactMatch());
    }

    @Test
    public void correctlySerialisesWithKeyOnly() {
        assertThat(Json.write(WireMock.matchingFormDataPattern("test", null)), equalToJson(
            "{                                 \n" +
            "    \"matchesFormData\": \"test\" \n" +
            "}"
        ));
    }

    @Test
    public void correctlyDeserialisesWithKeyOnly() {
        StringValuePattern stringValuePattern = Json.read(
                "{                                         \n" +
                        "  \"matchesFormData\": \"test\"       \n" +
                        "}",
                StringValuePattern.class);

        assertThat(stringValuePattern, instanceOf(MatchesFormDataPattern.class));
        assertThat(stringValuePattern.getExpected(), is("test"));

        MatchesFormDataPattern formDataPattern = (MatchesFormDataPattern) stringValuePattern;
        assertThat(formDataPattern.getExpectedKey(), is("test"));
        assertNull(formDataPattern.getExpectedValue());
    }

// Key and simple value
//----------------------------------------------------------------------------------------------------------------------

    @Test
    public void matchesBasedOnKeyAndSimpleValue() {
        StringValuePattern pattern = WireMock.matchingFormDataPattern("test", "success");
        assertTrue("Expected match when key exists in form data and value matches", pattern.match("test=success").isExactMatch());
    }

    @Test
    public void doesNotMatchBasedOnKeyAndSimpleValueWhenKeyIsNotPresent() {
        StringValuePattern pattern = WireMock.matchingFormDataPattern("test", "success");
        assertFalse("Expected no match when key does not exist in form data", pattern.match("other=failure").isExactMatch());
    }

    @Test
    public void doesNotMatchBasedOnKeyAndSimpleValueWhenKeyIsNotExpectedValue() {
        StringValuePattern pattern = WireMock.matchingFormDataPattern("test", "success");
        assertFalse("Expected no match when key exists in form data but value does not match", pattern.match("test=failure").isExactMatch());
    }

    @Test
    public void correctlySerialisesWithKeyAndSimpleValue() {
        assertThat(Json.write(WireMock.matchingFormDataPattern("test", "success")), equalToJson(
                "{                                  \n" +
                "  \"matchesFormData\": \"test\",   \n" +
                "  \"value\":           \"success\" \n" +
                "}"
        ));
    }

    @Test
    public void correctlyDeserialisesWithKeyAndSimpleValue() {
        StringValuePattern stringValuePattern = Json.read(
                "{                                  \n" +
                "  \"matchesFormData\": \"test\",   \n" +
                "  \"value\":           \"success\" \n" +
                "}",
        StringValuePattern.class);

        assertThat(stringValuePattern, instanceOf(MatchesFormDataPattern.class));
        assertThat(stringValuePattern.getExpected(), is("test"));

        MatchesFormDataPattern formDataPattern = (MatchesFormDataPattern) stringValuePattern;
        assertThat(formDataPattern.getExpectedKey(), is("test"));
        assertThat(formDataPattern.getExpectedValue(), is("success"));
    }

// Key and regex value
//----------------------------------------------------------------------------------------------------------------------

    @Test
    public void matchesBasedOnKeyAndRegexValue() {
        StringValuePattern pattern = WireMock.matchingFormDataPattern("test", "is_.*");
        assertTrue("Expected match when key exists in form data and regex value matches", pattern.match("test=is_success").isExactMatch());
    }

    @Test
    public void doesNotMatchBasedOnKeyAndRegexValueWhenKeyIsNotPresent() {
        StringValuePattern pattern = WireMock.matchingFormDataPattern("test", "is_.*");
        assertFalse("Expected no match when key does not exist in form data", pattern.match("other=failure").isExactMatch());
    }

    @Test
    public void doesNotMatchBasedOnKeyAndRegexValueWhenKeyIsNotExpectedValue() {
        StringValuePattern pattern = WireMock.matchingFormDataPattern("test", "is_.*");
        assertFalse("Expected no match when key exists in form data but value does not match regex", pattern.match("test=failure").isExactMatch());
    }

    @Test
    public void correctlySerialisesWithKeyAndRegexValue() {
        assertThat(Json.write(WireMock.matchingFormDataPattern("test", "is_.*")), equalToJson(
                "{                                  \n" +
                        "  \"matchesFormData\": \"test\",   \n" +
                        "  \"value\":           \"is_.*\" \n" +
                        "}"
        ));
    }

    @Test
    public void correctlyDeserialisesWithKeyAndRegexValue() {
        StringValuePattern stringValuePattern = Json.read(
                "{                                  \n" +
                        "  \"matchesFormData\": \"test\",   \n" +
                        "  \"value\":           \"is_.*\" \n" +
                        "}",
                StringValuePattern.class);

        assertThat(stringValuePattern, instanceOf(MatchesFormDataPattern.class));
        assertThat(stringValuePattern.getExpected(), is("test"));

        MatchesFormDataPattern formDataPattern = (MatchesFormDataPattern) stringValuePattern;
        assertThat(formDataPattern.getExpectedKey(), is("test"));
        assertThat(formDataPattern.getExpectedValue(), is("is_.*"));
    }

}

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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.tomakehurst.wiremock.extension.responsetemplating.helpers.HandlebarsFormDataPathHelper;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@JsonSerialize(using = FormDataPatternJsonSerializer.class)
public class MatchesFormDataPattern extends StringValuePattern {

    private final String expectedKey;
    private final String expectedValue;
    protected final Pattern pattern;

    public MatchesFormDataPattern(@JsonProperty("matchesFormData") String key, @JsonProperty("value") String value) {
        super(key);
        expectedKey = key;
        expectedValue = value;
        if(expectedValue != null) {
            pattern = Pattern.compile(expectedValue);
        }
        else {
            pattern = null;
        }
    }

    @Override
    public MatchResult match(String value) {
        if(value == null) return MatchResult.noMatch();

        Map formData = HandlebarsFormDataPathHelper.convertBodyToFormData(value);
        if(expectedKey != null && expectedValue != null) {
            return MatchResult.of(formData.containsKey(expectedKey) && formData.get(expectedKey) != null && pattern.matcher(formData.get(expectedKey).toString()).matches());
        }
        else if(expectedKey != null) {
            return MatchResult.of(formData.containsKey(expectedKey));
        }
        else {
            return MatchResult.noMatch();
        }
    }

    public String getExpectedKey() {
        return expectedKey;
    }

    public String getExpectedValue() {
        return expectedValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MatchesFormDataPattern that = (MatchesFormDataPattern) o;
        return getExpectedKey().equals(that.getExpectedKey()) &&
                Objects.equals(getExpectedValue(), that.getExpectedValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getExpectedKey(), getExpectedValue());
    }
}

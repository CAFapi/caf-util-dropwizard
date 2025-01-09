/*
 * Copyright 2024-2025 Open Text.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.cafapi.util.dropwizard;

import com.github.cafapi.common.util.secret.SecretUtil;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookup;
import org.apache.commons.text.lookup.StringLookupFactory;

/**
 * A custom implementation of StringSubstitutor that provides additional functionality for variable substitution, including secret lookup.
 * <p>
 * This class extends StringSubstitutor and configures it with a custom StringLookup that combines secret lookup and environment variable
 * lookup capabilities.
 */
public final class CafConfigSubstitutor extends StringSubstitutor
{
    /**
     * Constructs a new CafConfigSubstitutor with specified behavior for strict mode and substitution in variables.
     *
     * @param strict Determines whether undefined variables should throw an exception. If true, an exception will be thrown for undefined
     * variables.
     * @param substitutionInVariables Determines whether substitution is allowed in variable names. If true, variables can contain other
     * variables.
     */
    public CafConfigSubstitutor(final boolean strict, final boolean substitutionInVariables)
    {
        super(createStringLookup());
        this.setEnableUndefinedVariableException(strict);
        this.setEnableSubstitutionInVariables(substitutionInVariables);
    }

    private static StringLookup createStringLookup()
    {
        final Map<String, StringLookup> stringLookupMap = new HashMap<>();

        final StringLookup secretLookup = key -> {
            try {
                return SecretUtil.getSecret(key);
            } catch (final IOException e) {
                throw new UncheckedIOException("Unable to read secret: " + key, e);
            }
        };
        stringLookupMap.put("secret", secretLookup);

        final StringLookup defaultStringLookup = StringLookupFactory.INSTANCE.environmentVariableStringLookup();

        return StringLookupFactory.INSTANCE.interpolatorStringLookup(stringLookupMap, defaultStringLookup, false);
    }
}

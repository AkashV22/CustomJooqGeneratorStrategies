/*
 * Copyright 2019 AkashV22
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.akashv22.jooq.customjooqgeneratorstrategies;

import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.meta.Definition;

public class TableSuffixGeneratorStrategy extends DefaultGeneratorStrategy {
    @Override
    public String getJavaClassName(final Definition definition, final Mode mode) {
        final String javaClassName = super.getJavaClassName(definition, mode);

        if (mode == Mode.RECORD) {
            return javaClassName.substring(0, javaClassName.length() - "Record".length()) + "TableRecord";
        }

        return javaClassName + "Table";
    }
}

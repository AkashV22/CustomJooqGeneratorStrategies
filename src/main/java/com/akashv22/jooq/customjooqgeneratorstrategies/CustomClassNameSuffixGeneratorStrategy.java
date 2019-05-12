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

import java.util.Optional;
import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.meta.Definition;

public abstract class CustomClassNameSuffixGeneratorStrategy extends DefaultGeneratorStrategy {
    private final String tableClassSuffix;
    private final String recordClassSuffix;
    private final String pojoClassSuffix;
    private final String interfaceSuffix;
    private final String enumSuffix;
    private final String domainClassSuffix;
    private final String daoClassSuffix;

    @SuppressWarnings("WeakerAccess")
    public CustomClassNameSuffixGeneratorStrategy(
            final String tableClassSuffix
            , final String recordClassSuffix
            , final String pojoClassSuffix
            , final String interfaceSuffix
            , final String enumSuffix
            , final String domainClassSuffix
            , final String daoClassSuffix
    ) {
        this.tableClassSuffix = requireNonNullParam("tableClassSuffix", tableClassSuffix);
        this.recordClassSuffix = requireNonNullParam("recordClassSuffix", recordClassSuffix);
        this.pojoClassSuffix = requireNonNullParam("pojoClassSuffix", pojoClassSuffix);
        this.interfaceSuffix = requireNonNullParam("interfaceSuffix", interfaceSuffix);
        this.enumSuffix = requireNonNullParam("enumSuffix", enumSuffix);
        this.domainClassSuffix = requireNonNullParam("enumSuffix", domainClassSuffix);
        this.daoClassSuffix = requireNonNullParam("daoClassSuffix", daoClassSuffix);
    }

    @Override
    public final String getJavaClassName(final Definition definition, final Mode mode) {
        final String javaClassName = super.getJavaClassName(definition, mode);

        switch (mode) {
            case DEFAULT:
                return javaClassName + tableClassSuffix;
            case RECORD:
                return replaceSuffix(javaClassName, "Record", recordClassSuffix);
            case POJO:
                return javaClassName + pojoClassSuffix;
            case INTERFACE:
                return javaClassName + interfaceSuffix;
            case ENUM:
                return javaClassName + enumSuffix;
            case DAO:
                return replaceSuffix(javaClassName, "Dao", daoClassSuffix);
            case DOMAIN:
                return javaClassName + domainClassSuffix;
            default:
                return javaClassName;
        }
    }

    private static String requireNonNullParam(final String paramName, final String paramValue) {
        return Optional
                .ofNullable(paramValue)
                .orElseThrow(() -> new IllegalArgumentException(paramName + " cannot be null."))
                ;
    }

    private String replaceSuffix(final String javaClassName, final String originalSuffix, final String newSuffix) {
        return javaClassName.substring(0, javaClassName.length() - originalSuffix.length()) + newSuffix;
    }
}

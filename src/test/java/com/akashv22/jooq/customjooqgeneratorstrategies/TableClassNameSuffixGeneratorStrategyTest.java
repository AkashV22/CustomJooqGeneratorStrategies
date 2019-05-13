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

import org.h2.Driver;
import org.jooq.codegen.GenerationTool;
import org.jooq.codegen.JavaGenerator;
import org.jooq.meta.h2.H2Database;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Database;
import org.jooq.meta.jaxb.Generate;
import org.jooq.meta.jaxb.Generator;
import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.jaxb.Strategy;
import org.jooq.meta.jaxb.Target;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Disabled // TODO Remove @Disabled.
public class TableClassNameSuffixGeneratorStrategyTest {
    private static String projectBuildDir;

    @BeforeAll
    public static void setUpBeforeAll() throws IOException {
        try (InputStream inputStream =
                     TableClassNameSuffixGeneratorStrategyTest.class.getResourceAsStream("test.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);
            projectBuildDir = properties.getProperty("project.build.directory");
        }
    }

    @Test
    public void testGeneratorStrategy() throws Exception {
        Jdbc jdbc = new Jdbc()
                .withDriver(Driver.class.getName())
                .withUrl("")
                .withUsername("sa")
                .withPassword("")
                ;

        Strategy strategy = new Strategy()
                .withName(TableClassNameSuffixGeneratorStrategy.class.getName())
                ;

        Database database = new Database()
                .withName(H2Database.class.getName())
                .withIncludes(".*")
                .withInputSchema("PUBLIC")
                .withOutputSchemaToDefault(true)
                ;

        Generate generate = new Generate()
                .withPojos(true)
                .withInterfaces(true)
                .withDaos(true)
                ;

        Target target = new Target()
                .withPackageName("com.akashv22.jooq.customjooqgeneratorstrategies.test.generated")
                .withDirectory(projectBuildDir + "/tmp/jooq")
                ;

        Generator generator = new Generator()
                .withName(JavaGenerator.class.getName())
                .withStrategy(strategy)
                .withDatabase(database)
                .withGenerate(generate)
                .withTarget(target)
                ;

        Configuration configuration = new Configuration()
                .withJdbc(jdbc)
                .withGenerator(generator)
                ;

        GenerationTool.generate(configuration);
    }
}

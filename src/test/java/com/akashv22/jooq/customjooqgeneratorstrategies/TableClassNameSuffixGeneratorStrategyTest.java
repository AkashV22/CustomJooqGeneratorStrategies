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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Properties;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled // TODO Remove @Disabled.
public class TableClassNameSuffixGeneratorStrategyTest {
    private static final String JDBC_USERNAME = "sa";
    private static final String JDBC_PASSWORD = "";

    private static String projectBuildDir;
    private static String jdbcUrl;

    @BeforeAll
    public static void setUpBeforeAll() throws IOException, SQLException, LiquibaseException {
        try (InputStream inputStream =
                     TableClassNameSuffixGeneratorStrategyTest.class.getResourceAsStream("/test.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);
            projectBuildDir = properties.getProperty("project.build.directory");
            jdbcUrl = "jdbc:h2:" + projectBuildDir
                    + "/tmp/db/db;MODE=MYSQL;DATABASE_TO_UPPER=false;DB_CLOSE_ON_EXIT=false";
        }

        initDatabase();
    }

    /**
     * <p>Tear down after running all tests.</p>
     * <p>Logic to recursively remove files taken from
     * <a href="https://stackoverflow.com/a/35989142">this StackOverflow answer</a> by
     * <a href="https://stackoverflow.com/users/2333222/suboptimal">StackOverflow user SubOptimal</a>.</p>
     * @throws IOException if an error occurred during tear down.
     */
    @AfterAll
    public static void tearDownAfterAll() throws IOException {
        Path rootPath = Paths.get(projectBuildDir + "/tmp");
        Files.walk(rootPath)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(file -> {
                    try {
                        deleteFile(file);
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                })
        ;
    }

    @Test
    public void testGeneratorStrategy() throws Exception {


        Jdbc jdbc = new Jdbc()
                .withDriver(Driver.class.getName())
                .withUrl(jdbcUrl)
                .withUsername(JDBC_USERNAME)
                .withPassword(JDBC_PASSWORD)
                ;

        Strategy strategy = new Strategy()
                .withName(TableClassNameSuffixGeneratorStrategy.class.getName())
                ;

        Database database = new Database()
                .withName(H2Database.class.getName())
                .withIncludes(".*")
                .withExcludes("DATABASECHANGELOG|DATABASECHANGELOGLOCK")
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

    private static void initDatabase() throws SQLException, LiquibaseException {
        try (Connection conn = DriverManager.getConnection(jdbcUrl, JDBC_USERNAME, JDBC_PASSWORD)) {
            liquibase.database.Database database = DatabaseFactory
                    .getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(conn));

            Liquibase liquibase = new Liquibase(
                    "db.changelog/db.changelog-master.yaml"
                    , new ClassLoaderResourceAccessor()
                    , database
            );

            liquibase.update(new Contexts());
        }
    }

    private static void deleteFile(File file) throws IOException {
        final String fileOrDir;
        final String fileOrDirAtStartOfSentence;
        if (file.isDirectory()) {
            fileOrDir = "directory";
            fileOrDirAtStartOfSentence = "Directory";
        } else {
            fileOrDir = "file";
            fileOrDirAtStartOfSentence = "File";
        }

        System.out.println("Currently deleting " + fileOrDir + ": " + file); //TODO Replace with SLF4J.

        if (file.delete()) {
            System.out.println(fileOrDirAtStartOfSentence + " deleted successfully: " + file);
        } else {
            throw new IOException(fileOrDirAtStartOfSentence + " not deleted: " + file);
        }
    }
}

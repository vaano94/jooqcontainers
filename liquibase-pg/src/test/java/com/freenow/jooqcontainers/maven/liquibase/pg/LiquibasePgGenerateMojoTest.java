package com.freenow.jooqcontainers.maven.liquibase.pg;

import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class LiquibasePgGenerateMojoTest
{
    @Rule
    public MojoRule rule = new MojoRule();

    @After
    public void tearDown() throws Exception
    {
        FileUtils.deleteDirectory(new File("target/generated-sources/jooq"));
    }

    @Test
    public void runPluginPostgres() throws Exception
    {
        // GIVEN
        LiquibasePgGenerateMojo container = (LiquibasePgGenerateMojo) rule.lookupMojo(
            "generate", new File("src/test/resources/simple-postgres-pom.xml")
        );

        rule.setVariableValueToObject(container, "project", new MavenProjectStub());

        // WHEN
        container.execute();

        // THEN
        File generatedSource = new File("target/generated-sources/jooq/com/freenow/example/tables/pojos/Passenger.java");
        assertTrue(generatedSource.exists());
    }

    @Test
    public void runPluginPostgis() throws Exception
    {
        // GIVEN
        LiquibasePgGenerateMojo container = (LiquibasePgGenerateMojo) rule.lookupMojo(
            "generate", new File("src/test/resources/simple-postgis-pom.xml")
        );

        rule.setVariableValueToObject(container, "project", new MavenProjectStub());

        // WHEN
        container.execute();

        // THEN
        File generatedSource = new File("target/generated-sources/jooq/com/freenow/example/tables/pojos/Passenger.java");
        assertTrue(generatedSource.exists());
    }

    @Test
    public void runPluginPostgresWithParentChildLog() throws Exception
    {
        // GIVEN
        LiquibasePgGenerateMojo container = (LiquibasePgGenerateMojo) rule.lookupMojo(
                "generate", new File("src/test/resources/simple-postgres-parentchild-pom.xml")
        );

        rule.setVariableValueToObject(container, "project", new MavenProjectStub());

        // WHEN
        container.execute();

        // THEN
        File generatedPassengerSource = new File("target/generated-sources/jooq/com/freenow/example/tables/pojos/Passenger.java");
        File generatedSeatSource = new File("target/generated-sources/jooq/com/freenow/example/tables/pojos/Seat.java");
        assertTrue(generatedPassengerSource.exists());
        assertTrue(generatedSeatSource.exists());
    }
}
package com.mytaxi.jooqoverpostgres;

import java.io.File;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class JooqOverPostgresContainerTest
{
    @Rule
    public MojoRule rule = new MojoRule();

    @Test
    public void runPlugin() throws Exception
    {
        // GIVEN
        JooqOverPostgresContainer container = (JooqOverPostgresContainer) rule.lookupMojo(
            "jooqOverPostgresContainer", new File("src/test/resources/simple-postgres-pom.xml")
        );

        rule.setVariableValueToObject(container, "project", new MavenProjectStub());

        // WHEN
        container.execute();

        // THEN
        File generatedSource = new File("target/generated-sources/jooq/com/mytaxi/bookingoptionsservice/tables/pojos/Passenger.java");
        assertTrue(generatedSource.exists());
    }
}
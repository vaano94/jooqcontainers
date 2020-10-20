package com.freenow.jooqcontainers.core;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Generate;
import org.jooq.meta.jaxb.Generator;
import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.jaxb.Target;
import org.testcontainers.jdbc.ContainerDatabaseDriver;

public abstract class AbstractGenerator
{
    private final String tcJdbcUrl;
    private final Configuration jooqConfig;
    private final String targetDirectory;


    public AbstractGenerator(String tcJdbcUrl, Configuration jooqConfig, String targetDirectory)
    {
        this.tcJdbcUrl = tcJdbcUrl;
        this.jooqConfig = jooqConfig;
        this.targetDirectory = targetDirectory;
    }


    public AbstractGenerator(String tcDatabaseName, String tcDatabaseVersion, Configuration jooqConfig, String targetDirectory)
    {
        this.tcJdbcUrl = "jdbc:tc:" + tcDatabaseName + ":" + tcDatabaseVersion + "://localhost/test";
        this.jooqConfig = jooqConfig;
        this.targetDirectory = targetDirectory;
    }


    protected abstract void runMigration(Connection connection) throws Exception;

    protected abstract String getMigrationDefaultExcludes();


    public void generate()
    {
        try (Connection connection = getConnection())
        {
            runMigration(connection);
            tuneJooqConfiguration();
            generateJooqClasses();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        finally
        {
            ContainerDatabaseDriver.killContainer(tcJdbcUrl);
        }
    }


    private void tuneJooqConfiguration() {

        if (jooqConfig.getJdbc() == null)
        {
            jooqConfig.setJdbc(new Jdbc());
        }

        if (jooqConfig.getGenerator() == null)
        {
            jooqConfig.setGenerator(new Generator());
        }

        if (jooqConfig.getGenerator().getTarget() == null)
        {
            jooqConfig.getGenerator().setTarget(new Target());
        }

        if (jooqConfig.getGenerator().getGenerate() == null)
        {
            jooqConfig.getGenerator().setGenerate(new Generate());
        }

        if (jooqConfig.getGenerator().getDatabase() == null)
        {
            jooqConfig.getGenerator().setDatabase(new org.jooq.meta.jaxb.Database());
        }

        if (isNullOrEmpty(jooqConfig.getGenerator().getDatabase().getExcludes()))
        {
            jooqConfig.getGenerator().getDatabase().setExcludes(getMigrationDefaultExcludes());
        }

        jooqConfig.getJdbc().setDriver(ContainerDatabaseDriver.class.getName());
        jooqConfig.getJdbc().setUrl(tcJdbcUrl);
        jooqConfig.getGenerator().getTarget().setDirectory(Paths.get(targetDirectory).toAbsolutePath().toString());
    }

    private void generateJooqClasses() throws Exception
    {
        GenerationTool.generate(jooqConfig);
    }


    private Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection(tcJdbcUrl);
    }


    private static boolean isNullOrEmpty(String value)
    {
        return value == null || value.isEmpty();
    }

}

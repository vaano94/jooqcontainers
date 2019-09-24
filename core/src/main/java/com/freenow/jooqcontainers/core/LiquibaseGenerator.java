package com.freenow.jooqcontainers.core;

import java.nio.file.Paths;
import java.sql.Connection;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;
import org.jooq.meta.jaxb.Configuration;

public class LiquibaseGenerator extends AbstractGenerator
{
    private final String liquibaseChangeLogFile;


    public LiquibaseGenerator(String tcJdbcUrl, Configuration jooqConfig, String liquibaseChangeLogFile)
    {
        super(tcJdbcUrl, jooqConfig);
        this.liquibaseChangeLogFile = liquibaseChangeLogFile;
    }


    public LiquibaseGenerator(String tcDatabaseName, String tcDatabaseVersion, Configuration jooqConfig, String liquibaseChangeLogFile)
    {
        super(tcDatabaseName, tcDatabaseVersion, jooqConfig);
        this.liquibaseChangeLogFile = liquibaseChangeLogFile;
    }


    @Override
    protected void runMigration(Connection connection) throws LiquibaseException
    {
        new Liquibase(Paths.get(liquibaseChangeLogFile).toAbsolutePath().toString(), new FileSystemResourceAccessor(), getDatabase(connection))
            .update("main");
    }


    @Override
    protected String getMigrationDefaultExcludes()
    {
        return "databasechangelog.*";
    }


    private Database getDatabase(Connection c) throws DatabaseException
    {
        return DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(c));
    }
}

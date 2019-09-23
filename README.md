# What is this

Maven plugin which can be integrated to any maven project
Sample :   
```xml
<plugin>  
    <groupId>com.freenow</groupId>  
     <artifactId>jooqgen-liquibase-postgres</artifactId>
    <configuration>
        <schema>public</schema> <!-- schema name -->
        <packageName>com.example.yourpackage</packageName> <!-- generated classes target package -->
        <liquibaseChangeLogFile>${project.basedir}/src/main/resources/liquibase/changelog.xml</liquibaseChangeLogFile> 
    </configuration>
    <executions>
         <execution>
             <phase>generate-sources</phase>
            <goals>
                 <goal>jooqOverPostgresContainer</goal>
            </goals>
        </execution>
    </executions>
 </plugin>      
 ``` 

# What it does

1. Starts a postgress docker container.
2. Applies liquibase changes over the container.
3. Generates JOOQ classes for the source project connecting to postgres container.


# Problems and solutions

If generated classes fail to compile, 
1. include ` /target/generated-sources/jooq/` folder to corresponding compiler plugin.
2. If kotlin-maven-plugin compilation failes, add 
    ``` 
    <configuration>
        <sourceDirs>
            <source>src/main/java</source>
            <source>target/generated-sources/jooq</source>
        </sourceDirs>
    </configuration>
    ```
 3. If the `NoClassDefError` happens, this means the class files are missing. Add the following plugin
```
 <plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>build-helper-maven-plugin</artifactId>
    <executions>
        <execution>
            <phase>generate-sources</phase>
            <goals>
                <goal>add-source</goal>
            </goals>
            <configuration>
                <sources>
                    <source>${project.build.directory}/generated-sources/jooq</source>
                </sources>
            </configuration>
        </execution>
    </executions>
 </plugin>
```

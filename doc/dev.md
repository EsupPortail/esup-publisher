# migration help
 
## migrating from utf8 to utf8mb4
- old liquibase migration files where removed and a new init schema was generated
- to migrate :
__Don't forget to make a dump of your datas before such operation__
1. dump datas with 
```sql
  mysqldump -h hostname -u user -p --no-create-info --complete-insert --extended-insert --ignore-table="publisher.databasechangeloglock" --ignore-table="publisher.databasechangelog" --ignore-table="publisher.t_persistent_audit_event" --ignore-table="publisher.t_persistent_audit_event_data" "publisher" > "publisher.sql"
```
2. drop and recreate database
```sql
  drop database publisher;
  create database publisher DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci;
```
3. run the publisher app or run
```mvn
./mvnw compile liquibase:update
```
4. import dumped datas
```sql
  delete from publisher.t_user;
  \. publisher.sql
```

# mvn commands

## to add NOTICE
`./mvnw notice:check` Checks that a NOTICE file exists and that its content match what would be generated.
`./mvnw notice:generate` Generates a new NOTICE file, replacing any existing NOTICE file.


## to add licence headers
`./mvnw license:check` verify if some files miss license header
`./mvnw license:format` add the license header when missing. If a header is existing, it is updated to the new one.
`./mvnw license:remove` remove existing license header


## In dev process when changing the entities
`./mvnw clean generate-sources` will generates new or modified Q__ classes or to update evaluator's entites when updating evaluator lib

## To see deprecated code and warnings
 `./mvnw compile -Dmaven.compiler.showWarnings=true -Dmaven.compiler.showDeprecation=true`


## Liquibase memento :
[WARN]JAVA 8 use: Don't forget to add `-Djadira.usertype.useJdbc42Apis=false` jvm arg to the command line
- see in liquibase.properties.generation to set database name and defaultSchemaName (important for mysql, schemaName must be equals to database name)
- on non local database you can have X11 error if your are running the script from a server, see in liquibase.properties.generation property promptOnNonLocalDatabase to set to false
- `./mvnw compile liquibase:update` will apply changelog not loaded (from liquibase files) to the database
- `./mvnw compile liquibase:generateChangeLog` will generate the creation of database changelog file
- `./mvnw compile liquibase:diff` to generate each diff
- IMPORTANT : check for mysql that table names are in lower case in changelog files, do sames things for all table links...


## to run tests :
- `./mvnw test -Dspring.profiles.active=dev,fast,ldapgrp,test` don't forget to change databasename between prod
- `./mvnw test -Dtest=org.esupportail.publisher.repository.PermissionOnContextRepositoryTest#testInsert -Dspring.profiles.active=dev,fast,ldapgrp` don't forget to change databasename between prod for a specific class


## to run in dev :
- `./mvnw clean spring-boot:run -Dmaven.test.skip=true -Pdev` (+ `grunt serve` pour firefox)

## ./mvnw remember :
- `./mvnw release:perform -Dmaven.test.skip=true -Darguments="-DskipTests -Dmaven.deploy.skip=true"` to avoid to deploy a release
- on release run `grunt ngconstant` to update version in angular app constant, we should try to watch on a grunt task to make release

## to deploy :
- before you should build the war package : `./mvnw clean package -P prod -Dmaven.test.skip=true -Darguments="-DskipTests -Dmaven.deploy.skip=true"`
- and deploy the war into your webapps directory `unzip esup-publisher-ui-x.y.z.war -d publisher`

## mvn param to debug xml binding :
-Djaxb.debug=true
this permit to view : javax.xml.bind - Trying to locate org/esupportail/publisher/web/rest/vo/jaxb.properties and other params

# TODO

## TODO dependencies update
//@JsonFormat(shape = JsonFormat.Shape.STRING) should works with jackson 2.5, until use @JsonSerialize with custom serializer

## TODO with cache
error appear :
[WARN] net.sf.ehcache.pool.sizeof.ObjectGraphWalker - The configured limit of 1 000 object references was reached while attempting to calculate the size of the object graph.
Severe performance degradation could occur if the sizing operation continues. This can be avoided by setting the CacheManger or Cache <sizeOfPolicy> elements maxDepthExceededBehavior
to "abort" or adding stop points with @IgnoreSizeOf annotations. If performance degradation is NOT an issue at the configured limit, raise the limit value using the CacheManager or
Cache <sizeOfPolicy> elements maxDepth attribute. For more information, see the Ehcache configuration documentation.
=> read http://www.ehcache.org/documentation/2.8/configuration/cache-size.html#built-in-sizing-computation-and-enforcement



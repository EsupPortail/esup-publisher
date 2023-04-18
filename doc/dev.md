## migration help

## migrating from utf8 to utf8mb4

*   old liquibase migration files where removed and a new init schema was generated
*   to migrate :  
    **Don't forget to make a dump of your datas before such operation**

1.  dump datas with
2.  drop and recreate database
3.  run the publisher app or run
4.  import dumped datas

## developpers requirements

1.  This project have some dev requirements, like using nvm as a file .nvmrc is provided for all developpers use the same node version. The nvmrc file should follow the last quilified node version and according to CI test
2.  Also this project embed some git hooks, so please configure hooks path for this project with this command `git config core.hooksPath .githooks`
3.  As node version giit hooks check the java version, so please update the `.githooks/pre-commit.d/mvn_chek_on_pre-commit` file to follow versions

## mvn commands

## to add NOTICE

`./mvnw notice:check` Checks that a NOTICE file exists and that its content match what would be generated.  
`./mvnw notice:generate` Generates a new NOTICE file, replacing any existing NOTICE file.

## to add licence headers

`./mvnw license:check` verify if some files miss license header  
`./mvnw license:format` add the license header when missing. If a header is existing, it is updated to the new one.  
`./mvnw license:remove` remove existing license header

## to sort dependencies in pom

`./mvnw sortpom:sort` will sort all in pom  
`./mvnw sortpom:verify` will check the order in the pom and provide a bak file to compare.

## In dev process when changing the entities

`./mvnw clean generate-sources` will generates new or modified Q\_\_ classes or to update evaluator's entites when updating evaluator lib

## To see deprecated code and warnings

`./mvnw compile -Dmaven.compiler.showWarnings=true -Dmaven.compiler.showDeprecation=true`

## Liquibase memento :

\[WARN\]JAVA 8 use: Don't forget to add `-Djadira.usertype.useJdbc42Apis=false` jvm arg to the command line

*   see in liquibase.properties.generation to set database name and defaultSchemaName (important for mysql, schemaName must be equals to database name)
*   on non local database you can have X11 error if your are running the script from a server, see in liquibase.properties.generation property promptOnNonLocalDatabase to set to false
*   `./mvnw compile liquibase:update` will apply changelog not loaded (from liquibase files) to the database
*   `./mvnw compile liquibase:generateChangeLog` will generate the creation of database changelog file
*   `./mvnw compile liquibase:diff` to generate each diff
*   IMPORTANT : check for mysql that table names are in lower case in changelog files, do sames things for all table links...

## to run tests :

### Init steps :

*   running local docker `docker run -it -p 3306:3306/tcp --health-cmd="mysqladmin -uroot -proot ping" --health-interval=10s --health-timeout=10s --health-retries=10 -e "TZ=Europe/Paris" -e "MYSQL_USER=root" -e "MYSQL_ROOT_PASSWORD=root" -e "MYSQL_DATABASE=publisher_test" -e "MYSQL_DEFAULT_STORAGE_ENGINE=InnoDB" -e "MYSQL_CHARACTER_SET_SERVER=utf8mb4" -e "MYSQL_COLLATION_SERVER=utf8mb4_unicode_520_ci" -e "MYSQL_INNODB_BUFFER_POOL_SIZE=2G" -e "MYSQL_INNODB_DEFAULT_ROW_FORMAT=dynamic" -e "MYSQL_INNODB_DATA_FILE_PATH=ibdata1:100M:autoextend:max:10G" -e "MYSQL_INNODB_FLUSH_LOG_AT_TRX_COMMIT=1" -e "MYSQL_INNODB_LOG_BUFFER_SIZE=64M" -e "MYSQL_INNODB_LOG_FILE_SIZE=256M" -e "MYSQL_INNODB_STRICT_MODE=ON" -e "MYSQL_LOWER_CASE_TABLE_NAMES=1" -e "MYSQL_MAX_CONNECT_ERRORS=100" -e "MYSQL_MAX_CONNECTIONS=1000" -e "MYSQL_QUERY_CACHE_LIMIT=10M" -e "MYSQL_QUERY_CACHE_SIZE=0" -e "MYSQL_QUERY_CACHE_TYPE=OFF" wodby/mariadb:latest`
*   `./mvnw clean generate-sources compile liquibase:update`
*   `mysql -h127.0.0.1 -u root -proot -e "ALTER TABLE publisher_test.t_permission CHANGE perm perm VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL;"`
*   run the openldap-unittest docker like on github-action description

### And running test:

*   `./mvnw test -Ptest -Dspring.profiles.active=test` don't forget to change databasename between prod, adding argument `-Dspring.profiles.active=test,fast,ldapgrp` will permit to override the default conf
*   `./mvnw test -Dtest=org.esupportail.publisher.repository.PermissionOnContextRepositoryTest#testInsert -Ptest -Dspring.profiles.active=test` don't forget to change databasename between prod for a specific class

## to run in dev :

*   `./mvnw clean spring-boot:run -Dmaven.test.skip=true -Pdev` (+ `npm run serve` pour firefox)

## to run with additional configuration overriding

*   `./mvnw spring-boot:run -Pprod -e -Dspring-boot.run.arguments="--spring.profiles.active=prod --spring.config.additional-location=${project.home}/esup-publisher/config"` to provide custom properties, like overriding config

## Release process :

*   put away local change `git stash`
*   `./mvnw clean generate-sources release:prepare release:perform deploy -P prod -Dmaven.test.skip=true -Darguments="-DskipTests -Dmaven.deploy.skip=true"`
*   `git push origin master`
*   `git stash apply`

## ./mvnw remember :

*   `./mvnw release:perform -Dmaven.test.skip=true -Darguments="-DskipTests -Dmaven.deploy.skip=true"` to avoid to deploy a release

## to deploy :

*   before you should build the war package : `./mvnw clean package -P prod -Dmaven.test.skip=true -Darguments="-DskipTests -Dmaven.deploy.skip=true"`
*   and deploy the war into your webapps directory `unzip esup-publisher-ui-x.y.z.war -d publisher`
*   into a local nexus `./mvnw deploy:deploy-file -Durl=https://nexus.recia.fr/repository/public/ -DrepositoryId=local-nexus-snapshot -Dfile=target/esup-publisher-ui-2.0.0-SNAPSHOT.war -DpomFile=pom.xml`

## mvn param to debug xml binding :

\-Djaxb.debug=true  
this permit to view : javax.xml.bind - Trying to locate org/esupportail/publisher/web/rest/vo/jaxb.properties and other params

## TODO

## TODO dependencies update

//@JsonFormat(shape = JsonFormat.Shape.STRING) should works with jackson 2.5, until use @JsonSerialize with custom serializer

## TODO with cache

error appear :  
\[WARN\] net.sf.ehcache.pool.sizeof.ObjectGraphWalker - The configured limit of 1 000 object references was reached while attempting to calculate the size of the object graph.  
Severe performance degradation could occur if the sizing operation continues. This can be avoided by setting the CacheManger or Cache elements maxDepthExceededBehavior  
to "abort" or adding stop points with @IgnoreSizeOf annotations. If performance degradation is NOT an issue at the configured limit, raise the limit value using the CacheManager or  
Cache elements maxDepth attribute. For more information, see the Ehcache configuration documentation.  
\=> read http://www.ehcache.org/documentation/2.8/configuration/cache-size.html#built-in-sizing-computation-and-enforcement

```plaintext
delete from publisher.t_user;
\. publisher.sql
```

```plaintext
./mvnw compile liquibase:update
```

```plaintext
drop database publisher;
create database publisher DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci;
```

```plaintext
mysqldump -h hostname -u user -p --no-create-info --complete-insert --extended-insert --ignore-table="publisher.databasechangeloglock" --ignore-table="publisher.databasechangelog" --ignore-table="publisher.t_persistent_audit_event" --ignore-table="publisher.t_persistent_audit_event_data" "publisher" &gt; "publisher.sql"
```
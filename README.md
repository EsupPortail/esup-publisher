esup-publisher-ui
=================

Application to publish content into several/different apps (uPortal portlet apps as example but not only!)


Database Initialization
-----------------------

### Requirement

The database server should be a recent version supporting utf8mb4 characters and the collation indicated bellow.
As example you can use this [mariadb configuration](https://github.com/GIP-RECIA/docker-mariadb/) the application is qualified on a such server.

Running with openjdk 8 or 11

### Initialization

```sql
create database publisher DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci;
```

run mvn command to init the database

```shell
./mvnw compile liquibase:update
```

or deploy and run directly the app

for more command details see doc/dev.txt info

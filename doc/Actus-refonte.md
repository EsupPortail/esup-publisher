# Documentation ENT Redesign - News

Technical document relating to additions/modifications to the publisher application as part of the replacement of the
‘esup-lecture’ application.

## 1: Application context

The first question asked was whether to replace ‘esup-lecture’ with an upgrade of the publisher application or to
develop a new tool from scratch.
L'application publisher est une plateforme de gestion des permettant notamment la publication, la modification et la
suppression de contenu tels que des articles ou des documents.
As a result, publisher already includes all the logic associated with news, role management and authentication.
What's more, publisher had all the configurations needed to access the content stored in the database.
In this context, we thought it would be quicker and easier to use the existing logic and enhance the publisher
application with new features rather than starting from scratch.

## 2: Technical aspects

In a desire to modernise the technologies used and to meet expectations as closely as possible, it has been decided that
the old portlet system of ‘esup-lecture’ will be replaced by a REST API.

## 3: NewsController

### Overview

The NewsController is a Spring Boot REST controller responsible for handling news-related API requests. It provides
endpoints for retrieving user-specific news, fetching individual news items, obtaining reading information, and managing
the reading status of news items.

### Endpoints

1. `GET /news/myNews/{reader_id}`

Description: Retrieves news for a specific user based on filters and pagination.

Parameters :

* `reader_id` (PathVariable, Long, required): The ID of the reader.
* `pageIndex` (RequestParam, Integer, optional, default: 0): The page index for pagination.
* `source` (RequestParam, String, optional): The news source filter.
* `rubriques` (RequestParam, String, optional): The news source filter.
* `lecture` (RequestParam, Boolean, optional): Filter by reading status.

Response:

* `200` Returns paginated news data for the user.
* `400` Bad Request if rubriques are provided without source.
* `404` Not Found if no news is found.
* Generic error message for unexpected exceptions.


2. `GET /news/item/{item_id}`

Description: Fetches a specific news item based on its ID.

Parameters:

* `item_id` (PathVariable, Long, required): The ID of the news item.

Response:

* `200` Returns the news item data.


3. `GET /news/readingInfos`

Description: Retrieves reading status information for all news items of the current user.

Response:

* `200` A map containing news item IDs as keys and their reading status as values.
* null if an exception occurs.


4. `PATCH /news/setNewsReading/{item_id}/{isRead}`

Description: Updates the reading status of a specific news item.

Parameters:

* `item_id` (PathVariable, Long, required): The ID of the news item.

* `isRead` (PathVariable, boolean, required): The new reading status (true for read, false for unread).

Response:

* `200` OK on successful update.

* Throws a RuntimeException if an error occurs.

## 4: Security and authentication

Before the change, all security was handled by the CAS protocol. From now on, and in the context of replacing the
portlet, the security of our new controller is ensured via the ‘Soffit’ protocol based on OIDC.

1. Adding a new security string to SecurityConfiguration

```java

@Bean
@Order(1) // Priorité élevée pour les URLs liées à JWT
public SecurityFilterChain configureSoffit(HttpSecurity http) throws Exception {

  final AbstractPreAuthenticatedProcessingFilter soffitFilter =
    new SoffitApiPreAuthenticatedProcessingFilter(signatureKey);

  soffitFilter.setAuthenticationManager(soffitAuthenticationManager());

  http.antMatcher("/news/**")
    .sessionManagement()
    .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
    .cors().and().csrf().disable()
    .addFilterBefore(soffitFilter, BasicAuthenticationFilter.class)
    .authorizeRequests()
    .antMatchers("/news/home").permitAll() // URLs publiques
    .antMatchers("/news/**").authenticated() // URLs nécessitant une authentification JWT
    .and().authenticationManager(soffitAuthenticationManager());

  return http.build();
}
```

### Warning:

The two types of authentication may conflict. In order to differentiate the paths relative to each, declare the
‘antMatcher’ property for the rest.

## 5: Cache Management

In order to meet the performance requirements imposed by the project, the caching system was adapted to our needs.

Addition of a new cache in CacheConfiguration.java :

```java

@Bean
public JCacheManagerCustomizer cacheManagerCustomizer() {
  return cm -> {
    createCache(cm, "actualiteByPublisher");
  };
}
```

### Uses

The first step is to declare the cache (TreeGenerationService.java):

```java

@Cacheable(value = "actualiteByPublisher", key = "#publisher")
public Actualite getActualiteByPublisher(Publisher publisher, final HttpServletRequest request) {
}
```

Once you have obtained a list of all the publishers, this method is used to retrieve all the news items linked to that
publisher. It is this result that we put in the cache.
So we create several entries in the ‘actualiteByPublisher’ cache, identified by their publisher. This allows greater
granularity in cache management.

We use this granularity in the following cases:

#### Cache invalidation when a new news item is published (`ContentService.java`)

```java
public ResponseEntity<?> create(@RequestBody ContentDTO content) throws URISyntaxException {
  if (content.getItem().getId() != null) {
    return ResponseEntity.badRequest().header("Failure", "A new contents cannot already have an ID").build();
  }
  final List<Publisher> publishers = Lists.newArrayList(this.publisherRepository.findAll(
    PublisherPredicates.AllOfOrganizationAndRedactor(content.getItem().getOrganization(),
      content.getItem().getRedactor())));

  publishers.forEach(publisher -> {
    Objects.requireNonNull(cacheManager.getCache("actualiteByPublisher")).evict(publisher);
  });
  return contentService.saveContent(content);
}
```

#### Cache invalidation when modifying a news item (`ContentService.java`)

```java
public ResponseEntity<?> update(@RequestBody ContentDTO content) throws URISyntaxException {
  final List<Publisher> publishers = Lists.newArrayList(this.publisherRepository.findAll(
    PublisherPredicates.AllOfOrganizationAndRedactor(content.getItem().getOrganization(),
      content.getItem().getRedactor())));
  publishers.forEach(publisher -> {
    Objects.requireNonNull(cacheManager.getCache("actualiteByPublisher")).evict(publisher);
  });
  return contentService.saveContent(content);
}
```

#### Cache invalidation when deleting a news item (`ContentService.java`)

```java
public void delete(@PathVariable Long id) {
  Optional<AbstractItem> i = itemRepository.findById(id);
  final List<Publisher> publishers = Lists.newArrayList(this.publisherRepository.findAll(
    PublisherPredicates.AllOfOrganizationAndRedactor(i.get().getOrganization(), i.get().getRedactor())));
  publishers.forEach(
    publisher -> Objects.requireNonNull(cacheManager.getCache("actualiteByPublisher")).evict(publisher));
  contentService.deleteContent(id);
}
```

## 6: Database

### Create `T_READING_INDICATOR` Table

The purpose of this new table is to create a correspondence between a user and a news item so that it can be used as a
read indicator.

```xml

<createTable tableName="T_READING_INDICATOR">
  <column autoIncrement="true" name="id" type="BIGINT">
    <constraints nullable="false" primaryKey="true" primaryKeyName="T_READING_INDICATORPK"/>
  </column>
  <column name="isRead" type="VARCHAR(255)"/>
  <column name="readingCounter" type="BIGINT"/>
  <column name="item_id" type="BIGINT"/>
  <column name="user_id" type="VARCHAR(50)"/>
</createTable>
```

### WARNING 1 :

Pay particular attention to the encoding used to create the columns. The entire database is in `utf8mb4_unicode_520_ci`,
yet the changelog created during `liquibase:diff` is in `utf8mb4_unicode_ci`. This caused me problems when creating the
table in the database. To solve this problem :

```xml

<sql>
  ALTER TABLE T_READING_INDICATOR CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci;
</sql>
```

This should solve the problem.

### WARNING 2 :

When you run the `liquibase:base` command, the new changelog may contain the following lines :

```xml
<changeSet author="" id="xxxxx">
  <dropDefaultValue columnDataType="varchar(2048)" columnName="ressource_url" tableName="T_ITEM"/>
</changeSet>
```

#### If this is the case, delete these lines, otherwise the schema and the database will be out of sync. 




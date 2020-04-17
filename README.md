# Table of Contents
1. [Introduction](#introduction)
2. [Prerequisites](#prerequisites)
3. [Features](#features)

# Introduction

Some helpful validations to be used in your Spring application

# Installation

Add the following dependency in your project to start using JPA Query Param.

```bash
implementation 'org.manuel.spring:manuelarte-validation:{latest-version}'
```

# Prerequisites

- Java8 or above
- Spring Data

# Features

## Exists Constraint

This constraint can be used to check in a controller if the entity exists before executing the method

### Prerequisites

- The constraint validations need to be executed, for example annotating your Controller with @Validated
- The entity/document that is going to be checked needs to have a Repository.

### Example

```java
...
@Validated
public class DocumentController {
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DocumentEntity> findOne(
      @PathVariable @Exists(DocumentEntity.class) final String id) {
        return ResponseEntity.ok(documentService.findOne(id));
    }
}

@Repository
public interface DocumentEntityRepository extends CrudRepository<DocumentEntity, Long> {}
```

## Validation Groups

There are three groups added to be used in your dtos, New, Update, PartialUpdate, that can be used
as validation groups.

### Example

Imagine that you have an entity that you want to allow to be created, updated and partially updated.
By using validation groups, we can have the same dto for the different endpoints. Here is an example:

```java
public class OneEntityDto {

    @Null(groups = {New.class, PartialUpdate.class})
    @NotNull(groups = Update.class)
    private final Long id;

    @NotEmpty(groups = {New.class, Update.class})
    private final String firstName;
    @NotEmpty(groups = {New.class, Update.class})
    private final String lastName;
    ...
}

...
@Validated
public class OneEntityController {
    @PostMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OneEntity> findOne(
    @Validated({Default.class, New.class}) @RequestBody final OneEntity newEntity) {
        return ResponseEntity.ok(entityService.save(newEntity));
    }
}
```

As you can see, when posting new entities, the validation groups default and new will apply.

## CrupRepository

Extension for the Spring Data Repository to allow partial updates.

### Prerequisites

- The entity needs to have a single field with @Id attribute
# Table of Contents
1. [Introduction](#introduction)
2. [Prerequisites](#prerequisites)
3. [Features](#featires)

# Introduction

Some helpful validations to be used in your Spring application

# Installation

Add the following dependency in your project to start using JPA Query Param.

```bash
implementation 'org.manuel.spring:manuelarte-validation:{latest-version}'
```

# Prerequisites

- Java8 or above
- Spring Data MongoDB

# Features

## Exists Constraint

This constraint can be used to check in a controller if the document exists before executing the method

### Prerequisites

- The constraint validations need to be executed, for example annotating your Controller with @Validated

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
```
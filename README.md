# TIS Revalidation Concerns Microservice

## About
This is a template to be used for TIS microservices with the following
technology:

 - Java 11
 - Spring Boot
 - Gradle/Maven
 - JUnit 5

Boilerplate code is to be generated with:
 - Lombok
 - MapStruct

Code quality checking and enforcement is done with the following tools:
 - EditorConfig
 - Checkstyle
 - JaCoCo
 - SonarQube

Error and exception logging is done using Sentry.

## Usage
To use this template, create a new repository from it and follow the TODOs in
the code, with the following additional changes.
 - Update copyright year in [LICENSE](LICENSE).
 - Update copyright year in [TemplateApplication].
 - Update copyright year in [TemplateApplicationTest].
 - Update this README.
 - Set up Sentry project.
 - Provide `SENTRY_DSN` and `SENTRY_ENVIRONMENT` as environmental variables
   during deployment.
 - Add repository to SonarCloud.
 - Add SonarCloud API key to repository secrets.
 - Add repository to Dependabot.
 - Update the references to `tis-template` and port number in [task-definition].
 - Add the AWS credentials to repository secrets.

## Versioning
This project uses [Semantic Versioning](semver.org).

## License
This project is license under [The MIT License (MIT)](LICENSE).

[task-definition]: .aws/task-definition.json
[TemplateApplication]: src/main/java/uk/nhs/hee/tis/template/TemplateApplication.java
[TemplateApplicationTest]: src/test/java/uk/nhs/hee/tis/template/TemplateApplicationTest.java

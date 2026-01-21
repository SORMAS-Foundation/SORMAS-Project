# AGENTS.md

## Overview

SORMAS (Surveillance Outbreak Response Management and Analysis System) is an open-source eHealth system designed for public health authorities to monitor infectious diseases and manage outbreak responses [1](#1-0) . The system consists of separate web and mobile applications optimized for disease surveillance, contact tracing, and outbreak management.

## System Architecture

### Multi-Module Maven Structure

SORMAS follows a multi-module Maven architecture with clear separation of concerns [2](#1-1) :

- **sormas-api**: Shared DTOs, interfaces, and business logic definitions
- **sormas-backend**: Server-side implementation with facades and services
- **sormas-ui**: Vaadin-based web application
- **sormas-rest**: REST API interface
- **sormas-app**: Android mobile application
- **sormas-base**: Parent POM with build configuration
- **sormas-cargoserver**: Local development server setup

### Technology Stack

The system uses Java EE 8 on Payara Server 5.2022.5 [3](#1-2)  with:

- **Backend**: EJB 3.2, JPA 2.2, JAX-RS 2.1, CDI 2.0
- **Database**: PostgreSQL with Hibernate 5.6.15
- **Web UI**: Vaadin 8.14.3 with GWT compiler
- **Mobile**: Android with ORMLite for SQLite
- **Authentication**: Keycloak 21.1.2 for OIDC/OAuth2
- **API Documentation**: Swagger/OpenAPI 2.2.15

## Core Domain Model

SORMAS manages public health data through interconnected entities:

- **Person**: Central entity for demographic data
- **Case**: Disease cases with classification and outcome tracking
- **Contact**: Exposed individuals with follow-up status
- **Event**: Outbreaks or gatherings with participants
- **Sample & PathogenTest**: Laboratory specimen tracking
- **Immunization**: Vaccination history and records

## Development Environment

### Prerequisites

- Java 11 JDK for backend development [4](#1-3)
- Maven 3.6.3+ for build management
- PostgreSQL database
- Payara Server for deployment

### Setup Process

1. Clone repository and configure Git for rebase workflow
2. Install Java 11 JDK and Maven
3. Set up local SORMAS server using Maven Cargo or Docker
4. Configure IDE (IntelliJ or Eclipse) with proper plugins
5. Build project with `mvn install` from sormas-base directory

## Key Development Patterns

### Facade-Service Architecture

Backend follows a clear separation:

- **Facades**: EJBs with `@RightsAllowed` annotations for security
- **Services**: Business logic implementation with JPA queries
- **DTOs**: Data transfer objects for client communication

### Testing Framework

Uses comprehensive testing approach [5](#1-4) :

- JUnit 5 for core testing
- Hamcrest for matchers
- Mockito for mocking
- ArchUnit for architectural rules
- Testcontainers for PostgreSQL integration tests

### Security Model

Multi-layered security with:

- OIDC/OAuth2 via Keycloak
- Role-based access control with `@RightsAllowed`
- Jurisdiction-based data filtering
- Pseudonymization for GDPR compliance

## Important Configuration

### sormas.properties

Main configuration file controls [6](#1-5) :

- Feature flags and server settings
- Authentication providers
- Database connections
- File paths and external service URLs

### Feature Management

System uses feature toggles in `FeatureType` enum [7](#1-6)  to enable/disable functionality per deployment.

## API Documentation

REST API is automatically documented with OpenAPI specification [8](#1-7) :

- Semantic versioning (X.Y.Z)
- Development branch for ongoing work
- Feature branches for new functionality
- Pull requests with code review requirements

## Documentation Structure

Comprehensive documentation in docs/ folder:

- `CONTRIBUTING.md`: Development guidelines
- `DEVELOPMENT_ENVIRONMENT.md`: Setup instructions
- `SERVER_SETUP.md`: Production deployment
- `SERVER_CUSTOMIZATION.md`: Configuration options

## Notes

This reference document provides essential technical information for understanding and working with the SORMAS codebase. For detailed implementation specifics, refer to the individual module README files and the comprehensive wiki documentation available at https://wiki.sorm.as.

Wiki pages you might want to explore:

- [Overview (SORMAS-Foundation/SORMAS-Project)](/wiki/SORMAS-Foundation/SORMAS-Project#1)

### Citations

**File:** README.md (L16-19)

```markdown
# SORMAS

**SORMAS** (Surveillance Outbreak Response Management and Analysis System) is an open source eHealth system - consisting of separate web and mobile apps - that is geared towards optimizing the processes used in monitoring the spread of infectious diseases and responding to outbreak situations.

```

**File:** README.md (L77-93)

```markdown
## Project Structure

The project consists of the following modules:

- [**sormas-api:**](/sormas-api) General business logic and definitions for data exchange between app and server
- [**sormas-app:**](/sormas-app) The Android app
- [**sormas-backend:**](/sormas-backend) Server entity services, facades, etc.
- [**sormas-base:**](/sormas-base) Base project that also contains build scripts
- [**sormas-cargoserver:**](/sormas-cargoserver) Setup for a local dev server using maven-cargo
- [**sormas-e2e-performance-tests:**](/sormas-e2e-performance-tests) Automated performance tests addressing the ReST interface (sormas-rest)
- [**sormas-e2e-tests:**](/sormas-e2e-tests) Automated frontend tests addressing sormas-ui **and** API tests against sormas-rest. The API steps are partly used to prepare data for UI tests.
- [**sormas-ear:**](/sormas-ear) The ear needed to build the application
- [**sormas-keycloak-service-provider:**](/sormas-keycloak-service-provider) Custom Keycloak SPI for SORMAS
- [**sormas-rest:**](/sormas-rest) The REST interface; see [`sormas-rest/README.md`](sormas-rest/README.md)
- [**sormas-serverlibs:**](/sormas-serverlibs) Dependencies to be deployed with the payara server
- [**sormas-ui:**](/sormas-ui) The web application
- [**sormas-widgetset:**](/sormas-widgetset) The GWT widgetset generated by Vaadin
- [**sormas-e2e-tests:**](/sormas-e2e-tests) Automated tests addressing the sormas-ui, and the ReST interface
```

**File:** sormas-base/pom.xml (L20-27)

```text
		<!-- *** Payara module versions *** -->
		<glassfish.jaxb.version>2.3.7</glassfish.jaxb.version>
		<jackson.version>2.13.4</jackson.version>
		<jakarta.activation.version>1.2.2</jakarta.activation.version>
		<javaee.version>8.0.1</javaee.version>
		<jersey.version>2.37</jersey.version>
		<!-- Payara version used for release. Overwrite them in settings.xml for local development with different versions -->
		<payara.version>5.2022.5</payara.version>
```

**File:** docs/DEVELOPMENT_ENVIRONMENT.md (L14-18)

```markdown
## Step 2: Install Java

Download and install the **Java 11 JDK** (not JRE) for your operating system, which is also needed for the [Server Setup](SERVER_SETUP.md).
We suggest using [Zulu OpenJDK](https://www.azul.com/downloads/?version=java-11-lts&package=jdk). If you're running Linux, please refer to the [official documentation](https://docs.azul.com/zulu/zuludocs/ZuluUserGuide/PrepareZuluPlatform/AttachAPTRepositoryUbuntuOrDebianSys.htm) on how to install Zulu OpenJDK on your system.

Note: To work with the Android app JDK 17 is needed for the gradle build. The needed JDK is part of Android Studio, thus there is no need to manually install it.
```

**File:** sormas-backend/README.md (L5-19)

```markdown
## Unit Testing

### JUnit 5, Hamcrest & Mockito

* [Junit 5](https://junit.org/junit5/) is the core testing framework used here.
* [Hamcrest](https://hamcrest.org/JavaHamcrest/index) is used to define declarative matchers.\
  A good tutorial can be found here: [Hamcrest Guide](https://www.baeldung.com/java-junit-hamcrest-guide).
* [Mockito](https://site.mockito.org/) is used to mock unavailable or unwanted behaviour of classes and methods.\
  An extensive tutorial can be found here: [Mockito Tutorial](https://www.baeldung.com/mockito-series).

### Jakarta EE Testing

The relevant aspects of Jakarta EE are covered in the following sub chapters.

Most important:

* **The `AbstractBeanTest` class should be used as a super class** for all EJB unit test classes. It initializes all needed mocks (e.g. `javax.ejb.SessionContext`) and provides some utility methods like `loginWith` and `executeInTransaction` (see below).
* The TestDataCreator class should be used to generated test entities and dtos where needed.
```

**File:** sormas-base/setup/sormas.properties (L114-128)

```properties
# Determines whether default infrastructure data and users are created when the server is starting with an empty database.
# Please note that a default admin user is always created to make sure that you can log in and use the system.
# Default: false
# Possible Values: true, false
#createDefaultEntities=false
# Determines whether logging in as a default user using a default password will bring up a prompt that asks the user to change their password.
# Default: false
# Possible Values: true, false
#skipDefaultPasswordCheck=false
# Dev mode allows administrators to use functionalities that are not intended for live systems, such as creating random cases for testing or training purposes.
# Default: false
# Possible Values: true, false
#devmode=false
```

**File:** sormas-api/src/main/java/de/symeda/sormas/api/feature/FeatureType.java (L340-371)

```java
  public static final FeatureType[] SURVEILLANCE_FEATURE_TYPES = {
        FeatureType.CASE_SURVEILANCE,
        FeatureType.EVENT_SURVEILLANCE,
        FeatureType.AGGREGATE_REPORTING};

/**
 * Server feature means that the feature only needs to be configured once per server since they define the way the system
 * is supposed to operate.
 */
private final boolean serverFeature;

/**
 * Is the feature enabled by default?
 */
private final boolean enabledDefault;

private final FeatureType[] dependentFeatures;
private final List<DeletableEntityType> entityTypes;
private final Map<FeatureTypeProperty, Object> supportedPropertyDefaults;

FeatureType(
        boolean serverFeature,
        boolean enabledDefault,
        FeatureType[] dependentFeatures,
        List<DeletableEntityType> entityTypes,
        Map<FeatureTypeProperty, Object> supportedPropertyDefaults) {
    this.serverFeature = serverFeature;
    this.enabledDefault = enabledDefault;
    this.dependentFeatures = dependentFeatures;
    this.entityTypes = entityTypes;
    this.supportedPropertyDefaults = supportedPropertyDefaults;
}
```

**File:** sormas-rest/README.md (L19-25)

```markdown
## API Documentation

The SORMAS REST API is documented automatically. The OpenAPI specification files are generated during the build process
and can be found at `${Project Root}/sormas-rest/target/swagger.{json,yaml}`.

You can render the OpenAPI specification with tools like
[editor.swagger.io](https://editor.swagger.io/?url=https://raw.githubusercontent.com/sormas-foundation/SORMAS-Project/development/sormas-rest/swagger.yaml).
This allows you to inspect endpoints and example payloads, generate a matching API client for many languages, and to easily interact with the API of a live instance.
```

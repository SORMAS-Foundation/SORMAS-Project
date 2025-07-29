# SORMAS Backend

The SORMAS backend layer is the implementation of the `sormas-api` and gives access to the data and logic of SORMAS. It contains a big set of facades and services that are organized based on the different entities and their domains, like case surveillance and contact tracing.

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

#### Contexts and Dependency Injection
[cdi-test](https://cdi-test.hilling.de/) boots a cdi container once for all tests using [Weld (cdi reference implementation)](http://weld.cdi-spec.org/).\
This means you can make full use of cdi annotations like `@Inject`.

#### Enterprise Beans (EJB)
cdi-test imitates this by adding cdi annotations to classes annotated with bean annotations like `@Stateless`.\
This means all EJB classes can be used in test classes by injecting them.

You can **mock beans** as explained here: [Mocking Beans](https://cdi-test.hilling.de/#mocking-beans)\
Those mocks will automatically replace all usages of the bean, so **you don't have to use the `@InjectMocks` annotation**.

As an alternative you can **inherit beans in test implementations** as explained here: [Test Implementations](https://cdi-test.hilling.de/#test-implementations)\
This allows you to only override a part of the beans logic, keeping the other functionality as-is.

#### Authentication / Security API
`java.security.Principal` is mocked. You can use `AbstractBeanTest.loginWith` to login with any default user or user you have created with `TestDataCreator`.

As we are using the `@RightsAllowed` custom security annotation, user rights will be validated as-well, as long as you don't mock the `CurrentUserService`.

#### Transactions (JTA)
**Important to understand**: Every call from a test method to a bean class or a class annotated with `@TransactionalEjb` will be intercepted and start a transaction. Calls from such a method to another will keep the same transaction. **When the call is finished the transaction will be committed and the entity manager will be cleared**.

This means that within the test method itself no transaction is active by default. Use the `AbstractBeanTest.executeInTransaction` method to wrap parts of your logic that need to be executed in one transaction.

#### Persistence (JPA)
The JPA part is build with `hibernate-core` as JPA provider and `h2database` as in-memory double for PostgreSQL.\
Custom Postgres functions of SORMAS are provided in the H2Function class.

The database is automatically reset after every test method execution. See `TestDatabaseCleaner`.

### ArchUnit
[ArchUnit](https://www.archunit.org/) is used to enforce some architectural rules, e.g. making sure backend facades are correctly annotated in terms of user access checks.\
Typcial ArchUnit use cases: [Use Cases](https://www.archunit.org/use-cases)

### Testcontainers
[Testcontainers](https://www.testcontainers.org/) is used to provide a throwaway instance of a postgres database for tests that can't be done on an in-memory h2 database.\
This is currently used to test the history tables versioning trigger.
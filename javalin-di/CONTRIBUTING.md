## Building the project

Javalin DI is built with Gradle and includes in sources the Gradle wrapper. To build the project, run the following
command:

```text
./gradlew build
```

## Running the tests

Javalin DI library consists of two types of tests:

- unit tests that are located in `src/test` directory
- E2E tests that are located in `src/e2eTest` directory

### Unit tests

Unit tests are used to verify the functionality of each module that builds the library. They are written using JUnit 5
in Kotlin and use JAssert for assertions.

Unit tests are meant to be:

- **atomic**: testing one scenario/functionality at once
- **simple**: tests itself should be simple and easy to understand, they should contain only the necessary logic to
  verify the functionality
- **explicit**: tests should be explicit about what they are testing, without unnecessary nesting or abstraction
- **fast**: tests should run quickly without the need for heavy setup/operation (e.g. I/O)
- **independent**: tests should not depend on each other, they should be able to run in any order

Unit tests should follow the following format:

```kotlin
class ComponentTest {

    @Test
    fun `should {expected behavior}`() {
        // given
        // ...

        // when
        // ...

        // then
        // ...
    }

}
```

To run the unit tests, execute the following command:

```text
./gradlew test
```

### E2E tests

E2E tests are used to verify the functionality of the library as a whole from the end user's perspective. They are
written in Kotlin using [Cucumber](https://cucumber.io/) framework.

E2E tests are the heaviest tests in the library as during the test flow they build the test Gradle project in temporary directory
with specified components to verify if the library works as expected when used by the end user along with Javalin.

Test scenarios are located in `src/e2eTest/resources/features` directory and are written in Gherkin language.

To run the E2E tests, execute the following command:

```text
./gradlew e2eTest
```

### Code coverage

Code coverage is set up using [JaCoCo](https://www.jacoco.org/) plugin but its minimal value is not currently enforced in CI pipeline.
However, it is advised to keep the code coverage as high as possible.

To generate the code coverage report, execute the following command:

```text
./gradlew jacocoTestReport
```

The report will be generated in `build/jacocoHtml` directory.

### Documentation

Each contribution should be documented in form of Java doc comments. The documentation should be clear and concise and
should explain the purpose of the contribution, its usage, and any other necessary information.





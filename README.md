# RestAssured and utilities

Java 8 library for API checks with RestAssured and TestNG, plus helpers used in QA automation.

## Utilities

| Class | What it does |
|---|---|
| `RestAssuredUtil` | HTTP calls |
| `CSVReadUtil` / `CSVWriteUtil` | CSV read and write |
| `ExcelReader` | Excel read |
| `DBconnectUtil` / `MySQLQueryUtil` | MySQL connection and queries |
| `CreateJwtToken` | JWT for auth |
| `ConnectToServer` | SSH |
| `ExtentReportUtil` | Extent reports |
| `SlackIntegrationUtil` | Slack messages |
| `PropertyFactory` | `Env1` and `Env2` property profiles |

Connection values live in `src/main/resources/Connection.properties` and `src/main/resources/Profiles/`. Replace the placeholder host, user, and password before you run the sample. Tests that still see placeholders are skipped.

## Setup

JDK 8 and Maven.

```bash
git clone https://github.com/Mandeep-Singh-Chawla/RestAssured-Framework-and-Important-Utilities.git
cd RestAssured-Framework-and-Important-Utilities
mvn -DskipTests package
```

## Run the sample

The sample class is `src/test/java/Testing.java`. The POM does not set a Surefire suite file, so run TestNG on that class:

```bash
mvn test -Dtest=Testing
```

DB, SSH, and API samples fail until `Connection.properties` points at a real environment.

# Points Tracker Service

This is a Java Spring Boot service that manages transactions and points for different payers. The service provides endpoints to add transactions, spend points, and check the balance of points for different payers. This README contains instructions for setting up, compiling, and running the service, as well as details about its usage. Please follow the steps carefully, especially if you're new to Java or Spring Boot.

## Prerequisites

To compile and run this code, you need the following installed on your machine:

- **Java Development Kit (JDK) 17**: This project uses Java 17. You can install it from [Oracle](https://www.oracle.com/java/technologies/javase-downloads.html) or use a package manager such as [SDKMAN](https://sdkman.io/).
- **Maven**: Maven is used to manage dependencies and build the project. You can download it from [Apache Maven](https://maven.apache.org/download.cgi) or install it via a package manager such as Homebrew (`brew install maven`).

### Verify Installation

To verify that Java and Maven are installed, run:

```sh
java -version
mvn -v
```

You should see the installed versions of Java and Maven.

## Project Setup

### 1. Clone the Repository

Start by cloning the repository to your local machine:

```sh
git clone https://github.com/shreyk2/points-tracker.git
cd points-tracker
```

### 2. Build the Project

To compile the project and download all dependencies, use the following Maven command:

```sh
mvn clean install
```

This command will compile the Java code and download all necessary dependencies as specified in the `pom.xml` file.

### 3. Run the Application

To start the Spring Boot application, use the following command:

```sh
mvn spring-boot:run
```

Alternatively, you can run the compiled JAR file:

```sh
java -jar target/points-tracker-0.0.1-SNAPSHOT.jar
```

The application should now be running on **http://localhost:8080**.

## Code Breakdown

All information about the code and it's functionalities can be found within the code, 
and in `src/main/java/com.fetch.points_tracker/README.md`

## API Documentation

This service exposes three main endpoints:

### 1. Add Points

- **URL**: `/add`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "payer": "DANNON",
    "points": 300,
    "timestamp": "2022-10-31T10:00:00Z"
  }
  ```
- **Description**: Adds a new transaction for a payer. The timestamp must be in ISO-8601 format.

### 2. Spend Points

- **URL**: `/spend`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "points": 5000
  }
  ```
- **Description**: Spends the specified number of points across all payers in a "first-in, first-out" order.

- **Response Example**:
  ```json
  [
    { "payer": "DANNON", "points": -300 },
    { "payer": "UNILEVER", "points": -200 },
    { "payer": "MILLER COORS", "points": -4500 }
  ]
  ```

### 3. Get Balances

- **URL**: `/balance`
- **Method**: `GET`
- **Description**: Retrieves the current points balance for each payer.

- **Response Example**:
  ```json
  {
    "DANNON": 1000,
    "UNILEVER": 0,
    "MILLER COORS": 5300
  }
  ```

## Testing the Application

### Using Postman

You can use [Postman](https://www.postman.com/downloads/) to test the API endpoints:
1. **Add Transaction**: Create a `POST` request to `http://localhost:8080/add` with a JSON body to add transactions.
2. **Spend Points**: Create a `POST` request to `http://localhost:8080/spend` with a JSON body to spend points.
3. **Get Balances**: Create a `GET` request to `http://localhost:8080/balance` to retrieve current balances.

### Running Unit Tests

Unit tests are included to verify the core functionality of the service. To run the tests, execute:

```sh
mvn test
```

This will run all tests defined in the project and provide a summary of the results.

## Common Issues

### 1. Port Already in Use
If you get an error indicating the port is already in use, modify the default port in `src/main/resources/application.properties` by adding:
```properties
server.port=8081
```

### 2. Dependency Issues
If Maven fails to resolve dependencies, try updating the project:
```sh
mvn clean install -U
```

This will force update all dependencies.

## Additional Resources
- [Spring Boot Documentation](https://spring.io/guides/gs/spring-boot/)
- [Java 17 Documentation](https://docs.oracle.com/en/java/javase/17/)
- [Maven Documentation](https://maven.apache.org/guides/index.html)

## Author
This project is maintained by **Shrey K**. For questions or feedback, please feel free to reach out via email.

---

Thank you for using the Points Tracker Service! If you encounter any issues, please open an issue on the [GitHub repository](https://github.com/shreyk2/points-tracker).


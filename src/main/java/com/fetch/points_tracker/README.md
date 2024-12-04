# Points Tracker Service

This project is a Java Spring Boot application that manages transactions for different payers, allowing you to add points, spend points, and retrieve current balances. It follows a modular architecture consisting of controller, service, repository, model, and exception classes to keep the code organized and maintainable.

## Project Structure

The project is organized into different packages, each serving a distinct purpose:

```
points-tracker/
├── src/
│   ├── main/
│   │   ├── java/com/fetch/points_tracker/
│   │   │   ├── controller/          # Handles HTTP requests
│   │   │   │   └── PointsController.java
│   │   │   ├── exceptions/          # Defines custom exceptions
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── InsufficientPointsException.java
│   │   │   │   └── InvalidTransactionException.java
│   │   │   ├── model/               # Domain model classes
│   │   │   │   └── Transaction.java
│   │   │   ├── repository/          # Handles data operations
│   │   │   │   ├── InMemoryPointsRepository.java
│   │   │   │   └── PointsRepository.java
│   │   │   └── service/             # Business logic
│   │   │       └── PointsService.java
│   └── resources/                   # Configuration files
│       └── application.properties
└── pom.xml                          # Project dependencies and build configuration
```

### 1. **Controller**
The `PointsController` class handles incoming HTTP requests and provides endpoints for adding points, spending points, and retrieving balances.

- **Endpoints**:
    - **`/add`** (POST): Adds points for a given payer.
    - **`/spend`** (POST): Spends the specified number of points across all payers.
    - **`/balance`** (GET): Retrieves the current points balance for all payers.

### 2. **Service**
The `PointsService` class contains the business logic for handling transactions, adding points, spending points, and getting balances.

- **Methods**:
    - **`addPoints(Transaction transaction)`**: Adds a new transaction after validating the input.
    - **`spendPoints(int points)`**: Deducts points across multiple payers following a first-in, first-out (FIFO) approach.
    - **`getBalances()`**: Retrieves the balance for each payer.

### 3. **Repository**
The repository is responsible for storing and managing transaction data. This project uses an in-memory repository (`InMemoryPointsRepository`) to store data without using a database.

- **Classes**:
    - **`InMemoryPointsRepository`**: Implements data storage operations, including saving, deleting, and retrieving transactions.
    - **`PointsRepository` (interface)**: Defines methods to be implemented by the repository, such as `saveTransaction()`, `getAllTransactions()`, and `updateBalance()`.

### 4. **Model**
The model layer contains domain-specific objects that represent data.

- **`Transaction`**: Represents a transaction that includes details like `payer`, `points`, and `timestamp`. The `Transaction` class has getter and setter methods to manage these fields.

### 5. **Exceptions**
Custom exceptions are used to handle specific errors that might occur during execution.

- **Classes**:
    - **`InvalidTransactionException`**: Thrown when a transaction is invalid, such as when required fields are missing.
    - **`InsufficientPointsException`**: Thrown when there are not enough points available to spend.
    - **`GlobalExceptionHandler`**: A global exception handler that catches and handles exceptions to ensure appropriate error messages are returned to clients.

    - **Handlers**:
        - **`handleInvalidTransaction()`**: Handles `InvalidTransactionException` and returns an error response with a status of `400 Bad Request`.
        - **`handleInsufficientPoints()`**: Handles `InsufficientPointsException` and returns an error response with a status of `400 Bad Request`.
        - **`handleGeneralException()`**: Catches any other unhandled exceptions and returns a `500 Internal Server Error` response.

## Process Flow Diagram

Below is a simplified diagram describing the workflow of the Points Tracker Service:

```
+--------------+       +--------------+       +---------------+       +---------------+
|  HTTP Client | ----> |  Controller  | ----> |   Service     | ----> |   Repository  |
|              | <---- |              | <---- |               | <---- |               |
+--------------+       +--------------+       +---------------+       +---------------+
    ^  ^                  ^    ^                    ^   ^                   ^   ^
    |  |                  |    |                    |   |                   |   |
    |  |                  |    |                    |   |                   |   |
    |  |                  |    |                    |   |                   |   |
  Add, Spend,       Handles HTTP           Contains business     Stores & manages
  or Get Balance    requests and           logic for adding,     transactions and
  Requests          responses              spending, and         balances in-memory
                                            fetching balances
```

- **HTTP Client**: Represents the user or tool making requests to the service.
- **Controller**: Receives HTTP requests, processes input, and delegates actions to the service layer.
- **Service**: Contains the core business logic to handle requests related to adding, spending, or retrieving points.
- **Repository**: Handles data persistence (using an in-memory store in this case) for transactions and payer balances.

This architecture ensures that different parts of the application have distinct responsibilities, making the code easy to understand, maintain, and extend.


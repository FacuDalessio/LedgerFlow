Below is the complete technical and functional definition of the project, written from the perspective of a corporate client or lead architect commissioning its development.

Client: FinTech Global Services Inc.
System Name: LedgerFlow - Enterprise Multi-Currency Transaction & Settlement Platform

# 1. Executive Summary and Business Problem
Our platform handles cross-border financial operations. We need a system capable of:

Receiving and validating financial transactions in real time through secure REST APIs.

Processing high-volume end-of-day reconciliation and settlement through batch files.

Providing a secure and interactive web portal where financial analysts can monitor transactions, audit fraud alerts, and generate reports.

# 2. Functional Requirements by Module
*Module A*: REST API Transaction Engine (Backend)

*Authentication and Authorization:*

Log in using JWT (JSON Web Tokens) with differentiated roles: ROLE_USER (Analyst) and ROLE_ADMIN (Auditor/Supervisor).

Apply rate limiting to sensitive endpoints using Redis (a maximum of 100 requests per minute per token).

Real-Time Transaction Management:

POST /api/v1/transactions endpoint: Allows payment orders in foreign currencies (USD, EUR, and ARS) to be registered.

Business-rule validation: verify that sufficient funds are available, confirm that the destination account is active, and proactively detect anomalous patterns (e.g., transactions greater than $10,000 USD require an audit flag).

*Hexagonal Architecture:*

The domain logic must remain framework-agnostic.

Inbound ports: REST controllers.

Outbound ports: persistence interfaces (JPA repositories) and external services.

*Module B:* Account CRUD
Implement CRUD operations for accounts following the hexagonal architecture and good practices. Each account must have an accountNumber. Then complete the entity with sensible additional attributes.

*Module C:* Get exchange rate

Develop the API call following the hexagonal architecture. Use RestClient.

Example of the request: 
GET https://api.frankfurter.dev/v2/rate/USD/EUR

response:
{
    "date": "2026-08-11",
    "base": "USD",
    "quote": "EUR",
    "rate": 0.8653
}

*Module D:* Batch Settlement & Processing Engine (Spring Batch)
Bulk Processing:

Daily settlement job (settlementJob) configured with Spring Batch.

It must read a CSV or JSON file containing up to 100,000 daily transaction records provided by an external partner.

This can be invoked through an endpoint, for example, POST /api/v1/settlements/trigger,
or it can be invoked by a cron job.

*Example of the file:*
external_tx_id,source_account,destination_account,amount,currency,fee_amount,timestamp,merchant_code
TX10098231,ACC-88123,ACC-99411,150.00,USD,2.25,2026-08-11T03:15:00Z,MERCH-AMAZON
TX10098232,ACC-11204,ACC-99411,12500.00,EUR,187.50,2026-08-11T03:18:12Z,MERCH-BINANCE
TX10098233,ACC-88123,ACC-44102,-50.00,USD,0.75,2026-08-11T03:20:00Z,MERCH-UBER
TX10098231,ACC-88123,ACC-99411,150.00,USD,2.25,2026-08-11T03:15:00Z,MERCH-AMAZON
CORRUPT_ROW_DATA_WITHOUT_COMMAS

*Validation:*
Nothing can be null.
The amount must be greater than 0.
The fee amount cannot be negative.
The source account and destination account must exist.

*Processing*
The fee_amount must be 1% of the amount; otherwise, set state = FEE_DISCREPANCY.
If the currency is different from USD, the system needs to obtain the exchange_rate and calculate the amount in USD (the same applies to the fee). Use the API call developed in Module C.
If the amount is greater than or equal to 10,000, set state = SETTLED_PENDING_AUDIT; otherwise, set state = SETTLED_APPROVED.

*Fault Tolerance and Performance:*

Implement chunk-oriented processing with a batch size of 1,000 records.

Use retry strategies (up to three retries for database connection failures) and skip strategies (skip records with corrupted data formats by writing them to an error file named failed_transactions.log).

*Module E:* Audit


*Module F:* Financial Web Portal (Angular 17+)
Authentication and Interceptors:

Route Guards to restrict views according to the JWT role.

HTTP interceptor to automatically attach the Bearer token and handle 401/403 errors by redirecting to the login page.

Operational Transaction Dashboard:

Main view built with standalone components, with state managed using Signals/RxJS.

Paginated table with dynamic, real-time filtering by date range, transaction status (PENDING/APPROVED/REJECTED), and account ID.

Interactive charts showing daily transaction volume (using Chart.js or Ngx-charts).

Reactive form with advanced validation for manually creating transactions.

# 3. Non-Functional Requirements and Technology Quality
**Persistence and migrations:** PostgreSQL database. Schema version control using Liquibase or Flyway.

**Code quality and testing:**

Unit test coverage above 80% using JUnit 5 and Mockito on the backend.

Component and service test coverage on the frontend using Jasmine/Karma or Jest.

**Containerization and DevOps:**

Complete orchestration through docker-compose.yml. When `docker compose up` is executed, the Java backend, Angular frontend, PostgreSQL database, and Redis instance must start without manual intervention.

CI/CD pipeline configured in .github/workflows/ci-cd.yml that automatically executes: Build -> Run unit tests -> SonarQube code analysis.

# 4. Repository Structure
Ensure that the root of the GitHub project maintains the following structure:

ledgerflow/
├── .github/
│   └── workflows/
│       └── ci-cd.yml
├── ledgerflow-backend/
│   ├── src/
│   │   ├── main/java/com/fintech/ledgerflow/
│   │   │   ├── domain/           # Core domain models & business rules
│   │   │   ├── application/      # Use cases / Application services
│   │   │   └── infrastructure/   # Adapters (REST API, JPA Entities, Spring Batch)
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
├── ledgerflow-frontend/
│   ├── src/app/
│   │   ├── core/                 # Guards, Interceptors, Services
│   │   ├── features/             # Dashboard, Transactions, Auth components
│   │   └── shared/               # Reusable UI components
│   ├── Dockerfile
│   └── package.json
├── docker-compose.yml
└── README.md

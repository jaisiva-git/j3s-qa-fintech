# J3S Fintech Test Suites (API + UI)
Java 17 • Maven • Spring Boot • Rest Assured • Selenium • Cucumber

## Prerequisites
- Start the backend from the earlier sample (defaults to http://localhost:8080):
  ```bash
  cd ../j3s-fintech-app/backend
  mvn spring-boot:run
  ```

## Run tests
```bash
mvn -Dtests.backendBaseUrl=http://localhost:5173 test
```

Reports:
- Cucumber HTML: `target/cucumber-report.html`
- JUnit XML: `target/cucumber-junit.xml`

## What these tests cover
1. **API Test Suite**
   - CRUD-style coverage (create + list) for `/api/users`, plus transaction creation `/api/transactions`
   - Error scenarios: invalid email (validation 400), insufficient funds (400)
   - Data validation checks (presence of fields)
   - Auth placeholder checks (backend has no auth; tests document current behavior)

2. **UI Test Suite (mock frontend)**
   - A small Spring Boot server hosts `index.html` with the same `data-testid` hooks as the React UI
   - **User registration flow**: fills and submits the form, verifies success message
   - **Transaction flow**: creates users via API, then submits transfer in UI
   - **Error message validation**: attempts invalid email, expects error text

## Notes
- Override the backend URL with `-Dtests.backendBaseUrl=...` if your app runs elsewhere.
- Headless Chrome is used via WebDriverManager.
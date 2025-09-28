# Fintech Backend (Spring Boot, Java 17, Maven)

## Endpoints
- **POST** `/api/users` – create user  
  Body:
  ```json
  {
    "name": "John Doe",
    "email": "john@example.com",
    "accountType": "premium"
  }
  ```
- **GET** `/api/users` – list users

- **POST** `/api/transactions` – create transaction (simulated transfer)  
  Body:
  ```json
  {
    "userId": "123",
    "amount": 100.50,
    "type": "transfer",
    "recipientId": "456"
  }
  ```
- **GET** `/api/transactions` – list transactions

### Run
```bash
mvn spring-boot:run
```
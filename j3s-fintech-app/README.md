## J3S Fintech App (Spring Boot + React)

**Backend (Java 17, Maven, Spring Boot)** at `backend/`  
**Frontend (React + Vite)** at `frontend/`

## Quick Start
1) Backend:
```bash
cd backend
mvn spring-boot:run
```

2) Frontend:
```bash
cd ../frontend
npm install
npm run dev
```

Open http://localhost:5173

## REST APIs

**Create User**
POST `/api/users`
```json
{
  "name": "John Doe",
  "email": "john@j3s.com",
  "accountType": "premium"
}
```
cl
**Create Transaction**
POST `/api/transactions`
```json
{
  "userId": "123",
  "amount": 100.50,
  "type": "transfer",
  "recipientId": "456"
}
```

Additional helpers:
- GET `/api/users`
- GET `/api/transactions`

Notes:
- Users start with a default balance of 1000.00 for simple transfer simulation.
- UI exposes `data-testid` hooks for easy automation.
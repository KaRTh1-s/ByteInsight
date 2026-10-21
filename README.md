# ByteInsight — Nutrition Intelligence Platform

ByteInsight is a full-stack web application for tracking nutrition, managing meals, and monitoring dietary goals. It combines a React frontend with a Spring Boot backend and MySQL database to provide personalized nutrition information and meal management tools.

**Live Demo:** https://vibrant-laughter-production-0250.up.railway.app

## Features

- **User Authentication:** Account registration and login using JWT-based authentication.
- **Personalized Nutrition:** Calculate estimated daily calorie requirements using profile information and activity levels.
- **Food Analysis:** Search food items and review nutritional values, including calories, protein, carbohydrates, fats, and selected micronutrients.
- **Meal Journal:** Record, review, and manage daily food intake.
- **Nutrition Dashboard:** View calorie and macronutrient summaries alongside nutrition trends.
- **Dietary Goals:** Configure nutrition targets and track progress.
- **Meal Planning:** Organize planned meals and manage recipes.
- **Shopping List:** Maintain a list of ingredients and food items for planned meals.
- **Dietary Preferences:** Support dietary tags and allergy-related food filtering.

## Tech Stack

| Layer | Technologies |
|---|---|
| Frontend | React, JavaScript, HTML, CSS |
| Backend | Java 21, Spring Boot 3, Spring Security |
| Database | MySQL |
| Authentication | JWT, BCrypt |
| Build Tools | npm, Maven |
| Deployment | Railway |

## Architecture

```text
React Frontend
      |
      | REST API over HTTPS
      v
Spring Boot Backend
      |
      | JPA / Hibernate
      v
MySQL Database
```

The frontend communicates with the backend through REST endpoints. The backend handles authentication, nutrition calculations, meal management, and database operations.

## Project Structure

```text
ByteInsight/
├── nutrition-frontend/
│   ├── public/
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   └── services/
│   ├── package.json
│   └── .env.example
│
├── nutrition-backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   ├── Dockerfile
│   ├── pom.xml
│   └── .env.example
│
└── README.md
```

## Getting Started

### Prerequisites

Install the following before running the project locally:

- Java 21
- Node.js and npm
- MySQL 8 or a compatible MySQL version

### 1. Clone the Repository

```bash
git clone https://github.com/KaRTh1-s/ByteInsight.git
cd ByteInsight
```

### 2. Configure the Database

Create a MySQL database:

```sql
CREATE DATABASE nutritiondb;
```

Configure the backend environment variables for your local database connection.

### 3. Configure the Backend

Navigate to the backend directory:

```bash
cd nutrition-backend
```

Set the required environment variables before starting the application:

```text
DB_URL=jdbc:mysql://localhost:3306/nutritiondb?serverTimezone=UTC
DB_USERNAME=your_mysql_username
DB_PASSWORD=your_mysql_password
JWT_SECRET=your_secure_random_secret
JWT_EXPIRATION_MS=86400000
```

Optional integrations can be configured using the variable names documented in `nutrition-backend/.env.example`.

Start the backend on Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

The backend uses port `8080` by default unless configured otherwise.

### 4. Configure the Frontend

Open a separate terminal:

```bash
cd nutrition-frontend
```

Create a local `.env` file based on `.env.example` and configure the backend API URL:

```text
REACT_APP_API_URL=http://localhost:8080/api
```

Install dependencies and start the frontend:

```bash
npm install
npm start
```

The React development server typically runs at `http://localhost:3000`.

## Testing

Run the backend tests from `nutrition-backend`:

```powershell
.\mvnw.cmd test
```

Run the frontend tests from `nutrition-frontend`:

```bash
npm test -- --watchAll=false
```

Create a frontend production build:

```bash
npm run build
```

## Deployment

ByteInsight is deployed using Railway, with separate services for the frontend, backend, and MySQL database.

For deployment:

1. Configure the backend's database connection and JWT secret through Railway environment variables.
2. Set the backend's allowed CORS origin to the deployed frontend domain.
3. Configure `REACT_APP_API_URL` with the public backend API URL, including `/api`, before building the frontend.
4. Deploy each service and verify authentication, API requests, and database persistence.

Do not commit production credentials or secret environment files to the repository.

## Security Notes

- Store database credentials, JWT signing keys, and external API keys in environment variables.
- Use a strong, randomly generated JWT secret in production.
- Keep `.env` files out of version control.
- Use HTTPS for production traffic.
- Restrict CORS to trusted frontend origins.


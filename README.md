🍎 ByteInsight — Smart Nutrition Tracker
A full-stack nutrition tracking app built using React (frontend) and Spring Boot (backend) with MySQL as the database. Users can register, log in, and fetch nutrition information for any food item using a live Nutrition API.

🚀 Features:
🔐 User Registration and Login (Spring Boot + MySQL).
🧠 Password strength validation. 
⚙️ RESTful APIs for authentication and nutrition data. 
🍽️ Nutrition data fetched from an external API.
🎨 Modern UI built with React and custom CSS.
🔄 Seamless integration between frontend and backend via Axios

🧩 Folder Structure:
ByteInsight/ 
│ 
├── nutrition-frontend/ # React frontend 
│ ├── src/ 
│ ├── public/ 
│ └── package.json 
│
├── nutrition-backend/ # Spring Boot backend
│ ├── src/main/java/com/example/nutrition/
│ ├── src/main/resources/
│ └── pom.xml 
│ └── README.md # Project setup guide

🛠️ Requirements:
Make sure you have the following installed: Node.js v16 or higher npm v8 or higher Java JDK 17 or higher Maven 3.8+ MySQL 8.0+

1️⃣ Configure MySQL:
Create a new database: sql CREATE DATABASE nutritiondb;

2️⃣ Configure Application Properties:
Edit nutrition-backend/src/main/resources/application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/nutritiondb spring.datasource.username=YOUR_USERNAME spring.datasource.password=YOUR_PASSWORD spring.jpa.hibernate.ddl-auto=update spring.jpa.show-sql=true server.port=8080

3️⃣ Run Backend
cd nutrition-backend mvn spring-boot:run Backend runs at: http://localhost:8080

✅ API Endpoints:
Method Endpoint Description POST /api/auth/register Register a new user POST /api/auth/login Login existing user GET /api/nutrition?query=apple Fetch nutrition data for a food item

💻 Frontend Setup (nutrition-frontend):
1️⃣ Install Dependencies
cd nutrition-frontend npm install

2️⃣ Run React App
npm start Frontend runs at: http://localhost:3000

🔗 Connecting Frontend and Backend
Ensure both servers are running:

--> Spring Boot: http://localhost:8080

--> React App: http://localhost:3000

CORS is already enabled in Spring Boot, so frontend requests are allowed. Visit http://localhost:3000 to: 🧍 Register a new user 🔑 Login 🍏 Search for food items and view nutrition info

👨‍💻 Author Karthikeyan S 📧 karthi.techprodigy@gmail.com 🚀 GitHub: https://github.com/KaRTh1-s

📜 License : You’re free to use and modify this project for educational or personal purposes.

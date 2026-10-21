# ByteInsight — Personalized Full-Stack Nutrition Intelligence Platform

> An evidence-based, deterministic nutrition decision-support and dietary tracking ecosystem engineered with Spring Boot 3, React 19, and MySQL.

---

## 1. Product Overview & Key Differentiation

ByteInsight is not just a food calorie calculator or a generic AI chatbot that answers questions about nutrition. It is a **personalized nutrition intelligence and meal decision-support platform** that bridges food chemistry, clinical dietary guidelines (AMDR, DRI, Mifflin-St Jeor), and real-world grocery constraints into deterministic, explainable insights.

### What Makes ByteInsight Different?

| Conventional Nutrition Apps | ByteInsight Nutrition Intelligence |
|:---|:---|
| Flat calorie sums with no metabolic context | **Deterministic BMR & TDEE Baselines** via Mifflin-St Jeor based on user biometrics and activity coefficients |
| Black-box AI estimates that hallucinate calories | **Traceable Nutrition Pipeline** prioritizing curated verified databases, local caching, and explicit data provenance |
| One-size-fits-all generic nutrient targets | **Personalized AMDR Macro Envelopes** (Carbs 45–65%, Protein 10–35%, Fat 20–35%) aligned with user weight goals |
| Isolated single-meal judgment | **Multi-Day Dietary Trajectory** tracking stability over rolling 7-day intake periods |
| "Good vs. Bad" shaming health scores | **0–100 Meal Quality & Balance Score** calculated from documented sub-factors (macro ratios, fiber density, sodium threshold) |
| Impractical or expensive food suggestions | **Nutrient & Budget Substitution Engine** with explicit trade-off metrics (e.g. Quinoa vs White Rice, Lentils vs Ground Beef) |
| Separate disjointed grocery list | **Direct Planner-to-Pantry Synchronization** automatically compiling weekly meal plans into aisle-grouped shopping lists |
| Exaggerated medical diagnosis claims | **Non-Clinical Decision Support** that clearly separates recorded estimates from clinical diagnosis |

---

## 2. Platform Architecture & Modules

```
                           +---------------------------+
                           |  React 19 SPA (Client)    |
                           |  - Responsive Design Sys  |
                           |  - Dark / Light Themes    |
                           |  - Axios JWT Interceptor  |
                           +-------------+-------------+
                                         | REST / JSON
                                         v
                      +--------------------------------------+
                      |  Spring Boot 3.5 Backend (Port 8080) |
                      +--------------------------------------+
                      |  [Security]                          |
                      |  - JWT Filter + Custom UserDetails   |
                      |  - BCrypt Password Hashing           |
                      +--------------------------------------+
                      |  [Intelligence Layer]                |
                      |  - NutritionIntelligenceService      |
                      |    (Mifflin-St Jeor BMR/TDEE, AMDR)  |
                      |    (Meal Balance Scoring 0-100)      |
                      |    (Explainable Gap Insights)        |
                      |  - FoodVisionService                 |
                      |    (File validation & candidates)    |
                      +--------------------------------------+
                      |  [Service & Persistence Layer]       |
                      |  - FoodService (Verified DB + Cache) |
                      |  - MealJournalService (CRUD + Dupl)  |
                      |  - MealPlannerService (Allergen-safe)|
                      |  - ShoppingList & Goals Services     |
                      +--------------------------------------+
                                         |
                                         v
                         +--------------------------------+
                         |       MySQL 8.0 Database       |
                         |  (nutritiondb on Port 3306)    |
                         +--------------------------------+
```

### Module Breakdown

1. **Dashboard (`/dashboard`)**:
   - Interactive calorie gauge (recorded vs. Mifflin-St Jeor TDEE target).
   - Real-time AMDR progress bars for Protein, Carbohydrates, and Fat.
   - Evidence-based Meal Quality & Balance score (0–100).
   - 7-day historical intake trajectory chart.
   - Today's logged meals with deletion and fast-add controls.
   - Non-diagnostic actionable nutrition guidance.

2. **Food Analyzer (`/food-analyzer`)**:
   - Multi-ingredient meal builder with real-time portion scaling and custom units.
   - Instant autocomplete across 40+ verified whole foods and staples.
   - Detailed micronutrient breakdown table (fiber, sugars, sodium, potassium, calcium, iron).
   - Modular Food Image Analysis workflow: validates MIME type and file size (≤ 5MB), identifies candidate foods with confidence scoring, and requires user portion confirmation.
   - Instant "Save Meal to Journal" integration.

3. **Meal Journal (`/journal`)**:
   - Date-navigated food diary categorized into Breakfast, Lunch, Dinner, and Snacks.
   - Real-time recalculation of meal and daily totals.
   - Single-click Day Duplication feature for consistent dietary patterns.
   - Individual item and entire meal category deletion.

4. **Nutrition Insights (`/insights`)**:
   - Scientific Acceptable Macronutrient Distribution Range (AMDR) compliance tracking.
   - Micronutrient threshold analysis: fiber density goals, sodium reference limits (2,300 mg/day), and potassium balance.
   - Contextual, non-diagnostic guidance messages.

5. **Personalized Meal Planner (`/planner`)**:
   - Deterministic 7-day meal plan generator based on calorie goals, dietary preference (Vegetarian, Vegan, Non-Vegetarian), and declared allergens (Peanuts, Gluten, Dairy, Shellfish, etc.).
   - Daily breakfast, lunch, dinner, and snack suggestions with macro totals and estimated meal costs.
   - One-click export to Grocery Shopping List.

6. **Recipes & Food Substitutions (`/recipes`)**:
   - Chef-curated nutrient-dense recipes with preparation time, instructions, and macro breakdown.
   - Interactive Food Substitution Engine comparing nutrient trade-offs, calorie deltas, fiber changes, and cost differences.

7. **Budget-Aware Grocery List (`/shopping-list`)**:
   - Aisle-grouped grocery inventory (Produce, Dairy, Bakery & Grains, Proteins, Pantry).
   - Live budget tracker: daily food budget allocation vs. weekly grocery cart total.
   - Check off purchased items with instant total recalculation.
   - Curated high-yield affordable nutrient guide.

8. **Goals & Progress Tracking (`/goals`)**:
   - Multi-day calorie target adherence metrics.
   - Active logging consistency streak counter.
   - Optional, user-directed weight progression log with timeline history.

9. **Profile & Dietary Settings (`/profile`)**:
   - Biometric inputs: Age, Sex, Height, Weight, Activity Level.
   - Goal configuration: Weight Loss (-500 kcal), Muscle Gain (+350 kcal), Maintenance, or General Balance.
   - Transparent display of Mifflin-St Jeor formula and Physical Activity Levels (PAL).
   - Allergen checklist and dietary restriction preferences.

---

## 3. Technology Stack

### Backend
- **Framework**: Spring Boot 3.5.6 (Java 21)
- **Security**: Spring Security 6, JJWT (io.jsonwebtoken 0.12.6), BCrypt Password Encoder
- **Persistence**: Spring Data JPA, Hibernate 6.6, HikariCP
- **Database**: MySQL 8.0 (with H2 in-memory profile configured in `src/test/resources` for hermetic test execution)
- **Validation**: Jakarta Bean Validation (`spring-boot-starter-validation`)
- **Testing**: JUnit 5, Mockito, Spring Boot Test

### Frontend
- **Framework**: React 19 (SPA with React Router v7)
- **Design System**: Vanilla CSS Design Tokens (Dark and Light themes, glassmorphism, responsive grid layouts)
- **Iconography**: Lucide React
- **HTTP Client**: Axios with automated Bearer Token interceptors and centralized error handling
- **Testing**: Jest, React Testing Library, TextEncoder/TextDecoder polyfills

---

## 4. Local Development Setup

### Prerequisites
- **Java Development Kit**: JDK 21+
- **Node.js**: v18+ & npm v9+
- **MySQL Server**: 8.0+ running on `localhost:3306`

### Database Setup
Log into your local MySQL instance and create the database:
```sql
CREATE DATABASE IF NOT EXISTS nutritiondb;
```

---

### Backend Local Run (`nutrition-backend`)

1. Navigate to the backend directory:
   ```powershell
   cd nutrition-backend
   ```

2. Configure local environment variables:
   Create a local `.env` or set shell variables. For local testing, ensure `JWT_SECRET` is defined:
   ```powershell
   $env:JWT_SECRET="YourLocalDevelopmentSecretKeyMustBeAtLeast256BitsLong2026!"
   $env:DB_URL="jdbc:mysql://localhost:3306/nutritiondb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
   $env:DB_USERNAME="root"
   $env:DB_PASSWORD="root"
   ```

3. Run automated tests (runs hermetically with H2 database):
   ```powershell
   $env:JAVA_HOME="<path-to-jdk-21>"; .\mvnw.cmd test
   ```

4. Build production jar:
   ```powershell
   $env:JAVA_HOME="<path-to-jdk-21>"; .\mvnw.cmd package -DskipTests
   ```
   *Artifact generated: `target/nutrition.jar`*

5. Start the backend:
   ```powershell
   $env:JAVA_HOME="<path-to-jdk-21>"; .\mvnw.cmd spring-boot:run
   ```
   *Backend runs on `http://localhost:8080`.*

---

### Frontend Local Run (`nutrition-frontend`)

1. Navigate to the frontend directory:
   ```powershell
   cd nutrition-frontend
   ```

2. Install dependencies:
   ```powershell
   npm install
   ```

3. Run unit tests:
   ```powershell
   npm test -- --watchAll=false
   ```

4. Build production bundle:
   ```powershell
   npm run build
   ```

5. Start local development server:
   ```powershell
   npm start
   ```
   *Frontend opens at `http://localhost:3000`.*

---

## 5. Railway Deployment Guide

This section covers deploying the MySQL database, Spring Boot backend, and React frontend to **Railway**.

### Step 1: Deploy MySQL Database on Railway
1. In your Railway project, click **+ New** > **Database** > **Add MySQL**.
2. Railway provisions a managed MySQL instance.
3. Ensure the service name is `MySQL` (default). Keep the database inside Railway's private network.

---

### Step 2: Deploy Spring Boot Backend on Railway
1. In the same Railway project, click **+ New** > **GitHub Repo** > Select your repository.
2. In the service settings:
   - **Root Directory**: `nutrition-backend`
   - **Build**: Railway will automatically detect the multi-stage `Dockerfile` (or use Nixpacks with Java 21).
3. In the **Variables** tab of the backend service, configure:

| Variable Name | Value (Reference or Custom) | Notes |
|:---|:---|:---|
| `PORT` | *(Leave empty - Railway injects automatically)* | Default is `8080` |
| `DB_URL` | `jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}?serverTimezone=UTC` | Railway private network reference |
| `DB_USERNAME` | `${{MySQL.MYSQLUSER}}` | Railway MySQL user reference |
| `DB_PASSWORD` | `${{MySQL.MYSQLPASSWORD}}` | Railway MySQL password reference |
| `DDL_AUTO` | `update` | Use `update` initially; switch to `validate` later |
| `JWT_SECRET` | *(Generate a 64+ char random string)* | **REQUIRED**: E.g. generate via `openssl rand -base64 64` |
| `JWT_EXPIRATION_MS` | `86400000` | 24 hours in milliseconds |
| `CORS_ALLOWED_ORIGINS` | `https://your-frontend.up.railway.app,http://localhost:3000` | Restricts CORS to your deployed frontend domain |
| `NUTRITION_API_KEY` | *(Optional)* | CalorieNinjas API key (leave blank to use verified DB) |
| `USDA_API_KEY` | *(Optional)* | USDA FoodData Central key |

> **Important on Railway Variable References**: `${{MySQL.MYSQLHOST}}` is Railway's template syntax for private networking. Do not write these literally into `application.properties`; enter them in Railway's Web UI under the **Variables** tab.

4. Click **Deploy**. The backend container will build using `Dockerfile`, connect to MySQL over Railway private networking, initialize the seed data, and start listening on `$PORT`.
5. Under **Settings** > **Networking**, click **Generate Domain** to get your public backend URL (e.g. `https://nutrition-backend-production.up.railway.app`).

---

### Step 3: Deploy React Frontend on Railway (or Vercel / Netlify)
1. Add a new service from your GitHub repo.
2. Set **Root Directory** to `nutrition-frontend`.
3. In the **Variables** tab, set:

| Variable Name | Value | Notes |
|:---|:---|:---|
| `REACT_APP_API_URL` | `https://nutrition-backend-production.up.railway.app/api` | **Must be set before building!** |

> **Important on Build-Time Variables**: In Create React App, variables prefixed with `REACT_APP_` are embedded into static HTML/JS bundles at **build time**. If you update `REACT_APP_API_URL`, you must trigger a redeploy/rebuild.

4. Once the frontend is deployed and has a public domain (e.g., `https://nutrition-frontend-production.up.railway.app`), copy that domain and update `CORS_ALLOWED_ORIGINS` in your backend Railway variables.

---

## 6. Environment Variable Reference Matrix

| Variable | Required? | Default | Scope | Description |
|:---|:---|:---|:---|:---|
| `PORT` | Auto (Railway) | `8080` | Backend | Port bound by embedded Tomcat |
| `DB_URL` | **Yes** | `jdbc:mysql://localhost:3306/nutritiondb...` | Backend | JDBC connection URL to MySQL |
| `DB_USERNAME` | **Yes** | `root` | Backend | Database user |
| `DB_PASSWORD` | **Yes** | `root` | Backend | Database password |
| `JWT_SECRET` | **Yes (Prod)** | *(None in main config)* | Backend | Minimum 256-bit signing key for JWT |
| `JWT_EXPIRATION_MS` | No | `86400000` (24h) | Backend | JWT validity window |
| `DDL_AUTO` | No | `update` | Backend | Hibernate schema management mode |
| `SHOW_SQL` | No | `false` | Backend | SQL query logging toggle |
| `CORS_ALLOWED_ORIGINS`| No | `http://localhost:3000,http://127.0.0.1:3000` | Backend | Comma-separated allowed frontend origins |
| `NUTRITION_API_KEY` | No | *(Empty)* | Backend | Optional CalorieNinjas API Key |
| `USDA_API_KEY` | No | *(Empty)* | Backend | Optional USDA API Key |
| `REACT_APP_API_URL` | **Yes (Prod)** | `http://localhost:8080/api` | Frontend | Backend API base URL (build-time) |

---

## 7. DevSecOps & Credential Rotation Advisory

> [!WARNING]
> **Credential Rotation Alert**:
> If any API key (such as CalorieNinjas) or development JWT secret was committed to a public Git history in previous commits, **removing it from the latest code does not delete it from historical commits**.
> 1. Immediately log into your [CalorieNinjas Dashboard](https://calorieninjas.com/) and rotate / generate a new API key.
> 2. Always generate a unique, cryptographically random `JWT_SECRET` in your Railway Variables tab (e.g. `openssl rand -base64 64`). Never reuse development secrets in production.
> 3. Never commit `.env` or files containing plain-text keys. Use the provided `.env.example` templates.

---

## 8. REST API Documentation

### Authentication & Profile
- `POST /api/auth/register` — Register a new account (`username`, `email`, `password`). Automatically initializes a personalized `UserProfile`.
- `POST /api/auth/login` — Authenticate and receive a JWT Bearer token.
- `GET /api/auth/me` — Retrieve the currently authenticated user identity.
- `GET /api/profile` — Fetch user physiological profile, targets, BMR, TDEE, and dietary preferences.
- `PUT /api/profile` — Update biometrics, activity level, dietary patterns, and allergens. Deterministically recalculates energy expenditure.

### Food & Nutrition
- `GET /api/foods/search?query={term}` — Autocomplete food search across verified database.
- `GET /api/foods/{id}` — Fetch detailed nutrient profile for a food item.
- `POST /api/foods/analyze` — Analyze a multi-item meal, returning total calories, macronutrients, micronutrients, AMDR distribution, and Meal Balance Score (0–100).
- `POST /api/foods/analyze-image` — Modular food image analysis accepting `multipart/form-data`.
- `POST /api/nutrition` — **Backward-compatible legacy endpoint** accepting `{ "query": "1 banana and 1 apple" }`.

### Meal Journal
- `GET /api/meals?date={YYYY-MM-DD}` — Retrieve all logged meals for a specific date with running totals.
- `POST /api/meals` — Add a food item or batch meal to the journal.
- `DELETE /api/meals/{mealId}/items/{itemId}` — Remove a single logged food item.
- `DELETE /api/meals/{mealId}` — Delete an entire meal category.
- `POST /api/meals/duplicate?sourceDate={YYYY-MM-DD}&targetDate={YYYY-MM-DD}` — Clone an entire day's meals.

### Dashboard & Insights
- `GET /api/dashboard/summary?date={YYYY-MM-DD}` — Daily dashboard intelligence: logged calories vs target, AMDR progress, balance score, 7-day trend, and actionable insights.

### Planner, Recipes & Shopping
- `GET /api/planner/current` — Fetch or auto-generate the current 7-day personalized meal plan.
- `POST /api/planner/generate` — Generate a fresh plan matching calorie targets, dietary preference, and allergen constraints.
- `POST /api/planner/to-shopping-list` — Convert meal plan items into an aisle-grouped grocery list.
- `GET /api/recipes?query={term}&category={cat}` — Discover chef-curated recipes with prep instructions and macros.
- `GET /api/shopping-list` — View user's grocery items and estimated total costs.
- `POST /api/shopping-list` — Add custom grocery items.
- `PUT /api/shopping-list/{id}/toggle` — Toggle item purchased status.
- `DELETE /api/shopping-list/clear-purchased` — Remove completed grocery items.
- `POST /api/goals/weight` — Log weight progress entry.

---

## 9. Scientific Formulas & Calculations

### 1. Basal Metabolic Rate (BMR) — Mifflin-St Jeor Equation
$$\text{BMR}_{\text{male}} = 10 \times \text{weight (kg)} + 6.25 \times \text{height (cm)} - 5 \times \text{age (yr)} + 5$$
$$\text{BMR}_{\text{female}} = 10 \times \text{weight (kg)} + 6.25 \times \text{height (cm)} - 5 \times \text{age (yr)} - 161$$

### 2. Total Daily Energy Expenditure (TDEE)
$$\text{TDEE} = \text{BMR} \times \text{Physical Activity Level (PAL)}$$
- Sedentary: $1.20$
- Lightly Active: $1.375$
- Moderately Active: $1.55$
- Very Active: $1.725$
- Extra Active: $1.90$

### 3. Meal Quality & Balance Score (0–100)
A documented multi-factor score composed of:
1. **Macronutrient Proportionality (40 pts)**: Penalizes divergence from AMDR guidelines (Protein 15–25%, Carbs 45–60%, Fat 20–35% of calories).
2. **Dietary Fiber Density (30 pts)**: Rewards ≥ 3.5g fiber per 400 kcal consumed.
3. **Sodium Threshold Control (20 pts)**: Penalizes meals exceeding 800 mg sodium per meal or 2,300 mg daily reference.
4. **Micronutrient Completeness (10 pts)**: Evaluates presence of essential minerals (potassium, calcium, iron).

---

## 10. Current Limitations & Planned Improvements

- **Local Price Estimation**: Food item pricing is based on representative national averages ($USD). Future releases will support user-selectable local currencies (INR, EUR, GBP) and grocery store API sync.
- **Computer Vision Provider**: Current vision module uses local candidate feature matching with honesty guards. Integration with proprietary edge or cloud models (e.g., Google Cloud Vision) can be plugged in via `FoodVisionService`.
- **Offline PWA Support**: Future iterations will package the React frontend as a Progressive Web App with offline IndexedDB meal caching.

---

## 11. License & Non-Diagnostic Disclaimer

ByteInsight is an educational and dietary lifestyle application. **It is not a clinical diagnostic medical device.** All calculated targets, nutrient values, and recommendations represent nutritional estimations intended to support informed personal meal decisions, not medical treatment. Always consult a licensed healthcare professional or registered dietitian for clinical dietary intervention.

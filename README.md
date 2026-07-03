<div align="center">

# 🌾 IQFarm

### *Intelligent Agriculture for African Smallholder Farmers*

[![Angular](https://img.shields.io/badge/Angular-19.2-DD0031?style=for-the-badge&logo=angular&logoColor=white)](https://angular.io/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.7-3178C6?style=for-the-badge&logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
[![TailwindCSS](https://img.shields.io/badge/Tailwind_CSS-4.3-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white)](https://tailwindcss.com/)

> **Challenge 4 — Agriculture & Food Security** | Empowering African farmers to improve productivity, resilience, and food security through intelligent, AI-driven decision support.

</div>

---

## 📋 Table of Contents

- [About the Project](#-about-the-project)
- [Challenge Context](#-challenge-context)
- [Key Features](#-key-features)
- [AI Capabilities](#-ai-capabilities)
- [System Architecture](#-system-architecture)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Backend Setup](#backend-setup)
  - [Frontend Setup](#frontend-setup)
- [Configuration](#-configuration)
- [Development Workflow](#-development-workflow)
- [African Context Considerations](#-african-context-considerations)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🌍 About the Project

**IQFarm** is an intelligent agricultural decision-support platform built specifically for African smallholder farmers. It bridges the critical information gap faced by millions of farmers across the continent by combining AI-driven insights, real-time weather data, market price intelligence, and crop advisory tools into a single, accessible platform.

The platform is designed with the African farming context at its core — accounting for limited connectivity, diverse crop varieties, local market dynamics, and the growing threat of climate variability.

---

## 🎯 Challenge Context

| Category | Details |
|---|---|
| **Challenge** | Agriculture & Food Security (#4) |
| **Problem** | Farmers face climate uncertainty, crop diseases, limited market access, and critical information gaps |
| **Goal** | Empower farmers to make better decisions and improve agricultural outcomes |
| **Target Users** | Smallholder farmers across Sub-Saharan Africa |

---

## ✨ Key Features

### 🌱 Crop Advisory Tools
Personalised, data-driven recommendations on planting schedules, crop selection based on soil type, and seasonal best practices tailored to each farmer's region.

### 🌦️ Weather Insights
Hyperlocal weather forecasting and climate trend analysis to help farmers plan planting, harvesting, and irrigation with confidence.

### 🛒 Market Access Platform
Real-time commodity pricing, buyer-seller connectivity, and price trend analysis to help farmers get fair value for their produce.

### 🪲 Pest & Disease Identification
AI-powered image recognition to identify crop diseases and pest infestations early, with actionable treatment recommendations.

### 📊 Farm Planning Dashboard
A unified dashboard consolidating farm health metrics, activity schedules, financial tracking, and yield history into a single, intuitive interface.

---

## 🤖 AI Capabilities

IQFarm leverages AI meaningfully across the entire agricultural lifecycle:

| AI Feature | Description |
|---|---|
| **Disease Detection** | Computer vision models identify plant diseases from farmer-uploaded photos |
| **Yield Forecasting** | ML models predict expected yields based on weather patterns, soil data, and historical harvests |
| **Price Prediction** | Time-series forecasting to predict commodity price movements and optimal sell windows |
| **Recommendation Systems** | Personalised advisory engine suggesting crops, inputs, and interventions based on farm profile |

---

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────┐
│                     IQFarm Platform                   │
│                                                       │
│  ┌──────────────────────┐  ┌───────────────────────┐ │
│  │    Angular 19 SSR    │  │   Spring Boot 4.1.0   │ │
│  │    (Client / PWA)    │◄─►│   (REST API Server)   │ │
│  │                      │  │                       │ │
│  │  • TailwindCSS 4.x   │  │  • Spring Data JPA    │ │
│  │  • RxJS 7.8          │  │  • Spring Validation  │ │
│  │  • Angular SSR       │  │  • Spring Web MVC     │ │
│  └──────────────────────┘  │  • Lombok             │ │
│                             └──────────┬────────────┘ │
│                                        │              │
│                             ┌──────────▼────────────┐ │
│                             │   PostgreSQL 16        │ │
│                             │   (IQFarm_db)          │ │
│                             └───────────────────────┘ │
└─────────────────────────────────────────────────────┘
```

The application follows a **monorepo structure** with a clear client/server separation:

- **`/Client`** — Angular 19 Single Page Application with Server-Side Rendering (SSR) for improved performance and SEO
- **`/Server`** — Spring Boot REST API backend with JPA/Hibernate ORM over PostgreSQL

---

## 🛠️ Tech Stack

### Frontend (`/Client`)

| Technology | Version | Purpose |
|---|---|---|
| Angular | 19.2 | SPA Framework with SSR support |
| TypeScript | 5.7 | Type-safe development |
| Tailwind CSS | 4.3 | Utility-first styling |
| RxJS | 7.8 | Reactive data streams |
| Angular SSR | 19.2 | Server-side rendering |
| Express | 4.18 | SSR server runtime |

### Backend (`/Server`)

| Technology | Version | Purpose |
|---|---|---|
| Spring Boot | 4.1.0 | Application framework |
| Java | 17 | Runtime language |
| Spring Data JPA | — | ORM & database abstraction |
| Spring Web MVC | — | REST API layer |
| Spring Validation | — | Input validation |
| PostgreSQL | 16 | Primary relational database |
| Lombok | — | Boilerplate reduction |

---

## 📁 Project Structure

```
IQFarm/
├── Client/                         # Angular 19 Frontend Application
│   ├── src/
│   │   ├── app/
│   │   │   ├── app.component.ts    # Root component
│   │   │   ├── app.routes.ts       # Application routing
│   │   │   ├── app.config.ts       # Application configuration
│   │   │   └── app.config.server.ts# SSR configuration
│   │   ├── main.ts                 # Browser entry point
│   │   ├── main.server.ts          # Server entry point (SSR)
│   │   ├── server.ts               # Express SSR server
│   │   ├── styles.css              # Global styles
│   │   └── index.html              # HTML shell
│   ├── angular.json                # Angular CLI configuration
│   ├── tsconfig.json               # TypeScript configuration
│   ├── package.json                # Node.js dependencies
│   └── .postcssrc.json             # PostCSS configuration
│
└── Server/                         # Spring Boot Backend API
    ├── src/
    │   ├── main/
    │   │   ├── java/com/example/server/
    │   │   │   └── ServerApplication.java  # Application entry point
    │   │   └── resources/
    │   │       └── application.properties  # App configuration
    │   └── test/                           # Unit & integration tests
    ├── pom.xml                             # Maven build descriptor
    └── mvnw                                # Maven wrapper
```

---

## 🚀 Getting Started

### Prerequisites

Ensure the following are installed on your system:

| Tool | Version | Download |
|---|---|---|
| **Node.js** | ≥ 18.x | [nodejs.org](https://nodejs.org/) |
| **npm** | ≥ 9.x | Included with Node.js |
| **Angular CLI** | ≥ 19.x | `npm install -g @angular/cli` |
| **Java JDK** | 17 | [adoptium.net](https://adoptium.net/) |
| **Maven** | ≥ 3.9 | [maven.apache.org](https://maven.apache.org/) or use `./mvnw` |
| **PostgreSQL** | ≥ 14 | [postgresql.org](https://www.postgresql.org/download/) |

---

### Backend Setup

**1. Create the PostgreSQL database**

```sql
CREATE DATABASE "IQFarm_db";
```

**2. Configure credentials**

Open `Server/src/main/resources/application.properties` and update the datasource settings to match your local PostgreSQL instance:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/IQFarm_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

> ⚠️ **Never commit real credentials.** Use environment variables or a `.env` file in production environments.

**3. Build and run the server**

```bash
cd Server

# Using the Maven wrapper (recommended)
./mvnw spring-boot:run

# Or using Maven directly
mvn spring-boot:run
```

The API will be available at: **`http://localhost:8080`**

---

### Frontend Setup

**1. Install dependencies**

```bash
cd Client
npm install
```

**2. Start the development server**

```bash
npm start
# or
ng serve
```

The application will be available at: **`http://localhost:4200`**

**3. Build for production**

```bash
npm run build
```

**4. Run with SSR**

```bash
npm run serve:ssr:Client
```

---

## ⚙️ Configuration

### Environment Variables

| Variable | Description | Default |
|---|---|---|
| `DB_URL` | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/IQFarm_db` |
| `DB_USERNAME` | Database username | `postgres` |
| `DB_PASSWORD` | Database password | — |
| `SERVER_PORT` | Backend server port | `8080` |

### JPA / Hibernate

The current configuration uses `create-drop` DDL auto mode for development, which rebuilds the schema on each restart.

> ⚠️ **Before deploying to production**, change `spring.jpa.hibernate.ddl-auto` from `create-drop` to `validate` or `none` to prevent data loss.

```properties
# Development
spring.jpa.hibernate.ddl-auto=create-drop

# Production
spring.jpa.hibernate.ddl-auto=validate
```

---

## 🔄 Development Workflow

This project uses a two-branch Git strategy:

| Branch | Purpose |
|---|---|
| `main` | Stable, production-ready code |
| `Dev` | Active development and feature work |

**Recommended workflow:**

```bash
# Always branch off Dev for new features
git checkout Dev
git checkout -b feature/your-feature-name

# Keep your branch up to date
git pull origin Dev

# Open a Pull Request into Dev when complete
# Merge Dev → main for releases only
```

---

## 🌍 African Context Considerations

IQFarm is built with the realities of African smallholder farming in mind:

| Consideration | Implementation Approach |
|---|---|
| **Offline Functionality** | Angular SSR + PWA caching for offline access in low-connectivity areas |
| **Affordability** | Lightweight UI with minimal data consumption; USSD integration planned |
| **Smallholder Scale** | Farm profiles calibrated for plots ranging from 0.5 to 10 hectares |
| **Climate Variability** | Integration with regional meteorological APIs; seasonal advisory calendars |
| **Local Languages** | i18n-ready architecture to support Swahili, Zulu, Twi, Hausa, and more |
| **Low-End Devices** | Responsive design optimised for entry-level Android smartphones |

---

## 🤝 Contributing

Contributions are welcome and appreciated. To get started:

1. **Fork** the repository
2. **Create** a feature branch from `Dev` (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes with clear, conventional commit messages (`git commit -m 'feat: add crop disease detection endpoint'`)
4. **Push** to your branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request targeting the `Dev` branch

Please ensure your code:
- Follows existing code conventions and style
- Includes relevant unit tests
- Does not break existing tests (`ng test` / `./mvnw test`)
- Has meaningful commit messages following [Conventional Commits](https://www.conventionalcommits.org/)

---

## 📄 License

This project is submitted as part of **Challenge 4 — Agriculture & Food Security**. All rights reserved by the IQFarm team.

---

<div align="center">

**Built with ❤️ for African farmers**

*"Technology in service of those who feed the continent."*

</div>

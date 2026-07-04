# IQFarm — AGENTS.md

> This file governs how every AI agent (Antigravity, Copilot, Cursor, etc.) should reason,
> plan, and write code for this project.
> **Read it in full before touching any file.**

---

## Project Overview

**IQFarm** is a production-quality intelligent agricultural decision-support platform built for
African smallholder farmers. It combines AI-driven crop advisory, real-time weather insights,
commodity market intelligence, and pest/disease detection into a single accessible platform.

The system is a full-stack monorepo with a clear client/server split:

- **`/Client`** — Angular 19 SPA with SSR (Angular Universal / Express)
- **`/Server`** — Spring Boot 4 REST API over PostgreSQL via Spring Data JPA

This project is also a **teaching project**. The codebase must stay clean, simple, and
explainable — approachable for developers who are learning how to build a real full-stack
Angular + Spring Boot app, feature by feature.

---

## Tech Stack

| Layer | Choice |
|---|---|
| Frontend Framework | Angular 19.2 (Standalone components, SSR) |
| Frontend Language | TypeScript 5.7 (strict) |
| Styling | Tailwind CSS 4.x (PostCSS plugin) |
| Reactive Layer | RxJS 7.8 |
| SSR Runtime | Angular SSR + Express 4.18 |
| Backend Framework | Spring Boot 4.1.0 |
| Backend Language | Java 17 |
| ORM | Spring Data JPA / Hibernate |
| Validation (Server) | Spring Validation (Bean Validation / Jakarta) |
| REST Layer | Spring Web MVC |
| Boilerplate Reduction | Lombok |
| Database | PostgreSQL 16 |

Do **not** introduce new major libraries without a strong reason. If a library would
meaningfully simplify the implementation, recommend it and ask for approval before installing.

---

## Development Philosophy

Build **feature by feature**.

For every feature:

1. Read this file first.
2. Understand the full request before touching code.
3. Identify exactly which files need to change — check the branch prefix to determine which layer(s) are in scope (see **Git Workflow → Branch Prefix Rules**).
4. Keep the implementation simple and focused.
5. Prefer readable code over clever code.
6. Build the smallest useful version first.
7. Refactor only when duplication or complexity clearly appears.
8. Fix all errors (TypeScript, lint, Java compiler) before finishing.

> This project must feel like a real app, but remain approachable for students.

---

## Architecture Guidelines

### Directory Structure

```
IQFarm/
├── Client/                              # Angular 19 Frontend Application
│   └── src/
│       ├── app/
│       │   ├── core/                    # Singleton services, guards, interceptors
│       │   │   ├── services/            # API services (HttpClient wrappers)
│       │   │   ├── guards/              # Route guards (auth etc.)
│       │   │   └── interceptors/        # HTTP interceptors (auth token, error)
│       │   ├── features/                # Feature modules (one folder per domain)
│       │   │   ├── dashboard/           # Farm planning dashboard
│       │   │   ├── crop-advisory/       # Crop recommendation tools
│       │   │   ├── weather/             # Weather insights
│       │   │   ├── market/              # Market access & pricing
│       │   │   ├── disease-detection/   # AI disease/pest identification
│       │   │   └── auth/                # Login, register, profile
│       │   ├── shared/                  # Shared components, pipes, directives
│       │   │   ├── components/          # Reusable UI pieces (Card, Badge, Alert…)
│       │   │   ├── pipes/               # Custom Angular pipes
│       │   │   └── directives/          # Custom Angular directives
│       │   ├── app.component.ts         # Root component
│       │   ├── app.routes.ts            # Application routing
│       │   ├── app.config.ts            # Application providers
│       │   └── app.config.server.ts     # SSR providers
│       ├── styles.css                   # Global styles & design tokens
│       ├── index.html                   # HTML shell
│       ├── main.ts                      # Browser entry point
│       ├── main.server.ts               # Server entry point (SSR)
│       └── server.ts                    # Express SSR server
│
└── Server/                              # Spring Boot Backend API
    └── src/
        ├── main/
        │   ├── java/com/example/server/
        │   │   ├── controller/          # REST controllers (@RestController)
        │   │   ├── service/             # Business logic (@Service)
        │   │   ├── repository/          # JPA repositories (@Repository)
        │   │   ├── model/               # JPA entities (@Entity)
        │   │   ├── dto/                 # Request / response DTOs
        │   │   ├── exception/           # Custom exceptions & global handler
        │   │   ├── config/              # Spring configuration (@Configuration)
        │   │   └── ServerApplication.java
        │   └── resources/
        │       └── application.properties
        └── test/                        # Unit & integration tests
```

> Directories that do not exist yet should be created when the first file in them is needed.

---

### Angular (`/Client`) Architecture

#### Standalone Components (Angular 19)

All Angular components must be **standalone** (no `NgModule`). Import only what the
component needs directly in its `imports` array.

```ts
@Component({
  selector: 'app-example',
  standalone: true,            // ✅ Always standalone in Angular 19
  imports: [CommonModule, RouterLink],
  templateUrl: './example.component.html',
  styleUrl: './example.component.css',
})
export class ExampleComponent {}
```

#### Feature Organisation

Each feature lives in `src/app/features/<feature-name>/` and contains:

```
features/dashboard/
  ├── dashboard.component.ts
  ├── dashboard.component.html
  ├── dashboard.component.css
  └── dashboard.routes.ts       # Lazy-loaded child routes for the feature
```

Register feature routes via **lazy loading** in `app.routes.ts`:

```ts
{
  path: 'dashboard',
  loadChildren: () =>
    import('./features/dashboard/dashboard.routes').then(m => m.DASHBOARD_ROUTES),
}
```

#### `core/` Services

- Services in `core/services/` are **provided in root** (`providedIn: 'root'`).
- They wrap `HttpClient` and return `Observable<T>`.
- Never call `HttpClient` directly inside a component. Always go through a service.

#### `shared/` Components

Create a shared component only when:
- It is reused in **two or more** feature modules, **or**
- It represents a distinct UI concept (`FarmCardComponent`, `WeatherBadgeComponent`).

Avoid extracting tiny one-off components prematurely.

#### SSR Awareness

The Client uses Angular SSR. Every component must be SSR-safe:
- Never access `window`, `document`, or `localStorage` directly.
- Inject `PLATFORM_ID` and use `isPlatformBrowser()` before accessing browser APIs.
- Use `TransferState` or a resolver to avoid double-fetching data on hydration.

```ts
import { isPlatformBrowser } from '@angular/common';
import { PLATFORM_ID, inject } from '@angular/core';

readonly #platformId = inject(PLATFORM_ID);

if (isPlatformBrowser(this.#platformId)) {
  // browser-only code here
}
```

---

### Spring Boot (`/Server`) Architecture

#### Layered Architecture

Always respect the strict layer separation:

```
HTTP Request → Controller → Service → Repository → Database
                                 ↘ DTO mapping ↗
```

- **Controller** — validates input (via `@Valid`), delegates to Service, returns response DTO.
- **Service** — owns all business logic; calls Repository methods; never accesses the DB directly.
- **Repository** — extends `JpaRepository<Entity, Long>`; keeps custom queries here.
- **Entity** — annotated with `@Entity`; represents a DB table; never returned directly from a Controller.
- **DTO** — plain Java record or class used for request/response shapes; never the entity itself.

#### DTOs vs Entities

**Never** expose JPA entities directly in API responses. Always map to a DTO:

```java
// ✅ CORRECT — return a DTO
public record FarmerResponse(Long id, String name, String region) {}

// ❌ INCORRECT — returning the JPA entity
@GetMapping("/farmers/{id}")
public Farmer getFarmer(@PathVariable Long id) { ... }
```

#### Global Exception Handling

Add a `@RestControllerAdvice` in `exception/` to handle errors uniformly. All error
responses must follow the standard shape:

```json
{ "success": false, "error": "Descriptive message" }
```

All success responses must follow:

```json
{ "success": true, "data": { ... } }
```

Use `ResponseEntity<ApiResponse<T>>` consistently across all controllers.

#### Validation

- Use **Jakarta Bean Validation** (`@NotBlank`, `@NotNull`, `@Min`, `@Email`, etc.) on DTO fields.
- Always annotate the controller method parameter with `@Valid`.
- Validation errors are caught by the global `@RestControllerAdvice`.

```java
@PostMapping("/farmers")
public ResponseEntity<ApiResponse<FarmerResponse>> create(@Valid @RequestBody CreateFarmerRequest req) {
    ...
}
```

---

## Database Rules

### JPA / Hibernate

- Entities live in `Server/src/main/java/com/example/server/model/`.
- Use `spring.jpa.hibernate.ddl-auto=create-drop` **only in development**.
- Use `spring.jpa.hibernate.ddl-auto=validate` or `none` **in production**.
- For complex queries not expressible with Spring Data method names, use JPQL (`@Query`)
  rather than native SQL unless absolutely necessary.
- Always use `Optional<T>` for `findById` and handle the empty case explicitly.

### Naming Conventions

| Layer | Convention | Example |
|---|---|---|
| Entity | PascalCase, singular | `Farmer`, `CropAdvisory` |
| Table | snake_case, plural | `farmers`, `crop_advisories` |
| Column | snake_case | `region_name`, `created_at` |
| Repository | `<Entity>Repository` | `FarmerRepository` |
| Service | `<Entity>Service` | `FarmerService` |
| Controller | `<Entity>Controller` | `FarmerController` |
| DTO | `<Action><Entity>Request/Response` | `CreateFarmerRequest`, `FarmerResponse` |

### Common Entity Fields

Every entity should include audit fields via a `@MappedSuperclass`:

```java
@MappedSuperclass
public abstract class BaseEntity {
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

---

## API Design Rules

- Base path: `/api/v1/`
- All endpoints return `ResponseEntity<ApiResponse<T>>`
- Use standard HTTP status codes: `200 OK`, `201 Created`, `400 Bad Request`,
  `401 Unauthorized`, `403 Forbidden`, `404 Not Found`, `500 Internal Server Error`
- Never expose stack traces or internal error messages in production responses
- Use `@CrossOrigin` on controllers or configure CORS globally in a `WebMvcConfigurer`
  bean — allow `http://localhost:4200` in development

### Endpoint Naming

```
GET    /api/v1/farmers              # list all
POST   /api/v1/farmers              # create
GET    /api/v1/farmers/{id}         # get one
PUT    /api/v1/farmers/{id}         # replace
PATCH  /api/v1/farmers/{id}         # partial update
DELETE /api/v1/farmers/{id}         # delete
```

---

## TypeScript Rules (Client)

- Use strict TypeScript throughout. `"strict": true` is set in `tsconfig.json`.
- Avoid `any`. Use `unknown` when the type is genuinely uncertain, then narrow it.
- Use Angular's **inject()** function instead of constructor injection in new components:

```ts
// ✅ Preferred in Angular 19
readonly #farmerService = inject(FarmerService);

// ❌ Avoid for new code
constructor(private farmerService: FarmerService) {}
```

- Define API response models as interfaces in the same file or in `core/models/` if reused.
- Use `readonly` on injected services and signals.
- Prefer `signal()`, `computed()`, and `effect()` (Angular Signals) over `BehaviorSubject`
  for local component state in new code.

---

## Styling Rules (Client)

### Design System

IQFarm uses a **green + earth-tone** agricultural design system. The goal is to feel
trustworthy, modern, and approachable — not clinical or generic.

All design tokens (CSS custom properties) are defined in `Client/src/styles.css`.

### Colour Palette

| Token | Hex | Usage |
|---|---|---|
| `--color-primary` | `#2D7D46` | Primary green — CTAs, active states |
| `--color-primary-light` | `#4CAF74` | Hover states, highlights |
| `--color-earth` | `#8B5E3C` | Earth brown — secondary accents |
| `--color-sky` | `#4A9ECC` | Weather features, info states |
| `--color-warning` | `#E8A838` | Alerts, price changes |
| `--color-danger` | `#D94F3D` | Errors, disease alerts |
| `--color-surface` | `#F8FAF5` | Page background |
| `--color-surface-dark` | `#1A2E1F` | Dark mode canvas |
| `--color-text` | `#1C2B1E` | Primary text |
| `--color-text-muted` | `#6B7C6D` | Secondary text |

### Tailwind Usage

- Use Tailwind utility classes for all layout, spacing, and responsive design.
- Use CSS custom properties (above) for brand colours — reference them with
  `text-[var(--color-primary)]` or map them in the Tailwind config.
- Never use hardcoded hex values in templates. Always reference a design token.
- Avoid inline `style=""` attributes except for runtime-calculated dynamic values.

### Component Patterns

```html
<!-- ✅ Card pattern -->
<div class="rounded-2xl border border-border bg-surface p-6 shadow-sm">
  ...
</div>

<!-- ✅ Primary button -->
<button class="rounded-lg bg-primary px-4 py-2 text-white font-medium hover:bg-primary-light transition-colors">
  Save Farm
</button>
```

### Responsive Design

Always design **mobile-first**. IQFarm users are predominantly on low-end Android phones:

- Minimum tested width: **375px**
- Use `sm:`, `md:`, `lg:` breakpoints progressively
- Touch targets must be ≥ 44px
- Avoid hover-only interactions — ensure tap equivalents exist
- Use `prefers-reduced-motion` media query for animations

---

## Angular State Management

| State type | Approach |
|---|---|
| Server data | Fetch via `core/services/` using `HttpClient`; use `AsyncPipe` in templates |
| Local component state | Angular Signals (`signal()`, `computed()`) |
| Cross-component state | Service with `signal()` or `BehaviorSubject` in a `providedIn: 'root'` service |
| URL / navigation state | `ActivatedRoute`, `Router` |
| Form state | `ReactiveFormsModule` (`FormGroup`, `FormControl`) |

Do **not** introduce NgRx or other state management libraries without approval.

---

## Auth Rules

Auth is **not yet implemented** — plan ahead for it:

- Authentication will use JWT tokens issued by the Spring Boot backend.
- The Angular client will store the token in `localStorage` (with SSR guard).
- An HTTP interceptor in `core/interceptors/` will attach `Authorization: Bearer <token>`
  to all API requests.
- Route guards in `core/guards/` will protect authenticated routes.
- When implementing auth, follow the layered architecture strictly on the server side:
  - `AuthController` → `AuthService` → `UserRepository`
  - Issue JWTs in `AuthService` using a JWT library (e.g., `jjwt`).

> Do not implement auth unless explicitly asked. Plan the folder structure now
> so it lands cleanly when the time comes.

---

## AI Feature Rules

IQFarm's AI capabilities (disease detection, yield forecasting, price prediction,
recommendation engine) are **external integrations**, not in-repo ML models.

Guidelines for implementing AI features:

1. AI calls go through a dedicated service in `core/services/` (e.g., `DiseaseDetectionService`).
2. The Spring Boot backend acts as a proxy for AI API calls — never expose AI API keys to the Angular frontend.
3. Validate all AI request payloads with Bean Validation before forwarding.
4. Handle AI API failures gracefully: return a `503 Service Unavailable` with a user-friendly message.
5. For image uploads (disease detection): accept `multipart/form-data` on the Spring endpoint; forward the image bytes to the AI service.
6. AI responses must be mapped to typed DTOs before returning to the client.

---

## African Context Constraints

These are non-negotiable design constraints. Every feature must respect them:

| Constraint | Implementation Rule |
|---|---|
| **Low connectivity** | All API responses must be as small as possible. Paginate lists. Avoid over-fetching. |
| **PWA / offline** | Register a Service Worker. Cache critical assets and last-known data for offline viewing. |
| **Low-end devices** | No heavy animations or large JavaScript bundles. Use Angular lazy loading aggressively. |
| **Data affordability** | Compress images before upload. Avoid polling; prefer on-demand fetches. |
| **i18n readiness** | Use Angular's `i18n` attributes on all user-visible strings from day one. Do not hardcode UI text. |
| **Local crop context** | All crop, region, and weather data must be configurable per locale — no hardcoded crop lists. |

---

## Git Workflow

The project uses a **two-branch strategy**:

| Branch | Purpose |
|---|---|
| `main` | Stable, production-ready code |
| `develop` | Active development and feature work |

### Branch Prefix Rules

Every feature branch **must** be prefixed with either `FE/` or `BE/` to declare its scope.
This prefix is a hard constraint — it determines which layer(s) an agent may modify.

| Prefix | Scope | Rule |
|---|---|---|
| `FE/` | Frontend only | **Only** `/Client` files may be edited. `/Server` is **read-only** — use it as a reference to understand existing API contracts, but do not change any server-side file. |
| `BE/` | Backend only | **Only** `/Server` files may be edited. `/Client` is **read-only** — use it as a reference to understand how the frontend consumes the API, but do not change any client-side file. |

> **Why?** This prevents accidental cross-layer changes, makes PRs easier to review,
> and reinforces the Client/Server architectural boundary for learners.

**Feature workflow:**

```bash
# Always branch off develop
git checkout develop

# Frontend feature
git checkout -b FE/your-feature-name

# Backend feature
git checkout -b BE/your-feature-name

# Keep up to date
git pull origin develop

# Commit with Conventional Commits
git commit -m "feat(weather): add hyperlocal forecast component"
git commit -m "fix(market): correct price trend chart axis labels"

# Open a PR into develop — never directly into main
```

**Commit message format:** `<type>(<scope>): <description>`

| Type | When to use |
|---|---|
| `feat` | New feature |
| `fix` | Bug fix |
| `refactor` | Code restructure, no behaviour change |
| `style` | CSS/Tailwind changes only |
| `test` | Adding or updating tests |
| `docs` | Documentation only |
| `chore` | Build scripts, deps, config |

---

## Running the Project

### Backend (Spring Boot)

```bash
cd Server

# Development run
./mvnw spring-boot:run

# Run tests
./mvnw test

# Build JAR
./mvnw package
```

API available at: `http://localhost:8080`

### Frontend (Angular)

```bash
cd Client

# Install dependencies
npm install

# Development server (SPA mode)
npm start           # or: ng serve

# Development with SSR
npm run watch

# Production build
npm run build

# Run SSR server (after build)
npm run serve:ssr:Client

# Run tests
npm test
```

App available at: `http://localhost:4200`

---

## Environment Variable Rules

### Backend (`application.properties`)

| Property | Description | Never commit? |
|---|---|---|
| `spring.datasource.url` | PostgreSQL connection URL | ✅ Yes |
| `spring.datasource.username` | DB username | ✅ Yes |
| `spring.datasource.password` | DB password | ✅ Yes |
| `server.port` | Spring server port (default 8080) | No |
| `spring.jpa.hibernate.ddl-auto` | `create-drop` (dev) / `validate` (prod) | No |

Use `${ENV_VAR_NAME}` syntax in `application.properties` to read from the environment.
Provide an `application.properties.example` with placeholder values.

### Frontend (`environment.ts`)

Use Angular's environment files (`environment.ts` / `environment.prod.ts`):

```ts
export const environment = {
  production: false,
  apiBaseUrl: 'http://localhost:8080/api/v1',
};
```

Never hardcode backend URLs in services. Always reference `environment.apiBaseUrl`.

> **Never** commit real credentials. Add `application.properties` to `.gitignore`
> and provide a `application.properties.example` template instead.

---

## Linting and Validation

### Frontend

Before finishing any feature, run:

```bash
cd Client
npm run lint     # ESLint
npm test         # Karma/Jasmine unit tests
npm run build    # Catch TypeScript errors
```

Fix all ESLint errors and warnings before committing.

### Backend

Before finishing any feature, run:

```bash
cd Server
./mvnw verify    # Compile + test + package
./mvnw test      # Unit tests only
```

Fix all Java compiler errors and failing tests before committing.

---

## Feature Implementation Checklist

When asked to build a feature, follow this checklist:

1. **Read this file** before writing code.
2. **Understand the request fully** before touching files.
3. **Check the branch prefix** to determine which layer is in scope:
   - `FE/*` branch → edit only `/Client`; treat `/Server` as read-only reference.
   - `BE/*` branch → edit only `/Server`; treat `/Client` as read-only reference.
   - No prefix → full-stack change; identify the minimal set of files across both layers.
4. **Backend first** (when in scope): Entity → Repository → Service → Controller → DTO.
5. **Frontend second** (when in scope): Service → Component → Template → Styles.
6. **Validate** all user input with `@Valid` (server) and Angular Reactive Forms (client).
7. **Handle errors** gracefully — both HTTP errors and empty states.
8. **Respect African context constraints** (offline, mobile, i18n, data size).
9. **Fix all TypeScript and Java errors** before finishing.
10. **Test the feature end-to-end** and confirm it works.

---

## Decision Making and Clarifications

If something is unclear or could be significantly improved:

- Proactively suggest the better approach.
- If a new library would meaningfully simplify the implementation:
  - Recommend it clearly.
  - Explain why it is useful.
  - Ask for permission before installing.

> Example: "This could be built manually, but `Mapstruct` would handle DTO ↔ Entity
> mapping cleanly across the server layer. Want me to add it?"

Do **not** install or use new libraries without approval.

---

## Communication Style

- Be concise.
- Explain **what changed** and **how to test it**.
- When making multiple file changes, list the files touched at the end of the response.
- When you hit a design decision, flag it clearly and offer two or three options.
- Always state whether a change is **Client**, **Server**, or **both**.

---

## Final Reminder

Before every feature implementation:

- Read this file
- Follow it strictly
- Build clean, simple, teachable code
- Respect the African context constraints in every decision
- Never commit real credentials or secrets
- Fix all errors before finishing
- Think mobile-first, low-bandwidth, low-end device at every step

> **"Technology in service of those who feed the continent."**

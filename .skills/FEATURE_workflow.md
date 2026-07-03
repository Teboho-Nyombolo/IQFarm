# Skill: New Feature — End-to-End Workflow (IQFarm)

> **Read this when starting any new feature from scratch.**
> This skill covers the complete branch → implementation → verify → commit
> cycle, with all IQFarm-specific rules and the exact steps that are easy
> to get wrong without project context.

---

## 0. Before You Write a Single Line

Answer these four questions first:

1. **Which layer is in scope?**
   - `FE/*` branch → only `/Client` files. `/Server` is read-only reference.
   - `BE/*` branch → only `/Server` files. `/Client` is read-only reference.
   - No prefix → both layers; identify the minimum cross-layer file set.

2. **Does the endpoint already exist?**
   Check `IQFarm Endpoints/` (the Postman-style docs at the project root) and
   `Server/src/main/java/com/example/server/controller/` before building a new one.

3. **Does a shared component already exist?**
   Check `Client/src/app/shared/` before creating a new one.

4. **Is a new library required?**
   Flag it explicitly and ask for approval — never `npm install` or add a Maven
   dependency without confirmation.

---

## 1. Branch Setup

```bash
# Always branch off develop, never off main
git checkout develop
git pull origin develop

# Frontend feature
git checkout -b FE/crop-advisory-form

# Backend feature
git checkout -b BE/crop-advisory-endpoint
```

### Branch naming rules

| Prefix | When | Example |
|---|---|---|
| `FE/` | Only `/Client` changes | `FE/auth-login-page` |
| `BE/` | Only `/Server` changes | `BE/farm-crud-api` |
| No prefix | Agreed full-stack change | `crop-advisory` |

---

## 2. Backend Feature Steps

> See `BE_feature_implementation.md` for the full detail on each step.

Strict order — each step depends on the previous:

```
1. Entity            → model/ 
2. Repository        → repository/
3. Service Interface → service/
4. ServiceImpl       → service/impl/
5. Mapper            → mapper/
6. Request DTO       → dto/request/
7. Response DTO      → dto/response/
8. Controller        → controller/
```

**Compile after each step:**
```bash
cd Server
./mvnw compile        # catch errors early, not at the end
```

**Run all tests before committing:**
```bash
./mvnw verify
```

---

## 3. Frontend Feature Steps

> See `FE_angular_ssr_component.md` for full detail.

Strict order:

```
1. TypeScript model interfaces   → core/models/
2. API service                   → core/services/
3. Feature route file            → features/<name>/<name>.routes.ts
4. Register route in app.routes.ts (lazy loadChildren)
5. Component TS                  → features/<name>/<name>.component.ts
6. Component HTML template
7. Component CSS (Tailwind + design tokens only)
```

**Lint and build after finishing:**
```bash
cd Client
npm run lint
npm run build
```

---

## 4. Connecting FE to BE

### API base URL

Never hardcode the backend URL. Always use:
```typescript
// environment.ts
apiBaseUrl: 'http://localhost:8080/api'
```

### Response unwrapping

The Spring API always returns `ApiResponse<T>`. The Angular service returns the full
wrapper; the component unwraps `.data`:

```typescript
// service
getAllFarms(): Observable<ApiResponse<FarmResponseDTO[]>> {
  return this.#http.get<ApiResponse<FarmResponseDTO[]>>(this.#baseUrl);
}

// component
this.#farmService.getAllFarms().subscribe({
  next: (res) => this.farms.set(res.data ?? []),
});
```

### CORS

Every Spring controller must have:
```java
@CrossOrigin(origins = "http://localhost:8080")
```
If the Angular app gets a CORS error, this annotation is missing or the origin is wrong.

---

## 5. Commit Message Format

```
<type>(<scope>): <short description>

feat(farms): add farm creation endpoint
fix(auth): correct password hashing on update
style(dashboard): update card hover colour to design token
refactor(crop): extract crop mapper to separate class
test(user): add unit test for getUserById
docs(agents): update branch prefix rules
chore(deps): upgrade Spring Boot to 4.1.1
```

**Scope** = the domain noun: `auth`, `farms`, `crops`, `weather`, `market`, `dashboard`.

---

## 6. PR Rules

- PRs go into `develop`, **never directly into `main`**.
- Title matches the commit format: `feat(farms): add farm CRUD API`.
- PR description must list:
  1. What changed (files and reason)
  2. How to test it (curl commands or UI steps)
  3. Any design decisions made

---

## 7. Running Both Servers

```bash
# Terminal 1 — Backend
cd Server
./mvnw spring-boot:run
# API: http://localhost:8080

# Terminal 2 — Frontend
cd Client
npm start
# App: http://localhost:4200
```

**PostgreSQL must be running first.** If `spring-boot:run` fails with:
```
Unable to acquire JDBC Connection
```
Start PostgreSQL:
```bash
brew services start postgresql@16
# or
pg_ctl -D /usr/local/var/postgresql@16 start
```

Check `Server/src/main/resources/application.properties` for the expected DB name and credentials.

---

## 8. Common Cross-Cutting Mistakes

### Backend
| Mistake | Symptom | Fix |
|---|---|---|
| Missing `@CrossOrigin` | CORS error in browser console | Add `@CrossOrigin(origins = "http://localhost:4200")` to controller |
| Returning entity instead of DTO | Risk of infinite recursion / password leak | Always map through `Mapper.toResponseDTO()` |
| `Optional.get()` without check | `NoSuchElementException` at runtime | Use `.orElseThrow(() -> new RuntimeException(...))` |
| `FetchType.EAGER` on collection | N+1 queries, huge JSON | Use `FetchType.LAZY` |
| `@Valid` missing on `@RequestBody` | Validation silently skipped | Add `@Valid` before `@RequestBody` |

### Frontend
| Mistake | Symptom | Fix |
|---|---|---|
| `window` access without SSR guard | Crash on server render | Wrap in `isPlatformBrowser()` |
| Hardcoded `localhost:8080` in service | Production build breaks | Use `environment.apiBaseUrl` |
| `loadComponent` for multi-route feature | Child routes not matched | Use `loadChildren` + separate route file |
| Missing `provideHttpClient()` | `NullInjectorError: HttpClient` | Add `provideHttpClient(withFetch())` to `app.config.ts` |
| Hardcoded hex in template | Diverges from design system | Use Tailwind token class (`bg-primary`) |

---

## 9. Definitions Quick Reference

| Term | Location | Purpose |
|---|---|---|
| `ApiResponse<T>` | `dto/response/ApiResponse.java` | Standard response wrapper for all endpoints |
| `PasswordEncryption` | `service/impl/PasswordEncryption.java` | BCrypt-style hashing — use for all passwords |
| `environment.ts` | `Client/src/environments/` | Backend URL config — never hardcode |
| `styles.css` | `Client/src/styles.css` | All CSS design tokens and Tailwind theme |
| `app.config.ts` | `Client/src/app/app.config.ts` | Angular providers (HttpClient, Router, SSR) |
| `app.routes.ts` | `Client/src/app/app.routes.ts` | Top-level lazy route registration |

---

## 10. End-of-Feature Checklist

### Backend (BE/* branch)
- [ ] `./mvnw verify` passes — zero compile errors, zero test failures
- [ ] All endpoints return `ApiResponse<T>`
- [ ] `@Valid` on every `@RequestBody`
- [ ] `@CrossOrigin` on every controller
- [ ] No entity returned directly from any controller method
- [ ] `Optional.get()` is never called — always `.orElseThrow(...)`

### Frontend (FE/* branch)
- [ ] `npm run lint` passes — zero ESLint errors
- [ ] `npm run build` passes — zero TypeScript errors
- [ ] No `window`/`localStorage`/`document` outside `isPlatformBrowser()` guard
- [ ] `environment.apiBaseUrl` used — no hardcoded URLs
- [ ] Tailwind design tokens used — no hardcoded hex values
- [ ] All user-visible strings have `i18n` attributes
- [ ] Touch targets ≥ 44 px

### Both
- [ ] Branch is off `develop` (not `main`)
- [ ] Commit messages follow Conventional Commits format
- [ ] PR targets `develop`
- [ ] No real credentials committed (passwords, API keys, DB URLs)

# Skill: Angular SSR-Safe Component Authoring (IQFarm)

> **Read this before writing or modifying any Angular component.**
> IQFarm uses Angular 19 with SSR (Angular Universal + Express).
> Components run in Node.js during SSR — `window`, `document`, and
> `localStorage` do not exist there. This skill documents every pattern
> needed to stay safe.

---

## Golden Rule

> Any browser API access **must** be guarded with `isPlatformBrowser()`.
> Any violation causes a hard crash on the SSR server and breaks initial page load.

---

## 1. Component Anatomy

Every component must be **standalone**. No `NgModule` is used in this project.

```typescript
import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule, isPlatformBrowser }              from '@angular/common';
import { PLATFORM_ID }                                   from '@angular/core';
import { RouterLink }                                    from '@angular/router';

@Component({
  selector:     'app-farm-card',
  standalone:   true,                             // ✅ always
  imports:      [CommonModule, RouterLink],        // import only what is used
  templateUrl:  './farm-card.component.html',
  styleUrl:     './farm-card.component.css',
})
export class FarmCardComponent implements OnInit {

  // ── Injections (use inject() — not constructor params) ────────
  readonly #platformId  = inject(PLATFORM_ID);
  readonly #farmService = inject(FarmService);    // core service, not impl

  // ── Local state (use signals, not BehaviorSubject) ────────────
  readonly farms        = signal<FarmResponseDTO[]>([]);
  readonly isLoading    = signal(false);
  readonly errorMessage = signal<string | null>(null);

  // ── Computed (derived state) ──────────────────────────────────
  readonly hasFarms = computed(() => this.farms().length > 0);

  ngOnInit(): void {
    this.loadFarms();
  }

  private loadFarms(): void {
    this.isLoading.set(true);
    this.#farmService.getAllFarms().subscribe({
      next:  (res) => { this.farms.set(res.data ?? []); this.isLoading.set(false); },
      error: (err) => { this.errorMessage.set(err.message); this.isLoading.set(false); },
    });
  }
}
```

### What `imports` array should contain

| Need | Import |
|---|---|
| `*ngIf`, `*ngFor`, `async` pipe | `CommonModule` |
| `[routerLink]`, `routerLinkActive` | `RouterLink`, `RouterLinkActive` |
| Reactive forms | `ReactiveFormsModule` |
| HTTP calls | Never in components — use a `core/services/` service |
| Shared IQFarm components | Import the component class directly |

---

## 2. SSR Browser-API Guard

```typescript
// ✅ CORRECT — guarded
readonly #platformId = inject(PLATFORM_ID);

ngOnInit(): void {
  if (isPlatformBrowser(this.#platformId)) {
    const stored = localStorage.getItem('token');   // safe
    this.isMobile = window.innerWidth <= 768;        // safe
  }
}

// ❌ WRONG — crashes Node.js during SSR
ngOnInit(): void {
  const stored = localStorage.getItem('token');      // ReferenceError on server
  this.isMobile = window.innerWidth <= 768;          // ReferenceError on server
}
```

### Patterns that need a guard

| API | Guard required? |
|---|---|
| `window.innerWidth` / `window.scrollY` | ✅ Yes |
| `localStorage` / `sessionStorage` | ✅ Yes |
| `document.querySelector` | ✅ Yes |
| `navigator.userAgent` | ✅ Yes |
| `HostListener('window:resize')` | ✅ Yes — check `isPlatformBrowser` inside handler |
| Angular `HttpClient` | ❌ No — safe on server |
| `Router.navigate()` | ❌ No — safe on server |
| Angular `signal()` / `computed()` | ❌ No — safe on server |

### `@HostListener` with window events

```typescript
// ✅ Guard inside the listener
@HostListener('window:resize')
onResize(): void {
  if (isPlatformBrowser(this.#platformId)) {
    this.isMobile = window.innerWidth <= 768;
  }
}
```

> **Known issue in `AppComponent`:** The current `app.component.ts` calls
> `window.innerWidth` in `checkScreenSize()` without an SSR guard.
> Fix it whenever you touch that file.

---

## 3. Lazy-Loaded Feature Routes

Each feature lives in `src/app/features/<feature>/` with its own route file.

### Feature route file

```typescript
// src/app/features/farm/farm.routes.ts
import { Routes } from '@angular/router';

export const FARM_ROUTES: Routes = [
  {
    path:      '',
    component: FarmListComponent,
  },
  {
    path:      ':id',
    component: FarmDetailComponent,
  },
];
```

### Register in `app.routes.ts`

```typescript
// src/app/app.routes.ts
import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'farms',
    loadChildren: () =>
      import('./features/farm/farm.routes').then(m => m.FARM_ROUTES),
  },
  {
    path: 'auth',
    loadChildren: () =>
      import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES),
  },
  {
    path:       '**',
    redirectTo: 'farms',
  },
];
```

### Common mistakes
- ❌ Using `loadComponent` when you have multiple child routes — use `loadChildren`.
- ❌ Importing the component eagerly in `app.routes.ts` — defeats lazy loading.
- ❌ Forgetting the empty-path (`path: ''`) route inside the feature file — causes a
  navigation miss when visiting `/farms` without a trailing slash.

---

## 4. Core API Services

All HTTP calls go through a service in `src/app/core/services/`.

```typescript
// src/app/core/services/farm.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient }         from '@angular/common/http';
import { Observable }         from 'rxjs';
import { environment }        from '../../../environments/environment';
import { ApiResponse }        from '../models/api-response.model';
import { FarmResponseDTO }    from '../models/farm.model';

@Injectable({ providedIn: 'root' })
export class FarmService {

  readonly #http    = inject(HttpClient);
  readonly #baseUrl = `${environment.apiBaseUrl}/farms`;

  getAllFarms(): Observable<ApiResponse<FarmResponseDTO[]>> {
    return this.#http.get<ApiResponse<FarmResponseDTO[]>>(this.#baseUrl);
  }

  getFarmById(id: number): Observable<ApiResponse<FarmResponseDTO>> {
    return this.#http.get<ApiResponse<FarmResponseDTO>>(`${this.#baseUrl}/${id}`);
  }

  createFarm(payload: CreateFarmRequest): Observable<ApiResponse<FarmResponseDTO>> {
    return this.#http.post<ApiResponse<FarmResponseDTO>>(this.#baseUrl, payload);
  }
}
```

### Wiring `HttpClient` in `app.config.ts`

`provideHttpClient()` must be added to providers before any service can make requests:

```typescript
// src/app/app.config.ts
import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter }          from '@angular/router';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideClientHydration(withEventReplay()),
    provideHttpClient(withFetch()),               // ← required for HTTP
  ],
};
```

> **`withFetch()`** — use this instead of the default XHR strategy; it works on the
> SSR server (Node.js) without extra polyfills.

---

## 5. API Response Model

Define a TypeScript model that mirrors the Java `ApiResponse<T>`:

```typescript
// src/app/core/models/api-response.model.ts
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data:    T | null;
}
```

Use this type as the generic parameter in every `HttpClient` call — never use `any`.

---

## 6. Reactive Forms

Use `ReactiveFormsModule` (never template-driven forms).

```typescript
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  ...
})
export class LoginFormComponent {
  readonly #fb = inject(FormBuilder);

  loginForm: FormGroup = this.#fb.group({
    email:    ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  onSubmit(): void {
    if (this.loginForm.invalid) return;
    const { email, password } = this.loginForm.value;
    // call service
  }
}
```

```html
<!-- Template: show validation error -->
<input id="email" formControlName="email" type="email" />
<p *ngIf="loginForm.get('email')?.invalid && loginForm.get('email')?.touched">
  Valid email is required.
</p>
```

### Common mistakes
- ❌ Calling the API when `loginForm.invalid` — always guard with `if (this.loginForm.invalid) return;`.
- ❌ Using `[(ngModel)]` — this is template-driven; not used here.
- ❌ Forgetting `ReactiveFormsModule` in the `imports` array — form bindings will fail silently.

---

## 7. Tailwind + Design Token Usage

Always use the IQFarm Tailwind theme tokens. Never hardcode hex values.

```html
<!-- ✅ Primary button -->
<button class="rounded-lg bg-primary px-4 py-2 text-white font-medium
               hover:bg-primary-dark transition-colors duration-200
               focus:outline-none focus:ring-2 focus:ring-primary focus:ring-offset-2
               disabled:opacity-50 disabled:cursor-not-allowed"
        [disabled]="isLoading()">
  {{ isLoading() ? 'Saving…' : 'Save Farm' }}
</button>

<!-- ✅ Card -->
<div class="rounded-xl border border-border bg-surface-card p-6 shadow-sm
            hover:shadow-md transition-shadow duration-200">
  ...
</div>

<!-- ✅ Error state -->
<p class="text-danger text-sm mt-1" role="alert">{{ errorMessage() }}</p>

<!-- ❌ Hardcoded colour -->
<button style="background: #50C878">Save</button>
```

### Token → Tailwind utility mapping

| CSS var | Tailwind class |
|---|---|
| `--color-primary` | `bg-primary`, `text-primary`, `border-primary` |
| `--color-primary-dark` | `bg-primary-dark`, `hover:bg-primary-dark` |
| `--color-tertiary` | `bg-tertiary`, `text-tertiary` |
| `--color-surface-card` | `bg-surface-card` |
| `--color-border` | `border-border` |
| `--color-text-muted` | `text-text-muted` |
| `--color-danger` | `text-danger`, `bg-danger` |
| `--color-warning` | `text-warning` |

---

## 8. Mobile-First Responsive Design

IQFarm targets low-end Android phones (375 px minimum width).

```html
<!-- ✅ Mobile-first grid: 1 col on mobile, 2 on sm, 3 on lg -->
<div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
  ...
</div>

<!-- ✅ Touch targets ≥ 44px -->
<button class="min-h-[44px] px-4 py-2">Tap me</button>

<!-- ✅ Reduced motion -->
<div class="transition-transform duration-200 motion-reduce:transition-none">
  ...
</div>
```

---

## 9. i18n Readiness

Every user-visible string must have an `i18n` attribute:

```html
<h1 i18n="@@dashboard.title">My Farm Dashboard</h1>
<p  i18n="@@dashboard.subtitle">Manage your crops and weather alerts.</p>
<button i18n="@@action.save">Save Farm</button>
```

The attribute format is `@@<page>.<key>` for easy extraction with `ng extract-i18n`.

---

## 10. Verification Checklist

Before finishing an FE feature:

- [ ] Component is `standalone: true`
- [ ] All browser APIs are guarded with `isPlatformBrowser()`
- [ ] No `window`, `document`, or `localStorage` accessed outside the guard
- [ ] All HTTP calls go through a `core/services/` service
- [ ] Service returns `Observable<ApiResponse<T>>` (not `any`)
- [ ] `environment.apiBaseUrl` is used — no hardcoded `localhost:8080`
- [ ] Tailwind design tokens used (no inline `style=""` with hex colours)
- [ ] Every user-visible string has an `i18n` attribute
- [ ] Touch targets are ≥ 44 px
- [ ] `npm run lint` passes with zero errors
- [ ] `npm run build` completes with zero TypeScript errors

---

## 11. Environment Files

```typescript
// src/environments/environment.ts  (development)
export const environment = {
  production: false,
  apiBaseUrl: 'http://localhost:8080/api',
};

// src/environments/environment.prod.ts  (production)
export const environment = {
  production: true,
  apiBaseUrl: 'https://api.iqfarm.app/api',   // update when deployed
};
```

Use `environment.apiBaseUrl` everywhere. Never hardcode the backend URL in a service.

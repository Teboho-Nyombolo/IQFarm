import { Routes } from '@angular/router';

export const routes: Routes = [
  // Root redirect — points to the dashboard home page
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  {
    path: 'home',
    loadChildren: () =>
      import('./features/dashboard/dashboard.routes').then((m) => m.DASHBOARD_ROUTES),
  },
  {
    path: 'weather',
    loadChildren: () =>
      import('./features/weather/weather.routes').then((m) => m.WEATHER_ROUTES),
  },
];



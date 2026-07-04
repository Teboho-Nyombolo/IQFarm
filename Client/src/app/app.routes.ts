import { Routes } from '@angular/router';

export const routes: Routes = [
  // Redirect root to weather until the full dashboard shell exists
  { path: '', redirectTo: 'weather', pathMatch: 'full' },
  {
    path: 'weather',
    loadChildren: () =>
      import('./features/weather/weather.routes').then((m) => m.WEATHER_ROUTES),
  },
];


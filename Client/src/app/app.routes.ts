import { Routes } from '@angular/router';
import { LandingPageComponent } from './features/landing-page/landing-page.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { FarmComponent } from './features/auth/farm/farm.component';
import { RegisterComponent } from './features/auth/regiter/register.component';
import { WeatherComponent } from './features/weather/weather.component';

export const routes: Routes = [
  {
    path: '',
    component: LandingPageComponent
  },
  {
    path: 'dashboard',
    component: DashboardComponent
  },
  {
    path: 'dashboard/weather',
    component: WeatherComponent
  },
  {
    path: 'farmInfo',
    component: FarmComponent
  }, {
    path: 'register',
    component: RegisterComponent
  }
];

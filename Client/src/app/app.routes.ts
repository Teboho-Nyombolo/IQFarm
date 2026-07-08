import { Routes } from '@angular/router';
import { LandingPageComponent } from './features/landing-page/landing-page.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { FarmComponent } from './features/auth/farm/farm.component';
import { RegisterComponent } from './features/auth/regiter/register.component';
import { CropsComponent } from './features/crops/crops.component';
import { AddCropComponent } from './features/crops/add-crop/add-crop.component';
import { WeatherComponent } from './features/weather/weather.component';
import { PlantAdvisoryComponent } from './features/plant-advisory/plant-advisory.component';
import { SeasonalInsightsComponent } from './features/seasonal-insights/seasonal-insights.component';
import { CropDetailsComponent } from './features/crops/crop-details/crop-details.component';
import { CropInspectComponent } from './features/crops/crop-inspect/crop-inspect.component';
import { ProfileComponent } from './features/profile/profile.component';

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
    },
    {
        path: 'register',
        component: RegisterComponent
    },
    {
        path: 'crops',
        component: CropsComponent
    },
    {
        path: 'crops/add',
        component: AddCropComponent
    },
    {
        path: 'plant-advisory',
        component: PlantAdvisoryComponent
    },
    {
        path: 'seasonal-insights',
        component: SeasonalInsightsComponent
    },
    {
        path: 'crops/:id',
        component: CropDetailsComponent
    },
    {
        path: 'crops/:id/inspect',
        component: CropInspectComponent
    },{
        path: 'profile',
        component: ProfileComponent
    }
];

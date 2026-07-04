import {
  Component,
  computed,
  inject,
  OnInit,
  PLATFORM_ID,
  signal,
} from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { WeatherService } from '../weather/services/weather.service';
import { CropService } from './services/crop.service';
import { DailyWeather } from '../weather/models/weather.model';
import { CropSummary, DiseaseReference } from './models/dashboard.model';
import { NgClass } from '@angular/common';

/** Maps a crop status to a soil-moisture proxy percentage (0–100). */
function statusToMoisturePercent(status: CropSummary['status']): number {
  switch (status) {
    case 'ACTIVE':    return 78;
    case 'PLANNING':  return 40;
    case 'HARVESTED': return 100;
    case 'FAILED':    return 10;
    default:          return 50;
  }
}

/** Returns a time-of-day greeting string. */
function getGreeting(): string {
  const h = new Date().getHours();
  if (h < 12) return 'Good morning';
  if (h < 17) return 'Good afternoon';
  return 'Good evening';
}

/** Returns today's date formatted as "Month Day, Year". */
function todayLabel(): string {
  return new Date().toLocaleDateString('en-US', {
    month: 'long',
    day: 'numeric',
    year: 'numeric',
  });
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [NgClass, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent implements OnInit {
  readonly #weatherService = inject(WeatherService);
  readonly #cropService = inject(CropService);
  readonly #platformId = inject(PLATFORM_ID);

  // ─── Greeting ──────────────────────────────────────────────
  readonly greeting = getGreeting();
  readonly dateLabel = todayLabel();

  // ─── State ─────────────────────────────────────────────────
  readonly weather = signal<DailyWeather | null>(null);
  readonly crops = signal<CropSummary[]>([]);
  readonly isLoadingWeather = signal(true);
  readonly isLoadingCrops = signal(true);

  // ─── Derived: Crop Health ───────────────────────────────────
  readonly activeCropsCount = computed(() =>
    this.crops().filter((c) => c.status === 'ACTIVE').length
  );

  /** Overall status badge text driven by crop statuses. */
  readonly overallCropStatus = computed<string>(() => {
    const list = this.crops();
    if (!list.length) return 'No Data';
    const failed = list.some((c) => c.status === 'FAILED');
    if (failed) return 'At Risk';
    const allActive = list.every((c) => c.status === 'ACTIVE');
    if (allActive) return 'Optimal';
    return 'Fair';
  });

  /** Soil moisture percentage from the primary active crop's status. */
  readonly soilMoisturePercent = computed<number>(() => {
    const primary = this.crops().find((c) => c.status === 'ACTIVE') ?? this.crops()[0];
    return primary ? statusToMoisturePercent(primary.status) : 0;
  });

  // ─── Static Disease Data ────────────────────────────────────
  readonly diseases: DiseaseReference[] = [
    {
      crop: 'Tomato',
      disease: 'Late Blight',
      severity: 'HIGH ALERT',
      description: 'Rapidly spreading fungal infection favoured by high humidity. Monitor Sector 4B.',
      imageSrc: 'assets/diseases/tomato-late-blight.png',
    },
    {
      crop: 'Wheat',
      disease: 'Yellow Rust',
      severity: 'MODERATE',
      description: 'Stripe-like yellow pustules on leaves. Check irrigation frequency in lowlands.',
      imageSrc: 'assets/diseases/wheat-yellow-rust.png',
    },
    {
      crop: 'Potato',
      disease: 'Common Scab',
      severity: 'IDLE',
      description: 'Surface-level lesions affecting marketability. Soil pH management recommended.',
      imageSrc: 'assets/diseases/potato-common-scab.png',
    },
  ];

  // ─── Lifecycle ──────────────────────────────────────────────
  ngOnInit(): void {
    if (!isPlatformBrowser(this.#platformId)) {
      this.isLoadingWeather.set(false);
      this.isLoadingCrops.set(false);
      return;
    }

    // TODO: Replace farmId=1 with auth-derived ID once auth is implemented.
    forkJoin({
      weather: this.#weatherService.getWeather(1),
      crops: this.#cropService.getCropsByFarm(1),
    }).subscribe({
      next: ({ weather, crops }) => {
        this.weather.set(weather.dailyWeather[0] ?? null);
        this.crops.set(crops);
        this.isLoadingWeather.set(false);
        this.isLoadingCrops.set(false);
      },
      error: () => {
        this.isLoadingWeather.set(false);
        this.isLoadingCrops.set(false);
      },
    });
  }

  round(n: number): number {
    return Math.round(n);
  }
}

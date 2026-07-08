import { Component, computed, inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { isPlatformBrowser, NgClass } from '@angular/common';
import { Router } from '@angular/router';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { LogoComponent } from '../../shared/components/logo/logo.component';
import { WeatherService } from './services/weather.service';
import { DailyWeather } from './models/weather.model';

/**
 * Maps the free-text condition string returned by Open-Meteo (via the server)
 * to a Material Symbols icon name.
 */
function conditionToIcon(condition: string): string {
  const c = condition.toLowerCase();
  if (c.includes('thunder') || c.includes('storm')) return 'thunderstorm';
  if (c.includes('rain') || c.includes('shower') || c.includes('drizzle')) return 'rainy';
  if (c.includes('cloud') && c.includes('sun')) return 'partly_cloudy_day';
  if (c.includes('cloud') || c.includes('overcast')) return 'cloud';
  if (c.includes('snow') || c.includes('sleet') || c.includes('blizzard')) return 'ac_unit';
  if (c.includes('fog') || c.includes('mist') || c.includes('haze')) return 'foggy';
  // default / sunny
  return 'sunny';
}

/** Abbreviates a yyyy-MM-dd date string to a 3-letter day name, e.g. "Mon". */
function dateToDayLabel(dateStr: string): string {
  const days = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
  const d = new Date(dateStr + 'T00:00:00'); // force local midnight to avoid UTC offset issues
  return days[d.getDay()];
}

@Component({
  selector: 'app-weather',
  standalone: true,
  imports: [NgClass, NavbarComponent, LogoComponent],
  templateUrl: './weather.component.html',
  styleUrl: './weather.component.css',
})
export class WeatherComponent implements OnInit {
  readonly #weatherService = inject(WeatherService);
  readonly #platformId = inject(PLATFORM_ID);
  readonly #router = inject(Router);

  // ─── State ─────────────────────────────────────────────────
  readonly dailyWeather = signal<DailyWeather[]>([]);
  readonly isLoading = signal(true);
  readonly errorMessage = signal<string | null>(null);
  readonly activeDayIndex = signal(0);

  // ─── Derived values ────────────────────────────────────────

  /** Today's weather entry (first in the array). */
  readonly today = computed<DailyWeather | null>(() => {
    const list = this.dailyWeather();
    return list.length > 0 ? list[0] : null;
  });

  /** Material Symbols icon name for today's condition. */
  readonly todayIcon = computed(() => conditionToIcon(this.today()?.condition ?? ''));

  /** UV index label derived from today's max temp (placeholder logic). */
  readonly uvLabel = computed(() => {
    const t = this.today()?.highTemp ?? 0;
    if (t >= 30) return 'High';
    if (t >= 22) return 'Medium';
    return 'Low';
  });

  /** Day cards list for the 7-day outlook scroller. */
  readonly dayCards = computed(() =>
    this.dailyWeather().map((d, i) => ({
      index: i,
      label: dateToDayLabel(d.date),
      icon: conditionToIcon(d.condition),
      high: Math.round(d.highTemp),
      low: Math.round(d.lowTemp),
    }))
  );

  // ─── Lifecycle ──────────────────────────────────────────────
  ngOnInit(): void {
    if (!isPlatformBrowser(this.#platformId)) {
      // Skip API call during SSR; data will load on the browser.
      this.isLoading.set(false);
      return;
    }

    // TODO: Replace hardcoded farmId=1 with auth-derived farm ID once auth is implemented.
    this.#weatherService.getWeather(1).subscribe({
      next: (res) => {
        this.dailyWeather.set(res.dailyWeather ?? []);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set('Unable to load weather data. Please try again.');
        this.isLoading.set(false);
      },
    });
  }

  // ─── Interactions ───────────────────────────────────────────
  selectDay(index: number): void {
    this.activeDayIndex.set(index);
  }

  /** Expose helper for template use. */
  round(n: number): number {
    return Math.round(n);
  }

  viewSeasonalInsights(): void {
    this.#router.navigate(['/seasonal-insights']);
  }

    goToProfile():void {
    this.#router.navigate(['/profile']);
  }
}

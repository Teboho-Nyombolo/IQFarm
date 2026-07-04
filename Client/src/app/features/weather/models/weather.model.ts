/**
 * Mirrors the server-side DailyWeatherDTO record.
 * date is serialised as "yyyy-MM-dd" by Jackson.
 */
export interface DailyWeather {
  date: string;
  highTemp: number;
  lowTemp: number;
  precipitation: number;
  windSpeed: number;
  humidity: number;
  condition: string;
}

/**
 * Mirrors the server-side WeatherResponseDTO class.
 */
export interface WeatherResponse {
  dailyWeather: DailyWeather[];
}

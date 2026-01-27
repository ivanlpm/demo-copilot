export interface WeatherData {
  city: string;
  main: {
    temp: number;
    feels_like: number;
    humidity: number;
  };
  weather: Array<{
    main: string;
    description: string;
    icon: string;
  }>;
  cod: number;
}

export interface WeatherError {
  error: string;
}

export interface GeolocationCoordinates {
  lat: number;
  lon: number;
}

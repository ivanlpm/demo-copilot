export interface WeatherData {
  name: string;
  main: {
    temp: number;
    feels_like: number;
    humidity: number;
    temp_min: number;
    temp_max: number;
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

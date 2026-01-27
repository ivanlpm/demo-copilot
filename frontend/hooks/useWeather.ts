import { useState, useEffect } from 'react';
import { WeatherData, WeatherError, GeolocationCoordinates } from '@/types/weather';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';

export function useWeather(city?: string) {
  const [weather, setWeather] = useState<WeatherData | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchWeather = async (cityName: string) => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch(`${API_BASE_URL}/api/weather/${encodeURIComponent(cityName)}`);
      
      if (response.status === 429) {
        throw new Error('Rate limit exceeded. Please try again later.');
      }
      
      if (!response.ok) {
        const errorData: WeatherError = await response.json();
        throw new Error(errorData.error || 'Failed to fetch weather');
      }

      const data: WeatherData = await response.json();
      setWeather(data);
    } catch (err) {
      const message = err instanceof Error ? err.message : 'An unexpected error occurred';
      setError(message);
      setWeather(null);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (city) {
      fetchWeather(city);
    }
  }, [city]);

  return { weather, loading, error, fetchWeather };
}

export function useWeatherByCoordinates() {
  const [weather, setWeather] = useState<WeatherData | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchWeatherByCoordinates = async (coords: GeolocationCoordinates) => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch(
        `${API_BASE_URL}/api/weather/coordinates?lat=${coords.lat}&lon=${coords.lon}`
      );
      
      if (response.status === 429) {
        throw new Error('Rate limit exceeded. Please try again later.');
      }
      
      if (!response.ok) {
        const errorData: WeatherError = await response.json();
        throw new Error(errorData.error || 'Failed to fetch weather');
      }

      const data: WeatherData = await response.json();
      setWeather(data);
    } catch (err) {
      const message = err instanceof Error ? err.message : 'An unexpected error occurred';
      setError(message);
      setWeather(null);
    } finally {
      setLoading(false);
    }
  };

  return { weather, loading, error, fetchWeatherByCoordinates };
}

export function useGeolocation() {
  const [coordinates, setCoordinates] = useState<GeolocationCoordinates | null>(null);
  const [permissionStatus, setPermissionStatus] = useState<'prompt' | 'granted' | 'denied'>('prompt');
  const [error, setError] = useState<string | null>(null);

  const requestLocation = () => {
    if (!navigator.geolocation) {
      setError('Geolocation is not supported by your browser');
      return;
    }

    navigator.geolocation.getCurrentPosition(
      (position) => {
        setCoordinates({
          lat: position.coords.latitude,
          lon: position.coords.longitude,
        });
        setPermissionStatus('granted');
        setError(null);
      },
      (err) => {
        switch (err.code) {
          case err.PERMISSION_DENIED:
            setError('Location permission denied. Please enable location access.');
            setPermissionStatus('denied');
            break;
          case err.POSITION_UNAVAILABLE:
            setError('Location information unavailable.');
            break;
          case err.TIMEOUT:
            setError('Location request timed out.');
            break;
          default:
            setError('An unknown error occurred.');
        }
      },
      {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 300000, // 5 minutes cache
      }
    );
  };

  return { coordinates, permissionStatus, error, requestLocation };
}

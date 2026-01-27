'use client';

import { useState, useEffect } from 'react';
import { useWeather, useWeatherByCoordinates, useGeolocation } from '@/hooks/useWeather';
import { WeatherCard } from './ui/WeatherCard';

const SUPPORTED_CITIES = ['Malaga', 'Madrid', 'Barcelona', 'Valencia', 'Seville'];

export function WeatherWidget() {
  const [selectedCity, setSelectedCity] = useState<string>('Malaga');
  const [useLocation, setUseLocation] = useState(false);
  
  const { weather: cityWeather, loading: cityLoading, error: cityError, fetchWeather } = useWeather();
  const { weather: geoWeather, loading: geoLoading, error: geoError, fetchWeatherByCoordinates } = useWeatherByCoordinates();
  const { coordinates, permissionStatus, error: locationError, requestLocation } = useGeolocation();

  // Fetch initial city weather
  useEffect(() => {
    if (!useLocation) {
      fetchWeather(selectedCity);
    }
  }, [selectedCity, useLocation]);

  // Fetch weather by geolocation when coordinates are available
  useEffect(() => {
    if (useLocation && coordinates) {
      fetchWeatherByCoordinates(coordinates);
    }
  }, [coordinates, useLocation]);

  const handleCityChange = (city: string) => {
    setSelectedCity(city);
    setUseLocation(false);
  };

  const handleLocationToggle = () => {
    if (!useLocation) {
      requestLocation();
      setUseLocation(true);
    } else {
      setUseLocation(false);
    }
  };

  const weather = useLocation ? geoWeather : cityWeather;
  const loading = useLocation ? geoLoading : cityLoading;
  const error = useLocation ? (geoError || locationError) : cityError;

  return (
    <div className="space-y-4">
      <div className="flex flex-col sm:flex-row gap-4 items-start sm:items-center">
        <div className="flex flex-wrap gap-2">
          {SUPPORTED_CITIES.map((city) => (
            <button
              key={city}
              onClick={() => handleCityChange(city)}
              disabled={useLocation}
              className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                selectedCity === city && !useLocation
                  ? 'bg-blue-600 text-white'
                  : 'bg-gray-200 text-gray-700 hover:bg-gray-300 disabled:opacity-50 disabled:cursor-not-allowed'
              }`}
            >
              {city}
            </button>
          ))}
        </div>

        <button
          onClick={handleLocationToggle}
          className={`px-4 py-2 rounded-lg font-medium transition-colors flex items-center gap-2 ${
            useLocation
              ? 'bg-green-600 text-white'
              : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
          }`}
        >
          <span>📍</span>
          {useLocation ? 'Using Location' : 'Use My Location'}
        </button>
      </div>

      {permissionStatus === 'denied' && (
        <div className="bg-yellow-100 border-l-4 border-yellow-500 text-yellow-700 p-4 rounded">
          <p className="font-bold">Location Permission Denied</p>
          <p className="text-sm">Please enable location access in your browser settings to use this feature.</p>
        </div>
      )}

      {loading && (
        <div className="flex justify-center items-center p-8">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>
      )}

      {error && !loading && (
        <div className="bg-red-100 border-l-4 border-red-500 text-red-700 p-4 rounded">
          <p className="font-bold">Error</p>
          <p>{error}</p>
        </div>
      )}

      {weather && !loading && !error && <WeatherCard weather={weather} />}
    </div>
  );
}

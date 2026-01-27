'use client';

import { useEffect } from 'react';
import { useWeatherByCoordinates, useGeolocation } from '@/hooks/useWeather';
import { WeatherCard } from './ui/WeatherCard';

export function WeatherWidget() {
  const { weather, loading, error, fetchWeatherByCoordinates } = useWeatherByCoordinates();
  const { coordinates, permissionStatus, requestLocation } = useGeolocation();

  // Automatically request location on mount
  useEffect(() => {
    requestLocation();
  }, []);

  // Fetch weather when coordinates are available
  useEffect(() => {
    if (coordinates) {
      fetchWeatherByCoordinates(coordinates);
    }
  }, [coordinates]);

  if (permissionStatus === 'denied') {
    return (
      <div className="w-40 h-40 bg-zinc-800 rounded-[2.5rem] p-4 text-white flex flex-col items-center justify-center text-center gap-2">
        <span className="text-2xl">📍</span>
        <p className="text-[10px] opacity-70">Location access denied</p>
      </div>
    );
  }

  if (loading || !weather) {
    return (
      <div className="w-40 h-40 bg-zinc-800 rounded-[2.5rem] p-4 text-white flex items-center justify-center">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-white/20"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="w-40 h-40 bg-red-900/50 rounded-[2.5rem] p-4 text-white flex flex-col items-center justify-center text-center gap-1">
        <span className="text-xl">⚠️</span>
        <p className="text-[10px] font-bold">Failed to load</p>
      </div>
    );
  }

  return <WeatherCard weather={weather} />;
}

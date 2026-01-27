'use client';

import { WeatherData } from '@/types/weather';

interface WeatherCardProps {
  weather: WeatherData;
}

export function WeatherCard({ weather }: WeatherCardProps) {
  const mainWeather = weather.weather[0];
  const iconUrl = `https://openweathermap.org/img/wn/${mainWeather.icon}@2x.png`;

  return (
    <div className="bg-gradient-to-br from-blue-500 to-blue-700 rounded-lg shadow-lg p-6 text-white max-w-sm">
      <h2 className="text-2xl font-bold mb-2">{weather.city}</h2>
      
      <div className="flex items-center justify-between mb-4">
        <div>
          <p className="text-5xl font-bold">{Math.round(weather.main.temp)}°C</p>
          <p className="text-sm opacity-80">Feels like {Math.round(weather.main.feels_like)}°C</p>
        </div>
        <img 
          src={iconUrl} 
          alt={mainWeather.description}
          className="w-24 h-24"
        />
      </div>

      <div className="border-t border-white/20 pt-4">
        <p className="text-xl capitalize mb-2">{mainWeather.description}</p>
        <div className="flex justify-between text-sm">
          <span>Humidity: {weather.main.humidity}%</span>
        </div>
      </div>
    </div>
  );
}

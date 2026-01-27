'use client';

import { WeatherData } from '@/types/weather';
import Image from 'next/image';

interface WeatherCardProps {
  weather: WeatherData;
}

export function WeatherCard({ weather }: WeatherCardProps) {
  const mainWeather = weather.weather[0];
  const iconUrl = `https://openweathermap.org/img/wn/${mainWeather.icon}@2x.png`;

  return (
    <div className="w-40 h-40 bg-[#3a4454] rounded-[2.5rem] p-5 text-white shadow-2xl flex flex-col justify-between transition-all hover:scale-105 select-none">
      <div className="space-y-0.5">
        <h3 className="text-[17px] font-semibold tracking-tight leading-tight">{weather.name}</h3>
        <p className="text-5xl font-light tracking-tighter">
          {Math.round(weather.main.temp)}°
        </p>
      </div>
      
      <div className="mt-auto">
        <div className="flex items-center gap-1">
          <div className="relative w-7 h-7">
            <Image 
              src={iconUrl} 
              alt={mainWeather.description}
              fill
              className="object-contain"
            />
          </div>
          <span className="text-[13px] font-medium capitalize truncate">
            {mainWeather.main}
          </span>
        </div>
        
        <div className="flex gap-2 text-[11px] font-bold opacity-60 mt-0.5">
          <span>H:{Math.round(weather.main.temp_max)}°</span>
          <span>L:{Math.round(weather.main.temp_min)}°</span>
        </div>
      </div>
    </div>
  );
}

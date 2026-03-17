"use client";

import React, { useState } from "react";
import Image from "next/image";

/**
 * Props for the DogCard component.
 */
interface DogCardProps {
  url: string;
  breed: string;
  loading: boolean;
}

/**
 * A stylized card component to display a dog.
 */
export const DogCard: React.FC<DogCardProps> = ({ url, breed, loading }) => {
  const [imageLoaded, setImageLoaded] = useState(false);

  const formattedBreed = breed
    ? breed.replace(/-/g, " ").replace(/\b\w/g, (c) => c.toUpperCase())
    : "";

  return (
    <div className="group relative w-full max-w-sm overflow-hidden rounded-3xl border-2 border-amber-100 bg-white shadow-xl transition-all duration-300 hover:border-amber-200 hover:shadow-2xl dark:border-zinc-800 dark:bg-zinc-950 sm:max-w-md md:hover:-translate-y-2 lg:max-w-lg">
      {/* Loading Skeleton or Image */}
      <div className="relative aspect-square w-full bg-zinc-100 dark:bg-zinc-900">
        {loading ? (
          <div className="flex h-full w-full items-center justify-center">
            <div className="h-10 w-10 animate-spin rounded-full border-4 border-amber-500 border-t-transparent md:h-12 md:w-12"></div>
          </div>
        ) : (
          url && (
            <Image
              src={url}
              alt={`Random ${formattedBreed} dog`}
              fill
              className={`object-cover transition-opacity duration-500 ${imageLoaded ? "opacity-100" : "opacity-0"}`}
              onLoad={() => setImageLoaded(true)}
            />
          )
        )}
      </div>

      {/* Card Content */}
      <div className="p-6 md:p-8">
        <div className="flex items-center justify-between gap-4">
          <h3 className="text-xl font-black text-zinc-900 dark:text-zinc-50 md:text-2xl">
            {loading ? "Dog Discovery" : formattedBreed || "Dog Discovery"}
          </h3>
          <span className="shrink-0 rounded-full bg-amber-100 px-2.5 py-0.5 text-[10px] font-bold text-amber-600 dark:bg-amber-900/40 dark:text-amber-300 md:px-3 md:py-1 md:text-xs">
            NEW
          </span>
        </div>
        <p className="mt-3 text-sm leading-relaxed text-zinc-600 dark:text-zinc-400 md:text-base">
          {loading
            ? "Fetching a new furry friend..."
            : `A ${formattedBreed || "dog"} captured in all its glory.`}
        </p>

        <div className="mt-6 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <span className="flex h-2.5 w-2.5 animate-pulse rounded-full bg-green-500 md:h-3 md:w-3"></span>
            <span className="text-[10px] font-bold text-zinc-400 uppercase tracking-widest md:text-xs">
              Live from dog.ceo
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};

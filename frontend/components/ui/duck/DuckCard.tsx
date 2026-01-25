"use client";

import React from "react";

/**
 * Props for the DuckCard component.
 */
interface DuckCardProps {
  url: string;
  message: string;
  loading: boolean;
}

/**
 * A stylized card component to display a duck.
 */
export const DuckCard: React.FC<DuckCardProps> = ({ url, message, loading }) => {
  return (
    <div className="group relative w-full max-w-md overflow-hidden rounded-3xl border-2 border-orange-100 bg-white shadow-xl transition-all duration-300 hover:-translate-y-2 hover:border-orange-200 hover:shadow-2xl dark:border-zinc-800 dark:bg-zinc-950">
      {/* Loading Skeleton or Image */}
      <div className="relative aspect-square w-full bg-zinc-100 dark:bg-zinc-900">
        {loading ? (
          <div className="flex h-full w-full items-center justify-center">
            <div className="h-12 w-12 animate-spin rounded-full border-4 border-orange-500 border-t-transparent"></div>
          </div>
        ) : (
          <img
            src={url}
            alt="Random Duck"
            className="h-full w-full object-cover transition-opacity duration-500"
            onLoad={(e) => (e.currentTarget.style.opacity = "1")}
            style={{ opacity: 0 }}
          />
        )}
      </div>

      {/* Card Content */}
      <div className="p-8">
        <div className="flex items-center justify-between">
          <h3 className="text-2xl font-black text-zinc-900 dark:text-zinc-50">
            Duck Discovery
          </h3>
          <span className="rounded-full bg-orange-100 px-3 py-1 text-xs font-bold text-orange-600 dark:bg-orange-900/40 dark:text-orange-300">
            NEW
          </span>
        </div>
        <p className="mt-3 text-base leading-relaxed text-zinc-600 dark:text-zinc-400">
          {message || "Watching this magnificent creature in its natural habitat."}
        </p>
        
        <div className="mt-6 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <span className="flex h-3 w-3 animate-pulse rounded-full bg-green-500"></span>
            <span className="text-xs font-bold text-zinc-400 uppercase tracking-widest">
              Live from random-d.uk
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};

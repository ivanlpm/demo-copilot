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
    <div className="group relative w-full max-w-md overflow-hidden rounded-2xl bg-white shadow-xl transition-all duration-300 hover:-translate-y-2 hover:shadow-2xl dark:bg-zinc-900">
      {/* Loading Skeleton or Image */}
      <div className="relative aspect-square w-full bg-zinc-100 dark:bg-zinc-800">
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
      <div className="p-6">
        <h3 className="text-xl font-bold text-zinc-900 dark:text-zinc-50">
          Random Duck Found!
        </h3>
        <p className="mt-2 text-sm text-zinc-600 dark:text-zinc-400">
          {message || "No message provided."}
        </p>
        
        <div className="mt-4 flex items-center gap-2">
          <span className="flex h-2 w-2 rounded-full bg-green-500"></span>
          <span className="text-xs font-medium text-zinc-500 uppercase tracking-wider">
            Sourced from random-d.uk
          </span>
        </div>
      </div>
    </div>
  );
};

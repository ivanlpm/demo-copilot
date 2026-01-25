"use client";

import { useDuck } from "../hooks/useDuck";
import { DuckCard } from "../components/ui/duck/DuckCard";

/**
 * Main application page to discover new ducks.
 */
export default function Home() {
  const { duck, loading, error, fetchDuck } = useDuck();

  return (
    <div className="flex min-h-screen flex-col items-center bg-zinc-50 px-4 py-12 dark:bg-black font-sans">
      {/* Hero Section */}
      <header className="mb-16 text-center">
        <div className="inline-block rounded-full bg-orange-100 px-4 py-1.5 text-sm font-semibold text-orange-600 dark:bg-orange-900/30 dark:text-orange-400 mb-6">
          Quack Quack! 🦆
        </div>
        <h1 className="text-5xl font-black tracking-tight text-zinc-900 dark:text-white sm:text-7xl">
          The <span className="text-orange-500">Duck</span> Gallery
        </h1>
        <p className="mx-auto mt-6 max-w-2xl text-lg text-zinc-600 dark:text-zinc-400">
          A dedicated space to enjoy the world's most beautiful ducks.
        </p>
      </header>

      {/* Main content area */}
      <main className="flex flex-col items-center gap-10">
        {error ? (
          <div className="rounded-xl border border-red-200 bg-red-50 p-6 text-center text-red-700 dark:border-red-900/50 dark:bg-red-900/20 dark:text-red-400">
            <h2 className="text-lg font-bold">Connection Error</h2>
            <p className="mt-1">{error}</p>
            <button 
              onClick={fetchDuck}
              className="mt-4 rounded-lg bg-red-600 px-4 py-2 font-semibold text-white transition-colors hover:bg-red-700"
            >
              Try Again
            </button>
          </div>
        ) : (
          <DuckCard 
            url={duck?.url || ""} 
            message={duck?.message || ""} 
            loading={loading} 
          />
        )}

        {/* Interaction Button */}
        <button
          onClick={fetchDuck}
          disabled={loading}
          className="group relative h-16 w-64 items-center justify-center overflow-hidden rounded-full bg-zinc-900 text-lg font-bold text-white shadow-xl transition-all hover:bg-zinc-800 active:scale-95 disabled:opacity-50 dark:bg-white dark:text-black dark:hover:bg-zinc-200"
        >
          <span className="relative z-10 flex items-center justify-center gap-2">
            {loading ? "Discovering..." : "Get Another Duck"}
            {!loading && <span className="text-2xl">✨</span>}
          </span>
        </button>
      </main>

      {/* Simple Footer */}
      <footer className="mt-24 border-t border-zinc-200 pt-8 text-center text-zinc-400 dark:border-zinc-800">
        <p className="text-sm uppercase tracking-widest font-medium">
          ivanlpm © {new Date().getFullYear()}
        </p>
      </footer>
    </div>
  );
}

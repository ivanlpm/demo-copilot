"use client";

import React from "react";
import Link from "next/link";

export const Navbar: React.FC = () => {
  return (
    <nav className="fixed top-0 left-0 right-0 z-50 bg-white/80 backdrop-blur-md border-b border-zinc-200 dark:bg-black/80 dark:border-zinc-800">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16 items-center">
          <div className="flex-shrink-0 flex items-center">
            <span className="text-2xl font-black text-orange-500">🦆 Gallery</span>
          </div>
          <div className="hidden sm:ml-6 sm:flex sm:space-x-8">
            <Link
              href="/"
              className="inline-flex items-center px-1 pt-1 border-b-2 border-orange-500 text-sm font-bold text-zinc-900 dark:text-white"
            >
              Duck Gallery
            </Link>
            <div className="group relative inline-flex items-center px-1 pt-1 border-b-2 border-transparent text-sm font-medium text-zinc-400 cursor-not-allowed">
              Bear Gallery
              <span className="ml-2 rounded-md bg-zinc-100 px-1.5 py-0.5 text-[10px] font-bold text-zinc-500 dark:bg-zinc-800 dark:text-zinc-500">
                SOON
              </span>
              <div className="absolute top-full left-0 mt-2 hidden group-hover:block w-48 rounded-md bg-white p-2 shadow-lg ring-1 ring-black ring-opacity-5 dark:bg-zinc-900">
                <p className="text-[10px] text-zinc-500">The bears are still hibernating...</p>
              </div>
            </div>
          </div>
          <div className="sm:hidden flex items-center">
            {/* Mobile menu button could go here */}
            <span className="text-xs text-zinc-400">v1.0</span>
          </div>
        </div>
      </div>
    </nav>
  );
};

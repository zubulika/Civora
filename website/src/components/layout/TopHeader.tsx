'use client';

import React from 'react';
import Link from 'next/link';
import { Plus, Search } from 'lucide-react';

interface TopHeaderProps {
  title: string;
  subtitle?: string;
  onSearch?: (query: string) => void;
}

export default function TopHeader({ title, subtitle, onSearch }: TopHeaderProps) {
  return (
    <header className="h-16 bg-white border-b border-slate-200 px-6 flex items-center justify-between sticky top-0 z-20">
      <div>
        <h1 className="text-base font-semibold text-slate-900">{title}</h1>
        {subtitle && <p className="text-xs text-slate-500">{subtitle}</p>}
      </div>

      <div className="flex items-center gap-3">
        {onSearch && (
          <div className="relative w-64">
            <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              placeholder="Search users..."
              onChange={(e) => onSearch(e.target.value)}
              className="w-full pl-9 pr-3 py-1.5 bg-slate-50 border border-slate-200 rounded-lg text-xs text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-600/20 focus:border-emerald-700 transition"
            />
          </div>
        )}

        <Link
          href="/users/new"
          className="inline-flex items-center gap-1.5 bg-emerald-700 hover:bg-emerald-800 text-white px-3 py-1.5 rounded-lg text-xs font-medium transition cursor-pointer"
        >
          <Plus className="w-3.5 h-3.5" />
          <span>Add User</span>
        </Link>
      </div>
    </header>
  );
}

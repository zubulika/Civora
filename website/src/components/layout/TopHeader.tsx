'use client';

import React from 'react';
import Link from 'next/link';
import { Plus, Bell, Shield, Search } from 'lucide-react';

interface TopHeaderProps {
  title: string;
  subtitle?: string;
  onSearch?: (query: string) => void;
}

export default function TopHeader({ title, subtitle, onSearch }: TopHeaderProps) {
  return (
    <header className="h-20 bg-white border-b border-gray-200/80 px-8 flex items-center justify-between sticky top-0 z-20 shadow-xs">
      <div>
        <h1 className="text-xl font-bold text-gray-900 tracking-tight">{title}</h1>
        {subtitle && <p className="text-xs text-gray-500 mt-0.5">{subtitle}</p>}
      </div>

      <div className="flex items-center gap-4">
        {/* Quick Search */}
        {onSearch && (
          <div className="relative w-72">
            <Search className="w-4 h-4 text-gray-400 absolute left-3 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              placeholder="Search by ID, Name or Sponsor..."
              onChange={(e) => onSearch(e.target.value)}
              className="w-full pl-9 pr-4 py-2 bg-gray-50 border border-gray-200 rounded-xl text-xs focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-600 transition-all placeholder:text-gray-400"
            />
          </div>
        )}

        {/* Issue ID Quick Action Button */}
        <Link
          href="/users/new"
          className="flex items-center gap-2 bg-[#056839] hover:bg-[#04522d] text-white px-4 py-2 rounded-xl text-xs font-semibold shadow-md shadow-emerald-950/10 transition-colors"
        >
          <Plus className="w-3.5 h-3.5" />
          <span>Issue Digital ID</span>
        </Link>

        {/* Security Indicator */}
        <div className="flex items-center gap-2 pl-3 border-l border-gray-200 text-xs text-gray-600">
          <div className="w-7 h-7 rounded-full bg-emerald-50 border border-emerald-200 flex items-center justify-center text-emerald-700">
            <Shield className="w-3.5 h-3.5" />
          </div>
          <div className="hidden sm:block text-right">
            <span className="text-[11px] font-medium text-emerald-700 block leading-tight">Ministry Portal</span>
            <span className="text-[10px] text-gray-400">Authenticated 2FA</span>
          </div>
        </div>
      </div>
    </header>
  );
}

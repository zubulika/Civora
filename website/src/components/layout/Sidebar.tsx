'use client';

import React from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { 
  LayoutDashboard, 
  Users, 
  UserPlus, 
  FileCheck2, 
  ShieldCheck, 
  ExternalLink,
  LogOut,
  Building2
} from 'lucide-react';

const NAV_ITEMS = [
  { href: '/', label: 'Dashboard', icon: LayoutDashboard },
  { href: '/users', label: 'Citizens & Residents', icon: Users },
  { href: '/users/new', label: 'Issue Digital ID', icon: UserPlus },
  { href: '/documents', label: 'Issued Documents', icon: FileCheck2 },
];

export default function Sidebar() {
  const pathname = usePathname();

  return (
    <aside className="w-64 bg-[#0a2c21] text-white flex flex-col border-r border-[#144233] h-screen sticky top-0 shrink-0">
      {/* Brand Header */}
      <div className="p-6 border-b border-[#144233]">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-[#00a651] to-[#056839] flex items-center justify-center shadow-lg shadow-emerald-950/40">
            <Building2 className="w-6 h-6 text-white" />
          </div>
          <div>
            <div className="font-bold text-lg tracking-wide flex items-center gap-1.5">
              <span>Absher</span>
              <span className="text-[10px] uppercase tracking-wider px-1.5 py-0.5 rounded bg-emerald-500/20 text-emerald-400 font-semibold border border-emerald-500/30">
                Admin
              </span>
            </div>
            <p className="text-xs text-emerald-200/60 font-mono">Operations Portal</p>
          </div>
        </div>
      </div>

      {/* Cloud Status Pill */}
      <div className="mx-4 mt-4 p-3 rounded-lg bg-[#0e372a] border border-[#164d3b] flex items-center justify-between">
        <div className="flex items-center gap-2">
          <span className="relative flex h-2 w-2">
            <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
            <span className="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
          </span>
          <span className="text-xs font-medium text-emerald-200">Firebase Firestore</span>
        </div>
        <span className="text-[11px] text-emerald-400/80 font-mono">civora-app</span>
      </div>

      {/* Navigation */}
      <nav className="flex-1 px-4 py-6 space-y-1.5 overflow-y-auto">
        <div className="text-[11px] font-semibold uppercase tracking-wider text-emerald-400/50 px-3 pb-2">
          Management
        </div>
        {NAV_ITEMS.map((item) => {
          const Icon = item.icon;
          const isActive = pathname === item.href;
          return (
            <Link
              key={item.href}
              href={item.href}
              className={`flex items-center gap-3 px-3.5 py-2.5 rounded-xl font-medium text-sm transition-all duration-150 ${
                isActive
                  ? 'bg-gradient-to-r from-emerald-600 to-emerald-700 text-white shadow-md shadow-emerald-950/50'
                  : 'text-emerald-100/70 hover:bg-[#113f30] hover:text-white'
              }`}
            >
              <Icon className={`w-4 h-4 ${isActive ? 'text-white' : 'text-emerald-400/80'}`} />
              <span>{item.label}</span>
            </Link>
          );
        })}
      </nav>

      {/* Admin Profile Footer */}
      <div className="p-4 border-t border-[#144233] bg-[#07241b]">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 rounded-full bg-emerald-800/80 border border-emerald-600/50 flex items-center justify-center font-bold text-xs text-emerald-200">
              MOI
            </div>
            <div className="overflow-hidden">
              <div className="text-xs font-semibold truncate">Director General</div>
              <div className="text-[11px] text-emerald-400/60 truncate">Civil & Muqeem Affairs</div>
            </div>
          </div>
          <Link
            href="/login"
            title="Switch Session / Log Out"
            className="text-emerald-400/60 hover:text-red-400 p-1.5 rounded-lg hover:bg-emerald-950/40 transition-colors"
          >
            <LogOut className="w-4 h-4" />
          </Link>
        </div>
      </div>
    </aside>
  );
}

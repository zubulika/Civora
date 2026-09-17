'use client';

import React from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { 
  LayoutDashboard, 
  Users, 
  UserPlus, 
  FileText, 
  LogOut,
  Shield
} from 'lucide-react';
import { useAdminAuth } from '@/context/AuthContext';

const NAV_ITEMS = [
  { href: '/', label: 'Dashboard', icon: LayoutDashboard },
  { href: '/users', label: 'Users', icon: Users },
  { href: '/users/new', label: 'Add User', icon: UserPlus },
  { href: '/documents', label: 'Documents', icon: FileText },
];

export default function Sidebar() {
  const pathname = usePathname();
  const { admin, logout } = useAdminAuth();

  return (
    <aside className="w-60 bg-white text-slate-800 flex flex-col border-r border-slate-200 h-screen sticky top-0 shrink-0">
      {/* Brand */}
      <div className="h-16 px-5 flex items-center gap-3 border-b border-slate-100">
        <div className="w-8 h-8 rounded-lg bg-emerald-700 text-white flex items-center justify-center shrink-0">
          <Shield className="w-4 h-4" />
        </div>
        <div className="leading-tight">
          <span className="font-semibold text-sm text-slate-900 block">Absher Admin</span>
          <span className="text-[11px] text-slate-400">Identity Portal</span>
        </div>
      </div>

      {/* Navigation */}
      <nav className="flex-1 px-3 py-4 space-y-1">
        {NAV_ITEMS.map((item) => {
          const Icon = item.icon;
          const isActive = pathname === item.href;
          return (
            <Link
              key={item.href}
              href={item.href}
              className={`flex items-center gap-2.5 px-3 py-2 rounded-lg text-sm font-medium transition ${
                isActive
                  ? 'bg-emerald-50 text-emerald-800 font-semibold'
                  : 'text-slate-600 hover:bg-slate-50 hover:text-slate-900'
              }`}
            >
              <Icon className={`w-4 h-4 ${isActive ? 'text-emerald-700' : 'text-slate-400'}`} />
              <span>{item.label}</span>
            </Link>
          );
        })}
      </nav>

      {/* Footer Profile */}
      <div className="p-3 border-t border-slate-100 flex items-center justify-between">
        <div className="flex items-center gap-2.5 min-w-0 pr-2">
          <div className="w-8 h-8 rounded-full bg-slate-100 text-slate-700 flex items-center justify-center font-semibold text-xs shrink-0">
            {admin?.fullName ? admin.fullName.charAt(0) : 'A'}
          </div>
          <div className="min-w-0">
            <div className="text-xs font-medium text-slate-900 truncate">
              {admin?.fullName || 'Admin'}
            </div>
            <div className="text-[11px] text-slate-400 truncate">
              {admin?.email || 'admin@absher.moi.gov.sa'}
            </div>
          </div>
        </div>
        <button
          type="button"
          onClick={logout}
          title="Sign out"
          className="p-1.5 text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-md transition cursor-pointer shrink-0"
        >
          <LogOut className="w-4 h-4" />
        </button>
      </div>
    </aside>
  );
}

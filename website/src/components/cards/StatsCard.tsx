import React from 'react';
import { LucideIcon } from 'lucide-react';

interface StatsCardProps {
  title: string;
  value: string | number;
  subtitle?: string;
  icon: LucideIcon;
  trend?: string;
  accent?: 'emerald' | 'amber' | 'blue';
}

export default function StatsCard({
  title,
  value,
  subtitle,
  icon: Icon,
  trend,
  accent = 'emerald'
}: StatsCardProps) {
  const accentColors = {
    emerald: 'bg-emerald-50 text-emerald-700 border-emerald-100',
    amber: 'bg-amber-50 text-amber-700 border-amber-100',
    blue: 'bg-blue-50 text-blue-700 border-blue-100',
  };

  return (
    <div className="bg-white p-6 rounded-2xl border border-gray-200/90 shadow-xs flex items-center justify-between">
      <div>
        <div className="text-xs font-semibold uppercase tracking-wider text-gray-400 mb-1">{title}</div>
        <div className="text-3xl font-bold text-gray-900 tracking-tight font-mono">{value}</div>
        {subtitle && <div className="text-xs text-gray-500 mt-1">{subtitle}</div>}
        {trend && <div className="text-[11px] text-emerald-700 mt-1 font-medium">{trend}</div>}
      </div>
      <div className={`w-12 h-12 rounded-xl border flex items-center justify-center ${accentColors[accent]}`}>
        <Icon className="w-6 h-6" />
      </div>
    </div>
  );
}

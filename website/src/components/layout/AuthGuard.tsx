'use client';

import React, { useEffect } from 'react';
import { useRouter, usePathname } from 'next/navigation';
import { useAdminAuth } from '@/context/AuthContext';
import { Loader2 } from 'lucide-react';

export default function AuthGuard({ children }: { children: React.ReactNode }) {
  const router = useRouter();
  const pathname = usePathname();
  const { isAuthenticated, isLoading } = useAdminAuth();

  const isLoginPage = pathname === '/login';

  useEffect(() => {
    if (isLoading) return;

    if (!isAuthenticated && !isLoginPage) {
      router.replace('/login');
    } else if (isAuthenticated && isLoginPage) {
      router.replace('/');
    }
  }, [isAuthenticated, isLoading, isLoginPage, router]);

  if (isLoading) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center text-slate-500">
        <div className="flex items-center gap-2 text-sm">
          <Loader2 className="w-4 h-4 animate-spin text-emerald-700" />
          <span>Loading...</span>
        </div>
      </div>
    );
  }

  if (!isAuthenticated && !isLoginPage) {
    return null;
  }

  return <>{children}</>;
}

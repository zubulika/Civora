'use client';

import React, { createContext, useContext, useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { 
  AdminUser, 
  authenticateAdmin, 
  getAdminSession, 
  clearAdminSession,
  seedAdminToDatabase
} from '@/lib/adminAuthService';

interface AuthContextType {
  admin: AdminUser | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, pass: string) => Promise<{ success: boolean; error?: string }>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const router = useRouter();

  const [admin, setAdmin] = useState<AdminUser | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  // Initialize session and database on mount
  useEffect(() => {
    void Promise.resolve().then(() => {
      void seedAdminToDatabase();
      const existing = getAdminSession();
      if (existing?.isActive) {
        setAdmin(existing);
      }
      setIsLoading(false);
    });
  }, []);

  const login = async (email: string, pass: string) => {
    setIsLoading(true);
    try {
      const res = await authenticateAdmin(email, pass);
      if (res.success && res.user) {
        setAdmin(res.user);
        setIsLoading(false);
        return { success: true };
      }
      setIsLoading(false);
      return { success: false, error: res.error || 'Authentication failed.' };
    } catch (err: unknown) {
      setIsLoading(false);
      const message = err instanceof Error ? err.message : 'An unexpected error occurred.';
      return { success: false, error: message };
    }
  };

  const logout = () => {
    clearAdminSession();
    setAdmin(null);
    router.push('/login');
  };

  return (
    <AuthContext.Provider
      value={{
        admin,
        isAuthenticated: !!admin,
        isLoading,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAdminAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAdminAuth must be used within an AuthProvider');
  }
  return context;
}

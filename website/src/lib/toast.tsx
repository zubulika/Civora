'use client';

import React, { useState, useEffect, createContext, useContext, useCallback } from 'react';
import { CheckCircle2, AlertCircle, Loader2, X, Info } from 'lucide-react';

export type ToastType = 'success' | 'error' | 'loading' | 'info';

export interface ToastItem {
  id: string;
  message: string;
  type: ToastType;
  duration?: number;
}

interface ToastContextType {
  addToast: (message: string, type: ToastType, duration?: number) => string;
  removeToast: (id: string) => void;
}

const ToastContext = createContext<ToastContextType | null>(null);

let globalAddToast: ((message: string, type: ToastType, duration?: number) => string) | null = null;
let globalRemoveToast: ((id: string) => void) | null = null;

export const toast = {
  success: (msg: string, opts?: { duration?: number }) => {
    return globalAddToast ? globalAddToast(msg, 'success', opts?.duration ?? 3500) : '';
  },
  error: (msg: string, opts?: { duration?: number }) => {
    return globalAddToast ? globalAddToast(msg, 'error', opts?.duration ?? 4500) : '';
  },
  loading: (msg: string, opts?: { duration?: number }) => {
    return globalAddToast ? globalAddToast(msg, 'loading', opts?.duration ?? 0) : '';
  },
  info: (msg: string, opts?: { duration?: number }) => {
    return globalAddToast ? globalAddToast(msg, 'info', opts?.duration ?? 3500) : '';
  },
  dismiss: (id?: string) => {
    if (globalRemoveToast && id) globalRemoveToast(id);
  }
};

export function ToastProvider({ children }: { children: React.ReactNode }) {
  const [toasts, setToasts] = useState<ToastItem[]>([]);

  const removeToast = useCallback((id: string) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  }, []);

  const addToast = useCallback((message: string, type: ToastType, duration = 3500) => {
    const id = `toast_${Date.now()}_${Math.random().toString(36).slice(2, 7)}`;
    setToasts((prev) => [...prev, { id, message, type, duration }]);

    if (duration > 0) {
      setTimeout(() => {
        removeToast(id);
      }, duration);
    }
    return id;
  }, [removeToast]);

  useEffect(() => {
    globalAddToast = addToast;
    globalRemoveToast = removeToast;
    return () => {
      globalAddToast = null;
      globalRemoveToast = null;
    };
  }, [addToast, removeToast]);

  return (
    <ToastContext.Provider value={{ addToast, removeToast }}>
      {children}
      {/* Toast Render Viewport */}
      <div className="fixed top-5 right-5 z-[99999] flex flex-col gap-2 pointer-events-none max-w-md w-full">
        {toasts.map((t) => {
          const isSuccess = t.type === 'success';
          const isError = t.type === 'error';
          const isLoading = t.type === 'loading';

          return (
            <div
              key={t.id}
              className={`pointer-events-auto flex items-center justify-between gap-3 px-4 py-3 rounded-2xl shadow-xl border backdrop-blur-md transition-all animate-in slide-in-from-top-4 fade-in duration-200 ${
                isSuccess
                  ? 'bg-emerald-900/95 text-white border-emerald-700/80 shadow-emerald-950/20'
                  : isError
                  ? 'bg-rose-900/95 text-white border-rose-700/80 shadow-rose-950/20'
                  : isLoading
                  ? 'bg-slate-900/95 text-white border-slate-700 shadow-slate-950/20'
                  : 'bg-slate-800/95 text-white border-slate-600 shadow-slate-950/20'
              }`}
            >
              <div className="flex items-center gap-3">
                {isSuccess && (
                  <div className="w-7 h-7 rounded-xl bg-emerald-500/20 text-emerald-300 flex items-center justify-center shrink-0 border border-emerald-400/30">
                    <CheckCircle2 className="w-4 h-4" />
                  </div>
                )}
                {isError && (
                  <div className="w-7 h-7 rounded-xl bg-rose-500/20 text-rose-300 flex items-center justify-center shrink-0 border border-rose-400/30">
                    <AlertCircle className="w-4 h-4" />
                  </div>
                )}
                {isLoading && (
                  <div className="w-7 h-7 rounded-xl bg-white/10 text-white flex items-center justify-center shrink-0">
                    <Loader2 className="w-4 h-4 animate-spin" />
                  </div>
                )}
                {t.type === 'info' && (
                  <div className="w-7 h-7 rounded-xl bg-blue-500/20 text-blue-300 flex items-center justify-center shrink-0">
                    <Info className="w-4 h-4" />
                  </div>
                )}
                <span className="text-xs font-semibold leading-snug">{t.message}</span>
              </div>
              <button
                type="button"
                onClick={() => removeToast(t.id)}
                className="text-white/60 hover:text-white p-1 rounded-lg hover:bg-white/10 transition-colors cursor-pointer shrink-0"
              >
                <X className="w-3.5 h-3.5" />
              </button>
            </div>
          );
        })}
      </div>
    </ToastContext.Provider>
  );
}

export function Toaster() {
  return null; // Integrated inside ToastProvider
}

export function useToast() {
  const ctx = useContext(ToastContext);
  if (!ctx) return toast;
  return toast;
}

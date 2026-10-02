'use client';

import React, { useState, useRef, useEffect } from 'react';
import { UserProfile } from '@/types';
import { downloadDigitalDocument, ExportFormat } from '@/lib/documentExporter';
import { 
  Download, 
  FileText, 
  Image as ImageIcon, 
  FileCode, 
  Loader2, 
  ChevronDown, 
  Check,
  CreditCard,
  Car
} from 'lucide-react';

interface DocumentExportMenuProps {
  user: Partial<UserProfile>;
  docType?: 'RESIDENT_ID' | 'DRIVING_LICENSE';
  showDocSelector?: boolean;
  buttonClassName?: string;
  size?: 'sm' | 'md' | 'lg';
  placement?: 'top' | 'bottom';
}

export default function DocumentExportMenu({
  user,
  docType = 'RESIDENT_ID',
  showDocSelector = false,
  buttonClassName = '',
  size = 'md',
  placement = 'bottom'
}: DocumentExportMenuProps) {
  const [isOpen, setIsOpen] = useState(false);
  const [overrideDoc, setOverrideDoc] = useState<'RESIDENT_ID' | 'DRIVING_LICENSE' | null>(null);
  const [isExporting, setIsExporting] = useState<string | null>(null);
  const [lastDownloaded, setLastDownloaded] = useState<string | null>(null);
  const menuRef = useRef<HTMLDivElement>(null);

  const selectedDoc = showDocSelector && overrideDoc ? overrideDoc : docType;

  useEffect(() => {
    function handleClickOutside(e: MouseEvent) {
      if (menuRef.current && !menuRef.current.contains(e.target as Node)) {
        setIsOpen(false);
      }
    }
    if (isOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    }
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [isOpen]);

  const handleDownload = async (format: ExportFormat) => {
    const key = `${selectedDoc}_${format}`;
    setIsExporting(key);
    try {
      await downloadDigitalDocument(user, selectedDoc, format);
      setLastDownloaded(key);
      setTimeout(() => setLastDownloaded(null), 3000);
      setIsOpen(false);
    } catch (err) {
      console.error('Failed to export document:', err);
      alert('Error exporting document. Please try again.');
    } finally {
      setIsExporting(null);
    }
  };

  const sizeClasses = {
    sm: 'px-2.5 py-1.5 text-xs rounded-lg gap-1.5',
    md: 'px-3.5 py-2 text-xs font-bold rounded-xl gap-2',
    lg: 'px-5 py-2.5 text-sm font-bold rounded-xl gap-2.5'
  }[size];

  const menuPlacementClasses = placement === 'top'
    ? 'bottom-full mb-2 origin-bottom-right'
    : 'top-full mt-2 origin-top-right';

  return (
    <div className="relative inline-block text-left" ref={menuRef}>
      <button
        type="button"
        onClick={() => setIsOpen(!isOpen)}
        disabled={isExporting !== null}
        className={`flex items-center bg-white hover:bg-emerald-50 text-slate-800 hover:text-emerald-900 border border-emerald-300/80 shadow-2xs hover:shadow-xs transition-all active:scale-98 cursor-pointer disabled:opacity-60 font-bold ${sizeClasses} ${buttonClassName}`}
      >
        {isExporting ? (
          <Loader2 className="w-3.5 h-3.5 animate-spin text-emerald-600" />
        ) : (
          <Download className="w-3.5 h-3.5 text-emerald-700" />
        )}
        <span>{isExporting ? 'Generating...' : 'Download (PDF / JPG / PNG)'}</span>
        <ChevronDown className={`w-3.5 h-3.5 text-slate-400 transition-transform ${isOpen ? 'rotate-180' : ''}`} />
      </button>

      {isOpen && (
        <div className={`absolute right-0 w-72 bg-white rounded-2xl shadow-2xl border border-slate-200/90 py-2 z-50 animate-in fade-in zoom-in-95 duration-150 ${menuPlacementClasses}`}>
          {showDocSelector && (
            <div className="px-3 pt-1 pb-2 mb-1 border-b border-slate-100">
              <span className="block text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-1.5">
                Select Document
              </span>
              <div className="grid grid-cols-2 gap-1.5">
                <button
                  type="button"
                  onClick={() => setOverrideDoc('RESIDENT_ID')}
                  className={`flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg text-xs font-bold transition-colors cursor-pointer ${
                    selectedDoc === 'RESIDENT_ID'
                      ? 'bg-emerald-50 text-emerald-800 border border-emerald-300'
                      : 'bg-slate-50 text-slate-600 hover:bg-slate-100 border border-transparent'
                  }`}
                >
                  <CreditCard className="w-3.5 h-3.5 text-emerald-600" />
                  <span className="truncate">Resident ID</span>
                </button>
                <button
                  type="button"
                  onClick={() => setOverrideDoc('DRIVING_LICENSE')}
                  className={`flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg text-xs font-bold transition-colors cursor-pointer ${
                    selectedDoc === 'DRIVING_LICENSE'
                      ? 'bg-blue-50 text-blue-800 border border-blue-300'
                      : 'bg-slate-50 text-slate-600 hover:bg-slate-100 border border-transparent'
                  }`}
                >
                  <Car className="w-3.5 h-3.5 text-blue-600" />
                  <span className="truncate">License</span>
                </button>
              </div>
            </div>
          )}

          <div className="px-3 py-1">
            <span className="block text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-1">
              Choose Format
            </span>
          </div>

          {/* PDF Option */}
          <button
            type="button"
            onClick={() => handleDownload('pdf')}
            disabled={isExporting !== null}
            className="w-full flex items-center justify-between px-3.5 py-2.5 hover:bg-emerald-50/70 transition-colors text-left cursor-pointer group"
          >
            <div className="flex items-center gap-2.5">
              <div className="w-8 h-8 rounded-lg bg-rose-50 border border-rose-200 flex items-center justify-center text-rose-700 shrink-0 group-hover:scale-105 transition-transform">
                <FileText className="w-4 h-4" />
              </div>
              <div>
                <div className="font-bold text-xs text-slate-900 flex items-center gap-1.5">
                  <span>PDF Document</span>
                  <span className="text-[9px] font-mono bg-rose-100 text-rose-800 px-1.5 py-0.2 rounded font-bold">.PDF</span>
                </div>
                <p className="text-[11px] text-slate-500">Print-ready standard CR80 ID card size</p>
              </div>
            </div>
            {lastDownloaded === `${selectedDoc}_pdf` ? (
              <Check className="w-4 h-4 text-emerald-600 shrink-0" />
            ) : isExporting === `${selectedDoc}_pdf` ? (
              <Loader2 className="w-4 h-4 text-rose-600 animate-spin shrink-0" />
            ) : null}
          </button>

          {/* PNG Option */}
          <button
            type="button"
            onClick={() => handleDownload('png')}
            disabled={isExporting !== null}
            className="w-full flex items-center justify-between px-3.5 py-2.5 hover:bg-emerald-50/70 transition-colors text-left cursor-pointer group"
          >
            <div className="flex items-center gap-2.5">
              <div className="w-8 h-8 rounded-lg bg-emerald-50 border border-emerald-200 flex items-center justify-center text-emerald-700 shrink-0 group-hover:scale-105 transition-transform">
                <ImageIcon className="w-4 h-4" />
              </div>
              <div>
                <div className="font-bold text-xs text-slate-900 flex items-center gap-1.5">
                  <span>PNG High-Res Image</span>
                  <span className="text-[9px] font-mono bg-emerald-100 text-emerald-800 px-1.5 py-0.2 rounded font-bold">.PNG</span>
                </div>
                <p className="text-[11px] text-slate-500">Lossless 1586x1000 crystal-clear graphic</p>
              </div>
            </div>
            {lastDownloaded === `${selectedDoc}_png` ? (
              <Check className="w-4 h-4 text-emerald-600 shrink-0" />
            ) : isExporting === `${selectedDoc}_png` ? (
              <Loader2 className="w-4 h-4 text-emerald-600 animate-spin shrink-0" />
            ) : null}
          </button>

          {/* JPG Option */}
          <button
            type="button"
            onClick={() => handleDownload('jpg')}
            disabled={isExporting !== null}
            className="w-full flex items-center justify-between px-3.5 py-2.5 hover:bg-emerald-50/70 transition-colors text-left cursor-pointer group"
          >
            <div className="flex items-center gap-2.5">
              <div className="w-8 h-8 rounded-lg bg-blue-50 border border-blue-200 flex items-center justify-center text-blue-700 shrink-0 group-hover:scale-105 transition-transform">
                <FileCode className="w-4 h-4" />
              </div>
              <div>
                <div className="font-bold text-xs text-slate-900 flex items-center gap-1.5">
                  <span>JPG Photo Image</span>
                  <span className="text-[9px] font-mono bg-blue-100 text-blue-800 px-1.5 py-0.2 rounded font-bold">.JPG</span>
                </div>
                <p className="text-[11px] text-slate-500">High quality compressed digital photo</p>
              </div>
            </div>
            {lastDownloaded === `${selectedDoc}_jpg` ? (
              <Check className="w-4 h-4 text-emerald-600 shrink-0" />
            ) : isExporting === `${selectedDoc}_jpg` ? (
              <Loader2 className="w-4 h-4 text-blue-600 animate-spin shrink-0" />
            ) : null}
          </button>

          <div className="px-3 pt-2 mt-1 border-t border-slate-100">
            <p className="text-[10px] text-slate-400 text-center">
              Rendered in authentic mobile app layout with Absher verification
            </p>
          </div>
        </div>
      )}
    </div>
  );
}

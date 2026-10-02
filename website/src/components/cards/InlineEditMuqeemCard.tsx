'use client';

import React, { useState, useRef, useCallback } from 'react';
import { UserProfile } from '@/types';
import { buildOfficialQrPayload, getQrCodeFallbackUrls, BARCODE_PATTERN } from '@/lib/officialQr';
import {
  RefreshCw, CheckCircle2, ShieldCheck, Camera, ScanLine,
  Loader2, Sparkles, UploadCloud, X
} from 'lucide-react';

export const toArabicNumerals = (str: string): string => {
  if (!str) return '';
  const eastern = '٠١٢٣٤٥٦٧٨٩';
  return str.replace(/\d/g, d => eastern[parseInt(d, 10)] || d);
};

export const extractDateDigits = (dateStr: string): string => {
  if (!dateStr) return '';
  const clean = dateStr.trim();
  const ymd = clean.match(/^(\d{4})[\/\-](\d{1,2})[\/\-](\d{1,2})$/);
  if (ymd) {
    const y = ymd[1].slice(-2);
    const m = ymd[2].padStart(2, '0');
    const d = ymd[3].padStart(2, '0');
    return `${d}${m}${y}`;
  }
  const dmy = clean.match(/^(\d{1,2})[\/\-](\d{1,2})[\/\-](\d{4})$/);
  if (dmy) {
    const d = dmy[1].padStart(2, '0');
    const m = dmy[2].padStart(2, '0');
    const y = dmy[3].slice(-2);
    return `${d}${m}${y}`;
  }
  const digitsOnly = clean.replace(/\D/g, '');
  return digitsOnly.slice(0, 6);
};

interface InlineEditMuqeemCardProps {
  user: Partial<UserProfile>;
  onChange: (changes: Partial<UserProfile>) => void;
  className?: string;
}

// ─── Inline editable value on the card ─────────────────────────────────────
interface RowFieldProps {
  label: string;          // Arabic label (hard-coded, read-only)
  value: string;
  placeholder: string;    // English placeholder
  onChange: (v: string) => void;
  mono?: boolean;
  dir?: 'ltr' | 'rtl';
}

function RowField({ label, value, placeholder, onChange, mono, dir = 'ltr' }: RowFieldProps) {
  const [editing, setEditing] = useState(false);
  const ref = useRef<HTMLInputElement>(null);
  const open = () => { setEditing(true); setTimeout(() => ref.current?.focus(), 10); };

  return (
    <div className="flex items-baseline gap-1.5">
      <span
        className="text-[#5a5850] font-bold select-none shrink-0"
        style={{ fontFamily: 'var(--font-tajawal), "Tajawal", sans-serif' }}
      >
        {label}
      </span>
      {editing ? (
        <input
          ref={ref}
          value={value}
          onChange={e => onChange(e.target.value)}
          onBlur={() => setEditing(false)}
          placeholder={placeholder}
          dir={dir}
          className={`bg-white/95 border-2 border-emerald-500 rounded-md px-2 py-0.5 outline-none text-[11px] font-bold text-[#3e3d3b] shadow-lg font-sans ${mono ? 'font-mono tracking-wider' : ''}`}
          style={{ minWidth: 80, maxWidth: 140 }}
        />
      ) : (
        <button
          type="button"
          onClick={open}
          title={`Click to edit — ${placeholder}`}
          className={`font-bold text-[#3e3d3b] font-sans hover:bg-white/60 hover:text-emerald-800 px-1 py-0.5 rounded-md cursor-pointer border border-transparent hover:border-emerald-400 transition-colors ${mono ? 'font-mono tracking-wider' : ''}`}
        >
          {value || <span className="text-[#999590] italic font-medium font-sans">{placeholder}</span>}
        </button>
      )}
    </div>
  );
}

// Name field — larger text directly on the card
interface NameFieldProps {
  value: string;
  onChange: (v: string) => void;
  placeholder: string;
  className: string;
  dir?: 'ltr' | 'rtl';
}

function NameField({ value, onChange, placeholder, className, dir = 'ltr' }: NameFieldProps) {
  const [editing, setEditing] = useState(false);
  const ref = useRef<HTMLInputElement>(null);
  const open = () => { setEditing(true); setTimeout(() => ref.current?.focus(), 10); };

  if (editing) {
    return (
      <input
        ref={ref}
        value={value}
        onChange={e => onChange(e.target.value)}
        onBlur={() => setEditing(false)}
        placeholder={placeholder}
        dir={dir}
        className={`bg-white/95 border-2 border-emerald-500 rounded-md px-2 py-0.5 outline-none shadow-lg text-[#343436] font-sans w-full ${className}`}
        style={{ fontSize: 'inherit' }}
      />
    );
  }

  return (
    <button
      type="button"
      onClick={open}
      title={`Click to edit — ${placeholder}`}
      className={`${className} font-sans hover:bg-white/50 rounded-md px-1 py-0.5 cursor-pointer border border-transparent hover:border-emerald-400 transition-colors text-right w-full`}
    >
      {value || <span className="text-[#a0ac9e] italic font-medium font-sans" style={{ fontSize: '12px' }}>{placeholder}</span>}
    </button>
  );
}

type OcrStatus = 'idle' | 'loading' | 'done' | 'error';

export default function InlineEditMuqeemCard({
  user,
  onChange,
  className = '',
}: InlineEditMuqeemCardProps) {
  const [showQrBack, setShowQrBack] = useState(false);
  const [qrSrcIndex, setQrSrcIndex] = useState(0);
  const [ocrStatus, setOcrStatus] = useState<OcrStatus>('idle');
  const [ocrMessage, setOcrMessage] = useState('');
  const [showOcrPanel, setShowOcrPanel] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const photoInputRef = useRef<HTMLInputElement>(null);

  // QR disclaimer lines — editable in English, shown in Arabic on card
  const defaultDisclaimerLines = ['يجب التحقق', 'من الرمز السريع', 'قبل اعتماد', 'التعامل مع الهوية'];
  const [disclaimerLines, setDisclaimerLines] = useState<string[]>(defaultDisclaimerLines);
  const [editingDisclaimer, setEditingDisclaimer] = useState(false);
  const [disclaimerEnglish, setDisclaimerEnglish] = useState(
    'Verify QR\nbefore accepting\nthis ID document\nas valid'
  );

  const qrPayload = buildOfficialQrPayload(user);
  const qrUrls = getQrCodeFallbackUrls(qrPayload, 240);
  const qrImageUrl = qrUrls[qrSrcIndex % qrUrls.length];
  const totalBarcodeUnits = BARCODE_PATTERN.reduce((s, w) => s + w, 0);

  // Photo upload handler
  const handlePhotoUpload = useCallback((file: File) => {
    const reader = new FileReader();
    reader.onload = (e) => {
      const dataUrl = e.target?.result as string;
      onChange({ photoUrl: dataUrl });
    };
    reader.readAsDataURL(file);
  }, [onChange]);

  const runOcr = useCallback(async (file: File) => {
    setOcrStatus('loading');
    setOcrMessage('Initialising OCR engine…');
    try {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      const Tesseract: any = await import('tesseract.js');
      setOcrMessage('Scanning document…');
      const { data } = await Tesseract.recognize(file, 'eng+ara', {
        logger: (m: { status: string; progress: number }) => {
          if (m.status === 'recognizing text')
            setOcrMessage(`Reading… ${Math.round(m.progress * 100)}%`);
        },
      });
      const lines: string[] = (data.text || '').split('\n').map((l: string) => l.trim()).filter(Boolean);
      const extracted: Partial<UserProfile> = {};

      for (const line of lines) {
        // National ID (10-digit starting with 1 or 2)
        if (!extracted.nationalId) {
          const m = line.match(/\b([12]\d{9})\b/);
          if (m) extracted.nationalId = m[1];
        }
        // Sponsor/employer ID (10-digit starting with 7)
        if (!extracted.sponsorId) {
          const m = line.match(/\b(7\d{9})\b/);
          if (m) extracted.sponsorId = m[1];
        }
        const dateMatches = [...line.matchAll(/\b(\d{1,4}[\/\-]\d{1,2}[\/\-]\d{1,4})\b/g)];
        const lLower = line.toLowerCase();

        if ((lLower.includes('expir') || lLower.includes('انتهاء')) && dateMatches[0] && !extracted.expiryDateEn) {
          extracted.expiryDateEn = dateMatches[0][1];
          extracted.expiryDateAr = toArabicNumerals(dateMatches[0][1]);
          extracted.expiryDateDigits = extractDateDigits(dateMatches[0][1]);
        }
        if ((lLower.includes('birth') || lLower.includes('ميلاد')) && dateMatches[0] && !extracted.dateOfBirth) {
          extracted.dateOfBirth = dateMatches[0][1];
          extracted.dateOfBirthAr = toArabicNumerals(dateMatches[0][1]);
        }

        // English name: all-caps multi-word
        if (!extracted.fullNameEn && /^[A-Z][A-Z\s]{8,}$/.test(line) && line.split(' ').length >= 2)
          extracted.fullNameEn = line.trim();

        // Nationality keywords
        if (!extracted.nationality && /banglad/i.test(line)) { extracted.nationality = 'Bangladesh'; extracted.nationalityAr = 'بنجلاديش'; }
        if (!extracted.nationality && /pakistan/i.test(line)) { extracted.nationality = 'Pakistan'; extracted.nationalityAr = 'باكستان'; }
        if (!extracted.nationality && /india/i.test(line)) { extracted.nationality = 'India'; extracted.nationalityAr = 'الهند'; }
        if (!extracted.nationality && /egypt/i.test(line)) { extracted.nationality = 'Egypt'; extracted.nationalityAr = 'مصر'; }
        if (!extracted.nationality && /philippine|pilipino/i.test(line)) { extracted.nationality = 'Philippines'; extracted.nationalityAr = 'الفلبين'; }

        // Religion
        if (!extracted.religionEn && /muslim|islam/i.test(line)) { extracted.religionEn = 'Islam'; extracted.religionAr = 'مسلم'; }
        if (!extracted.religionEn && /christian/i.test(line)) { extracted.religionEn = 'Christian'; extracted.religionAr = 'مسيحي'; }
      }

      const count = Object.keys(extracted).length;
      if (count === 0) { setOcrStatus('error'); setOcrMessage('No fields detected. Try a clearer image.'); return; }
      onChange(extracted);
      setOcrStatus('done');
      setOcrMessage(`Auto-filled ${count} field${count !== 1 ? 's' : ''}. Review and correct as needed.`);
    } catch {
      setOcrStatus('error');
      setOcrMessage('OCR failed. Ensure tesseract.js is installed: npm i tesseract.js');
    }
  }, [onChange]);

  const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) runOcr(file);
    e.target.value = '';
  };

  return (
    <div className={`flex flex-col items-center w-full ${className}`}>
      {/* Top bar */}
      <div className="w-full flex flex-wrap items-center justify-between gap-2 mb-2 px-1">
        <span className="inline-flex items-center gap-1 text-[11px] font-semibold text-emerald-800 bg-emerald-100/90 border border-emerald-300 px-2.5 py-1 rounded-full">
          <CheckCircle2 className="w-3 h-3 text-emerald-600" />
          Click any field on card to edit
        </span>
        <div className="flex items-center gap-1.5 flex-wrap">
          <button
            type="button"
            onClick={() => setShowOcrPanel(v => !v)}
            className="flex items-center gap-1.5 text-xs font-semibold text-violet-700 hover:text-violet-900 bg-violet-50 hover:bg-violet-100 border border-violet-300 px-3 py-1.5 rounded-lg shadow-2xs transition-all cursor-pointer"
          >
            <ScanLine className="w-3.5 h-3.5" />
            <span>Scan Doc</span>
          </button>
          <button
            type="button"
            onClick={() => setShowQrBack(!showQrBack)}
            className="flex items-center gap-1.5 text-xs font-semibold text-gray-700 hover:text-emerald-700 bg-white hover:bg-emerald-50 border border-gray-300 px-3 py-1.5 rounded-lg shadow-2xs transition-all cursor-pointer"
          >
            <RefreshCw className={`w-3.5 h-3.5 text-emerald-600 ${showQrBack ? 'rotate-180' : ''}`} />
            <span>{showQrBack ? 'Front' : 'QR'}</span>
          </button>
        </div>
      </div>

      {/* OCR Panel */}
      {showOcrPanel && (
        <div className="w-full mb-3 bg-gradient-to-br from-violet-50 to-purple-50 border border-violet-200 rounded-2xl p-4 shadow-sm">
          <div className="flex items-start justify-between mb-3">
            <div>
              <h4 className="text-xs font-bold text-violet-900 flex items-center gap-1.5">
                <Sparkles className="w-3.5 h-3.5 text-violet-600" />
                On-Device OCR Scan
              </h4>
              <p className="text-[11px] text-violet-600 mt-0.5">Upload a photo of the Digital Document / E-CAMA Card (هوية مقيم). Processed entirely on your device.</p>
            </div>
            <button type="button" onClick={() => setShowOcrPanel(false)} className="text-violet-400 hover:text-violet-700 cursor-pointer">
              <X className="w-4 h-4" />
            </button>
          </div>

          {ocrStatus === 'idle' && (
            <label className="flex flex-col items-center justify-center gap-2 border-2 border-dashed border-violet-300 rounded-xl p-5 cursor-pointer hover:bg-violet-100/60 transition-colors">
              <UploadCloud className="w-7 h-7 text-violet-400" />
              <span className="text-xs font-semibold text-violet-700">Click or drop document image here</span>
              <span className="text-[10px] text-violet-500">JPG / PNG / WEBP — no data leaves your device</span>
              <input ref={fileInputRef} type="file" accept="image/*" onChange={handleFileSelect} className="hidden" />
            </label>
          )}

          {ocrStatus === 'loading' && (
            <div className="flex flex-col items-center gap-2 py-4">
              <Loader2 className="w-6 h-6 text-violet-600 animate-spin" />
              <span className="text-xs font-semibold text-violet-700">{ocrMessage}</span>
            </div>
          )}

          {(ocrStatus === 'done' || ocrStatus === 'error') && (
            <div className={`rounded-xl px-4 py-3 text-xs font-semibold ${ocrStatus === 'done' ? 'bg-emerald-50 text-emerald-800 border border-emerald-200' : 'bg-rose-50 text-rose-800 border border-rose-200'}`}>
              <p>{ocrMessage}</p>
              <button type="button" onClick={() => { setOcrStatus('idle'); setOcrMessage(''); }} className="mt-1.5 text-[11px] underline opacity-70 hover:opacity-100 cursor-pointer">
                Scan another
              </button>
            </div>
          )}
        </div>
      )}

      {/* Card */}
      <div
        className="w-full aspect-[1.586/1] rounded-2xl shadow-xl border border-amber-900/15 overflow-hidden relative"
        style={{
          backgroundColor: '#FCFBF7',
          backgroundImage: 'url(/bg_resident_card.webp)',
          backgroundSize: 'cover',
          backgroundPosition: 'center',
          boxShadow: '0 12px 32px -4px rgba(12,61,46,0.18), 0 4px 12px -2px rgba(0,0,0,0.08)',
        }}
      >
        {!showQrBack ? (
          <div className="absolute inset-0">
            {/* Version number */}
            <div className="absolute font-serif font-black text-[#8c6d23]" style={{ left: '9.8%', top: '15.5%', fontSize: '3.6%', lineHeight: 1 }}>
              {user.versionNumber || '٢'}
            </div>

            {/* Photo — click to upload */}
            <div
              className="absolute overflow-hidden rounded-[4px] bg-[#E8EEF4] border border-[#dcd6c8] cursor-pointer group"
              style={{ left: '5.8%', top: '28.2%', width: '25.4%', height: '46.1%' }}
              onClick={() => photoInputRef.current?.click()}
              title="Click to upload photo"
            >
              <input
                ref={photoInputRef}
                type="file"
                accept="image/*"
                className="hidden"
                onChange={e => {
                  const f = e.target.files?.[0];
                  if (f) handlePhotoUpload(f);
                  e.target.value = '';
                }}
              />
              {user.photoUrl ? (
                <>
                  <img src={user.photoUrl} alt={user.fullNameEn || 'Resident'} className="w-full h-full object-cover object-center" />
                  <div className="absolute inset-0 bg-black/0 group-hover:bg-black/30 transition-all flex items-center justify-center opacity-0 group-hover:opacity-100">
                    <Camera className="w-6 h-6 text-white drop-shadow-md" />
                  </div>
                </>
              ) : (
                <div className="w-full h-full flex flex-col items-center justify-center bg-slate-100 group-hover:bg-slate-200 transition-colors">
                  <Camera className="w-7 h-7 text-gray-400 group-hover:text-emerald-600 transition-colors" />
                  <span className="text-[8px] text-gray-400 mt-1 text-center px-1">Tap to upload photo</span>
                </div>
              )}
            </div>

            {/* QR + disclaimer box */}
            <div className="absolute bg-white rounded-[4px] border border-[#DCD6C8] pr-1 pl-0.5 py-0.5 flex items-center justify-between" style={{ left: '4.6%', top: '75.8%', width: '27.0%', height: '16.5%' }}>
              <div className="relative flex items-center justify-start h-full aspect-square">
                <img src={qrImageUrl} alt="QR" onError={() => setQrSrcIndex(p => p + 1)} className="w-full h-full object-contain" />
                <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[28%] h-[28%] bg-white rounded-2xs p-0.5 shadow-2xs flex items-center justify-center">
                  <img src="/ic_absher_qr_emblem.png" alt="Absher" className="w-full h-full object-contain" />
                </div>
              </div>
              {/* Editable disclaimer text */}
              <div className="flex-1 pr-1 h-full flex items-center justify-end">
                {editingDisclaimer ? (
                  <textarea
                    autoFocus
                    value={disclaimerEnglish}
                    onChange={e => setDisclaimerEnglish(e.target.value)}
                    onBlur={() => {
                      // Keep Arabic lines synced: each line of English maps to a line displayed on card
                      const lines = disclaimerEnglish.split('\n').slice(0, 4);
                      while (lines.length < 4) lines.push('');
                      setDisclaimerLines(lines);
                      setEditingDisclaimer(false);
                    }}
                    rows={4}
                    className="w-full h-full text-[8px] text-slate-700 bg-yellow-50 border border-emerald-400 rounded outline-none resize-none p-0.5 leading-tight"
                    placeholder="4 lines of text"
                    title="Type in English — displayed on card"
                  />
                ) : (
                  <button
                    type="button"
                    onClick={() => setEditingDisclaimer(true)}
                    title="Click to edit disclaimer text (English → shown as Arabic on card)"
                    className="text-right text-[#2b2b2b] font-black w-full h-full flex flex-col justify-between py-0.5 cursor-pointer hover:bg-emerald-50/60 rounded transition-colors font-[family-name:var(--font-kufi)]"
                    style={{
                      fontSize: '7.5px',
                      lineHeight: '9px',
                      fontFamily: 'var(--font-kufi), "Noto Kufi Arabic", sans-serif',
                    }}
                    dir="rtl"
                  >
                    {disclaimerLines.map((line, i) => <div key={i}>{line}</div>)}
                  </button>
                )}
              </div>
            </div>

            {/* Barcode */}
            <div className="absolute bg-white flex items-center px-0.5" style={{ left: '5.7%', top: '93.0%', width: '25.4%', height: '5.8%' }}>
              <svg viewBox={`0 0 ${totalBarcodeUnits} 10`} className="w-full h-full" preserveAspectRatio="none">
                {(() => {
                  let x = 0;
                  return BARCODE_PATTERN.map((w, idx) => {
                    const rect = idx % 2 === 0 ? <rect key={idx} x={x} y={0} width={w} height={10} fill="#000" /> : null;
                    x += w;
                    return rect;
                  });
                })()}
              </svg>
            </div>

            {/* Data fields — names anchored to divider line, rows packed tight */}
            <div className="absolute flex flex-col" style={{ left: '31.5%', top: '28.5%', width: '65.0%', height: '69%', gap: '2.5%' }} dir="rtl">
              {/* Names — right-aligned, hugging the horizontal divider */}
              <div className="flex flex-col text-right items-end w-full">
                <NameField value={user.fullNameAr || ''} onChange={v => onChange({ fullNameAr: v })} placeholder="الاسم الكامل بالعربية" className="font-black text-[#343436] leading-tight text-[18px] truncate text-right" dir="rtl" />
                <NameField value={user.fullNameEn || ''} onChange={v => onChange({ fullNameEn: v.toUpperCase() })} placeholder="ENGLISH NAME" className="font-bold text-[#32322a] uppercase tracking-wide text-[13px] truncate text-right" dir="ltr" />
              </div>

              {/* Row 1: ID | Expiry */}
              <div className="flex items-baseline justify-between text-[13.5px] leading-tight">
                <RowField label="رقم الهوية:" value={user.nationalId || ''} onChange={v => onChange({ nationalId: v.replace(/\D/g, '') })} placeholder="10-digit ID" mono />
                <RowField
                  label="تاريخ الانتهاء:"
                  value={user.expiryDateEn || ''}
                  onChange={v => {
                    const ar = toArabicNumerals(v);
                    const digits = extractDateDigits(v);
                    onChange({
                      expiryDateEn: v,
                      expiryDateAr: ar,
                      expiryDateDigits: digits || user.expiryDateDigits || '081026',
                    });
                  }}
                  placeholder="YYYY/MM/DD"
                  mono
                />
              </div>

              {/* Row 2: DOB | Place of Birth */}
              <div className="flex items-baseline justify-between text-[13.5px] leading-tight">
                <RowField
                  label="تاريخ الميلاد:"
                  value={user.dateOfBirth || ''}
                  onChange={v => {
                    const ar = toArabicNumerals(v);
                    onChange({
                      dateOfBirth: v,
                      dateOfBirthAr: ar,
                    });
                  }}
                  placeholder="YYYY/MM/DD"
                  mono
                />
                <RowField label="مكان الميلاد:" value={user.placeOfBirthEn || ''} onChange={v => onChange({ placeOfBirthEn: v, placeOfBirthAr: v })} placeholder="City, Country" />
              </div>

              {/* Row 3: Nationality | Religion */}
              <div className="flex items-baseline justify-between text-[13.5px] leading-tight">
                <RowField label="الجنسية:" value={user.nationality || ''} onChange={v => onChange({ nationality: v })} placeholder="e.g. Bangladesh" />
                <RowField label="الديانة:" value={user.religionEn || ''} onChange={v => onChange({ religionEn: v, religionAr: v === 'Islam' ? 'مسلم' : v })} placeholder="Islam / Christian" />
              </div>

              {/* Row 4: Profession — المهنة */}
              <div className="flex items-baseline gap-1.5 text-[13.5px] leading-tight">
                <RowField
                  label="المهنة:"
                  value={user.professionEn || ''}
                  onChange={v => onChange({ professionEn: v, professionAr: v })}
                  placeholder="e.g. Driver / سائق"
                />
              </div>

              {/* Row 5: Sponsor ID — هوية صاحب العمل */}
              <div className="flex items-baseline gap-1.5 text-[13.5px] leading-tight">
                <RowField
                  label="هوية صاحب العمل:"
                  value={user.sponsorId || ''}
                  onChange={v => onChange({ sponsorId: v })}
                  placeholder="e.g. 7001234567 / هوية المنشأة"
                  mono
                />
              </div>

              {/* Row 6: Place of Issue — مكان الإصدار */}
              <div className="flex items-baseline gap-1.5 text-[13.5px] leading-tight">
                <RowField
                  label="مكان الإصدار:"
                  value={user.issuePlaceEn || ''}
                  onChange={v => onChange({ issuePlaceEn: v, issuePlace: v })}
                  placeholder="e.g. Riyadh / الرياض"
                />
              </div>

              {/* Row 7: Workplace — مكان العمل */}
              <div className="flex items-baseline gap-1.5 text-[13.5px] leading-tight">
                <RowField
                  label="مكان العمل:"
                  value={user.workPlaceAr || ''}
                  onChange={v => onChange({ workPlaceAr: v })}
                  placeholder="e.g. Riyadh Region / منطقة الرياض"
                />
              </div>

              {/* Row 8: Employer/Sponsor Name — اسم صاحب العمل (matches Android app) */}
              <div className="flex items-baseline gap-1.5 text-[13.5px] leading-tight">
                <RowField
                  label="اسم صاحب العمل:"
                  value={user.sponsorNameEn || user.sponsorName || ''}
                  onChange={v => onChange({ sponsorNameEn: v, sponsorName: v })}
                  placeholder="e.g. Saudi Aramco / اسم المنشأة"
                />
              </div>
            </div>
          </div>
        ) : (
          <div className="w-full h-full p-5 flex flex-col items-center justify-between text-center bg-white/95 backdrop-blur-xs">
            <div className="flex items-center gap-2 text-emerald-800">
              <ShieldCheck className="w-5 h-5 text-emerald-600" />
              <span className="text-xs font-bold uppercase tracking-wider">Official Verification Matrix</span>
            </div>
            <div className="p-3 bg-white border-2 border-emerald-600/30 rounded-xl shadow-md flex flex-col items-center">
              <div className="relative w-36 h-36 bg-gray-50 border border-gray-200 rounded-lg flex items-center justify-center p-2">
                <img src={qrImageUrl} alt="QR" onError={() => setQrSrcIndex(p => p + 1)} className="w-full h-full object-contain" />
                <div className="absolute w-8 h-8 bg-white rounded-md p-1 shadow-md flex items-center justify-center">
                  <img src="/ic_absher_qr_emblem.png" alt="Absher" className="w-full h-full object-contain" />
                </div>
              </div>
              <span className="text-[10px] font-mono font-bold text-gray-800 mt-1.5">{user.nationalId || '-'}</span>
            </div>
            <div className="text-[10px] text-gray-600 max-w-xs leading-relaxed">Scan with Absher Official Inspector App to verify cryptographically signed residence status.</div>
            <div className="text-[8px] font-mono text-gray-400">DOC-ID: {user.id || 'usr_new'} • SECURE-HASH: MOI-SA-{(user.nationalId || '0000').slice(-4)}</div>
          </div>
        )}
      </div>
      <p className="text-[10px] text-slate-400 mt-2">💡 Click any value directly on the card to edit</p>
    </div>
  );
}

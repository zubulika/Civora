'use client';

import React, { useState, useRef, useCallback } from 'react';
import { UserProfile } from '@/types';
import { buildOfficialQrPayload, getQrCodeFallbackUrls } from '@/lib/officialQr';
import {
  RefreshCw, CheckCircle2, ShieldCheck, ScanLine,
  Loader2, Sparkles, UploadCloud, X, Camera
} from 'lucide-react';

interface InlineEditDrivingLicenseProps {
  user: Partial<UserProfile>;
  onChange: (changes: Partial<UserProfile>) => void;
  className?: string;
}

const toArabicNumerals = (str: string): string =>
  str.replace(/[0-9]/g, (d) => '٠١٢٣٤٥٦٧٨٩'[parseInt(d, 10)]);

// ─── Inline editable text field rendered on the card ───────────────────────
interface InlineFieldProps {
  value: string;
  placeholder: string;
  arLabel: string;
  enLabel: string;
  onChange: (v: string) => void;
  mono?: boolean;
  arLabelStyle?: React.CSSProperties;
  enLabelStyle?: React.CSSProperties;
}

function InlineField({ value, placeholder, arLabel, enLabel, onChange, mono, arLabelStyle, enLabelStyle }: InlineFieldProps) {
  const [editing, setEditing] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);

  const open = () => {
    setEditing(true);
    setTimeout(() => inputRef.current?.focus(), 10);
  };

  return (
    <div className="flex items-center justify-between w-full">
      {/* English sub-column (Left side): flex 0.53 */}
      <div className="flex-[0.53] flex items-center gap-[0.6cqw]">
        <span className="font-bold tracking-tight select-none shrink-0" style={enLabelStyle}>{enLabel}</span>
        {editing ? (
          <input
            ref={inputRef}
            value={value}
            onChange={e => onChange(e.target.value)}
            onBlur={() => setEditing(false)}
            placeholder={placeholder}
            className={`bg-white/95 border-2 border-emerald-500 rounded px-1 py-0 font-black text-black outline-none shadow-lg ${mono ? 'font-mono' : 'font-sans'}`}
            style={{
              fontSize: '2.72cqw',
              lineHeight: '3.1cqw',
              minWidth: '10cqw',
              maxWidth: '22cqw',
            }}
          />
        ) : (
          <button
            type="button"
            onClick={open}
            title={`Click to edit — ${placeholder}`}
            className={`font-black text-black hover:bg-white/60 hover:text-emerald-900 px-0.5 py-0 rounded cursor-pointer transition-colors text-left border border-transparent hover:border-emerald-400 truncate flex-1 ${mono ? 'font-mono' : 'font-sans'}`}
            style={{
              fontSize: '2.72cqw',
              lineHeight: '3.1cqw',
            }}
          >
            {value || <span className="text-gray-400/70 italic font-normal text-[2.0cqw]">{placeholder}</span>}
          </button>
        )}
      </div>

      <div style={{ width: '0.55cqw' }} />

      {/* Arabic sub-column (Right side): flex 0.47, dir RTL */}
      <div className="flex-[0.47] flex items-center gap-[0.6cqw]" dir="rtl">
        <span className="font-extrabold tracking-tight select-none shrink-0" style={arLabelStyle}>{arLabel}</span>
        <span
          className="font-black text-black font-sans truncate flex-1 text-right"
          style={{
            fontSize: '2.72cqw',
            lineHeight: '3.1cqw',
          }}
        >
          {value ? toArabicNumerals(value) : ''}
        </span>
      </div>
    </div>
  );
}

interface NameFieldProps {
  value: string;
  onChange: (v: string) => void;
  placeholder: string;
  className: string;
  style?: React.CSSProperties;
  dir: 'ltr' | 'rtl';
}

function NameInlineField({ value, onChange, placeholder, className, style, dir }: NameFieldProps) {
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
        className={`bg-white/95 border-2 border-emerald-500 rounded px-1 py-0 outline-none shadow-lg text-slate-900 w-full ${className}`}
        style={style}
      />
    );
  }

  return (
    <button
      type="button"
      onClick={open}
      title={`Click to edit — ${placeholder}`}
      className={`${className} hover:bg-white/40 rounded px-1 py-0 cursor-pointer border border-transparent hover:border-emerald-400 transition-colors text-right w-full truncate`}
      style={style}
    >
      {value || <span className="text-gray-400/60 italic font-normal text-[2.2cqw]">{placeholder}</span>}
    </button>
  );
}

type OcrStatus = 'idle' | 'loading' | 'done' | 'error';

export default function InlineEditDrivingLicense({
  user,
  onChange,
  className = '',
}: InlineEditDrivingLicenseProps) {
  const [showQrBack, setShowQrBack] = useState(false);
  const [qrSrcIndex, setQrSrcIndex] = useState(0);
  const [ocrStatus, setOcrStatus] = useState<OcrStatus>('idle');
  const [ocrMessage, setOcrMessage] = useState('');
  const [showOcrPanel, setShowOcrPanel] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const photoInputRef = useRef<HTMLInputElement>(null);

  const qrPayload = buildOfficialQrPayload(user);
  const qrUrls = getQrCodeFallbackUrls(qrPayload, 240);
  const qrImageUrl = qrUrls[qrSrcIndex % qrUrls.length];

  const labelStrokeStyleAr: React.CSSProperties = {
    color: '#ffffff',
    WebkitTextStroke: '0.36cqw #111111',
    textShadow:
      '-0.12cqw -0.12cqw 0 #111, 0.12cqw -0.12cqw 0 #111, -0.12cqw 0.12cqw 0 #111, 0.12cqw 0.12cqw 0 #111, 0 0.28cqw 0.44cqw rgba(0, 0, 0, 0.45)',
    fontFamily: 'var(--font-tajawal), "Tajawal", sans-serif',
    fontSize: '2.35cqw',
    lineHeight: '2.72cqw',
  };

  const labelStrokeStyleEn: React.CSSProperties = {
    color: '#ffffff',
    WebkitTextStroke: '0.36cqw #111111',
    textShadow:
      '-0.12cqw -0.12cqw 0 #111, 0.12cqw -0.12cqw 0 #111, -0.12cqw 0.12cqw 0 #111, 0.12cqw 0.12cqw 0 #111, 0 0.28cqw 0.44cqw rgba(0, 0, 0, 0.45)',
    fontFamily: 'var(--font-sans), "Inter", "Segoe UI", Arial, sans-serif',
    fontSize: '2.22cqw',
    lineHeight: '2.72cqw',
  };

  // Direct photo upload handler
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
        if (!extracted.nationalId) {
          const m = line.match(/\b([12]\d{9})\b/);
          if (m) extracted.nationalId = m[1];
        }
        if (!extracted.bloodType && /\b(A|B|AB|O)[+-]/i.test(line)) {
          const m = line.match(/\b(A|B|AB|O)[+-]/i);
          if (m) extracted.bloodType = m[0].toUpperCase();
        }
        if (!extracted.licenseTypeEn && /private|خصوصي/i.test(line)) extracted.licenseTypeEn = 'Private';
        if (!extracted.licenseTypeEn && /public|عمومي/i.test(line)) extracted.licenseTypeEn = 'Public';
        if (!extracted.licenseTypeEn && /motorcycle|دراجة/i.test(line)) extracted.licenseTypeEn = 'Motorcycle';

        const dateMatches = [...line.matchAll(/\b(\d{1,4}[\/\-]\d{1,2}[\/\-]\d{1,4})\b/g)];
        const lLower = line.toLowerCase();

        if ((lLower.includes('issue') || lLower.includes('إصدار')) && dateMatches[0] && !extracted.licenseIssueDateEn)
          extracted.licenseIssueDateEn = dateMatches[0][1];
        if ((lLower.includes('expir') || lLower.includes('انتهاء')) && dateMatches[0] && !extracted.licenseExpiryDateEn)
          extracted.licenseExpiryDateEn = dateMatches[0][1];
        if ((lLower.includes('birth') || lLower.includes('ميلاد')) && dateMatches[0] && !extracted.dateOfBirth)
          extracted.dateOfBirth = dateMatches[0][1];
        if (!extracted.fullNameEn && /^[A-Z][A-Z\s]{8,}$/.test(line) && line.split(' ').length >= 2)
          extracted.fullNameEn = line.trim();
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
              <p className="text-[11px] text-violet-600 mt-0.5">Upload a photo of the driving license. Processed 100% on your device — no server.</p>
            </div>
            <button type="button" onClick={() => setShowOcrPanel(false)} className="text-violet-400 hover:text-violet-700 cursor-pointer">
              <X className="w-4 h-4" />
            </button>
          </div>

          {ocrStatus === 'idle' && (
            <label className="flex flex-col items-center justify-center gap-2 border-2 border-dashed border-violet-300 rounded-xl p-5 cursor-pointer hover:bg-violet-100/60 transition-colors">
              <UploadCloud className="w-7 h-7 text-violet-400" />
              <span className="text-xs font-semibold text-violet-700">Click or drop document image here</span>
              <span className="text-[10px] text-violet-500">JPG / PNG / WEBP — extracted on-device</span>
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
        className="w-full aspect-[1.586/1] rounded-2xl shadow-xl border-2 border-white overflow-hidden relative @container"
        style={{
          backgroundImage: 'url(/bg_driving_license.webp)',
          backgroundSize: '100% 100%',
          backgroundPosition: 'center',
          boxShadow: '0 12px 32px -4px rgba(12,61,46,0.18), 0 4px 12px -2px rgba(0,0,0,0.08)',
          containerType: 'inline-size',
        }}
      >
        {user.hasDrivingLicense === false && (
          <div className="absolute inset-0 z-30 bg-slate-900/85 backdrop-blur-xs flex flex-col items-center justify-center p-6 text-center">
            <div className="w-12 h-12 rounded-full bg-amber-500/20 text-amber-400 flex items-center justify-center mb-2 border border-amber-500/40">
              <ShieldCheck className="w-6 h-6 opacity-60" />
            </div>
            <h4 className="text-white font-bold text-sm">Driving License Disabled (غير متاحة)</h4>
            <p className="text-slate-300 text-xs mt-1 max-w-xs">This citizen currently has no active driving license. Toggle ON in the Driving License form section to enable.</p>
          </div>
        )}
        {!showQrBack ? (
          <div className="absolute inset-0">
            {/* Photo (Positioned precisely to completely cover the template's pre-printed photo frame cutout) */}
            <div
              className="absolute overflow-hidden rounded-[2.77cqw] bg-[#E8EEF4] border border-gray-300 shadow-2xs cursor-pointer group"
              style={{ left: '2.6%', top: '22.7%', width: '28.2%', height: '49.6%' }}
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
                  <img src={user.photoUrl} alt="Holder" className="w-full h-full object-cover" />
                  <div className="absolute inset-0 bg-black/0 group-hover:bg-black/30 transition-all flex items-center justify-center opacity-0 group-hover:opacity-100">
                    <Camera className="w-6 h-6 text-white drop-shadow-md" />
                  </div>
                </>
              ) : (
                <div className="w-full h-full flex flex-col items-center justify-center bg-slate-100 group-hover:bg-slate-200 transition-colors">
                  <Camera className="w-6 h-6 text-gray-400 group-hover:text-emerald-600 transition-colors" />
                  <span className="text-[1.8cqw] text-gray-400 mt-1">Tap to upload photo</span>
                </div>
              )}
            </div>

            {/* QR box */}
            <div
              className="absolute bg-white/95 rounded-[0.83cqw] border border-gray-300 px-[0.3cqw] py-0 flex items-center justify-between"
              style={{ left: '2.6%', top: '73.5%', width: '28.2%', height: '16.3%' }}
            >
              <div className="relative flex items-center justify-center" style={{ width: '44%', height: '90%' }}>
                <img src={qrImageUrl} alt="QR" onError={() => setQrSrcIndex(p => p + 1)} className="w-full h-full object-contain" />
                <div className="absolute w-[30%] h-[30%] bg-white rounded-[0.25cqw] p-[0.15cqw] shadow-xs flex items-center justify-center">
                  <img src="/ic_absher_qr_emblem.png" alt="Absher" className="w-full h-full object-contain" />
                </div>
              </div>
              <div
                className="text-right text-[#2B2B2B] font-black flex-1 pr-[0.4cqw] py-[0.25cqw] flex flex-col justify-between h-full select-none font-[family-name:var(--font-kufi)]"
                style={{
                  fontSize: '1.33cqw',
                  lineHeight: '1.55cqw',
                  fontFamily: 'var(--font-kufi), "Noto Kufi Arabic", sans-serif',
                }}
                dir="rtl"
              >
                <div>يجب التحقق</div><div>من الرمز السريع</div><div>قبل اعتماد</div><div>التعامل مع الهوية</div>
              </div>
            </div>

            {/* Names: Dynamic names using standard SansSerif (1:1 Android Alignment) */}
            <div
              className="absolute flex flex-col items-end text-right"
              style={{
                left: '32.5%',
                top: '24.0%',
                width: '64.0%',
              }}
            >
              <NameInlineField
                value={user.fullNameAr || ''}
                onChange={v => onChange({ fullNameAr: v })}
                placeholder="الاسم بالعربية"
                className="font-bold text-[#222222] font-sans"
                style={{
                  fontSize: '3.94cqw',
                  lineHeight: '4.4cqw',
                }}
                dir="rtl"
              />
              <div style={{ height: '0.4cqw' }} />
              <NameInlineField
                value={user.fullNameEn || ''}
                onChange={v => onChange({ fullNameEn: v.toUpperCase() })}
                placeholder="ENGLISH NAME"
                className="font-semibold text-[#222222] uppercase font-sans"
                style={{
                  fontSize: '2.83cqw',
                  lineHeight: '3.2cqw',
                  letterSpacing: '0.08cqw',
                }}
                dir="ltr"
              />
            </div>

            {/* Data rows: Pre-printed Arabic labels use Tajawal, English labels use SansSerif, dynamic values use SansSerif Black */}
            <div
              className="absolute flex flex-col justify-between"
              style={{
                left: '32.5%',
                top: '42.2%',
                width: '64.0%',
                height: '52.0%',
              }}
            >
              <InlineField enLabel="ID Number:" arLabel="رقم الهوية:" value={user.nationalId || ''} onChange={v => onChange({ nationalId: v.replace(/\D/g, '') })} placeholder="10-digit National ID" mono enLabelStyle={labelStrokeStyleEn} arLabelStyle={labelStrokeStyleAr} />
              <InlineField enLabel="License Type:" arLabel="نوع الرخصة:" value={user.licenseTypeEn || ''} onChange={v => onChange({ licenseTypeEn: v, licenseTypeAr: v === 'Private' ? 'خصوصي' : v === 'Public' ? 'عمومي' : v })} placeholder="Private / Public / Motorcycle" enLabelStyle={labelStrokeStyleEn} arLabelStyle={labelStrokeStyleAr} />
              <InlineField enLabel="Issue Date:" arLabel="تاريخ الإصدار:" value={user.licenseIssueDateEn || ''} onChange={v => onChange({ licenseIssueDateEn: v, licenseIssueDateAr: toArabicNumerals(v) })} placeholder="DD/MM/YYYY" mono enLabelStyle={labelStrokeStyleEn} arLabelStyle={labelStrokeStyleAr} />
              <InlineField enLabel="Date of Birth:" arLabel="تاريخ الميلاد:" value={user.dateOfBirth || ''} onChange={v => onChange({ dateOfBirth: v, dateOfBirthAr: toArabicNumerals(v) })} placeholder="DD/MM/YYYY" mono enLabelStyle={labelStrokeStyleEn} arLabelStyle={labelStrokeStyleAr} />
              <InlineField enLabel="Nationality:" arLabel="الجنسية:" value={user.nationality || ''} onChange={v => onChange({ nationality: v })} placeholder="e.g. Bangladesh" enLabelStyle={labelStrokeStyleEn} arLabelStyle={labelStrokeStyleAr} />
              <InlineField enLabel="Expiry Date:" arLabel="تاريخ الانتهاء:" value={user.licenseExpiryDateEn || ''} onChange={v => onChange({ licenseExpiryDateEn: v, licenseExpiryDateAr: toArabicNumerals(v) })} placeholder="DD/MM/YYYY" mono enLabelStyle={labelStrokeStyleEn} arLabelStyle={labelStrokeStyleAr} />
              <InlineField enLabel="Blood Type:" arLabel="فصيلة الدم:" value={user.bloodType || ''} onChange={v => onChange({ bloodType: v })} placeholder="A+, O-, AB+…" enLabelStyle={labelStrokeStyleEn} arLabelStyle={labelStrokeStyleAr} />
            </div>
          </div>
        ) : (
          <div className="w-full h-full p-5 flex flex-col items-center justify-between text-center bg-white/95 backdrop-blur-xs">
            <div className="flex items-center gap-2 text-emerald-800">
              <ShieldCheck className="w-5 h-5 text-emerald-600" />
              <span className="text-xs font-bold uppercase tracking-wider">Official Driving License Matrix</span>
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
            <div className="text-[10px] text-gray-600 max-w-xs leading-relaxed">Scan with Absher Traffic Inspector App to verify license validity.</div>
            <div className="text-[8px] font-mono text-gray-400">LICENSE-ID: {user.nationalId || '0000000000'} • CLASS: {user.licenseTypeEn || 'Private'}</div>
          </div>
        )}
      </div>
      <p className="text-[10px] text-slate-400 mt-2">💡 Click any value directly on the card to edit</p>
    </div>
  );
}

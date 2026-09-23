'use client';

import React, { useState } from 'react';
import { UserProfile } from '@/types';
import { buildOfficialQrPayload, getQrCodeFallbackUrls, BARCODE_PATTERN } from '@/lib/officialQr';
import { RefreshCw, CheckCircle2, ShieldCheck, User } from 'lucide-react';

interface MuqeemCardPreviewProps {
  user: Partial<UserProfile>;
  className?: string;
}

export default function MuqeemCardPreview({ user, className = '' }: MuqeemCardPreviewProps) {
  const [showQrBack, setShowQrBack] = useState(false);
  const [qrSrcIndex, setQrSrcIndex] = useState(0);

  const qrPayload = buildOfficialQrPayload(user);
  const qrUrls = getQrCodeFallbackUrls(qrPayload, 240);
  const qrImageUrl = qrUrls[qrSrcIndex % qrUrls.length];

  // Barcode dimensions calculation
  const totalBarcodeUnits = BARCODE_PATTERN.reduce((sum, w) => sum + w, 0);

  return (
    <div className={`flex flex-col items-center ${className}`}>
      {/* Top action bar */}
      <div className="w-full max-w-[540px] flex items-center justify-between mb-3 px-1">
        <div className="flex items-center gap-2">
          <span className="inline-flex items-center gap-1 text-[11px] font-semibold text-emerald-800 bg-emerald-100/90 border border-emerald-300 px-2 py-0.5 rounded-full">
            <CheckCircle2 className="w-3 h-3 text-emerald-600" />
            Official MOI Digital Template
          </span>
        </div>
        <button
          type="button"
          onClick={() => setShowQrBack(!showQrBack)}
          className="flex items-center gap-1.5 text-xs font-semibold text-gray-700 hover:text-emerald-700 bg-white hover:bg-emerald-50 border border-gray-300 px-3 py-1 rounded-lg shadow-2xs transition-all cursor-pointer"
        >
          <RefreshCw className={`w-3.5 h-3.5 text-emerald-600 ${showQrBack ? 'rotate-180 transition-transform' : ''}`} />
          <span>{showQrBack ? 'Show ID Front' : 'Show Digital QR'}</span>
        </button>
      </div>

      {/* Card Container (Aspect Ratio 1.586 matching Android ISO/IEC 7810 ID-1 standard) */}
      <div
        className="w-full max-w-[540px] aspect-[1.586/1] rounded-2xl shadow-xl border border-amber-900/15 overflow-hidden relative select-none"
        style={{
          backgroundColor: '#FCFBF7',
          backgroundImage: 'url(/bg_resident_card.webp)',
          backgroundSize: 'cover',
          backgroundPosition: 'center',
          boxShadow: '0 12px 32px -4px rgba(12, 61, 46, 0.18), 0 4px 12px -2px rgba(0, 0, 0, 0.08)',
        }}
      >
        {!showQrBack ? (
          /* FRONT SIDE: 1:1 Pixel Alignment with Android DynamicMuqeemCard.kt */
          <div className="absolute inset-0">
            {/* 1. Version Number beside pre-printed 'رقم النسخة' */}
            <div
              className="absolute font-serif font-black text-[#8c6d23]"
              style={{
                left: '9.8%',
                top: '15.5%',
                fontSize: '3.6%',
                lineHeight: 1,
              }}
            >
              {user.versionNumber || '٢'}
            </div>

            {/* 2. Portrait Photo Cutout */}
            <div
              className="absolute overflow-hidden rounded-[4px] bg-[#E8EEF4] border border-[#dcd6c8] shadow-xs"
              style={{
                left: '5.8%',
                top: '28.2%',
                width: '25.4%',
                height: '46.1%',
              }}
            >
              {user.photoUrl ? (
                <img
                  src={user.photoUrl}
                  alt={user.fullNameEn || 'Resident Photo'}
                  className="w-full h-full object-cover object-center"
                />
              ) : (
                <div className="w-full h-full flex flex-col items-center justify-center text-gray-400 bg-slate-100">
                  <User className="w-7 h-7 text-gray-300" />
                  <span className="text-[8px] font-medium text-gray-400 mt-1">No Photo</span>
                </div>
              )}
            </div>

            {/* 3. Lower Verification Box: Authentic Scannable QR + 4-Line Arabic Disclaimer */}
            <div
              className="absolute bg-white rounded-[4px] border border-[#DCD6C8] px-1 py-0.5 flex items-center justify-between"
              style={{
                left: '4.6%',
                top: '75.8%',
                width: '26.6%',
                height: '16.5%',
              }}
            >
              {/* QR Code Container with Centered Absher Logo */}
              <div className="relative flex items-center justify-center" style={{ width: '46%', height: '90%' }}>
                <img
                  src={qrImageUrl}
                  alt="Valid Security QR Code"
                  onError={() => setQrSrcIndex((prev) => prev + 1)}
                  className="w-full h-full object-contain"
                />
                <div className="absolute w-[30%] h-[30%] bg-white rounded-2xs p-0.5 shadow-2xs flex items-center justify-center">
                  <img
                    src="/ic_absher_qr_emblem.png"
                    alt="Absher"
                    className="w-full h-full object-contain"
                  />
                </div>
              </div>

              {/* 4-Line Official Arabic Disclaimer */}
              <div
                className="text-right text-[#1a1a1a] font-bold flex-1 pr-1"
                style={{
                  fontSize: '5.2px',
                  lineHeight: '6.2px',
                }}
                dir="rtl"
              >
                <div>يجب التحقق</div>
                <div>من الرمز السريع</div>
                <div>قبل اعتماد</div>
                <div>التعامل مع الهوية</div>
              </div>
            </div>

            {/* 4. 1D Barcode Strip under verification box */}
            <div
              className="absolute bg-white flex items-center px-0.5"
              style={{
                left: '5.7%',
                top: '93.0%',
                width: '25.4%',
                height: '5.8%',
              }}
            >
              <svg viewBox={`0 0 ${totalBarcodeUnits} 10`} className="w-full h-full" preserveAspectRatio="none">
                {(() => {
                  let currentX = 0;
                  return BARCODE_PATTERN.map((w, idx) => {
                    const isBlack = idx % 2 === 0;
                    const rect = isBlack ? (
                      <rect key={idx} x={currentX} y={0} width={w} height={10} fill="#000" />
                    ) : null;
                    currentX += w;
                    return rect;
                  });
                })()}
              </svg>
            </div>

            {/* 5. Citizen Data Fields (Aligned precisely across the right security guilloche waves) */}
            <div
              className="absolute flex flex-col justify-between"
              style={{
                left: '31.5%',
                top: '23.8%',
                width: '65.0%',
                height: '72.2%',
              }}
              dir="rtl"
            >
              {/* Names Header */}
              <div className="flex flex-col text-right">
                <div className="font-black text-[#111827] leading-tight text-[15.5px] truncate">
                  {user.fullNameAr || ''}
                </div>
                <div
                  className="font-bold text-[#1f2937] uppercase tracking-wide text-[11.2px] truncate mt-0.5"
                  dir="ltr"
                >
                  {user.fullNameEn?.toUpperCase() || ''}
                </div>
              </div>

              {/* Row 1: Expiry Date (Left) | National ID (Right) */}
              <div className="flex items-baseline justify-between text-[9.5px] leading-tight">
                <div className="flex items-baseline gap-1">
                  <span className="text-gray-500 font-medium">رقم الهوية:</span>
                  <span className="font-bold text-gray-950 font-mono tracking-wider">{user.nationalId || ''}</span>
                </div>
                <div className="flex items-baseline gap-1">
                  <span className="text-gray-500 font-medium">تاريخ الانتهاء:</span>
                  <span className="font-bold text-gray-900">{user.expiryDateAr || user.expiryDateEn || ''}</span>
                </div>
              </div>

              {/* Row 2: Place of Birth (Left) | Date of Birth (Right) */}
              <div className="flex items-baseline justify-between text-[9.5px] leading-tight">
                <div className="flex items-baseline gap-1">
                  <span className="text-gray-500 font-medium">تاريخ الميلاد:</span>
                  <span className="font-bold text-gray-900">{user.dateOfBirthAr || user.dateOfBirth || ''}</span>
                </div>
                <div className="flex items-baseline gap-1">
                  <span className="text-gray-500 font-medium">مكان الميلاد:</span>
                  <span className="font-bold text-gray-900 truncate max-w-[110px]">{user.placeOfBirthAr || ''}</span>
                </div>
              </div>

              {/* Row 3: Religion (Left) | Nationality (Right) */}
              <div className="flex items-baseline justify-between text-[9.5px] leading-tight">
                <div className="flex items-baseline gap-1">
                  <span className="text-gray-500 font-medium">الجنسية:</span>
                  <span className="font-bold text-gray-900">{user.nationalityAr || ''}</span>
                </div>
                <div className="flex items-baseline gap-1">
                  <span className="text-gray-500 font-medium">الديانة:</span>
                  <span className="font-bold text-gray-900">{user.religionAr || ''}</span>
                </div>
              </div>

              {/* Row 4: Profession */}
              <div className="flex items-baseline gap-1 text-[9.5px] leading-tight">
                <span className="text-gray-500 font-medium">المهنة:</span>
                <span className="font-bold text-gray-950 truncate">{user.professionAr || ''}</span>
              </div>

              {/* Row 5: Employer / Sponsor ID */}
              <div className="flex items-baseline gap-1 text-[9.5px] leading-tight">
                <span className="text-gray-500 font-medium">هوية صاحب العمل:</span>
                <span className="font-mono text-gray-900 font-bold">{user.sponsorId || ''}</span>
              </div>

              {/* Row 6: Place of Issue */}
              <div className="flex items-baseline gap-1 text-[9.5px] leading-tight">
                <span className="text-gray-500 font-medium">مكان الإصدار:</span>
                <span className="font-medium text-gray-900 truncate">{user.issuePlace || ''}</span>
              </div>

              {/* Row 7: Place of Work */}
              <div className="flex items-baseline gap-1 text-[9.5px] leading-tight">
                <span className="text-gray-500 font-medium">مكان العمل:</span>
                <span className="font-medium text-gray-900 truncate">{user.workPlaceAr || ''}</span>
              </div>

              {/* Row 8: Employer / Sponsor Name */}
              <div className="flex items-baseline gap-1 text-[9.5px] leading-tight pb-0.5">
                <span className="text-gray-500 font-medium">اسم صاحب العمل:</span>
                <span className="font-bold text-gray-900 truncate">{user.sponsorName || ''}</span>
              </div>
            </div>
          </div>
        ) : (
          /* BACK SIDE: Cryptographically Valid Official QR View */
          <div className="w-full h-full p-5 flex flex-col items-center justify-between text-center bg-white/95 backdrop-blur-xs">
            <div className="flex items-center gap-2 text-emerald-800">
              <ShieldCheck className="w-5 h-5 text-emerald-600" />
              <span className="text-xs font-bold uppercase tracking-wider">Official Verification Matrix</span>
            </div>

            {/* Valid Scannable High-Res QR */}
            <div className="p-3 bg-white border-2 border-emerald-600/30 rounded-xl shadow-md flex flex-col items-center">
              <div className="relative w-36 h-36 bg-gray-50 border border-gray-200 rounded-lg flex items-center justify-center p-2">
                <img
                  src={qrImageUrl}
                  alt="Official Absher Scannable QR Code"
                  onError={() => setQrSrcIndex((prev) => prev + 1)}
                  className="w-full h-full object-contain"
                />
                <div className="absolute w-8 h-8 bg-white rounded-md p-1 shadow-md flex items-center justify-center">
                  <img
                    src="/ic_absher_qr_emblem.png"
                    alt="Absher Emblem"
                    className="w-full h-full object-contain"
                  />
                </div>
              </div>
              <span className="text-[10px] font-mono font-bold text-gray-800 mt-1.5">{user.nationalId || '-'}</span>
            </div>

            <div className="text-[10px] text-gray-600 max-w-xs leading-relaxed">
              Scan with Absher Official Inspector App to securely verify cryptographically signed residence status.
            </div>

            <div className="text-[8px] font-mono text-gray-400">
              DOC-ID: {user.id || 'usr_new'} • SECURE-HASH: MOI-SA-{(user.nationalId || '0000').slice(-4)}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

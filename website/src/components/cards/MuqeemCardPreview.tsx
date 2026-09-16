'use client';

import React, { useState } from 'react';
import { UserProfile } from '@/types';
import { QrCode, RefreshCw, ShieldCheck, Sparkles, CheckCircle2 } from 'lucide-react';

interface MuqeemCardPreviewProps {
  user: Partial<UserProfile>;
  className?: string;
}

export default function MuqeemCardPreview({ user, className = '' }: MuqeemCardPreviewProps) {
  const [showQrBack, setShowQrBack] = useState(false);

  // Defaults fallback
  const fullNameEn = user.fullNameEn || 'FULL NAME IN ENGLISH';
  const fullNameAr = user.fullNameAr || 'الاسم الكامل بالعربية';
  const nationalId = user.nationalId || '2495685261';
  const dob = user.dateOfBirth || '1988/02/03';
  const nationalityAr = user.nationalityAr || 'بنجلاديش';
  const nationalityEn = user.nationality || 'Bangladesh';
  const placeOfBirthAr = user.placeOfBirthAr || user.nationalityAr || 'بنجلاديش';
  const religionAr = user.religionAr || 'الاسلام';
  const professionAr = user.professionAr || 'عامل غسيل ملابس';
  const sponsorId = user.sponsorId || '7034884309';
  const sponsorName = user.sponsorName || 'مؤسسة درر نجاح للملابس';
  const issuePlace = user.issuePlace || 'شركة العلم لامن المعلومات';
  const workPlaceAr = user.workPlaceAr || 'منطقة الرياض';
  const expiryDateEn = user.expiryDateEn || '2026/10/08';
  const versionNumber = user.versionNumber || '٢';
  const photoUrl = user.photoUrl || '/avatar_placeholder.png';

  return (
    <div className={`flex flex-col items-center ${className}`}>
      {/* Action bar on top of card */}
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
          className="flex items-center gap-1.5 text-xs text-gray-600 hover:text-emerald-700 bg-white hover:bg-emerald-50 border border-gray-200 px-3 py-1 rounded-lg shadow-2xs transition-all"
        >
          <RefreshCw className={`w-3 h-3 ${showQrBack ? 'rotate-180 transition-transform' : ''}`} />
          <span>{showQrBack ? 'Show Front' : 'Show Digital QR'}</span>
        </button>
      </div>

      {/* Card Container (Aspect Ratio roughly 1.586 standard ID card) */}
      <div 
        className="w-full max-w-[540px] aspect-[1.586/1] rounded-2xl shadow-xl border border-amber-900/15 overflow-hidden relative select-none transition-transform duration-300 hover:scale-[1.01]"
        style={{
          backgroundColor: '#fbf8f0',
          backgroundImage: 'url(/bg_resident_card.webp)',
          backgroundSize: 'cover',
          backgroundPosition: 'center',
          boxShadow: '0 12px 32px -4px rgba(12, 61, 46, 0.18), 0 4px 12px -2px rgba(0, 0, 0, 0.08)'
        }}
      >
        {!showQrBack ? (
          /* FRONT SIDE */
          <div className="w-full h-full p-4 relative flex flex-col justify-between">
            {/* Top Row: Titles & Emblem */}
            <div className="flex justify-between items-start">
              {/* Left Top: هوية مقيم رقم النسخة */}
              <div className="text-left font-serif">
                <div className="text-[#8c6d23] font-bold text-sm tracking-wide">
                  هوية مقيم
                </div>
                <div className="text-[#8c6d23] text-xs font-semibold">
                  {versionNumber} رقم النسخة
                </div>
              </div>

              {/* Right Top: المملكة العربية السعودية / وزارة الداخلية & Emblem */}
              <div className="flex items-center gap-2 text-right">
                <div className="font-serif">
                  <div className="text-[#8c6d23] font-bold text-xs">
                    المملكة العربية السعودية
                  </div>
                  <div className="text-[#8c6d23] text-[11px] font-semibold">
                    وزارة الداخلية
                  </div>
                </div>
                {/* Emblem Seal */}
                <div className="w-8 h-8 rounded-full border border-[#b3913b] flex items-center justify-center p-0.5 bg-amber-50/50">
                  <img src="/saudi_emblem.svg" alt="Emblem" className="w-full h-full object-contain opacity-90" />
                </div>
              </div>
            </div>

            {/* Middle Section: Photo on Left, Bilingual Fields on Right */}
            <div className="grid grid-cols-12 gap-3 items-center my-auto">
              {/* Left 4 Cols: Portrait Photo + QR badge + Barcode */}
              <div className="col-span-4 flex flex-col items-center">
                <div className="w-24 h-32 rounded-lg border-2 border-[#164230]/70 overflow-hidden bg-gray-200 shadow-md relative">
                  <img
                    src={photoUrl}
                    alt={fullNameEn}
                    className="w-full h-full object-cover"
                    onError={(e) => {
                      (e.target as HTMLImageElement).src = 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&auto=format&fit=crop&q=80';
                    }}
                  />
                </div>
                {/* Security verification stamp & barcode box */}
                <div className="w-24 mt-1.5 bg-white/80 border border-gray-300 rounded p-1 flex items-center justify-between text-[7px] text-gray-700">
                  <div className="w-4 h-4 bg-emerald-700 rounded-2xs flex items-center justify-center text-white">
                    <QrCode className="w-3 h-3" />
                  </div>
                  <div className="text-[7px] text-right font-medium leading-tight">
                    يجب التحقق<br />من الرمز السريع
                  </div>
                </div>
              </div>

              {/* Right 8 Cols: Names & Details */}
              <div className="col-span-8 flex flex-col justify-center">
                {/* Primary Arabic & English Names */}
                <div className="text-right border-b border-amber-900/10 pb-1 mb-1.5">
                  <div className="text-base font-bold text-gray-900 tracking-wide leading-tight">
                    {fullNameAr}
                  </div>
                  <div className="text-[11px] font-semibold text-gray-700 uppercase tracking-wider">
                    {fullNameEn}
                  </div>
                </div>

                {/* 2-Column Details Grid */}
                <div className="text-[9.5px] leading-tight text-gray-800 space-y-1" dir="rtl">
                  <div className="flex justify-between items-baseline">
                    <span className="text-gray-500 font-medium">رقم الهوية:</span>
                    <span className="font-bold text-gray-900 font-mono tracking-wider">{nationalId}</span>
                  </div>
                  <div className="flex justify-between items-baseline">
                    <span className="text-gray-500 font-medium">تاريخ الميلاد:</span>
                    <span className="font-semibold">{dob}</span>
                  </div>
                  <div className="flex justify-between items-baseline">
                    <span className="text-gray-500 font-medium">الجنسية:</span>
                    <span className="font-semibold">{nationalityAr}</span>
                  </div>
                  <div className="flex justify-between items-baseline">
                    <span className="text-gray-500 font-medium">المهنة:</span>
                    <span className="font-bold text-gray-900">{professionAr}</span>
                  </div>
                  <div className="flex justify-between items-baseline">
                    <span className="text-gray-500 font-medium">هوية صاحب العمل:</span>
                    <span className="font-mono text-gray-900">{sponsorId}</span>
                  </div>
                  <div className="flex justify-between items-baseline">
                    <span className="text-gray-500 font-medium">مكان الإصدار:</span>
                    <span className="font-medium truncate max-w-[170px]">{issuePlace}</span>
                  </div>
                  <div className="flex justify-between items-baseline">
                    <span className="text-gray-500 font-medium">تاريخ الانتهاء:</span>
                    <span className="font-bold text-emerald-900">{expiryDateEn}</span>
                  </div>
                  <div className="flex justify-between items-baseline">
                    <span className="text-gray-500 font-medium">صاحب العمل:</span>
                    <span className="font-medium truncate max-w-[170px]">{sponsorName}</span>
                  </div>
                </div>
              </div>
            </div>

            {/* Bottom Barcode Strip */}
            <div className="w-full flex justify-between items-center pt-1 border-t border-amber-900/10 text-[8px] text-gray-500">
              <span className="font-mono tracking-widest">||| | |||| | ||||| ||| |||| ||||| | ||</span>
              <span className="text-[8px] font-mono text-gray-400">SAUDI ARABIA • MOI • RESIDENT IDENTITY</span>
            </div>
          </div>
        ) : (
          /* BACK SIDE: OFFICIAL SECURE QR */
          <div className="w-full h-full p-6 flex flex-col items-center justify-between text-center bg-white/95 backdrop-blur-xs">
            <div className="flex items-center gap-2 text-emerald-800">
              <ShieldCheck className="w-5 h-5 text-emerald-600" />
              <span className="text-xs font-bold uppercase tracking-wider">Official Verification Matrix</span>
            </div>

            {/* Big QR Display */}
            <div className="p-3 bg-white border-2 border-emerald-700/30 rounded-xl shadow-md flex flex-col items-center">
              <div className="w-36 h-36 bg-gray-50 border border-gray-200 rounded-lg flex items-center justify-center p-2">
                <div className="w-full h-full border-2 border-dashed border-emerald-600/40 rounded flex flex-col items-center justify-center gap-1 text-emerald-900">
                  <QrCode className="w-16 h-16 text-emerald-700" />
                  <span className="text-[9px] font-mono font-bold">{nationalId}</span>
                </div>
              </div>
            </div>

            <div className="text-[11px] text-gray-600 max-w-xs leading-relaxed">
              Scan with Absher Official Inspector App to securely verify cryptographically signed residence status.
            </div>

            <div className="text-[9px] font-mono text-gray-400">
              DOC-ID: {user.id || 'usr_new'} • SECURE-HASH: MOI-SA-{nationalId.slice(-4)}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

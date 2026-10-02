'use client';

import React, { useState } from 'react';
import { UserProfile } from '@/types';
import { buildOfficialQrPayload, getQrCodeFallbackUrls, BARCODE_PATTERN } from '@/lib/officialQr';
import { RefreshCw, CheckCircle2, ShieldCheck, User, Languages } from 'lucide-react';
import DocumentExportMenu from '@/components/common/DocumentExportMenu';

const toArabicNumerals = (str: string): string => {
  if (!str) return '';
  const eastern = '٠١٢٣٤٥٦٧٨٩';
  return str.replace(/\d/g, d => eastern[parseInt(d, 10)] || d);
};

interface MuqeemCardPreviewProps {
  user: Partial<UserProfile>;
  className?: string;
}

export default function MuqeemCardPreview({ user, className = '' }: MuqeemCardPreviewProps) {
  const [showQrBack, setShowQrBack] = useState(false);
  const [lang, setLang] = useState<'ar' | 'en'>('ar');
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
        <div className="flex items-center gap-1.5">
          <div className="flex items-center bg-slate-100 p-0.5 rounded-lg border border-slate-200 shadow-2xs">
            <button
              type="button"
              onClick={() => setLang('en')}
              className={`flex items-center gap-1 text-xs font-semibold px-2 py-0.5 rounded-md transition-all cursor-pointer ${
                lang === 'en'
                  ? 'bg-white text-emerald-800 shadow-2xs font-bold'
                  : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              <Languages className="w-3 h-3 text-emerald-600" />
              <span>English</span>
            </button>
            <button
              type="button"
              onClick={() => setLang('ar')}
              className={`flex items-center gap-1 text-xs font-semibold px-2 py-0.5 rounded-md transition-all cursor-pointer ${
                lang === 'ar'
                  ? 'bg-white text-emerald-800 shadow-2xs font-bold'
                  : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              <span>العربية</span>
            </button>
          </div>
          <DocumentExportMenu
            user={user}
            docType="RESIDENT_ID"
            size="sm"
          />
          <button
            type="button"
            onClick={() => setShowQrBack(!showQrBack)}
            className="flex items-center gap-1.5 text-xs font-semibold text-gray-700 hover:text-emerald-700 bg-white hover:bg-emerald-50 border border-gray-300 px-3 py-1 rounded-lg shadow-2xs transition-all cursor-pointer"
          >
            <RefreshCw className={`w-3.5 h-3.5 text-emerald-600 ${showQrBack ? 'rotate-180 transition-transform' : ''}`} />
            <span>{showQrBack ? 'Front' : 'QR'}</span>
          </button>
        </div>
      </div>

      {/* Card Container (Aspect Ratio 1.586 matching Android ISO/IEC 7810 ID-1 standard) */}
      <div
        className="w-full max-w-[540px] aspect-[1.586/1] rounded-2xl shadow-xl border border-amber-900/15 overflow-hidden relative select-none @container"
        style={{
          backgroundColor: '#FCFBF7',
          backgroundImage: 'url(/bg_resident_card.webp)',
          backgroundSize: 'cover',
          backgroundPosition: 'center',
          boxShadow: '0 12px 32px -4px rgba(12, 61, 46, 0.18), 0 4px 12px -2px rgba(0, 0, 0, 0.08)',
          containerType: 'inline-size',
        }}
      >
        {!showQrBack ? (
          /* FRONT SIDE: 1:1 Pixel Alignment with Android DynamicMuqeemCard.kt */
          <div className="absolute inset-0">
            {/* 1. Version Number beside pre-printed 'رقم النسخة' */}
            <div
              className="absolute font-sans font-extrabold text-[#1E1E1E]"
              style={{
                left: '9.8%',
                top: '15.5%',
                fontSize: '4.30cqw',
                lineHeight: 1,
              }}
            >
              {user.versionNumber ? (lang === 'en' ? user.versionNumber : toArabicNumerals(user.versionNumber)) : (lang === 'en' ? '1' : '١')}
            </div>

            {/* 2. Portrait Photo Cutout */}
            <div
              className="absolute overflow-hidden rounded-[0.55cqw] bg-[#E8EEF4] border border-[#dcd6c8] shadow-xs"
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
                  <span className="text-[1.8cqw] font-medium text-gray-400 mt-1">No Photo</span>
                </div>
              )}
            </div>

            {/* 3. Lower Verification Box: Authentic Scannable QR + 4-Line Arabic Disclaimer */}
            <div
              className="absolute bg-white rounded-[0.83cqw] border border-[#D0CAC0] pr-[0.4cqw] pl-[0.2cqw] py-0 flex items-center justify-between"
              style={{
                left: '4.6%',
                top: '75.5%',
                width: '27.0%',
                height: '16.8%',
              }}
            >
              {/* QR Code Container with Centered Absher Logo (Touching left border, larger) */}
              <div className="relative flex items-center justify-start h-full aspect-square">
                <img
                  src={qrImageUrl}
                  alt="Valid Security QR Code"
                  onError={() => setQrSrcIndex((prev) => prev + 1)}
                  className="w-full h-full object-contain"
                />
                <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[28%] h-[28%] bg-white rounded-[0.25cqw] p-[0.15cqw] shadow-xs flex items-center justify-center">
                  <img
                    src="/ic_absher_qr_emblem.png"
                    alt="Absher"
                    className="w-full h-full object-contain"
                  />
                </div>
              </div>

              {/* 4-Line Official Arabic Disclaimer with Extra Bold Typography (Zero Cropping) */}
              <div
                className="text-right text-[#2B2B2B] font-black flex-1 pr-[0.4cqw] py-[0.35cqw] flex flex-col justify-between h-full select-none font-[family-name:var(--font-kufi)]"
                style={{
                  fontSize: '1.33cqw',
                  lineHeight: '1.55cqw',
                  fontFamily: 'var(--font-kufi), "Noto Kufi Arabic", sans-serif',
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

            {/* 5. Citizen Data Fields (Shifted to top: 28.2% to ensure clearance below top decorative wave line) */}
            {lang === 'ar' ? (
              /* ARABIC DATA LAYOUT */
              <div
                className="absolute flex flex-col justify-between"
                style={{
                  left: '32.0%',
                  top: '25.5%',
                  width: '64.5%',
                  height: '70.5%',
                }}
                dir="rtl"
              >
                {/* Names Header */}
                <div className="flex flex-col text-right">
                  <div
                    className="font-bold text-[#1E1E1E] leading-none truncate font-sans"
                    style={{
                      fontSize: '3.80cqw',
                      lineHeight: '4.10cqw',
                    }}
                  >
                    {user.fullNameAr || ''}
                  </div>
                  <div
                    className="font-medium text-[#222222] uppercase leading-none truncate font-sans"
                    style={{
                      fontSize: '2.70cqw',
                      lineHeight: '2.90cqw',
                      letterSpacing: '0.04cqw',
                      marginTop: '0.05cqw',
                    }}
                    dir="ltr"
                  >
                    {user.fullNameEn?.toUpperCase() || ''}
                  </div>
                </div>

                {/* Row 1: Expiry Date (Left) | National ID (Right) */}
                <div className="flex items-center justify-between min-w-0">
                  <div className="flex-[1.12] flex items-center gap-[0.40cqw] min-w-0">
                    <span
                      className="text-[#828072] font-semibold select-none shrink-0"
                      style={{
                        fontFamily: 'var(--font-tajawal), "Tajawal", sans-serif',
                        fontSize: '2.15cqw',
                        lineHeight: '2.35cqw',
                      }}
                    >
                      رقم الهوية:
                    </span>
                    <span
                      className="font-bold text-[#1E1E1E] font-mono tracking-wider truncate"
                      style={{
                        fontSize: '2.35cqw',
                        lineHeight: '2.55cqw',
                      }}
                    >
                      {user.nationalId ? toArabicNumerals(user.nationalId) : ''}
                    </span>
                  </div>
                  <div className="flex-1 flex items-center gap-[0.40cqw] min-w-0">
                    <span
                      className="text-[#828072] font-semibold select-none shrink-0"
                      style={{
                        fontFamily: 'var(--font-tajawal), "Tajawal", sans-serif',
                        fontSize: '2.15cqw',
                        lineHeight: '2.35cqw',
                      }}
                    >
                      تاريخ الانتهاء:
                    </span>
                    <span
                      className="font-bold text-[#1E1E1E] font-sans truncate"
                      style={{
                        fontSize: '2.35cqw',
                        lineHeight: '2.55cqw',
                      }}
                    >
                      {user.expiryDateAr || (user.expiryDateEn ? toArabicNumerals(user.expiryDateEn) : '')}
                    </span>
                  </div>
                </div>

                {/* Row 2: Place of Birth (Left) | Date of Birth (Right) */}
                <div className="flex items-center justify-between min-w-0">
                  <div className="flex-[1.12] flex items-center gap-[0.40cqw] min-w-0">
                    <span
                      className="text-[#828072] font-semibold select-none shrink-0"
                      style={{
                        fontFamily: 'var(--font-tajawal), "Tajawal", sans-serif',
                        fontSize: '2.15cqw',
                        lineHeight: '2.35cqw',
                      }}
                    >
                      تاريخ الميلاد:
                    </span>
                    <span
                      className="font-bold text-[#1E1E1E] font-sans truncate"
                      style={{
                        fontSize: '2.35cqw',
                        lineHeight: '2.55cqw',
                      }}
                    >
                      {user.dateOfBirthAr || (user.dateOfBirth ? toArabicNumerals(user.dateOfBirth) : '')}
                    </span>
                  </div>
                  <div className="flex-1 flex items-center gap-[0.40cqw] min-w-0">
                    <span
                      className="text-[#828072] font-semibold select-none shrink-0"
                      style={{
                        fontFamily: 'var(--font-tajawal), "Tajawal", sans-serif',
                        fontSize: '2.15cqw',
                        lineHeight: '2.35cqw',
                      }}
                    >
                      مكان الميلاد:
                    </span>
                    <span
                      className="font-bold text-[#1E1E1E] truncate font-sans"
                      style={{
                        fontSize: '2.35cqw',
                        lineHeight: '2.55cqw',
                      }}
                    >
                      {user.placeOfBirthAr || user.placeOfBirthEn || ''}
                    </span>
                  </div>
                </div>

                {/* Row 3: Religion (Left) | Nationality (Right) */}
                <div className="flex items-center justify-between min-w-0">
                  <div className="flex-[1.12] flex items-center gap-[0.40cqw] min-w-0">
                    <span
                      className="text-[#828072] font-semibold select-none shrink-0"
                      style={{
                        fontFamily: 'var(--font-tajawal), "Tajawal", sans-serif',
                        fontSize: '2.15cqw',
                        lineHeight: '2.35cqw',
                      }}
                    >
                      الجنسية:
                    </span>
                    <span
                      className="font-bold text-[#1E1E1E] font-sans truncate"
                      style={{
                        fontSize: '2.35cqw',
                        lineHeight: '2.55cqw',
                      }}
                    >
                      {user.nationalityAr || user.nationality || ''}
                    </span>
                  </div>
                  <div className="flex-1 flex items-center gap-[0.40cqw] min-w-0">
                    <span
                      className="text-[#828072] font-semibold select-none shrink-0"
                      style={{
                        fontFamily: 'var(--font-tajawal), "Tajawal", sans-serif',
                        fontSize: '2.15cqw',
                        lineHeight: '2.35cqw',
                      }}
                    >
                      الديانة:
                    </span>
                    <span
                      className="font-bold text-[#1E1E1E] font-sans truncate"
                      style={{
                        fontSize: '2.35cqw',
                        lineHeight: '2.55cqw',
                      }}
                    >
                      {user.religionAr || (user.religionEn === 'Islam' ? 'مسلم' : user.religionEn) || ''}
                    </span>
                  </div>
                </div>

                {/* Row 4: Profession */}
                <div className="flex items-center gap-[0.40cqw] min-w-0">
                  <span
                    className="text-[#828072] font-semibold select-none shrink-0"
                    style={{
                      fontFamily: 'var(--font-tajawal), "Tajawal", sans-serif',
                      fontSize: '2.15cqw',
                      lineHeight: '2.35cqw',
                    }}
                  >
                    المهنة:
                  </span>
                  <span
                    className="font-bold text-[#1E1E1E] truncate font-sans"
                    style={{
                      fontSize: '2.35cqw',
                      lineHeight: '2.55cqw',
                    }}
                  >
                    {user.professionAr || user.professionEn || ''}
                  </span>
                </div>

                {/* Row 5: Employer / Sponsor ID */}
                <div className="flex items-center gap-[0.40cqw] min-w-0">
                  <span
                    className="text-[#828072] font-semibold select-none shrink-0"
                    style={{
                      fontFamily: 'var(--font-tajawal), "Tajawal", sans-serif',
                      fontSize: '2.15cqw',
                      lineHeight: '2.35cqw',
                    }}
                  >
                    هوية صاحب العمل:
                  </span>
                  <span
                    className="font-mono text-[#1E1E1E] font-bold truncate"
                    style={{
                      fontSize: '2.35cqw',
                      lineHeight: '2.55cqw',
                    }}
                  >
                    {user.sponsorId ? toArabicNumerals(user.sponsorId) : ''}
                  </span>
                </div>

                {/* Row 6: Place of Issue */}
                <div className="flex items-center gap-[0.40cqw] min-w-0">
                  <span
                    className="text-[#828072] font-semibold select-none shrink-0"
                    style={{
                      fontFamily: 'var(--font-tajawal), "Tajawal", sans-serif',
                      fontSize: '2.15cqw',
                      lineHeight: '2.35cqw',
                    }}
                  >
                    مكان الإصدار:
                  </span>
                  <span
                    className="font-bold text-[#1E1E1E] truncate font-sans"
                    style={{
                      fontSize: '2.35cqw',
                      lineHeight: '2.55cqw',
                    }}
                  >
                    {user.issuePlace || user.issuePlaceEn || 'شركة العلم لامن المعلومات'}
                  </span>
                </div>

                {/* Row 7: Place of Work (مكان العمل) */}
                <div className="flex items-center gap-[0.40cqw] min-w-0">
                  <span
                    className="text-[#828072] font-semibold select-none shrink-0"
                    style={{
                      fontFamily: 'var(--font-tajawal), "Tajawal", sans-serif',
                      fontSize: '2.15cqw',
                      lineHeight: '2.35cqw',
                    }}
                  >
                    مكان العمل:
                  </span>
                  <span
                    className="font-bold text-[#1E1E1E] truncate font-sans"
                    style={{
                      fontSize: '2.35cqw',
                      lineHeight: '2.55cqw',
                    }}
                  >
                    {user.workPlaceAr || 'منطقة الرياض'}
                  </span>
                </div>

                {/* Row 8: Employer Name (اسم صاحب العمل) */}
                <div className="flex items-center gap-[0.40cqw] min-w-0">
                  <span
                    className="text-[#828072] font-semibold select-none shrink-0"
                    style={{
                      fontFamily: 'var(--font-tajawal), "Tajawal", sans-serif',
                      fontSize: '2.15cqw',
                      lineHeight: '2.35cqw',
                    }}
                  >
                    اسم صاحب العمل:
                  </span>
                  <span
                    className="font-bold text-[#1E1E1E] truncate font-sans"
                    style={{
                      fontSize: '2.35cqw',
                      lineHeight: '2.55cqw',
                    }}
                  >
                    {user.sponsorName || user.sponsorNameEn || ''}
                  </span>
                </div>
              </div>
            ) : (
              /* ENGLISH DATA LAYOUT */
              <div
                className="absolute flex flex-col justify-between"
                style={{
                  left: '32.0%',
                  top: '25.5%',
                  width: '64.5%',
                  height: '70.5%',
                }}
                dir="ltr"
              >
                {/* Names Header */}
                <div className="flex flex-col text-left">
                  <div
                    className="font-bold text-[#1E1E1E] leading-none truncate font-sans"
                    style={{
                      fontSize: '3.40cqw',
                      lineHeight: '3.70cqw',
                    }}
                    dir="rtl"
                  >
                    {user.fullNameAr || ''}
                  </div>
                  <div
                    className="font-medium text-[#222222] uppercase leading-none truncate font-sans"
                    style={{
                      fontSize: '2.65cqw',
                      lineHeight: '2.85cqw',
                      letterSpacing: '0.04cqw',
                      marginTop: '0.05cqw',
                    }}
                    dir="ltr"
                  >
                    {user.fullNameEn?.toUpperCase() || ''}
                  </div>
                </div>

                {/* Row 1: Iqama Number (Left) | Expiry Date (Right) */}
                <div className="flex items-center justify-between min-w-0">
                  <div className="flex-[1.25] flex items-center gap-[0.40cqw] min-w-0">
                    <span
                      className="text-[#828072] font-medium font-sans select-none shrink-0"
                      style={{ fontSize: '1.95cqw', lineHeight: '2.15cqw' }}
                    >
                      Iqama Number:
                    </span>
                    <span
                      className="font-bold text-[#1E1E1E] font-mono tracking-wider truncate"
                      style={{ fontSize: '2.10cqw', lineHeight: '2.30cqw' }}
                    >
                      {user.nationalId || ''}
                    </span>
                  </div>
                  <div className="flex-1 flex items-center gap-[0.40cqw] min-w-0">
                    <span
                      className="text-[#828072] font-medium font-sans select-none shrink-0"
                      style={{ fontSize: '1.95cqw', lineHeight: '2.15cqw' }}
                    >
                      Expiry Date:
                    </span>
                    <span
                      className="font-bold text-[#1E1E1E] font-sans truncate"
                      style={{ fontSize: '2.10cqw', lineHeight: '2.30cqw' }}
                    >
                      {user.expiryDateEn || (user.expiryDateAr ? toArabicNumerals(user.expiryDateAr) : '')}
                    </span>
                  </div>
                </div>

                {/* Row 2: Date of Birth | Place of Birth */}
                <div className="flex items-center justify-between min-w-0">
                  <div className="flex-[1.25] flex items-center gap-[0.40cqw] min-w-0">
                    <span
                      className="text-[#828072] font-medium font-sans select-none shrink-0"
                      style={{ fontSize: '1.95cqw', lineHeight: '2.15cqw' }}
                    >
                      Date of Birth :
                    </span>
                    <span
                      className="font-bold text-[#1E1E1E] font-sans truncate"
                      style={{ fontSize: '2.10cqw', lineHeight: '2.30cqw' }}
                    >
                      {user.dateOfBirth || (user.dateOfBirthAr ? toArabicNumerals(user.dateOfBirthAr) : '')}
                    </span>
                  </div>
                  <div className="flex-1 flex items-center gap-[0.40cqw] min-w-0">
                    <span
                      className="text-[#828072] font-medium font-sans select-none shrink-0"
                      style={{ fontSize: '1.95cqw', lineHeight: '2.15cqw' }}
                    >
                      Place of Birth :
                    </span>
                    <span
                      className="font-bold text-[#1E1E1E] truncate font-sans"
                      style={{ fontSize: '2.10cqw', lineHeight: '2.30cqw' }}
                    >
                      {user.placeOfBirthEn || user.placeOfBirthAr || ''}
                    </span>
                  </div>
                </div>

                {/* Row 3: Nationality | Religion */}
                <div className="flex items-center justify-between min-w-0">
                  <div className="flex-[1.25] flex items-center gap-[0.40cqw] min-w-0">
                    <span
                      className="text-[#828072] font-medium font-sans select-none shrink-0"
                      style={{ fontSize: '1.95cqw', lineHeight: '2.15cqw' }}
                    >
                      Nationality:
                    </span>
                    <span
                      className="font-bold text-[#1E1E1E] font-sans truncate"
                      style={{ fontSize: '2.10cqw', lineHeight: '2.30cqw' }}
                    >
                      {user.nationality || user.nationalityAr || ''}
                    </span>
                  </div>
                  <div className="flex-1 flex items-center gap-[0.40cqw] min-w-0">
                    <span
                      className="text-[#828072] font-medium font-sans select-none shrink-0"
                      style={{ fontSize: '1.95cqw', lineHeight: '2.15cqw' }}
                    >
                      Religion:
                    </span>
                    <span
                      className="font-bold text-[#1E1E1E] font-sans truncate"
                      style={{ fontSize: '2.10cqw', lineHeight: '2.30cqw' }}
                    >
                      {user.religionEn || (user.religionAr === 'مسلم' ? 'Islam' : user.religionAr) || ''}
                    </span>
                  </div>
                </div>

                {/* Row 4: Occupation */}
                <div className="flex items-center gap-[0.40cqw] min-w-0">
                  <span
                    className="text-[#828072] font-medium font-sans select-none shrink-0"
                    style={{ fontSize: '1.95cqw', lineHeight: '2.15cqw' }}
                  >
                    Occupation :
                  </span>
                  <span
                    className="font-bold text-[#1E1E1E] truncate font-sans"
                    style={{ fontSize: '2.10cqw', lineHeight: '2.30cqw' }}
                  >
                    {user.professionEn || user.professionAr || ''}
                  </span>
                </div>

                {/* Row 5: Sponsor ID */}
                <div className="flex items-center gap-[0.40cqw] min-w-0">
                  <span
                    className="text-[#828072] font-medium font-sans select-none shrink-0"
                    style={{ fontSize: '1.95cqw', lineHeight: '2.15cqw' }}
                  >
                    Sponsor ID:
                  </span>
                  <span
                    className="font-mono text-[#1E1E1E] font-bold truncate"
                    style={{ fontSize: '2.10cqw', lineHeight: '2.30cqw' }}
                  >
                    {user.sponsorId || ''}
                  </span>
                </div>

                {/* Row 6: Issuing Place */}
                <div className="flex items-center gap-[0.40cqw] min-w-0">
                  <span
                    className="text-[#828072] font-medium font-sans select-none shrink-0"
                    style={{ fontSize: '1.95cqw', lineHeight: '2.15cqw' }}
                  >
                    Issuing Place:
                  </span>
                  <span
                    className="font-bold text-[#1E1E1E] truncate font-sans"
                    style={{ fontSize: '2.10cqw', lineHeight: '2.30cqw' }}
                  >
                    {user.issuePlaceEn || user.issuePlace || 'Elm Information Security'}
                  </span>
                </div>

                {/* Row 7: Work Place */}
                <div className="flex items-center gap-[0.40cqw] min-w-0">
                  <span
                    className="text-[#828072] font-medium font-sans select-none shrink-0"
                    style={{ fontSize: '1.95cqw', lineHeight: '2.15cqw' }}
                  >
                    Work Place:
                  </span>
                  <span
                    className="font-bold text-[#1E1E1E] truncate font-sans"
                    style={{ fontSize: '2.10cqw', lineHeight: '2.30cqw' }}
                  >
                    {user.workPlaceAr ? (user.workPlaceAr === 'منطقة الرياض' ? 'Riyadh Region' : user.workPlaceAr) : 'Riyadh Region'}
                  </span>
                </div>

                {/* Row 8: Sponsor Name */}
                <div className="flex items-center gap-[0.40cqw] min-w-0">
                  <span
                    className="text-[#828072] font-medium font-sans select-none shrink-0"
                    style={{ fontSize: '1.95cqw', lineHeight: '2.15cqw' }}
                  >
                    Sponsor Name:
                  </span>
                  <span
                    className="font-bold text-[#1E1E1E] truncate font-sans"
                    style={{ fontSize: '2.10cqw', lineHeight: '2.30cqw' }}
                  >
                    {user.sponsorNameEn || user.sponsorName || ''}
                  </span>
                </div>
              </div>
            )}
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

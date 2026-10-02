'use client';

import React, { useState } from 'react';
import { UserProfile } from '@/types';
import { buildOfficialQrPayload, getQrCodeFallbackUrls } from '@/lib/officialQr';
import { RefreshCw, CheckCircle2, ShieldCheck, User } from 'lucide-react';
import DocumentExportMenu from '@/components/common/DocumentExportMenu';

interface DrivingLicensePreviewProps {
  user: Partial<UserProfile>;
  className?: string;
}

const toArabicNumerals = (str: string): string =>
  str.replace(/[0-9]/g, (d) => '٠١٢٣٤٥٦٧٨٩'[parseInt(d, 10)]);

export default function DrivingLicensePreview({ user, className = '' }: DrivingLicensePreviewProps) {
  const [showQrBack, setShowQrBack] = useState(false);
  const [qrSrcIndex, setQrSrcIndex] = useState(0);

  const qrPayload = buildOfficialQrPayload(user);
  const qrUrls = getQrCodeFallbackUrls(qrPayload, 240);
  const qrImageUrl = qrUrls[qrSrcIndex % qrUrls.length];

  const fields = [
    {
      labelEn: 'ID Number:',
      valueEn: user.nationalId || '',
      labelAr: 'رقم الهوية:',
      valueAr: user.nationalId ? toArabicNumerals(user.nationalId) : '',
    },
    {
      labelEn: 'License Type:',
      valueEn: user.licenseTypeEn || '',
      labelAr: 'نوع الرخصة:',
      valueAr: user.licenseTypeAr || '',
    },
    {
      labelEn: 'Issue Date:',
      valueEn: user.licenseIssueDateEn || '',
      labelAr: 'تاريخ الإصدار:',
      valueAr: user.licenseIssueDateAr || (user.licenseIssueDateEn ? toArabicNumerals(user.licenseIssueDateEn) : ''),
    },
    {
      labelEn: 'Date of Birth:',
      valueEn: user.dateOfBirth || '',
      labelAr: 'تاريخ الميلاد:',
      valueAr: user.dateOfBirthAr || (user.dateOfBirth ? toArabicNumerals(user.dateOfBirth) : ''),
    },
    {
      labelEn: 'Nationality:',
      valueEn: user.nationality || '',
      labelAr: 'الجنسية:',
      valueAr: user.nationalityAr || '',
    },
    {
      labelEn: 'Expiry Date:',
      valueEn: user.licenseExpiryDateEn || '',
      labelAr: 'تاريخ الانتهاء:',
      valueAr: user.licenseExpiryDateAr || (user.licenseExpiryDateEn ? toArabicNumerals(user.licenseExpiryDateEn) : ''),
    },
    {
      labelEn: 'Blood Type:',
      valueEn: user.bloodType || '',
      labelAr: 'فصيلة الدم:',
      valueAr: user.bloodType || '',
    },
  ];

  // Pre-printed permanent labels: Tajawal for Arabic, SansSerif for English
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

  return (
    <div className={`flex flex-col items-center ${className}`}>
      {/* Top action bar */}
      <div className="w-full max-w-[540px] flex items-center justify-between mb-3 px-1">
        <div className="flex items-center gap-2">
          <span className="inline-flex items-center gap-1 text-[11px] font-semibold text-emerald-800 bg-emerald-100/90 border border-emerald-300 px-2 py-0.5 rounded-full">
            <CheckCircle2 className="w-3 h-3 text-emerald-600" />
            Official Driving License Template
          </span>
        </div>
        <div className="flex items-center gap-1.5">
          <DocumentExportMenu
            user={user}
            docType="DRIVING_LICENSE"
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

      {/* Card Container (Aspect Ratio 1.586 standard ID card matching Android) */}
      <div
        className="w-full max-w-[540px] aspect-[1.586/1] rounded-2xl shadow-xl border-2 border-white overflow-hidden relative select-none @container"
        style={{
          backgroundColor: '#FFFFFF',
          backgroundImage: 'url(/bg_driving_license.webp)',
          backgroundSize: '100% 100%',
          backgroundPosition: 'center',
          boxShadow: '0 12px 32px -4px rgba(12, 61, 46, 0.18), 0 4px 12px -2px rgba(0, 0, 0, 0.08)',
          containerType: 'inline-size',
        }}
      >
        {!showQrBack ? (
          /* FRONT SIDE: 1:1 Match with Android DynamicDrivingLicenseCard.kt */
          <div className="absolute inset-0">
            {/* 1. Holder Photo (Positioned precisely to cover the template's pre-printed photo frame cutout) */}
            <div
              className="absolute overflow-hidden rounded-[2.77cqw] bg-[#E8EEF4] border border-gray-300 shadow-2xs"
              style={{
                left: '2.6%',
                top: '22.7%',
                width: '28.2%',
                height: '49.6%',
              }}
            >
              {user.photoUrl ? (
                <img
                  src={user.photoUrl}
                  alt="Holder Photo"
                  className="w-full h-full object-cover object-center"
                />
              ) : (
                <div className="w-full h-full flex flex-col items-center justify-center text-gray-400 bg-slate-100">
                  <User className="w-7 h-7 text-gray-300" />
                  <span className="text-[1.8cqw] font-medium text-gray-400 mt-1">No Photo</span>
                </div>
              )}
            </div>

            {/* 2. Verification Box: Scannable QR + 4-Line Arabic Disclaimer */}
            <div
              className="absolute bg-white/95 rounded-[0.83cqw] border border-gray-300 px-[0.3cqw] py-0 flex items-center justify-between"
              style={{
                left: '2.6%',
                top: '73.5%',
                width: '28.2%',
                height: '16.3%',
              }}
            >
              {/* QR Container with Centered Absher Logo */}
              <div className="relative flex items-center justify-center" style={{ width: '44%', height: '90%' }}>
                <img
                  src={qrImageUrl}
                  alt="Valid Security QR Code"
                  onError={() => setQrSrcIndex((prev) => prev + 1)}
                  className="w-full h-full object-contain"
                />
                <div className="absolute w-[30%] h-[30%] bg-white rounded-[0.25cqw] p-[0.15cqw] shadow-xs flex items-center justify-center">
                  <img
                    src="/ic_absher_qr_emblem.png"
                    alt="Absher"
                    className="w-full h-full object-contain"
                  />
                </div>
              </div>

              {/* 4-Line Official Arabic Disclaimer with Noto Kufi Arabic Black typography */}
              <div
                className="text-right text-[#2B2B2B] font-black flex-1 pr-[0.4cqw] py-[0.25cqw] flex flex-col justify-between h-full select-none font-[family-name:var(--font-kufi)]"
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

            {/* 3. Holder Name Section: Dynamic names use standard SansSerif */}
            <div
              className="absolute flex flex-col items-end text-right"
              style={{
                left: '32.5%',
                top: '24.0%',
                width: '64.0%',
              }}
            >
              <div
                className="font-bold text-[#222222] leading-tight truncate font-sans w-full"
                style={{
                  fontSize: '3.94cqw',
                  lineHeight: '4.4cqw',
                }}
                dir="rtl"
              >
                {user.fullNameAr || ''}
              </div>
              <div style={{ height: '0.4cqw' }} />
              <div
                className="font-semibold tracking-wide text-[#222222] uppercase truncate font-sans w-full"
                style={{
                  fontSize: '2.83cqw',
                  lineHeight: '3.2cqw',
                  letterSpacing: '0.08cqw',
                }}
                dir="ltr"
              >
                {user.fullNameEn?.toUpperCase() || ''}
              </div>
            </div>

            {/* 4. 7 Bilingual Driving License Credential Rows */}
            <div
              className="absolute flex flex-col justify-between"
              style={{
                left: '32.5%',
                top: '42.2%',
                width: '64.0%',
                height: '52.0%',
              }}
            >
              {fields.map((f, i) => (
                <div key={i} className="flex items-center justify-between w-full">
                  {/* English Column (Left): Pre-printed Label (SansSerif) + Much Bolder Dynamic English Value (SansSerif Black) */}
                  <div className="flex-[0.53] flex items-center gap-[0.6cqw]">
                    <span className="font-bold tracking-tight select-none shrink-0" style={labelStrokeStyleEn}>{f.labelEn}</span>
                    <span
                      className="font-black text-black font-sans truncate flex-1"
                      style={{
                        fontSize: '2.72cqw',
                        lineHeight: '3.1cqw',
                      }}
                    >
                      {f.valueEn}
                    </span>
                  </div>

                  <div style={{ width: '0.55cqw' }} />

                  {/* Arabic Column (Right): Pre-printed Label (Tajawal) + Much Bolder Dynamic Arabic Value (SansSerif Black) */}
                  <div className="flex-[0.47] flex items-center gap-[0.6cqw]" dir="rtl">
                    <span className="font-extrabold tracking-tight select-none shrink-0" style={labelStrokeStyleAr}>{f.labelAr}</span>
                    <span
                      className="font-black text-black font-sans truncate flex-1 text-right"
                      style={{
                        fontSize: '2.72cqw',
                        lineHeight: '3.1cqw',
                      }}
                    >
                      {f.valueAr}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        ) : (
          /* BACK SIDE: Cryptographically Valid Official QR View */
          <div className="w-full h-full p-5 flex flex-col items-center justify-between text-center bg-white/95 backdrop-blur-xs">
            <div className="flex items-center gap-2 text-emerald-800">
              <ShieldCheck className="w-5 h-5 text-emerald-600" />
              <span className="text-xs font-bold uppercase tracking-wider">Official Driving License Matrix</span>
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
              Scan with Absher Traffic Inspector App to verify official driving license privileges and active validity.
            </div>

            <div className="text-[8px] font-mono text-gray-400">
              LICENSE-ID: {user.nationalId || '0000000000'} • CLASS: {user.licenseTypeEn || 'Private'}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

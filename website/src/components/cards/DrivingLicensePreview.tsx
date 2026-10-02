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

  // White font fill with crisp black outline stroke and subtle drop shadow for placeholder permanent labels
  const labelStrokeStyle: React.CSSProperties = {
    color: '#ffffff',
    WebkitTextStroke: '0.6px #111111',
    textShadow:
      '-0.8px -0.8px 0 #111, 0.8px -0.8px 0 #111, -0.8px 0.8px 0 #111, 0.8px 0.8px 0 #111, -0.8px 0 0 #111, 0.8px 0 0 #111, 0 -0.8px 0 #111, 0 0.8px 0 #111, 0 1px 2px rgba(0, 0, 0, 0.45)',
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
        className="w-full max-w-[540px] aspect-[1.586/1] rounded-2xl shadow-xl border-2 border-white overflow-hidden relative select-none"
        style={{
          backgroundColor: '#FFFFFF',
          backgroundImage: 'url(/bg_driving_license.webp)',
          backgroundSize: '100% 100%',
          backgroundPosition: 'center',
          boxShadow: '0 12px 32px -4px rgba(12, 61, 46, 0.18), 0 4px 12px -2px rgba(0, 0, 0, 0.08)',
        }}
      >
        {!showQrBack ? (
          /* FRONT SIDE: 1:1 Match with Android DynamicDrivingLicenseCard.kt */
          <div className="absolute inset-0">
            {/* 1. Holder Photo (Positioned precisely to cover the template's pre-printed photo frame cutout) */}
            <div
              className="absolute overflow-hidden rounded-[12px] bg-[#E8EEF4] border border-gray-300 shadow-2xs"
              style={{
                left: '4.0%',
                top: '23.5%',
                width: '27.4%',
                height: '48.2%',
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
                  <span className="text-[8px] font-medium text-gray-400 mt-1">No Photo</span>
                </div>
              )}
            </div>

            {/* 2. Verification Box: Scannable QR + 4-Line Arabic Disclaimer */}
            <div
              className="absolute bg-white/95 rounded-[4px] border border-gray-300 px-1 py-0.5 flex items-center justify-between"
              style={{
                left: '4.0%',
                top: '72.8%',
                width: '27.4%',
                height: '15.8%',
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
                <div className="absolute w-[30%] h-[30%] bg-white rounded-2xs p-0.5 shadow-2xs flex items-center justify-center">
                  <img
                    src="/ic_absher_qr_emblem.png"
                    alt="Absher"
                    className="w-full h-full object-contain"
                  />
                </div>
              </div>

              {/* 4-Line Official Arabic Disclaimer (Identical typography to primary document) */}
              <div
                className="text-right text-[#2b2b2b] font-black flex-1 pr-1.5 py-0.5 flex flex-col justify-between h-full select-none"
                style={{
                  fontSize: '4.8px',
                  lineHeight: '5.6px',
                }}
                dir="rtl"
              >
                <div>يجب التحقق</div>
                <div>من الرمز السريع</div>
                <div>قبل اعتماد</div>
                <div>التعامل مع الهوية</div>
              </div>
            </div>

            {/* 3. Holder Name Section (Crisp bold dark text matching reference image) */}
            <div
              className="absolute flex flex-col items-end text-right"
              style={{
                right: '5.0%',
                top: '27.2%',
                maxWidth: '65%',
              }}
            >
              <div
                className="font-black text-[#222222] leading-tight truncate text-[14.5px]"
              >
                {user.fullNameAr || ''}
              </div>
              <div
                className="font-bold tracking-wide text-[#222222] uppercase mt-0.5 truncate text-[10.5px]"
              >
                {user.fullNameEn?.toUpperCase() || ''}
              </div>
            </div>

            {/* 4. 7 Bilingual Driving License Credential Rows */}
            <div
              className="absolute flex flex-col justify-between"
              style={{
                left: '33.2%',
                top: '44.0%',
                width: '62.0%',
                height: '49.0%',
              }}
            >
              {fields.map((f, i) => (
                <div key={i} className="flex items-center justify-between text-[#1e1e1e] font-bold text-[8.8px] leading-tight">
                  {/* English Column (Left): White text with black outline stroke + crisp solid black value */}
                  <div className="flex items-center gap-1.5">
                    <span className="font-bold" style={labelStrokeStyle}>{f.labelEn}</span>
                    <span className="font-bold text-[#1e1e1e]">{f.valueEn}</span>
                  </div>

                  {/* Arabic Column (Right): White text with black outline stroke + crisp solid black value */}
                  <div className="flex items-center gap-1.5" dir="rtl">
                    <span className="font-bold" style={labelStrokeStyle}>{f.labelAr}</span>
                    <span className="font-bold text-[#1e1e1e]">{f.valueAr}</span>
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

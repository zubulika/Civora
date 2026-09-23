'use client';
import React from 'react';
import { UserProfile } from '@/types';
import { QrCode } from 'lucide-react';

interface DrivingLicensePreviewProps {
  user: Partial<UserProfile>;
}

const toArabicNumerals = (str: string): string =>
  str.replace(/[0-9]/g, (d) => '٠١٢٣٤٥٦٧٨٩'[parseInt(d)]);

export default function DrivingLicensePreview({ user }: DrivingLicensePreviewProps) {
  const fields = [
    {
      labelEn: 'ID Number:',
      valueEn: user.nationalId || '2631173305',
      labelAr: 'رقم الهوية:',
      valueAr: toArabicNumerals(user.nationalId || '2631173305'),
    },
    {
      labelEn: 'License Type:',
      valueEn: user.licenseTypeEn || 'Private',
      labelAr: 'نوع الرخصة:',
      valueAr: user.licenseTypeAr || 'خصوصي',
    },
    {
      labelEn: 'Issue Date:',
      valueEn: user.licenseIssueDateEn || '10/03/2026',
      labelAr: 'تاريخ الإصدار:',
      valueAr: user.licenseIssueDateAr || toArabicNumerals('2026/03/10'),
    },
    {
      labelEn: 'Date of Birth:',
      valueEn: user.dateOfBirth || '10/01/1984',
      labelAr: 'تاريخ الميلاد:',
      valueAr: user.dateOfBirthAr || toArabicNumerals('1984/01/10'),
    },
    {
      labelEn: 'Nationality:',
      valueEn: user.nationality || 'Bangladesh',
      labelAr: 'الجنسية:',
      valueAr: user.nationalityAr || 'بنجلاديش',
    },
    {
      labelEn: 'Expiry Date:',
      valueEn: user.licenseExpiryDateEn || '21/11/2035',
      labelAr: 'تاريخ الانتهاء:',
      valueAr: user.licenseExpiryDateAr || toArabicNumerals('2035/11/21'),
    },
    {
      labelEn: 'Blood Type:',
      valueEn: user.bloodType || 'A+',
      labelAr: 'فصيلة الدم:',
      valueAr: user.bloodType || 'A+',
    },
  ];

  return (
    <div
      className="relative w-full max-w-[620px] rounded-2xl overflow-hidden shadow-2xl select-none"
      style={{ aspectRatio: '1.586' }}
    >
      {/* Background template with official graphics & pre-printed headers */}
      <img
        src="/driving-license-template.svg"
        alt="Driving License Template"
        className="absolute inset-0 w-full h-full object-cover"
        draggable={false}
      />

      {/* Dynamic Content Overlay (Strictly holder specific - headers are in template) */}
      <div className="absolute inset-0">

        {/* 1. Holder Photo */}
        <div
          className="absolute overflow-hidden rounded-md bg-slate-200"
          style={{
            left: '6.5%',
            top: '25.9%',
            width: '24.2%',
            height: '44.6%',
          }}
        >
          {user.photoUrl ? (
            <img
              src={user.photoUrl}
              alt="Holder Photo"
              className="w-full h-full object-cover object-center"
            />
          ) : (
            <div className="w-full h-full flex items-center justify-center text-gray-400 text-xs font-medium">
              Photo
            </div>
          )}
        </div>

        {/* 2. Verification Box (QR + 4-line Arabic disclaimer) */}
        <div
          className="absolute flex items-center justify-between"
          style={{
            left: '6.5%',
            top: '73.2%',
            width: '23.6%',
            height: '14.4%',
            padding: '0 1%',
          }}
        >
          <div className="flex items-center justify-center" style={{ width: '42%' }}>
            <QrCode className="text-emerald-900 w-full h-auto" />
          </div>
          <div
            className="text-right text-gray-950 font-bold leading-tight flex-1"
            style={{ fontSize: '1.3%', lineHeight: 1.25 }}
            dir="rtl"
          >
            يجب التحقق
            <br />
            من الرمز السريع
            <br />
            قبل اعتماد
            <br />
            التعامل مع الهوية
          </div>
        </div>

        {/* 3. Holder Name Section */}
        <div
          className="absolute flex flex-col items-end text-right"
          style={{
            right: '5.5%',
            top: '28.0%',
          }}
        >
          <div className="font-bold text-gray-950 leading-tight" style={{ fontSize: '3.4%' }}>
            {user.fullNameAr || 'محمد بالا مد حسين أوسين'}
          </div>
          <div className="font-bold tracking-wide text-gray-950 uppercase mt-0.5" style={{ fontSize: '2.4%' }}>
            {user.fullNameEn || 'MD BALAL HOSSAIN'}
          </div>
        </div>

        {/* 4. 7 Bilingual Driving License Credential Rows */}
        <div
          className="absolute flex flex-col justify-between"
          style={{
            left: '33.8%',
            top: '44.5%',
            width: '60.7%',
            height: '52.0%',
          }}
        >
          {fields.map((field) => (
            <div
              key={field.labelEn}
              className="flex items-center"
              style={{ fontSize: '1.9%', lineHeight: 1.4 }}
            >
              {/* EN column (Left) */}
              <div className="flex items-center gap-1" style={{ width: '50%' }}>
                <span className="font-bold text-gray-950 whitespace-nowrap">{field.labelEn}</span>
                <span className="font-bold text-gray-950 truncate">{field.valueEn}</span>
              </div>
              {/* AR column (Right, RTL) */}
              <div className="flex items-center justify-start gap-1" style={{ width: '50%' }} dir="rtl">
                <span className="font-bold text-gray-950 whitespace-nowrap">{field.labelAr}</span>
                <span className="font-bold text-gray-950 truncate">{field.valueAr}</span>
              </div>
            </div>
          ))}
        </div>

      </div>
    </div>
  );
}

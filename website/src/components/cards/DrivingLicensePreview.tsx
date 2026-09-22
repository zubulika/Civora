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
      labelAr: ':رقم الهوية',
      valueAr: toArabicNumerals(user.nationalId || '2631173305'),
    },
    {
      labelEn: 'License Type:',
      valueEn: user.licenseTypeEn || 'Private',
      labelAr: ':نوع الرخصة',
      valueAr: user.licenseTypeAr || 'خصوصي',
    },
    {
      labelEn: 'Issue Date:',
      valueEn: user.licenseIssueDateEn || '10/03/2026',
      labelAr: ':تاريخ الإصدار',
      valueAr: user.licenseIssueDateAr || toArabicNumerals('2026/03/10'),
    },
    {
      labelEn: 'Date of Birth:',
      valueEn: user.dateOfBirth || '10/01/1984',
      labelAr: ':تاريخ الميلاد',
      valueAr: user.dateOfBirthAr || toArabicNumerals('1984/01/10'),
    },
    {
      labelEn: 'Nationality:',
      valueEn: user.nationality || 'Bangladesh',
      labelAr: ':الجنسية',
      valueAr: user.nationalityAr || 'بنجلاديش',
    },
    {
      labelEn: 'Expiry Date:',
      valueEn: user.licenseExpiryDateEn || '21/11/2035',
      labelAr: ':تاريخ الانتهاء',
      valueAr: user.licenseExpiryDateAr || toArabicNumerals('2035/11/21'),
    },
    {
      labelEn: 'Blood Type:',
      valueEn: user.bloodType || 'A+',
      labelAr: ':فصيلة الدم',
      valueAr: user.bloodType || 'A+',
    },
  ];

  return (
    <div
      className="relative w-full max-w-[620px] rounded-2xl overflow-hidden shadow-2xl select-none"
      style={{ aspectRatio: '1.586' }}
    >
      {/* Background template */}
      <img
        src="/driving-license-template.svg"
        alt="Driving License Template"
        className="absolute inset-0 w-full h-full object-cover"
        draggable={false}
      />

      {/* Content overlay */}
      <div className="absolute inset-0 flex flex-col" style={{ padding: '4.5% 5%' }}>

        {/* --- Row 1: Header --- */}
        <div className="flex items-start justify-between">
          {/* Left: Arabic title */}
          <div className="font-bold text-green-900" style={{ fontSize: '3.2%', lineHeight: 1.3 }}>
            رخصة سياقة
          </div>
          {/* Right: Saudi emblem + ministry name */}
          <div className="flex flex-col items-end gap-0.5">
            <img src="/saudi_emblem.svg" alt="Saudi Emblem" style={{ height: '8%', width: 'auto' }} className="h-[8%]" />
            <div className="text-right" style={{ fontSize: '1.8%' }}>
              <div className="font-bold text-gray-800">المملكة العربية السعودية</div>
              <div className="text-gray-700">وزارة الداخلية</div>
            </div>
          </div>
        </div>

        {/* --- Row 2: Name section --- */}
        <div className="flex justify-end mt-1">
          <div className="text-right leading-tight">
            <div className="font-bold text-gray-900" style={{ fontSize: '3.5%' }}>
              {user.fullNameAr || 'محمد بالا مد حسين أوسين'}
            </div>
            <div className="font-semibold tracking-wide text-gray-700 uppercase" style={{ fontSize: '2.4%' }}>
              {user.fullNameEn || 'MD BALAL HOSSAIN'}
            </div>
          </div>
        </div>

        {/* --- Row 3: Photo + Fields --- */}
        <div className="flex gap-3 flex-1 mt-2 min-h-0">
          {/* Left column: photo + QR */}
          <div className="flex flex-col gap-1.5" style={{ width: '26%' }}>
            <div
              className="bg-gray-200 overflow-hidden rounded-sm border border-gray-300/50"
              style={{ flex: '1 1 auto' }}
            >
              {user.photoUrl ? (
                <img
                  src={user.photoUrl}
                  alt="Holder Photo"
                  className="w-full h-full object-cover object-center"
                />
              ) : (
                <div className="w-full h-full flex items-center justify-center text-gray-400 text-xs">
                  Photo
                </div>
              )}
            </div>
            {/* QR code area */}
            <div className="bg-white/80 p-1.5 rounded-sm flex flex-col items-center justify-center gap-0.5">
              <QrCode className="text-emerald-800" style={{ width: '70%', height: 'auto' }} />
            </div>
            <div className="text-center text-gray-700" style={{ fontSize: '1.4%', lineHeight: 1.2 }} dir="rtl">
              يجب التحقق<br />من الرمز السريع<br />قبل اعتماد<br />التعامل مع الهوية
            </div>
          </div>

          {/* Right column: bilingual fields */}
          <div className="flex-1 flex flex-col justify-start gap-y-0.5">
            {fields.map((field) => (
              <div
                key={field.labelEn}
                className="flex items-center"
                style={{ fontSize: '2%', lineHeight: 1.5 }}
              >
                {/* EN side */}
                <div className="flex items-center gap-0.5" style={{ width: '50%' }}>
                  <span className="font-semibold text-gray-700 whitespace-nowrap">{field.labelEn}</span>
                  <span className="font-bold text-gray-900 ml-1">{field.valueEn}</span>
                </div>
                {/* AR side */}
                <div className="flex items-center justify-end gap-0.5" style={{ width: '50%' }} dir="rtl">
                  <span className="font-semibold text-gray-700 whitespace-nowrap">{field.labelAr}</span>
                  <span className="font-bold text-gray-900 mr-1">{field.valueAr}</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

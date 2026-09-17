'use client';

import React, { useState } from 'react';
import { UserProfile } from '@/types';
import { 
  User, 
  Briefcase, 
  FileText, 

  ShieldCheck, 
  Save, 

  Upload,
  KeyRound,
  Eye,
  EyeOff,
  RefreshCw
} from 'lucide-react';

interface CitizenFormProps {
  initialData?: Partial<UserProfile>;
  onSubmit: (data: UserProfile) => Promise<void>;
  onChange?: (data: UserProfile) => void;
  isSubmitting?: boolean;
}

const NATIONALITY_PRESETS = [
  { en: 'Bangladesh', ar: 'بنجلاديش' },
  { en: 'India', ar: 'الهند' },
  { en: 'Pakistan', ar: 'باكستان' },
  { en: 'Egypt', ar: 'مصر' },
  { en: 'Philippines', ar: 'الفلبين' },
  { en: 'Yemen', ar: 'اليمن' },
  { en: 'Saudi Arabia', ar: 'المملكة العربية السعودية' }
];

const PROFESSION_PRESETS = [
  { en: 'Laundry Worker', ar: 'عامل غسيل ملابس' },
  { en: 'General Laborer', ar: 'عامل عادي' },
  { en: 'Driver', ar: 'سائق خاص' },
  { en: 'Software Engineer', ar: 'مهندس برمجيات' },
  { en: 'Accountant', ar: 'محاسب عام' },
  { en: 'Electrical Technician', ar: 'فني كهرباء' },
  { en: 'Pharmacist', ar: 'صيدلي' }
];

export default function CitizenForm({ 
  initialData, 
  onSubmit, 
  onChange, 
  isSubmitting = false 
}: CitizenFormProps) {
  const [formData, setFormData] = useState<UserProfile>({
    id: initialData?.id || 'usr_new',
    nationalId: initialData?.nationalId || '',
    appPassword: initialData?.appPassword || 'Civora2026!',
    accountStatus: initialData?.accountStatus || 'ACTIVE',
    fullNameEn: initialData?.fullNameEn || '',
    fullNameAr: initialData?.fullNameAr || '',
    dateOfBirth: initialData?.dateOfBirth || '1990/01/01',
    dateOfBirthAr: initialData?.dateOfBirthAr || '١٩٩٠/٠١/٠١',
    dateOfBirthHijri: initialData?.dateOfBirthHijri || '1410/06/04',
    nationality: initialData?.nationality || 'Bangladesh',
    nationalityAr: initialData?.nationalityAr || 'بنجلاديش',
    placeOfBirthEn: initialData?.placeOfBirthEn || 'Bangladesh',
    placeOfBirthAr: initialData?.placeOfBirthAr || 'بنجلاديش',
    religionEn: initialData?.religionEn || 'Islam',
    religionAr: initialData?.religionAr || 'الاسلام',
    professionEn: initialData?.professionEn || 'Laundry Worker',
    professionAr: initialData?.professionAr || 'عامل غسيل ملابس',
    sponsorId: initialData?.sponsorId || '7034884309',
    sponsorNameEn: initialData?.sponsorNameEn || 'Durrat Najah Laundry',
    sponsorName: initialData?.sponsorName || 'مؤسسة درر نجاح للملابس',
    issuePlaceEn: initialData?.issuePlaceEn || 'Elm Information Security',
    issuePlace: initialData?.issuePlace || 'شركة العلم لامن المعلومات',
    workPlaceAr: initialData?.workPlaceAr || 'منطقة الرياض',
    expiryDateEn: initialData?.expiryDateEn || '2026/10/08',
    expiryDateAr: initialData?.expiryDateAr || '٢٠٢٦/١٠/٠٨',
    versionNumber: initialData?.versionNumber || '٢',
    expiryDateDigits: initialData?.expiryDateDigits || '081026',
    issueDateDigits: initialData?.issueDateDigits || '070926',
    photoUrl: initialData?.photoUrl || 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&auto=format&fit=crop&q=80',
    verificationLevel: initialData?.verificationLevel || 'TIER_3_VERIFIED',
    digitalIdActive: initialData?.digitalIdActive !== false,
    totalDocuments: initialData?.totalDocuments || 4,
    activeRequestsCount: initialData?.activeRequestsCount || 0,
    unreadNotificationsCount: initialData?.unreadNotificationsCount || 0
  });

  const [showPassword, setShowPassword] = useState(false);

  const generateRandomPassword = () => {
    const randomNum = Math.floor(1000 + Math.random() * 9000);
    const newPass = `Absher#${randomNum}!`;
    handleChange('appPassword', newPass);
  };

  const handleChange = (field: keyof UserProfile, value: UserProfile[keyof UserProfile]) => {
    const updated = { ...formData, [field]: value };
    setFormData(updated);
    onChange?.(updated);
  };

  const handleChanges = (changes: Partial<UserProfile>) => {
    const updated = { ...formData, ...changes };
    setFormData(updated);
    onChange?.(updated);
  };

  const handlePhotoUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        if (typeof reader.result === 'string') {
          handleChange('photoUrl', reader.result);
        }
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await onSubmit(formData);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      {/* 1. Personal Identity */}
      <div className="bg-white p-6 rounded-2xl border border-gray-200/90 shadow-xs">
        <div className="flex items-center gap-2.5 pb-4 mb-5 border-b border-gray-100">
          <div className="w-8 h-8 rounded-lg bg-emerald-50 text-emerald-700 flex items-center justify-center font-bold">
            <User className="w-4 h-4" />
          </div>
          <div>
            <h3 className="font-bold text-gray-900 text-sm">Personal Information</h3>
            <p className="text-xs text-gray-500">Citizen & Resident demographics</p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {/* National / Iqama ID */}
          <div>
            <label className="block text-xs font-semibold text-gray-700 mb-1">
              National ID / Iqama Number (10 digits) *
            </label>
            <input
              type="text"
              required
              maxLength={10}
              pattern="\\d{10}"
              inputMode="numeric"
              value={formData.nationalId}
              onChange={(e) => handleChange('nationalId', e.target.value.replace(/\D/g, ''))}
              placeholder="e.g. 2495685261"
              className="w-full px-3.5 py-2 border border-gray-300 rounded-xl text-xs font-mono font-bold tracking-wider text-gray-900 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-600"
            />
          </div>

          {/* Photo Selector */}
          <div>
            <label className="block text-xs font-semibold text-gray-700 mb-1">
              Portrait Photo (File or URL)
            </label>
            <div className="flex gap-2">
              <input
                type="text"
                value={formData.photoUrl}
                onChange={(e) => handleChange('photoUrl', e.target.value)}
                placeholder="https://... or choose local file"
                className="flex-1 px-3.5 py-2 border border-gray-300 rounded-xl text-xs focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-600"
              />
              <label className="px-3 py-2 bg-gray-100 hover:bg-gray-200 border border-gray-300 rounded-xl cursor-pointer text-xs font-medium text-gray-700 flex items-center gap-1.5 transition-colors">
                <Upload className="w-3.5 h-3.5" />
                <span>Upload</span>
                <input type="file" accept="image/*" onChange={handlePhotoUpload} className="hidden" />
              </label>
            </div>
          </div>

          {/* Arabic Full Name */}
          <div>
            <label className="block text-xs font-semibold text-gray-700 mb-1">
              Full Name in Arabic (الاسم الكامل بالعربية) *
            </label>
            <input
              type="text"
              required
              dir="rtl"
              value={formData.fullNameAr}
              onChange={(e) => handleChange('fullNameAr', e.target.value)}
              placeholder="مثال: مد عبد ال حليم مياه"
              className="w-full px-3.5 py-2 border border-gray-300 rounded-xl text-xs font-bold text-gray-900 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-600"
            />
          </div>

          {/* English Full Name */}
          <div>
            <label className="block text-xs font-semibold text-gray-700 mb-1">
              Full Name in English (Official Passport format) *
            </label>
            <input
              type="text"
              required
              value={formData.fullNameEn}
              onChange={(e) => handleChange('fullNameEn', e.target.value.toUpperCase())}
              placeholder="e.g. MD ABDUL HALIM MEIA"
              className="w-full px-3.5 py-2 border border-gray-300 rounded-xl text-xs font-semibold uppercase text-gray-900 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-600"
            />
          </div>

          {/* Nationality with Presets */}
          <div>
            <div className="flex justify-between items-center mb-1">
              <label className="text-xs font-semibold text-gray-700">Nationality (English & Arabic)</label>
            </div>
            <div className="grid grid-cols-2 gap-2 mb-1.5">
              <input
                type="text"
                value={formData.nationality}
                onChange={(e) => handleChange('nationality', e.target.value)}
                placeholder="English (e.g. Bangladesh)"
                className="w-full px-3 py-1.5 border border-gray-300 rounded-xl text-xs"
              />
              <input
                type="text"
                dir="rtl"
                value={formData.nationalityAr}
                onChange={(e) => handleChange('nationalityAr', e.target.value)}
                placeholder="العربية (مثال: بنجلاديش)"
                className="w-full px-3 py-1.5 border border-gray-300 rounded-xl text-xs"
              />
            </div>
            {/* Quick preset chips */}
            <div className="flex flex-wrap gap-1">
              {NATIONALITY_PRESETS.map((p) => (
                <button
                  key={p.en}
                  type="button"
                  onClick={() => handleChanges({
                    nationality: p.en,
                    nationalityAr: p.ar,
                    placeOfBirthAr: p.ar,
                  })}
                  className="text-[10px] px-2 py-0.5 rounded-md bg-gray-100 hover:bg-emerald-100 hover:text-emerald-800 text-gray-600 transition-colors"
                >
                  {p.en}
                </button>
              ))}
            </div>
          </div>

          {/* Date of Birth */}
          <div>
            <label className="block text-xs font-semibold text-gray-700 mb-1">
              Date of Birth (Gregorian & Hijri)
            </label>
            <div className="grid grid-cols-2 gap-2">
              <input
                type="text"
                value={formData.dateOfBirth}
                onChange={(e) => handleChange('dateOfBirth', e.target.value)}
                placeholder="YYYY/MM/DD (1988/02/03)"
                className="w-full px-3 py-1.5 border border-gray-300 rounded-xl text-xs font-mono"
              />
              <input
                type="text"
                value={formData.dateOfBirthHijri}
                onChange={(e) => handleChange('dateOfBirthHijri', e.target.value)}
                placeholder="Hijri (1408/10/18)"
                className="w-full px-3 py-1.5 border border-gray-300 rounded-xl text-xs font-mono"
              />
            </div>
          </div>
        </div>
      </div>

      {/* Mobile App Login Credentials */}
      <div className="bg-white p-6 rounded-2xl border border-gray-200/90 shadow-xs">
        <div className="flex items-center gap-2.5 pb-4 mb-5 border-b border-gray-100">
          <div className="w-8 h-8 rounded-lg bg-emerald-50 text-emerald-700 flex items-center justify-center font-bold">
            <KeyRound className="w-4 h-4" />
          </div>
          <div className="flex-1">
            <div className="flex items-center justify-between">
              <h3 className="font-bold text-gray-900 text-sm">Mobile App Login Credentials</h3>
              <span className={`text-[10px] font-semibold px-2 py-0.5 rounded-full border ${
                formData.accountStatus === 'SUSPENDED'
                  ? 'bg-rose-50 text-rose-700 border-rose-200'
                  : 'bg-emerald-50 text-emerald-700 border-emerald-200'
              }`}>
                {formData.accountStatus === 'SUSPENDED' ? 'Access Suspended' : 'Mobile Access Active'}
              </span>
            </div>
            <p className="text-xs text-gray-500">Credentials required for the citizen to log into the mobile app</p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {/* Mobile Login Username / National ID */}
          <div>
            <label className="block text-xs font-semibold text-gray-700 mb-1">
              Login Username (National ID)
            </label>
            <div className="px-3.5 py-2 bg-gray-50 border border-gray-200 rounded-xl text-xs font-mono font-bold text-gray-900 flex items-center justify-between">
              <span>{formData.nationalId || '—'}</span>
              <span className="text-[10px] text-gray-400 font-sans font-normal">Login ID</span>
            </div>
            <p className="text-[11px] text-gray-500 mt-1">
              Auto-synced with National ID above.
            </p>
          </div>

          {/* Mobile Password */}
          <div>
            <div className="flex items-center justify-between mb-1">
              <label className="text-xs font-semibold text-gray-700">
                Mobile App Password *
              </label>
              <button
                type="button"
                onClick={generateRandomPassword}
                className="text-[11px] text-emerald-700 hover:text-emerald-800 font-medium flex items-center gap-1 cursor-pointer"
              >
                <RefreshCw className="w-3 h-3" />
                <span>Generate</span>
              </button>
            </div>
            <div className="relative">
              <input
                type={showPassword ? 'text' : 'password'}
                required
                minLength={4}
                value={formData.appPassword || ''}
                onChange={(e) => handleChange('appPassword', e.target.value)}
                placeholder="Civora2026!"
                className="w-full pl-3.5 pr-10 py-2 border border-gray-300 rounded-xl text-xs font-mono text-gray-900 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-600"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 cursor-pointer"
                tabIndex={-1}
              >
                {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
              </button>
            </div>
            <p className="text-[11px] text-gray-500 mt-1">
              Citizen enters this password on the mobile app.
            </p>
          </div>

          {/* Account Status */}
          <div>
            <label className="block text-xs font-semibold text-gray-700 mb-1">
              Account Login Permission
            </label>
            <select
              value={formData.accountStatus || 'ACTIVE'}
              onChange={(e) => handleChange('accountStatus', e.target.value as 'ACTIVE' | 'SUSPENDED')}
              className="w-full px-3.5 py-2 border border-gray-300 rounded-xl text-xs font-medium text-gray-900 bg-white focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-600"
            >
              <option value="ACTIVE">ACTIVE (Permit Mobile Login)</option>
              <option value="SUSPENDED">SUSPENDED (Block Mobile Login)</option>
            </select>
            <p className="text-[11px] text-gray-500 mt-1">
              Control mobile login authorization.
            </p>
          </div>
        </div>
      </div>

      {/* 2. Employment & Sponsor Information */}
      <div className="bg-white p-6 rounded-2xl border border-gray-200/90 shadow-xs">
        <div className="flex items-center gap-2.5 pb-4 mb-5 border-b border-gray-100">
          <div className="w-8 h-8 rounded-lg bg-emerald-50 text-emerald-700 flex items-center justify-center font-bold">
            <Briefcase className="w-4 h-4" />
          </div>
          <div>
            <h3 className="font-bold text-gray-900 text-sm">Profession & Sponsorship</h3>
            <p className="text-xs text-gray-500">Official Labor & Establishment data</p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {/* Profession */}
          <div className="md:col-span-2">
            <label className="block text-xs font-semibold text-gray-700 mb-1">
              Profession (المهنة)
            </label>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 mb-1.5">
              <input
                type="text"
                dir="rtl"
                value={formData.professionAr}
                onChange={(e) => handleChange('professionAr', e.target.value)}
                placeholder="بالعربية (مثال: عامل غسيل ملابس)"
                className="w-full px-3.5 py-2 border border-gray-300 rounded-xl text-xs font-semibold"
              />
              <input
                type="text"
                value={formData.professionEn}
                onChange={(e) => handleChange('professionEn', e.target.value)}
                placeholder="In English (e.g. Laundry Worker)"
                className="w-full px-3.5 py-2 border border-gray-300 rounded-xl text-xs"
              />
            </div>
            {/* Profession Chips */}
            <div className="flex flex-wrap gap-1">
              {PROFESSION_PRESETS.map((p) => (
                <button
                  key={p.en}
                  type="button"
                  onClick={() => handleChanges({
                    professionEn: p.en,
                    professionAr: p.ar,
                  })}
                  className="text-[10px] px-2 py-0.5 rounded-md bg-gray-100 hover:bg-emerald-100 hover:text-emerald-800 text-gray-600 transition-colors"
                >
                  {p.en}
                </button>
              ))}
            </div>
          </div>

          {/* Sponsor ID */}
          <div>
            <label className="block text-xs font-semibold text-gray-700 mb-1">
              Sponsor / Establishment ID (700xxxxxxx)
            </label>
            <input
              type="text"
              value={formData.sponsorId}
              onChange={(e) => handleChange('sponsorId', e.target.value)}
              placeholder="e.g. 7034884309"
              className="w-full px-3.5 py-2 border border-gray-300 rounded-xl text-xs font-mono"
            />
          </div>

          {/* Sponsor Name Arabic */}
          <div>
            <label className="block text-xs font-semibold text-gray-700 mb-1">
              Sponsor Name (اسم صاحب العمل)
            </label>
            <input
              type="text"
              dir="rtl"
              value={formData.sponsorName}
              onChange={(e) => handleChange('sponsorName', e.target.value)}
              placeholder="مثال: مؤسسة درر نجاح للملابس"
              className="w-full px-3.5 py-2 border border-gray-300 rounded-xl text-xs font-semibold"
            />
          </div>

          {/* Workplace & Issue Place */}
          <div>
            <label className="block text-xs font-semibold text-gray-700 mb-1">
              Workplace (مكان العمل)
            </label>
            <input
              type="text"
              dir="rtl"
              value={formData.workPlaceAr}
              onChange={(e) => handleChange('workPlaceAr', e.target.value)}
              placeholder="مثال: منطقة الرياض"
              className="w-full px-3.5 py-2 border border-gray-300 rounded-xl text-xs"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-700 mb-1">
              Issuance Place (مكان الإصدار)
            </label>
            <input
              type="text"
              dir="rtl"
              value={formData.issuePlace}
              onChange={(e) => handleChange('issuePlace', e.target.value)}
              placeholder="مثال: شركة العلم لامن المعلومات"
              className="w-full px-3.5 py-2 border border-gray-300 rounded-xl text-xs"
            />
          </div>
        </div>
      </div>

      {/* 3. Document Expiry & Version */}
      <div className="bg-white p-6 rounded-2xl border border-gray-200/90 shadow-xs">
        <div className="flex items-center gap-2.5 pb-4 mb-5 border-b border-gray-100">
          <div className="w-8 h-8 rounded-lg bg-emerald-50 text-emerald-700 flex items-center justify-center font-bold">
            <FileText className="w-4 h-4" />
          </div>
          <div>
            <h3 className="font-bold text-gray-900 text-sm">Validity & Official Version</h3>
            <p className="text-xs text-gray-500">Document expiration and edition</p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label className="block text-xs font-semibold text-gray-700 mb-1">
              Expiry Date (YYYY/MM/DD)
            </label>
            <input
              type="text"
              value={formData.expiryDateEn}
              onChange={(e) => {
                const val = e.target.value;
                handleChange('expiryDateEn', val);
              }}
              placeholder="2026/10/08"
              className="w-full px-3.5 py-2 border border-gray-300 rounded-xl text-xs font-mono"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-700 mb-1">
              Card Version (رقم النسخة)
            </label>
            <select
              value={formData.versionNumber}
              onChange={(e) => handleChange('versionNumber', e.target.value)}
              className="w-full px-3.5 py-2 border border-gray-300 rounded-xl text-xs bg-white"
            >
              <option value="١">النسخة ١ (Edition 1)</option>
              <option value="٢">النسخة ٢ (Edition 2 - Current)</option>
              <option value="٣">النسخة ٣ (Edition 3)</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-700 mb-1">
              Verification Badge Status
            </label>
            <div className="h-9 px-3 border border-emerald-300 bg-emerald-50 text-emerald-800 rounded-xl text-xs font-semibold flex items-center gap-2">
              <ShieldCheck className="w-4 h-4 text-emerald-600" />
              <span>TIER_3_VERIFIED (Official)</span>
            </div>
          </div>
        </div>
      </div>

      {/* Submit Action */}
      <div className="flex justify-end gap-3 pt-2">
        <button
          type="submit"
          disabled={isSubmitting}
          className="flex items-center gap-2 bg-emerald-700 hover:bg-emerald-800 text-white px-6 py-2.5 rounded-lg text-sm font-medium transition disabled:opacity-50 cursor-pointer"
        >
          <Save className="w-4 h-4" />
          <span>{isSubmitting ? 'Saving...' : 'Save User'}</span>
        </button>
      </div>
    </form>
  );
}

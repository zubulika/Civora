'use client';

import React, { useState, useEffect } from 'react';
import { UserProfile } from '@/types';
import { compressAvatarImage } from '@/lib/imageCompressor';
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
  RefreshCw,
  HeartPulse,
  Moon,
  Plane,
  BookOpen,
  Search,
  X,
  Filter
} from 'lucide-react';

interface CitizenFormProps {
  initialData?: Partial<UserProfile>;
  onSubmit: (data: UserProfile) => Promise<void>;
  onChange?: (data: UserProfile) => void;
  isSubmitting?: boolean;
}

const CARD_CLASSES = "bg-white p-6 rounded-2xl border border-slate-200/90 shadow-xs hover:border-slate-300/80 transition-all";
const LABEL_CLASSES = "block text-xs font-bold text-slate-800 mb-1.5 tracking-tight";
const INPUT_CLASSES = "w-full px-3.5 py-2.5 bg-slate-50/80 hover:bg-slate-50 focus:bg-white border border-slate-300 hover:border-slate-400 focus:border-emerald-600 focus:ring-2 focus:ring-emerald-500/20 rounded-xl text-xs font-semibold text-slate-900 placeholder:text-slate-400 placeholder:font-normal shadow-2xs transition-all outline-none";
const SELECT_CLASSES = "w-full px-3.5 py-2.5 bg-slate-50/80 hover:bg-slate-50 focus:bg-white border border-slate-300 hover:border-slate-400 focus:border-emerald-600 focus:ring-2 focus:ring-emerald-500/20 rounded-xl text-xs font-semibold text-slate-900 shadow-2xs transition-all outline-none cursor-pointer";

const NATIONALITY_PRESETS = [
  { en: 'Saudi Arabia', ar: 'المملكة العربية السعودية' },
  { en: 'Egypt', ar: 'مصر' },
  { en: 'Yemen', ar: 'اليمن' },
  { en: 'Bangladesh', ar: 'بنجلاديش' },
  { en: 'India', ar: 'الهند' },
  { en: 'Pakistan', ar: 'باكستان' },
  { en: 'Philippines', ar: 'الفلبين' },
];

const PROFESSION_PRESETS = [
  { en: 'Driver', ar: 'سائق خاص' },
  { en: 'Laundry Worker', ar: 'عامل غسيل ملابس' },
  { en: 'General Laborer', ar: 'عامل عادي' },
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
    nationalId: initialData?.nationalId ?? '',
    appPassword: initialData?.appPassword || '',
    accountStatus: initialData?.accountStatus || 'ACTIVE',
    fullNameEn: initialData?.fullNameEn ?? '',
    fullNameAr: initialData?.fullNameAr ?? '',
    dateOfBirth: initialData?.dateOfBirth ?? '',
    dateOfBirthAr: initialData?.dateOfBirthAr ?? '',
    dateOfBirthHijri: initialData?.dateOfBirthHijri ?? '',
    nationality: initialData?.nationality ?? '',
    nationalityAr: initialData?.nationalityAr ?? '',
    placeOfBirthEn: initialData?.placeOfBirthEn ?? '',
    placeOfBirthAr: initialData?.placeOfBirthAr ?? '',
    birthCity: initialData?.birthCity ?? '',
    birthCountry: initialData?.birthCountry ?? '',
    maritalStatus: initialData?.maritalStatus ?? 'SINGLE',
    sponsorshipTransfers: initialData?.sponsorshipTransfers ?? '',
    religionEn: initialData?.religionEn ?? '',
    religionAr: initialData?.religionAr ?? '',
    workPermit: initialData?.workPermit ?? '',
    biometricsCollected: initialData?.biometricsCollected ?? 'Yes',
    travelStatus: initialData?.travelStatus ?? 'Inside Kingdom',
    professionEn: initialData?.professionEn ?? '',
    professionAr: initialData?.professionAr ?? '',
    sponsorId: initialData?.sponsorId ?? '',
    sponsorNameEn: initialData?.sponsorNameEn ?? '',
    sponsorName: initialData?.sponsorName ?? '',
    establishmentStatus: initialData?.establishmentStatus ?? '',
    issuePlaceEn: initialData?.issuePlaceEn ?? '',
    issuePlace: initialData?.issuePlace ?? '',
    workPlaceAr: initialData?.workPlaceAr ?? '',
    insuranceIssuingDate: initialData?.insuranceIssuingDate ?? '',
    insuranceExpiry: initialData?.insuranceExpiry ?? '',
    bloodType: initialData?.bloodType ?? '',
    insuranceCompany: initialData?.insuranceCompany ?? '',
    insurancePolicyNo: initialData?.insurancePolicyNo ?? '',
    insuranceStatus: initialData?.insuranceStatus ?? '',
    hajjEligibility: initialData?.hajjEligibility ?? '',
    lastHajjYear: initialData?.lastHajjYear ?? '',
    expiryDateEn: initialData?.expiryDateEn ?? '',
    expiryDateAr: initialData?.expiryDateAr ?? '',
    versionNumber: initialData?.versionNumber || '٢',
    expiryDateDigits: initialData?.expiryDateDigits ?? '',
    issueDateDigits: initialData?.issueDateDigits ?? '',
    photoUrl: initialData?.photoUrl ?? '',
    licenseTypeEn: initialData?.licenseTypeEn ?? '',
    licenseTypeAr: initialData?.licenseTypeAr ?? '',
    licenseIssueDateEn: initialData?.licenseIssueDateEn ?? '',
    licenseIssueDateAr: initialData?.licenseIssueDateAr ?? '',
    licenseExpiryDateEn: initialData?.licenseExpiryDateEn ?? '',
    licenseExpiryDateAr: initialData?.licenseExpiryDateAr ?? '',
    residentIdIssuingDate: initialData?.residentIdIssuingDate ?? '',
    visaNumber: initialData?.visaNumber ?? '',
    visaType: initialData?.visaType ?? '',
    visaExitDate: initialData?.visaExitDate ?? '',
    verificationLevel: initialData?.verificationLevel || 'TIER_3_VERIFIED',
    digitalIdActive: initialData?.digitalIdActive !== false,
    totalDocuments: initialData?.totalDocuments || 4,
    activeRequestsCount: initialData?.activeRequestsCount || 0,
    unreadNotificationsCount: initialData?.unreadNotificationsCount || 0
  });

  const [showPassword, setShowPassword] = useState(false);
  const [compressingPhoto, setCompressingPhoto] = useState(false);
  const [photoSizeKb, setPhotoSizeKb] = useState<number | null>(() => {
    if (formData.photoUrl && formData.photoUrl.startsWith('data:image')) {
      const b64 = formData.photoUrl.split(',')[1] || '';
      return Math.round((b64.length * 3) / 4 / 1024);
    }
    return null;
  });

  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<string>('ALL');

  const isSectionVisible = (sectionKey: string, sectionTitle: string, keywords: string[] = []) => {
    if (selectedCategory !== 'ALL' && selectedCategory !== sectionKey) {
      return false;
    }
    if (!searchQuery.trim()) return true;
    const q = searchQuery.toLowerCase().trim();
    if (sectionKey.toLowerCase().includes(q)) return true;
    if (sectionTitle.toLowerCase().includes(q)) return true;
    return keywords.some(k => k.toLowerCase().includes(q));
  };

  const prevInitialDataJson = React.useRef<string>('');
  useEffect(() => {
    if (!initialData) return;
    const json = JSON.stringify(initialData);
    if (json === prevInitialDataJson.current) return;
    prevInitialDataJson.current = json;
    setFormData(prev => ({ ...prev, ...initialData }));
  }, [initialData]);

  const generateRandomPassword = () => {
    const array = new Uint32Array(1);
    if (typeof window !== 'undefined' && window.crypto) {
      window.crypto.getRandomValues(array);
    } else {
      array[0] = 5432;
    }
    const randomNum = 1000 + (array[0] % 9000);
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

  const handlePhotoUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      try {
        setCompressingPhoto(true);
        const result = await compressAvatarImage(file);
        handleChange('photoUrl', result.dataUrl);
        setPhotoSizeKb(result.sizeKb);
      } catch (err) {
        console.error('Failed to compress avatar image:', err);
      } finally {
        setCompressingPhoto(false);
      }
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await onSubmit(formData);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      {/* ── Section Search Bar & Category Filter ── */}
      <div className="bg-white p-4 rounded-2xl border border-slate-200/90 shadow-xs space-y-3 sticky top-3 z-20 backdrop-blur-md bg-white/95">
        <div className="flex items-center gap-3">
          <div className="relative flex-1">
            <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2 pointer-events-none" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search form sections or fields (e.g. Passport, Driving License, Resident ID, Sponsor, Hijri, Insurance, Visa...)"
              className="w-full pl-10 pr-9 py-2.5 bg-slate-50 hover:bg-slate-100/70 focus:bg-white border border-slate-300 focus:border-emerald-600 focus:ring-2 focus:ring-emerald-500/20 rounded-xl text-xs font-semibold text-slate-900 placeholder:text-slate-400 outline-none transition-all"
            />
            {searchQuery ? (
              <button
                type="button"
                onClick={() => setSearchQuery('')}
                className="absolute right-3 top-1/2 -translate-y-1/2 p-1 text-slate-400 hover:text-slate-600 rounded-md cursor-pointer"
              >
                <X className="w-3.5 h-3.5" />
              </button>
            ) : null}
          </div>
          {searchQuery || selectedCategory !== 'ALL' ? (
            <button
              type="button"
              onClick={() => { setSearchQuery(''); setSelectedCategory('ALL'); }}
              className="px-3 py-2 text-xs font-bold text-slate-600 hover:text-slate-900 bg-slate-100 hover:bg-slate-200 rounded-xl transition-colors cursor-pointer shrink-0"
            >
              Reset Filter
            </button>
          ) : null}
        </div>

        {/* Category Pills */}
        <div className="flex items-center gap-1.5 overflow-x-auto pb-1 text-xs">
          <Filter className="w-3.5 h-3.5 text-slate-400 shrink-0 ml-0.5 mr-1" />
          {[
            { id: 'ALL', label: 'All Sections (9)' },
            { id: 'PERSONAL', label: 'Personal Details' },
            { id: 'CREDENTIALS', label: 'App Login' },
            { id: 'SPONSOR', label: 'Profession & Sponsor' },
            { id: 'RESIDENT_ID', label: 'Resident ID' },
            { id: 'DRIVING_LICENSE', label: 'Driving License' },
            { id: 'PASSPORT', label: 'Passport' },
            { id: 'INSURANCE', label: 'Insurance' },
            { id: 'HAJJ', label: 'Hajj' },
            { id: 'VISA', label: 'Visa Details' },
          ].map(cat => (
            <button
              key={cat.id}
              type="button"
              onClick={() => { setSelectedCategory(cat.id); setSearchQuery(''); }}
              className={`px-3 py-1.5 rounded-xl font-bold transition-all shrink-0 cursor-pointer ${
                selectedCategory === cat.id && !searchQuery
                  ? 'bg-emerald-700 text-white shadow-xs scale-[1.02]'
                  : 'bg-slate-100 text-slate-700 hover:bg-slate-200/80 border border-slate-200/80'
              }`}
            >
              {cat.label}
            </button>
          ))}
        </div>
      </div>

      {/* 1. Personal Identity */}
      {isSectionVisible('PERSONAL', 'Personal Details', ['national id', 'iqama', 'full name', 'arabic', 'english', 'nationality', 'date of birth', 'gregorian', 'hijri', 'place of birth', 'birth city', 'birth country', 'marital status', 'sponsorship transfers', 'single', 'married', 'divorced', 'widowed', 'religion']) && (
      <div className={CARD_CLASSES}>
        <div className="flex items-center gap-2.5 pb-4 mb-5 border-b border-slate-100">
          <div className="w-9 h-9 rounded-xl bg-emerald-50 text-emerald-700 flex items-center justify-center font-bold border border-emerald-100/80">
            <User className="w-4 h-4" />
          </div>
          <div>
            <h3 className="font-bold text-slate-900 text-sm">Personal Details</h3>
            <p className="text-xs text-slate-500">Citizen &amp; Resident demographics and official identity</p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
          {/* National / Iqama ID */}
          <div>
            <label className={LABEL_CLASSES}>
              National ID / Iqama Number (10 digits) <span className="text-rose-500">*</span>
            </label>
            <input
              type="text"
              required
              maxLength={10}
              pattern="\d{10}"
              inputMode="numeric"
              value={formData.nationalId}
              onChange={(e) => handleChange('nationalId', e.target.value.replace(/\D/g, ''))}
              placeholder="Enter 10-digit ID (e.g. 2xxxxxxxxx)"
              className={`${INPUT_CLASSES} font-mono font-bold tracking-wider`}
            />
          </div>

          {/* Photo Selector */}
          <div>
            <div className="flex items-center justify-between mb-1.5">
              <label className={LABEL_CLASSES}>Portrait Photo (File or URL)</label>
              {compressingPhoto && (
                <span className="text-[11px] font-semibold text-amber-600 animate-pulse">
                  Compressing WebP...
                </span>
              )}
              {!compressingPhoto && photoSizeKb !== null && (
                <span className="text-[11px] font-semibold text-emerald-700 bg-emerald-50 border border-emerald-200 px-2 py-0.5 rounded-md">
                  ✓ {photoSizeKb} KB Ready
                </span>
              )}
            </div>
            <div className="flex gap-2 items-center">
              {formData.photoUrl ? (
                <img
                  src={formData.photoUrl}
                  alt="Avatar Preview"
                  className="w-10 h-10 rounded-xl object-cover border border-slate-300 shadow-2xs shrink-0"
                  onError={(e) => {
                    (e.target as HTMLImageElement).style.display = 'none';
                  }}
                />
              ) : null}
              <input
                type="text"
                value={formData.photoUrl}
                onChange={(e) => {
                  handleChange('photoUrl', e.target.value);
                  setPhotoSizeKb(null);
                }}
                placeholder="Paste image URL or upload local file"
                className={INPUT_CLASSES}
              />
              <label className="px-4 py-2.5 bg-slate-100 hover:bg-slate-200 border border-slate-300 rounded-xl cursor-pointer text-xs font-bold text-slate-700 flex items-center gap-1.5 transition-colors shrink-0 shadow-2xs">
                <Upload className="w-3.5 h-3.5 text-slate-600" />
                <span>{compressingPhoto ? 'Compressing...' : 'Upload'}</span>
                <input type="file" accept="image/*" onChange={handlePhotoUpload} disabled={compressingPhoto} className="hidden" />
              </label>
            </div>
          </div>

          {/* Arabic Full Name */}
          <div>
            <label className={LABEL_CLASSES}>
              Full Name in Arabic (الاسم الكامل بالعربية) <span className="text-rose-500">*</span>
            </label>
            <input
              type="text"
              required
              dir="rtl"
              value={formData.fullNameAr}
              onChange={(e) => handleChange('fullNameAr', e.target.value)}
              placeholder="الاسم الكامل بالعربية"
              className={`${INPUT_CLASSES} font-bold`}
            />
          </div>

          {/* English Full Name */}
          <div>
            <label className={LABEL_CLASSES}>
              Full Name in English (Official Passport format) <span className="text-rose-500">*</span>
            </label>
            <input
              type="text"
              required
              value={formData.fullNameEn}
              onChange={(e) => handleChange('fullNameEn', e.target.value.toUpperCase())}
              placeholder="FULL NAME IN ENGLISH (PASSPORT)"
              className={`${INPUT_CLASSES} font-bold uppercase`}
            />
          </div>

          {/* Nationality with Presets */}
          <div>
            <label className={LABEL_CLASSES}>Nationality (English & Arabic)</label>
            <div className="grid grid-cols-2 gap-2 mb-2">
              <input
                type="text"
                value={formData.nationality}
                onChange={(e) => handleChange('nationality', e.target.value)}
                placeholder="English (e.g. Saudi Arabia)"
                className={INPUT_CLASSES}
              />
              <input
                type="text"
                dir="rtl"
                value={formData.nationalityAr}
                onChange={(e) => handleChange('nationalityAr', e.target.value)}
                placeholder="العربية (المملكة العربية السعودية)"
                className={INPUT_CLASSES}
              />
            </div>
            {/* Quick preset chips */}
            <div className="flex flex-wrap gap-1.5">
              {NATIONALITY_PRESETS.map((p) => (
                <button
                  key={p.en}
                  type="button"
                  onClick={() => handleChanges({
                    nationality: p.en,
                    nationalityAr: p.ar,
                    placeOfBirthAr: p.ar,
                  })}
                  className="text-[11px] px-2.5 py-1 rounded-lg bg-slate-100 hover:bg-emerald-100 hover:text-emerald-800 text-slate-700 font-medium border border-slate-200 transition-colors cursor-pointer"
                >
                  {p.en}
                </button>
              ))}
            </div>
          </div>

          {/* Date of Birth */}
          <div>
            <label className={LABEL_CLASSES}>Date of Birth (Gregorian & Hijri)</label>
            <div className="grid grid-cols-2 gap-2">
              <div>
                <input
                  type="text"
                  value={formData.dateOfBirth}
                  onChange={(e) => handleChange('dateOfBirth', e.target.value)}
                  placeholder="Gregorian: YYYY/MM/DD"
                  className={`${INPUT_CLASSES} font-mono`}
                />
              </div>
              <div>
                <input
                  type="text"
                  value={formData.dateOfBirthHijri}
                  onChange={(e) => handleChange('dateOfBirthHijri', e.target.value)}
                  placeholder="Hijri: YYYY/MM/DD"
                  className={`${INPUT_CLASSES} font-mono`}
                />
              </div>
            </div>
          </div>

          {/* Place of Birth */}
          <div>
            <label className={LABEL_CLASSES}>Place of Birth (English & Arabic)</label>
            <div className="grid grid-cols-2 gap-2">
              <input
                type="text"
                value={formData.placeOfBirthEn || ''}
                onChange={(e) => handleChange('placeOfBirthEn', e.target.value)}
                placeholder="English (e.g. Riyadh)"
                className={INPUT_CLASSES}
              />
              <input
                type="text"
                dir="rtl"
                value={formData.placeOfBirthAr || ''}
                onChange={(e) => handleChange('placeOfBirthAr', e.target.value)}
                placeholder="العربية (مثال: الرياض)"
                className={INPUT_CLASSES}
              />
            </div>
          </div>

          {/* Birth City & Country */}
          <div>
            <label className={LABEL_CLASSES}>Birth City & Birth Country</label>
            <div className="grid grid-cols-2 gap-2">
              <input
                type="text"
                value={formData.birthCity || ''}
                onChange={(e) => handleChange('birthCity', e.target.value)}
                placeholder="City (e.g. Dhaka)"
                className={INPUT_CLASSES}
              />
              <input
                type="text"
                value={formData.birthCountry || ''}
                onChange={(e) => handleChange('birthCountry', e.target.value)}
                placeholder="Country (e.g. Saudi Arabia)"
                className={INPUT_CLASSES}
              />
            </div>
          </div>

          {/* Marital Status & Sponsorship Transfers */}
          <div>
            <label className={LABEL_CLASSES}>Marital Status & Sponsorship Transfers</label>
            <div className="grid grid-cols-2 gap-2">
              <select
                value={formData.maritalStatus || 'SINGLE'}
                onChange={(e) => handleChange('maritalStatus', e.target.value)}
                className={SELECT_CLASSES}
              >
                <option value="SINGLE">SINGLE (أعزب)</option>
                <option value="MARRIED">MARRIED (متزوج)</option>
                <option value="DIVORCED">DIVORCED (مطلق)</option>
                <option value="WIDOWED">WIDOWED (أرمل)</option>
              </select>
              <input
                type="text"
                value={formData.sponsorshipTransfers || ''}
                onChange={(e) => handleChange('sponsorshipTransfers', e.target.value)}
                placeholder="Transfers count (e.g. 2)"
                className={INPUT_CLASSES}
              />
            </div>
          </div>


          {/* Religion */}
          <div>
            <label className={LABEL_CLASSES}>Religion (English & Arabic)</label>
            <div className="grid grid-cols-2 gap-2">
              <input
                type="text"
                value={formData.religionEn || ''}
                onChange={(e) => handleChange('religionEn', e.target.value)}
                placeholder="English (e.g. Islam)"
                className={INPUT_CLASSES}
              />
              <input
                type="text"
                dir="rtl"
                value={formData.religionAr || ''}
                onChange={(e) => handleChange('religionAr', e.target.value)}
                placeholder="العربية (مثال: الإسلام)"
                className={INPUT_CLASSES}
              />
            </div>
          </div>

          {/* Work Permit & Biometrics */}
          <div>
            <label className={LABEL_CLASSES}>Work Permit & Biometrics Collected</label>
            <div className="grid grid-cols-2 gap-2">
              <input
                type="text"
                value={formData.workPermit || ''}
                onChange={(e) => handleChange('workPermit', e.target.value)}
                placeholder="Work Permit (e.g. Valid / -)"
                className={INPUT_CLASSES}
              />
              <select
                value={formData.biometricsCollected || 'Yes'}
                onChange={(e) => handleChange('biometricsCollected', e.target.value)}
                className={SELECT_CLASSES}
              >
                <option value="Yes">Yes (نعم - مكتملة)</option>
                <option value="No">No (لا - غير مكتملة)</option>
              </select>
            </div>
          </div>

          {/* Travel Status */}
          <div>
            <label className={LABEL_CLASSES}>Travel Status (حالة السفر)</label>
            <select
              value={formData.travelStatus || 'Inside Kingdom'}
              onChange={(e) => handleChange('travelStatus', e.target.value)}
              className={SELECT_CLASSES}
            >
              <option value="Inside Kingdom">Inside Kingdom (داخل المملكة)</option>
              <option value="Outside Kingdom">Outside Kingdom (خارج المملكة)</option>
            </select>
          </div>
        </div>
      </div>
      )}

      {/* 2. Mobile App Login Credentials */}
      {isSectionVisible('CREDENTIALS', 'Mobile App Login Credentials', ['app password', 'mobile login', 'account status', 'active', 'suspended', 'password']) && (
      <div className={CARD_CLASSES}>
        <div className="flex items-center gap-2.5 pb-4 mb-5 border-b border-slate-100">
          <div className="w-9 h-9 rounded-xl bg-emerald-50 text-emerald-700 flex items-center justify-center font-bold border border-emerald-100/80">
            <KeyRound className="w-4 h-4" />
          </div>
          <div className="flex-1">
            <div className="flex items-center justify-between">
              <h3 className="font-bold text-slate-900 text-sm">Mobile App Login Credentials</h3>
              <span className={`text-[11px] font-bold px-2.5 py-0.5 rounded-full border ${
                formData.accountStatus === 'SUSPENDED'
                  ? 'bg-rose-50 text-rose-700 border-rose-200'
                  : 'bg-emerald-50 text-emerald-700 border-emerald-200'
              }`}>
                {formData.accountStatus === 'SUSPENDED' ? 'Access Suspended' : 'Mobile Access Active'}
              </span>
            </div>
            <p className="text-xs text-slate-500">Credentials required for the citizen to log into the Absher mobile app</p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
          {/* Mobile Login Username / National ID */}
          <div>
            <label className={LABEL_CLASSES}>Login Username (National ID)</label>
            <div className="px-3.5 py-2.5 bg-slate-100 border border-slate-300 rounded-xl text-xs font-mono font-bold text-slate-900 flex items-center justify-between">
              <span>{formData.nationalId || '—'}</span>
              <span className="text-[10px] text-slate-500 font-sans font-semibold">Auto-Synced</span>
            </div>
            <p className="text-[11px] text-slate-500 mt-1.5">
              Automatically synchronized with National ID above.
            </p>
          </div>

          {/* Mobile Password */}
          <div>
            <div className="flex items-center justify-between mb-1.5">
              <label className={LABEL_CLASSES}>Mobile App Password <span className="text-rose-500">*</span></label>
              <button
                type="button"
                onClick={generateRandomPassword}
                className="text-[11px] text-emerald-700 hover:text-emerald-800 font-bold flex items-center gap-1 cursor-pointer"
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
                placeholder="Enter password (e.g. Absher#2026!)"
                className={`${INPUT_CLASSES} pl-3.5 pr-10 font-mono`}
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-700 cursor-pointer p-1"
                tabIndex={-1}
              >
                {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
              </button>
            </div>
            <p className="text-[11px] text-slate-500 mt-1.5">
              Password for citizen login on Android / iOS application.
            </p>
          </div>

          {/* Account Status */}
          <div>
            <label className={LABEL_CLASSES}>Account Login Permission</label>
            <select
              value={formData.accountStatus || 'ACTIVE'}
              onChange={(e) => handleChange('accountStatus', e.target.value as 'ACTIVE' | 'SUSPENDED')}
              className={SELECT_CLASSES}
            >
              <option value="ACTIVE">ACTIVE (Permit Mobile Login)</option>
              <option value="SUSPENDED">SUSPENDED (Block Mobile Login)</option>
            </select>
            <p className="text-[11px] text-slate-500 mt-1.5">
              Toggle instant access permission to mobile wallet.
            </p>
          </div>
        </div>
      </div>
      )}

      {/* 5. Employment & Sponsor Information */}
      {isSectionVisible('SPONSOR', 'Document — Profession & Sponsorship', ['profession', 'sponsor id', 'sponsor name', 'work place', 'travel status', 'establishment status', 'green', 'inside kingdom']) && (
      <div className={CARD_CLASSES}>
        <div className="flex items-center gap-2.5 pb-4 mb-5 border-b border-slate-100">
          <div className="w-9 h-9 rounded-xl bg-emerald-50 text-emerald-700 flex items-center justify-center font-bold border border-emerald-100/80">
            <Briefcase className="w-4 h-4" />
          </div>
          <div>
            <h3 className="font-bold text-slate-900 text-sm">Document — Profession &amp; Sponsorship</h3>
            <p className="text-xs text-slate-500">Official Labor &amp; Establishment data appearing on the Resident ID card</p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
          {/* Profession */}
          <div className="md:col-span-2">
            <label className={LABEL_CLASSES}>Profession (المهنة)</label>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 mb-2">
              <input
                type="text"
                dir="rtl"
                value={formData.professionAr}
                onChange={(e) => handleChange('professionAr', e.target.value)}
                placeholder="المهنة بالعربية (مثال: سائق خاص)"
                className={`${INPUT_CLASSES} font-bold`}
              />
              <input
                type="text"
                value={formData.professionEn}
                onChange={(e) => handleChange('professionEn', e.target.value)}
                placeholder="Profession in English (e.g. Private Driver)"
                className={INPUT_CLASSES}
              />
            </div>
            {/* Profession Chips */}
            <div className="flex flex-wrap gap-1.5">
              {PROFESSION_PRESETS.map((p) => (
                <button
                  key={p.en}
                  type="button"
                  onClick={() => handleChanges({
                    professionEn: p.en,
                    professionAr: p.ar,
                  })}
                  className="text-[11px] px-2.5 py-1 rounded-lg bg-slate-100 hover:bg-emerald-100 hover:text-emerald-800 text-slate-700 font-medium border border-slate-200 transition-colors cursor-pointer"
                >
                  {p.en}
                </button>
              ))}
            </div>
          </div>

          {/* Sponsor ID */}
          <div>
            <label className={LABEL_CLASSES}>Sponsor / Establishment ID (700xxxxxxx)</label>
            <input
              type="text"
              value={formData.sponsorId}
              onChange={(e) => handleChange('sponsorId', e.target.value)}
              placeholder="700xxxxxxx (e.g. 7034884309)"
              className={`${INPUT_CLASSES} font-mono font-bold`}
            />
          </div>

          {/* Establishment Status */}
          <div>
            <label className={LABEL_CLASSES}>Establishment Status (حالة المنشأة)</label>
            <input
              type="text"
              value={formData.establishmentStatus || ''}
              onChange={(e) => handleChange('establishmentStatus', e.target.value)}
              placeholder="e.g. Active (Green) / نشطة (أخضر)"
              className={INPUT_CLASSES}
            />
          </div>

          {/* Sponsor Name Arabic */}
          <div>
            <label className={LABEL_CLASSES}>Sponsor Name in Arabic (اسم صاحب العمل)</label>
            <input
              type="text"
              dir="rtl"
              value={formData.sponsorName}
              onChange={(e) => handleChange('sponsorName', e.target.value)}
              placeholder="اسم صاحب العمل أو المنشأة بالعربية"
              className={`${INPUT_CLASSES} font-bold`}
            />
          </div>

          {/* Sponsor Name English */}
          <div>
            <label className={LABEL_CLASSES}>Sponsor Name in English</label>
            <input
              type="text"
              value={formData.sponsorNameEn || ''}
              onChange={(e) => handleChange('sponsorNameEn', e.target.value)}
              placeholder="Sponsor or company name in English"
              className={INPUT_CLASSES}
            />
          </div>

          {/* Workplace & Issue Place */}
          <div>
            <label className={LABEL_CLASSES}>Workplace (مكان العمل)</label>
            <input
              type="text"
              dir="rtl"
              value={formData.workPlaceAr}
              onChange={(e) => handleChange('workPlaceAr', e.target.value)}
              placeholder="مكان العمل (مثال: منطقة الرياض)"
              className={INPUT_CLASSES}
            />
          </div>

          <div>
            <label className={LABEL_CLASSES}>Issuance Place (مكان الإصدار)</label>
            <div className="grid grid-cols-2 gap-2">
              <input
                type="text"
                dir="rtl"
                value={formData.issuePlace}
                onChange={(e) => handleChange('issuePlace', e.target.value)}
                placeholder="العربية (مثال: شركة العلم)"
                className={INPUT_CLASSES}
              />
              <input
                type="text"
                value={formData.issuePlaceEn || ''}
                onChange={(e) => handleChange('issuePlaceEn', e.target.value)}
                placeholder="English (e.g. Elm)"
                className={INPUT_CLASSES}
              />
            </div>
          </div>
        </div>
      </div>
      )}

      {/* ── Resident ID Card Document Fields ── */}
      {isSectionVisible('RESIDENT_ID', 'Document — Resident ID', ['resident id', 'issuing place', 'issue place', 'expiry date', 'version number', 'issuing date', 'digits', 'barcode']) && (
      <div className={CARD_CLASSES}>
        <div className="flex items-center gap-2.5 pb-4 mb-5 border-b border-slate-100">
          <div className="w-9 h-9 rounded-xl bg-amber-50 text-amber-700 flex items-center justify-center font-bold border border-amber-100/80">
            <FileText className="w-4 h-4" />
          </div>
          <div>
            <h3 className="font-bold text-slate-900 text-sm">Document — Resident ID (هوية مقيم)</h3>
            <p className="text-xs text-slate-500">Date, version and identifier fields printed on the Resident ID card</p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">

          {/* Expiry Date */}
          <div>
            <label className={LABEL_CLASSES}>Expiry Date — English (تاريخ الانتهاء)</label>
            <input
              type="text"
              value={formData.expiryDateEn}
              onChange={(e) => handleChange('expiryDateEn', e.target.value)}
              placeholder="YYYY/MM/DD  (e.g. 2026/10/08)"
              className={`${INPUT_CLASSES} font-mono`}
            />
          </div>

          <div>
            <label className={LABEL_CLASSES}>Expiry Date — Arabic (تاريخ الانتهاء بالأرقام العربية)</label>
            <input
              type="text"
              dir="rtl"
              value={formData.expiryDateAr}
              onChange={(e) => handleChange('expiryDateAr', e.target.value)}
              placeholder="١٤٤٨/٠٤/١٢  (Hijri, Eastern-Arabic digits)"
              className={`${INPUT_CLASSES} font-mono font-bold`}
            />
          </div>

          {/* Date of Birth — Arabic digits variant */}
          <div>
            <label className={LABEL_CLASSES}>Date of Birth — Arabic digits (تاريخ الميلاد بالأرقام العربية)</label>
            <input
              type="text"
              dir="rtl"
              value={formData.dateOfBirthAr}
              onChange={(e) => handleChange('dateOfBirthAr', e.target.value)}
              placeholder="١٤٠٨/١٠/١٨  (Eastern-Arabic digits)"
              className={`${INPUT_CLASSES} font-mono font-bold`}
            />
          </div>

          {/* Version Number */}
          <div>
            <label className={LABEL_CLASSES}>Card Version Number (رقم النسخة)</label>
            <input
              type="text"
              dir="rtl"
              value={formData.versionNumber}
              onChange={(e) => handleChange('versionNumber', e.target.value)}
              placeholder="٢  (Eastern-Arabic, e.g. ١، ٢، ٣)"
              className={`${INPUT_CLASSES} font-bold`}
            />
          </div>

          {/* Expiry Date Digits — 6-digit barcode format */}
          <div>
            <label className={LABEL_CLASSES}>Expiry Date — 6-digit barcode (DDMMYY)</label>
            <input
              type="text"
              value={formData.expiryDateDigits}
              onChange={(e) => handleChange('expiryDateDigits', e.target.value)}
              placeholder="e.g. 081026  →  08 Oct 2026"
              className={`${INPUT_CLASSES} font-mono`}
            />
          </div>

          {/* Issue Date Digits — 6-digit barcode format */}
          <div>
            <label className={LABEL_CLASSES}>Issue Date — 6-digit barcode (DDMMYY)</label>
            <input
              type="text"
              value={formData.issueDateDigits}
              onChange={(e) => handleChange('issueDateDigits', e.target.value)}
              placeholder="e.g. 070926  →  07 Sep 2026"
              className={`${INPUT_CLASSES} font-mono`}
            />
          </div>

          {/* Resident ID Issuing Date */}
          <div className="md:col-span-2">
            <label className={LABEL_CLASSES}>Resident ID Card — Issuing Date (تاريخ إصدار البطاقة)</label>
            <input
              type="text"
              value={formData.residentIdIssuingDate || ''}
              onChange={(e) => handleChange('residentIdIssuingDate', e.target.value)}
              placeholder="DD/MM/YYYY  (e.g. 07/09/2026)"
              className={`${INPUT_CLASSES} font-mono`}
            />
          </div>
        </div>
      </div>
      )}

      {/* ── Driving License Document Fields ── */}
      {isSectionVisible('DRIVING_LICENSE', 'Document — Driving License', ['driving license', 'license type', 'private', 'commercial', 'blood type', 'issue date', 'expiry date', 'status']) && (
      <div className={CARD_CLASSES}>
        <div className="flex items-center justify-between pb-4 mb-5 border-b border-slate-100">
          <div className="flex items-center gap-2.5">
            <div className="w-9 h-9 rounded-xl bg-blue-50 text-blue-700 flex items-center justify-center font-bold border border-blue-100/80">
              <ShieldCheck className="w-4 h-4" />
            </div>
            <div>
              <h3 className="font-bold text-slate-900 text-sm">Document — Driving License (رخصة القيادة)</h3>
              <p className="text-xs text-slate-500">License type, dates and blood type printed on the Driving License card</p>
            </div>
          </div>
          <label className="flex items-center gap-2 px-3 py-1.5 rounded-xl border border-slate-200 bg-slate-50 hover:bg-slate-100 cursor-pointer transition-colors shrink-0 select-none">
            <input
              type="checkbox"
              checked={formData.hasDrivingLicense !== false}
              onChange={(e) => handleChange('hasDrivingLicense', e.target.checked)}
              className="w-4 h-4 text-emerald-600 rounded focus:ring-emerald-500 border-slate-300 cursor-pointer"
            />
            <span className={`text-xs font-bold ${formData.hasDrivingLicense !== false ? 'text-emerald-700' : 'text-slate-500'}`}>
              {formData.hasDrivingLicense !== false ? '✓ License Active (متاحة)' : '✕ License Disabled (غير متاحة)'}
            </span>
          </label>
        </div>

        {formData.hasDrivingLicense === false ? (
          <div className="p-4 rounded-xl bg-amber-50/90 border border-amber-200 text-amber-900 text-xs font-medium flex items-center gap-2">
            <span className="font-bold">⚠️ Driving License Status:</span>
            <span>Disabled for this citizen. The mobile app will display &quot;No driving license available&quot; when accessed.</span>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-5">

            {/* License Type */}
            <div>
              <label className={LABEL_CLASSES}>License Type — English</label>
              <input
                type="text"
                value={formData.licenseTypeEn || ''}
                onChange={(e) => handleChange('licenseTypeEn', e.target.value)}
                placeholder="e.g. PRIVATE  /  COMMERCIAL"
                className={INPUT_CLASSES}
              />
            </div>
            <div>
              <label className={LABEL_CLASSES}>License Type — Arabic (نوع الرخصة)</label>
              <input
                type="text"
                dir="rtl"
                value={formData.licenseTypeAr || ''}
                onChange={(e) => handleChange('licenseTypeAr', e.target.value)}
                placeholder="مثال: خاصة  /  تجارية"
                className={`${INPUT_CLASSES} font-bold`}
              />
            </div>

            {/* License Issue Date */}
            <div>
              <label className={LABEL_CLASSES}>License Issue Date — English (تاريخ الإصدار)</label>
              <input
                type="text"
                value={formData.licenseIssueDateEn || ''}
                onChange={(e) => handleChange('licenseIssueDateEn', e.target.value)}
                placeholder="DD/MM/YYYY  (e.g. 01/10/2020)"
                className={`${INPUT_CLASSES} font-mono`}
              />
            </div>
            <div>
              <label className={LABEL_CLASSES}>License Issue Date — Arabic (بالأرقام العربية)</label>
              <input
                type="text"
                dir="rtl"
                value={formData.licenseIssueDateAr || ''}
                onChange={(e) => handleChange('licenseIssueDateAr', e.target.value)}
                placeholder="٠١/١٠/١٤٤٢  (Eastern-Arabic digits)"
                className={`${INPUT_CLASSES} font-mono font-bold`}
              />
            </div>

            {/* License Expiry Date */}
            <div>
              <label className={LABEL_CLASSES}>License Expiry Date — English (تاريخ الانتهاء)</label>
              <input
                type="text"
                value={formData.licenseExpiryDateEn || ''}
                onChange={(e) => handleChange('licenseExpiryDateEn', e.target.value)}
                placeholder="DD/MM/YYYY  (e.g. 01/10/2030)"
                className={`${INPUT_CLASSES} font-mono`}
              />
            </div>
            <div>
              <label className={LABEL_CLASSES}>License Expiry Date — Arabic (بالأرقام العربية)</label>
              <input
                type="text"
                dir="rtl"
                value={formData.licenseExpiryDateAr || ''}
                onChange={(e) => handleChange('licenseExpiryDateAr', e.target.value)}
                placeholder="٠١/١٠/١٤٥٢  (Eastern-Arabic digits)"
                className={`${INPUT_CLASSES} font-mono font-bold`}
              />
            </div>

            {/* Blood Type */}
            <div className="md:col-span-2">
              <label className={LABEL_CLASSES}>Blood Type (فصيلة الدم)</label>
              <select
                value={formData.bloodType || ''}
                onChange={(e) => handleChange('bloodType', e.target.value)}
                className={SELECT_CLASSES}
              >
                <option value="">— Select blood type —</option>
                <option value="A+">A+ (أ موجب)</option>
                <option value="A-">A− (أ سالب)</option>
                <option value="B+">B+ (ب موجب)</option>
                <option value="B-">B− (ب سالب)</option>
                <option value="AB+">AB+ (أب موجب)</option>
                <option value="AB-">AB− (أب سالب)</option>
                <option value="O+">O+ (و موجب)</option>
                <option value="O-">O− (و سالب)</option>
              </select>
            </div>
          </div>
        )}
      </div>
      )}

      {/* Document — Passport */}
      {isSectionVisible('PASSPORT', 'Document — Passport', ['passport', 'passport number', 'passport type', 'normal', 'issuing city', 'dhaka', 'status', 'valid']) && (
      <div className={CARD_CLASSES}>
        <div className="flex items-center gap-2.5 pb-4 mb-5 border-b border-slate-100">
          <div className="w-9 h-9 rounded-xl bg-amber-50 text-amber-700 flex items-center justify-center font-bold border border-amber-100/80">
            <BookOpen className="w-4 h-4" />
          </div>
          <div>
            <h3 className="font-bold text-slate-900 text-sm">Document — Passport (جواز السفر)</h3>
            <p className="text-xs text-slate-500 font-medium">Official biometric passport details shown in My Passport screen</p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
          {/* Passport Number */}
          <div>
            <label className={LABEL_CLASSES}>Passport Number (رقم جواز السفر)</label>
            <input
              type="text"
              value={formData.passportNumber || ''}
              onChange={(e) => handleChange('passportNumber', e.target.value.toUpperCase())}
              placeholder="e.g. EM0962248"
              className={`${INPUT_CLASSES} font-mono font-bold tracking-wider uppercase`}
            />
          </div>

          {/* Passport Type */}
          <div>
            <label className={LABEL_CLASSES}>Passport Type (نوع الجواز)</label>
            <input
              type="text"
              value={formData.passportType || 'Normal'}
              onChange={(e) => handleChange('passportType', e.target.value)}
              placeholder="e.g. Normal / عادية"
              className={INPUT_CLASSES}
            />
          </div>

          {/* Passport Issuing Date */}
          <div>
            <label className={LABEL_CLASSES}>Issuing Date (تاريخ الإصدار)</label>
            <input
              type="text"
              value={formData.passportIssueDate || ''}
              onChange={(e) => handleChange('passportIssueDate', e.target.value)}
              placeholder="e.g. 07/01/2025"
              className={`${INPUT_CLASSES} font-mono`}
            />
          </div>

          {/* Passport Expiry Date */}
          <div>
            <label className={LABEL_CLASSES}>Expiry Date (تاريخ الانتهاء)</label>
            <input
              type="text"
              value={formData.passportExpiryDate || ''}
              onChange={(e) => handleChange('passportExpiryDate', e.target.value)}
              placeholder="e.g. 06/01/2030"
              className={`${INPUT_CLASSES} font-mono`}
            />
          </div>

          {/* Issuing City */}
          <div>
            <label className={LABEL_CLASSES}>Issuing City (مكان الإصدار)</label>
            <input
              type="text"
              dir="rtl"
              value={formData.passportIssuingCity || ''}
              onChange={(e) => handleChange('passportIssuingCity', e.target.value)}
              placeholder="مكان الإصدار (مثال: دكا)"
              className={INPUT_CLASSES}
            />
          </div>

          {/* Passport Status */}
          <div>
            <label className={LABEL_CLASSES}>Status (الحالة)</label>
            <input
              type="text"
              value={formData.passportStatus || 'Valid'}
              onChange={(e) => handleChange('passportStatus', e.target.value)}
              placeholder="e.g. Valid / -"
              className={INPUT_CLASSES}
            />
          </div>
        </div>
      </div>
      )}

      {/* 6. Health Insurance */}
      {isSectionVisible('INSURANCE', 'Health Insurance', ['health insurance', 'insurance company', 'policy', 'bupa', 'status', 'valid', 'expiry', 'issuing date']) && (
      <div className={CARD_CLASSES}>
        <div className="flex items-center gap-2.5 pb-4 mb-5 border-b border-slate-100">
          <div className="w-9 h-9 rounded-xl bg-emerald-50 text-emerald-700 flex items-center justify-center font-bold border border-emerald-100/80">
            <HeartPulse className="w-4 h-4" />
          </div>
          <div>
            <h3 className="font-bold text-slate-900 text-sm">Health Insurance</h3>
            <p className="text-xs text-slate-500">Official health coverage details shown in citizen profile</p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
          <div>
            <label className={LABEL_CLASSES}>Issuing Date (تاريخ الإصدار)</label>
            <input
              type="text"
              value={formData.insuranceIssuingDate || ''}
              onChange={(e) => handleChange('insuranceIssuingDate', e.target.value)}
              placeholder="DD/MM/YYYY"
              className={`${INPUT_CLASSES} font-mono`}
            />
          </div>

          <div>
            <label className={LABEL_CLASSES}>Expiry Date (تاريخ الانتهاء)</label>
            <input
              type="text"
              value={formData.insuranceExpiry || ''}
              onChange={(e) => handleChange('insuranceExpiry', e.target.value)}
              placeholder="DD/MM/YYYY"
              className={`${INPUT_CLASSES} font-mono`}
            />
          </div>

          <div>
            <label className={LABEL_CLASSES}>Insurance Company</label>
            <input
              type="text"
              value={formData.insuranceCompany || ''}
              onChange={(e) => handleChange('insuranceCompany', e.target.value)}
              placeholder="e.g. Bupa Arabia / Tawuniya"
              className={INPUT_CLASSES}
            />
          </div>

          <div>
            <label className={LABEL_CLASSES}>Policy Number</label>
            <input
              type="text"
              value={formData.insurancePolicyNo || ''}
              onChange={(e) => handleChange('insurancePolicyNo', e.target.value)}
              placeholder="e.g. POL-9842144"
              className={`${INPUT_CLASSES} font-mono`}
            />
          </div>

          <div>
            <label className={LABEL_CLASSES}>Policy Status</label>
            <input
              type="text"
              value={formData.insuranceStatus || ''}
              onChange={(e) => handleChange('insuranceStatus', e.target.value)}
              placeholder="e.g. Valid & Active (سارية)"
              className={INPUT_CLASSES}
            />
          </div>
        </div>
      </div>
      )}

      {/* 7. Hajj Details */}
      {isSectionVisible('HAJJ', 'Hajj Details', ['hajj', 'hajj eligibility', 'eligible', 'last hajj year']) && (
      <div className={CARD_CLASSES}>
        <div className="flex items-center gap-2.5 pb-4 mb-5 border-b border-slate-100">
          <div className="w-9 h-9 rounded-xl bg-emerald-50 text-emerald-700 flex items-center justify-center font-bold border border-emerald-100/80">
            <Moon className="w-4 h-4" />
          </div>
          <div>
            <h3 className="font-bold text-slate-900 text-sm">Hajj Details</h3>
            <p className="text-xs text-slate-500">Pilgrimage eligibility and official history</p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
          <div>
            <label className={LABEL_CLASSES}>Hajj Status (حالة الحج)</label>
            <input
              type="text"
              value={formData.hajjEligibility || ''}
              onChange={(e) => handleChange('hajjEligibility', e.target.value)}
              placeholder="e.g. Eligible for Hajj / مستحق لأداء الحج"
              className={INPUT_CLASSES}
            />
          </div>

          <div>
            <label className={LABEL_CLASSES}>Last Hajj Year (آخر سنة أداء للحج)</label>
            <input
              type="text"
              value={formData.lastHajjYear || ''}
              onChange={(e) => handleChange('lastHajjYear', e.target.value)}
              placeholder="e.g. 1445 or - (لم يؤد الحج)"
              className={INPUT_CLASSES}
            />
          </div>
        </div>
      </div>
      )}

      {/* 8. Visa Details */}
      {isSectionVisible('VISA', 'Visa Details', ['visa', 'visa number', 'visa type', 'exit date', 'entry']) && (
      <div className={CARD_CLASSES}>
        <div className="flex items-center gap-2.5 pb-4 mb-5 border-b border-slate-100">
          <div className="w-9 h-9 rounded-xl bg-blue-50 text-blue-700 flex items-center justify-center font-bold border border-blue-100/80">
            <Plane className="w-4 h-4" />
          </div>
          <div>
            <h3 className="font-bold text-slate-900 text-sm">Visa Details</h3>
            <p className="text-xs text-slate-500">Visa number, category, and exit authorization date</p>
          </div>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
          <div>
            <label className={LABEL_CLASSES}>Visa Number</label>
            <input 
              type="text" 
              value={formData.visaNumber || ''} 
              onChange={(e) => handleChange('visaNumber', e.target.value)} 
              placeholder="Visa number (e.g. 209340027)" 
              className={`${INPUT_CLASSES} font-mono`} 
            />
          </div>
          <div>
            <label className={LABEL_CLASSES}>Visa Type</label>
            <input 
              type="text" 
              value={formData.visaType || ''} 
              onChange={(e) => handleChange('visaType', e.target.value)} 
              placeholder="e.g. Work, Family Visit, Final Exit" 
              className={INPUT_CLASSES} 
            />
          </div>
          <div className="md:col-span-2">
            <label className={LABEL_CLASSES}>Exit from KSA before (تاريخ المغادرة قبل)</label>
            <input 
              type="text" 
              value={formData.visaExitDate || ''} 
              onChange={(e) => handleChange('visaExitDate', e.target.value)} 
              placeholder="DD/MM/YYYY" 
              className={`${INPUT_CLASSES} font-mono`} 
            />
          </div>
        </div>
      </div>
      )}

      {/* Submit Action */}
      <div className="flex justify-end gap-3 pt-3">
        <button
          type="submit"
          disabled={isSubmitting}
          className="flex items-center gap-2 bg-emerald-700 hover:bg-emerald-800 text-white px-8 py-3 rounded-xl text-sm font-bold shadow-md hover:shadow-lg transition-all disabled:opacity-50 cursor-pointer active:scale-98"
        >
          <Save className="w-4 h-4" />
          <span>{isSubmitting ? 'Saving Citizen...' : 'Save & Issue Document'}</span>
        </button>
      </div>
    </form>
  );
}

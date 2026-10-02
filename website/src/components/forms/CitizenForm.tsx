'use client';

import React, { useState, useEffect } from 'react';
import { UserProfile } from '@/types';
import { 
  KeyRound,
  User, 
  Briefcase, 
  HeartPulse,
  Moon,
  BookOpen,
  FileText,
  Plane,
  ShieldCheck, 
  Save, 
  Eye,
  EyeOff,
  RefreshCw,
  Search,
  X,
  Filter
} from 'lucide-react';

const toArabicNumerals = (str: string): string => {
  if (!str) return '';
  const eastern = '٠١٢٣٤٥٦٧٨٩';
  return str.replace(/\d/g, d => eastern[parseInt(d, 10)] || d);
};

const extractDateDigits = (dateStr: string): string => {
  if (!dateStr) return '';
  const clean = dateStr.trim();
  const ymd = clean.match(/^(\d{4})[\/\-](\d{1,2})[\/\-](\d{1,2})$/);
  if (ymd) {
    const y = ymd[1].slice(-2);
    const m = ymd[2].padStart(2, '0');
    const d = ymd[3].padStart(2, '0');
    return `${d}${m}${y}`;
  }
  const dmy = clean.match(/^(\d{1,2})[\/\-](\d{1,2})[\/\-](\d{4})$/);
  if (dmy) {
    const d = dmy[1].padStart(2, '0');
    const m = dmy[2].padStart(2, '0');
    const y = dmy[3].slice(-2);
    return `${d}${m}${y}`;
  }
  const digitsOnly = clean.replace(/\D/g, '');
  return digitsOnly.slice(0, 6);
};

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

const CATEGORIES = [
  { id: 'ALL', label: 'All Sections (9)' },
  { id: 'CREDENTIALS', label: '1. App Login' },
  { id: 'PERSONAL', label: '2. Personal Details' },
  { id: 'SPONSOR', label: '3. Sponsor Details' },
  { id: 'INSURANCE', label: '4. Health Insurance' },
  { id: 'HAJJ', label: '5. Hajj Details' },
  { id: 'PASSPORT', label: '6. My Passport' },
  { id: 'RESIDENT_ID', label: '7. My Resident ID' },
  { id: 'VISA', label: '8. My Visa' },
  { id: 'DRIVING_LICENSE', label: '9. Driving License' },
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
    hasDrivingLicense: initialData?.hasDrivingLicense !== false,
    licenseTypeEn: initialData?.licenseTypeEn ?? '',
    licenseTypeAr: initialData?.licenseTypeAr ?? '',
    licenseIssueDateEn: initialData?.licenseIssueDateEn ?? '',
    licenseIssueDateAr: initialData?.licenseIssueDateAr ?? '',
    licenseExpiryDateEn: initialData?.licenseExpiryDateEn ?? '',
    licenseExpiryDateAr: initialData?.licenseExpiryDateAr ?? '',
    residentIdIssuingDate: initialData?.residentIdIssuingDate ?? '',
    passportNumber: initialData?.passportNumber ?? '',
    passportType: initialData?.passportType ?? '',
    passportIssueDate: initialData?.passportIssueDate ?? '',
    passportExpiryDate: initialData?.passportExpiryDate ?? '',
    passportIssuingCity: initialData?.passportIssuingCity ?? '',
    passportStatus: initialData?.passportStatus ?? '',
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
              placeholder="Search form sections or fields (e.g. Login, Personal, Sponsor, Insurance, Hajj, Passport, Resident ID, Visa, Driving License...)"
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
              className="px-3 py-2 text-xs font-bold text-slate-600 hover:text-slate-900 bg-slate-100 hover:bg-slate-200 rounded-xl transition-colors shrink-0 cursor-pointer"
            >
              Reset Filter
            </button>
          ) : null}
        </div>

        {/* Category Pills */}
        <div className="flex items-center gap-1.5 overflow-x-auto pb-1 text-xs">
          <Filter className="w-3.5 h-3.5 text-slate-400 shrink-0 ml-0.5 mr-1" />
          {CATEGORIES.map(cat => (
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

      {/* ── 1. Mobile App Login Credentials ── */}
      {isSectionVisible('CREDENTIALS', '1. Mobile App Login Credentials', ['app login', 'user id', 'national id', 'iqama', 'app password', 'mobile login', 'account status', 'active', 'suspended', 'password']) && (
      <div className={CARD_CLASSES}>
        <div className="flex items-center gap-2.5 pb-4 mb-5 border-b border-slate-100">
          <div className="w-9 h-9 rounded-xl bg-emerald-50 text-emerald-700 flex items-center justify-center font-bold border border-emerald-100/80">
            <KeyRound className="w-4 h-4" />
          </div>
          <div className="flex-1">
            <div className="flex items-center justify-between">
              <h3 className="font-bold text-slate-900 text-sm">1. Mobile App Login Credentials</h3>
              <span className={`text-[11px] font-bold px-2.5 py-0.5 rounded-full border ${
                formData.accountStatus === 'SUSPENDED'
                  ? 'bg-rose-50 text-rose-700 border-rose-200'
                  : 'bg-emerald-50 text-emerald-700 border-emerald-200'
              }`}>
                {formData.accountStatus === 'SUSPENDED' ? 'Access Suspended' : 'Mobile Access Active'}
              </span>
            </div>
            <p className="text-xs text-slate-500">User ID and password credentials required for the user to log into the mobile app</p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
          {/* User ID / National ID */}
          <div>
            <label className={LABEL_CLASSES}>
              User ID / National ID Number (10 digits) <span className="text-rose-500">*</span>
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
            <p className="text-[11px] text-slate-500 mt-1.5">
              Unique Saudi National / Iqama ID used as mobile login username.
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
              Password for citizen login on Android mobile application.
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

      {/* ── 2. Personal Details ── */}
      {isSectionVisible('PERSONAL', '2. Personal Details', ['personal details', 'name', 'full name', 'birth city', 'birth country', 'date of birth', 'marital status', 'sponsorship transfers', 'work permit', 'biometrics collected', 'travel status']) && (
      <div className={CARD_CLASSES}>
        <div className="flex items-center gap-2.5 pb-4 mb-5 border-b border-slate-100">
          <div className="w-9 h-9 rounded-xl bg-emerald-50 text-emerald-700 flex items-center justify-center font-bold border border-emerald-100/80">
            <User className="w-4 h-4" />
          </div>
          <div>
            <h3 className="font-bold text-slate-900 text-sm">2. Personal Details</h3>
            <p className="text-xs text-slate-500">Citizen demographics and personal identity details matching the application</p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
          {/* Name (English) */}
          <div>
            <label className={LABEL_CLASSES}>
              Name (Full Name in English) <span className="text-rose-500">*</span>
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

          {/* Name (Arabic) */}
          <div>
            <label className={LABEL_CLASSES}>
              Name in Arabic (الاسم الكامل بالعربية) <span className="text-rose-500">*</span>
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

          {/* Birth City */}
          <div>
            <label className={LABEL_CLASSES}>Birth City</label>
            <input
              type="text"
              value={formData.birthCity || ''}
              onChange={(e) => handleChange('birthCity', e.target.value)}
              placeholder="Birth City (e.g. Dhaka / -)"
              className={INPUT_CLASSES}
            />
          </div>

          {/* Birth Country */}
          <div>
            <label className={LABEL_CLASSES}>Birth Country</label>
            <input
              type="text"
              value={formData.birthCountry || ''}
              onChange={(e) => handleChange('birthCountry', e.target.value)}
              placeholder="Birth Country (e.g. Bangladesh / Saudi Arabia)"
              className={INPUT_CLASSES}
            />
          </div>

          {/* Date of Birth */}
          <div>
            <label className={LABEL_CLASSES}>Date of Birth (Gregorian & Hijri)</label>
            <div className="grid grid-cols-2 gap-2">
              <input
                type="text"
                value={formData.dateOfBirth}
                onChange={(e) => {
                  const val = e.target.value;
                  const updated = {
                    ...formData,
                    dateOfBirth: val,
                    dateOfBirthAr: toArabicNumerals(val)
                  };
                  setFormData(updated);
                  onChange?.(updated);
                }}
                placeholder="Gregorian: YYYY/MM/DD"
                className={`${INPUT_CLASSES} font-mono`}
              />
              <input
                type="text"
                value={formData.dateOfBirthHijri}
                onChange={(e) => handleChange('dateOfBirthHijri', e.target.value)}
                placeholder="Hijri: YYYY/MM/DD"
                className={`${INPUT_CLASSES} font-mono`}
              />
            </div>
          </div>

          {/* Marital Status */}
          <div>
            <label className={LABEL_CLASSES}>Marital Status</label>
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
          </div>

          {/* Number of sponsorship transfers */}
          <div>
            <label className={LABEL_CLASSES}>Number of sponsorship transfers</label>
            <input
              type="text"
              value={formData.sponsorshipTransfers || ''}
              onChange={(e) => handleChange('sponsorshipTransfers', e.target.value)}
              placeholder="Transfers count (e.g. 2)"
              className={INPUT_CLASSES}
            />
          </div>

          {/* Village on work permit / Work Permit */}
          <div>
            <label className={LABEL_CLASSES}>Village on work permit / Work Permit</label>
            <input
              type="text"
              value={formData.workPermit || ''}
              onChange={(e) => handleChange('workPermit', e.target.value)}
              placeholder="Work Permit (e.g. Valid / -)"
              className={INPUT_CLASSES}
            />
          </div>

          {/* Biometrics Collected */}
          <div>
            <label className={LABEL_CLASSES}>Biometric Collected</label>
            <select
              value={formData.biometricsCollected || 'Yes'}
              onChange={(e) => handleChange('biometricsCollected', e.target.value)}
              className={SELECT_CLASSES}
            >
              <option value="Yes">Yes (نعم - مكتملة)</option>
              <option value="No">No (لا - غير مكتملة)</option>
            </select>
          </div>

          {/* Travel Status */}
          <div>
            <label className={LABEL_CLASSES}>Travel Status</label>
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

      {/* ── 3. Sponsor Details ── */}
      {isSectionVisible('SPONSOR', '3. Sponsor Details', ['sponsor details', 'sponsor name', 'sponsor id']) && (
      <div className={CARD_CLASSES}>
        <div className="flex items-center gap-2.5 pb-4 mb-5 border-b border-slate-100">
          <div className="w-9 h-9 rounded-xl bg-emerald-50 text-emerald-700 flex items-center justify-center font-bold border border-emerald-100/80">
            <Briefcase className="w-4 h-4" />
          </div>
          <div>
            <h3 className="font-bold text-slate-900 text-sm">3. Sponsor Details</h3>
            <p className="text-xs text-slate-500">Official sponsor name and sponsor ID number as configured in the mobile application</p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
          {/* Sponsor Name */}
          <div>
            <label className={LABEL_CLASSES}>Sponsor Name</label>
            <input
              type="text"
              value={formData.sponsorName || ''}
              onChange={(e) => handleChange('sponsorName', e.target.value)}
              placeholder="Sponsor Name (e.g. مغاسل درر نجاح للملابس / Durrat Najah Laundry)"
              className={`${INPUT_CLASSES} font-bold`}
            />
          </div>

          {/* Sponsor ID */}
          <div>
            <label className={LABEL_CLASSES}>Sponsor ID Number</label>
            <input
              type="text"
              value={formData.sponsorId || ''}
              onChange={(e) => handleChange('sponsorId', e.target.value)}
              placeholder="Sponsor ID (e.g. 7034884309)"
              className={`${INPUT_CLASSES} font-mono font-bold`}
            />
          </div>
        </div>
      </div>
      )}

      {/* ── 4. Health Insurance ── */}
      {isSectionVisible('INSURANCE', '4. Health Insurance', ['health insurance', 'insurance', 'issuing date', 'expiry date', 'blood type']) && (
      <div className={CARD_CLASSES}>
        <div className="flex items-center gap-2.5 pb-4 mb-5 border-b border-slate-100">
          <div className="w-9 h-9 rounded-xl bg-emerald-50 text-emerald-700 flex items-center justify-center font-bold border border-emerald-100/80">
            <HeartPulse className="w-4 h-4" />
          </div>
          <div>
            <h3 className="font-bold text-slate-900 text-sm">4. Health Insurance</h3>
            <p className="text-xs text-slate-500">Official health coverage details shown in the citizen profile</p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
          <div>
            <label className={LABEL_CLASSES}>Issuing Date (تاريخ الإصدار)</label>
            <input
              type="text"
              value={formData.insuranceIssuingDate || ''}
              onChange={(e) => handleChange('insuranceIssuingDate', e.target.value)}
              placeholder="DD/MM/YYYY (e.g. -)"
              className={`${INPUT_CLASSES} font-mono`}
            />
          </div>

          <div>
            <label className={LABEL_CLASSES}>Expiry Date (تاريخ الانتهاء)</label>
            <input
              type="text"
              value={formData.insuranceExpiry || ''}
              onChange={(e) => handleChange('insuranceExpiry', e.target.value)}
              placeholder="DD/MM/YYYY (e.g. 14/04/2026)"
              className={`${INPUT_CLASSES} font-mono`}
            />
          </div>

          <div>
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
      </div>
      )}

      {/* ── 5. Hajj Details ── */}
      {isSectionVisible('HAJJ', '5. Hajj Details', ['hajj', 'hajj eligibility', 'hodge', 'eligible', 'last hajj year']) && (
      <div className={CARD_CLASSES}>
        <div className="flex items-center gap-2.5 pb-4 mb-5 border-b border-slate-100">
          <div className="w-9 h-9 rounded-xl bg-emerald-50 text-emerald-700 flex items-center justify-center font-bold border border-emerald-100/80">
            <Moon className="w-4 h-4" />
          </div>
          <div>
            <h3 className="font-bold text-slate-900 text-sm">5. Hajj Details</h3>
            <p className="text-xs text-slate-500">Pilgrimage eligibility and official history</p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
          <div>
            <label className={LABEL_CLASSES}>Hajj Status / Eligibility (حالة الحج)</label>
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

      {/* ── 6. My Passport ── */}
      {isSectionVisible('PASSPORT', '6. My Passport', ['passport', 'passport number', 'passport type', 'normal', 'issuing city', 'status', 'valid']) && (
      <div className={CARD_CLASSES}>
        <div className="flex items-center gap-2.5 pb-4 mb-5 border-b border-slate-100">
          <div className="w-9 h-9 rounded-xl bg-amber-50 text-amber-700 flex items-center justify-center font-bold border border-amber-100/80">
            <BookOpen className="w-4 h-4" />
          </div>
          <div>
            <h3 className="font-bold text-slate-900 text-sm">6. My Passport</h3>
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
              value={formData.passportIssuingCity || ''}
              onChange={(e) => handleChange('passportIssuingCity', e.target.value)}
              placeholder="مكان الإصدار (مثال: دكا / Dhaka)"
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

      {/* ── 7. My Resident ID ── */}
      {isSectionVisible('RESIDENT_ID', '7. My Resident ID', ['my resident id', 'resident id', 'id version', 'version number', 'issuing date', 'expiry date']) && (
      <div className={CARD_CLASSES}>
        <div className="flex items-center gap-2.5 pb-4 mb-5 border-b border-slate-100">
          <div className="w-9 h-9 rounded-xl bg-emerald-50 text-emerald-700 flex items-center justify-center font-bold border border-emerald-100/80">
            <FileText className="w-4 h-4" />
          </div>
          <div>
            <h3 className="font-bold text-slate-900 text-sm">7. My Resident ID</h3>
            <p className="text-xs text-slate-500">Official Resident ID profile card details shown in My Resident ID screen</p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
          {/* Resident ID Number (Synced with National ID) */}
          <div>
            <label className={LABEL_CLASSES}>Resident ID Number</label>
            <div className="px-3.5 py-2.5 bg-slate-100 border border-slate-300 rounded-xl text-xs font-mono font-bold text-slate-900 flex items-center justify-between">
              <span>{formData.nationalId || '—'}</span>
              <span className="text-[10px] text-slate-500 font-sans font-semibold">Synced with Login ID</span>
            </div>
          </div>

          {/* ID Version */}
          <div>
            <label className={LABEL_CLASSES}>ID Version (رقم النسخة)</label>
            <input
              type="text"
              value={formData.versionNumber || '٢'}
              onChange={(e) => handleChange('versionNumber', e.target.value)}
              placeholder="e.g. ٢ or 2"
              className={`${INPUT_CLASSES} font-bold`}
            />
          </div>

          {/* Issuing Date */}
          <div>
            <label className={LABEL_CLASSES}>Issuing Date (تاريخ الإصدار)</label>
            <input
              type="text"
              value={formData.residentIdIssuingDate || ''}
              onChange={(e) => {
                const val = e.target.value;
                const digits = extractDateDigits(val);
                const updated = {
                  ...formData,
                  residentIdIssuingDate: val,
                  ...(digits ? { issueDateDigits: digits } : {})
                };
                setFormData(updated);
                onChange?.(updated);
              }}
              placeholder="DD/MM/YYYY (e.g. 28/03/2021)"
              className={`${INPUT_CLASSES} font-mono`}
            />
          </div>

          {/* Expiry Date */}
          <div>
            <label className={LABEL_CLASSES}>Expiry Date (تاريخ الانتهاء)</label>
            <input
              type="text"
              value={formData.expiryDateEn || ''}
              onChange={(e) => {
                const val = e.target.value;
                const digits = extractDateDigits(val);
                const updated = {
                  ...formData,
                  expiryDateEn: val,
                  expiryDateAr: toArabicNumerals(val),
                  ...(digits ? { expiryDateDigits: digits } : {})
                };
                setFormData(updated);
                onChange?.(updated);
              }}
              placeholder="YYYY/MM/DD (e.g. 2026/10/08)"
              className={`${INPUT_CLASSES} font-mono`}
            />
          </div>
        </div>
      </div>
      )}

      {/* ── 8. My Visa ── */}
      {isSectionVisible('VISA', '8. My Visa', ['visa', 'my visa', 'visa number', 'visa type', 'exit date', 'entry']) && (
      <div className={CARD_CLASSES}>
        <div className="flex items-center gap-2.5 pb-4 mb-5 border-b border-slate-100">
          <div className="w-9 h-9 rounded-xl bg-blue-50 text-blue-700 flex items-center justify-center font-bold border border-blue-100/80">
            <Plane className="w-4 h-4" />
          </div>
          <div>
            <h3 className="font-bold text-slate-900 text-sm">8. My Visa</h3>
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

      {/* ── 9. Driving License ── */}
      {isSectionVisible('DRIVING_LICENSE', '9. Driving License', ['driving license', 'license type', 'private', 'commercial', 'blood type', 'issue date', 'expiry date', 'status']) && (
      <div className={CARD_CLASSES}>
        <div className="flex items-center justify-between pb-4 mb-5 border-b border-slate-100">
          <div className="flex items-center gap-2.5">
            <div className="w-9 h-9 rounded-xl bg-blue-50 text-blue-700 flex items-center justify-center font-bold border border-blue-100/80">
              <ShieldCheck className="w-4 h-4" />
            </div>
            <div>
              <h3 className="font-bold text-slate-900 text-sm">9. Driving License</h3>
              <p className="text-xs text-slate-500">License type, dates and blood type shown in Driving License viewer</p>
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
                onChange={(e) => {
                  const val = e.target.value;
                  const updated = {
                    ...formData,
                    licenseIssueDateEn: val,
                    licenseIssueDateAr: toArabicNumerals(val)
                  };
                  setFormData(updated);
                  onChange?.(updated);
                }}
                placeholder="DD/MM/YYYY  (e.g. 10/03/2026)"
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
                placeholder="١٠/٠٣/١٤٤٧  (Eastern-Arabic digits)"
                className={`${INPUT_CLASSES} font-mono font-bold`}
              />
            </div>

            {/* License Expiry Date */}
            <div>
              <label className={LABEL_CLASSES}>License Expiry Date — English (تاريخ الانتهاء)</label>
              <input
                type="text"
                value={formData.licenseExpiryDateEn || ''}
                onChange={(e) => {
                  const val = e.target.value;
                  const updated = {
                    ...formData,
                    licenseExpiryDateEn: val,
                    licenseExpiryDateAr: toArabicNumerals(val)
                  };
                  setFormData(updated);
                  onChange?.(updated);
                }}
                placeholder="DD/MM/YYYY  (e.g. 21/11/2035)"
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
                placeholder="٢١/١١/١٤٥٧  (Eastern-Arabic digits)"
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

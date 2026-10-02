'use client';

import React, { useState, useRef } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import Sidebar from '@/components/layout/Sidebar';
import TopHeader from '@/components/layout/TopHeader';
import CitizenForm from '@/components/forms/CitizenForm';
import InlineEditMuqeemCard from '@/components/cards/InlineEditMuqeemCard';
import InlineEditDrivingLicense from '@/components/cards/InlineEditDrivingLicense';
import DocumentExportMenu from '@/components/common/DocumentExportMenu';
import { saveCitizen } from '@/lib/firestoreService';
import { UserProfile } from '@/types';
import { toast } from '@/lib/toast';
import {
  ArrowLeft, Car, CreditCard, Save, Download
} from 'lucide-react';

export default function NewCitizenPage() {
  const router = useRouter();
  const [activeCardTab, setActiveCardTab] = useState<'resident' | 'license'>('resident');
  const [formData, setFormData] = useState<Partial<UserProfile>>({
    nationalId: '',
    fullNameEn: '',
    fullNameAr: '',
    dateOfBirth: '',
    dateOfBirthAr: '',
    dateOfBirthHijri: '',
    nationality: 'Bangladesh',
    nationalityAr: 'بنجلاديش',
    placeOfBirthEn: '',
    placeOfBirthAr: '',
    birthCity: '',
    birthCountry: '',
    maritalStatus: 'SINGLE (أعزب)',
    sponsorshipTransfers: '0',
    religionEn: 'Islam',
    religionAr: 'مسلم',
    workPermit: 'REGULAR',
    biometricsCollected: 'Enrolled (بصمة مسجلة)',
    travelStatus: 'INSIDE_KINGDOM',
    professionEn: '',
    professionAr: '',
    sponsorId: '7000000000',
    sponsorNameEn: '',
    sponsorName: '',
    establishmentStatus: 'High Green (النطاق البلاتيني)',
    issuePlaceEn: 'Riyadh',
    issuePlace: 'الرياض',
    workPlaceAr: 'منطقة الرياض',
    insuranceIssuingDate: '2026/01/01',
    insuranceExpiry: '2027/01/01',
    bloodType: 'A+',
    insuranceCompany: 'Tawuniya (التعاونية للتأمين)',
    insurancePolicyNo: 'POL-2026-9901',
    insuranceStatus: 'ACTIVE (ساري)',
    hajjEligibility: 'ELIGIBLE (مؤهل للحج)',
    lastHajjYear: 'None',
    expiryDateEn: '',
    expiryDateAr: '',
    versionNumber: '٢',
    expiryDateDigits: '081026',
    issueDateDigits: '010126',
    photoUrl: '',
    licenseTypeEn: 'Private',
    licenseTypeAr: 'خصوصي',
    licenseIssueDateEn: '10/03/2026',
    licenseIssueDateAr: '١٠/٠٣/٢٠٢٦',
    licenseExpiryDateEn: '21/11/2035',
    licenseExpiryDateAr: '٢١/١١/٢٠٣٥',
    residentIdIssuingDate: '2026/01/01',
    visaNumber: '',
    visaType: 'Work Visa (تأشيرة عمل)',
    visaExitDate: '',
    verificationLevel: 'TIER_3_VERIFIED',
    digitalIdActive: true,
    hasDrivingLicense: true,
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const formDataJsonRef = useRef<string>('');

  const handleFormChange = (updated: UserProfile) => {
    const json = JSON.stringify(updated);
    if (json === formDataJsonRef.current) return;
    formDataJsonRef.current = json;
    setFormData(updated);
  };

  const handleCardChange = (changes: Partial<UserProfile>) => {
    setFormData(prev => {
      const next = { ...prev, ...changes };
      formDataJsonRef.current = JSON.stringify(next);
      return next;
    });
  };

  const handleSubmit = async (data: UserProfile) => {
    setIsSubmitting(true);
    try {
      const merged = { ...formData, ...data } as UserProfile;
      await saveCitizen(merged);
      toast.success(`User & Document created successfully for ${merged.fullNameEn || merged.nationalId}!`);
      // Transition URL to edit page without full reload so user is not bounced back
      if (merged.id || merged.nationalId) {
        router.replace(`/users/${merged.id || merged.nationalId}`);
      }
    } catch (err) {
      console.error(err);
      toast.error('Error creating user. Check console or try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="flex min-h-screen bg-[#f8faf9]">
      <Sidebar />

      <main className="flex-1 flex flex-col min-w-0">
        <TopHeader
          title="Add User"
          subtitle="Create a user profile and issue their digital ID card"
        />

        <div className="p-6 space-y-6 max-w-[1600px]">
          {/* Nav + Save bar */}
          <div className="flex items-center justify-between">
            <Link
              href="/users"
              className="inline-flex items-center gap-1.5 text-xs font-medium text-slate-500 hover:text-slate-900 transition-colors"
            >
              <ArrowLeft className="w-3.5 h-3.5" />
              <span>Back to Users</span>
            </Link>

            <button
              type="button"
              onClick={() => {
                if (!formData.nationalId) {
                  toast.error('Please enter a National ID / Iqama number.');
                  return;
                }
                handleSubmit(formData as UserProfile);
              }}
              disabled={isSubmitting}
              className="flex items-center gap-2 bg-emerald-700 hover:bg-emerald-800 text-white px-6 py-2.5 rounded-xl text-sm font-bold shadow-md hover:shadow-lg transition-all disabled:opacity-50 cursor-pointer active:scale-98"
            >
              <Save className="w-4 h-4" />
              <span>{isSubmitting ? 'Issuing…' : 'Save & Issue'}</span>
            </button>
          </div>

          {/* Main two-column layout matching Edit page */}
          <div className="grid grid-cols-1 xl:grid-cols-[1fr_740px] gap-8 items-start">
            {/* LEFT: Personal + general form */}
            <div className="min-w-0">
              <CitizenForm
                initialData={formData as UserProfile}
                onChange={handleFormChange}
                onSubmit={handleSubmit}
                isSubmitting={isSubmitting}
              />
            </div>

            {/* RIGHT: Inline-editable document cards */}
            <div className="sticky top-6 space-y-4">
              <div className="bg-white rounded-2xl border border-slate-200 shadow-xs">
                <div className="px-5 py-4 border-b border-slate-100 flex items-center justify-between">
                  <div>
                    <h3 className="font-bold text-slate-900 text-sm">Document Card Editor</h3>
                    <p className="text-[11px] text-slate-500 mt-0.5">
                      Click any field directly on the card template to edit. Use <span className="font-semibold text-violet-700">Scan Doc</span> to auto-fill from a photo.
                    </p>
                  </div>
                </div>

                {/* Tab switcher */}
                <div className="flex bg-slate-50 border-b border-slate-200">
                  {([
                    { key: 'resident', label: 'Digital Document / E-CAMA Card', icon: CreditCard },
                    { key: 'license', label: 'Driving License', icon: Car },
                  ] as const).map(({ key, label, icon: Icon }) => (
                    <button
                      key={key}
                      type="button"
                      onClick={() => setActiveCardTab(key)}
                      className={`flex-1 flex items-center justify-center gap-1.5 py-3 text-xs font-bold transition-all cursor-pointer border-b-2 ${
                        activeCardTab === key
                          ? 'text-emerald-800 border-emerald-600 bg-white'
                          : 'text-slate-500 hover:text-slate-800 border-transparent hover:bg-white/60'
                      }`}
                    >
                      <Icon className="w-3.5 h-3.5" />
                      <span>{label}</span>
                    </button>
                  ))}
                </div>

                {/* Card content */}
                <div className="p-5 space-y-6">
                  {activeCardTab === 'resident' && (
                    <InlineEditMuqeemCard
                      user={formData}
                      onChange={handleCardChange}
                    />
                  )}

                  {activeCardTab === 'license' && (
                    <InlineEditDrivingLicense
                      user={formData}
                      onChange={handleCardChange}
                    />
                  )}
                </div>

                {/* Bottom Card Action Bar with Full Export Options */}
                <div className="px-5 py-3.5 bg-slate-50/90 border-t border-slate-200 rounded-b-2xl flex items-center justify-between gap-3">
                  <div className="flex items-center gap-2 text-xs text-slate-600 font-semibold">
                    <Download className="w-4 h-4 text-emerald-700" />
                    <span>Download Official Document:</span>
                  </div>
                  <DocumentExportMenu
                    user={formData}
                    docType={activeCardTab === 'resident' ? 'RESIDENT_ID' : 'DRIVING_LICENSE'}
                    showDocSelector
                    size="md"
                    placement="top"
                  />
                </div>
              </div>

              {/* Save hint */}
              <p className="text-[11px] text-slate-400 text-center">
                Changes on the card are reflected live. Hit <strong className="text-slate-600">Save & Issue</strong> when done.
              </p>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}

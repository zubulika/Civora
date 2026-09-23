'use client';

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import Sidebar from '@/components/layout/Sidebar';
import TopHeader from '@/components/layout/TopHeader';
import CitizenForm from '@/components/forms/CitizenForm';
import MuqeemCardPreview from '@/components/cards/MuqeemCardPreview';
import DrivingLicensePreview from '@/components/cards/DrivingLicensePreview';
import { saveCitizen } from '@/lib/firestoreService';
import { UserProfile } from '@/types';
import { ArrowLeft, CheckCircle2, Sparkles, CreditCard, Car, Layers } from 'lucide-react';
import Link from 'next/link';

export default function NewCitizenPage() {
  const router = useRouter();
  const [activeCardTab, setActiveCardTab] = useState<'license' | 'resident' | 'both'>('license');
  const [livePreviewData, setLivePreviewData] = useState<Partial<UserProfile>>({
    nationalId: '',
    fullNameEn: '',
    fullNameAr: '',
    dateOfBirth: '',
    dateOfBirthAr: '',
    dateOfBirthHijri: '',
    nationality: '',
    nationalityAr: '',
    placeOfBirthEn: '',
    placeOfBirthAr: '',
    birthCity: '',
    birthCountry: '',
    maritalStatus: '',
    sponsorshipTransfers: '',
    religionEn: '',
    religionAr: '',
    workPermit: '',
    biometricsCollected: '',
    travelStatus: '',
    professionEn: '',
    professionAr: '',
    sponsorId: '',
    sponsorNameEn: '',
    sponsorName: '',
    establishmentStatus: '',
    issuePlaceEn: '',
    issuePlace: '',
    workPlaceAr: '',
    insuranceIssuingDate: '',
    insuranceExpiry: '',
    bloodType: '',
    insuranceCompany: '',
    insurancePolicyNo: '',
    insuranceStatus: '',
    hajjEligibility: '',
    lastHajjYear: '',
    expiryDateEn: '',
    expiryDateAr: '',
    versionNumber: '٢',
    expiryDateDigits: '',
    issueDateDigits: '',
    photoUrl: '',
    licenseTypeEn: '',
    licenseTypeAr: '',
    licenseIssueDateEn: '',
    licenseIssueDateAr: '',
    licenseExpiryDateEn: '',
    licenseExpiryDateAr: '',
    residentIdIssuingDate: '',
    visaNumber: '',
    visaType: '',
    visaExitDate: '',
    verificationLevel: 'TIER_3_VERIFIED',
    digitalIdActive: true,
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const handleSubmit = async (data: UserProfile) => {
    setIsSubmitting(true);
    try {
      await saveCitizen(data);
      setSuccessMessage(`Document issued successfully for ${data.fullNameEn || data.fullNameAr || data.nationalId}!`);
      setTimeout(() => {
        router.push('/users');
      }, 1500);
    } catch (err) {
      console.error(err);
      alert('Error saving citizen. Check console.');
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

        <div className="p-8 space-y-6 max-w-7xl">
          {/* Back link & Success Alert */}
          <div className="flex items-center justify-between">
            <Link
              href="/users"
              className="inline-flex items-center gap-1.5 text-xs font-semibold text-gray-500 hover:text-emerald-700 transition-colors"
            >
              <ArrowLeft className="w-3.5 h-3.5" />
              <span>Back to Citizen Directory</span>
            </Link>

            {successMessage && (
              <div className="flex items-center gap-2 bg-emerald-100 text-emerald-800 border border-emerald-300 px-4 py-2 rounded-xl text-xs font-bold animate-fade-in">
                <CheckCircle2 className="w-4 h-4 text-emerald-600" />
                <span>{successMessage}</span>
              </div>
            )}
          </div>

          {/* 2-Column Responsive Layout */}
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
            {/* Left 7 Cols: Input Form */}
            <div className="lg:col-span-7">
              <CitizenForm
                initialData={livePreviewData}
                onChange={(updated) => setLivePreviewData(updated)}
                onSubmit={handleSubmit}
                isSubmitting={isSubmitting}
              />
            </div>

            {/* Right 5 Cols: Live Real-Time Card Preview */}
            <div className="lg:col-span-5 sticky top-28 space-y-4">
              <div className="bg-white p-6 rounded-2xl border border-gray-200/90 shadow-xs">
                <div className="flex items-center justify-between pb-3 mb-4 border-b border-gray-100">
                  <div>
                    <h3 className="font-bold text-gray-900 text-sm flex items-center gap-1.5">
                      <span>Real-Time Card Preview</span>
                      <Sparkles className="w-3.5 h-3.5 text-amber-500" />
                    </h3>
                    <p className="text-[11px] text-gray-500">Live preview matching mobile application rendering</p>
                  </div>
                </div>

                {/* Card Template Switcher */}
                <div className="flex bg-slate-100 p-1 rounded-xl mb-4 border border-slate-200">
                  <button
                    type="button"
                    onClick={() => setActiveCardTab('license')}
                    className={`flex-1 flex items-center justify-center gap-1.5 py-2 text-xs font-bold rounded-lg transition-all cursor-pointer ${
                      activeCardTab === 'license'
                        ? 'bg-white text-emerald-800 shadow-2xs border border-slate-200/80'
                        : 'text-slate-500 hover:text-slate-900'
                    }`}
                  >
                    <Car className="w-3.5 h-3.5" />
                    <span>Driving License (رخصة القيادة)</span>
                  </button>
                  <button
                    type="button"
                    onClick={() => setActiveCardTab('resident')}
                    className={`flex-1 flex items-center justify-center gap-1.5 py-2 text-xs font-bold rounded-lg transition-all cursor-pointer ${
                      activeCardTab === 'resident'
                        ? 'bg-white text-emerald-800 shadow-2xs border border-slate-200/80'
                        : 'text-slate-500 hover:text-slate-900'
                    }`}
                  >
                    <CreditCard className="w-3.5 h-3.5" />
                    <span>Resident ID (هوية مقيم)</span>
                  </button>
                  <button
                    type="button"
                    onClick={() => setActiveCardTab('both')}
                    className={`flex items-center justify-center gap-1.5 px-3 py-2 text-xs font-bold rounded-lg transition-all cursor-pointer ${
                      activeCardTab === 'both'
                        ? 'bg-white text-emerald-800 shadow-2xs border border-slate-200/80'
                        : 'text-slate-500 hover:text-slate-900'
                    }`}
                  >
                    <Layers className="w-3.5 h-3.5" />
                    <span>Both</span>
                  </button>
                </div>

                {/* Interactive Card Preview */}
                {activeCardTab === 'license' && (
                  <DrivingLicensePreview user={livePreviewData} />
                )}
                {activeCardTab === 'resident' && (
                  <MuqeemCardPreview user={livePreviewData} />
                )}
                {activeCardTab === 'both' && (
                  <div className="space-y-6">
                    <div>
                      <div className="text-xs font-bold text-slate-700 mb-2 flex items-center gap-1.5">
                        <Car className="w-3.5 h-3.5 text-blue-600" />
                        <span>Official Saudi Driving License (رخصة القيادة)</span>
                      </div>
                      <DrivingLicensePreview user={livePreviewData} />
                    </div>
                    <div className="pt-4 border-t border-slate-200">
                      <div className="text-xs font-bold text-slate-700 mb-2 flex items-center gap-1.5">
                        <CreditCard className="w-3.5 h-3.5 text-emerald-600" />
                        <span>Official Resident ID (هوية مقيم / Muqeem)</span>
                      </div>
                      <MuqeemCardPreview user={livePreviewData} />
                    </div>
                  </div>
                )}

                <div className="mt-4 p-3 bg-emerald-50/70 border border-emerald-200 rounded-xl text-[11px] text-emerald-900 leading-relaxed">
                  <strong>Notice:</strong> Once issued, this digital document will immediately sync to Firebase Firestore collection{' '}
                  <code className="font-mono text-[10px] bg-emerald-100 px-1 py-0.5 rounded">
                    users/{livePreviewData.nationalId || '[National-ID]'}
                  </code>{' '}
                  and become viewable on the citizen&apos;s mobile device upon login.
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}

'use client';

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import Sidebar from '@/components/layout/Sidebar';
import TopHeader from '@/components/layout/TopHeader';
import CitizenForm from '@/components/forms/CitizenForm';
import MuqeemCardPreview from '@/components/cards/MuqeemCardPreview';
import { saveCitizen } from '@/lib/firestoreService';
import { UserProfile } from '@/types';
import { ArrowLeft, CheckCircle2, Sparkles } from 'lucide-react';
import Link from 'next/link';

export default function NewCitizenPage() {
  const router = useRouter();
  const [livePreviewData, setLivePreviewData] = useState<Partial<UserProfile>>({
    nationalId: '2495685261',
    fullNameEn: 'MD ABDUL HALIM MEIA',
    fullNameAr: 'مد عبد ال حليم مياه',
    dateOfBirth: '1988/02/03',
    nationality: 'Bangladesh',
    nationalityAr: 'بنجلاديش',
    professionEn: 'Laundry Worker',
    professionAr: 'عامل غسيل ملابس',
    sponsorId: '7034884309',
    sponsorName: 'مؤسسة درر نجاح للملابس',
    issuePlace: 'شركة العلم لامن المعلومات',
    expiryDateEn: '2026/10/08',
    versionNumber: '٢',
    photoUrl: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&auto=format&fit=crop&q=80'
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const handleSubmit = async (data: UserProfile) => {
    setIsSubmitting(true);
    try {
      await saveCitizen(data);
      setSuccessMessage(`Document issued successfully for ${data.fullNameEn}!`);
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
          title="Issue Digital Identity & Residence Permit"
          subtitle="Ministry of Interior • Direct Cloud Provisioning"
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
                      <span>Real-Time ID Preview</span>
                      <Sparkles className="w-3.5 h-3.5 text-amber-500" />
                    </h3>
                    <p className="text-[11px] text-gray-500">Live preview of the issued Saudi Resident card</p>
                  </div>
                </div>

                {/* The Interactive Preview */}
                <MuqeemCardPreview user={livePreviewData} />

                <div className="mt-4 p-3 bg-emerald-50/70 border border-emerald-200 rounded-xl text-[11px] text-emerald-900 leading-relaxed">
                  <strong>Notice:</strong> Once issued, this digital document will immediately sync to Firebase Firestore collection <code className="font-mono text-[10px] bg-emerald-100 px-1 py-0.5 rounded">users/{livePreviewData.nationalId}</code> and become viewable on the citizen's mobile device upon login.
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}

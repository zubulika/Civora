'use client';

import React, { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import Link from 'next/link';
import Sidebar from '@/components/layout/Sidebar';
import TopHeader from '@/components/layout/TopHeader';
import CitizenForm from '@/components/forms/CitizenForm';
import MuqeemCardPreview from '@/components/cards/MuqeemCardPreview';
import DrivingLicensePreview from '@/components/cards/DrivingLicensePreview';
import { fetchCitizenById, saveCitizen } from '@/lib/firestoreService';
import { UserProfile } from '@/types';
import { ArrowLeft, CheckCircle2, Sparkles, AlertCircle, CreditCard, Car, Layers } from 'lucide-react';

export default function EditCitizenPage() {
  const params = useParams();
  const router = useRouter();
  const citizenId = params.id as string;

  const [activeCardTab, setActiveCardTab] = useState<'license' | 'resident' | 'both'>('license');
  const [citizen, setCitizen] = useState<UserProfile | null>(null);
  const [livePreviewData, setLivePreviewData] = useState<Partial<UserProfile>>({});
  const [loading, setLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  useEffect(() => {
    async function load() {
      try {
        const data = await fetchCitizenById(citizenId);
        if (data) {
          setCitizen(data);
          setLivePreviewData(data);
        }
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    }
    load();
  }, [citizenId]);

  const handleSubmit = async (updatedData: UserProfile) => {
    setIsSubmitting(true);
    try {
      await saveCitizen(updatedData);
      setSuccessMessage(`Changes successfully saved for ${updatedData.fullNameEn}!`);
      setTimeout(() => {
        router.push('/users');
      }, 1500);
    } catch (err) {
      console.error(err);
      alert('Error updating citizen.');
    } finally {
      setIsSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="flex min-h-screen bg-[#f8faf9]">
        <Sidebar />
        <main className="flex-1 p-12 text-center text-xs text-gray-400">Loading citizen profile...</main>
      </div>
    );
  }

  if (!citizen) {
    return (
      <div className="flex min-h-screen bg-[#f8faf9]">
        <Sidebar />
        <main className="flex-1 p-12 text-center text-gray-500">
          <AlertCircle className="w-8 h-8 text-amber-500 mx-auto mb-2" />
          <p className="font-bold text-sm">Citizen profile not found.</p>
          <Link href="/users" className="text-emerald-700 text-xs hover:underline mt-2 inline-block">
            Return to Directory
          </Link>
        </main>
      </div>
    );
  }

  return (
    <div className="flex min-h-screen bg-[#f8faf9]">
      <Sidebar />

      <main className="flex-1 flex flex-col min-w-0">
        <TopHeader
          title={`Edit User: ${citizen.fullNameEn}`}
          subtitle={`ID: ${citizen.nationalId}`}
        />

        <div className="p-6 space-y-6 max-w-7xl">
          <div className="flex items-center justify-between">
            <Link
              href="/users"
              className="inline-flex items-center gap-1.5 text-xs font-medium text-slate-500 hover:text-slate-900 transition-colors"
            >
              <ArrowLeft className="w-3.5 h-3.5" />
              <span>Back to Users</span>
            </Link>

            {successMessage && (
              <div className="flex items-center gap-2 bg-emerald-100 text-emerald-800 border border-emerald-300 px-4 py-2 rounded-xl text-xs font-bold animate-fade-in">
                <CheckCircle2 className="w-4 h-4 text-emerald-600" />
                <span>{successMessage}</span>
              </div>
            )}
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
            <div className="lg:col-span-7">
              <CitizenForm
                initialData={citizen}
                onChange={(updated) => setLivePreviewData(updated)}
                onSubmit={handleSubmit}
                isSubmitting={isSubmitting}
              />
            </div>

            <div className="lg:col-span-5 sticky top-28 space-y-4">
              <div className="bg-white p-6 rounded-2xl border border-gray-200/90 shadow-xs">
                <div className="flex items-center justify-between pb-3 mb-4 border-b border-gray-100">
                  <div>
                    <h3 className="font-bold text-gray-900 text-sm flex items-center gap-1.5">
                      <span>Live Card Preview</span>
                      <Sparkles className="w-3.5 h-3.5 text-amber-500" />
                    </h3>
                    <p className="text-[11px] text-gray-500">Updates live matching mobile application view</p>
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
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}

'use client';

import React, { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import Link from 'next/link';
import Sidebar from '@/components/layout/Sidebar';
import TopHeader from '@/components/layout/TopHeader';
import CitizenForm from '@/components/forms/CitizenForm';
import InlineEditDrivingLicense from '@/components/cards/InlineEditDrivingLicense';
import InlineEditMuqeemCard from '@/components/cards/InlineEditMuqeemCard';
import { fetchCitizenById, saveCitizen } from '@/lib/firestoreService';
import { UserProfile } from '@/types';
import {
  ArrowLeft, CheckCircle2, AlertCircle, Car, CreditCard, Save
} from 'lucide-react';

export default function EditCitizenPage() {
  const params = useParams();
  const router = useRouter();
  const citizenId = params.id as string;

  const [activeDocTab, setActiveDocTab] = useState<'resident' | 'license'>('resident');
  const [citizen, setCitizen] = useState<UserProfile | null>(null);
  const [formData, setFormData] = useState<Partial<UserProfile>>({});
  const [loading, setLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  // Stable serialized snapshot to avoid re-render loops when syncing form ↔ card
  const formDataJsonRef = React.useRef<string>('');

  useEffect(() => {
    async function load() {
      try {
        const data = await fetchCitizenById(citizenId);
        if (data) {
          setCitizen(data);
          setFormData(data);
        }
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    }
    load();
  }, [citizenId]);

  // Merge updates from either the personal form or the inline card editors
  // Guard with JSON comparison so CitizenForm's sync effect doesn't loop back
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

  const handleSave = async () => {
    if (!citizen) return;
    setIsSubmitting(true);
    try {
      const merged = { ...citizen, ...formData } as UserProfile;
      await saveCitizen(merged);
      setSuccessMessage(`Changes saved for ${merged.fullNameEn || 'user'}!`);
      setTimeout(() => router.push('/users'), 1500);
    } catch (err) {
      console.error(err);
      alert('Error saving changes.');
    } finally {
      setIsSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="flex min-h-screen bg-[#f8faf9]">
        <Sidebar />
        <main className="flex-1 p-12 text-center text-xs text-gray-400">Loading citizen profile…</main>
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

            <div className="flex items-center gap-3">
              {successMessage && (
                <div className="flex items-center gap-2 bg-emerald-100 text-emerald-800 border border-emerald-300 px-4 py-2 rounded-xl text-xs font-bold">
                  <CheckCircle2 className="w-4 h-4 text-emerald-600" />
                  <span>{successMessage}</span>
                </div>
              )}
              <button
                type="button"
                onClick={handleSave}
                disabled={isSubmitting}
                className="flex items-center gap-2 bg-emerald-700 hover:bg-emerald-800 text-white px-6 py-2.5 rounded-xl text-sm font-bold shadow-md hover:shadow-lg transition-all disabled:opacity-50 cursor-pointer active:scale-98"
              >
                <Save className="w-4 h-4" />
                <span>{isSubmitting ? 'Saving…' : 'Save & Issue'}</span>
              </button>
            </div>
          </div>

          {/* Main two-column layout */}
          <div className="grid grid-cols-1 xl:grid-cols-[1fr_740px] gap-8 items-start">

            {/* LEFT: Personal + general form */}
            <div className="min-w-0">
              <CitizenForm
                initialData={{ ...citizen, ...formData } as UserProfile}
                onChange={handleFormChange}
                onSubmit={async (data) => {
                  setIsSubmitting(true);
                  try {
                    await saveCitizen(data);
                    setSuccessMessage(`Changes saved for ${data.fullNameEn}!`);
                    setTimeout(() => router.push('/users'), 1500);
                  } catch { alert('Error saving.'); }
                  finally { setIsSubmitting(false); }
                }}
                isSubmitting={isSubmitting}
              />
            </div>

            {/* RIGHT: Inline-editable document cards */}
            <div className="sticky top-6 space-y-4">
              {/* Section header */}
              <div className="bg-white rounded-2xl border border-slate-200 shadow-xs overflow-hidden">
                <div className="px-5 py-4 border-b border-slate-100">
                  <h3 className="font-bold text-slate-900 text-sm">Document Card Editor</h3>
                  <p className="text-[11px] text-slate-500 mt-0.5">
                    Click any field directly on the card template to edit. Use <span className="font-semibold text-violet-700">Scan Doc</span> to auto-fill from a photo.
                  </p>
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
                      onClick={() => setActiveDocTab(key)}
                      className={`flex-1 flex items-center justify-center gap-1.5 py-3 text-xs font-bold transition-all cursor-pointer border-b-2 ${
                        activeDocTab === key
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
                  {activeDocTab === 'resident' && (
                    <InlineEditMuqeemCard
                      user={formData}
                      onChange={handleCardChange}
                    />
                  )}

                  {activeDocTab === 'license' && (
                    <InlineEditDrivingLicense
                      user={formData}
                      onChange={handleCardChange}
                    />
                  )}
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

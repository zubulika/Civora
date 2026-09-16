'use client';

import React, { useEffect, useState } from 'react';
import Link from 'next/link';
import Sidebar from '@/components/layout/Sidebar';
import TopHeader from '@/components/layout/TopHeader';
import StatsCard from '@/components/cards/StatsCard';
import MuqeemCardPreview from '@/components/cards/MuqeemCardPreview';
import { fetchCitizens } from '@/lib/firestoreService';
import { UserProfile } from '@/types';
import { 
  Users, 
  FileCheck2, 
  ShieldCheck, 
  ExternalLink, 
  ArrowRight,
  UserPlus,
  Eye,
  CheckCircle2,
  Clock
} from 'lucide-react';

export default function DashboardPage() {
  const [citizens, setCitizens] = useState<UserProfile[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedCitizen, setSelectedCitizen] = useState<UserProfile | null>(null);

  useEffect(() => {
    async function loadData() {
      try {
        const data = await fetchCitizens();
        setCitizens(data);
        if (data.length > 0) {
          setSelectedCitizen(data[0]);
        }
      } catch (e) {
        console.error('Error loading citizens:', e);
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, []);

  return (
    <div className="flex min-h-screen bg-[#f8faf9]">
      <Sidebar />

      <main className="flex-1 flex flex-col min-w-0">
        <TopHeader 
          title="Executive Operations Dashboard" 
          subtitle="Ministry of Interior • Absher Civil & Resident Document Authority"
        />

        <div className="p-8 space-y-8 max-w-7xl">
          {/* Stats Overview */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
            <StatsCard
              title="Registered Citizens"
              value={loading ? '...' : citizens.length}
              subtitle="Provisioned through Admin"
              icon={Users}
              accent="emerald"
            />
            <StatsCard
              title="Digital Documents"
              value={loading ? '...' : citizens.length * 3}
              subtitle="Muqeem, Licenses, Passports"
              icon={FileCheck2}
              accent="blue"
            />
            <StatsCard
              title="Verified Rate"
              value="100%"
              subtitle="All profiles locked & signed"
              icon={ShieldCheck}
              accent="emerald"
            />
            <StatsCard
              title="Cloud Database"
              value="Active"
              subtitle="Firestore: civora-app-433214"
              icon={CheckCircle2}
              accent="amber"
            />
          </div>

          {/* Quick Banner & Live Preview */}
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
            {/* Left 7 Cols: Quick Actions & Recent Citizens */}
            <div className="lg:col-span-7 space-y-6">
              {/* Promotion / Quick Issue Banner */}
              <div className="bg-gradient-to-r from-[#0c3d2e] to-[#056839] rounded-2xl p-6 text-white shadow-lg shadow-emerald-950/20 relative overflow-hidden">
                <div className="relative z-10 max-w-lg">
                  <span className="text-emerald-300 font-semibold text-xs tracking-wider uppercase mb-1 block">
                    Administrative Action
                  </span>
                  <h2 className="text-xl font-bold mb-2">Issue New Saudi Resident Identity</h2>
                  <p className="text-xs text-emerald-100/80 mb-5 leading-relaxed">
                    Create a new citizen account with bilingual Arabic/English credentials. The digital document will instantly appear in the citizen's mobile app.
                  </p>
                  <Link
                    href="/users/new"
                    className="inline-flex items-center gap-2 bg-white hover:bg-emerald-50 text-[#056839] px-5 py-2.5 rounded-xl text-xs font-bold transition-all shadow-sm"
                  >
                    <UserPlus className="w-4 h-4" />
                    <span>Open ID Issuance Form</span>
                    <ArrowRight className="w-3.5 h-3.5 ml-1" />
                  </Link>
                </div>
              </div>

              {/* Citizen Directory Snippet */}
              <div className="bg-white rounded-2xl border border-gray-200/90 shadow-xs overflow-hidden">
                <div className="p-5 border-b border-gray-100 flex items-center justify-between">
                  <div>
                    <h3 className="font-bold text-gray-900 text-sm">Recently Managed Citizens</h3>
                    <p className="text-xs text-gray-500">Click any row to inspect digital document</p>
                  </div>
                  <Link
                    href="/users"
                    className="text-xs font-semibold text-emerald-700 hover:text-emerald-800 flex items-center gap-1"
                  >
                    <span>View Directory</span>
                    <ArrowRight className="w-3.5 h-3.5" />
                  </Link>
                </div>

                <div className="divide-y divide-gray-100">
                  {loading ? (
                    <div className="p-6 text-center text-xs text-gray-400">Loading citizens...</div>
                  ) : citizens.length === 0 ? (
                    <div className="p-6 text-center text-xs text-gray-400">No citizens provisioned yet.</div>
                  ) : (
                    citizens.slice(0, 5).map((citizen) => {
                      const isSelected = selectedCitizen?.id === citizen.id;
                      return (
                        <div
                          key={citizen.id}
                          onClick={() => setSelectedCitizen(citizen)}
                          className={`p-4 flex items-center justify-between cursor-pointer transition-colors ${
                            isSelected ? 'bg-emerald-50/60' : 'hover:bg-gray-50'
                          }`}
                        >
                          <div className="flex items-center gap-3">
                            <div className="w-10 h-10 rounded-full border border-gray-200 overflow-hidden bg-gray-100 shrink-0">
                              <img
                                src={citizen.photoUrl || '/avatar_placeholder.png'}
                                alt={citizen.fullNameEn}
                                className="w-full h-full object-cover"
                                onError={(e) => {
                                  (e.target as HTMLImageElement).src = 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&auto=format&fit=crop&q=80';
                                }}
                              />
                            </div>
                            <div>
                              <div className="font-bold text-gray-900 text-xs">{citizen.fullNameAr}</div>
                              <div className="text-[11px] font-medium text-gray-600 uppercase">{citizen.fullNameEn}</div>
                              <div className="text-[10px] text-gray-400 font-mono">Iqama: {citizen.nationalId}</div>
                            </div>
                          </div>

                          <div className="flex items-center gap-2">
                            <span className="text-[10px] px-2 py-0.5 rounded-full font-semibold bg-emerald-100 text-emerald-800 border border-emerald-200">
                              Verified
                            </span>
                            <Link
                              href={`/users/${citizen.id}`}
                              className="text-xs text-gray-500 hover:text-emerald-700 p-1.5 rounded-lg hover:bg-gray-100"
                              title="Edit Details"
                              onClick={(e) => e.stopPropagation()}
                            >
                              <ExternalLink className="w-4 h-4" />
                            </Link>
                          </div>
                        </div>
                      );
                    })
                  )}
                </div>
              </div>
            </div>

            {/* Right 5 Cols: Live Card Preview of Selected Citizen */}
            <div className="lg:col-span-5 space-y-4">
              <div className="bg-white p-6 rounded-2xl border border-gray-200/90 shadow-xs">
                <div className="flex items-center justify-between pb-4 mb-4 border-b border-gray-100">
                  <div>
                    <h3 className="font-bold text-gray-900 text-sm">Live Mobile App Digital Card</h3>
                    <p className="text-xs text-gray-500">Exact rendering as shown on resident's phone</p>
                  </div>
                  {selectedCitizen && (
                    <Link
                      href={`/users/${selectedCitizen.id}`}
                      className="text-xs font-semibold text-emerald-700 hover:underline"
                    >
                      Edit Citizen
                    </Link>
                  )}
                </div>

                {selectedCitizen ? (
                  <MuqeemCardPreview user={selectedCitizen} />
                ) : (
                  <div className="py-12 text-center text-xs text-gray-400">
                    Select a citizen to preview their official card.
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

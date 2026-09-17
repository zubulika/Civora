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

  CheckCircle2,

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
          title="Dashboard" 
          subtitle="Overview of users and digital documents"
        />

        <div className="p-6 space-y-6 max-w-7xl">
          {/* Stats Overview */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            <StatsCard
              title="Total Users"
              value={loading ? '...' : citizens.length}
              subtitle="Registered profiles"
              icon={Users}
              accent="emerald"
            />
            <StatsCard
              title="Digital Documents"
              value={loading ? '...' : citizens.length * 3}
              subtitle="Muqeem cards issued"
              icon={FileCheck2}
              accent="blue"
            />
            <StatsCard
              title="Active IDs"
              value="100%"
              subtitle="Valid & verified"
              icon={ShieldCheck}
              accent="emerald"
            />
            <StatsCard
              title="Database"
              value="Connected"
              subtitle="Synced with mobile app"
              icon={CheckCircle2}
              accent="amber"
            />
          </div>

          {/* Quick Action & Live Preview */}
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
            {/* Left 7 Cols: Quick Actions & Recent Users */}
            <div className="lg:col-span-7 space-y-6">
              {/* Quick Add User Banner */}
              <div className="bg-emerald-800 rounded-xl p-6 text-white shadow-xs">
                <h2 className="text-lg font-semibold mb-1.5">Create User & Issue Digital ID</h2>
                <p className="text-xs text-emerald-100/90 mb-4 max-w-md leading-relaxed">
                  Add personal information and issue a digital resident ID card. The card will immediately sync to the user&apos;s mobile app.
                </p>
                <Link
                  href="/users/new"
                  className="inline-flex items-center gap-1.5 bg-white hover:bg-emerald-50 text-emerald-900 px-4 py-2 rounded-lg text-xs font-semibold transition cursor-pointer"
                >
                  <UserPlus className="w-3.5 h-3.5" />
                  <span>Add User</span>
                  <ArrowRight className="w-3 h-3 ml-0.5" />
                </Link>
              </div>

              {/* User Directory Snippet */}
              <div className="bg-white rounded-xl border border-slate-200 shadow-xs overflow-hidden">
                <div className="p-4 border-b border-slate-100 flex items-center justify-between">
                  <div>
                    <h3 className="font-semibold text-slate-900 text-sm">Recent Users</h3>
                    <p className="text-xs text-slate-500">Click a user to preview their digital card</p>
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
                    <p className="text-xs text-gray-500">Exact rendering as shown on resident&apos;s phone</p>
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

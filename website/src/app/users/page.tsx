'use client';

import React, { useCallback, useEffect, useState } from 'react';
import Link from 'next/link';
import Sidebar from '@/components/layout/Sidebar';
import TopHeader from '@/components/layout/TopHeader';
import { fetchCitizens, removeCitizen } from '@/lib/firestoreService';
import { UserProfile } from '@/types';
import { 
  UserPlus, 
  Search, 
  Trash2, 
  Edit3, 
  ShieldCheck, 
  Eye,
  Filter,
  Car,
  CreditCard
} from 'lucide-react';
import MuqeemCardPreview from '@/components/cards/MuqeemCardPreview';
import DrivingLicensePreview from '@/components/cards/DrivingLicensePreview';

export default function UsersDirectoryPage() {
  const [citizens, setCitizens] = useState<UserProfile[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedNationality, setSelectedNationality] = useState('ALL');
  const [previewCitizen, setPreviewCitizen] = useState<UserProfile | null>(null);
  const [modalCardTab, setModalCardTab] = useState<'license' | 'resident'>('license');

  const loadCitizens = useCallback(async () => {
    setLoading(true);
    try {
      const data = await fetchCitizens();
      setCitizens(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    const task = window.setTimeout(() => {
      void loadCitizens();
    }, 0);
    return () => window.clearTimeout(task);
  }, [loadCitizens]);

  const handleDelete = async (id: string, name: string) => {
    if (confirm(`Are you sure you want to remove ${name} from Absher?`)) {
      await removeCitizen(id);
      loadCitizens();
    }
  };

  // Filter logic
  const filtered = citizens.filter((c) => {
    const matchesSearch = 
      c.nationalId.includes(searchQuery) ||
      c.fullNameEn.toLowerCase().includes(searchQuery.toLowerCase()) ||
      c.fullNameAr.includes(searchQuery) ||
      c.sponsorName.includes(searchQuery);

    const matchesNat = selectedNationality === 'ALL' || c.nationality === selectedNationality;
    return matchesSearch && matchesNat;
  });

  const nationalities = Array.from(new Set(citizens.map((c) => c.nationality)));

  return (
    <div className="flex min-h-screen bg-[#f8faf9]">
      <Sidebar />

      <main className="flex-1 flex flex-col min-w-0">
        <TopHeader
          title="Users"
          subtitle="Manage registered citizen and resident profiles"
          onSearch={setSearchQuery}
        />

        <div className="p-6 space-y-6 max-w-7xl">
          {/* Controls Bar */}
          <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-xs flex flex-wrap items-center justify-between gap-4">
            <div className="flex items-center gap-3">
              <div className="relative w-64">
                <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
                <input
                  type="text"
                  placeholder="Filter by ID or name..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="w-full pl-9 pr-3 py-1.5 bg-slate-50 border border-slate-200 rounded-lg text-xs text-slate-900 focus:outline-none focus:ring-2 focus:ring-emerald-600/20 focus:border-emerald-700"
                />
              </div>

              {/* Nationality dropdown */}
              <div className="flex items-center gap-1.5 text-xs text-slate-600">
                <Filter className="w-3.5 h-3.5 text-slate-400" />
                <select
                  value={selectedNationality}
                  onChange={(e) => setSelectedNationality(e.target.value)}
                  className="bg-slate-50 border border-slate-200 rounded-lg px-2.5 py-1.5 text-xs focus:outline-none focus:ring-2 focus:ring-emerald-600/20"
                >
                  <option value="ALL">All Nationalities</option>
                  {nationalities.map((nat) => (
                    <option key={nat} value={nat}>{nat}</option>
                  ))}
                </select>
              </div>
            </div>

            <Link
              href="/users/new"
              className="inline-flex items-center gap-1.5 bg-emerald-700 hover:bg-emerald-800 text-white px-3.5 py-1.5 rounded-lg text-xs font-medium shadow-xs transition cursor-pointer"
            >
              <UserPlus className="w-3.5 h-3.5" />
              <span>Add User</span>
            </Link>
          </div>

          {/* Directory Table */}
          <div className="bg-white rounded-2xl border border-gray-200/90 shadow-xs overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="bg-gray-50/80 border-b border-gray-200/80 text-[11px] font-semibold text-gray-500 uppercase tracking-wider">
                    <th className="py-3.5 px-5">Resident / Citizen</th>
                    <th className="py-3.5 px-4">Iqama / National ID</th>
                    <th className="py-3.5 px-4">Profession & Employer</th>
                    <th className="py-3.5 px-4">Nationality</th>
                    <th className="py-3.5 px-4">Expiry Date</th>
                    <th className="py-3.5 px-4">Status</th>
                    <th className="py-3.5 px-5 text-right">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100 text-xs">
                  {loading ? (
                    <tr>
                      <td colSpan={7} className="py-12 text-center text-gray-400">Loading citizen records...</td>
                    </tr>
                  ) : filtered.length === 0 ? (
                    <tr>
                      <td colSpan={7} className="py-12 text-center text-gray-400">No citizens found matching criteria.</td>
                    </tr>
                  ) : (
                    filtered.map((c) => (
                      <tr key={c.id} className="hover:bg-emerald-50/40 transition-colors">
                        {/* Name & Photo */}
                        <td className="py-4 px-5">
                          <div className="flex items-center gap-3">
                            <div className="w-10 h-10 rounded-full border border-gray-200 overflow-hidden bg-gray-100 shrink-0">
                              <img
                                src={c.photoUrl || '/avatar_placeholder.png'}
                                alt={c.fullNameEn}
                                className="w-full h-full object-cover"
                                onError={(e) => {
                                  (e.target as HTMLImageElement).src = 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&auto=format&fit=crop&q=80';
                                }}
                              />
                            </div>
                            <div>
                              <div className="font-bold text-gray-900">{c.fullNameAr}</div>
                              <div className="text-[11px] font-medium text-gray-500 uppercase">{c.fullNameEn}</div>
                            </div>
                          </div>
                        </td>

                        {/* National ID & App Credentials */}
                        <td className="py-4 px-4">
                          <div className="font-mono font-bold text-gray-800">{c.nationalId}</div>
                          <div className="text-[10px] text-gray-500 font-mono mt-0.5 flex items-center gap-1">
                            <span className="text-gray-400">pass:</span>
                            <span className="font-semibold text-slate-700">{c.appPassword || 'Civora2026!'}</span>
                          </div>
                        </td>

                        {/* Profession & Sponsor */}
                        <td className="py-4 px-4">
                          <div className="font-medium text-gray-900">{c.professionAr}</div>
                          <div className="text-[11px] text-gray-500 truncate max-w-[200px]">{c.sponsorName}</div>
                        </td>

                        {/* Nationality */}
                        <td className="py-4 px-4 font-medium text-gray-700">
                          {c.nationality}
                        </td>

                        {/* Expiry Date */}
                        <td className="py-4 px-4 font-mono text-gray-600">
                          {c.expiryDateEn}
                        </td>

                        {/* Verification & Mobile App Status */}
                        <td className="py-4 px-4">
                          <div className="flex flex-col gap-1">
                            <span className="inline-flex items-center gap-1 text-[10px] font-bold px-2 py-0.5 rounded-full bg-emerald-50 text-emerald-800 border border-emerald-200 w-fit">
                              <ShieldCheck className="w-3 h-3 text-emerald-600" />
                              Verified
                            </span>
                            {c.accountStatus === 'SUSPENDED' ? (
                              <span className="inline-flex items-center text-[10px] font-semibold px-2 py-0.5 rounded-full bg-rose-50 text-rose-700 border border-rose-200 w-fit">
                                App Blocked
                              </span>
                            ) : (
                              <span className="inline-flex items-center text-[10px] font-semibold px-2 py-0.5 rounded-full bg-emerald-50 text-emerald-700 border border-emerald-200 w-fit">
                                App Active
                              </span>
                            )}
                          </div>
                        </td>

                        {/* Actions */}
                        <td className="py-4 px-5 text-right">
                          <div className="flex items-center justify-end gap-2">
                            <button
                              onClick={() => setPreviewCitizen(c)}
                              title="Live Card Preview"
                              className="p-1.5 text-gray-500 hover:text-emerald-700 hover:bg-emerald-100/50 rounded-lg transition-colors"
                            >
                              <Eye className="w-4 h-4" />
                            </button>
                            <Link
                              href={`/users/${c.id}`}
                              title="Edit Resident Profile"
                              className="p-1.5 text-gray-500 hover:text-emerald-700 hover:bg-emerald-100/50 rounded-lg transition-colors"
                            >
                              <Edit3 className="w-4 h-4" />
                            </Link>
                            <button
                              onClick={() => handleDelete(c.id, c.fullNameEn)}
                              title="Remove Citizen"
                              className="p-1.5 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                            >
                              <Trash2 className="w-4 h-4" />
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>

        {/* Modal for Live Card Preview */}
        {previewCitizen && (
          <div className="fixed inset-0 z-50 bg-black/50 backdrop-blur-xs flex items-center justify-center p-4">
            <div className="bg-white rounded-3xl p-6 max-w-2xl w-full shadow-2xl border border-gray-200">
              <div className="flex items-center justify-between pb-4 mb-4 border-b border-gray-100">
                <div>
                  <h3 className="font-bold text-gray-900 text-base">Digital Document Verification</h3>
                  <p className="text-xs text-gray-500">{previewCitizen.fullNameEn} • {previewCitizen.nationalId}</p>
                </div>
                <button
                  onClick={() => setPreviewCitizen(null)}
                  className="w-8 h-8 rounded-full bg-gray-100 hover:bg-gray-200 flex items-center justify-center text-gray-600 font-bold"
                >
                  ✕
                </button>
              </div>

              {/* Modal Card Template Switcher */}
              <div className="flex bg-slate-100 p-1 rounded-xl mb-4 border border-slate-200">
                <button
                  type="button"
                  onClick={() => setModalCardTab('license')}
                  className={`flex-1 flex items-center justify-center gap-1.5 py-1.5 text-xs font-bold rounded-lg transition-all cursor-pointer ${
                    modalCardTab === 'license'
                      ? 'bg-white text-emerald-800 shadow-2xs border border-slate-200/80'
                      : 'text-slate-500 hover:text-slate-900'
                  }`}
                >
                  <Car className="w-3.5 h-3.5" />
                  <span>Driving License (رخصة القيادة)</span>
                </button>
                <button
                  type="button"
                  onClick={() => setModalCardTab('resident')}
                  className={`flex-1 flex items-center justify-center gap-1.5 py-1.5 text-xs font-bold rounded-lg transition-all cursor-pointer ${
                    modalCardTab === 'resident'
                      ? 'bg-white text-emerald-800 shadow-2xs border border-slate-200/80'
                      : 'text-slate-500 hover:text-slate-900'
                  }`}
                >
                  <CreditCard className="w-3.5 h-3.5" />
                  <span>Resident ID (هوية مقيم)</span>
                </button>
              </div>

              <div className="py-2">
                {modalCardTab === 'license' ? (
                  <DrivingLicensePreview user={previewCitizen} />
                ) : (
                  <MuqeemCardPreview user={previewCitizen} />
                )}
              </div>

              <div className="flex justify-end gap-3 mt-6 pt-4 border-t border-gray-100">
                <Link
                  href={`/users/${previewCitizen.id}`}
                  className="px-5 py-2 bg-emerald-700 text-white text-xs font-semibold rounded-xl hover:bg-emerald-800"
                >
                  Edit Information
                </Link>
                <button
                  onClick={() => setPreviewCitizen(null)}
                  className="px-5 py-2 bg-gray-100 text-gray-700 text-xs font-semibold rounded-xl hover:bg-gray-200"
                >
                  Close
                </button>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}

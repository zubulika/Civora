'use client';

import React, { useEffect, useState } from 'react';
import Link from 'next/link';
import Sidebar from '@/components/layout/Sidebar';
import TopHeader from '@/components/layout/TopHeader';
import { fetchDocuments, fetchCitizens } from '@/lib/firestoreService';
import { DigitalDocument, UserProfile } from '@/types';
import { 
  FileText, 
  ShieldCheck, 
  QrCode, 
  ExternalLink,
  Building2
} from 'lucide-react';
import MuqeemCardPreview from '@/components/cards/MuqeemCardPreview';
import DrivingLicensePreview from '@/components/cards/DrivingLicensePreview';

export default function DocumentsPage() {
  const [documents, setDocuments] = useState<DigitalDocument[]>([]);
  const [citizens, setCitizens] = useState<UserProfile[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [previewCitizen, setPreviewCitizen] = useState<{user: UserProfile, doc: DigitalDocument} | null>(null);

  useEffect(() => {
    async function load() {
      try {
        const [docs, users] = await Promise.all([fetchDocuments(), fetchCitizens()]);
        setDocuments(docs);
        setCitizens(users);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    }
    load();
  }, []);

  const filtered = documents.filter(d => 
    d.documentNumber.includes(searchQuery) ||
    d.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
    d.titleAr.includes(searchQuery)
  );

  return (
    <div className="flex min-h-screen bg-[#f8faf9]">
      <Sidebar />

      <main className="flex-1 flex flex-col min-w-0">
        <TopHeader
          title="Documents"
          subtitle="All digital documents issued to users"
          onSearch={setSearchQuery}
        />

        <div className="p-8 space-y-6 max-w-7xl">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
            <div className="bg-white p-5 rounded-2xl border border-gray-200/90 shadow-xs flex items-center gap-4">
              <div className="w-12 h-12 rounded-xl bg-emerald-50 border border-emerald-100 flex items-center justify-center text-emerald-700">
                <FileText className="w-6 h-6" />
              </div>
              <div>
                <div className="text-xs text-gray-500 font-semibold">Active Residence Permits</div>
                <div className="text-2xl font-bold text-gray-900 font-mono">{citizens.length}</div>
              </div>
            </div>

            <div className="bg-white p-5 rounded-2xl border border-gray-200/90 shadow-xs flex items-center gap-4">
              <div className="w-12 h-12 rounded-xl bg-blue-50 border border-blue-100 flex items-center justify-center text-blue-700">
                <ShieldCheck className="w-6 h-6" />
              </div>
              <div>
                <div className="text-xs text-gray-500 font-semibold">Security QR Matrices</div>
                <div className="text-2xl font-bold text-gray-900 font-mono">{citizens.length}</div>
              </div>
            </div>

            <div className="bg-white p-5 rounded-2xl border border-gray-200/90 shadow-xs flex items-center gap-4">
              <div className="w-12 h-12 rounded-xl bg-amber-50 border border-amber-100 flex items-center justify-center text-amber-700">
                <Building2 className="w-6 h-6" />
              </div>
              <div>
                <div className="text-xs text-gray-500 font-semibold">Issuing Authority</div>
                <div className="text-xs font-bold text-gray-800">Ministry of Interior (MOI)</div>
              </div>
            </div>
          </div>

          {/* Documents Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {loading ? (
              <div className="md:col-span-2 lg:col-span-3 bg-white rounded-2xl border border-gray-200/90 p-12 text-center text-xs text-gray-400">
                Loading digital documents...
              </div>
            ) : filtered.length === 0 ? (
              <div className="md:col-span-2 lg:col-span-3 bg-white rounded-2xl border border-gray-200/90 p-12 text-center text-xs text-gray-400">
                No digital documents match your search.
              </div>
            ) : filtered.map((document) => {
              const citizen = citizens.find((user) => user.id === document.userId || user.nationalId === document.documentNumber);
              if (!citizen) return null;

              return (
              <div 
                key={citizen.id}
                className="bg-white rounded-2xl border border-gray-200/90 shadow-xs p-6 hover:shadow-md transition-shadow flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-start justify-between mb-4">
                    {document.type === 'DRIVING_LICENSE' ? (
                      <span className="text-[10px] font-bold px-2.5 py-1 rounded-full bg-blue-50 text-blue-800 border border-blue-200">
                        رخصة قيادة • DRIVING LICENSE
                      </span>
                    ) : (
                      <span className="text-[10px] font-bold px-2.5 py-1 rounded-full bg-emerald-50 text-emerald-800 border border-emerald-200">
                        هوية مقيم • RESIDENT ID
                      </span>
                    )}
                    <span className="text-[11px] font-mono text-gray-400">Ver. {citizen.versionNumber}</span>
                  </div>

                  <div className="flex items-center gap-3 mb-4">
                    <div className="w-12 h-12 rounded-lg border border-gray-200 overflow-hidden bg-gray-100 shrink-0">
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
                      <div className="font-bold text-sm text-gray-900 leading-tight">{citizen.fullNameAr}</div>
                      <div className="text-xs font-semibold text-gray-600 uppercase">{citizen.fullNameEn}</div>
                      <div className="text-xs font-mono font-bold text-emerald-800 mt-0.5">
                        {document.type === 'DRIVING_LICENSE' ? `License ID: ${citizen.nationalId}` : `Iqama: ${citizen.nationalId}`}
                      </div>
                    </div>
                  </div>

                  <div className="text-xs text-gray-600 space-y-1 py-3 border-y border-gray-100">
                    <div className="flex justify-between">
                      <span className="text-gray-400">Profession:</span>
                      <span className="font-medium text-gray-800">{citizen.professionAr}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-400">Sponsor ID:</span>
                      <span className="font-mono text-gray-800">{citizen.sponsorId}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-400">Expiry Date:</span>
                      <span className="font-semibold text-emerald-700">
                        {document.type === 'DRIVING_LICENSE' ? citizen.licenseExpiryDateEn : citizen.expiryDateEn}
                      </span>
                    </div>
                  </div>
                </div>

                <div className="flex items-center justify-between pt-4 mt-2">
                  <button
                    onClick={() => setPreviewCitizen({user: citizen, doc: document})}
                    className="flex items-center gap-1.5 text-xs font-semibold text-emerald-700 hover:text-emerald-800 hover:underline"
                  >
                    <QrCode className="w-3.5 h-3.5" />
                    <span>Preview Card & QR</span>
                  </button>
                  <Link
                    href={`/users/${citizen.id}`}
                    className="p-1.5 text-gray-400 hover:text-emerald-700 hover:bg-emerald-50 rounded-lg transition-colors"
                    title="Edit Information"
                  >
                    <ExternalLink className="w-4 h-4" />
                  </Link>
                </div>
              </div>
              );
            })}
          </div>
        </div>

        {/* Modal for Live Card Preview */}
        {previewCitizen && (
          <div className="fixed inset-0 z-50 bg-black/50 backdrop-blur-xs flex items-center justify-center p-4">
            <div className="bg-white rounded-3xl p-6 max-w-2xl w-full shadow-2xl border border-gray-200">
              <div className="flex items-center justify-between pb-4 mb-4 border-b border-gray-100">
                <div>
                  <h3 className="font-bold text-gray-900 text-base">Digital Document Verification</h3>
                  <p className="text-xs text-gray-500">{previewCitizen.user.fullNameEn} • {previewCitizen.user.nationalId}</p>
                </div>
                <button
                  onClick={() => setPreviewCitizen(null)}
                  className="w-8 h-8 rounded-full bg-gray-100 hover:bg-gray-200 flex items-center justify-center text-gray-600 font-bold"
                >
                  ✕
                </button>
              </div>

              <div className="py-2 flex justify-center">
                {previewCitizen.doc.type === 'DRIVING_LICENSE' ? (
                  <DrivingLicensePreview user={previewCitizen.user} />
                ) : (
                  <MuqeemCardPreview user={previewCitizen.user} />
                )}
              </div>

              <div className="flex justify-end gap-3 mt-6 pt-4 border-t border-gray-100">
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

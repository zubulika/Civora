import { db } from './firebase';
import { 
  collection, 
  doc, 
  getDocs, 
  getDoc, 
  setDoc, 

  deleteDoc, 
  serverTimestamp 
} from 'firebase/firestore';
import { UserProfile, DigitalDocument } from '@/types';
import { INITIAL_USERS, INITIAL_DOCUMENTS } from './mockData';

const LOCAL_STORAGE_USERS_KEY = 'absher_admin_users';


// Helper to get local fallback state
function getLocalUsers(): UserProfile[] {
  if (typeof window === 'undefined') return INITIAL_USERS;
  try {
    const data = localStorage.getItem(LOCAL_STORAGE_USERS_KEY);
    if (data) return JSON.parse(data);
  } catch (e) {
    console.error('Error reading localStorage users:', e);
  }
  return INITIAL_USERS;
}

function saveLocalUsers(users: UserProfile[]) {
  if (typeof window === 'undefined') return;
  try {
    localStorage.setItem(LOCAL_STORAGE_USERS_KEY, JSON.stringify(users));
  } catch (e) {
    console.error('Error writing localStorage users:', e);
  }
}

export async function fetchCitizens(): Promise<UserProfile[]> {
  try {
    const usersCol = collection(db, 'users');
    const snapshot = await getDocs(usersCol);
    if (!snapshot.empty) {
      const users: UserProfile[] = [];
      snapshot.forEach(docSnap => {
        const data = docSnap.data();
        users.push({
          id: docSnap.id,
          nationalId: data.nationalId || '',
          fullNameEn: data.fullNameEn || '',
          fullNameAr: data.fullNameAr || '',
          dateOfBirth: data.dateOfBirth || '1988/02/03',
          dateOfBirthAr: data.dateOfBirthAr || '١٩٨٨/٠٢/٠٣',
          dateOfBirthHijri: data.dateOfBirthHijri || '1408/10/18',
          nationality: data.nationality || 'Bangladesh',
          nationalityAr: data.nationalityAr || 'بنجلاديش',
          placeOfBirthEn: data.placeOfBirthEn || 'Bangladesh',
          placeOfBirthAr: data.placeOfBirthAr || 'بنجلاديش',
          religionEn: data.religionEn || 'Islam',
          religionAr: data.religionAr || 'الاسلام',
          professionEn: data.professionEn || 'Worker',
          professionAr: data.professionAr || 'عامل',
          sponsorId: data.sponsorId || '',
          sponsorNameEn: data.sponsorNameEn || '',
          sponsorName: data.sponsorName || '',
          issuePlaceEn: data.issuePlaceEn || '',
          issuePlace: data.issuePlace || '',
          workPlaceAr: data.workPlaceAr || 'منطقة الرياض',
          expiryDateEn: data.expiryDateEn || '2026/10/08',
          expiryDateAr: data.expiryDateAr || '٢٠٢٦/١٠/٠٨',
          versionNumber: data.versionNumber || '٢',
          expiryDateDigits: data.expiryDateDigits || '081026',
          issueDateDigits: data.issueDateDigits || '070926',
          photoUrl: data.photoUrl || '',
          licenseTypeEn: data.licenseTypeEn || '',
          licenseTypeAr: data.licenseTypeAr || '',
          licenseIssueDateEn: data.licenseIssueDateEn || '',
          licenseIssueDateAr: data.licenseIssueDateAr || '',
          licenseExpiryDateEn: data.licenseExpiryDateEn || '',
          licenseExpiryDateAr: data.licenseExpiryDateAr || '',
          bloodType: data.bloodType || '',
          residentIdIssuingDate: data.residentIdIssuingDate || '',
          visaNumber: data.visaNumber || '',
          visaType: data.visaType || '',
          visaExitDate: data.visaExitDate || '',
          birthCity: data.birthCity || '-',
          birthCountry: data.birthCountry || 'Bangladesh',
          maritalStatus: data.maritalStatus || 'SINGLE',
          sponsorshipTransfers: data.sponsorshipTransfers || '2',
          workPermit: data.workPermit || '-',
          biometricsCollected: data.biometricsCollected || 'Yes',
          travelStatus: data.travelStatus || 'Inside Kingdom',
          establishmentStatus: data.establishmentStatus || 'Active (Green)',
          insuranceCompany: data.insuranceCompany || 'Bupa Arabia',
          insurancePolicyNo: data.insurancePolicyNo || 'POL-9842144',
          insuranceStatus: data.insuranceStatus || 'Valid & Active',
          insuranceExpiry: data.insuranceExpiry || '-',
          insuranceIssuingDate: data.insuranceIssuingDate || '-',
          hajjEligibility: data.hajjEligibility || 'Not Eligible / Not Performed',
          lastHajjYear: data.lastHajjYear || '-',
          verificationLevel: data.verificationLevel || 'TIER_3_VERIFIED',
          appPassword: data.appPassword || 'Civora2026!',
          accountStatus: data.accountStatus || 'ACTIVE',
          digitalIdActive: data.digitalIdActive !== false,
          totalDocuments: data.totalDocuments || 4,
          activeRequestsCount: data.activeRequestsCount || 0,
          unreadNotificationsCount: data.unreadNotificationsCount || 0,
          createdAt: data.createdAt ? String(data.createdAt) : new Date().toISOString(),
          updatedAt: data.updatedAt ? String(data.updatedAt) : new Date().toISOString(),
        });
      });
      // Synchronize local cache with Firestore
      saveLocalUsers(users);
      return users;
    }
  } catch (err) {
    console.warn('Firestore fetch failed or offline; using synced local cache:', err);
  }

  // Fallback to local storage or initial mock
  return getLocalUsers();
}

export async function fetchCitizenById(id: string): Promise<UserProfile | null> {
  try {
    const userRef = doc(db, 'users', id);
    const snap = await getDoc(userRef);
    if (snap.exists()) {
      return { id: snap.id, ...(snap.data() as Partial<UserProfile>) } as UserProfile;
    }
  } catch (err) {
    console.warn('Firestore getDoc failed; checking local cache:', err);
  }

  const local = getLocalUsers();
  return local.find(u => u.id === id || u.nationalId === id) || null;
}

export async function saveCitizen(citizen: UserProfile): Promise<UserProfile> {
  const citizenWithTimestamp: UserProfile = {
    ...citizen,
    appPassword: citizen.appPassword || 'Civora2026!',
    accountStatus: citizen.accountStatus || 'ACTIVE',
    updatedAt: new Date().toISOString().split('T')[0],
    createdAt: citizen.createdAt || new Date().toISOString().split('T')[0]
  };

  // 1. Try to sync to Firestore
  try {
    const docId = citizen.id || `usr_${citizen.nationalId}`;
    const userRef = doc(db, 'users', docId);
    await setDoc(userRef, {
      ...citizenWithTimestamp,
      lastSyncedAt: serverTimestamp()
    }, { merge: true });
  } catch (err) {
    console.warn('Could not write directly to Firestore (saving locally):', err);
  }

  // 2. Persist in local storage
  const existing = getLocalUsers();
  const index = existing.findIndex(u => u.id === citizen.id || u.nationalId === citizen.nationalId);
  if (index >= 0) {
    existing[index] = citizenWithTimestamp;
  } else {
    existing.unshift(citizenWithTimestamp);
  }
  saveLocalUsers(existing);

  return citizenWithTimestamp;
}

export async function removeCitizen(id: string): Promise<void> {
  try {
    const userRef = doc(db, 'users', id);
    await deleteDoc(userRef);
  } catch (err) {
    console.warn('Could not delete from Firestore (removing locally):', err);
  }

  const existing = getLocalUsers().filter(u => u.id !== id && u.nationalId !== id);
  saveLocalUsers(existing);
}

export async function fetchDocuments(): Promise<DigitalDocument[]> {
  try {
    const documentsCol = collection(db, 'documents');
    const snapshot = await getDocs(documentsCol);
    if (!snapshot.empty) {
      return snapshot.docs.map((document) => ({
        id: document.id,
        ...(document.data() as Omit<DigitalDocument, 'id'>),
      }));
    }
  } catch (err) {
    console.warn('Firestore documents fetch failed; using local records:', err);
  }

  return INITIAL_DOCUMENTS;
}

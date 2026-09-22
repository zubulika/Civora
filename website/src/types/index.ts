export type VerificationLevel = 
  | 'TIER_1_BASIC'
  | 'TIER_2_INTERMEDIATE'
  | 'TIER_3_VERIFIED';

export type DocumentStatus = 'ACTIVE' | 'EXPIRED' | 'SUSPENDED' | 'RENEWAL_PENDING';

export interface UserProfile {
  id: string; // e.g. usr_992140 or nationalId
  nationalId: string; // 10-digit Saudi Iqama/National ID
  appPassword?: string; // Mobile app login password
  accountStatus?: 'ACTIVE' | 'SUSPENDED'; // Mobile app login authorization status
  fullNameEn: string;
  fullNameAr: string;
  dateOfBirth: string; // Gregorian YYYY/MM/DD
  dateOfBirthAr: string; // Arabic numerals
  dateOfBirthHijri: string; // e.g. 1408/10/18
  nationality: string;
  nationalityAr: string;
  placeOfBirthEn: string;
  placeOfBirthAr: string;
  religionEn: string;
  religionAr: string;
  professionEn: string;
  professionAr: string;
  sponsorId: string;
  sponsorNameEn: string;
  sponsorName: string;
  issuePlaceEn: string;
  issuePlace: string;
  workPlaceAr: string;
  expiryDateEn: string;
  expiryDateAr: string;
  versionNumber: string; // e.g. "٢" or "2"
  expiryDateDigits: string; // e.g. "081026"
  issueDateDigits: string; // e.g. "070926"
  photoUrl?: string;
  licenseTypeEn?: string;
  licenseTypeAr?: string;
  licenseIssueDateEn?: string;
  licenseIssueDateAr?: string;
  licenseExpiryDateEn?: string;
  licenseExpiryDateAr?: string;
  bloodType?: string;
  // Resident ID specific
  residentIdIssuingDate?: string;
  // Visa specific
  visaNumber?: string;
  visaType?: string;
  visaExitDate?: string;
  verificationLevel: VerificationLevel;
  digitalIdActive: boolean;
  totalDocuments: number;
  activeRequestsCount: number;
  unreadNotificationsCount: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface DigitalDocument {
  id: string;
  userId: string;
  type: 'NATIONAL_ID' | 'RESIDENCE_PERMIT' | 'PASSPORT' | 'DRIVING_LICENSE' | 'VEHICLE_REGISTRATION';
  title: string;
  titleAr: string;
  subtitle: string;
  documentNumber: string;
  issueDate: string;
  expiryDate: string;
  status: DocumentStatus;
  issuer: string;
  qrCodePayload: string;
  details: Record<string, string>;
}

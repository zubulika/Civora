import { db } from './firebase';
import { 
  collection, 
  doc, 
  getDocs, 
  getDoc, 
  setDoc, 

  serverTimestamp 
} from 'firebase/firestore';

export interface AdminUser {
  id: string;
  email: string;
  passwordHash: string;
  fullName: string;
  role: string;
  department: string;
  badgeNumber: string;
  isActive: boolean;
  createdAt: string;
}

export const MASTER_ADMIN_CREDENTIALS = {
  email: 'admin@absher.moi.gov.sa',
  password: 'AbsherAdmin#2026!',
  fullName: 'Abdullah Al-Otaibi',
  role: 'Admin',
  department: 'Civil Affairs',
  badgeNumber: 'ADM-01',
};

const LOCAL_ADMIN_USERS_KEY = 'absher_admin_users_list';
const LOCAL_ADMIN_SESSION_KEY = 'absher_active_admin_session';

function getLocalAdmins(): AdminUser[] {
  if (typeof window === 'undefined') return [];
  try {
    const raw = localStorage.getItem(LOCAL_ADMIN_USERS_KEY);
    if (raw) {
      const parsed = JSON.parse(raw);
      if (Array.isArray(parsed) && parsed.length > 0) return parsed;
    }
  } catch {}

  const defaultAdmin: AdminUser = {
    id: 'admin_001',
    email: MASTER_ADMIN_CREDENTIALS.email,
    passwordHash: MASTER_ADMIN_CREDENTIALS.password,
    fullName: MASTER_ADMIN_CREDENTIALS.fullName,
    role: MASTER_ADMIN_CREDENTIALS.role,
    department: MASTER_ADMIN_CREDENTIALS.department,
    badgeNumber: MASTER_ADMIN_CREDENTIALS.badgeNumber,
    isActive: true,
    createdAt: '2026-01-01',
  };

  if (typeof window !== 'undefined') {
    try {
      localStorage.setItem(LOCAL_ADMIN_USERS_KEY, JSON.stringify([defaultAdmin]));
    } catch {}
  }

  return [defaultAdmin];
}

export async function seedAdminToDatabase(): Promise<void> {
  try {
    const adminDocRef = doc(db, 'admin_users', 'admin_001');
    const snap = await getDoc(adminDocRef);

    if (!snap.exists()) {
      await setDoc(adminDocRef, {
        id: 'admin_001',
        email: MASTER_ADMIN_CREDENTIALS.email.toLowerCase(),
        passwordHash: MASTER_ADMIN_CREDENTIALS.password,
        fullName: MASTER_ADMIN_CREDENTIALS.fullName,
        role: MASTER_ADMIN_CREDENTIALS.role,
        department: MASTER_ADMIN_CREDENTIALS.department,
        badgeNumber: MASTER_ADMIN_CREDENTIALS.badgeNumber,
        isActive: true,
        createdAt: new Date().toISOString(),
        lastSyncedAt: serverTimestamp(),
      });
    }
  } catch {
    // Offline / fallback mode
  }
}

export async function authenticateAdmin(
  emailInput: string, 
  passwordInput: string
): Promise<{ success: boolean; user?: AdminUser; error?: string }> {
  const cleanEmail = emailInput.trim().toLowerCase();
  const cleanPassword = passwordInput.trim();

  if (!cleanEmail || !cleanPassword) {
    return { success: false, error: 'Email and password are required.' };
  }

  // 1. Try Firestore database
  try {
    const adminCol = collection(db, 'admin_users');
    const snapshot = await getDocs(adminCol);

    if (!snapshot.empty) {
      let found: AdminUser | null = null;
      snapshot.forEach(docSnap => {
        const data = docSnap.data();
        if (data.email && data.email.toLowerCase() === cleanEmail) {
          found = {
            id: docSnap.id,
            email: data.email,
            passwordHash: data.passwordHash || '',
            fullName: data.fullName || 'Admin',
            role: data.role || 'Admin',
            department: data.department || '',
            badgeNumber: data.badgeNumber || 'ADM-01',
            isActive: data.isActive !== false,
            createdAt: data.createdAt || '',
          };
        }
      });

      if (found) {
        const user = found as AdminUser;
        if (!user.isActive) {
          return { success: false, error: 'Account is deactivated.' };
        }
        if (user.passwordHash !== cleanPassword) {
          return { success: false, error: 'Incorrect email or password.' };
        }
        setAdminSession(user);
        return { success: true, user };
      }
    }
  } catch {}

  // 2. Check local database
  const localAdmins = getLocalAdmins();
  const match = localAdmins.find(a => a.email.toLowerCase() === cleanEmail);

  if (!match) {
    return { success: false, error: 'Incorrect email or password.' };
  }

  if (!match.isActive) {
    return { success: false, error: 'Account is deactivated.' };
  }

  if (match.passwordHash !== cleanPassword) {
    return { success: false, error: 'Incorrect email or password.' };
  }

  setAdminSession(match);
  seedAdminToDatabase().catch(() => {});

  return { success: true, user: match };
}

export function getAdminSession(): AdminUser | null {
  if (typeof window === 'undefined') return null;
  try {
    const session = localStorage.getItem(LOCAL_ADMIN_SESSION_KEY);
    if (session) return JSON.parse(session);
  } catch {}
  return null;
}

export function setAdminSession(user: AdminUser): void {
  if (typeof window === 'undefined') return;
  try {
    localStorage.setItem(LOCAL_ADMIN_SESSION_KEY, JSON.stringify(user));
  } catch {}
}

export function clearAdminSession(): void {
  if (typeof window === 'undefined') return;
  try {
    localStorage.removeItem(LOCAL_ADMIN_SESSION_KEY);
  } catch {}
}

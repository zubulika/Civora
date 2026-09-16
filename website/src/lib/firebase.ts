import { initializeApp, getApps, getApp } from 'firebase/app';
import { getFirestore, Firestore } from 'firebase/firestore';

const firebaseConfig = {
  apiKey: process.env.NEXT_PUBLIC_FIREBASE_API_KEY || "AIzaSyBJmsTgqmZcbAmWCOWfeO7Wh2MrsbWC9Vs",
  authDomain: "civora-app-433214.firebaseapp.com",
  projectId: "civora-app-433214",
  storageBucket: "civora-app-433214.firebasestorage.app",
  messagingSenderId: "330006742963",
  appId: "1:330006742963:web:9102ab3c4d5e6f7a8b9c0d"
};

const app = getApps().length > 0 ? getApp() : initializeApp(firebaseConfig);
const db: Firestore = getFirestore(app);

export { app, db };

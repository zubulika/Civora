import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";

const inter = Inter({
  subsets: ["latin"],
  variable: "--font-inter",
  display: "swap",
});

export const metadata: Metadata = {
  title: "Absher Admin Operations Portal | Ministry of Interior",
  description: "Official administrative management portal for Absher digital identities and residence permits",
};

import { AuthProvider } from "@/context/AuthContext";
import AuthGuard from "@/components/layout/AuthGuard";
import { ToastProvider } from "@/lib/toast";

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html
      lang="en"
      className={`${inter.variable} ${inter.className} h-full antialiased`}
      suppressHydrationWarning
    >
      <body className="min-h-full flex flex-col" suppressHydrationWarning>
        <ToastProvider>
          <AuthProvider>
            <AuthGuard>{children}</AuthGuard>
          </AuthProvider>
        </ToastProvider>
      </body>
    </html>
  );
}

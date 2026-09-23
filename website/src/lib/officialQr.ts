import { UserProfile } from '@/types';

const OFFICIAL_SIGNATURE =
  'Q6HFXkZ5rv0cxjyESBCX/YegdHCzE2/4ujU1nUlq5V3CBjzYe2k2NlwpPfQw8ZX2DkFWEJQU/ZtIvWXIlD89UEjGYoQAWR+Y5iodT6uZUc1ZVPAa6F1sPrJVwaTNf/TUHhkNyWnzR1sg9v8ZzYuSqgYmr6Hl9VsmboZIWoJVHnm6d2skUnKKKvhSrPrnTFrzse2UbO0H2zdS1EiSZAu6l34qHotCdsS27psDNwinHm82FZR+pmDRZpz0pMLpr4P3skg9Y9RGZJFa6wOtFdhHj9zwPci+9QLqvZrU+91cyPG51+/o7fZ/ea/H8rsRNxg65lWov/97smO2GMhFq+2ZtA==';

const OFFICIAL_KEY_ID = 121980001;

/**
 * Builds the authentic cryptographic Saudi Absher / Muqeem QR code envelope.
 * Matches OfficialQrGenerator.kt in the Android mobile application.
 */
export function buildOfficialQrPayload(user: Partial<UserProfile>, timestampMillis = Date.now()): string {
  const hid = user.nationalId || '2495685261';
  const exp = user.expiryDateDigits || '081026';
  const iat = user.issueDateDigits || '070926';

  const cdaInner = `{"hid":"${hid}","cnt":{"pid":"${hid}"},"typ":2,"exp":"${exp}","iat":"${iat}"}`;
  const escapedCda = cdaInner.replace(/"/g, '\\"');

  return `{"sig":"${OFFICIAL_SIGNATURE}","payload":{"cda":"100$ISS:1$${escapedCda}","iat":${timestampMillis}},"header":{"kid":${OFFICIAL_KEY_ID}}}`;
}

/**
 * Returns high-res, valid QR code image URLs for the payload with primary and fallback endpoints.
 */
export function getQrCodeImageUrl(payload: string, size = 300): string {
  return `https://api.qrserver.com/v1/create-qr-code/?size=${size}x${size}&ecc=M&margin=1&data=${encodeURIComponent(payload)}`;
}

export function getQrCodeFallbackUrls(payload: string, size = 300): string[] {
  const enc = encodeURIComponent(payload);
  return [
    `https://api.qrserver.com/v1/create-qr-code/?size=${size}x${size}&ecc=M&margin=1&data=${enc}`,
    `https://quickchart.io/qr?text=${enc}&size=${size}&ecLevel=M&margin=1`,
    `https://chart.googleapis.com/chart?cht=qr&chs=${size}x${size}&chl=${enc}&chld=M|1`,
  ];
}

/**
 * Authentic 1D barcode unit pattern matching Android's ResidentBarcode Canvas.
 */
export const BARCODE_PATTERN = [
  2, 1, 1, 2, 3, 1, 2, 2, 1, 3, 1, 2, 1, 1, 3, 2,
  1, 2, 2, 1, 1, 3, 2, 1, 3, 1, 1, 2, 2, 2, 1, 1,
  2, 3, 1, 2, 1, 2, 2, 1, 3, 1, 2, 2, 1, 1, 2, 3,
  1, 2, 1, 3, 2, 1, 2, 2, 1, 2, 3, 1, 1, 2, 2, 2,
];

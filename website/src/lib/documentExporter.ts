import { UserProfile } from '@/types';
import { buildOfficialQrPayload, getQrCodeFallbackUrls, BARCODE_PATTERN } from './officialQr';

export const toArabicNumerals = (str: string): string => {
  if (!str) return '';
  const eastern = '٠١٢٣٤٥٦٧٨٩';
  return str.replace(/\d/g, d => eastern[parseInt(d, 10)] || d);
};

export type ExportFormat = 'png' | 'jpg' | 'pdf';

/**
 * Loads an image URL into an HTMLImageElement with crossOrigin support.
 */
function loadImage(src: string): Promise<HTMLImageElement> {
  return new Promise((resolve, reject) => {
    const img = new Image();
    img.crossOrigin = 'anonymous';
    img.onload = () => resolve(img);
    img.onerror = () => {
      // Fallback: try without crossOrigin if local or already failed
      const fallbackImg = new Image();
      fallbackImg.onload = () => resolve(fallbackImg);
      fallbackImg.onerror = (e) => reject(e);
      fallbackImg.src = src;
    };
    img.src = src;
  });
}

/**
 * Renders rounded rectangle path onto canvas context.
 */
function roundRect(
  ctx: CanvasRenderingContext2D,
  x: number,
  y: number,
  w: number,
  h: number,
  r: number
) {
  ctx.beginPath();
  ctx.moveTo(x + r, y);
  ctx.lineTo(x + w - r, y);
  ctx.quadraticCurveTo(x + w, y, x + w, y + r);
  ctx.lineTo(x + w, y + h - r);
  ctx.quadraticCurveTo(x + w, y + h, x + w - r, y + h);
  ctx.lineTo(x + r, y + h);
  ctx.quadraticCurveTo(x, y + h, x, y + h - r);
  ctx.lineTo(x, y + r);
  ctx.quadraticCurveTo(x, y, x + r, y);
  ctx.closePath();
}

/**
 * Draws the FeatheredStrokeLabel used for Saudi Driving License headers/labels.
 * Matches FeatheredStrokeLabel in DynamicDrivingLicenseCard.kt.
 */
function drawFeatheredStrokeLabel(
  ctx: CanvasRenderingContext2D,
  text: string,
  x: number,
  y: number,
  fontSize: number,
  align: CanvasTextAlign = 'left'
) {
  ctx.save();
  ctx.font = `bold ${fontSize}px "Tajawal", "Segoe UI", Arial, sans-serif`;
  ctx.textAlign = align;
  ctx.textBaseline = 'middle';

  // 1. Black outline stroke
  ctx.strokeStyle = '#111111';
  ctx.lineWidth = Math.max(2, fontSize * 0.22);
  ctx.lineJoin = 'round';
  ctx.miterLimit = 2;
  ctx.strokeText(text, x, y);

  // 2. Crisp pure white text fill on top
  ctx.fillStyle = '#FFFFFF';
  ctx.fillText(text, x, y);
  ctx.restore();
}

/**
 * Generates the authentic 1586x1000 Canvas for Resident ID (Muqeem Card).
 * 1:1 Pixel-perfect match with Android DynamicMuqeemCard.kt.
 */
async function renderMuqeemCardCanvas(user: Partial<UserProfile>): Promise<HTMLCanvasElement> {
  const canvas = document.createElement('canvas');
  const W = 1586;
  const H = 1000;
  canvas.width = W;
  canvas.height = H;
  const ctx = canvas.getContext('2d');
  if (!ctx) throw new Error('Canvas 2D context unavailable');

  // Smooth anti-aliasing
  ctx.imageSmoothingEnabled = true;
  ctx.imageSmoothingQuality = 'high';

  // 1. Draw Template Background
  try {
    const bgImg = await loadImage('/bg_resident_card.webp');
    ctx.drawImage(bgImg, 0, 0, W, H);
  } catch {
    ctx.fillStyle = '#FCFBF7';
    ctx.fillRect(0, 0, W, H);
  }

  // 2. Version Indicator (aligned to top-left near version mark)
  const ver = toArabicNumerals(user.versionNumber || '1');
  ctx.fillStyle = '#222222';
  ctx.font = '800 48px "Tajawal", "Segoe UI", Arial, sans-serif';
  ctx.textAlign = 'center';
  ctx.fillText(ver, W * 0.106, H * 0.185);

  // 3. Citizen Portrait Photo
  const photoX = W * 0.058;
  const photoY = H * 0.282;
  const photoW = W * 0.254;
  const photoH = H * 0.461;
  const photoRadius = 10;

  ctx.save();
  roundRect(ctx, photoX, photoY, photoW, photoH, photoRadius);
  ctx.clip();
  ctx.fillStyle = '#E8EEF4';
  ctx.fillRect(photoX, photoY, photoW, photoH);

  if (user.photoUrl) {
    try {
      const avatarImg = await loadImage(user.photoUrl);
      ctx.drawImage(avatarImg, photoX, photoY, photoW, photoH);
    } catch {
      // Photo load fallback
    }
  }
  ctx.restore();

  // 4. Lower Verification Box (White Box with QR Code + 4-Line Arabic Security Disclaimer)
  const boxX = W * 0.046;
  const boxY = H * 0.755;
  const boxW = W * 0.270;
  const boxH = H * 0.168;
  const boxRadius = 12;

  ctx.save();
  ctx.fillStyle = '#FFFFFF';
  roundRect(ctx, boxX, boxY, boxW, boxH, boxRadius);
  ctx.fill();
  ctx.strokeStyle = '#D0CAC0';
  ctx.lineWidth = 2.5;
  ctx.stroke();

  // Draw QR Code
  const qrPayload = buildOfficialQrPayload(user);
  const qrUrls = getQrCodeFallbackUrls(qrPayload, 300);
  const qrSize = boxH * 0.90;
  const qrX = boxX + 6;
  const qrY = boxY + (boxH - qrSize) / 2;

  for (const url of qrUrls) {
    try {
      const qrImg = await loadImage(url);
      ctx.drawImage(qrImg, qrX, qrY, qrSize, qrSize);

      // Centered Absher Emblem over QR Code
      try {
        const emblemImg = await loadImage('/ic_absher_qr_emblem.png');
        const embSize = qrSize * 0.26;
        const embX = qrX + (qrSize - embSize) / 2;
        const embY = qrY + (qrSize - embSize) / 2;
        ctx.fillStyle = '#FFFFFF';
        roundRect(ctx, embX - 3, embY - 3, embSize + 6, embSize + 6, 4);
        ctx.fill();
        ctx.drawImage(emblemImg, embX, embY, embSize, embSize);
      } catch {}
      break;
    } catch {
      continue;
    }
  }

  // 4-Line Arabic Security Disclaimer
  ctx.fillStyle = '#2B2B2B';
  ctx.font = '900 21px "Tajawal", "Noto Kufi Arabic", "Segoe UI", Arial, sans-serif';
  ctx.textAlign = 'right';
  ctx.direction = 'rtl';
  const discX = boxX + boxW - 14;
  ctx.fillText('يجب التحقق', discX, boxY + boxH * 0.27);
  ctx.fillText('من الرمز السريع', discX, boxY + boxH * 0.49);
  ctx.fillText('قبل اعتماد', discX, boxY + boxH * 0.71);
  ctx.fillText('التعامل مع الهوية', discX, boxY + boxH * 0.92);
  ctx.restore();

  // 5. 1D Barcode Strip at Bottom-Left
  const barX = W * 0.057;
  const barY = H * 0.930;
  const barW = W * 0.254;
  const barH = H * 0.058;

  ctx.fillStyle = '#FFFFFF';
  ctx.fillRect(barX, barY, barW, barH);

  const totalUnits = BARCODE_PATTERN.reduce((sum, w) => sum + w, 0);
  const unitW = barW / totalUnits;
  let currX = barX;
  ctx.fillStyle = '#000000';
  for (let idx = 0; idx < BARCODE_PATTERN.length; idx++) {
    const w = BARCODE_PATTERN[idx] * unitW;
    if (idx % 2 === 0) {
      ctx.fillRect(currX, barY + 3, w, barH - 6);
    }
    currX += w;
  }

  // 6. Dynamic Citizen Data Fields (Aligned 1:1 matching official reference ID)
  const dataLeft = W * 0.320;
  const dataTop = H * 0.248;
  const dataW = W * 0.640;
  const dataH = H * 0.712;

  ctx.save();
  ctx.direction = 'rtl';

  // Names Header: Arabic bold on top, English uppercase below
  ctx.fillStyle = '#343436';
  ctx.font = 'bold 50px "Tajawal", "Segoe UI", Arial, sans-serif';
  ctx.textAlign = 'right';
  ctx.fillText(user.fullNameAr || '', dataLeft + dataW, dataTop + 38);

  ctx.fillStyle = '#32322A';
  ctx.font = 'bold 34px "Segoe UI", Arial, sans-serif';
  ctx.textAlign = 'right';
  ctx.direction = 'ltr';
  ctx.fillText((user.fullNameEn || '').toUpperCase(), dataLeft + dataW, dataTop + 82);

  // 8 Dynamic Arabic Rows
  const startRowY = dataTop + 145;
  const rowSpacing = (dataH - 145) / 7.2;

  const labelFont = '600 27px "Tajawal", "Segoe UI", Arial, sans-serif';
  const valFont = 'bold 31px "Tajawal", "Segoe UI", Arial, sans-serif';
  const labelColor = '#7A786E';
  const valColor = '#242424';

  const rightColX = dataLeft + dataW;
  const leftColX = dataLeft + dataW * 0.48;

  // Helper for 2-column row
  const drawTwoColRow = (
    y: number,
    rLabel: string,
    rVal: string,
    lLabel: string,
    lVal: string
  ) => {
    // Right Column
    ctx.direction = 'rtl';
    ctx.textAlign = 'right';
    ctx.font = labelFont;
    ctx.fillStyle = labelColor;
    ctx.fillText(rLabel, rightColX, y);
    const rLabelWidth = ctx.measureText(rLabel).width;
    ctx.font = valFont;
    ctx.fillStyle = valColor;
    ctx.fillText(rVal, rightColX - rLabelWidth - 10, y);

    // Left Column
    ctx.font = labelFont;
    ctx.fillStyle = labelColor;
    ctx.fillText(lLabel, leftColX, y);
    const lLabelWidth = ctx.measureText(lLabel).width;
    ctx.font = valFont;
    ctx.fillStyle = valColor;
    ctx.fillText(lVal, leftColX - lLabelWidth - 10, y);
  };

  // Helper for 1-column row
  const drawSingleRow = (y: number, label: string, val: string) => {
    ctx.direction = 'rtl';
    ctx.textAlign = 'right';
    ctx.font = labelFont;
    ctx.fillStyle = labelColor;
    ctx.fillText(label, rightColX, y);
    const labelWidth = ctx.measureText(label).width;
    ctx.font = valFont;
    ctx.fillStyle = valColor;
    ctx.fillText(val, rightColX - labelWidth - 12, y);
  };

  const expAr = user.expiryDateEn ? toArabicNumerals(user.expiryDateEn) : (user.expiryDateAr || '');
  const dobAr = user.dateOfBirth ? toArabicNumerals(user.dateOfBirth) : (user.dateOfBirthAr || '');
  const natIdAr = toArabicNumerals(user.nationalId || '');
  const sponsorIdAr = toArabicNumerals(user.sponsorId || '');

  // Row 1: Expiry Date (Left) | National ID (Right)
  drawTwoColRow(startRowY, 'رقم الهوية:', natIdAr, 'تاريخ الانتهاء:', expAr);

  // Row 2: Place of Birth (Left) | Date of Birth (Right)
  drawTwoColRow(startRowY + rowSpacing * 1, 'تاريخ الميلاد:', dobAr, 'مكان الميلاد:', user.placeOfBirthAr || '');

  // Row 3: Religion (Left) | Nationality (Right)
  drawTwoColRow(startRowY + rowSpacing * 2, 'الجنسية:', user.nationalityAr || '', 'الديانة:', user.religionAr || '');

  // Row 4: Profession
  drawSingleRow(startRowY + rowSpacing * 3, 'المهنة:', user.professionAr || '');

  // Row 5: Employer / Sponsor ID
  drawSingleRow(startRowY + rowSpacing * 4, 'هوية صاحب العمل:', sponsorIdAr);

  // Row 6: Place of Issue
  drawSingleRow(startRowY + rowSpacing * 5, 'مكان الإصدار:', user.issuePlace || 'شركة العلم لامن المعلومات');

  // Row 7: Place of Work
  drawSingleRow(startRowY + rowSpacing * 6, 'مكان العمل:', user.workPlaceAr || 'منطقة الرياض');

  // Row 8: Sponsor Name
  drawSingleRow(startRowY + rowSpacing * 7, 'اسم صاحب العمل:', user.sponsorName || user.sponsorNameEn || '');

  ctx.restore();
  return canvas;
}

/**
 * Generates the authentic 1586x1000 Canvas for Saudi Driving License.
 * 1:1 Pixel-perfect match with Android DynamicDrivingLicenseCard.kt.
 */
async function renderDrivingLicenseCanvas(user: Partial<UserProfile>): Promise<HTMLCanvasElement> {
  const canvas = document.createElement('canvas');
  const W = 1586;
  const H = 1000;
  canvas.width = W;
  canvas.height = H;
  const ctx = canvas.getContext('2d');
  if (!ctx) throw new Error('Canvas 2D context unavailable');

  // Smooth anti-aliasing
  ctx.imageSmoothingEnabled = true;
  ctx.imageSmoothingQuality = 'high';

  // 1. Draw Template Background
  try {
    const bgImg = await loadImage('/bg_driving_license.webp');
    ctx.drawImage(bgImg, 0, 0, W, H);
  } catch {
    ctx.fillStyle = '#FFFFFF';
    ctx.fillRect(0, 0, W, H);
  }

  // 2. Holder Photo (Positioned precisely to cover the template's pre-printed photo frame)
  const photoX = W * 0.040;
  const photoY = H * 0.235;
  const photoW = W * 0.274;
  const photoH = H * 0.482;
  const photoRadius = 22;

  ctx.save();
  roundRect(ctx, photoX, photoY, photoW, photoH, photoRadius);
  ctx.clip();
  ctx.fillStyle = '#E8EEF4';
  ctx.fillRect(photoX, photoY, photoW, photoH);

  if (user.photoUrl) {
    try {
      const avatarImg = await loadImage(user.photoUrl);
      ctx.drawImage(avatarImg, photoX, photoY, photoW, photoH);
    } catch {}
  }
  ctx.restore();

  // 3. Verification Box (QR Code with Centered Absher Emblem + 4-Line Arabic Disclaimer)
  const boxX = W * 0.040;
  const boxY = H * 0.728;
  const boxW = W * 0.274;
  const boxH = H * 0.158;
  const boxRadius = 10;

  ctx.save();
  ctx.fillStyle = '#FFFFFF';
  roundRect(ctx, boxX, boxY, boxW, boxH, boxRadius);
  ctx.fill();
  ctx.strokeStyle = '#D0CAC0';
  ctx.lineWidth = 2;
  ctx.stroke();

  // Draw QR Code
  const qrPayload = buildOfficialQrPayload(user);
  const qrUrls = getQrCodeFallbackUrls(qrPayload, 300);
  const qrSize = boxH * 0.88;
  const qrX = boxX + 6;
  const qrY = boxY + (boxH - qrSize) / 2;

  for (const url of qrUrls) {
    try {
      const qrImg = await loadImage(url);
      ctx.drawImage(qrImg, qrX, qrY, qrSize, qrSize);

      try {
        const emblemImg = await loadImage('/ic_absher_qr_emblem.png');
        const embSize = qrSize * 0.26;
        const embX = qrX + (qrSize - embSize) / 2;
        const embY = qrY + (qrSize - embSize) / 2;
        ctx.fillStyle = '#FFFFFF';
        roundRect(ctx, embX - 2, embY - 2, embSize + 4, embSize + 4, 3);
        ctx.fill();
        ctx.drawImage(emblemImg, embX, embY, embSize, embSize);
      } catch {}
      break;
    } catch {
      continue;
    }
  }

  // 4-Line Arabic Disclaimer
  ctx.fillStyle = '#2B2B2B';
  ctx.font = '900 20px "Tajawal", "Noto Kufi Arabic", "Segoe UI", Arial, sans-serif';
  ctx.textAlign = 'right';
  ctx.direction = 'rtl';
  const discX = boxX + boxW - 12;
  ctx.fillText('يجب التحقق', discX, boxY + boxH * 0.27);
  ctx.fillText('من الرمز السريع', discX, boxY + boxH * 0.49);
  ctx.fillText('قبل اعتماد', discX, boxY + boxH * 0.71);
  ctx.fillText('التعامل مع الهوية', discX, boxY + boxH * 0.92);
  ctx.restore();

  // 4. Holder Names Header (Right-aligned)
  const nameRight = W * 0.961;
  const nameTop = H * 0.275;

  ctx.save();
  ctx.fillStyle = '#222222';
  ctx.font = 'bold 46px "Tajawal", "Segoe UI", Arial, sans-serif';
  ctx.textAlign = 'right';
  ctx.direction = 'rtl';
  ctx.fillText(user.fullNameAr || '', nameRight, nameTop);

  ctx.fillStyle = '#222222';
  ctx.font = '600 32px "Segoe UI", Arial, sans-serif';
  ctx.textAlign = 'right';
  ctx.direction = 'ltr';
  ctx.fillText((user.fullNameEn || '').toUpperCase(), nameRight, nameTop + 42);
  ctx.restore();

  // 5. 7 Bilingual License Credentials Rows
  const fields = [
    {
      labelEn: 'ID Number:',
      valEn: user.nationalId || '',
      labelAr: 'رقم الهوية:',
      valAr: toArabicNumerals(user.nationalId || ''),
    },
    {
      labelEn: 'License Type:',
      valEn: user.licenseTypeEn || 'Private',
      labelAr: 'نوع الرخصة:',
      valAr: user.licenseTypeAr || 'خصوصي',
    },
    {
      labelEn: 'Issue Date:',
      valEn: user.licenseIssueDateEn || '10/03/2026',
      labelAr: 'تاريخ الإصدار:',
      valAr: user.licenseIssueDateAr || toArabicNumerals(user.licenseIssueDateEn || '10/03/2026'),
    },
    {
      labelEn: 'Date of Birth:',
      valEn: user.dateOfBirth || '10/01/1984',
      labelAr: 'تاريخ الميلاد:',
      valAr: user.dateOfBirthAr || toArabicNumerals(user.dateOfBirth || '10/01/1984'),
    },
    {
      labelEn: 'Nationality:',
      valEn: user.nationality || 'Bangladesh',
      labelAr: 'الجنسية:',
      valAr: user.nationalityAr || 'بنجلاديش',
    },
    {
      labelEn: 'Expiry Date:',
      valEn: user.licenseExpiryDateEn || '21/11/2035',
      labelAr: 'تاريخ الانتهاء:',
      valAr: user.licenseExpiryDateAr || toArabicNumerals(user.licenseExpiryDateEn || '21/11/2035'),
    },
    {
      labelEn: 'Blood Type:',
      valEn: user.bloodType || 'A+',
      labelAr: 'فصيلة الدم:',
      valAr: user.bloodType || 'A+',
    },
  ];

  const rowStartY = H * 0.448;
  const rowH = (H * 0.512) / 6.4;
  const colLeftX = W * 0.326;
  const colRightX = W * 0.961;

  fields.forEach((field, i) => {
    const y = rowStartY + i * rowH;

    // English sub-column (Left side): Label + Value
    drawFeatheredStrokeLabel(ctx, field.labelEn, colLeftX, y, 26, 'left');
    ctx.save();
    ctx.font = 'bold 28px "Segoe UI", Arial, sans-serif';
    ctx.fillStyle = '#1E1E1E';
    ctx.textAlign = 'left';
    ctx.fillText(field.valEn, colLeftX + 175, y);
    ctx.restore();

    // Arabic sub-column (Right side): Value + Label
    drawFeatheredStrokeLabel(ctx, field.labelAr, colRightX, y, 26, 'right');
    ctx.save();
    ctx.font = 'bold 28px "Tajawal", "Segoe UI", Arial, sans-serif';
    ctx.fillStyle = '#1E1E1E';
    ctx.textAlign = 'right';
    ctx.fillText(field.valAr, colRightX - 165, y);
    ctx.restore();
  });

  return canvas;
}

/**
 * Creates a standard ISO ID-1 PDF (85.6mm x 53.98mm, 300 DPI equivalent) embedding the JPEG image.
 */
function createCardPdfBlob(jpegDataUrl: string): Blob {
  const base64Data = jpegDataUrl.replace(/^data:image\/jpeg;base64,/, '');
  const binaryString = atob(base64Data);
  const imgLen = binaryString.length;

  const imgBytes = new Uint8Array(imgLen);
  for (let i = 0; i < imgLen; i++) {
    imgBytes[i] = binaryString.charCodeAt(i);
  }

  // Standard CR80 Card Size: 85.60 mm x 53.98 mm in points (72 pt per inch)
  const ptW = 242.65;
  const ptH = 153.01;

  // Build PDF 1.4 objects
  const contentStream = `q\n${ptW.toFixed(2)} 0 0 ${ptH.toFixed(2)} 0 0 cm\n/Im1 Do\nQ\n`;
  const contentStreamLen = contentStream.length;

  const header = `%PDF-1.4\n%\n`;

  const obj1 = `1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n`;
  const obj2 = `2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n`;
  const obj3 = `3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 ${ptW.toFixed(2)} ${ptH.toFixed(2)}] /Resources << /XObject << /Im1 4 0 R >> >> /Contents 5 0 R >>\nendobj\n`;
  const obj4Header = `4 0 obj\n<< /Type /XObject /Subtype /Image /Width 1586 /Height 1000 /ColorSpace /DeviceRGB /BitsPerComponent 8 /Filter /DCTDecode /Length ${imgLen} >>\nstream\n`;
  const obj4Footer = `\nendstream\nendobj\n`;
  const obj5 = `5 0 obj\n<< /Length ${contentStreamLen} >>\nstream\n${contentStream}endstream\nendobj\n`;

  const encoder = new TextEncoder();
  const hBytes = encoder.encode(header);
  const o1Bytes = encoder.encode(obj1);
  const o2Bytes = encoder.encode(obj2);
  const o3Bytes = encoder.encode(obj3);
  const o4HBytes = encoder.encode(obj4Header);
  const o4FBytes = encoder.encode(obj4Footer);
  const o5Bytes = encoder.encode(obj5);

  const offset1 = hBytes.length;
  const offset2 = offset1 + o1Bytes.length;
  const offset3 = offset2 + o2Bytes.length;
  const offset4 = offset3 + o3Bytes.length;
  const offset5 = offset4 + o4HBytes.length + imgLen + o4FBytes.length;
  const xrefOffset = offset5 + o5Bytes.length;

  const pad10 = (n: number) => n.toString().padStart(10, '0');

  const xref = `xref\n0 6\n0000000000 65535 f \n${pad10(offset1)} 00000 n \n${pad10(offset2)} 00000 n \n${pad10(offset3)} 00000 n \n${pad10(offset4)} 00000 n \n${pad10(offset5)} 00000 n \ntrailer\n<< /Size 6 /Root 1 0 R >>\nstartxref\n${xrefOffset}\n%%EOF\n`;
  const xrefBytes = encoder.encode(xref);

  return new Blob(
    [hBytes, o1Bytes, o2Bytes, o3Bytes, o4HBytes, imgBytes, o4FBytes, o5Bytes, xrefBytes],
    { type: 'application/pdf' }
  );
}

/**
 * Downloads a Blob or DataURL to the user's computer.
 */
function triggerDownload(urlOrBlob: string | Blob, filename: string) {
  const url = typeof urlOrBlob === 'string' ? urlOrBlob : URL.createObjectURL(urlOrBlob);
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  if (typeof urlOrBlob !== 'string') {
    setTimeout(() => URL.revokeObjectURL(url), 1000);
  }
}

/**
 * Main export function: Generates authentic, high-resolution document files in PNG, JPG, or PDF format.
 */
export async function downloadDigitalDocument(
  user: Partial<UserProfile>,
  docType: 'RESIDENT_ID' | 'DRIVING_LICENSE',
  format: ExportFormat = 'png'
): Promise<void> {
  const canvas =
    docType === 'DRIVING_LICENSE'
      ? await renderDrivingLicenseCanvas(user)
      : await renderMuqeemCardCanvas(user);

  const cleanName = (user.fullNameEn || user.nationalId || 'document')
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, '_')
    .slice(0, 30);
  const docPrefix = docType === 'DRIVING_LICENSE' ? 'driving_license' : 'muqeem_card';

  if (format === 'png') {
    const pngUrl = canvas.toDataURL('image/png');
    triggerDownload(pngUrl, `${docPrefix}_${cleanName}.png`);
  } else if (format === 'jpg') {
    const jpgUrl = canvas.toDataURL('image/jpeg', 0.96);
    triggerDownload(jpgUrl, `${docPrefix}_${cleanName}.jpg`);
  } else if (format === 'pdf') {
    const jpgUrl = canvas.toDataURL('image/jpeg', 0.96);
    const pdfBlob = createCardPdfBlob(jpgUrl);
    triggerDownload(pdfBlob, `${docPrefix}_${cleanName}.pdf`);
  }
}

/**
 * Client-side image compressor for citizen avatar and ID portrait photos.
 * Compresses images into ultra-compact WebP/JPEG Data URLs (~20KB - 40KB)
 * for direct, zero-cost storage inside Firebase Firestore documents without
 * needing external object storage or triggering Blaze billing.
 */

export interface CompressionResult {
  dataUrl: string;
  sizeKb: number;
  width: number;
  height: number;
}

export function compressAvatarImage(
  source: File | Blob | string,
  maxWidth = 360,
  maxHeight = 460,
  quality = 0.82
): Promise<CompressionResult> {
  return new Promise((resolve, reject) => {
    const img = new Image();
    img.crossOrigin = 'anonymous';

    img.onload = () => {
      try {
        let { width, height } = img;

        // Calculate proportional scale to fit within maxWidth x maxHeight
        const ratio = Math.min(maxWidth / width, maxHeight / height, 1);
        const targetWidth = Math.round(width * ratio);
        const targetHeight = Math.round(height * ratio);

        const canvas = document.createElement('canvas');
        canvas.width = targetWidth;
        canvas.height = targetHeight;

        const ctx = canvas.getContext('2d');
        if (!ctx) {
          reject(new Error('Failed to create canvas context'));
          return;
        }

        // Apply smooth rendering
        ctx.imageSmoothingEnabled = true;
        ctx.imageSmoothingQuality = 'high';

        // Draw image onto canvas
        ctx.drawImage(img, 0, 0, targetWidth, targetHeight);

        // Try WebP first, fallback to JPEG
        let dataUrl = canvas.toDataURL('image/webp', quality);
        if (!dataUrl.startsWith('data:image/webp')) {
          dataUrl = canvas.toDataURL('image/jpeg', quality);
        }

        // Calculate approximate size in KB
        const base64Length = dataUrl.split(',')[1]?.length || dataUrl.length;
        const sizeKb = Math.round((base64Length * 3) / 4 / 1024);

        resolve({
          dataUrl,
          sizeKb,
          width: targetWidth,
          height: targetHeight
        });
      } catch (err) {
        reject(err);
      }
    };

    img.onerror = (err) => {
      reject(new Error(`Failed to load image for compression: ${err}`));
    };

    if (typeof source === 'string') {
      img.src = source;
    } else {
      const reader = new FileReader();
      reader.onload = (e) => {
        if (typeof e.target?.result === 'string') {
          img.src = e.target.result;
        } else {
          reject(new Error('Failed to read file as Data URL'));
        }
      };
      reader.onerror = reject;
      reader.readAsDataURL(source);
    }
  });
}

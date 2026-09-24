package com.civora.app.core.util

import android.graphics.Bitmap
import com.civora.app.core.model.UserProfile
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

object OfficialQrGenerator {

    private const val OFFICIAL_SIGNATURE =
        "Q6HFXkZ5rv0cxjyESBCX/YegdHCzE2/4ujU1nUlq5V3CBjzYe2k2NlwpPfQw8ZX2DkFWEJQU/ZtIvWXIlD89UEjGYoQAWR+Y5iodT6uZUc1ZVPAa6F1sPrJVwaTNf/TUHhkNyWnzR1sg9v8ZzYuSqgYmr6Hl9VsmboZIWoJVHnm6d2skUnKKKvhSrPrnTFrzse2UbO0H2zdS1EiSZAu6l34qHotCdsS27psDNwinHm82FZR+pmDRZpz0pMLpr4P3skg9Y9RGZJFa6wOtFdhHj9zwPci+9QLqvZrU+91cyPG51+/o7fZ/ea/H8rsRNxg65lWov/97smO2GMhFq+2ZtA=="

    private const val OFFICIAL_KEY_ID = 121980001

    /**
     * Builds the official cryptographic Saudi Absher / Muqeem QR code envelope.
     * Dynamic attributes (National ID/Iqama number, expiry date, issue date, current timestamp)
     * are bound directly to the user profile while preserving the official envelope structure
     * and cryptographic verification signature.
     */
    fun buildPayload(user: UserProfile, timestampMillis: Long = System.currentTimeMillis()): String {
        val hid = user.nationalId.ifEmpty { "2495685261" }
        val exp = user.expiryDateDigits.ifEmpty { "081026" }
        val iat = user.issueDateDigits.ifEmpty { "070926" }

        val cdaInner = """{"hid":"$hid","cnt":{"pid":"$hid"},"typ":2,"exp":"$exp","iat":"$iat"}"""
        val escapedCda = cdaInner.replace("\"", "\\\"")

        return """{"sig":"$OFFICIAL_SIGNATURE","payload":{"cda":"100${'$'}ISS:1${'$'}$escapedCda","iat":$timestampMillis},"header":{"kid":$OFFICIAL_KEY_ID}}"""
    }

    fun generateBitmap(content: String, size: Int = 512): Bitmap? {
        return try {
            val hints = mapOf(
                EncodeHintType.CHARACTER_SET to "UTF-8",
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H,
                EncodeHintType.MARGIN to 0
            )
            val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
            val matrixWidth = bitMatrix.width
            val matrixHeight = bitMatrix.height

            var minX = matrixWidth
            var minY = matrixHeight
            var maxX = 0
            var maxY = 0

            for (x in 0 until matrixWidth) {
                for (y in 0 until matrixHeight) {
                    if (bitMatrix[x, y]) {
                        if (x < minX) minX = x
                        if (y < minY) minY = y
                        if (x > maxX) maxX = x
                        if (y > maxY) maxY = y
                    }
                }
            }

            if (maxX < minX || maxY < minY) {
                minX = 0
                minY = 0
                maxX = matrixWidth - 1
                maxY = matrixHeight - 1
            }

            val qrWidth = maxX - minX + 1
            val qrHeight = maxY - minY + 1

            val bitmap = Bitmap.createBitmap(qrWidth, qrHeight, Bitmap.Config.ARGB_8888)
            for (x in 0 until qrWidth) {
                for (y in 0 until qrHeight) {
                    bitmap.setPixel(
                        x,
                        y,
                        if (bitMatrix[minX + x, minY + y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                    )
                }
            }
            bitmap
        } catch (e: Exception) {
            null
        }
    }
}


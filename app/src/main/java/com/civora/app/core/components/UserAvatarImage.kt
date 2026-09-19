package com.civora.app.core.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.civora.app.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

/**
 * High-performance image component that seamlessly displays citizen/resident avatar photos:
 * 1. Supports client-compressed WebP/JPEG Base64 data URLs directly from Firestore (zero network roundtrip, instant offline load).
 * 2. Supports standard HTTP/HTTPS URLs (downloaded asynchronously in background).
 * 3. Gracefully falls back to the official default user avatar drawable if empty or invalid.
 */
@Composable
fun UserAvatarImage(
    photoUrl: String,
    contentDescription: String? = "Cardholder Photo",
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholderRes: Int = R.drawable.user_avatar
) {
    var decodedBitmap by remember(photoUrl) {
        mutableStateOf<Bitmap?>(decodeBase64Fast(photoUrl))
    }

    // If it's a remote web URL rather than a Base64 string, fetch it asynchronously
    LaunchedEffect(photoUrl) {
        if (decodedBitmap == null && photoUrl.isNotBlank() && (photoUrl.startsWith("http://") || photoUrl.startsWith("https://"))) {
            withContext(Dispatchers.IO) {
                try {
                    val stream = URL(photoUrl).openStream()
                    val bmp = BitmapFactory.decodeStream(stream)
                    stream.close()
                    withContext(Dispatchers.Main) {
                        decodedBitmap = bmp
                    }
                } catch (_: Exception) {
                    // Fall back to default placeholder on network failure
                }
            }
        }
    }

    val bmp = decodedBitmap
    if (bmp != null) {
        Image(
            bitmap = bmp.asImageBitmap(),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    } else {
        Image(
            painter = painterResource(id = placeholderRes),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    }
}

/**
 * Fast synchronous Base64 decoder for in-memory Data URLs stored in Firestore.
 */
private fun decodeBase64Fast(source: String): Bitmap? {
    if (source.isBlank()) return null
    return try {
        val cleanBase64 = if (source.contains(",")) {
            source.substringAfter(",")
        } else {
            source
        }
        val bytes = Base64.decode(cleanBase64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (_: Exception) {
        null
    }
}

package com.civora.app.core.update

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

data class AppUpdateInfo(
    val latestVersion: String,
    val releaseNotes: String,
    val downloadUrl: String,
    val isUpdateAvailable: Boolean
)

class UpdateManager(private val context: Context) {

    companion object {
        private const val GITHUB_OWNER = "zubulika"
        private const val GITHUB_REPO = "Civora"
        const val RELEASES_API_URL = "https://api.github.com/repos/$GITHUB_OWNER/$GITHUB_REPO/releases/latest"
    }

    /**
     * Queries GitHub Releases API for the latest published release.
     * Compares tag_name with the currently running app version.
     */
    suspend fun checkForUpdate(currentVersion: String = "1.0.0"): Result<AppUpdateInfo> = withContext(Dispatchers.IO) {
        try {
            val url = URL(RELEASES_API_URL)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("Accept", "application/vnd.github.v3+json")
                setRequestProperty("User-Agent", "Absher-Android-App")
                connectTimeout = 8000
                readTimeout = 8000
            }

            val responseCode = connection.responseCode
            if (responseCode == 200) {
                val jsonStr = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(jsonStr)

                val tagName = json.optString("tag_name", "").removePrefix("v").trim()
                val body = json.optString("body", "Bug fixes and performance improvements.")
                
                // Find apk asset
                var downloadUrl = ""
                val assets = json.optJSONArray("assets")
                if (assets != null) {
                    for (i in 0 until assets.length()) {
                        val asset = assets.getJSONObject(i)
                        val name = asset.optString("name", "")
                        if (name.endsWith(".apk", ignoreCase = true)) {
                            downloadUrl = asset.optString("browser_download_url", "")
                            break
                        }
                    }
                }

                // If no direct APK asset, fall back to release html_url
                if (downloadUrl.isEmpty()) {
                    downloadUrl = json.optString("html_url", "")
                }

                val hasUpdate = isNewerVersion(currentVersion, tagName)
                Result.success(
                    AppUpdateInfo(
                        latestVersion = if (tagName.isNotEmpty()) tagName else currentVersion,
                        releaseNotes = body,
                        downloadUrl = downloadUrl,
                        isUpdateAvailable = hasUpdate
                    )
                )
            } else if (responseCode == 404) {
                // No releases published yet on GitHub
                Result.success(
                    AppUpdateInfo(
                        latestVersion = currentVersion,
                        releaseNotes = "You are on the latest version of Absher.",
                        downloadUrl = "",
                        isUpdateAvailable = false
                    )
                )
            } else {
                Result.failure(Exception("GitHub API returned HTTP $responseCode"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Directly streams and downloads the APK with live byte progress.
     * Follows GitHub release CDN redirects seamlessly.
     */
    suspend fun downloadApkDirect(
        downloadUrl: String,
        onProgress: (bytesDownloaded: Long, totalBytes: Long) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            var currentUrl = downloadUrl
            var connection: HttpURLConnection? = null
            var redirectCount = 0

            while (redirectCount < 6) {
                val url = URL(currentUrl)
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    instanceFollowRedirects = false
                    setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android) Civora-Updater")
                    setRequestProperty("Accept", "*/*")
                    connectTimeout = 15000
                    readTimeout = 30000
                }
                val code = conn.responseCode
                if (code == HttpURLConnection.HTTP_MOVED_TEMP || code == HttpURLConnection.HTTP_MOVED_PERM || code == 307 || code == 308) {
                    val newLoc = conn.getHeaderField("Location")
                    conn.disconnect()
                    if (newLoc.isNullOrEmpty()) break
                    currentUrl = newLoc
                    redirectCount++
                } else {
                    connection = conn
                    break
                }
            }

            val finalConn = connection ?: return@withContext Result.failure(Exception("Failed to connect to download server"))
            if (finalConn.responseCode != 200) {
                return@withContext Result.failure(Exception("Server returned HTTP ${finalConn.responseCode}"))
            }

            val totalBytes = finalConn.contentLengthLong
            val targetDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.cacheDir
            val targetFile = File(targetDir, "Absher-update.apk")
            if (targetFile.exists()) {
                targetFile.delete()
            }

            finalConn.inputStream.use { input ->
                targetFile.outputStream().use { output ->
                    val buffer = ByteArray(16384)
                    var bytesRead: Int
                    var totalRead = 0L
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        totalRead += bytesRead
                        onProgress(totalRead, totalBytes)
                    }
                    output.flush()
                }
            }

            Result.success(targetFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Downloads the APK file using Android's system DownloadManager.
     * Guaranteed to persist in the background without being cancelled when the user minimizes the app.
     * Shows a system notification in the notification bar and streams live progress to the UI if open.
     */
    fun startBackgroundDownload(
        downloadUrl: String,
        onProgress: (progress: Float, statusText: String) -> Unit = { _, _ -> },
        onComplete: (File) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        if (downloadUrl.isEmpty()) return

        // If it's a web URL to the release page rather than direct apk
        if (!downloadUrl.endsWith(".apk", ignoreCase = true)) {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(browserIntent)
            return
        }

        try {
            val fileName = "Absher-update.apk"
            val destinationFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
            if (destinationFile.exists()) {
                destinationFile.delete()
            }

            val request = DownloadManager.Request(Uri.parse(downloadUrl))
                .setTitle("Absher Update")
                .setDescription("Downloading latest version in background...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadId = downloadManager.enqueue(request)

            // Register receiver to trigger install when finished
            val onCompleteReceiver = object : BroadcastReceiver() {
                override fun onReceive(ctxt: Context?, intent: Intent?) {
                    val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) ?: -1
                    if (id == downloadId) {
                        try {
                            context.unregisterReceiver(this)
                        } catch (_: Exception) {}
                        onComplete(destinationFile)
                        installApk(destinationFile)
                    }
                }
            }

            val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(onCompleteReceiver, filter, Context.RECEIVER_EXPORTED)
            } else {
                context.registerReceiver(onCompleteReceiver, filter)
            }

            // Monitor progress asynchronously to update the in-app UI
            CoroutineScope(Dispatchers.IO).launch {
                var isDownloading = true
                while (isDownloading) {
                    delay(500)
                    val query = DownloadManager.Query().setFilterById(downloadId)
                    val cursor = downloadManager.query(query)
                    if (cursor != null && cursor.moveToFirst()) {
                        val bytesDownloaded = cursor.getLong(
                            cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                        )
                        val totalBytes = cursor.getLong(
                            cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                        )
                        val status = cursor.getInt(
                            cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)
                        )

                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            isDownloading = false
                            onProgress(1f, "Download complete. Starting installation...")
                        } else if (status == DownloadManager.STATUS_FAILED) {
                            isDownloading = false
                            onError("Background download failed. Please try again.")
                        } else if (totalBytes > 0L) {
                            val progress = (bytesDownloaded.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f)
                            val mbRead = bytesDownloaded / (1024 * 1024f)
                            val mbTotal = totalBytes / (1024 * 1024f)
                            onProgress(progress, "Downloading: %.1f MB / %.1f MB".format(mbRead, mbTotal))
                        } else if (bytesDownloaded > 0L) {
                            val mbRead = bytesDownloaded / (1024 * 1024f)
                            onProgress(0.1f, "Downloading: %.1f MB".format(mbRead))
                        }
                        cursor.close()
                    }
                }
            }

        } catch (e: Exception) {
            onError(e.localizedMessage ?: "Failed to start download")
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(browserIntent)
        }
    }

    /**
     * Downloads the APK file using Android's DownloadManager and triggers package installation.
     */
    fun startDownloadAndInstall(downloadUrl: String, onDownloadStarted: () -> Unit = {}) {
        startBackgroundDownload(
            downloadUrl = downloadUrl,
            onProgress = { _, _ -> onDownloadStarted() }
        )
    }

    /**
     * Invokes the system package installer with the downloaded APK FileProvider URI.
     */
    fun installApk(file: File) {
        try {
            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(contentUri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(installIntent)
        } catch (_: Exception) {
            // Safe fallback
        }
    }

    /**
     * SemVer comparator: e.g. "1.0.1" > "1.0.0" -> true
     */
    private fun isNewerVersion(current: String, latest: String): Boolean {
        if (latest.isEmpty() || latest == current) return false
        val currentParts = current.split(".").mapNotNull { it.toIntOrNull() }
        val latestParts = latest.split(".").mapNotNull { it.toIntOrNull() }

        val length = maxOf(currentParts.size, latestParts.size)
        for (i in 0 until length) {
            val c = currentParts.getOrElse(i) { 0 }
            val l = latestParts.getOrElse(i) { 0 }
            if (l > c) return true
            if (l < c) return false
        }
        return false
    }
}

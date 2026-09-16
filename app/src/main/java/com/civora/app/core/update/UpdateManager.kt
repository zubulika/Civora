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
import kotlinx.coroutines.Dispatchers
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
     * Downloads the APK file using Android's DownloadManager and triggers package installation.
     */
    fun startDownloadAndInstall(downloadUrl: String, onDownloadStarted: () -> Unit = {}) {
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
                .setDescription("Downloading latest version...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationUri(Uri.fromFile(destinationFile))
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadId = downloadManager.enqueue(request)
            onDownloadStarted()

            // Register receiver to trigger install when finished
            val onComplete = object : BroadcastReceiver() {
                override fun onReceive(ctxt: Context?, intent: Intent?) {
                    val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) ?: -1
                    if (id == downloadId) {
                        try {
                            context.unregisterReceiver(this)
                        } catch (_: Exception) {}
                        installApk(destinationFile)
                    }
                }
            }

            val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(onComplete, filter, Context.RECEIVER_EXPORTED)
            } else {
                context.registerReceiver(onComplete, filter)
            }

        } catch (e: Exception) {
            // Fallback: open in external browser
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(browserIntent)
        }
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

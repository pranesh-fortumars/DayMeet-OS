package com.example.model

import java.io.File
import com.example.BuildConfig
import com.google.android.play.core.appupdate.AppUpdateInfo as PlayAppUpdateInfo

enum class UpdateChannel {
    GOOGLE_PLAY,
    DIRECT_PRODUCTION_APK
}

enum class PlayUpdateMode {
    FLEXIBLE,
    IMMEDIATE
}

data class AppUpdateInfo(
    val isUpdateAvailable: Boolean = false,
    val currentVersionName: String = BuildConfig.VERSION_NAME,
    val currentVersionCode: Int = BuildConfig.VERSION_CODE,
    val latestVersionName: String = BuildConfig.VERSION_NAME,
    val latestVersionCode: Int = BuildConfig.VERSION_CODE,
    val releaseDate: String = "September 2026",
    val releaseNotes: List<String> = listOf(
        "Google Play In-App Updates API integration",
        "Monthly habit consistency heatmap with daily tracking",
        "Smooth CSS strike-through and slide-out task completion animation",
        "Visual 2-hour urgency warning indicators with red border",
        "Real-time production release sync and auto-updater"
    ),
    val apkDownloadUrl: String = "https://github.com/aistudio/daymeet/releases/latest/download/app-release.apk",
    val isMandatory: Boolean = false,
    val isDownloading: Boolean = false,
    val downloadProgress: Float = 0f,
    val bytesDownloaded: Long = 0L,
    val totalBytesToDownload: Long = 0L,
    val downloadedApkFile: File? = null,
    val isReadyToInstall: Boolean = false,
    val updateChannel: UpdateChannel = UpdateChannel.GOOGLE_PLAY,
    val updateMode: PlayUpdateMode = PlayUpdateMode.FLEXIBLE,
    val playUpdateInfo: PlayAppUpdateInfo? = null,
    val errorMessage: String? = null
)


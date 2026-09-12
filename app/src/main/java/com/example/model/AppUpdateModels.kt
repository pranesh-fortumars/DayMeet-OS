package com.example.model

import java.io.File

data class AppUpdateInfo(
    val isUpdateAvailable: Boolean = false,
    val currentVersionName: String = "1.0",
    val currentVersionCode: Int = 1,
    val latestVersionName: String = "1.1",
    val latestVersionCode: Int = 2,
    val releaseDate: String = "September 2026",
    val releaseNotes: List<String> = listOf(
        "Monthly habit consistency heatmap with daily tracking",
        "Smooth CSS strike-through and slide-out task completion animation",
        "Visual 2-hour urgency warning indicators with red border",
        "Real-time production release sync and auto-updater"
    ),
    val apkDownloadUrl: String = "https://github.com/aistudio/daymeet/releases/latest/download/app-release.apk",
    val isMandatory: Boolean = false,
    val isDownloading: Boolean = false,
    val downloadProgress: Float = 0f,
    val downloadedApkFile: File? = null,
    val isReadyToInstall: Boolean = false,
    val errorMessage: String? = null
)

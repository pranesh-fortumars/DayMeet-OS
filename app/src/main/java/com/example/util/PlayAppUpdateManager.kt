package com.example.util

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.example.model.AppUpdateInfo
import com.example.model.PlayUpdateMode
import com.example.model.UpdateChannel
import com.google.android.play.core.appupdate.AppUpdateInfo as PlayAppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager as PlayCoreAppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object PlayAppUpdateManager {
    private const val TAG = "PlayAppUpdateManager"
    const val PLAY_UPDATE_REQUEST_CODE = 9001

    private var playUpdateManager: PlayCoreAppUpdateManager? = null
    private var installStateListener: InstallStateUpdatedListener? = null

    fun isGooglePlayStoreAvailable(context: Context): Boolean {
        return try {
            val pm = context.packageManager
            val intent = android.content.Intent("com.google.android.play.core.install.BIND_UPDATE_SERVICE").apply {
                setPackage("com.android.vending")
            }
            val resolvedServices = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                pm.queryIntentServices(intent, android.content.pm.PackageManager.ResolveInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                pm.queryIntentServices(intent, 0)
            }
            resolvedServices.isNotEmpty()
        } catch (_: Exception) {
            false
        }
    }

    fun getOrCreate(context: Context): PlayCoreAppUpdateManager {
        return playUpdateManager ?: AppUpdateManagerFactory.create(context.applicationContext).also {
            playUpdateManager = it
        }
    }

    /**
     * Checks Google Play Store for production in-app updates.
     * If running in a test/sideloaded environment without Google Play access,
     * falls back gracefully to direct APK production sync without binding errors.
     */
    suspend fun checkPlayUpdate(
        context: Context,
        onProgressUpdate: (bytes: Long, total: Long, status: Int) -> Unit = { _, _, _ -> }
    ): AppUpdateInfo = withContext(Dispatchers.IO) {
        // If Google Play Store is not installed or PlayCore service cannot be bound,
        // fall back directly without attempting IPC service binding
        if (!isGooglePlayStoreAvailable(context)) {
            val fallback = AppUpdateManager.checkForUpdates(context)
            return@withContext fallback.copy(
                updateChannel = UpdateChannel.DIRECT_PRODUCTION_APK
            )
        }

        val manager = getOrCreate(context)

        // Attach listener for download progress
        registerInstallListener(context, onProgressUpdate)

        try {
            val playInfo = suspendCancellableCoroutine<PlayAppUpdateInfo> { continuation ->
                manager.appUpdateInfo
                    .addOnSuccessListener { info ->
                        if (continuation.isActive) continuation.resume(info)
                    }
                    .addOnFailureListener { e ->
                        if (continuation.isActive) continuation.resumeWithException(e)
                    }
            }

            val updateAvailability = playInfo.updateAvailability()
            val availableVersionCode = playInfo.availableVersionCode()
            val currentCode = AppUpdateManager.getEffectiveVersionCode(context)
            val currentName = AppUpdateManager.getEffectiveVersionName(context)

            val isAvailable = (updateAvailability == UpdateAvailability.UPDATE_AVAILABLE ||
                    updateAvailability == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) &&
                    availableVersionCode > currentCode

            val isImmediateAllowed = playInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            val isFlexibleAllowed = playInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)

            val mode = if (isFlexibleAllowed) PlayUpdateMode.FLEXIBLE else PlayUpdateMode.IMMEDIATE

            AppUpdateInfo(
                isUpdateAvailable = isAvailable,
                currentVersionName = currentName,
                currentVersionCode = currentCode,
                latestVersionName = if (isAvailable) "v$availableVersionCode" else currentName,
                latestVersionCode = if (isAvailable) availableVersionCode else currentCode,
                releaseDate = "Latest Production Release",
                releaseNotes = listOf(
                    "Official Google Play In-App Updates integration",
                    "Seamless background downloads & zero-friction installs",
                    "Monthly habit consistency heatmap with daily tracking",
                    "Smooth CSS strike-through and slide-out task completion animation",
                    "Visual 2-hour urgency warning indicators with red border"
                ),
                updateChannel = UpdateChannel.GOOGLE_PLAY,
                updateMode = mode,
                playUpdateInfo = playInfo,
                isReadyToInstall = playInfo.installStatus() == InstallStatus.DOWNLOADED
            )
        } catch (e: Exception) {
            Log.w(TAG, "Google Play update check unavailable: ${e.message}. Falling back to direct APK sync.")
            val fallback = AppUpdateManager.checkForUpdates(context)
            fallback.copy(
                updateChannel = UpdateChannel.DIRECT_PRODUCTION_APK,
                errorMessage = e.message
            )
        }
    }

    /**
     * Starts the official Google Play In-App Update flow.
     */
    fun startPlayUpdate(
        activity: Activity,
        playInfo: PlayAppUpdateInfo,
        mode: PlayUpdateMode,
        requestCode: Int = PLAY_UPDATE_REQUEST_CODE
    ): Boolean {
        return try {
            val manager = getOrCreate(activity)
            val playUpdateType = when (mode) {
                PlayUpdateMode.IMMEDIATE -> AppUpdateType.IMMEDIATE
                PlayUpdateMode.FLEXIBLE -> AppUpdateType.FLEXIBLE
            }
            val options = AppUpdateOptions.newBuilder(playUpdateType).build()
            manager.startUpdateFlowForResult(playInfo, activity, options, requestCode)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start Google Play update flow: ${e.message}")
            false
        }
    }

    /**
     * Completes a flexible Google Play in-app update that was downloaded.
     */
    suspend fun completePlayUpdate(context: Context): Boolean = withContext(Dispatchers.IO) {
        try {
            suspendCancellableCoroutine { continuation ->
                val manager = getOrCreate(context)
                manager.completeUpdate()
                    .addOnSuccessListener {
                        if (continuation.isActive) continuation.resume(true)
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Failed to complete Google Play update: ${e.message}")
                        if (continuation.isActive) continuation.resume(false)
                    }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in completePlayUpdate: ${e.message}")
            false
        }
    }

    private fun registerInstallListener(
        context: Context,
        onProgressUpdate: (bytes: Long, total: Long, status: Int) -> Unit
    ) {
        val manager = getOrCreate(context)
        if (installStateListener != null) {
            try { manager.unregisterListener(installStateListener!!) } catch (_: Exception) {}
        }

        installStateListener = InstallStateUpdatedListener { state ->
            val status = state.installStatus()
            val bytes = state.bytesDownloaded()
            val total = state.totalBytesToDownload()
            onProgressUpdate(bytes, total, status)
        }.also { listener ->
            manager.registerListener(listener)
        }
    }

    fun unregisterInstallListener() {
        installStateListener?.let { listener ->
            playUpdateManager?.unregisterListener(listener)
            installStateListener = null
        }
    }
}

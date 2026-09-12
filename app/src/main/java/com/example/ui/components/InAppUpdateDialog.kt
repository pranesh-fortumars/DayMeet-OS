package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import android.content.pm.PackageManager
import android.os.Build
import com.example.BuildConfig
import com.example.model.AppUpdateInfo
import com.example.ui.theme.*
import com.example.util.AppUpdateManager
import com.example.util.PlayAppUpdateManager
import com.google.android.play.core.install.model.UpdateAvailability

@Composable
fun InAppUpdateDialog(
    updateInfo: AppUpdateInfo,
    onStartDownload: () -> Unit,
    onInstall: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    // Current APK's installed version code
    val installedPackageVersionCode = remember(context) {
        try {
            val pInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                pInfo.versionCode
            }
        } catch (e: Exception) {
            BuildConfig.VERSION_CODE
        }
    }

    // Effective installed version code incorporating current APK's BuildConfig.VERSION_CODE and persisted installs
    val currentInstalledVersionCode = remember(installedPackageVersionCode) {
        maxOf(
            BuildConfig.VERSION_CODE,
            installedPackageVersionCode,
            AppUpdateManager.getEffectiveVersionCode(context)
        )
    }

    // Retrieve the latest available version code from Google Play Store or update payload
    var playStoreVersionCode by remember(updateInfo) {
        mutableStateOf(
            updateInfo.playUpdateInfo?.availableVersionCode() ?: updateInfo.latestVersionCode
        )
    }

    // Dynamically query Play Store In-App Updates to ensure live Play Store version code
    LaunchedEffect(context) {
        try {
            val manager = PlayAppUpdateManager.getOrCreate(context)
            manager.appUpdateInfo.addOnSuccessListener { info ->
                if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    val storeCode = info.availableVersionCode()
                    if (storeCode > 0) {
                        playStoreVersionCode = storeCode
                    }
                }
            }
        } catch (ignored: Exception) {}
    }

    // Version Check: Only show the update dialog if the Play Store version code is
    // strictly greater than the currently installed version code (BuildConfig.VERSION_CODE & effectiveInstalled),
    // preventing any infinite update loops.
    val isStrictlyGreater = playStoreVersionCode > BuildConfig.VERSION_CODE &&
            playStoreVersionCode > currentInstalledVersionCode

    if (!isStrictlyGreater || !updateInfo.isUpdateAvailable) {
        // Automatically dismiss dialog and do not show if version is not strictly greater
        LaunchedEffect(Unit) {
            onDismiss()
        }
        return
    }

    Dialog(
        onDismissRequest = {
            if (!updateInfo.isMandatory && !updateInfo.isDownloading) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = !updateInfo.isMandatory && !updateInfo.isDownloading,
            dismissOnClickOutside = !updateInfo.isMandatory && !updateInfo.isDownloading
        )
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .testTag("in_app_update_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimaryFixed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SystemUpdate,
                                contentDescription = "System Update",
                                tint = Primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Update Available",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = if (updateInfo.updateChannel == com.example.model.UpdateChannel.GOOGLE_PLAY)
                                        "Google Play In-App Update"
                                    else
                                        "Production APK Update",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    text = "• ${updateInfo.latestVersionName}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = OnSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }

                    if (!updateInfo.isMandatory && !updateInfo.isDownloading) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = OnSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Version Badge Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CURRENT VERSION",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = OnSurfaceVariant,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "v${updateInfo.currentVersionName} (Build $currentInstalledVersionCode)",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = OnSurface
                                )
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(18.dp)
                        )

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "NEW RELEASE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = EmeraldSuccess,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "v${updateInfo.latestVersionName} (Build $playStoreVersionCode)",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSuccess
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // What's New List
                Text(
                    text = "WHAT'S NEW",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    updateInfo.releaseNotes.forEach { note ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 6.dp)
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Primary)
                            )
                            Text(
                                text = note,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = OnSurface,
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Downloading Progress Bar
                if (updateInfo.isDownloading) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Downloading production update...",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Text(
                                text = "${(updateInfo.downloadProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                )
                            )
                        }
                        LinearProgressIndicator(
                            progress = { updateInfo.downloadProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = Primary,
                            trackColor = SurfaceContainerHigh
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Ready to install status indicator
                if (updateInfo.isReadyToInstall) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Secondary.copy(alpha = 0.12f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Secondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Package verified and ready to apply",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Secondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!updateInfo.isMandatory && !updateInfo.isDownloading) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(0.9f),
                            shape = RoundedCornerShape(12.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(OutlineVariant)
                            )
                        ) {
                            Text(
                                text = if (updateInfo.isReadyToInstall) "Done" else "Later",
                                color = OnSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (updateInfo.isReadyToInstall) {
                                onInstall()
                            } else if (!updateInfo.isDownloading) {
                                onStartDownload()
                            }
                        },
                        enabled = !updateInfo.isDownloading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("update_action_button")
                    ) {
                        Icon(
                            imageVector = if (updateInfo.isReadyToInstall) Icons.Default.InstallMobile else Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when {
                                updateInfo.isReadyToInstall -> "Install Now"
                                updateInfo.updateChannel == com.example.model.UpdateChannel.GOOGLE_PLAY -> "Update via Play"
                                else -> "Update Now"
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Global Theme State for DayMeet
var isAppInDarkMode: Boolean = false

// DayMeet Kinetic Minimalist Theme Colors (Adaptive to Light & Dark Theme Branding)
val Surface: Color get() = if (isAppInDarkMode) Color(0xFF0F172A) else Color(0xFFFAF9FF)
val SurfaceDim: Color get() = if (isAppInDarkMode) Color(0xFF0B1120) else Color(0xFFD7D9E7)
val SurfaceBright: Color get() = if (isAppInDarkMode) Color(0xFF1E293B) else Color(0xFFFAF9FF)
val SurfaceContainerLowest: Color get() = if (isAppInDarkMode) Color(0xFF1E293B) else Color(0xFFFFFFFF)
val SurfaceContainerLow: Color get() = if (isAppInDarkMode) Color(0xFF1E293B) else Color(0xFFF1F3FF)
val SurfaceContainer: Color get() = if (isAppInDarkMode) Color(0xFF1E293B) else Color(0xFFEBEDFB)
val SurfaceContainerHigh: Color get() = if (isAppInDarkMode) Color(0xFF334155) else Color(0xFFE5E8F5)
val SurfaceContainerHighest: Color get() = if (isAppInDarkMode) Color(0xFF475569) else Color(0xFFDFE2EF)
val SurfaceVariant: Color get() = if (isAppInDarkMode) Color(0xFF334155) else Color(0xFFDFE2EF)

val OnSurface: Color get() = if (isAppInDarkMode) Color(0xFFF8FAFC) else Color(0xFF181B25)
val OnSurfaceVariant: Color get() = if (isAppInDarkMode) Color(0xFF94A3B8) else Color(0xFF464555)
val InverseSurface: Color get() = if (isAppInDarkMode) Color(0xFFF8FAFC) else Color(0xFF2C303A)
val InverseOnSurface: Color get() = if (isAppInDarkMode) Color(0xFF0F172A) else Color(0xFFEEF0FE)

val Outline: Color get() = if (isAppInDarkMode) Color(0xFF64748B) else Color(0xFF777587)
val OutlineVariant: Color get() = if (isAppInDarkMode) Color(0xFF334155) else Color(0xFFC7C4D8)
val SurfaceTint: Color get() = if (isAppInDarkMode) Color(0xFF818CF8) else Color(0xFF4D44E3)

val Primary: Color get() = if (isAppInDarkMode) Color(0xFF818CF8) else Color(0xFF3525CD)
val OnPrimary: Color get() = if (isAppInDarkMode) Color(0xFF0F172A) else Color(0xFFFFFFFF)
val PrimaryContainer: Color get() = if (isAppInDarkMode) Color(0xFF4F46E5) else Color(0xFF4F46E5)
val OnPrimaryContainer: Color get() = if (isAppInDarkMode) Color(0xFFE0E7FF) else Color(0xFFDAD7FF)
val InversePrimary: Color get() = if (isAppInDarkMode) Color(0xFF3525CD) else Color(0xFFC3C0FF)
val PrimaryFixed: Color get() = if (isAppInDarkMode) Color(0xFF312E81) else Color(0xFFE2DFFF)
val PrimaryFixedDim: Color get() = if (isAppInDarkMode) Color(0xFF3730A3) else Color(0xFFC3C0FF)
val OnPrimaryFixed: Color get() = if (isAppInDarkMode) Color(0xFFE0E7FF) else Color(0xFF0F0069)
val OnPrimaryFixedVariant: Color get() = if (isAppInDarkMode) Color(0xFFC7D2FE) else Color(0xFF3323CC)

val Secondary: Color get() = if (isAppInDarkMode) Color(0xFF38BDF8) else Color(0xFF006591)
val OnSecondary: Color get() = if (isAppInDarkMode) Color(0xFF0F172A) else Color(0xFFFFFFFF)
val SecondaryContainer: Color get() = if (isAppInDarkMode) Color(0xFF0284C7) else Color(0xFF39B8FD)
val OnSecondaryContainer: Color get() = if (isAppInDarkMode) Color(0xFFE0F2FE) else Color(0xFF004666)
val SecondaryFixed: Color get() = if (isAppInDarkMode) Color(0xFF075985) else Color(0xFFC9E6FF)
val SecondaryFixedDim: Color get() = if (isAppInDarkMode) Color(0xFF0369A1) else Color(0xFF89CEFF)
val OnSecondaryFixed: Color get() = if (isAppInDarkMode) Color(0xFFE0F2FE) else Color(0xFF001E2F)
val OnSecondaryFixedVariant: Color get() = if (isAppInDarkMode) Color(0xFFBAE6FD) else Color(0xFF004C6E)

val Tertiary: Color get() = if (isAppInDarkMode) Color(0xFF34D399) else Color(0xFF005338)
val OnTertiary: Color get() = if (isAppInDarkMode) Color(0xFF064E3B) else Color(0xFFFFFFFF)
val TertiaryContainer: Color get() = if (isAppInDarkMode) Color(0xFF059669) else Color(0xFF006E4B)
val OnTertiaryContainer: Color get() = if (isAppInDarkMode) Color(0xFFD1FAE5) else Color(0xFF67F4B7)
val TertiaryFixed: Color get() = if (isAppInDarkMode) Color(0xFF065F46) else Color(0xFF6FFBBE)
val TertiaryFixedDim: Color get() = if (isAppInDarkMode) Color(0xFF047857) else Color(0xFF4EDEA3)
val OnTertiaryFixed: Color get() = if (isAppInDarkMode) Color(0xFFD1FAE5) else Color(0xFF002113)
val OnTertiaryFixedVariant: Color get() = if (isAppInDarkMode) Color(0xFFA7F3D0) else Color(0xFF005236)

val Error = Color(0xFFBA1A1A)
val OnError = Color(0xFFFFFFFF)
val ErrorContainer = Color(0xFFFFDAD6)
val OnErrorContainer = Color(0xFF93000A)

val Background: Color get() = if (isAppInDarkMode) Color(0xFF0F172A) else Color(0xFFFAF9FF)
val OnBackground: Color get() = if (isAppInDarkMode) Color(0xFFF8FAFC) else Color(0xFF181B25)

// Accent shades for metrics and tags
val EmeraldSuccess = Color(0xFF10B981)
val EmeraldLight = Color(0xFFD1FAE5)
val SkyBlue = Color(0xFF0EA5E9)
val SkyLight = Color(0xFFE0F2FE)
val AmberWarning = Color(0xFFF59E0B)
val AmberLight = Color(0xFFFEF3C7)
val IndigoAccent = Color(0xFF4F46E5)
val IndigoLight = Color(0xFFEEF2FF)

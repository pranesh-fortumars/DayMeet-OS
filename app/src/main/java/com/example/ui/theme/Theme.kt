package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    inversePrimary = InversePrimary,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    surfaceTint = SurfaceTint,
    inverseSurface = InverseSurface,
    inverseOnSurface = InverseOnSurface,
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    outline = Outline,
    outlineVariant = OutlineVariant,
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF818CF8), // Tailwind Indigo-400
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF4F46E5), // Tailwind Indigo-600
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = Color(0xFF38BDF8), // Tailwind Sky-400
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF0284C7),
    onSecondaryContainer = Color(0xFFE0F2FE),
    tertiary = Color(0xFF34D399), // Tailwind Emerald-400
    onTertiary = Color(0xFF064E3B),
    tertiaryContainer = Color(0xFF059669),
    onTertiaryContainer = Color(0xFFD1FAE5),
    background = Color(0xFF0F172A), // Tailwind Slate-900
    onBackground = Color(0xFFF8FAFC), // Tailwind Slate-50
    surface = Color(0xFF1E293B), // Tailwind Slate-800
    onSurface = Color(0xFFF8FAFC), // Tailwind Slate-50
    surfaceVariant = Color(0xFF334155), // Tailwind Slate-700
    onSurfaceVariant = Color(0xFFCBD5E1), // Tailwind Slate-300
    outline = Color(0xFF64748B), // Tailwind Slate-500
    outlineVariant = Color(0xFF334155), // Tailwind Slate-700
    inverseSurface = Color(0xFFF8FAFC),
    inverseOnSurface = Color(0xFF0F172A)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = colorScheme.background,
            contentColor = colorScheme.onBackground
        ) {
            content()
        }
    }
}

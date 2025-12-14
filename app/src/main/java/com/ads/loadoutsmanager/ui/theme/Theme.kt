package com.ads.loadoutsmanager.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

val LocalLoadoutTheme = staticCompositionLocalOf<LoadoutTheme> { LoadoutTheme.Default }

@Composable
fun LoadoutsManagerTheme(
    theme: LoadoutTheme = LoadoutTheme.Default,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalLoadoutTheme provides theme) {
        MaterialTheme(
            colorScheme = theme.colorScheme,
            typography = Typography,
            content = content
        )
    }
}
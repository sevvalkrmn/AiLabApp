package com.ktun.ailabapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * MD3 çekirdek renk şeması — açık tema.
 * Kaynak: Color.kt > Md3Light* sabitleri (seed #07137F, TonalSpot).
 */
private val AiLabLightColorScheme: ColorScheme = lightColorScheme(
    primary = Md3LightPrimary,
    onPrimary = Md3LightOnPrimary,
    primaryContainer = Md3LightPrimaryContainer,
    onPrimaryContainer = Md3LightOnPrimaryContainer,
    secondary = Md3LightSecondary,
    onSecondary = Md3LightOnSecondary,
    secondaryContainer = Md3LightSecondaryContainer,
    onSecondaryContainer = Md3LightOnSecondaryContainer,
    tertiary = Md3LightTertiary,
    onTertiary = Md3LightOnTertiary,
    tertiaryContainer = Md3LightTertiaryContainer,
    onTertiaryContainer = Md3LightOnTertiaryContainer,
    error = Md3LightError,
    onError = Md3LightOnError,
    errorContainer = Md3LightErrorContainer,
    onErrorContainer = Md3LightOnErrorContainer,
    background = Md3LightBackground,
    onBackground = Md3LightOnBackground,
    surface = Md3LightSurface,
    onSurface = Md3LightOnSurface,
    surfaceVariant = Md3LightSurfaceVariant,
    onSurfaceVariant = Md3LightOnSurfaceVariant,
    outline = Md3LightOutline,
    outlineVariant = Md3LightOutlineVariant,
    scrim = Md3LightScrim,
    inverseSurface = Md3LightInverseSurface,
    inverseOnSurface = Md3LightInverseOnSurface,
    inversePrimary = Md3LightInversePrimary,
    surfaceDim = Md3LightSurfaceDim,
    surfaceBright = Md3LightSurfaceBright,
    surfaceContainerLowest = Md3LightSurfaceContainerLowest,
    surfaceContainerLow = Md3LightSurfaceContainerLow,
    surfaceContainer = Md3LightSurfaceContainer,
    surfaceContainerHigh = Md3LightSurfaceContainerHigh,
    surfaceContainerHighest = Md3LightSurfaceContainerHighest,
)

/**
 * MD3 çekirdek renk şeması — koyu tema.
 * Kaynak: Color.kt > Md3Dark* sabitleri (seed #07137F, TonalSpot).
 */
private val AiLabDarkColorScheme: ColorScheme = darkColorScheme(
    primary = Md3DarkPrimary,
    onPrimary = Md3DarkOnPrimary,
    primaryContainer = Md3DarkPrimaryContainer,
    onPrimaryContainer = Md3DarkOnPrimaryContainer,
    secondary = Md3DarkSecondary,
    onSecondary = Md3DarkOnSecondary,
    secondaryContainer = Md3DarkSecondaryContainer,
    onSecondaryContainer = Md3DarkOnSecondaryContainer,
    tertiary = Md3DarkTertiary,
    onTertiary = Md3DarkOnTertiary,
    tertiaryContainer = Md3DarkTertiaryContainer,
    onTertiaryContainer = Md3DarkOnTertiaryContainer,
    error = Md3DarkError,
    onError = Md3DarkOnError,
    errorContainer = Md3DarkErrorContainer,
    onErrorContainer = Md3DarkOnErrorContainer,
    background = Md3DarkBackground,
    onBackground = Md3DarkOnBackground,
    surface = Md3DarkSurface,
    onSurface = Md3DarkOnSurface,
    surfaceVariant = Md3DarkSurfaceVariant,
    onSurfaceVariant = Md3DarkOnSurfaceVariant,
    outline = Md3DarkOutline,
    outlineVariant = Md3DarkOutlineVariant,
    scrim = Md3DarkScrim,
    inverseSurface = Md3DarkInverseSurface,
    inverseOnSurface = Md3DarkInverseOnSurface,
    inversePrimary = Md3DarkInversePrimary,
    surfaceDim = Md3DarkSurfaceDim,
    surfaceBright = Md3DarkSurfaceBright,
    surfaceContainerLowest = Md3DarkSurfaceContainerLowest,
    surfaceContainerLow = Md3DarkSurfaceContainerLow,
    surfaceContainer = Md3DarkSurfaceContainer,
    surfaceContainerHigh = Md3DarkSurfaceContainerHigh,
    surfaceContainerHighest = Md3DarkSurfaceContainerHighest,
)

/**
 * MD3'ün çekirdek rol setinde karşılığı olmayan, uygulamaya özel semantik
 * renkler (başarı / uyarı / bilgi durumları, rütbe rozetleri).
 * Bkz. Color.kt "GENİŞLETİLMİŞ (MARKA-ÖZEL) SEMANTİK RENKLER" bölümü.
 */
data class AiLabExtendedColors(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val info: Color,
    val onInfo: Color,
    val infoContainer: Color,
    val onInfoContainer: Color,
    val gold: Color,
    val silver: Color,
    val bronze: Color,
    /** Butonun "basılı" (pressed) durumundaki dolgu rengi. Sadece koyu
     *  temada normalden farklıdır (bkz. AiLabButton); açık temada mevcut
     *  primary ile aynıdır, yani görsel bir değişikliğe yol açmaz. */
    val primaryPressed: Color,
    /** TextField odak kenarlığı/imleci, bağlantı metinleri ve seçili
     *  bileşenler için vurgu rengi. Açık temada primary ile birebir
     *  aynıdır (mevcut davranışı korur); koyu temada daha açık, canlı bir
     *  mavi tona (DarkPrimaryLight) karşılık gelir. */
    val focusAccent: Color,
    /** Devre dışı içerik ve placeholder metni. Açık temada MD3'ün
     *  standart %38 alfa'lı onSurface değeriyle aynıdır; koyu temada
     *  sabit bir tondur (DarkTextDisabled). */
    val textDisabled: Color,
)

private val LightExtendedColors = AiLabExtendedColors(
    success = Md3LightSuccess,
    onSuccess = Md3LightOnSuccess,
    successContainer = Md3LightSuccessContainer,
    onSuccessContainer = Md3LightOnSuccessContainer,
    warning = Md3LightWarning,
    onWarning = Md3LightOnWarning,
    warningContainer = Md3LightWarningContainer,
    onWarningContainer = Md3LightOnWarningContainer,
    info = Md3LightInfo,
    onInfo = Md3LightOnInfo,
    infoContainer = Md3LightInfoContainer,
    onInfoContainer = Md3LightOnInfoContainer,
    gold = Md3RankGold,
    silver = Md3RankSilver,
    bronze = Md3RankBronze,
    primaryPressed = Md3LightPrimary,
    focusAccent = Md3LightPrimary,
    textDisabled = Md3LightOnSurface.copy(alpha = 0.38f),
)

private val DarkExtendedColors = AiLabExtendedColors(
    success = Md3DarkSuccess,
    onSuccess = Md3DarkOnSuccess,
    successContainer = Md3DarkSuccessContainer,
    onSuccessContainer = Md3DarkOnSuccessContainer,
    warning = Md3DarkWarning,
    onWarning = Md3DarkOnWarning,
    warningContainer = Md3DarkWarningContainer,
    onWarningContainer = Md3DarkOnWarningContainer,
    info = Md3DarkInfo,
    onInfo = Md3DarkOnInfo,
    infoContainer = Md3DarkInfoContainer,
    onInfoContainer = Md3DarkOnInfoContainer,
    gold = Md3RankGold,
    silver = Md3RankSilver,
    bronze = Md3RankBronze,
    primaryPressed = Md3DarkPrimaryPressed,
    focusAccent = Md3DarkFocusAccent,
    textDisabled = Md3DarkTextDisabled,
)

private val LocalAiLabExtendedColors = staticCompositionLocalOf { LightExtendedColors }
private val LocalAiLabIsDarkTheme = staticCompositionLocalOf { false }

/**
 * Genişletilmiş renklere MaterialTheme'e benzer şekilde erişim sağlar:
 *   AiLabTheme.extendedColors.success
 *
 * [isDark], aktif temanın koyu olup olmadığını söyler. Bileşenlerin, sadece
 * koyu temada davranışı/rengi değişen (açık temayı hiç etkilememesi gereken)
 * özel durumları uygulaması için kullanılır — bkz. AiLabButton, AiLabTextField.
 */
object AiLabTheme {
    val extendedColors: AiLabExtendedColors
        @Composable get() = LocalAiLabExtendedColors.current
    val isDark: Boolean
        @Composable get() = LocalAiLabIsDarkTheme.current

    /**
     * Başlık/vurgu metni ve ikonları için renk. Açık temada markanın
     * `primary` rengiyle birebir aynıdır (mevcut görünüm korunur). Koyu
     * temada `onSurface` (beyaza yakın, #F5F6FF) kullanılır — çünkü koyu
     * arka plan üzerinde `primary` mor/mavi tonu olarak algılanıyordu.
     * Kart/buton/rozet ZEMİNİ için hâlâ `MaterialTheme.colorScheme.primary`
     * kullanılmalı; bu sadece METİN ve İKON rengi içindir.
     */
    val headlineColor: Color
        @Composable get() = if (isDark) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary
}

@Composable
fun AiLabAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) AiLabDarkColorScheme else AiLabLightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(
        LocalAiLabExtendedColors provides extendedColors,
        LocalAiLabIsDarkTheme provides darkTheme,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = AppShapes,
            content = content
        )
    }
}

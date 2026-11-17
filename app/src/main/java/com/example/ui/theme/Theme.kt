package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class HealthColors(
  val ivoryBackground: Color = IvoryBackground,
  val headerSurface: Color = IvoryHeaderSurface,
  val cardSurface: Color = IvoryCardSurface,
  val warmBorder: Color = WarmBorder,
  val inkDark: Color = InkDark,
  val inkMuted: Color = InkMuted,
  val medicineGreen: Color = MedicineGreen,
  val medicineGreenContainer: Color = MedicineGreenContainer,
  val medicineGreenDark: Color = MedicineGreenDark,
  val waterBlue: Color = WaterBlue,
  val waterBlueContainer: Color = WaterBlueContainer,
  val waterBlueDark: Color = WaterBlueDark,
  val waterSkyFill: Color = WaterSkyFill,
  val waterGlassOutline: Color = WaterGlassOutline,
  val waterGlassEmpty: Color = WaterGlassEmpty,
  val morningTint: Color = MorningAmberTint,
  val morningIcon: Color = MorningAmberIcon,
  val lunchTint: Color = LunchTint,
  val lunchIcon: Color = LunchIcon,
  val dinnerTint: Color = DinnerIndigoTint,
  val dinnerIcon: Color = DinnerIndigoIcon,
)

val LocalHealthColors = staticCompositionLocalOf { HealthColors() }

private val SeniorLightColorScheme =
  lightColorScheme(
    primary = MedicineGreen,
    onPrimary = Color.White,
    primaryContainer = MedicineGreenContainer,
    onPrimaryContainer = MedicineGreenDark,
    secondary = WaterBlue,
    onSecondary = Color.White,
    secondaryContainer = WaterBlueContainer,
    onSecondaryContainer = WaterBlueDark,
    background = IvoryBackground,
    onBackground = InkDark,
    surface = IvoryCardSurface,
    onSurface = InkDark,
    surfaceVariant = IvoryHeaderSurface,
    onSurfaceVariant = InkMuted,
    outline = WarmBorder,
    outlineVariant = WarmBorder,
  )

@Composable
fun SeniorHealthTheme(content: @Composable () -> Unit) {
  CompositionLocalProvider(LocalHealthColors provides HealthColors()) {
    MaterialTheme(
      colorScheme = SeniorLightColorScheme,
      typography = SeniorTypography,
      content = content,
    )
  }
}

package com.shuhaibnc.mpvnc.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.shuhaibnc.mpvnc.preferences.AppearancePreferences
import com.shuhaibnc.mpvnc.preferences.preference.collectAsState
import org.koin.compose.koinInject

val ACCENT_COLORS = listOf(
  Color(0xFFFF0000),
  Color(0xFF2196F3),
  Color(0xFF4CAF50),
  Color(0xFF9C27B0),
  Color(0xFFFF9800),
  Color(0xFFE91E63),
  Color(0xFF009688),
  Color(0xFFFFC107),
)

val DEFAULT_ACCENT_COLOR = ACCENT_COLORS[0]

@Composable
fun accentColor(): Color {
  val preferences = koinInject<AppearancePreferences>()
  val argb by preferences.accentColor.collectAsState()
  return Color(argb)
}

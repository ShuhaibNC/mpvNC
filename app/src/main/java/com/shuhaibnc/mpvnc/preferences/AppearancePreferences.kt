package com.shuhaibnc.mpvnc.preferences

import android.os.Build
import com.shuhaibnc.mpvnc.preferences.preference.PreferenceStore
import com.shuhaibnc.mpvnc.preferences.preference.getEnum
import com.shuhaibnc.mpvnc.ui.theme.DarkMode

class AppearancePreferences(preferenceStore: PreferenceStore) {
  val darkMode = preferenceStore.getEnum("dark_mode", DarkMode.System)
  val materialYou = preferenceStore.getBoolean("material_you", Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
  val accentColor = preferenceStore.getInt("accent_color", 0xFFFF0000.toInt())
}

package com.shuhaibnc.mpvnc.di

import com.shuhaibnc.mpvnc.preferences.AdvancedPreferences
import com.shuhaibnc.mpvnc.preferences.AppearancePreferences
import com.shuhaibnc.mpvnc.preferences.AudioPreferences
import com.shuhaibnc.mpvnc.preferences.DecoderPreferences
import com.shuhaibnc.mpvnc.preferences.GesturePreferences
import com.shuhaibnc.mpvnc.preferences.PlayerPreferences
import com.shuhaibnc.mpvnc.preferences.SubtitlesPreferences
import com.shuhaibnc.mpvnc.preferences.preference.AndroidPreferenceStore
import com.shuhaibnc.mpvnc.preferences.preference.PreferenceStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val PreferencesModule = module {
  single { AndroidPreferenceStore(androidContext()) }.bind(PreferenceStore::class)

  singleOf(::AppearancePreferences)
  singleOf(::PlayerPreferences)
  singleOf(::GesturePreferences)
  singleOf(::DecoderPreferences)
  singleOf(::SubtitlesPreferences)
  singleOf(::AudioPreferences)
  singleOf(::AdvancedPreferences)
}

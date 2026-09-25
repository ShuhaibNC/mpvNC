package com.shuhaibnc.mpvnc.di

import androidx.room.Room
import com.shuhaibnc.mpvnc.database.Migrations
import com.shuhaibnc.mpvnc.database.MpvKtDatabase
import com.shuhaibnc.mpvnc.database.repository.CustomButtonRepositoryImpl
import com.shuhaibnc.mpvnc.database.repository.PlaybackStateRepositoryImpl
import com.shuhaibnc.mpvnc.domain.custombuttons.repository.CustomButtonRepository
import com.shuhaibnc.mpvnc.domain.playbackstate.repository.PlaybackStateRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val DatabaseModule = module {
  single<MpvKtDatabase> {
    Room
      .databaseBuilder(androidContext(), MpvKtDatabase::class.java, "mpvKt.db")
      .addMigrations(migrations = Migrations)
      .build()
  }

  singleOf(::CustomButtonRepositoryImpl).bind(CustomButtonRepository::class)
  singleOf(::PlaybackStateRepositoryImpl).bind(PlaybackStateRepository::class)
}

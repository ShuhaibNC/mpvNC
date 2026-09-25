package com.shuhaibnc.mpvnc.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shuhaibnc.mpvnc.database.dao.CustomButtonDao
import com.shuhaibnc.mpvnc.database.dao.PlaybackStateDao
import com.shuhaibnc.mpvnc.database.entities.CustomButtonEntity
import com.shuhaibnc.mpvnc.database.entities.PlaybackStateEntity

@Database(entities = [PlaybackStateEntity::class, CustomButtonEntity::class], version = 5)
abstract class MpvKtDatabase : RoomDatabase() {
  abstract fun videoDataDao(): PlaybackStateDao
  abstract fun customButtonDao(): CustomButtonDao
}

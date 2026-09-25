package com.shuhaibnc.mpvnc.domain.custombuttons.repository

import com.shuhaibnc.mpvnc.database.entities.CustomButtonEntity
import kotlinx.coroutines.flow.Flow

interface CustomButtonRepository {
  fun getCustomButtons(): Flow<List<CustomButtonEntity>>

  suspend fun upsert(customButtonEntity: CustomButtonEntity)

  suspend fun deleteAndReindex(customButtonEntity: CustomButtonEntity)

  suspend fun increaseIndex(customButtonEntity: CustomButtonEntity)

  suspend fun decreaseIndex(customButtonEntity: CustomButtonEntity)

  suspend fun updateButton(customButtonEntity: CustomButtonEntity)
}

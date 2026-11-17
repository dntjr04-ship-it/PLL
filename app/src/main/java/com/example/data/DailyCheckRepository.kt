package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DailyCheckRepository(private val dao: DailyCheckDao) {
  fun observeDay(dateKey: String): Flow<DailyCheckEntity> {
    return dao.observeByDate(dateKey).map { it ?: DailyCheckEntity(dateKey = dateKey) }
  }

  suspend fun toggleMorning(current: DailyCheckEntity) {
    dao.upsert(current.copy(morningTaken = !current.morningTaken))
  }

  suspend fun toggleLunch(current: DailyCheckEntity) {
    dao.upsert(current.copy(lunchTaken = !current.lunchTaken))
  }

  suspend fun toggleDinner(current: DailyCheckEntity) {
    dao.upsert(current.copy(dinnerTaken = !current.dinnerTaken))
  }

  suspend fun incrementWater(current: DailyCheckEntity) {
    dao.upsert(current.copy(waterCups = current.waterCups + 1))
  }

  suspend fun decrementWater(current: DailyCheckEntity) {
    if (current.waterCups > 0) {
      dao.upsert(current.copy(waterCups = current.waterCups - 1))
    }
  }

  suspend fun clearPreviousDays(todayKey: String) {
    dao.deletePreviousDays(todayKey)
  }
}

package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyCheckDao {
  @Query("SELECT * FROM daily_checks WHERE dateKey = :dateKey")
  fun observeByDate(dateKey: String): Flow<DailyCheckEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsert(entity: DailyCheckEntity)

  @Query("DELETE FROM daily_checks WHERE dateKey != :todayKey")
  suspend fun deletePreviousDays(todayKey: String)
}

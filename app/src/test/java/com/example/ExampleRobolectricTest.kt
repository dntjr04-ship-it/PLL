package com.example

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.DailyCheckEntity
import com.example.data.DailyCheckRepository
import com.example.ui.DailyCheckScreen
import com.example.ui.DailyCheckUiState
import com.example.ui.DailyCheckViewModel
import com.example.ui.theme.SeniorHealthTheme
import com.example.util.DateUtils
import java.util.Calendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun `read app name string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("건강 체크", appName)
  }

  @Test
  fun `daily check repository persists medicine and water state`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db =
      Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries().build()
    try {
      val repo = DailyCheckRepository(db.dailyCheckDao())
      val dateKey = "2026-11-16"

      val initial = repo.observeDay(dateKey).first()
      assertEquals(DailyCheckEntity(dateKey = dateKey), initial)

      repo.toggleMorning(initial)
      val afterMorning = repo.observeDay(dateKey).first()
      assertTrue(afterMorning.morningTaken)

      repo.incrementWater(afterMorning)
      repo.incrementWater(afterMorning.copy(waterCups = 1))
      val afterWater = repo.observeDay(dateKey).first()
      assertEquals(2, afterWater.waterCups)
    } finally {
      db.close()
    }
  }

  @Test
  fun `millisUntilNextMidnight computes positive duration to next midnight`() {
    val cal =
      Calendar.getInstance().apply {
        set(2026, Calendar.NOVEMBER, 16, 23, 59, 0)
        set(Calendar.MILLISECOND, 0)
      }
    val millis = DateUtils.millisUntilNextMidnight(cal.timeInMillis)
    assertEquals(60_050L, millis)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `midnight rollover resets yesterday medicine checks and water cups`() = runBlocking {
    val testDispatcher = UnconfinedTestDispatcher()
    Dispatchers.setMain(testDispatcher)
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db =
      Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries().build()
    try {
      val repo = DailyCheckRepository(db.dailyCheckDao())
      var currentDate = "2026-11-16"

      // Seed yesterday's completed checks
      db.dailyCheckDao()
        .upsert(
          DailyCheckEntity(
            dateKey = "2026-11-16",
            morningTaken = true,
            lunchTaken = true,
            dinnerTaken = true,
            waterCups = 5,
          )
        )

      val viewModel =
        DailyCheckViewModel(
          repository = repo,
          dateKeyProvider = { currentDate },
          millisUntilMidnightProvider = { 60_000_000L },
        )

      val collectJob = launch(testDispatcher) { viewModel.uiState.collect {} }

      val beforeMidnight =
        viewModel.uiState.first {
          it is DailyCheckUiState.Ready && it.dateKey == "2026-11-16"
        } as DailyCheckUiState.Ready
      assertTrue(beforeMidnight.morningTaken)
      assertEquals(5, beforeMidnight.waterCups)

      // Cross midnight to the next day
      currentDate = "2026-11-17"
      viewModel.refreshDateIfChanged()

      val afterMidnight =
        viewModel.uiState.first {
          it is DailyCheckUiState.Ready && it.dateKey == "2026-11-17"
        } as DailyCheckUiState.Ready
      assertFalse(afterMidnight.morningTaken)
      assertFalse(afterMidnight.lunchTaken)
      assertFalse(afterMidnight.dinnerTaken)
      assertEquals(0, afterMidnight.waterCups)

      // Yesterday's DB entry was also purged
      val yesterdayEntity = db.dailyCheckDao().observeByDate("2026-11-16").first()
      assertEquals(null, yesterdayEntity)

      collectJob.cancel()
    } finally {
      db.close()
      Dispatchers.resetMain()
    }
  }

  @Test
  fun `daily check screen toggles medicine and increments water cups`() {
    var state by
      mutableStateOf(
        DailyCheckUiState.Ready(
          dateKey = "2026-11-16",
          formattedDate = "2026년 11월 16일 (월)",
          morningTaken = false,
          lunchTaken = false,
          dinnerTaken = false,
          waterCups = 0,
        )
      )

    composeTestRule.setContent {
      SeniorHealthTheme {
        DailyCheckScreen(
          uiState = state,
          onToggleMorning = { state = state.copy(morningTaken = !state.morningTaken) },
          onToggleLunch = { state = state.copy(lunchTaken = !state.lunchTaken) },
          onToggleDinner = { state = state.copy(dinnerTaken = !state.dinnerTaken) },
          onAddWaterCup = { state = state.copy(waterCups = state.waterCups + 1) },
          onRemoveWaterCup = {
            state = state.copy(waterCups = (state.waterCups - 1).coerceAtLeast(0))
          },
        )
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("today_date_text").assertIsDisplayed()
    composeTestRule.onNodeWithTag("medicine_button_morning").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("복용 완료").assertIsDisplayed()

    composeTestRule.onNodeWithTag("water_add_button").performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("water_count_text").assertTextContains("1잔")
  }
}

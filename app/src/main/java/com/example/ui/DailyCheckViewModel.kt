package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.DailyCheckEntity
import com.example.data.DailyCheckRepository
import com.example.util.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

sealed interface DailyCheckUiState {
  data object Loading : DailyCheckUiState

  data class Ready(
    val dateKey: String,
    val formattedDate: String,
    val morningTaken: Boolean,
    val lunchTaken: Boolean,
    val dinnerTaken: Boolean,
    val waterCups: Int,
  ) : DailyCheckUiState {
    val takenCount: Int
      get() = listOf(morningTaken, lunchTaken, dinnerTaken).count { it }

    internal fun toEntity(): DailyCheckEntity =
      DailyCheckEntity(
        dateKey = dateKey,
        morningTaken = morningTaken,
        lunchTaken = lunchTaken,
        dinnerTaken = dinnerTaken,
        waterCups = waterCups,
      )
  }
}

@OptIn(ExperimentalCoroutinesApi::class)
class DailyCheckViewModel(
  private val repository: DailyCheckRepository,
  private val dateKeyProvider: () -> String = { DateUtils.todayKey() },
  private val millisUntilMidnightProvider: () -> Long = { DateUtils.millisUntilNextMidnight() },
) : ViewModel() {

  private val activeDateKey = MutableStateFlow(dateKeyProvider())

  init {
    viewModelScope.launch { repository.clearPreviousDays(activeDateKey.value) }
    viewModelScope.launch {
      while (isActive) {
        delay(millisUntilMidnightProvider())
        refreshDateIfChanged()
      }
    }
  }

  val uiState: StateFlow<DailyCheckUiState> =
    activeDateKey
      .flatMapLatest { key ->
        repository.observeDay(key).map { entity ->
          DailyCheckUiState.Ready(
            dateKey = entity.dateKey,
            formattedDate = DateUtils.formatKoreanDisplayDate(entity.dateKey),
            morningTaken = entity.morningTaken,
            lunchTaken = entity.lunchTaken,
            dinnerTaken = entity.dinnerTaken,
            waterCups = entity.waterCups,
          )
        }
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DailyCheckUiState.Loading,
      )

  fun refreshDateIfChanged() {
    val latest = dateKeyProvider()
    if (activeDateKey.value != latest) {
      activeDateKey.value = latest
      viewModelScope.launch { repository.clearPreviousDays(latest) }
    }
  }

  fun onToggleMorning() {
    val current = (uiState.value as? DailyCheckUiState.Ready)?.toEntity() ?: return
    viewModelScope.launch { repository.toggleMorning(current) }
  }

  fun onToggleLunch() {
    val current = (uiState.value as? DailyCheckUiState.Ready)?.toEntity() ?: return
    viewModelScope.launch { repository.toggleLunch(current) }
  }

  fun onToggleDinner() {
    val current = (uiState.value as? DailyCheckUiState.Ready)?.toEntity() ?: return
    viewModelScope.launch { repository.toggleDinner(current) }
  }

  fun onAddWaterCup() {
    val current = (uiState.value as? DailyCheckUiState.Ready)?.toEntity() ?: return
    viewModelScope.launch { repository.incrementWater(current) }
  }

  fun onRemoveWaterCup() {
    val current = (uiState.value as? DailyCheckUiState.Ready)?.toEntity() ?: return
    viewModelScope.launch { repository.decrementWater(current) }
  }

  class Factory(private val repository: DailyCheckRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return DailyCheckViewModel(repository = repository) as T
    }
  }
}

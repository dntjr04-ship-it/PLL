package com.example

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.DailyCheckRepository
import com.example.ui.DailyCheckScreen
import com.example.ui.DailyCheckViewModel
import com.example.ui.theme.SeniorHealthTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val database = AppDatabase.getInstance(applicationContext)
    val repository = DailyCheckRepository(database.dailyCheckDao())

    setContent {
      SeniorHealthTheme {
        val viewModel: DailyCheckViewModel =
          viewModel(factory = DailyCheckViewModel.Factory(repository))
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner, context) {
          val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
              viewModel.refreshDateIfChanged()
            }
          }
          lifecycleOwner.lifecycle.addObserver(observer)

          val dateChangeReceiver =
            object : BroadcastReceiver() {
              override fun onReceive(ctx: Context?, intent: Intent?) {
                viewModel.refreshDateIfChanged()
              }
            }
          val dateFilter =
            IntentFilter().apply {
              addAction(Intent.ACTION_DATE_CHANGED)
              addAction(Intent.ACTION_TIME_CHANGED)
              addAction(Intent.ACTION_TIMEZONE_CHANGED)
            }
          ContextCompat.registerReceiver(
            context,
            dateChangeReceiver,
            dateFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED,
          )

          onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            context.unregisterReceiver(dateChangeReceiver)
          }
        }

        DailyCheckScreen(
          uiState = uiState,
          onToggleMorning = viewModel::onToggleMorning,
          onToggleLunch = viewModel::onToggleLunch,
          onToggleDinner = viewModel::onToggleDinner,
          onAddWaterCup = viewModel::onAddWaterCup,
          onRemoveWaterCup = viewModel::onRemoveWaterCup,
        )
      }
    }
  }
}

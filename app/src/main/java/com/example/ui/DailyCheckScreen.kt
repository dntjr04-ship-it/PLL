package com.example.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.LocalHealthColors

private const val DAILY_WATER_GOAL = 8
private const val TOTAL_MEDICINE_COUNT = 3

@Composable
fun DailyCheckScreen(
  uiState: DailyCheckUiState,
  onToggleMorning: () -> Unit,
  onToggleLunch: () -> Unit,
  onToggleDinner: () -> Unit,
  onAddWaterCup: () -> Unit,
  onRemoveWaterCup: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val colors = LocalHealthColors.current

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = colors.ivoryBackground,
    contentWindowInsets = WindowInsets.safeDrawing,
  ) { innerPadding ->
    when (uiState) {
      DailyCheckUiState.Loading -> {
        Box(
          modifier = Modifier.fillMaxSize().padding(innerPadding),
          contentAlignment = Alignment.Center,
        ) {
          CircularProgressIndicator(
            modifier = Modifier.size(56.dp),
            color = colors.medicineGreen,
            strokeWidth = 6.dp,
          )
        }
      }

      is DailyCheckUiState.Ready -> {
        BoxWithConstraints(
          modifier = Modifier.fillMaxSize().padding(innerPadding),
          contentAlignment = Alignment.TopCenter,
        ) {
          val isExpanded = maxWidth >= 720.dp
          val scrollState = rememberScrollState()

          if (isExpanded) {
            Row(
              modifier =
                Modifier.fillMaxSize()
                  .verticalScroll(scrollState)
                  .padding(horizontal = 28.dp, vertical = 20.dp),
              horizontalArrangement = Arrangement.spacedBy(24.dp),
              verticalAlignment = Alignment.Top,
            ) {
              Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(20.dp),
              ) {
                DateHeaderCard(
                  formattedDate = uiState.formattedDate,
                  takenCount = uiState.takenCount,
                )
                MedicineListSection(
                  morningTaken = uiState.morningTaken,
                  lunchTaken = uiState.lunchTaken,
                  dinnerTaken = uiState.dinnerTaken,
                  onToggleMorning = onToggleMorning,
                  onToggleLunch = onToggleLunch,
                  onToggleDinner = onToggleDinner,
                )
              }

              Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(20.dp),
              ) {
                WaterTrackingSection(
                  waterCups = uiState.waterCups,
                  onAddWaterCup = onAddWaterCup,
                  onRemoveWaterCup = onRemoveWaterCup,
                )
              }
            }
          } else {
            Column(
              modifier =
                Modifier.fillMaxWidth()
                  .widthIn(max = 600.dp)
                  .verticalScroll(scrollState)
                  .padding(horizontal = 20.dp, vertical = 16.dp),
              verticalArrangement = Arrangement.spacedBy(22.dp),
            ) {
              DateHeaderCard(
                formattedDate = uiState.formattedDate,
                takenCount = uiState.takenCount,
              )

              MedicineListSection(
                morningTaken = uiState.morningTaken,
                lunchTaken = uiState.lunchTaken,
                dinnerTaken = uiState.dinnerTaken,
                onToggleMorning = onToggleMorning,
                onToggleLunch = onToggleLunch,
                onToggleDinner = onToggleDinner,
              )

              WaterTrackingSection(
                waterCups = uiState.waterCups,
                onAddWaterCup = onAddWaterCup,
                onRemoveWaterCup = onRemoveWaterCup,
              )

              Spacer(modifier = Modifier.height(8.dp))
            }
          }
        }
      }
    }
  }
}

@Composable
private fun DateHeaderCard(formattedDate: String, takenCount: Int) {
  val colors = LocalHealthColors.current
  val allMedsDone = takenCount == TOTAL_MEDICINE_COUNT

  Surface(
    modifier = Modifier.fillMaxWidth().testTag("date_header_card"),
    shape = RoundedCornerShape(24.dp),
    color = colors.headerSurface,
    border = BorderStroke(1.5.dp, colors.warmBorder),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 20.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Icon(
          imageVector = Icons.Rounded.CalendarToday,
          contentDescription = null,
          tint = colors.medicineGreen,
          modifier = Modifier.size(30.dp),
        )
        Text(
          text = formattedDate,
          style = MaterialTheme.typography.headlineLarge,
          color = colors.inkDark,
          modifier = Modifier.testTag("today_date_text"),
        )
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        if (allMedsDone) {
          Icon(
            imageVector = Icons.Rounded.CheckCircle,
            contentDescription = null,
            tint = colors.medicineGreen,
            modifier = Modifier.size(26.dp),
          )
        }
        Text(
          text =
            if (allMedsDone) {
              stringResource(R.string.header_all_meds_done)
            } else {
              stringResource(R.string.header_subtitle_format, TOTAL_MEDICINE_COUNT, takenCount)
            },
          style = MaterialTheme.typography.titleMedium,
          color = if (allMedsDone) colors.medicineGreenDark else colors.inkMuted,
          fontWeight = if (allMedsDone) FontWeight.Bold else FontWeight.SemiBold,
        )
      }
    }
  }
}

@Composable
private fun MedicineListSection(
  morningTaken: Boolean,
  lunchTaken: Boolean,
  dinnerTaken: Boolean,
  onToggleMorning: () -> Unit,
  onToggleLunch: () -> Unit,
  onToggleDinner: () -> Unit,
) {
  val colors = LocalHealthColors.current

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(18.dp),
  ) {
    MedicineCheckButton(
      label = stringResource(R.string.med_morning),
      isTaken = morningTaken,
      icon = Icons.Rounded.WbSunny,
      uncheckedIconTint = colors.morningIcon,
      uncheckedIconBg = colors.morningTint,
      onClick = onToggleMorning,
      testTag = "medicine_button_morning",
    )

    MedicineCheckButton(
      label = stringResource(R.string.med_lunch),
      isTaken = lunchTaken,
      icon = Icons.Rounded.LightMode,
      uncheckedIconTint = colors.lunchIcon,
      uncheckedIconBg = colors.lunchTint,
      onClick = onToggleLunch,
      testTag = "medicine_button_lunch",
    )

    MedicineCheckButton(
      label = stringResource(R.string.med_dinner),
      isTaken = dinnerTaken,
      icon = Icons.Rounded.NightsStay,
      uncheckedIconTint = colors.dinnerIcon,
      uncheckedIconBg = colors.dinnerTint,
      onClick = onToggleDinner,
      testTag = "medicine_button_dinner",
    )
  }
}

@Composable
private fun MedicineCheckButton(
  label: String,
  isTaken: Boolean,
  icon: ImageVector,
  uncheckedIconTint: Color,
  uncheckedIconBg: Color,
  onClick: () -> Unit,
  testTag: String,
) {
  val colors = LocalHealthColors.current
  val haptics = LocalHapticFeedback.current

  val containerColor by
    animateColorAsState(
      targetValue = if (isTaken) colors.medicineGreen else colors.cardSurface,
      animationSpec = tween(durationMillis = 220),
      label = "med_container_color",
    )
  val borderColor by
    animateColorAsState(
      targetValue = if (isTaken) colors.medicineGreen else colors.warmBorder,
      animationSpec = tween(durationMillis = 220),
      label = "med_border_color",
    )
  val titleColor by
    animateColorAsState(
      targetValue = if (isTaken) Color.White else colors.inkDark,
      animationSpec = tween(durationMillis = 220),
      label = "med_title_color",
    )
  val statusColor by
    animateColorAsState(
      targetValue = if (isTaken) Color.White else colors.inkMuted,
      animationSpec = tween(durationMillis = 220),
      label = "med_status_color",
    )

  val cdText =
    if (isTaken) {
      stringResource(R.string.cd_med_checked, label)
    } else {
      stringResource(R.string.cd_med_unchecked, label)
    }
  val isLargeFontScale = LocalDensity.current.fontScale >= 1.35f

  Surface(
    modifier =
      Modifier.fillMaxWidth()
        .heightIn(min = 104.dp)
        .clip(RoundedCornerShape(24.dp))
        .testTag(testTag)
        .semantics { contentDescription = cdText }
        .clickable(role = Role.Button) {
          haptics.performHapticFeedback(HapticFeedbackType.LongPress)
          onClick()
        },
    shape = RoundedCornerShape(24.dp),
    color = containerColor,
    border = BorderStroke(2.dp, borderColor),
    shadowElevation = if (isTaken) 4.dp else 2.dp,
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 20.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        Box(
          modifier =
            Modifier.size(56.dp)
              .clip(CircleShape)
              .background(if (isTaken) Color.White.copy(alpha = 0.22f) else uncheckedIconBg),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isTaken) Color.White else uncheckedIconTint,
            modifier = Modifier.size(32.dp),
          )
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = label,
            style = MaterialTheme.typography.headlineMedium,
            color = titleColor,
          )
          if (isLargeFontScale) {
            Text(
              text =
                if (isTaken) {
                  stringResource(R.string.med_status_taken)
                } else {
                  stringResource(R.string.med_status_pending)
                },
              style = MaterialTheme.typography.titleLarge,
              fontWeight = if (isTaken) FontWeight.Bold else FontWeight.Medium,
              color = statusColor,
            )
          }
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        if (!isLargeFontScale) {
          Text(
            text =
              if (isTaken) {
                stringResource(R.string.med_status_taken)
              } else {
                stringResource(R.string.med_status_pending)
              },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = if (isTaken) FontWeight.Bold else FontWeight.Medium,
            color = statusColor,
          )
        }

        Icon(
          imageVector =
            if (isTaken) Icons.Rounded.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
          contentDescription = null,
          tint = if (isTaken) Color.White else colors.warmBorder,
          modifier = Modifier.size(38.dp),
        )
      }
    }
  }
}

@Composable
private fun WaterTrackingSection(
  waterCups: Int,
  onAddWaterCup: () -> Unit,
  onRemoveWaterCup: () -> Unit,
) {
  val colors = LocalHealthColors.current
  val haptics = LocalHapticFeedback.current
  val goalReached = waterCups >= DAILY_WATER_GOAL
  val isLargeFontScale = LocalDensity.current.fontScale >= 1.35f

  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(28.dp),
    color = colors.cardSurface,
    border = BorderStroke(2.dp, colors.warmBorder),
    shadowElevation = 2.dp,
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(22.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
      if (isLargeFontScale) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          WaterHeaderTitle(goalReached = goalReached)
          WaterHeaderControls(
            waterCups = waterCups,
            onRemoveWaterCup = onRemoveWaterCup,
          )
        }
      } else {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          WaterHeaderTitle(goalReached = goalReached, modifier = Modifier.weight(1f))
          WaterHeaderControls(
            waterCups = waterCups,
            onRemoveWaterCup = onRemoveWaterCup,
          )
        }
      }

      // 8 visual water cups in 2 rows of 4 for large, easy-to-scan layout
      Column(
        modifier = Modifier.fillMaxWidth().testTag("water_cups_grid"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        for (rowIdx in 0 until 2) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            for (colIdx in 0 until 4) {
              val cupIndex = rowIdx * 4 + colIdx
              val isFilled = cupIndex < waterCups
              WaterCupIcon(
                cupNumber = cupIndex + 1,
                isFilled = isFilled,
                modifier = Modifier.size(width = 56.dp, height = 66.dp),
              )
            }
          }
        }
      }

      Button(
        onClick = {
          haptics.performHapticFeedback(HapticFeedbackType.LongPress)
          onAddWaterCup()
        },
        modifier = Modifier.fillMaxWidth().heightIn(min = 84.dp).testTag("water_add_button"),
        shape = RoundedCornerShape(22.dp),
        colors =
          ButtonDefaults.buttonColors(
            containerColor = colors.waterBlue,
            contentColor = Color.White,
          ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
      ) {
        Icon(
          imageVector = Icons.Rounded.WaterDrop,
          contentDescription = null,
          modifier = Modifier.size(32.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
          text = stringResource(R.string.water_add_button),
          style = MaterialTheme.typography.titleLarge.copy(fontSize = 23.sp),
          textAlign = TextAlign.Center,
        )
      }
    }
  }
}

@Composable
private fun WaterHeaderTitle(goalReached: Boolean, modifier: Modifier = Modifier) {
  val colors = LocalHealthColors.current
  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Text(
      text = stringResource(R.string.water_section_title),
      style = MaterialTheme.typography.titleLarge,
      color = colors.inkDark,
    )
    Text(
      text =
        if (goalReached) {
          stringResource(R.string.water_goal_reached)
        } else {
          stringResource(R.string.water_goal_label)
        },
      style = MaterialTheme.typography.titleMedium,
      color = if (goalReached) colors.waterBlueDark else colors.inkMuted,
      fontWeight = if (goalReached) FontWeight.Bold else FontWeight.Medium,
    )
  }
}

@Composable
private fun WaterHeaderControls(waterCups: Int, onRemoveWaterCup: () -> Unit) {
  val colors = LocalHealthColors.current
  val haptics = LocalHapticFeedback.current

  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    if (waterCups > 0) {
      val undoCd = stringResource(R.string.cd_water_undo)
      OutlinedButton(
        onClick = {
          haptics.performHapticFeedback(HapticFeedbackType.LongPress)
          onRemoveWaterCup()
        },
        modifier =
          Modifier.heightIn(min = 56.dp)
            .testTag("water_undo_button")
            .semantics { contentDescription = undoCd },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, colors.warmBorder),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
      ) {
        Text(
          text = stringResource(R.string.water_undo_button),
          style = MaterialTheme.typography.labelLarge,
          color = colors.inkMuted,
        )
      }
    }

    Surface(
      shape = RoundedCornerShape(18.dp),
      color = colors.waterBlueContainer,
    ) {
      Text(
        text = stringResource(R.string.water_cup_count_format, waterCups),
        style = MaterialTheme.typography.displayLarge,
        color = colors.waterBlueDark,
        modifier =
          Modifier.padding(horizontal = 18.dp, vertical = 8.dp).testTag("water_count_text"),
      )
    }
  }
}

@Composable
private fun WaterCupIcon(cupNumber: Int, isFilled: Boolean, modifier: Modifier = Modifier) {
  val colors = LocalHealthColors.current
  val fillProgress by
    animateFloatAsState(
      targetValue = if (isFilled) 1f else 0f,
      animationSpec =
        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
      label = "water_cup_fill_$cupNumber",
    )

  val cdText =
    if (isFilled) {
      stringResource(R.string.cd_water_cup_filled, cupNumber)
    } else {
      stringResource(R.string.cd_water_cup_empty, cupNumber)
    }

  Canvas(
    modifier =
      modifier.testTag("water_cup_$cupNumber").semantics { contentDescription = cdText },
  ) {
    val w = size.width
    val h = size.height
    val topInset = w * 0.08f
    val bottomInset = w * 0.20f
    val strokeW = w * 0.075f
    val topY = h * 0.10f
    val bottomY = h * 0.92f

    val glassPath =
      Path().apply {
        moveTo(topInset, topY)
        lineTo(w - topInset, topY)
        lineTo(w - bottomInset, bottomY)
        lineTo(bottomInset, bottomY)
        close()
      }

    // Empty interior tint
    drawPath(path = glassPath, color = colors.waterGlassEmpty)

    // Animated water level inside clipped glass path
    if (fillProgress > 0.01f) {
      val clamped = fillProgress.coerceIn(0f, 1f)
      val maxWaterHeight = (bottomY - topY) * 0.78f
      val waterTopY = bottomY - (maxWaterHeight * clamped)

      clipPath(glassPath) {
        drawRect(
          color = colors.waterSkyFill,
          topLeft = Offset(0f, waterTopY),
          size = androidx.compose.ui.geometry.Size(w, bottomY - waterTopY + 4f),
        )
        // Subtle water surface highlight line
        drawLine(
          color = Color.White.copy(alpha = 0.65f),
          start = Offset(topInset, waterTopY + 2f),
          end = Offset(w - topInset, waterTopY + 2f),
          strokeWidth = strokeW * 0.6f,
          cap = StrokeCap.Round,
        )
      }
    }

    // Glass outline
    drawPath(
      path = glassPath,
      color = if (isFilled) colors.waterBlue else colors.waterGlassOutline,
      style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round),
    )
  }
}

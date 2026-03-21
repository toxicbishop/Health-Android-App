package com.vital.health.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vital.health.data.local.HealthLogEntity
import com.vital.health.ui.theme.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitalsScreenContent(logs: List<HealthLogEntity> = emptyList()) {
    var selectedPeriod by remember { mutableStateOf("Week") }
    val periods = listOf("Week", "Month", "Year")
    var showDatePicker by remember { mutableStateOf(false) }
    var customDateMillis by remember { mutableStateOf<Long?>(null) }
    
    val cutoff = if (customDateMillis != null) {
        customDateMillis!!
    } else {
        when (selectedPeriod) {
            "Week" -> System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000)
            "Month" -> System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
            else -> System.currentTimeMillis() - (365L * 24 * 60 * 60 * 1000)
        }
    }
    val filtered = logs.filter { it.timestamp >= cutoff }
    val weightLogs = filtered.filter { it.logType == "WEIGHT" }
    val bpLogs = filtered.filter { it.logType == "BLOOD_PRESSURE" }
    
    // Weight calculations
    val weights = weightLogs.mapNotNull { it.value.toDoubleOrNull() }
    val avgWeight = if (weights.isNotEmpty()) "%.1f".format(weights.average()) else "--"
    val firstHalf = weights.take(weights.size / 2)
    val secondHalf = weights.drop(weights.size / 2)
    val weightDelta = if (firstHalf.isNotEmpty() && secondHalf.isNotEmpty()) {
        val diff = secondHalf.average() - firstHalf.average()
        "%+.1f".format(diff)
    } else "--"
    
    // BP calculations
    val bpPairs = bpLogs.mapNotNull { v ->
        v.value.split("/").let { parts ->
            if (parts.size == 2) (parts[0].toIntOrNull() to parts[1].toIntOrNull()) else null
        }
    }.filter { it.first != null && it.second != null }
    val avgSys = if (bpPairs.isNotEmpty()) bpPairs.map { it.first!! }.average().toInt() else null
    val avgDia = if (bpPairs.isNotEmpty()) bpPairs.map { it.second!! }.average().toInt() else null
    val avgBpStr = if (avgSys != null && avgDia != null) "$avgSys/$avgDia mmHg" else "-- mmHg"
    val bpStatus = when {
        avgSys == null -> "NO DATA"
        avgSys < 120 && avgDia!! < 80 -> "NORMAL"
        avgSys < 130 -> "ELEVATED"
        else -> "HIGH"
    }
    val bpStatusColor = when (bpStatus) {
        "NORMAL" -> VitalSuccess
        "ELEVATED" -> Color(0xFFE2B93D)
        "HIGH" -> VitalError
        else -> TextMuted
    }

    // Date picker subtitle
    val subtitleText = if (customDateMillis != null) {
        "From ${java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault()).format(java.util.Date(customDateMillis!!))}"
    } else {
        "Analytics & Insights"
    }

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { customDateMillis = it }
                    showDatePicker = false
                }) { Text("OK", color = PrimaryBlack) }
            },
            dismissButton = {
                TextButton(onClick = {
                    customDateMillis = null
                    showDatePicker = false
                }) { Text("Clear", color = TextMuted) }
            },
            colors = DatePickerDefaults.colors(containerColor = CreamCard)
        ) {
            DatePicker(state = datePickerState, colors = DatePickerDefaults.colors(
                containerColor = CreamCard,
                titleContentColor = TextMain,
                headlineContentColor = TextMain,
                weekdayContentColor = TextMuted,
                dayContentColor = TextMain,
                selectedDayContainerColor = PrimaryBlack,
                selectedDayContentColor = CreamBg,
                todayContentColor = PrimaryBlack,
                todayDateBorderColor = PrimaryBlack
            ))
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Health Trends", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = TextMain)
                Text(subtitleText, style = MaterialTheme.typography.bodyMedium, color = TextMuted)
            }
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).border(1.dp, TanButton, RoundedCornerShape(12.dp))
                    .clickable { showDatePicker = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.DateRange, contentDescription = "Calendar", tint = PrimaryBlack)
            }
        }

        // Segmented Control
        Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(CreamCard), horizontalArrangement = Arrangement.SpaceBetween) {
            periods.forEach { period ->
                val isSelected = selectedPeriod == period
                Box(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).then(if (isSelected) Modifier.background(PrimaryBlack) else Modifier).clickable { selectedPeriod = period }.padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(period, color = if (isSelected) Color.White else TextMuted, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Weight Trend Card
        Card(colors = CardDefaults.cardColors(containerColor = CreamCard), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("WEIGHT TREND", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("$avgWeight kg", color = TextMain, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Average", color = TextMuted, fontSize = 14.sp, modifier = Modifier.padding(bottom = 2.dp))
                        }
                    }
                    if (weightDelta != "--") {
                        val deltaColor = if (weightDelta.startsWith("-")) VitalSuccess else VitalError
                        Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(deltaColor).padding(horizontal = 8.dp, vertical = 4.dp), contentAlignment = Alignment.Center) {
                            Text("$weightDelta kg", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                // Mini chart dots for weight entries
                if (weights.isNotEmpty()) {
                    val max = weights.max()
                    val min = weights.min()
                    val range = if (max - min > 0) max - min else 1.0
                    Row(modifier = Modifier.fillMaxWidth().height(40.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) {
                        weights.takeLast(7).forEach { w ->
                            val fraction = ((w - min) / range).toFloat().coerceIn(0f, 1f)
                            Box(modifier = Modifier.width(8.dp).height((8 + fraction * 32).dp).clip(RoundedCornerShape(4.dp)).background(PrimaryBlack))
                        }
                    }
                } else {
                    Text("No weight data for this period", color = TextMuted, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("${weightLogs.size} entries", color = TextMuted, fontSize = 12.sp)
            }
        }

        // BP Card
        Card(colors = CardDefaults.cardColors(containerColor = CreamCard), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(PrimaryBlack), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Favorite, "Heart", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Blood Pressure", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Average: $avgBpStr", color = TextMuted, fontSize = 14.sp)
                }
                Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(bpStatusColor).padding(horizontal = 8.dp, vertical = 4.dp), contentAlignment = Alignment.Center) {
                    Text("• $bpStatus", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
            }
        }

        // Health Insights — dynamic
        SectionColumn("HEALTH INSIGHTS") {
            if (bpPairs.size >= 2) {
                val morningBp = bpLogs.filter {
                    val cal = Calendar.getInstance(); cal.timeInMillis = it.timestamp; cal.get(Calendar.HOUR_OF_DAY) in 6..11
                }.mapNotNull { it.value.split("/").firstOrNull()?.toIntOrNull() }
                val eveningBp = bpLogs.filter {
                    val cal = Calendar.getInstance(); cal.timeInMillis = it.timestamp; cal.get(Calendar.HOUR_OF_DAY) in 18..23
                }.mapNotNull { it.value.split("/").firstOrNull()?.toIntOrNull() }
                
                val insight = if (morningBp.isNotEmpty() && eveningBp.isNotEmpty()) {
                    val diff = morningBp.average() - eveningBp.average()
                    if (diff > 0) "Systolic readings are ${diff.toInt()} mmHg higher in the morning versus evening."
                    else "Evening readings are ${(-diff).toInt()} mmHg higher than morning readings."
                } else {
                    "Log BP at different times of day to see patterns."
                }
                InsightCard(Icons.Outlined.Info, "BLOOD PRESSURE PATTERN", insight, PrimaryBlack)
            }
            if (weights.size >= 2) {
                val trend = weights.last() - weights.first()
                val direction = if (trend < 0) "downward" else "upward"
                InsightCard(Icons.Outlined.Warning, "WEIGHT CORRELATION", "Weight shows a $direction trend of ${"%.1f".format(kotlin.math.abs(trend))} kg over this period.", if (trend < 0) VitalSuccess else VitalError)
            }
            if (bpPairs.size < 2 && weights.size < 2) {
                Card(colors = CardDefaults.cardColors(containerColor = CreamCard), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Text("Log more data to see health insights!", color = TextMuted, modifier = Modifier.padding(16.dp), fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun InsightCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, body: String, iconTint: Color) {
    Card(colors = CardDefaults.cardColors(containerColor = CreamCard), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Icon(icon, "Insight", tint = iconTint, modifier = Modifier.padding(top = 2.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, color = TextMain, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("• $body", color = TextMuted, fontSize = 14.sp)
            }
        }
    }
}

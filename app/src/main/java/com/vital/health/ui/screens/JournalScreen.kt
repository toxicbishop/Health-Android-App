package com.vital.health.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vital.health.data.local.HealthLogEntity
import com.vital.health.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun JournalScreenContent(
    logs: List<HealthLogEntity>,
    onAddNote: (String) -> Unit
) {
    var showNoteDialog by remember { mutableStateOf(false) }
    
    val todayStart = remember {
        val cal = Calendar.getInstance(); cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.timeInMillis
    }
    val todayLogs = logs.filter { it.timestamp >= todayStart }.sortedByDescending { it.timestamp }
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val dateStr = remember { SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date()) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Journal", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = TextMain)
            Text(dateStr, style = MaterialTheme.typography.bodyMedium, color = TextMuted)

            Spacer(modifier = Modifier.height(8.dp))

            if (todayLogs.isEmpty()) {
                Card(colors = CardDefaults.cardColors(containerColor = CreamCard), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(32.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📝", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No entries yet today", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Start logging your vitals, mood, or add a health note.", color = TextMuted, fontSize = 14.sp)
                    }
                }
            } else {
                todayLogs.forEach { log ->
                    val timeStr = timeFormat.format(Date(log.timestamp))
                    val (icon, iconBg, label) = getLogDisplayInfo(log)
                    
                    Card(colors = CardDefaults.cardColors(containerColor = CreamCard), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            // Timeline dot
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(iconBg), contentAlignment = Alignment.Center) {
                                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(label, color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(log.value, color = TextMain, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                if (!log.notes.isNullOrBlank()) {
                                    Text(log.notes, color = TextMuted, fontSize = 13.sp)
                                }
                            }
                            Text(timeStr, color = TextMuted, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        FloatingActionButton(
            onClick = { showNoteDialog = true },
            containerColor = PrimaryBlack,
            contentColor = CreamBg,
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 16.dp)
        ) {
            Icon(Icons.Filled.Add, "Add Note")
        }
    }

    if (showNoteDialog) {
        var noteText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showNoteDialog = false },
            title = { Text("Health Note", color = TextMain, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = noteText, onValueChange = { noteText = it },
                    label = { Text("Write a note...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5
                )
            },
            confirmButton = {
                Button(onClick = { if (noteText.isNotBlank()) { onAddNote(noteText); showNoteDialog = false } }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)) { Text("Save", color = CreamBg) }
            },
            dismissButton = { TextButton(onClick = { showNoteDialog = false }) { Text("Cancel", color = TextMuted) } },
            containerColor = CreamCard
        )
    }
}

fun getLogDisplayInfo(log: HealthLogEntity): Triple<ImageVector, Color, String> {
    return when (log.logType) {
        "WEIGHT" -> Triple(Icons.Outlined.Person, Color(0xFF6366F1), "WEIGHT")
        "BLOOD_PRESSURE" -> Triple(Icons.Outlined.Favorite, Color(0xFFEC4899), "BLOOD PRESSURE")
        "HEART_RATE" -> Triple(Icons.Outlined.Favorite, Color(0xFFEF4444), "HEART RATE")
        "MOOD" -> Triple(Icons.Outlined.Face, Color(0xFFF59E0B), "MOOD")
        "MEDICATION" -> Triple(Icons.Outlined.ShoppingCart, Color(0xFF10B981), "MEDICATION")
        "APPOINTMENT" -> Triple(Icons.Filled.DateRange, Color(0xFF8B5CF6), "APPOINTMENT")
        "NOTE" -> Triple(Icons.Outlined.Create, Color(0xFF8B5CF6), "HEALTH NOTE")
        else -> Triple(Icons.Outlined.Create, Color(0xFF6B7280), log.logType)
    }
}

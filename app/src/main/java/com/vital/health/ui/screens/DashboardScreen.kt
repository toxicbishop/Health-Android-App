package com.vital.health.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.vital.health.data.local.HealthLogEntity
import com.vital.health.ui.theme.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    logs: List<HealthLogEntity>,
    userName: String = "User",
    userEmail: String = "",
    userAvatarUrl: String? = null,
    onSaveProfile: (String, ByteArray?) -> Unit = { _, _ -> },
    onAddLog: (type: String, value: String, unit: String, notes: String?) -> Unit,
    onSync: () -> Unit,
    onBackup: () -> Unit = {},
    onRestore: () -> Unit = {},
    onLogout: () -> Unit
) {
    var showInitialOptions by remember { mutableStateOf(true) }
    var showLogDialog by remember { mutableStateOf(false) }
    var preselectedType by remember { mutableStateOf("WEIGHT") }
    var logBothMode by remember { mutableStateOf(false) }
    var showMoodDialog by remember { mutableStateOf(false) }
    var showAddMedDialog by remember { mutableStateOf(false) }
    var showMonthlySummary by remember { mutableStateOf(false) }
    var showHrDialog by remember { mutableStateOf(false) }
    var showAppointmentDialog by remember { mutableStateOf(false) }
    var medTaken by remember { mutableStateOf(false) }

    if (showInitialOptions) {
        AlertDialog(
            onDismissRequest = { showInitialOptions = false },
            title = { Text("Log Vitals", color = TextMain) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { preselectedType = "WEIGHT"; logBothMode = false; showLogDialog = true; showInitialOptions = false }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = TanButton, contentColor = TextMain)) { Text("Log Weight Only") }
                    Button(onClick = { preselectedType = "BLOOD_PRESSURE"; logBothMode = false; showLogDialog = true; showInitialOptions = false }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = TanButton, contentColor = TextMain)) { Text("Log BP Only") }
                    Button(onClick = { logBothMode = true; showLogDialog = true; showInitialOptions = false }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack, contentColor = CreamBg)) { Text("Log Both", fontWeight = FontWeight.Bold) }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showInitialOptions = false }) { Text("Close", color = TextMuted) } },
            containerColor = CreamCard
        )
    }

    if (showLogDialog) {
        AddLogDialog(initialType = preselectedType, logBothMode = logBothMode, onDismiss = { showLogDialog = false },
            onSave = { type, value, unit, notes -> onAddLog(type, value, unit, notes) },
            onSaveBoth = { weight, bp, notes ->
                if (weight.isNotBlank()) onAddLog("WEIGHT", weight, "kg", notes)
                if (bp.isNotBlank()) onAddLog("BLOOD_PRESSURE", bp, "mmHg", notes)
            })
    }
    if (showMoodDialog) {
        MoodLogDialog(onDismiss = { showMoodDialog = false }, onSave = { mood, notes -> onAddLog("MOOD", mood, "", notes) })
    }
    if (showAddMedDialog) {
        AddMedicationDialog(onDismiss = { showAddMedDialog = false }, onSave = { name, dosage, time -> onAddLog("MEDICATION", name, "dose", "$dosage • $time") })
    }
    if (showMonthlySummary) {
        MonthlySummaryDialog(logs = logs, onDismiss = { showMonthlySummary = false })
    }
    if (showHrDialog) {
        HeartRateDialog(onDismiss = { showHrDialog = false }, onSave = { bpm, notes -> onAddLog("HEART_RATE", bpm, "bpm", notes) })
    }
    if (showAppointmentDialog) {
        AppointmentDialog(onDismiss = { showAppointmentDialog = false }, onSave = { doctor, date, notes -> onAddLog("APPOINTMENT", doctor, "visit", "$date • $notes") })
    }

    val todayStr = remember { SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date()) }
    var currentTab by remember { mutableStateOf("HOME") }

    // Today's mood
    val todayStart = remember {
        val cal = Calendar.getInstance(); cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.timeInMillis
    }
    val todayMood = logs.filter { it.logType == "MOOD" && it.timestamp >= todayStart }.maxByOrNull { it.timestamp }
    val latestMed = logs.filter { it.logType == "MEDICATION" }.maxByOrNull { it.timestamp }
    val context = LocalContext.current

    Scaffold(
        containerColor = CreamBg,
        bottomBar = {
            NavigationBar(containerColor = CreamCard, contentColor = TextMuted) {
                NavigationBarItem(selected = currentTab == "HOME", onClick = { currentTab = "HOME" }, icon = { Icon(Icons.Filled.Home, "Home") }, label = { Text("Home") }, colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryBlack, selectedTextColor = PrimaryBlack, unselectedIconColor = TextMuted, unselectedTextColor = TextMuted, indicatorColor = TanButton))
                NavigationBarItem(selected = currentTab == "MEDS", onClick = { currentTab = "MEDS" }, icon = { Icon(Icons.Outlined.AddCircle, "Meds") }, label = { Text("Meds") }, colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryBlack, selectedTextColor = PrimaryBlack, unselectedIconColor = TextMuted, unselectedTextColor = TextMuted, indicatorColor = TanButton))
                NavigationBarItem(selected = currentTab == "TRENDS", onClick = { currentTab = "TRENDS" }, icon = { Icon(Icons.Filled.DateRange, "Trends") }, label = { Text("Trends") }, colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryBlack, selectedTextColor = PrimaryBlack, unselectedIconColor = TextMuted, unselectedTextColor = TextMuted, indicatorColor = TanButton))
                NavigationBarItem(selected = currentTab == "JOURNAL", onClick = { currentTab = "JOURNAL" }, icon = { Icon(Icons.Outlined.Create, "Journal") }, label = { Text("Journal") }, colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryBlack, selectedTextColor = PrimaryBlack, unselectedIconColor = TextMuted, unselectedTextColor = TextMuted, indicatorColor = TanButton))
                NavigationBarItem(selected = currentTab == "SETTINGS", onClick = { currentTab = "SETTINGS" }, icon = { Icon(Icons.Outlined.Settings, "Settings") }, label = { Text("Settings") }, colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryBlack, selectedTextColor = PrimaryBlack, unselectedIconColor = TextMuted, unselectedTextColor = TextMuted, indicatorColor = TanButton))
            }
        }
    ) { padding ->
        when (currentTab) {
            "MEDS" -> Box(modifier = Modifier.padding(padding).fillMaxSize()) { MedsScreenContent(onBack = { currentTab = "HOME" }) }
            "TRENDS" -> Box(modifier = Modifier.padding(padding).fillMaxSize()) { VitalsScreenContent(logs = logs) }
            "JOURNAL" -> Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                JournalScreenContent(
                    logs = logs,
                    onAddNote = { note -> onAddLog("NOTE", note, "", null) }
                )
            }
            "SETTINGS" -> Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                SettingsScreenContent(userName = userName, userEmail = userEmail, userAvatarUrl = userAvatarUrl, onSaveProfile = onSaveProfile, onLogout = onLogout, onBackup = onBackup, onRestore = onRestore)
            }
            else -> {
                // HOME TAB
                Column(
                    modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Vital Dashboard", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = TextMain)
                            Text(todayStr, style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        IconButton(onClick = onLogout) { Icon(Icons.Default.ExitToApp, "Logout", tint = TextMuted) }
                    }

                    // STREAK
                    val streak = remember(logs) {
                        var count = 0
                        val cal = Calendar.getInstance()
                        while (true) {
                            val dayStart = cal.clone() as Calendar
                            dayStart.set(Calendar.HOUR_OF_DAY, 0); dayStart.set(Calendar.MINUTE, 0); dayStart.set(Calendar.SECOND, 0)
                            val dayEnd = cal.clone() as Calendar
                            dayEnd.set(Calendar.HOUR_OF_DAY, 23); dayEnd.set(Calendar.MINUTE, 59); dayEnd.set(Calendar.SECOND, 59)
                            val hasLog = logs.any { it.timestamp in dayStart.timeInMillis..dayEnd.timeInMillis }
                            if (hasLog) { count++; cal.add(Calendar.DAY_OF_YEAR, -1) } else break
                        }
                        count
                    }
                    if (streak > 0) {
                        Card(colors = CardDefaults.cardColors(containerColor = CreamCard), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text("🔥", fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("$streak-day logging streak", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("Keep it up! Consistency is key.", color = TextMuted, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // TREND ALERTS
                    val recentBpLogs = logs.filter { it.logType == "BLOOD_PRESSURE" && it.timestamp >= System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000 }
                    val elevatedBpDays = recentBpLogs.mapNotNull { it.value.split("/").firstOrNull()?.toIntOrNull() }.count { it >= 130 }
                    val recentHrLogs = logs.filter { it.logType == "HEART_RATE" && it.timestamp >= System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000 }
                    val avgHr = recentHrLogs.mapNotNull { it.value.toIntOrNull() }.let { if (it.isNotEmpty()) it.average().toInt() else null }
                    var dismissedAlerts by remember { mutableStateOf(setOf<String>()) }

                    if (elevatedBpDays >= 2 && "bp" !in dismissedAlerts) {
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF3D1F1F)), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text("⚠️", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("BP Alert", color = Color(0xFFFF6B6B), fontWeight = FontWeight.Bold)
                                    Text("Systolic ≥130 in $elevatedBpDays of last 3 logs", color = Color(0xFFFFAAAA), fontSize = 13.sp)
                                }
                                IconButton(onClick = { dismissedAlerts = dismissedAlerts + "bp" }) { Icon(Icons.Filled.Close, "Dismiss", tint = Color(0xFFFF6B6B)) }
                            }
                        }
                    }
                    if (avgHr != null && avgHr > 100 && "hr" !in dismissedAlerts) {
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF3D2E1F)), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text("💓", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Heart Rate Alert", color = Color(0xFFFFB347), fontWeight = FontWeight.Bold)
                                    Text("Average HR this week: $avgHr bpm (above 100)", color = Color(0xFFFFDDA0), fontSize = 13.sp)
                                }
                                IconButton(onClick = { dismissedAlerts = dismissedAlerts + "hr" }) { Icon(Icons.Filled.Close, "Dismiss", tint = Color(0xFFFFB347)) }
                            }
                        }
                    }

                    val lastWeight = logs.filter { it.logType == "WEIGHT" }.maxByOrNull { it.id }?.value ?: "--"
                    val lastBp = logs.filter { it.logType == "BLOOD_PRESSURE" }.maxByOrNull { it.id }?.value ?: "--"
                    val lastHr = logs.filter { it.logType == "HEART_RATE" }.maxByOrNull { it.id }?.value ?: "--"

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        MetricCard(modifier = Modifier.weight(1f), title = "WEIGHT", icon = Icons.Outlined.Person, value = "$lastWeight kg", onLogClick = { preselectedType = "WEIGHT"; logBothMode = false; showLogDialog = true })
                        MetricCard(modifier = Modifier.weight(1f), title = "BP", icon = Icons.Outlined.FavoriteBorder, value = lastBp, onLogClick = { preselectedType = "BLOOD_PRESSURE"; logBothMode = false; showLogDialog = true })
                        MetricCard(modifier = Modifier.weight(1f), title = "HR", icon = Icons.Outlined.FavoriteBorder, value = "$lastHr bpm", onLogClick = { showHrDialog = true })
                    }

                    // WELL-BEING
                    SectionColumn("WELL-BEING") {
                        Card(colors = CardDefaults.cardColors(containerColor = CreamCard), shape = RoundedCornerShape(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(PrimaryBlack), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Outlined.Face, "Mood", tint = CreamBg)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Mood", color = TextMain, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                    Text(
                                        if (todayMood != null) "Today: ${todayMood.value}" else "No mood logged today",
                                        color = TextMuted, fontSize = 14.sp
                                    )
                                }
                                Button(onClick = { showMoodDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack, contentColor = CreamBg), shape = RoundedCornerShape(8.dp)) {
                                    Text("+ Log", fontWeight = FontWeight.Bold)
                                }
                            }
                            Divider(modifier = Modifier.padding(horizontal = 16.dp), color = CreamBg, thickness = 4.dp)
                        }
                    }

                    // MEDICATION
                    SectionColumn("MEDICATION") {
                        Card(colors = CardDefaults.cardColors(containerColor = CreamCard), shape = RoundedCornerShape(12.dp)) {
                            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(PrimaryBlack), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Outlined.ShoppingCart, "Medication", tint = CreamBg)
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("Active Prescriptions", color = TextMain, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            val medCount = logs.count { it.logType == "MEDICATION" }
                                            Box(modifier = Modifier.background(CreamBg, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                                Text("${if (medCount > 0) medCount else 1} Active", color = TextMuted, fontSize = 10.sp)
                                            }
                                        }
                                        Text(
                                            if (latestMed != null) "${latestMed.value} • ${latestMed.notes ?: ""}" else "Prenatal Vitamin • 8:00 AM",
                                            color = TextMuted, fontSize = 14.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { medTaken = true },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (medTaken) VitalSuccess else PrimaryBlack,
                                            contentColor = CreamBg
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        enabled = !medTaken
                                    ) {
                                        Text(if (medTaken) "✓ Taken" else "Mark as Taken", fontWeight = FontWeight.Bold)
                                    }
                                    Button(onClick = { showAddMedDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = TanButton, contentColor = TextMain), shape = RoundedCornerShape(8.dp)) {
                                        Text("+ Add")
                                    }
                                }
                            }
                        }
                    }

                    // CLINICAL REPORTS
                    SectionColumn("CLINICAL REPORTS") {
                        Card(colors = CardDefaults.cardColors(containerColor = CreamCard), shape = RoundedCornerShape(12.dp)) {
                            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { showMonthlySummary = true }.padding(vertical = 8.dp)) {
                                    Icon(Icons.Outlined.Info, "Report", tint = TextMuted)
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text("Monthly Health Summary", color = TextMain, modifier = Modifier.weight(1f))
                                    Icon(Icons.Outlined.KeyboardArrowRight, "Arrow", tint = TextMuted)
                                }
                                Divider(color = CreamBg, modifier = Modifier.padding(vertical = 8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { exportPdf(context, logs, userName) }.padding(vertical = 8.dp)) {
                                    Icon(Icons.Outlined.Info, "Export", tint = TextMuted)
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text("Export Clinical Data (PDF)", color = TextMain, modifier = Modifier.weight(1f))
                                    Icon(Icons.Outlined.KeyboardArrowRight, "Arrow", tint = TextMuted)
                                }
                            }
                        }
                    }

                    // APPOINTMENTS
                    val nextAppointment = logs.filter { it.logType == "APPOINTMENT" }.maxByOrNull { it.timestamp }
                    SectionColumn("APPOINTMENTS") {
                        Card(colors = CardDefaults.cardColors(containerColor = CreamCard), shape = RoundedCornerShape(12.dp)) {
                            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(PrimaryBlack), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Filled.DateRange, "Appointment", tint = CreamBg)
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Doctor Visits", color = TextMain, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                        Text(
                                            if (nextAppointment != null) "${nextAppointment.value} • ${nextAppointment.notes ?: ""}" else "No upcoming visits",
                                            color = TextMuted, fontSize = 14.sp
                                        )
                                    }
                                    Button(onClick = { showAppointmentDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack, contentColor = CreamBg), shape = RoundedCornerShape(8.dp)) {
                                        Text("+ Add", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

// --- Mood Dialog ---
@Composable
fun MoodLogDialog(onDismiss: () -> Unit, onSave: (String, String?) -> Unit) {
    var selectedMood by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    val moods = listOf("😄 Great", "🙂 Good", "😐 Okay", "😔 Low", "😢 Bad")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("How are you feeling?", color = TextMain, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    moods.forEach { mood ->
                        val emoji = mood.split(" ")[0]
                        val isSelected = selectedMood == mood
                        Button(
                            onClick = { selectedMood = mood },
                            modifier = Modifier.weight(1f).height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) PrimaryBlack else TanButton, contentColor = if (isSelected) CreamBg else TextMain),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(4.dp)
                        ) {
                            Text(emoji, fontSize = 20.sp)
                        }
                    }
                }
                if (selectedMood.isNotEmpty()) {
                    Text("Selected: $selectedMood", color = TextMuted, fontSize = 14.sp)
                }
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes (optional)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { if (selectedMood.isNotEmpty()) { onSave(selectedMood, notes.takeIf { it.isNotBlank() }); onDismiss() } }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)) { Text("Save", color = CreamBg) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) } },
        containerColor = CreamCard
    )
}

// --- Add Medication Dialog ---
@Composable
fun AddMedicationDialog(onDismiss: () -> Unit, onSave: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Medication", color = TextMain, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Medication Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = dosage, onValueChange = { dosage = it }, label = { Text("Dosage (e.g. 1 tablet)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time (e.g. 8:00 AM)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank()) { onSave(name, dosage, time); onDismiss() } }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)) { Text("Save", color = CreamBg) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) } },
        containerColor = CreamCard
    )
}

// --- Monthly Summary Dialog ---
@Composable
fun MonthlySummaryDialog(logs: List<HealthLogEntity>, onDismiss: () -> Unit) {
    val thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
    val monthLogs = logs.filter { it.timestamp >= thirtyDaysAgo }
    
    val weightLogs = monthLogs.filter { it.logType == "WEIGHT" }
    val bpLogs = monthLogs.filter { it.logType == "BLOOD_PRESSURE" }
    val moodLogs = monthLogs.filter { it.logType == "MOOD" }
    
    val avgWeight = weightLogs.mapNotNull { it.value.toDoubleOrNull() }.let { if (it.isNotEmpty()) "%.1f".format(it.average()) else "--" }
    val minWeight = weightLogs.mapNotNull { it.value.toDoubleOrNull() }.minOrNull()?.let { "%.1f".format(it) } ?: "--"
    val maxWeight = weightLogs.mapNotNull { it.value.toDoubleOrNull() }.maxOrNull()?.let { "%.1f".format(it) } ?: "--"
    
    val avgBp = bpLogs.mapNotNull { v -> v.value.split("/").let { parts -> if (parts.size == 2) (parts[0].toIntOrNull() to parts[1].toIntOrNull()) else null } }
        .filter { it.first != null && it.second != null }
        .let { pairs -> if (pairs.isNotEmpty()) "${pairs.map { it.first!! }.average().toInt()}/${pairs.map { it.second!! }.average().toInt()}" else "--" }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Monthly Health Summary", color = TextMain, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SummaryRow("Weight Entries", "${weightLogs.size}")
                SummaryRow("Avg Weight", "$avgWeight kg")
                SummaryRow("Weight Range", "$minWeight – $maxWeight kg")
                Divider(color = TanButton)
                SummaryRow("BP Entries", "${bpLogs.size}")
                SummaryRow("Avg BP", "$avgBp mmHg")
                Divider(color = TanButton)
                SummaryRow("Mood Entries", "${moodLogs.size}")
                moodLogs.groupBy { it.value }.forEach { (mood, entries) ->
                    SummaryRow("  $mood", "${entries.size}x")
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close", color = PrimaryBlack) } },
        containerColor = CreamCard
    )
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = TextMuted, fontSize = 14.sp)
        Text(value, color = TextMain, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

// --- PDF Export ---
fun exportPdf(context: Context, logs: List<HealthLogEntity>, userName: String) {
    val doc = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = doc.startPage(pageInfo)
    val canvas = page.canvas
    val paint = Paint().apply { textSize = 14f; isAntiAlias = true }
    val titlePaint = Paint().apply { textSize = 20f; isFakeBoldText = true; isAntiAlias = true }
    
    var y = 40f
    canvas.drawText("Vital Health – Clinical Report", 40f, y, titlePaint); y += 30f
    canvas.drawText("Patient: $userName", 40f, y, paint); y += 20f
    canvas.drawText("Date: ${SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())}", 40f, y, paint); y += 30f
    
    canvas.drawText("— Weight Logs —", 40f, y, titlePaint.apply { textSize = 16f }); y += 25f
    logs.filter { it.logType == "WEIGHT" }.takeLast(10).forEach { log ->
        val date = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()).format(Date(log.timestamp))
        canvas.drawText("$date   ${log.value} ${log.unit}", 40f, y, paint); y += 18f
    }
    y += 15f
    canvas.drawText("— Blood Pressure Logs —", 40f, y, titlePaint); y += 25f
    logs.filter { it.logType == "BLOOD_PRESSURE" }.takeLast(10).forEach { log ->
        val date = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()).format(Date(log.timestamp))
        canvas.drawText("$date   ${log.value} ${log.unit}", 40f, y, paint); y += 18f
    }
    y += 15f
    canvas.drawText("— Mood Logs —", 40f, y, titlePaint); y += 25f
    logs.filter { it.logType == "MOOD" }.takeLast(10).forEach { log ->
        val date = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()).format(Date(log.timestamp))
        canvas.drawText("$date   ${log.value}", 40f, y, paint); y += 18f
    }
    
    doc.finishPage(page)
    
    val file = File(context.cacheDir, "vital_health_report.pdf")
    doc.writeTo(file.outputStream())
    doc.close()
    
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share Clinical Report"))
}

// --- Shared Composables ---
@Composable
fun SectionColumn(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        content()
    }
}

@Composable
fun MetricCard(modifier: Modifier = Modifier, title: String, icon: ImageVector, value: String, onLogClick: () -> Unit) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = CreamCard), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Icon(icon, contentDescription = null, tint = PrimaryBlack)
                Text(title, color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(value, color = TextMain, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = onLogClick, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMain), border = androidx.compose.foundation.BorderStroke(1.dp, TanButton), shape = RoundedCornerShape(8.dp)) {
                Text("Log Entry", fontSize = 12.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLogDialog(
    initialType: String, logBothMode: Boolean, onDismiss: () -> Unit,
    onSave: (type: String, value: String, unit: String, notes: String?) -> Unit,
    onSaveBoth: (weight: String, bp: String, notes: String?) -> Unit
) {
    var selectedType by remember { mutableStateOf(initialType) }
    var weightValue by remember { mutableStateOf("") }
    var systolic by remember { mutableStateOf("") }
    var diastolic by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (logBothMode) "Log Both (Weight & BP)" else "Add Health Log", color = TextMain) },
        containerColor = CreamCard,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!logBothMode) { Text("Type: ${selectedType.replace("_", " ")}", color = TextMuted); Spacer(modifier = Modifier.height(8.dp)) }
                if (logBothMode || selectedType == "WEIGHT") {
                    OutlinedTextField(value = weightValue, onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d{0,3}(\\.\\d{0,2})?$"))) weightValue = it }, label = { Text("Weight (kg)", color = TextMuted) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextMain, unfocusedTextColor = TextMain, focusedBorderColor = PrimaryBlack, unfocusedBorderColor = TanButton))
                }
                if (logBothMode || selectedType == "BLOOD_PRESSURE") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(value = systolic, onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d{1,3}$"))) systolic = it }, label = { Text("Systolic", color = TextMuted) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextMain, unfocusedTextColor = TextMain, focusedBorderColor = PrimaryBlack, unfocusedBorderColor = TanButton))
                        Text("/", style = MaterialTheme.typography.headlineMedium, color = TextMuted)
                        OutlinedTextField(value = diastolic, onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d{1,3}$"))) diastolic = it }, label = { Text("Diastolic", color = TextMuted) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextMain, unfocusedTextColor = TextMain, focusedBorderColor = PrimaryBlack, unfocusedBorderColor = TanButton))
                    }
                }
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes (optional)", color = TextMuted) }, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextMain, unfocusedTextColor = TextMain, focusedBorderColor = PrimaryBlack, unfocusedBorderColor = TanButton))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (logBothMode) { onSaveBoth(weightValue, if (systolic.isNotBlank() && diastolic.isNotBlank()) "$systolic/$diastolic" else "", notes); onDismiss() }
                else {
                    if (selectedType == "BLOOD_PRESSURE") { if (systolic.isNotBlank() && diastolic.isNotBlank()) { onSave(selectedType, "$systolic/$diastolic", "mmHg", notes.takeIf { it.isNotBlank() }); onDismiss() } }
                    else { if (weightValue.isNotBlank()) { onSave(selectedType, weightValue, "kg", notes.takeIf { it.isNotBlank() }); onDismiss() } }
                }
            }) { Text("Save", color = PrimaryBlack, fontWeight = FontWeight.Bold) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) } }
    )
}

// --- Heart Rate Dialog ---
@Composable
fun HeartRateDialog(onDismiss: () -> Unit, onSave: (String, String?) -> Unit) {
    var bpm by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Heart Rate", color = TextMain, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = bpm, onValueChange = { if (it.isEmpty() || (it.matches(Regex("^\\d{1,3}$")) && (it.toIntOrNull() ?: 0) <= 220)) bpm = it }, label = { Text("BPM (40-220)") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes (optional)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { if (bpm.isNotBlank()) { onSave(bpm, notes.takeIf { it.isNotBlank() }); onDismiss() } }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)) { Text("Save", color = CreamBg) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) } },
        containerColor = CreamCard
    )
}

// --- Appointment Dialog ---
@Composable
fun AppointmentDialog(onDismiss: () -> Unit, onSave: (String, String, String) -> Unit) {
    var doctor by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Appointment", color = TextMain, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = doctor, onValueChange = { doctor = it }, label = { Text("Doctor Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date (e.g. Mar 25, 2026)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes (optional)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { if (doctor.isNotBlank()) { onSave(doctor, date, notes); onDismiss() } }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)) { Text("Save", color = CreamBg) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) } },
        containerColor = CreamCard
    )
}

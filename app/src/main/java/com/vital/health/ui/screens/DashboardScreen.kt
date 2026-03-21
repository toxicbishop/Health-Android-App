package com.vital.health.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vital.health.data.local.HealthLogEntity
import com.vital.health.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    logs: List<HealthLogEntity>,
    onAddLog: (type: String, value: String, unit: String, notes: String?) -> Unit,
    onSync: () -> Unit,
    onLogout: () -> Unit
) {
    var showInitialOptions by remember { mutableStateOf(true) }
    var showLogDialog by remember { mutableStateOf(false) }
    var preselectedType by remember { mutableStateOf("WEIGHT") }
    var logBothMode by remember { mutableStateOf(false) }

    if (showInitialOptions) {
        AlertDialog(
            onDismissRequest = { showInitialOptions = false },
            title = { Text("Log Vitals", color = TextMain) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { preselectedType = "WEIGHT"; logBothMode = false; showLogDialog = true; showInitialOptions = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = TanButton, contentColor = TextMain)
                    ) { Text("Log Weight Only") }
                    Button(
                        onClick = { preselectedType = "BLOOD_PRESSURE"; logBothMode = false; showLogDialog = true; showInitialOptions = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = TanButton, contentColor = TextMain)
                    ) { Text("Log BP Only") }
                    Button(
                        onClick = { logBothMode = true; showLogDialog = true; showInitialOptions = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack, contentColor = Color.White)
                    ) { Text("Log Both", fontWeight = FontWeight.Bold) }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showInitialOptions = false }) { Text("Close", color = TextMuted) }
            },
            containerColor = CreamCard
        )
    }

    if (showLogDialog) {
        AddLogDialog(
            initialType = preselectedType,
            logBothMode = logBothMode,
            onDismiss = { showLogDialog = false },
            onSave = { type, value, unit, notes ->
                onAddLog(type, value, unit, notes)
            },
            onSaveBoth = { weight, bp, notes ->
                if(weight.isNotBlank()) onAddLog("WEIGHT", weight, "kg", notes)
                if(bp.isNotBlank()) onAddLog("BLOOD_PRESSURE", bp, "mmHg", notes)
            }
        )
    }

    val todayStr = remember { SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date()) }
    var currentTab by remember { mutableStateOf("HOME") }

    Scaffold(
        containerColor = CreamBg,
        bottomBar = {
            NavigationBar(
                containerColor = CreamBg,
                contentColor = TextMuted
            ) {
                NavigationBarItem(selected = currentTab == "HOME", onClick = { currentTab = "HOME" }, icon = { Icon(Icons.Filled.Home, "Home") }, label = { Text("Home") }, colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryBlack, selectedTextColor = PrimaryBlack, indicatorColor = Color.Transparent))
                NavigationBarItem(selected = currentTab == "MEDS", onClick = { currentTab = "MEDS" }, icon = { Icon(Icons.Outlined.AddCircle, "Meds") }, label = { Text("Meds") }, colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryBlack, selectedTextColor = PrimaryBlack, indicatorColor = Color.Transparent))
                NavigationBarItem(selected = currentTab == "VITALS", onClick = { currentTab = "VITALS" }, icon = { Icon(Icons.Outlined.DateRange, "Vitals") }, label = { Text("Vitals") })
                NavigationBarItem(selected = currentTab == "TRACK", onClick = { currentTab = "TRACK" }, icon = { Icon(Icons.Outlined.DateRange, "Track") }, label = { Text("Track") })
                NavigationBarItem(selected = currentTab == "SETTINGS", onClick = { currentTab = "SETTINGS" }, icon = { Icon(Icons.Outlined.Settings, "Settings") }, label = { Text("Settings") })
            }
        }
    ) { padding ->
        if (currentTab == "MEDS") {
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                MedsScreenContent(onBack = { currentTab = "HOME" })
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Vital Dashboard", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = TextMain)
                    Text(todayStr, style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = onLogout) {
                    Icon(Icons.Default.ExitToApp, "Logout", tint = TextMuted)
                }
            }

            val lastWeight = logs.filter { it.logType == "WEIGHT" }.maxByOrNull { it.id }?.value ?: "--"
            val lastBp = logs.filter { it.logType == "BLOOD_PRESSURE" }.maxByOrNull { it.id }?.value ?: "--"

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "WEIGHT",
                    icon = Icons.Outlined.Person,
                    value = "Last: $lastWeight kg",
                    onLogClick = { preselectedType = "WEIGHT"; logBothMode = false; showLogDialog = true }
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "BP",
                    icon = Icons.Outlined.FavoriteBorder,
                    value = "Last: $lastBp",
                    onLogClick = { preselectedType = "BLOOD_PRESSURE"; logBothMode = false; showLogDialog = true }
                )
            }

            SectionColumn("WELL-BEING") {
                Card(colors = CardDefaults.cardColors(containerColor = CreamCard), shape = RoundedCornerShape(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(PrimaryBlack), contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.Face, "Mood", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Mood", color = TextMain, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            Text("No mood logged today", color = TextMuted, fontSize = 14.sp)
                        }
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack, contentColor = Color.White),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("+ Log", fontWeight = FontWeight.Bold)
                        }
                    }
                    Divider(modifier = Modifier.padding(horizontal = 16.dp), color = CreamBg, thickness = 4.dp)
                }
            }

            SectionColumn("MEDICATION") {
                Card(colors = CardDefaults.cardColors(containerColor = CreamCard), shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(PrimaryBlack), contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.ShoppingCart, "Medication", tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Active Prescriptions", color = TextMain, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(modifier = Modifier.background(CreamBg, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                        Text("1 Active", color = TextMuted, fontSize = 10.sp)
                                    }
                                }
                                Text("Prenatal Vitamin • 8:00 AM", color = TextMuted, fontSize = 14.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack, contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Mark as Taken", fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { },
                                colors = ButtonDefaults.buttonColors(containerColor = TanButton, contentColor = TextMain),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("+ Add")
                            }
                        }
                    }
                }
            }

            SectionColumn("CLINICAL REPORTS") {
                Card(colors = CardDefaults.cardColors(containerColor = CreamCard), shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Icon(Icons.Outlined.Info, "Report", tint = TextMuted)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Monthly Health Summary", color = TextMain, modifier = Modifier.weight(1f))
                            Icon(Icons.Outlined.KeyboardArrowRight, "Arrow", tint = TextMuted)
                        }
                        Divider(color = CreamBg, modifier = Modifier.padding(vertical = 8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Icon(Icons.Outlined.Info, "Export", tint = TextMuted)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Export Clinical Data (PDF)", color = TextMain, modifier = Modifier.weight(1f))
                            Icon(Icons.Outlined.KeyboardArrowRight, "Arrow", tint = TextMuted)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SectionColumn(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        content()
    }
}

@Composable
fun MetricCard(modifier: Modifier = Modifier, title: String, icon: ImageVector, value: String, onLogClick: () -> Unit) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CreamCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Icon(icon, contentDescription = null, tint = PrimaryBlack)
                Text(title, color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(value, color = TextMain, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = onLogClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMain),
                border = androidx.compose.foundation.BorderStroke(1.dp, TanButton),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Log Entry", fontSize = 12.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLogDialog(
    initialType: String,
    logBothMode: Boolean,
    onDismiss: () -> Unit,
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
                if (!logBothMode) {
                    Text("Type: ${selectedType.replace("_", " ")}", color = TextMuted)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (logBothMode || selectedType == "WEIGHT") {
                    OutlinedTextField(
                        value = weightValue,
                        onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d{0,3}(\\.\\d{0,2})?$"))) weightValue = it },
                        label = { Text("Weight (kg)", color = TextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextMain, unfocusedTextColor = TextMain,
                            focusedBorderColor = PrimaryBlack, unfocusedBorderColor = TanButton
                        )
                    )
                }

                if (logBothMode || selectedType == "BLOOD_PRESSURE") {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = systolic,
                            onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d{1,3}$"))) systolic = it },
                            label = { Text("Systolic", color = TextMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextMain, unfocusedTextColor = TextMain, focusedBorderColor = PrimaryBlack, unfocusedBorderColor = TanButton)
                        )
                        Text("/", style = MaterialTheme.typography.headlineMedium, color = TextMuted)
                        OutlinedTextField(
                            value = diastolic,
                            onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d{1,3}$"))) diastolic = it },
                            label = { Text("Diastolic", color = TextMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextMain, unfocusedTextColor = TextMain, focusedBorderColor = PrimaryBlack, unfocusedBorderColor = TanButton)
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextMain, unfocusedTextColor = TextMain, focusedBorderColor = PrimaryBlack, unfocusedBorderColor = TanButton)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (logBothMode) {
                        onSaveBoth(weightValue, if(systolic.isNotBlank() && diastolic.isNotBlank()) "$systolic/$diastolic" else "", notes)
                        onDismiss()
                    } else {
                        if (selectedType == "BLOOD_PRESSURE") {
                            if (systolic.isNotBlank() && diastolic.isNotBlank()) {
                                onSave(selectedType, "$systolic/$diastolic", "mmHg", notes.takeIf { it.isNotBlank() })
                                onDismiss()
                            }
                        } else {
                            if (weightValue.isNotBlank()) {
                                onSave(selectedType, weightValue, "kg", notes.takeIf { it.isNotBlank() })
                                onDismiss()
                            }
                        }
                    }
                }
            ) {
                Text("Save", color = PrimaryBlack, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) }
        }
    )
}


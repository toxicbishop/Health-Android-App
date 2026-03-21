package com.vital.health.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vital.health.ui.theme.*

data class ReminderData(val time: String, val description: String, val isActive: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedsScreenContent(onBack: () -> Unit) {
    var isActive by remember { mutableStateOf(true) }
    var selectedFrequency by remember { mutableStateOf("Daily") }
    val frequencies = listOf("Daily", "Weekly", "Custom")
    var reminders by remember { mutableStateOf(listOf(
        ReminderData("08:00 AM", "1 tablet • Before meal", true),
        ReminderData("08:00 PM", "1 tablet • Before meal", true)
    )) }
    var showAddTime by remember { mutableStateOf(false) }
    var editingIndex by remember { mutableStateOf(-1) }

    val completedCount = reminders.count { it.isActive }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Top Bar
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = TextMain) }
            Text("Metformin", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = TextMain, modifier = Modifier.weight(1f).padding(start = 8.dp))
            Text(if (isActive) "ACTIVE" else "PAUSED", color = if (isActive) VitalSuccess else TextMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.width(8.dp))
            Switch(checked = isActive, onCheckedChange = { isActive = it }, colors = SwitchDefaults.colors(checkedThumbColor = CreamBg, checkedTrackColor = PrimaryBlack))
        }

        // Today's Progress
        Card(colors = CardDefaults.cardColors(containerColor = CreamCard), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("TODAY'S PROGRESS", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text("$completedCount of ${reminders.size} completed", color = PrimaryBlack, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(TanButton)) {
                    val fraction = if (reminders.isNotEmpty()) completedCount.toFloat() / reminders.size else 0f
                    Box(modifier = Modifier.fillMaxWidth(fraction).height(8.dp).clip(RoundedCornerShape(4.dp)).background(PrimaryBlack))
                }
            }
        }

        // Frequency toggle
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Frequency", color = TextMain, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(CreamCard)) {
                frequencies.forEach { freq ->
                    val isSelected = selectedFrequency == freq
                    Box(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                            .then(if (isSelected) Modifier.background(PrimaryBlack) else Modifier)
                            .clickable { selectedFrequency = freq }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(freq, color = if (isSelected) CreamBg else TextMuted, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Schedule
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Schedule", color = TextMain, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text("${reminders.size} Reminders Set", color = TextMuted, fontSize = 12.sp)
            }
            reminders.forEachIndexed { index, reminder ->
                ReminderCard(
                    time = reminder.time,
                    description = reminder.description,
                    isActive = reminder.isActive,
                    onToggle = { active -> reminders = reminders.toMutableList().also { it[index] = reminder.copy(isActive = active) } },
                    onEdit = { editingIndex = index; showAddTime = true },
                    onDelete = { reminders = reminders.toMutableList().also { it.removeAt(index) } }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f, fill = false))

        Button(
            onClick = { editingIndex = -1; showAddTime = true },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack, contentColor = CreamBg)
        ) { Text("+ Add New Time", fontWeight = FontWeight.Bold, fontSize = 16.sp) }

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showAddTime) {
        val editing = if (editingIndex >= 0 && editingIndex < reminders.size) reminders[editingIndex] else null
        AddReminderDialog(
            initialTime = editing?.time ?: "",
            initialDesc = editing?.description ?: "",
            onDismiss = { showAddTime = false; editingIndex = -1 },
            onSave = { time, desc ->
                if (editingIndex >= 0 && editingIndex < reminders.size) {
                    reminders = reminders.toMutableList().also { it[editingIndex] = ReminderData(time, desc, true) }
                } else {
                    reminders = reminders + ReminderData(time, desc, true)
                }
                showAddTime = false; editingIndex = -1
            }
        )
    }
}

@Composable
fun ReminderCard(time: String, description: String, isActive: Boolean, onToggle: (Boolean) -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = CreamCard), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(TanButton), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.Notifications, contentDescription = "Time", tint = PrimaryBlack)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(time, color = TextMain, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(description, color = TextMuted, fontSize = 14.sp)
            }
            IconButton(onClick = onEdit) { Icon(Icons.Outlined.Create, contentDescription = "Edit", tint = TextMuted, modifier = Modifier.size(20.dp)) }
            IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = VitalError, modifier = Modifier.size(20.dp)) }
            Switch(checked = isActive, onCheckedChange = onToggle, colors = SwitchDefaults.colors(checkedThumbColor = CreamBg, checkedTrackColor = PrimaryBlack))
        }
    }
}

@Composable
fun AddReminderDialog(initialTime: String, initialDesc: String, onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var time by remember { mutableStateOf(initialTime) }
    var desc by remember { mutableStateOf(initialDesc) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialTime.isNotEmpty()) "Edit Reminder" else "Add Reminder", color = TextMain, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time (e.g. 08:00 AM)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description (e.g. 1 tablet • Before meal)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
        },
        confirmButton = {
            Button(onClick = { if (time.isNotBlank()) onSave(time.trim(), desc.trim()) }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)) { Text("Save", color = CreamBg) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) } },
        containerColor = CreamCard
    )
}

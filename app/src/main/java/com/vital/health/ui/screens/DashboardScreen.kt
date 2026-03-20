package com.vital.health.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import com.vital.health.data.local.HealthLogEntity
import com.vital.health.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    logs: List<HealthLogEntity>,
    onAddLog: (type: String, value: String, unit: String, notes: String?) -> Unit,
    onSync: () -> Unit,
    onLogout: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AddLogDialog(
            onDismiss = { showDialog = false },
            onSave = { type, value, unit, notes ->
                onAddLog(type, value, unit, notes)
                showDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("VITAL", style = MaterialTheme.typography.headlineMedium) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CreamBg),
                actions = {
                    IconButton(onClick = onSync) {
                        Icon(Icons.Default.Refresh, contentDescription = "Sync records")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = PrimaryBlack,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Log")
            }
        },
        containerColor = CreamBg
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Summary",
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
            
            items(logs) { log ->
                LogCard(log)
            }
        }
    }
}

@Composable
fun LogCard(log: HealthLogEntity) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = CreamCard)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.logType.replace("_", " "),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                Text(
                    text = log.value,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextMain
                )
            }
            Text(
                text = log.unit,
                style = MaterialTheme.typography.bodyLarge,
                color = TextDim
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLogDialog(
    onDismiss: () -> Unit,
    onSave: (type: String, value: String, unit: String, notes: String?) -> Unit
) {
    var selectedType by remember { mutableStateOf("WEIGHT") }
    var value by remember { mutableStateOf("") }
    var systolic by remember { mutableStateOf("") }
    var diastolic by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val logTypes = listOf("WEIGHT", "BLOOD_PRESSURE", "HEART_RATE")
    var expanded by remember { mutableStateOf(false) }

    val defaultUnit = when (selectedType) {
        "WEIGHT" -> "kg"
        "BLOOD_PRESSURE" -> "mmHg"
        "HEART_RATE" -> "bpm"
        else -> ""
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Health Log") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedType.replace("_", " "),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Log Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        logTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.replace("_", " ")) },
                                onClick = {
                                    selectedType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                if (selectedType == "BLOOD_PRESSURE") {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = systolic,
                            onValueChange = { 
                                // Regex: allow up to 3 digits only
                                if (it.isEmpty() || it.matches(Regex("^\\d{1,3}$"))) systolic = it 
                            },
                            label = { Text("Systolic") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Text("/", style = MaterialTheme.typography.headlineMedium)
                        OutlinedTextField(
                            value = diastolic,
                            onValueChange = { 
                                // Regex: allow up to 3 digits only
                                if (it.isEmpty() || it.matches(Regex("^\\d{1,3}$"))) diastolic = it 
                            },
                            label = { Text("Diastolic") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                } else {
                    OutlinedTextField(
                        value = value,
                        onValueChange = { 
                            // Regex: allow up to 3 digits and optional 2 decimals
                            if (it.isEmpty() || it.matches(Regex("^\\d{0,3}(\\.\\d{0,2})?$"))) value = it 
                        },
                        label = { Text("Value ($defaultUnit)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (selectedType == "BLOOD_PRESSURE") {
                        if (systolic.isNotBlank() && diastolic.isNotBlank()) {
                            val bpValue = "$systolic/$diastolic"
                            onSave(selectedType, bpValue, defaultUnit, notes.takeIf { it.isNotBlank() })
                        }
                    } else {
                        if (value.isNotBlank()) {
                            onSave(selectedType, value, defaultUnit, notes.takeIf { it.isNotBlank() })
                        }
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}


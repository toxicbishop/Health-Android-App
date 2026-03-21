package com.vital.health.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.vital.health.ui.theme.*

@Composable
fun SettingsScreenContent(
    userName: String,
    userEmail: String,
    userAvatarUrl: String?,
    onSaveProfile: (String, ByteArray?) -> Unit,
    onLogout: () -> Unit
) {
    val initial = if (userName.isNotEmpty()) userName.first().toString().uppercase() else "U"
    var showEditProfile by remember { mutableStateOf(false) }
    var showHealthGoals by remember { mutableStateOf(false) }
    var showNotifications by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("vital_prefs", Context.MODE_PRIVATE) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        Box(
            modifier = Modifier.size(100.dp).clip(CircleShape).background(PrimaryBlack),
            contentAlignment = Alignment.Center
        ) {
            if (userAvatarUrl != null) {
                AsyncImage(model = userAvatarUrl, contentDescription = "Avatar", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            } else {
                Text(initial, color = CreamBg, fontSize = 40.sp, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text(userName, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = TextMain)
        Text(userEmail, fontSize = 14.sp, color = TextMuted)
        
        Spacer(modifier = Modifier.height(32.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = CreamCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                SettingsItem(icon = Icons.Outlined.Person, label = "Personal Information", isToggle = false, hasBorder = true, onClick = { showEditProfile = true })
                SettingsItem(
                    icon = Icons.Outlined.Settings, label = "Dark Mode", isToggle = true, hasBorder = true,
                    checked = isAppDarkMode,
                    onCheckedChange = { isAppDarkMode = it; sharedPrefs.edit().putBoolean("dark_mode", it).apply() }
                )
                SettingsItem(icon = Icons.Outlined.Info, label = "Health Goals", isToggle = false, hasBorder = true, onClick = { showHealthGoals = true })
                SettingsItem(icon = Icons.Outlined.Notifications, label = "Notifications", isToggle = false, hasBorder = false, onClick = { showNotifications = true })
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VitalError, contentColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Filled.ExitToApp, contentDescription = "Sign Out", modifier = Modifier.padding(end = 8.dp))
            Text("Sign Out", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.weight(1f))
        Text("VERSION 2.4.0", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, modifier = Modifier.padding(vertical = 24.dp))
    }

    if (showEditProfile) {
        EditProfileDialog(currentName = userName, currentAvatarUrl = userAvatarUrl, onDismiss = { showEditProfile = false }, onSave = { name, photoBytes -> onSaveProfile(name, photoBytes); showEditProfile = false })
    }
    if (showHealthGoals) {
        HealthGoalsDialog(onDismiss = { showHealthGoals = false })
    }
    if (showNotifications) {
        NotificationsDialog(onDismiss = { showNotifications = false })
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, isToggle: Boolean, hasBorder: Boolean,
    checked: Boolean = false, onCheckedChange: ((Boolean) -> Unit)? = null, onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, color = TextMain, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        if (isToggle) {
            Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryBlack))
        } else {
            Icon(Icons.Filled.ArrowForward, contentDescription = "Arrow", tint = TextMuted, modifier = Modifier.size(16.dp))
        }
    }
    if (hasBorder) { Divider(color = TanButton, modifier = Modifier.padding(horizontal = 16.dp)) }
}

@Composable
fun EditProfileDialog(currentName: String, currentAvatarUrl: String?, onDismiss: () -> Unit, onSave: (String, ByteArray?) -> Unit) {
    var editName by remember { mutableStateOf(currentName) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? -> uri?.let { selectedImageUri = it } }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile", color = TextMain) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(CreamBg).clickable { launcher.launch("image/*") }, contentAlignment = Alignment.Center) {
                    if (selectedImageUri != null) { AsyncImage(model = selectedImageUri, contentDescription = "New Avatar", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()) }
                    else if (currentAvatarUrl != null) { AsyncImage(model = currentAvatarUrl, contentDescription = "Avatar", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()) }
                    else { val init = if (editName.isNotEmpty()) editName.first().toString().uppercase() else "U"; Text(init, color = TextMain, fontSize = 40.sp, fontWeight = FontWeight.Bold) }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Tap to change photo", color = TextMuted, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = {
                var bytes: ByteArray? = null
                selectedImageUri?.let { uri -> val inputStream = context.contentResolver.openInputStream(uri); bytes = inputStream?.readBytes(); inputStream?.close() }
                onSave(editName.trim(), bytes)
            }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)) { Text("Save", color = CreamBg) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) } },
        containerColor = CreamCard
    )
}

@Composable
fun HealthGoalsDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("health_goals", Context.MODE_PRIVATE) }
    
    var targetWeight by remember { mutableStateOf(prefs.getString("target_weight", "") ?: "") }
    var targetBP by remember { mutableStateOf(prefs.getString("target_bp", "") ?: "") }
    var dailySteps by remember { mutableStateOf(prefs.getString("daily_steps", "") ?: "") }
    var waterIntake by remember { mutableStateOf(prefs.getString("water_intake", "") ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Health Goals", color = TextMain, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = targetWeight, onValueChange = { targetWeight = it }, label = { Text("Target Weight (kg)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = targetBP, onValueChange = { targetBP = it }, label = { Text("Target BP (e.g. 120/80)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = dailySteps, onValueChange = { dailySteps = it }, label = { Text("Daily Step Goal") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = waterIntake, onValueChange = { waterIntake = it }, label = { Text("Daily Water (glasses)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
        },
        confirmButton = {
            Button(onClick = {
                prefs.edit()
                    .putString("target_weight", targetWeight)
                    .putString("target_bp", targetBP)
                    .putString("daily_steps", dailySteps)
                    .putString("water_intake", waterIntake)
                    .apply()
                onDismiss()
            }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)) { Text("Save", color = CreamBg) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) } },
        containerColor = CreamCard
    )
}

@Composable
fun NotificationsDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("notifications", Context.MODE_PRIVATE) }
    
    var medReminders by remember { mutableStateOf(prefs.getBoolean("med_reminders", true)) }
    var vitalsReminder by remember { mutableStateOf(prefs.getBoolean("vitals_reminder", true)) }
    var weeklySummary by remember { mutableStateOf(prefs.getBoolean("weekly_summary", true)) }
    var quietHours by remember { mutableStateOf(prefs.getBoolean("quiet_hours", false)) }
    var soundEnabled by remember { mutableStateOf(prefs.getBoolean("sound", true)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Notifications", color = TextMain, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                NotifToggleRow("Medication Reminders", "Get reminded to take your meds", medReminders) { medReminders = it }
                Divider(color = TanButton)
                NotifToggleRow("Vitals Logging Reminder", "Daily reminder to log vitals", vitalsReminder) { vitalsReminder = it }
                Divider(color = TanButton)
                NotifToggleRow("Weekly Health Summary", "Receive a weekly digest", weeklySummary) { weeklySummary = it }
                Divider(color = TanButton)
                NotifToggleRow("Quiet Hours (10PM–7AM)", "Mute notifications overnight", quietHours) { quietHours = it }
                Divider(color = TanButton)
                NotifToggleRow("Sound", "Play notification sounds", soundEnabled) { soundEnabled = it }
            }
        },
        confirmButton = {
            Button(onClick = {
                prefs.edit()
                    .putBoolean("med_reminders", medReminders)
                    .putBoolean("vitals_reminder", vitalsReminder)
                    .putBoolean("weekly_summary", weeklySummary)
                    .putBoolean("quiet_hours", quietHours)
                    .putBoolean("sound", soundEnabled)
                    .apply()
                onDismiss()
            }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)) { Text("Save", color = Color.White) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) } },
        containerColor = CreamCard
    )
}

@Composable
fun NotifToggleRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextMain, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(subtitle, color = TextMuted, fontSize = 12.sp)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryBlack))
    }
}

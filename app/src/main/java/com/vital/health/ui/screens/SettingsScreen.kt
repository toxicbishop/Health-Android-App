package com.vital.health.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vital.health.ui.theme.*

@Composable
fun SettingsScreenContent(
    userName: String,
    userEmail: String,
    onLogout: () -> Unit
) {
    val initial = if (userName.isNotEmpty()) userName.first().toString().uppercase() else "U"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        // Profile Circle
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(PrimaryBlack),
            contentAlignment = Alignment.Center
        ) {
            Text(initial, color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text(userName, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = TextMain)
        Text(userEmail, fontSize = 14.sp, color = TextMuted)
        
        Spacer(modifier = Modifier.height(32.dp))

        // Settings Menu List
        Card(
            colors = CardDefaults.cardColors(containerColor = CreamCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                SettingsItem(icon = Icons.Outlined.Person, label = "Personal Information", isToggle = false, hasBorder = true)
                SettingsItem(icon = Icons.Outlined.Settings, label = "Dark Mode", isToggle = true, hasBorder = true)
                SettingsItem(icon = Icons.Outlined.Info, label = "Health Goals", isToggle = false, hasBorder = true)
                SettingsItem(icon = Icons.Outlined.Notifications, label = "Notifications", isToggle = false, hasBorder = true)
                SettingsItem(icon = Icons.Outlined.Info, label = "Security", isToggle = false, hasBorder = false)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Sign out button
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
}

@Composable
fun SettingsItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, isToggle: Boolean, hasBorder: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, color = TextMain, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        
        if (isToggle) {
            Switch(
                checked = false,
                onCheckedChange = { },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PrimaryBlack
                )
            )
        } else {
            Icon(Icons.Filled.ArrowForward, contentDescription = "Arrow", tint = TextMuted, modifier = Modifier.size(16.dp))
        }
    }
    if (hasBorder) {
        Divider(color = TanButton, modifier = Modifier.padding(horizontal = 16.dp))
    }
}
